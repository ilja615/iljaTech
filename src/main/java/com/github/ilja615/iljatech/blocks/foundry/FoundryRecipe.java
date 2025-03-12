package com.github.ilja615.iljatech.blocks.foundry;

import com.github.ilja615.iljatech.energy.BoilingRecipe;
import com.github.ilja615.iljatech.init.ModRecipeTypes;
import com.github.ilja615.iljatech.util.CountedIngredient;
import com.mojang.datafixers.util.Function6;
import com.mojang.datafixers.util.Function8;
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

public record FoundryRecipe(List<CountedIngredient> ingredients, ItemStack output, CountedIngredient flux, ItemStack slag,
                            int processingTime, float slagChance, float slagChanceUsingFlux, boolean isFluxRequired) implements Recipe<FoundryRecipe.InputContainer> {
    public static final MapCodec<FoundryRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.list(CountedIngredient.CODEC.codec()).fieldOf("ingredients").forGetter(FoundryRecipe::ingredients),
            ItemStack.CODEC.fieldOf("result").forGetter(FoundryRecipe::output),
            CountedIngredient.CODEC.fieldOf("flux").forGetter(FoundryRecipe::flux),
            ItemStack.CODEC.fieldOf("slag").forGetter(FoundryRecipe::slag),
            Codec.INT.fieldOf("processing_time").forGetter(FoundryRecipe::processingTime),
            Codec.FLOAT.fieldOf("slag_chance_without_flux").forGetter(FoundryRecipe::slagChance),
            Codec.FLOAT.fieldOf("slag_chance_using_flux").forGetter(FoundryRecipe::slagChanceUsingFlux),
            Codec.BOOL.fieldOf("is_flux_required").forGetter(FoundryRecipe::isFluxRequired)
    ).apply(instance, FoundryRecipe::new));

    public static final PacketCodec<RegistryByteBuf, FoundryRecipe> PACKET_CODEC = tuple(
        CountedIngredient.PACKET_CODEC.collect(PacketCodecs.toList()),
        FoundryRecipe::ingredients,
        ItemStack.PACKET_CODEC,
        FoundryRecipe::output,
        CountedIngredient.PACKET_CODEC,
        FoundryRecipe::flux,
        ItemStack.PACKET_CODEC,
        FoundryRecipe::slag,
        PacketCodecs.INTEGER,
        FoundryRecipe::processingTime,
        PacketCodecs.FLOAT,
        FoundryRecipe::slagChance,
        PacketCodecs.FLOAT,
        FoundryRecipe::slagChanceUsingFlux,
        PacketCodecs.BOOL,
        FoundryRecipe::isFluxRequired,
        FoundryRecipe::new
    );

    @Override
    public boolean matches(FoundryRecipe.InputContainer input, World world) {
        if (input.getSize() != this.ingredients.size()) {
            return false;
        } else {
            // Check if a correct flux is provided
            if (!this.flux.getMatchingStacks().stream().map(ItemStack::getItem).toList().contains(input.flux.getItem())) {
                return false;
            }
            // Check if enough flux is provided
            if (input.flux.getCount() < this.flux.count()) {
                return false;
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

    static <B, C, T1, T2, T3, T4, T5, T6, T7, T8> PacketCodec<B, C> tuple(final PacketCodec<? super B, T1> codec1, final Function<C, T1> from1,
                                                                          final PacketCodec<? super B, T2> codec2, final Function<C, T2> from2,
                                                                          final PacketCodec<? super B, T3> codec3, final Function<C, T3> from3,
                                                                          final PacketCodec<? super B, T4> codec4, final Function<C, T4> from4,
                                                                          final PacketCodec<? super B, T5> codec5, final Function<C, T5> from5,
                                                                          final PacketCodec<? super B, T6> codec6, final Function<C, T6> from6,
                                                                          final PacketCodec<? super B, T7> codec7, final Function<C, T7> from7,
                                                                          final PacketCodec<? super B, T8> codec8, final Function<C, T8> from8,
                                                                          final Function8<T1, T2, T3, T4, T5, T6, T7, T8, C> to) {
        return new PacketCodec<B, C>() {
            public C decode(B object) {
                T1 object1 = codec1.decode(object);
                T2 object2 = codec2.decode(object);
                T3 object3 = codec3.decode(object);
                T4 object4 = codec4.decode(object);
                T5 object5 = codec5.decode(object);
                T6 object6 = codec6.decode(object);
                T7 object7 = codec7.decode(object);
                T8 object8 = codec8.decode(object);
                return to.apply(object1, object2, object3, object4, object5, object6, object7, object8);
            }

            public void encode(B object, C object2) {
                codec1.encode(object, from1.apply(object2));
                codec2.encode(object, from2.apply(object2));
                codec3.encode(object, from3.apply(object2));
                codec4.encode(object, from4.apply(object2));
                codec5.encode(object, from5.apply(object2));
                codec6.encode(object, from6.apply(object2));
                codec7.encode(object, from7.apply(object2));
                codec8.encode(object, from8.apply(object2));
            }
        };
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
