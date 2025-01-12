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

    @Override
    public String getName() {
        return "";
    }
}
