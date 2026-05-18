package com.github.ilja615.iljatech.blocks.cokeoven;

import com.github.ilja615.iljatech.blocks.foundry.FoundryRecipe;
import com.github.ilja615.iljatech.init.ModRecipeTypes;
import com.github.ilja615.iljatech.util.CountedIngredient;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

    public static final PacketCodec<RegistryByteBuf, CokingRecipe> PACKET_CODEC = PacketCodec.tuple(
        CountedIngredient.PACKET_CODEC,
        CokingRecipe::countedIngredient,
        ItemStack.PACKET_CODEC,
        CokingRecipe::output,
        PacketCodecs.INTEGER,
        CokingRecipe::fluidAmount,
        CokingRecipe::new
    );

    @Override
    public boolean matches(InputContainer input, World world) {
        return countedIngredient.test(input.stack());
    }

    @Override
    public ItemStack craft(InputContainer input, RegistryWrapper.WrapperLookup registries) {
        return output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return output.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Registries.RECIPE_SERIALIZER.get(Registries.RECIPE_TYPE.getId(getType()));
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.COKING_TYPE;
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

    public record Serializer() implements RecipeSerializer<CokingRecipe> {
        @Override
        public MapCodec<CokingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, CokingRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}