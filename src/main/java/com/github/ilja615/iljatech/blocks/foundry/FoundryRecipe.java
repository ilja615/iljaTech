package com.github.ilja615.iljatech.blocks.foundry;

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
import net.minecraft.recipe.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import java.util.List;

public record FoundryRecipe(List<CountedIngredient> ingredients, ItemStack output, CountedIngredient flux, ItemStack slag,
                            int processingTime, float slagChanceWithoutFlux, float slagChanceUsingFlux, boolean isFluxRequired) implements Recipe<FoundryRecipe.InputContainer> {
    public static final MapCodec<FoundryRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.list(CountedIngredient.CODEC.codec()).fieldOf("ingredients").forGetter(FoundryRecipe::ingredients),
            ItemStack.CODEC.fieldOf("result").forGetter(FoundryRecipe::output),
            CountedIngredient.CODEC.fieldOf("flux").forGetter(FoundryRecipe::flux),
            ItemStack.OPTIONAL_CODEC.fieldOf("slag").forGetter(FoundryRecipe::slag),
            Codec.INT.fieldOf("processing_time").forGetter(FoundryRecipe::processingTime),
            Codec.FLOAT.fieldOf("slag_chance_without_flux").forGetter(FoundryRecipe::slagChanceWithoutFlux),
            Codec.FLOAT.fieldOf("slag_chance_using_flux").forGetter(FoundryRecipe::slagChanceUsingFlux),
            Codec.BOOL.fieldOf("is_flux_required").forGetter(FoundryRecipe::isFluxRequired)
    ).apply(instance, FoundryRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FoundryRecipe> STREAM_CODEC = new StreamCodec<>() {
        private final StreamCodec<RegistryFriendlyByteBuf, List<CountedIngredient>> ingredientsCodec = CountedIngredient.PACKET_CODEC.apply(ByteBufCodecs.list());

        @Override
        public FoundryRecipe decode(RegistryFriendlyByteBuf buf) {
            List<CountedIngredient> ingredients = ingredientsCodec.decode(buf);
            ItemStack output = ItemStack.STREAM_CODEC.decode(buf);
            CountedIngredient flux = CountedIngredient.PACKET_CODEC.decode(buf);
            ItemStack slag = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
            int processingTime = ByteBufCodecs.INT.decode(buf);
            float slagChance = ByteBufCodecs.FLOAT.decode(buf);
            float slagChanceUsingFlux = ByteBufCodecs.FLOAT.decode(buf);
            boolean isFluxRequired = ByteBufCodecs.BOOL.decode(buf);
            return new FoundryRecipe(ingredients, output, flux, slag, processingTime, slagChance, slagChanceUsingFlux, isFluxRequired);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, FoundryRecipe recipe) {
            ingredientsCodec.encode(buf, recipe.ingredients);
            ItemStack.STREAM_CODEC.encode(buf, recipe.output);
            CountedIngredient.PACKET_CODEC.encode(buf, recipe.flux);
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, recipe.slag);
            ByteBufCodecs.INT.encode(buf, recipe.processingTime);
            ByteBufCodecs.FLOAT.encode(buf, recipe.slagChanceWithoutFlux);
            ByteBufCodecs.FLOAT.encode(buf, recipe.slagChanceUsingFlux);
            ByteBufCodecs.BOOL.encode(buf, recipe.isFluxRequired);
        }
    };

    @Override
    public boolean matches(FoundryRecipe.InputContainer input, Level world) {
        if (input.size() != this.ingredients.size()) {
            return false;
        } else {
            if (this.isFluxRequired) {
                // Check if a correct flux is provided
                if (!this.flux.getMatchingStacks().stream().map(ItemStack::getItem).toList().contains(input.flux.getItem())) {
                    return false;
                }
                // Check if enough flux is provided
                if (input.flux.getCount() < this.flux.count()) {
                    return false;
                }
            }

            List<Item> a = this.ingredients.stream().map(countedIngredient -> countedIngredient.ingredient().getItems()[0].getItem()).toList();
            List<Item> b = input.stacks.stream().map(ItemStack::getItem).toList();
            if (a.containsAll(b) && b.containsAll(a)) {
                // Technically a matching recipe was found but amounts have to be still checked
                for (CountedIngredient i : this.ingredients) {
                    if (i instanceof CountedIngredient countedIngredient) {
                        int count = countedIngredient.count(); // Needed amount for the recipe
                        boolean hasEnoughOfThis = false;
                        // Check all the slots if there is enough of the CountedIngredient
                        for (int h = 0; h < input.stacks.size(); h++) {
                            if (b.get(h).equals(countedIngredient.getMatchingStacks().get(0).getItem())) {
                                if (input.stacks.get(h).getCount() >= count) {
                                    hasEnoughOfThis = true;
                                    break;
                                }
                            }
                        }
                        if (!hasEnoughOfThis) {
                            // The ingredients and inputs matched but something was not enough
                            return false;
                        }
                    }
                }
                return true;
            }
            // The ingredients and inputs did not match
            return false;
        }
    }

    @Override
    public ItemStack craft(FoundryRecipe.InputContainer input, HolderLookup.Provider registries) {
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
        return ModRecipeTypes.FOUNDRY_TYPE;
    }

    public record InputContainer(List<ItemStack> stacks, ItemStack flux) implements RecipeInput {
        @Override
        public ItemStack getItem(int slot) {
            return stacks.get(slot);
        }

        @Override
        public int size() {
            return stacks.size();
        }
    }

    public record Serializer() implements RecipeSerializer<FoundryRecipe> {
        @Override
        public MapCodec<FoundryRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FoundryRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
