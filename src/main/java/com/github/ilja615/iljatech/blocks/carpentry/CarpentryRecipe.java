package com.github.ilja615.iljatech.blocks.carpentry;

import com.github.ilja615.iljatech.init.ModRecipeTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public record CarpentryRecipe(int layout, RawShapedRecipe raw, ItemStack result, int fluidAmount) implements Recipe<CraftingRecipeInput> {

    public static final MapCodec<CarpentryRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.INT.fieldOf("layout").forGetter(CarpentryRecipe::layout),
                    RawShapedRecipe.CODEC.forGetter(CarpentryRecipe::raw),
                    ItemStack.CODEC.fieldOf("result").forGetter(CarpentryRecipe::result),
                    Codec.INT.fieldOf("fluid_amount").forGetter(CarpentryRecipe::fluidAmount)
            ).apply(instance, CarpentryRecipe::new)
    );

    public static final PacketCodec<RegistryByteBuf, CarpentryRecipe> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER,
            CarpentryRecipe::layout,
            RawShapedRecipe.PACKET_CODEC,
            CarpentryRecipe::raw,
            ItemStack.PACKET_CODEC,
            CarpentryRecipe::result,
            PacketCodecs.INTEGER,
            CarpentryRecipe::fluidAmount,
            CarpentryRecipe::new
    );

    @Override
    public boolean matches(CraftingRecipeInput input, World world) {
        return this.raw.matches(input);
    }

    @Override
    public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup registries) {
        return this.getResult(registries).copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Registries.RECIPE_SERIALIZER.get(Registries.RECIPE_TYPE.getId(getType()));
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.CARPENTRY_TYPE;
    }

    public record Serializer() implements RecipeSerializer<CarpentryRecipe> {
        @Override
        public MapCodec<CarpentryRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, CarpentryRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}