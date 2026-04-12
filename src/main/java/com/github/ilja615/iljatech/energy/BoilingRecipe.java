package com.github.ilja615.iljatech.energy;

import com.github.ilja615.iljatech.init.ModRecipeTypes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record BoilingRecipe(Ingredient stack, ItemStack output) implements Recipe<BoilingRecipe.InputContainer> {

    public static final MapCodec<BoilingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient")
                .forGetter(BoilingRecipe::stack),
        ItemStack.CODEC.fieldOf("result")
                .forGetter(BoilingRecipe::output)
            ).apply(
                instance,
                BoilingRecipe::new
        )
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, BoilingRecipe> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC,
        BoilingRecipe::stack,
        ItemStack.STREAM_CODEC,
        BoilingRecipe::output,
        BoilingRecipe::new
    );

    @Override
    public boolean matches(InputContainer input, Level world) {
        return stack.test(input.stack());
    }

    @Override
    public ItemStack craft(InputContainer input, HolderLookup.Provider registries) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registriesLookup) {
        return output.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BuiltInRegistries.RECIPE_SERIALIZER.get(BuiltInRegistries.RECIPE_TYPE.getKey(getType()));
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.BOILING_TYPE;
    }

    public record InputContainer(ItemStack stack) implements RecipeInput {

        @Override
        public ItemStack getItem(int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        public int size() {
            return 0;
        }
    }

    public record Serializer() implements RecipeSerializer<BoilingRecipe> {
        @Override
        public MapCodec<BoilingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BoilingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}