package com.github.ilja615.iljatech.init;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.recipe.BoilingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipeTypes {
    public static final RecipeType<BoilingRecipe> BOILING_TYPE = register("boiling", new BoilingRecipe.Serializer());

    public static <T extends Recipe<?>> RecipeType<T> register(String name, RecipeSerializer<T> serializer) {
        Identifier ID = Identifier.of(IljaTech.MOD_ID, name);
        RecipeType<T> type = new RecipeType<>() {
            @Override
            public String toString() {
                return ID.toString();
            }
        };

        Registry.register(Registries.RECIPE_TYPE, ID, type);
        Registry.register(Registries.RECIPE_SERIALIZER, ID, serializer);

        return type;
    }

    public static void load() {}
}
