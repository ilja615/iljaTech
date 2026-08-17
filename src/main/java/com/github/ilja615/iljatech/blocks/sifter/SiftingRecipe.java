package com.github.ilja615.iljatech.blocks.sifter;

import com.github.ilja615.iljatech.blocks.cokeoven.CokingRecipe;
import com.github.ilja615.iljatech.init.ModRecipeTypes;
import com.github.ilja615.iljatech.util.CountedIngredient;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Arrays;
import java.util.List;

public record SiftingRecipe(Ingredient ingredient,List<SiftingResult> fines,List<SiftingResult> coarse) implements Recipe<SiftingRecipe.InputContainer> {

    public static final MapCodec<SiftingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.fieldOf("ingredient")
                    .forGetter(SiftingRecipe::ingredient),

            SiftingResult.CODEC.listOf()
                    .fieldOf("fines")
                    .forGetter(SiftingRecipe::fines),

            SiftingResult.CODEC.listOf()
                    .fieldOf("coarse")
                    .forGetter(SiftingRecipe::coarse)

    ).apply(instance, SiftingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<LootTable>> LOOTTABLE_STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.LOOT_TABLE);

    public static final StreamCodec<RegistryFriendlyByteBuf, SiftingRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            SiftingRecipe::ingredient,
            SiftingResult.STREAM_CODEC.apply(ByteBufCodecs.list()),
            SiftingRecipe::fines,
            SiftingResult.STREAM_CODEC.apply(ByteBufCodecs.list()),
            SiftingRecipe::coarse,
            SiftingRecipe::new
    );

    @Override
    public boolean matches(SiftingRecipe.InputContainer input, Level world) {
        return ingredient.test(input.stack());
    }

    @Override
    public ItemStack assemble(InputContainer recipeInput, HolderLookup.Provider provider) {
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return null;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BuiltInRegistries.RECIPE_SERIALIZER.get(BuiltInRegistries.RECIPE_TYPE.getKey(getType()));
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.SIFTING_TYPE;
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

    public record Serializer() implements RecipeSerializer<SiftingRecipe> {
        @Override
        public MapCodec<SiftingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SiftingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    public record SiftingResult(Holder<Item> item, float chance) {
                public static final Codec<SiftingResult> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.ITEM_NON_AIR_CODEC.fieldOf("item").forGetter(SiftingResult::item),
                Codec.FLOAT.fieldOf("chance").forGetter(SiftingResult::chance)
        ).apply(instance, SiftingResult::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, SiftingResult> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.holderRegistry(Registries.ITEM),
                SiftingResult::item,
                ByteBufCodecs.FLOAT,
                SiftingResult::chance,
                SiftingResult::new
        );
    }

}
