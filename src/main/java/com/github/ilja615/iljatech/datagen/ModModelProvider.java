package com.github.ilja615.iljatech.datagen;

import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.init.ModFluids;
import com.github.ilja615.iljatech.init.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        itemModelGenerator.generateFlatItem(ModItems.BOILED_EGG, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.RAW_TIN_ORE, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.RAW_NICKEL_ORE, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.RAW_ALUMINIUM_ORE, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.RAW_CHROME_ORE, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.TIN_INGOT, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.NICKEL_INGOT, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.ALUMINIUM_INGOT, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.CHROME_INGOT, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.CRUSHED_RAW_ALUMINIUM, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.CRUSHED_RAW_CHROME, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.CRUSHED_RAW_NICKEL, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.CRUSHED_RAW_COPPER, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.CRUSHED_RAW_GOLD, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.CRUSHED_RAW_TIN, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.CRUSHED_RAW_IRON, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.BRONZE_INGOT, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.BRONZE_GEAR, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.FIRE_CLAY_BALL, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.FIRE_BRICK, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.ASH, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.BOOK, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.STEEL_INGOT, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.COKE, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.CRUSHED_COKE, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.FERROUS_SLAG, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.STEEL_BLOOM, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModFluids.CREOSOTE_OIL_BUCKET, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModFluids.SEED_OIL_BUCKET, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.CLOTH, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.FLAX_FIBER, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.BLUE_PRINT, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.WINDMILL_PART, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.SULFUR, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.SALTPETER, ModelTemplates.FLAT_ITEM);

    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        blockStateModelGenerator.createTrivialCube(ModBlocks.TIN_ORE);
        blockStateModelGenerator.createTrivialCube(ModBlocks.NICKEL_ORE);
        blockStateModelGenerator.createTrivialCube(ModBlocks.CHROME_ORE);
        blockStateModelGenerator.createTrivialCube(ModBlocks.DEEPSLATE_TIN_ORE);
        blockStateModelGenerator.createTrivialCube(ModBlocks.DEEPSLATE_NICKEL_ORE);
        blockStateModelGenerator.createTrivialCube(ModBlocks.DEEPSLATE_CHROME_ORE);
        blockStateModelGenerator.createTrivialCube(ModBlocks.GRAVEL_ALUMINIUM_ORE);
        blockStateModelGenerator.createTrivialCube(ModBlocks.RAW_TIN_ORE);
        blockStateModelGenerator.createTrivialCube(ModBlocks.RAW_NICKEL_ORE);
        blockStateModelGenerator.createTrivialCube(ModBlocks.RAW_ALUMINIUM_ORE);
        blockStateModelGenerator.createTrivialCube(ModBlocks.RAW_CHROME_ORE);

        blockStateModelGenerator.createTrivialCube(ModBlocks.NAILED_ACACIA_PLANKS);
        blockStateModelGenerator.createTrivialCube(ModBlocks.NAILED_BAMBOO_PLANKS);
        blockStateModelGenerator.createTrivialCube(ModBlocks.NAILED_BIRCH_PLANKS);
        blockStateModelGenerator.createTrivialCube(ModBlocks.NAILED_CHERRY_PLANKS);
        blockStateModelGenerator.createTrivialCube(ModBlocks.NAILED_CRIMSON_PLANKS);
        blockStateModelGenerator.createTrivialCube(ModBlocks.NAILED_DARK_OAK_PLANKS);
        blockStateModelGenerator.createTrivialCube(ModBlocks.NAILED_JUNGLE_PLANKS);
        blockStateModelGenerator.createTrivialCube(ModBlocks.NAILED_MANGROVE_PLANKS);
        blockStateModelGenerator.createTrivialCube(ModBlocks.NAILED_OAK_PLANKS);
        blockStateModelGenerator.createTrivialCube(ModBlocks.NAILED_SPRUCE_PLANKS);
        blockStateModelGenerator.createTrivialCube(ModBlocks.NAILED_WARPED_PLANKS);

        blockStateModelGenerator.createTrivialCube(ModBlocks.ACACIA_FRAME);
        blockStateModelGenerator.createTrivialCube(ModBlocks.BAMBOO_FRAME);
        blockStateModelGenerator.createTrivialCube(ModBlocks.BIRCH_FRAME);
        blockStateModelGenerator.createTrivialCube(ModBlocks.CHERRY_FRAME);
        blockStateModelGenerator.createTrivialCube(ModBlocks.CRIMSON_FRAME);
        blockStateModelGenerator.createTrivialCube(ModBlocks.DARK_OAK_FRAME);
        blockStateModelGenerator.createTrivialCube(ModBlocks.JUNGLE_FRAME);
        blockStateModelGenerator.createTrivialCube(ModBlocks.MANGROVE_FRAME);
        blockStateModelGenerator.createTrivialCube(ModBlocks.OAK_FRAME);
        blockStateModelGenerator.createTrivialCube(ModBlocks.SPRUCE_FRAME);
        blockStateModelGenerator.createTrivialCube(ModBlocks.WARPED_FRAME);

        blockStateModelGenerator.createTrivialCube(ModBlocks.FIRE_CLAY);
        blockStateModelGenerator.createTrivialCube(ModBlocks.STEEL_BLOCK);

        var limestoneFamily = new BlockFamily.Builder(ModBlocks.LIMESTONE)
                .slab(ModBlocks.LIMESTONE_SLAB)
                .stairs(ModBlocks.LIMESTONE_STAIRS)
                .wall(ModBlocks.LIMESTONE_WALL)
                .getFamily();
        blockStateModelGenerator.family(limestoneFamily.getBaseBlock())
                .generateFor(limestoneFamily);

        var ironSheetMetalFamily = new BlockFamily.Builder(ModBlocks.IRON_SHEETMETAL)
                .slab(ModBlocks.IRON_SHEETMETAL_SLAB)
                .stairs(ModBlocks.IRON_SHEETMETAL_STAIRS)
                .getFamily();
        blockStateModelGenerator.family(ironSheetMetalFamily.getBaseBlock()).generateFor(ironSheetMetalFamily);
        var exposedIronSheetMetalFamily = new BlockFamily.Builder(ModBlocks.EXPOSED_IRON_SHEETMETAL)
                .slab(ModBlocks.EXPOSED_IRON_SHEETMETAL_SLAB)
                .stairs(ModBlocks.EXPOSED_IRON_SHEETMETAL_STAIRS)
                .getFamily();
        blockStateModelGenerator.family(exposedIronSheetMetalFamily.getBaseBlock()).generateFor(exposedIronSheetMetalFamily);
        var weatheredIronSheetMetalFamily = new BlockFamily.Builder(ModBlocks.WEATHERED_IRON_SHEETMETAL)
                .slab(ModBlocks.WEATHERED_IRON_SHEETMETAL_SLAB)
                .stairs(ModBlocks.WEATHERED_IRON_SHEETMETAL_STAIRS)
                .getFamily();
        blockStateModelGenerator.family(weatheredIronSheetMetalFamily.getBaseBlock()).generateFor(weatheredIronSheetMetalFamily);
        var rustyIronSheetMetalFamily = new BlockFamily.Builder(ModBlocks.RUSTY_IRON_SHEETMETAL)
                .slab(ModBlocks.RUSTY_IRON_SHEETMETAL_SLAB)
                .stairs(ModBlocks.RUSTY_IRON_SHEETMETAL_STAIRS)
                .getFamily();
        blockStateModelGenerator.family(rustyIronSheetMetalFamily.getBaseBlock()).generateFor(rustyIronSheetMetalFamily);
    }
}
