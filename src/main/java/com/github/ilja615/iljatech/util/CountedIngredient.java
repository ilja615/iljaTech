package com.github.ilja615.iljatech.util;

import com.github.ilja615.iljatech.IljaTech;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public record CountedIngredient(int count, Ingredient ingredient) implements CustomIngredient {
    public static MapCodec<CountedIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("count", 1).forGetter(CountedIngredient::count),
            Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter(CountedIngredient::ingredient)
    ).apply(instance, CountedIngredient::new));

    public static PacketCodec<RegistryByteBuf, CountedIngredient> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, CountedIngredient::count,
            Ingredient.PACKET_CODEC, CountedIngredient::ingredient,
            CountedIngredient::new
    );

    public static final Serializer SERIALIZER = new Serializer();

    public static CountedIngredient ofStacks(int count, ItemStack... items) {
        return new CountedIngredient(count, Ingredient.ofStacks(items));
    }

    @Override
    public boolean test(ItemStack stack) {
        return this.ingredient.test(stack) && stack.getCount() >= this.count;
    }

    @Override
    public List<ItemStack> getMatchingStacks() {
        return List.of(Arrays.stream(this.ingredient.getMatchingStacks()).map(stack -> {
            ItemStack copy = stack.copy();
            copy.setCount(this.count);
            return copy;
        }).toArray(ItemStack[]::new));
    }

    @Override
    public boolean requiresTesting() {
        return false;
    }

    @Override
    public CustomIngredientSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    public static class Serializer implements CustomIngredientSerializer<CountedIngredient> {
        public static final Identifier ID = Identifier.of(IljaTech.MOD_ID, "counted_ingredient");

        @Override
        public Identifier getIdentifier() {
            return ID;
        }

        @Override
        public MapCodec<CountedIngredient> getCodec(boolean allowEmpty) {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, CountedIngredient> getPacketCodec() {
            return PACKET_CODEC;
        }
    }
}