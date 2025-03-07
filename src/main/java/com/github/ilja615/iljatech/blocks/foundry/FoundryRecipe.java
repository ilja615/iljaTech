package com.github.ilja615.iljatech.blocks.foundry;

import com.github.ilja615.iljatech.energy.BoilingRecipe;
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
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Function;

public record FoundryRecipe(List<CountedIngredient> ingredients, ItemStack output) implements Recipe<FoundryRecipe.InputContainer> {
    public static final MapCodec<FoundryRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.list(CountedIngredient.CODEC.codec()).fieldOf("ingredients").forGetter(FoundryRecipe::ingredients),
            ItemStack.CODEC.fieldOf("result").forGetter(FoundryRecipe::output)
    ).apply(instance, (countedIngredients, itemStack) -> new FoundryRecipe(countedIngredients, itemStack)));

    public static final PacketCodec<RegistryByteBuf, FoundryRecipe> PACKET_CODEC = PacketCodec.tuple(
        CountedIngredient.PACKET_CODEC.collect(PacketCodecs.toList()),
        FoundryRecipe::ingredients,
        ItemStack.PACKET_CODEC,
        FoundryRecipe::output,
        FoundryRecipe::new
    );

    @Override
    public boolean matches(FoundryRecipe.InputContainer input, World world) {
        if (input.getSize() != this.ingredients.size()) {
            return false;
        } else {

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

    public record InputContainer(List<ItemStack> stacks) implements RecipeInput {
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
