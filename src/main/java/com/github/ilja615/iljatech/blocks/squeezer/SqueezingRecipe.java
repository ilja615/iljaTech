package com.github.ilja615.iljatech.blocks.squeezer;

import com.github.ilja615.iljatech.blocks.rollermill.RollingRecipe;
import com.github.ilja615.iljatech.init.ModRecipeTypes;
import com.github.ilja615.iljatech.util.CountedIngredient;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record SqueezingRecipe(Ingredient ingredient, FluidVariant fluidOutput, int fluidAmount) implements Recipe<SqueezingRecipe.InputContainer> {

    public static final MapCodec<SqueezingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient")
                    .forGetter(SqueezingRecipe::ingredient),
        FluidVariant.CODEC.fieldOf("fluid_output")
                .forGetter(SqueezingRecipe::fluidOutput),
        Codec.INT.fieldOf("fluid_amount")
                .forGetter(SqueezingRecipe::fluidAmount)
            ).apply(
                instance,
                SqueezingRecipe::new
        )
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, SqueezingRecipe> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC,
        SqueezingRecipe::ingredient,
        FluidVariant.PACKET_CODEC,
        SqueezingRecipe::fluidOutput,
        ByteBufCodecs.INT,
        SqueezingRecipe::fluidAmount,
        SqueezingRecipe::new
    );

    @Override
    public boolean matches(SqueezingRecipe.InputContainer input, Level world) {
        return ingredient.test(input.stack());
    }

    @Override
    public ItemStack craft(InputContainer input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
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
        return ModRecipeTypes.SQUEEZING_TYPE;
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

    public record Serializer() implements RecipeSerializer<SqueezingRecipe> {
        @Override
        public MapCodec<SqueezingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SqueezingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}