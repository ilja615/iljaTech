package com.github.ilja615.iljatech.blocks.researchtable;

import com.github.ilja615.iljatech.blocks.cokeoven.CokingRecipe;
import com.github.ilja615.iljatech.init.ModRecipeTypes;
import com.github.ilja615.iljatech.util.CountedIngredient;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

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

    public static final PacketCodec<RegistryByteBuf, BlueprintingRecipe> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER,
            BlueprintingRecipe::pointsCost,
            ItemStack.PACKET_CODEC,
            BlueprintingRecipe::output,
            BlueprintingRecipe::new
    );

    @Override
    public boolean matches(RecipeInput input, World world) {
        return true;
    }

    @Override
    public ItemStack craft(RecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return null;
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
        return ModRecipeTypes.BLUEPRINTING_TYPE;
    }

    public record Serializer() implements RecipeSerializer<BlueprintingRecipe> {
        @Override
        public MapCodec<BlueprintingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, BlueprintingRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}