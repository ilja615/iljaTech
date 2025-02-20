package com.github.ilja615.iljatech.recipe;

import com.github.ilja615.iljatech.IljaTech;
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
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public record BoilingRecipe(Ingredient stack, ItemStack output) implements Recipe<BoilingRecipe.InputContainer> {

    public static final MapCodec<BoilingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient")
                .forGetter(BoilingRecipe::stack),
        ItemStack.CODEC.fieldOf("result")
                .forGetter(BoilingRecipe::output)
            ).apply(
                instance,
                BoilingRecipe::new
        )
    );

    public static final PacketCodec<RegistryByteBuf, BoilingRecipe> PACKET_CODEC = PacketCodec.tuple(
        Ingredient.PACKET_CODEC,
        BoilingRecipe::stack,
        ItemStack.PACKET_CODEC,
        BoilingRecipe::output,
        BoilingRecipe::new
    );

    public static final Serializer SERIALIZER = Registry.register(
        Registries.RECIPE_SERIALIZER,
        Identifier.of(IljaTech.MOD_ID, "boiling"),
        new Serializer()
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
    public RecipeSerializer<? extends Recipe<InputContainer>> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<InputContainer>> getType() {
        return ModRecipeTypes.BOILING_TYPE;
    }

//    @Override
//    public IngredientPlacement getIngredientPlacement() {
//        return IngredientPlacement.forSingleSlot(stack);
//    }

//    @Override
//    public RecipeBookCategory getRecipeBookCategory() {
//        return CATEGORY;
//    }

    public static void register() {

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

    public record Serializer() implements RecipeSerializer<BoilingRecipe> {

        @Override
        public MapCodec<BoilingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, BoilingRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}