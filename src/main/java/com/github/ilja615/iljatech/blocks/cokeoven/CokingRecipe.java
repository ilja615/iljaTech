package com.github.ilja615.iljatech.blocks.cokeoven;

import com.github.ilja615.iljatech.blocks.foundry.FoundryRecipe;
import com.github.ilja615.iljatech.init.ModRecipeTypes;
import com.github.ilja615.iljatech.util.CountedIngredient;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record CokingRecipe(CountedIngredient countedIngredient, ItemStack output, int fluidAmount) implements Recipe<CokingRecipe.InputContainer> {

    public static final MapCodec<CokingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        CountedIngredient.CODEC.fieldOf("ingredient")
                .forGetter(CokingRecipe::countedIngredient),
        ItemStack.CODEC.fieldOf("result")
                .forGetter(CokingRecipe::output),
        Codec.INT.fieldOf("fluid_amount")
                .forGetter(CokingRecipe::fluidAmount)
            ).apply(
                instance,
                CokingRecipe::new
        )
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, CokingRecipe> STREAM_CODEC = StreamCodec.composite(
        CountedIngredient.PACKET_CODEC,
        CokingRecipe::countedIngredient,
        ItemStack.STREAM_CODEC,
        CokingRecipe::output,
        ByteBufCodecs.INT,
        CokingRecipe::fluidAmount,
        CokingRecipe::new
    );

    @Override
    public boolean matches(InputContainer input, Level world) {
        return countedIngredient.test(input.stack());
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
        return ModRecipeTypes.COKING_TYPE;
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

    public record Serializer() implements RecipeSerializer<CokingRecipe> {
        @Override
        public MapCodec<CokingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CokingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}