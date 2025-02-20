package com.github.ilja615.iljatech.init;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.recipe.BoilingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipeTypes {
    public static final RecipeType<BoilingRecipe> BOILING_TYPE = Registry.register(
            Registries.RECIPE_TYPE,
            Identifier.of(IljaTech.MOD_ID, "boiling"),
            new RecipeType<>() {
                private static final Identifier ID = Identifier.of(IljaTech.MOD_ID, "boiling");

                @Override
                public String toString() {
                    return ID.toString();
                }
            }
    );

    public static void load() {}
}
