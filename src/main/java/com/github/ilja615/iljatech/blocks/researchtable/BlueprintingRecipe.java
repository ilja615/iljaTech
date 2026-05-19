package com.github.ilja615.iljatech.blocks.researchtable;

import com.github.ilja615.iljatech.blocks.cokeoven.CokingRecipe;
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

public record BlueprintingRecipe(int pointsCost, ItemStack output) implements Recipe<RecipeInput> {

    public static final MapCodec<BlueprintingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.INT.fieldOf("pts_cost")
                            .forGetter(BlueprintingRecipe::pointsCost),
                    ItemStack.CODEC.fieldOf("result")
                            .forGetter(BlueprintingRecipe::output)
            ).apply(
                    instance,
                    BlueprintingRecipe::new
            )
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, BlueprintingRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            BlueprintingRecipe::pointsCost,
            ItemStack.STREAM_CODEC,
            BlueprintingRecipe::output,
            BlueprintingRecipe::new
    );

    @Override
    public boolean matches(RecipeInput input, Level world) {
        return true;
    }

    @Override
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider lookup) {
        return null;
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
        return ModRecipeTypes.BLUEPRINTING_TYPE;
    }

    public record Serializer() implements RecipeSerializer<BlueprintingRecipe> {
        @Override
        public MapCodec<BlueprintingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BlueprintingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}