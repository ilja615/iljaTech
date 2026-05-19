package com.github.ilja615.iljatech.datagen;

import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.init.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void buildRecipes(RecipeOutput exporter) {
        blockFwBwRecipes(ModItems.RAW_TIN_ORE, ModBlocks.RAW_TIN_ORE, exporter);
        blockFwBwRecipes(ModItems.RAW_NICKEL_ORE, ModBlocks.RAW_NICKEL_ORE, exporter);
        blockFwBwRecipes(ModItems.RAW_ALUMINIUM_ORE, ModBlocks.RAW_ALUMINIUM_ORE, exporter);
        blockFwBwRecipes(ModItems.RAW_CHROME_ORE, ModBlocks.RAW_CHROME_ORE, exporter);
        oreSmeltingBlastingRecipes(List.of(ModBlocks.TIN_ORE, ModBlocks.DEEPSLATE_TIN_ORE, ModItems.RAW_TIN_ORE),
                ModItems.TIN_INGOT, 0.7f, "tin", exporter);
        oreSmeltingBlastingRecipes(List.of(ModBlocks.NICKEL_ORE, ModBlocks.DEEPSLATE_NICKEL_ORE, ModItems.RAW_NICKEL_ORE),
                ModItems.NICKEL_INGOT, 0.7f, "nickel", exporter);
        plateRecipe(Items.IRON_INGOT, ModBlocks.IRON_PLATE, exporter);
        hammerRecipe(ModItems.STEEL_BLOOM, ModItems.STEEL_INGOT, exporter);
        hammerRecipe(ModItems.COKE, ModItems.CRUSHED_COKE, exporter);
        hammerRecipe(ModItems.RAW_ALUMINIUM_ORE, ModItems.CRUSHED_RAW_ALUMINIUM, exporter);
        hammerRecipe(ModItems.RAW_CHROME_ORE, ModItems.CRUSHED_RAW_CHROME, exporter);
        hammerRecipe(Items.RAW_COPPER, ModItems.CRUSHED_RAW_COPPER, exporter);
        hammerRecipe(Items.RAW_GOLD, ModItems.CRUSHED_RAW_GOLD, exporter);
        hammerRecipe(Items.RAW_IRON, ModItems.CRUSHED_RAW_IRON, exporter);
        hammerRecipe(ModItems.RAW_NICKEL_ORE, ModItems.CRUSHED_RAW_NICKEL, exporter);
        hammerRecipe(ModItems.RAW_TIN_ORE, ModItems.CRUSHED_RAW_TIN, exporter);
        sheetMetalRecipes(ModBlocks.IRON_PLATE, ModBlocks.IRON_SHEETMETAL, exporter);
        slabStairRecipes(ModBlocks.IRON_SHEETMETAL, ModBlocks.IRON_SHEETMETAL_SLAB, ModBlocks.IRON_SHEETMETAL_STAIRS, exporter);
        slabStairRecipes(ModBlocks.EXPOSED_IRON_SHEETMETAL, ModBlocks.EXPOSED_IRON_SHEETMETAL_SLAB, ModBlocks.EXPOSED_IRON_SHEETMETAL_STAIRS, exporter);
        slabStairRecipes(ModBlocks.WEATHERED_IRON_SHEETMETAL, ModBlocks.WEATHERED_IRON_SHEETMETAL_SLAB, ModBlocks.WEATHERED_IRON_SHEETMETAL_STAIRS, exporter);
        slabStairRecipes(ModBlocks.RUSTY_IRON_SHEETMETAL, ModBlocks.RUSTY_IRON_SHEETMETAL_SLAB, ModBlocks.RUSTY_IRON_SHEETMETAL_STAIRS, exporter);
        slabStairWallRecipes(ModBlocks.LIMESTONE, ModBlocks.LIMESTONE_SLAB, ModBlocks.LIMESTONE_STAIRS, ModBlocks.LIMESTONE_WALL, exporter);

        RecipeProvider.oreSmelting(exporter, List.of(ModItems.FIRE_CLAY_BALL), RecipeCategory.MISC, ModItems.FIRE_BRICK, 0.4f, 200, "fire_brick");
}

    private static void blockFwBwRecipes(Item item, ItemLike block, RecipeOutput exporter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, block)
                .define('#', item)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .unlockedBy(getHasName(item), has(item))
                .save(exporter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item, 9)
                .requires(block)
                .unlockedBy(getHasName(block), has(block))
                .save(exporter);
    }

    private static void oreSmeltingBlastingRecipes(List<ItemLike> ores, Item ingot, float xp, String group, RecipeOutput exporter) {
        RecipeProvider.oreBlasting(exporter, ores, RecipeCategory.MISC, ingot, xp, 100, group);
        RecipeProvider.oreSmelting(exporter, ores, RecipeCategory.MISC, ingot, xp, 200, group);    }

    private static void plateRecipe(Item item, ItemLike plate, RecipeOutput exporter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, plate)
                .define('#', item)
                .define('H', ModItems.IRON_HAMMER)
                .pattern("H ")
                .pattern("##")
                .unlockedBy(getHasName(item), has(item))
                .unlockedBy(getHasName(ModItems.IRON_HAMMER), has(ModItems.IRON_HAMMER))
                .save(exporter);
    }

    private static void sheetMetalRecipes(ItemLike plate, ItemLike block, RecipeOutput exporter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, block, 8)
                .define('#', plate)
                .pattern("##")
                .pattern("##")
                .unlockedBy(getHasName(plate), has(plate))
                .save(exporter);
    }

    private static void slabStairRecipes(ItemLike block, ItemLike slab, ItemLike stairs, RecipeOutput exporter) {
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, slab, 6)
                .define('#', block)
                .pattern("###")
                .unlockedBy(getHasName(block), has(block))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, stairs, 4)
                .define('#', block)
                .pattern("#  ")
                .pattern("## ")
                .pattern("###")
                .unlockedBy(getHasName(block), has(block))
                .save(exporter);
    }

    private static void slabStairWallRecipes(ItemLike block, ItemLike slab, ItemLike stairs, ItemLike wall, RecipeOutput exporter) {
        slabStairRecipes(block, slab, stairs, exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, wall, 6)
                .define('#', block)
                .pattern("###")
                .pattern("###")
                .unlockedBy(getHasName(block), has(block))
                .save(exporter);
    }

    private static void hammerRecipe(Item item, ItemLike output, RecipeOutput exporter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output)
                .define('#', item)
                .define('H', ModItems.IRON_HAMMER)
                .pattern("H")
                .pattern("#")
                .unlockedBy(getHasName(item), has(item))
                .unlockedBy(getHasName(ModItems.IRON_HAMMER), has(ModItems.IRON_HAMMER))
                .save(exporter);
    }

    @Override
    public String getName() {
        return "";
    }
}
