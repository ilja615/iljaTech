package com.github.ilja615.iljatech.blocks.squeezer;

import com.github.ilja615.iljatech.blocks.rollermill.RollingRecipe;
import com.github.ilja615.iljatech.init.ModRecipeTypes;
import com.github.ilja615.iljatech.util.CountedIngredient;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public record SqueezingRecipe(Ingredient ingredient, FluidVariant fluidOutput, int fluidAmount) implements Recipe<SqueezingRecipe.InputContainer> {

    public static final MapCodec<SqueezingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient")
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

    public static final PacketCodec<RegistryByteBuf, SqueezingRecipe> PACKET_CODEC = PacketCodec.tuple(
        Ingredient.PACKET_CODEC,
        SqueezingRecipe::ingredient,
        FluidVariant.PACKET_CODEC,
        SqueezingRecipe::fluidOutput,
        PacketCodecs.INTEGER,
        SqueezingRecipe::fluidAmount,
        SqueezingRecipe::new
    );

    @Override
    public boolean matches(SqueezingRecipe.InputContainer input, World world) {
        return ingredient.test(input.stack());
    }

    @Override
    public ItemStack craft(InputContainer input, RegistryWrapper.WrapperLookup registries) {
        return ItemStack.EMPTY;
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
        return ModRecipeTypes.SQUEEZING_TYPE;
    }

    public record InputContainer(ItemStack stack) implements RecipeInput {

        @Override
        public ItemStack getStackInSlot(int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSize() {
            return 0;
        }
    }

    public record Serializer() implements RecipeSerializer<SqueezingRecipe> {
        @Override
        public MapCodec<SqueezingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, SqueezingRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}