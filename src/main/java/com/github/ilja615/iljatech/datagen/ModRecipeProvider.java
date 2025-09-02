package com.github.ilja615.iljatech.datagen;

import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.init.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
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

        RecipeProvider.offerSmelting(exporter, List.of(ModItems.FIRE_CLAY_BALL), RecipeCategory.MISC, ModItems.FIRE_BRICK, 0.4f, 200, "fire_brick");
}

    private static void blockFwBwRecipes(Item item, ItemConvertible block, RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, block)
                .input('#', item)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .criterion(hasItem(item), conditionsFromItem(item))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, item, 9)
                .input(block)
                .criterion(hasItem(block), conditionsFromItem(block))
                .offerTo(exporter);
    }

    private static void oreSmeltingBlastingRecipes(List<ItemConvertible> ores, Item ingot, float xp, String group, RecipeExporter exporter) {
        RecipeProvider.offerBlasting(exporter, ores, RecipeCategory.MISC, ingot, xp, 100, group);
        RecipeProvider.offerSmelting(exporter, ores, RecipeCategory.MISC, ingot, xp, 200, group);    }

    private static void plateRecipe(Item item, ItemConvertible plate, RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, plate)
                .input('#', item)
                .input('H', ModItems.IRON_HAMMER)
                .pattern("H ")
                .pattern("##")
                .criterion(hasItem(item), conditionsFromItem(item))
                .criterion(hasItem(ModItems.IRON_HAMMER), conditionsFromItem(ModItems.IRON_HAMMER))
                .offerTo(exporter);
    }

    private static void sheetMetalRecipes(ItemConvertible plate, ItemConvertible block, RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, block, 8)
                .input('#', plate)
                .pattern("##")
                .pattern("##")
                .criterion(hasItem(plate), conditionsFromItem(plate))
                .offerTo(exporter);
    }

    private static void slabStairRecipes(ItemConvertible block, ItemConvertible slab, ItemConvertible stairs, RecipeExporter exporter) {
            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, slab, 6)
                .input('#', block)
                .pattern("###")
                .criterion(hasItem(block), conditionsFromItem(block))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, stairs, 4)
                .input('#', block)
                .pattern("#  ")
                .pattern("## ")
                .pattern("###")
                .criterion(hasItem(block), conditionsFromItem(block))
                .offerTo(exporter);
    }

    private static void slabStairWallRecipes(ItemConvertible block, ItemConvertible slab, ItemConvertible stairs, ItemConvertible wall, RecipeExporter exporter) {
        slabStairRecipes(block, slab, stairs, exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, wall, 6)
                .input('#', block)
                .pattern("###")
                .pattern("###")
                .criterion(hasItem(block), conditionsFromItem(block))
                .offerTo(exporter);
    }

    private static void hammerRecipe(Item item, ItemConvertible output, RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, output)
                .input('#', item)
                .input('H', ModItems.IRON_HAMMER)
                .pattern("H")
                .pattern("#")
                .criterion(hasItem(item), conditionsFromItem(item))
                .criterion(hasItem(ModItems.IRON_HAMMER), conditionsFromItem(ModItems.IRON_HAMMER))
                .offerTo(exporter);
    }

    @Override
    public String getName() {
        return "";
    }
}
