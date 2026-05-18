package com.github.ilja615.iljatech.blocks.stampinghammer;

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

public record StampingRecipe(Ingredient stack, ItemStack output) implements Recipe<StampingRecipe.InputContainer> {

    public static final MapCodec<StampingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient")
                .forGetter(StampingRecipe::stack),
        ItemStack.CODEC.fieldOf("result")
                .forGetter(StampingRecipe::output)
            ).apply(
                instance,
                StampingRecipe::new
        )
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, StampingRecipe> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC,
        StampingRecipe::stack,
        ItemStack.STREAM_CODEC,
        StampingRecipe::output,
        StampingRecipe::new
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
        return ModRecipeTypes.STAMPING_TYPE;
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

    public record Serializer() implements RecipeSerializer<StampingRecipe> {
        @Override
        public MapCodec<StampingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, StampingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}