package com.github.ilja615.iljatech.blocks.carpentry;

import com.github.ilja615.iljatech.init.ModRecipeTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.recipe.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;

public record CarpentryRecipe(ShapedRecipePattern raw, ItemStack result, int fluidAmount) implements Recipe<CraftingInput> {

    public static final MapCodec<CarpentryRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    ShapedRecipePattern.MAP_CODEC.forGetter(CarpentryRecipe::raw),
                    ItemStack.CODEC.fieldOf("result").forGetter(CarpentryRecipe::result),
                    Codec.INT.fieldOf("fluid_amount").forGetter(CarpentryRecipe::fluidAmount)
            ).apply(instance, CarpentryRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, CarpentryRecipe> STREAM_CODEC = StreamCodec.composite(
            ShapedRecipePattern.STREAM_CODEC,
            CarpentryRecipe::raw,
            ItemStack.STREAM_CODEC,
            CarpentryRecipe::result,
            ByteBufCodecs.INT,
            CarpentryRecipe::fluidAmount,
            CarpentryRecipe::new
    );

    @Override
    public boolean matches(CraftingInput input, Level world) {
        return this.raw.matches(input);
    }

    @Override
    public ItemStack craft(CraftingInput input, HolderLookup.Provider registries) {
        return this.getResultItem(registries).copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registriesLookup) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BuiltInRegistries.RECIPE_SERIALIZER.get(BuiltInRegistries.RECIPE_TYPE.getKey(getType()));
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
        public StreamCodec<RegistryFriendlyByteBuf, CarpentryRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}