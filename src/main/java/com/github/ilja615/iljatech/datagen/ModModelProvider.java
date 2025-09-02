package com.github.ilja615.iljatech.datagen;

import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.init.ModFluids;
import com.github.ilja615.iljatech.init.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.data.family.BlockFamily;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.BOILED_EGG, Models.GENERATED);
        itemModelGenerator.register(ModItems.RAW_TIN_ORE, Models.GENERATED);
        itemModelGenerator.register(ModItems.RAW_NICKEL_ORE, Models.GENERATED);
        itemModelGenerator.register(ModItems.RAW_ALUMINIUM_ORE, Models.GENERATED);
        itemModelGenerator.register(ModItems.RAW_CHROME_ORE, Models.GENERATED);
        itemModelGenerator.register(ModItems.TIN_INGOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.NICKEL_INGOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.ALUMINIUM_INGOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.CHROME_INGOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.CRUSHED_RAW_ALUMINIUM, Models.GENERATED);
        itemModelGenerator.register(ModItems.CRUSHED_RAW_CHROME, Models.GENERATED);
        itemModelGenerator.register(ModItems.CRUSHED_RAW_NICKEL, Models.GENERATED);
        itemModelGenerator.register(ModItems.CRUSHED_RAW_COPPER, Models.GENERATED);
        itemModelGenerator.register(ModItems.CRUSHED_RAW_GOLD, Models.GENERATED);
        itemModelGenerator.register(ModItems.CRUSHED_RAW_TIN, Models.GENERATED);
        itemModelGenerator.register(ModItems.CRUSHED_RAW_IRON, Models.GENERATED);
        itemModelGenerator.register(ModItems.BRONZE_INGOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.BRONZE_GEAR, Models.GENERATED);
        itemModelGenerator.register(ModItems.FIRE_CLAY_BALL, Models.GENERATED);
        itemModelGenerator.register(ModItems.FIRE_BRICK, Models.GENERATED);
        itemModelGenerator.register(ModItems.ASH, Models.GENERATED);
        itemModelGenerator.register(ModItems.BOOK, Models.GENERATED);
        itemModelGenerator.register(ModItems.STEEL_INGOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.COKE, Models.GENERATED);
        itemModelGenerator.register(ModItems.CRUSHED_COKE, Models.GENERATED);
        itemModelGenerator.register(ModItems.FERROUS_SLAG, Models.GENERATED);
        itemModelGenerator.register(ModItems.STEEL_BLOOM, Models.GENERATED);
        itemModelGenerator.register(ModFluids.CREOSOTE_OIL_BUCKET, Models.GENERATED);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.TIN_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.NICKEL_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.CHROME_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DEEPSLATE_TIN_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DEEPSLATE_NICKEL_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DEEPSLATE_CHROME_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.GRAVEL_ALUMINIUM_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.RAW_TIN_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.RAW_NICKEL_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.RAW_ALUMINIUM_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.RAW_CHROME_ORE);

        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.NAILED_ACACIA_PLANKS);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.NAILED_BAMBOO_PLANKS);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.NAILED_BIRCH_PLANKS);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.NAILED_CHERRY_PLANKS);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.NAILED_CRIMSON_PLANKS);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.NAILED_DARK_OAK_PLANKS);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.NAILED_JUNGLE_PLANKS);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.NAILED_MANGROVE_PLANKS);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.NAILED_OAK_PLANKS);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.NAILED_SPRUCE_PLANKS);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.NAILED_WARPED_PLANKS);

        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.FIRE_CLAY);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.ITEM_HATCH);

        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.WOODEN_SCAFFOLDING);

        var limestoneFamily = new BlockFamily.Builder(ModBlocks.LIMESTONE)
                .slab(ModBlocks.LIMESTONE_SLAB)
                .stairs(ModBlocks.LIMESTONE_STAIRS)
                .wall(ModBlocks.LIMESTONE_WALL)
                .build();
        blockStateModelGenerator.registerCubeAllModelTexturePool(limestoneFamily.getBaseBlock())
                .family(limestoneFamily);

        var ironSheetMetalFamily = new BlockFamily.Builder(ModBlocks.IRON_SHEETMETAL)
                .slab(ModBlocks.IRON_SHEETMETAL_SLAB)
                .stairs(ModBlocks.IRON_SHEETMETAL_STAIRS)
                .build();
        blockStateModelGenerator.registerCubeAllModelTexturePool(ironSheetMetalFamily.getBaseBlock()).family(ironSheetMetalFamily);
        var exposedIronSheetMetalFamily = new BlockFamily.Builder(ModBlocks.EXPOSED_IRON_SHEETMETAL)
                .slab(ModBlocks.EXPOSED_IRON_SHEETMETAL_SLAB)
                .stairs(ModBlocks.EXPOSED_IRON_SHEETMETAL_STAIRS)
                .build();
        blockStateModelGenerator.registerCubeAllModelTexturePool(exposedIronSheetMetalFamily.getBaseBlock()).family(exposedIronSheetMetalFamily);
        var weatheredIronSheetMetalFamily = new BlockFamily.Builder(ModBlocks.WEATHERED_IRON_SHEETMETAL)
                .slab(ModBlocks.WEATHERED_IRON_SHEETMETAL_SLAB)
                .stairs(ModBlocks.WEATHERED_IRON_SHEETMETAL_STAIRS)
                .build();
        blockStateModelGenerator.registerCubeAllModelTexturePool(weatheredIronSheetMetalFamily.getBaseBlock()).family(weatheredIronSheetMetalFamily);
        var rustyIronSheetMetalFamily = new BlockFamily.Builder(ModBlocks.RUSTY_IRON_SHEETMETAL)
                .slab(ModBlocks.RUSTY_IRON_SHEETMETAL_SLAB)
                .stairs(ModBlocks.RUSTY_IRON_SHEETMETAL_STAIRS)
                .build();
        blockStateModelGenerator.registerCubeAllModelTexturePool(rustyIronSheetMetalFamily.getBaseBlock()).family(rustyIronSheetMetalFamily);
    }
}
