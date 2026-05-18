package com.github.ilja615.iljatech.blocks.pulverizermill;

import com.github.ilja615.iljatech.init.ModRecipeTypes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public record PulverizingRecipe(Ingredient stack, ItemStack output) implements Recipe<PulverizingRecipe.InputContainer> {

    public static final MapCodec<PulverizingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient")
                .forGetter(PulverizingRecipe::stack),
        ItemStack.CODEC.fieldOf("result")
                .forGetter(PulverizingRecipe::output)
            ).apply(
                instance,
                PulverizingRecipe::new
        )
    );

    public static final PacketCodec<RegistryByteBuf, PulverizingRecipe> PACKET_CODEC = PacketCodec.tuple(
        Ingredient.PACKET_CODEC,
        PulverizingRecipe::stack,
        ItemStack.PACKET_CODEC,
        PulverizingRecipe::output,
        PulverizingRecipe::new
    );

    @Override
    public boolean matches(InputContainer input, World world) {
        return stack.test(input.stack());
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
        return ModRecipeTypes.PULVERIZING_TYPE;
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

    public record Serializer() implements RecipeSerializer<PulverizingRecipe> {
        @Override
        public MapCodec<PulverizingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, PulverizingRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}