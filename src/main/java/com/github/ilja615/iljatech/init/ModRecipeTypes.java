package com.github.ilja615.iljatech.init;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.blocks.cokeoven.CokingRecipe;
import com.github.ilja615.iljatech.blocks.foundry.FoundryRecipe;
import com.github.ilja615.iljatech.blocks.pulverizermill.PulverizingRecipe;
import com.github.ilja615.iljatech.blocks.rollermill.RollingRecipe;
import com.github.ilja615.iljatech.blocks.spinningframe.SpinningRecipe;
import com.github.ilja615.iljatech.blocks.squeezer.SqueezingRecipe;
import com.github.ilja615.iljatech.blocks.stampinghammer.StampingRecipe;
import com.github.ilja615.iljatech.energy.BoilingRecipe;
import com.github.ilja615.iljatech.util.CountedIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipeTypes {
    public static final RecipeType<BoilingRecipe> BOILING_TYPE = register("boiling", new BoilingRecipe.Serializer());
    public static final RecipeType<FoundryRecipe> FOUNDRY_TYPE = register("foundry", new FoundryRecipe.Serializer());
    public static final RecipeType<CokingRecipe> COKING_TYPE = register("coking", new CokingRecipe.Serializer());
    public static final RecipeType<RollingRecipe> ROLLING_TYPE = register("rolling", new RollingRecipe.Serializer());
    public static final RecipeType<StampingRecipe> STAMPING_TYPE = register("stamping", new StampingRecipe.Serializer());
    public static final RecipeType<PulverizingRecipe> PULVERIZING_TYPE = register("pulverizing", new PulverizingRecipe.Serializer());
    public static final RecipeType<SqueezingRecipe> SQUEEZING_TYPE = register("squeezing", new SqueezingRecipe.Serializer());
    public static final RecipeType<SpinningRecipe> SPINNING_TYPE = register("spinning", new SpinningRecipe.Serializer());

    public static void registerIngredientTypes()
    {
        CustomIngredientSerializer.register(new CountedIngredient.Serializer());
    }

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
