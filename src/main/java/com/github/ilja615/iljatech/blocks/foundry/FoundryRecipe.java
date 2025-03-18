package com.github.ilja615.iljatech.blocks.foundry;

import com.github.ilja615.iljatech.init.ModRecipeTypes;
import com.github.ilja615.iljatech.util.CountedIngredient;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

import java.util.List;

public record FoundryRecipe(List<CountedIngredient> ingredients, ItemStack output, CountedIngredient flux, ItemStack slag,
                            int processingTime, float slagChanceWithoutFlux, float slagChanceUsingFlux, boolean isFluxRequired) implements Recipe<FoundryRecipe.InputContainer> {
    public static final MapCodec<FoundryRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.list(CountedIngredient.CODEC.codec()).fieldOf("ingredients").forGetter(FoundryRecipe::ingredients),
            ItemStack.CODEC.fieldOf("result").forGetter(FoundryRecipe::output),
            CountedIngredient.CODEC.fieldOf("flux").forGetter(FoundryRecipe::flux),
            ItemStack.CODEC.fieldOf("slag").forGetter(FoundryRecipe::slag),
            Codec.INT.fieldOf("processing_time").forGetter(FoundryRecipe::processingTime),
            Codec.FLOAT.fieldOf("slag_chance_without_flux").forGetter(FoundryRecipe::slagChanceWithoutFlux),
            Codec.FLOAT.fieldOf("slag_chance_using_flux").forGetter(FoundryRecipe::slagChanceUsingFlux),
            Codec.BOOL.fieldOf("is_flux_required").forGetter(FoundryRecipe::isFluxRequired)
    ).apply(instance, FoundryRecipe::new));

    public static final PacketCodec<RegistryByteBuf, FoundryRecipe> PACKET_CODEC = new PacketCodec<>() {
        private final PacketCodec<RegistryByteBuf, List<CountedIngredient>> ingredientsCodec = CountedIngredient.PACKET_CODEC.collect(PacketCodecs.toList());

        @Override
        public FoundryRecipe decode(RegistryByteBuf buf) {
            List<CountedIngredient> ingredients = ingredientsCodec.decode(buf);
            ItemStack output = ItemStack.PACKET_CODEC.decode(buf);
            CountedIngredient flux = CountedIngredient.PACKET_CODEC.decode(buf);
            ItemStack slag = ItemStack.PACKET_CODEC.decode(buf);
            int processingTime = PacketCodecs.INTEGER.decode(buf);
            float slagChance = PacketCodecs.FLOAT.decode(buf);
            float slagChanceUsingFlux = PacketCodecs.FLOAT.decode(buf);
            boolean isFluxRequired = PacketCodecs.BOOL.decode(buf);
            return new FoundryRecipe(ingredients, output, flux, slag, processingTime, slagChance, slagChanceUsingFlux, isFluxRequired);
        }

        @Override
        public void encode(RegistryByteBuf buf, FoundryRecipe recipe) {
            ingredientsCodec.encode(buf, recipe.ingredients);
            ItemStack.PACKET_CODEC.encode(buf, recipe.output);
            CountedIngredient.PACKET_CODEC.encode(buf, recipe.flux);
            ItemStack.PACKET_CODEC.encode(buf, recipe.slag);
            PacketCodecs.INTEGER.encode(buf, recipe.processingTime);
            PacketCodecs.FLOAT.encode(buf, recipe.slagChanceWithoutFlux);
            PacketCodecs.FLOAT.encode(buf, recipe.slagChanceUsingFlux);
            PacketCodecs.BOOL.encode(buf, recipe.isFluxRequired);
        }
    };

    @Override
    public boolean matches(FoundryRecipe.InputContainer input, World world) {
        if (input.getSize() != this.ingredients.size()) {
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

            List<Item> a = this.ingredients.stream().map(countedIngredient -> countedIngredient.ingredient().getMatchingStacks()[0].getItem()).toList();
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
    public ItemStack craft(FoundryRecipe.InputContainer input, RegistryWrapper.WrapperLookup registries) {
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
        return ModRecipeTypes.FOUNDRY_TYPE;
    }

    public record InputContainer(List<ItemStack> stacks, ItemStack flux) implements RecipeInput {
        @Override
        public ItemStack getStackInSlot(int slot) {
            return stacks.get(slot);
        }

        @Override
        public int getSize() {
            return stacks.size();
        }
    }

    public record Serializer() implements RecipeSerializer<FoundryRecipe> {
        @Override
        public MapCodec<FoundryRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, FoundryRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}
