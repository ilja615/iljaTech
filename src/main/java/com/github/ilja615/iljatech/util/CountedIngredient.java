package com.github.ilja615.iljatech.util;

import com.github.ilja615.iljatech.IljaTech;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public record CountedIngredient(int count, Ingredient ingredient) implements CustomIngredient {
    public static MapCodec<CountedIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("count", 1).forGetter(CountedIngredient::count),
            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(CountedIngredient::ingredient)
    ).apply(instance, CountedIngredient::new));

    public static StreamCodec<RegistryFriendlyByteBuf, CountedIngredient> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, CountedIngredient::count,
            Ingredient.CONTENTS_STREAM_CODEC, CountedIngredient::ingredient,
            CountedIngredient::new
    );

    public static final Serializer SERIALIZER = new Serializer();

    public static CountedIngredient ofStacks(int count, ItemStack... items) {
        return new CountedIngredient(count, Ingredient.of(items));
    }

    @Override
    public boolean test(ItemStack stack) {
        return this.ingredient.test(stack) && stack.getCount() >= this.count;
    }

    @Override
    public List<ItemStack> getMatchingStacks() {
        return List.of(Arrays.stream(this.ingredient.getItems()).map(stack -> {
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
        public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, "counted_ingredient");

        @Override
        public ResourceLocation getIdentifier() {
            return ID;
        }

        @Override
        public MapCodec<CountedIngredient> getCodec(boolean allowEmpty) {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CountedIngredient> getPacketCodec() {
            return PACKET_CODEC;
        }
    }
}