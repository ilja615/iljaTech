package com.github.ilja615.iljatech.datagen;

import com.github.ilja615.iljatech.init.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.TIN_ORE).add(ModBlocks.DEEPSLATE_TIN_ORE).add(ModBlocks.RAW_TIN_ORE)
                .add(ModBlocks.NICKEL_ORE).add(ModBlocks.DEEPSLATE_NICKEL_ORE).add(ModBlocks.RAW_NICKEL_ORE)
                .add(ModBlocks.SANDSTONE_ALUMINIUM_ORE).add(ModBlocks.RED_SANDSTONE_ALUMINIUM_ORE).add(ModBlocks.GRAVEL_ALUMINIUM_ORE).add(ModBlocks.RAW_ALUMINIUM_ORE)
                .add(ModBlocks.GEARBOX).add(ModBlocks.DRILL)
                .add(ModBlocks.IRON_SHEETMETAL).add(ModBlocks.IRON_SHEETMETAL_SLAB).add(ModBlocks.IRON_SHEETMETAL_STAIRS)
                .add(ModBlocks.EXPOSED_IRON_SHEETMETAL).add(ModBlocks.EXPOSED_IRON_SHEETMETAL_SLAB).add(ModBlocks.EXPOSED_IRON_SHEETMETAL_STAIRS)
                .add(ModBlocks.WEATHERED_IRON_SHEETMETAL).add(ModBlocks.WEATHERED_IRON_SHEETMETAL_SLAB).add(ModBlocks.WEATHERED_IRON_SHEETMETAL_STAIRS)
                .add(ModBlocks.RUSTY_IRON_SHEETMETAL).add(ModBlocks.RUSTY_IRON_SHEETMETAL_SLAB).add(ModBlocks.RUSTY_IRON_SHEETMETAL_STAIRS);

        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.CHROME_ORE).add(ModBlocks.DEEPSLATE_CHROME_ORE).add(ModBlocks.RAW_CHROME_ORE)
                .add(ModBlocks.STEEL_BLOCK);

        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(ModBlocks.TIN_ORE).add(ModBlocks.DEEPSLATE_TIN_ORE).add(ModBlocks.RAW_TIN_ORE)
                .add(ModBlocks.NICKEL_ORE).add(ModBlocks.DEEPSLATE_NICKEL_ORE).add(ModBlocks.RAW_NICKEL_ORE)
                .add(ModBlocks.SANDSTONE_ALUMINIUM_ORE).add(ModBlocks.RED_SANDSTONE_ALUMINIUM_ORE).add(ModBlocks.RAW_ALUMINIUM_ORE)
                .add(ModBlocks.CHROME_ORE).add(ModBlocks.DEEPSLATE_CHROME_ORE).add(ModBlocks.RAW_CHROME_ORE)
                .add(ModBlocks.GEARBOX).add(ModBlocks.ROLLER_MILL).add(ModBlocks.DRILL)
                .add(ModBlocks.COPPER_WIRE).add(ModBlocks.FIRE_BRICKS).add(ModBlocks.FIREBOX).add(ModBlocks.FOUNDRY)
                .add(ModBlocks.CLINKER_BRICKS).add(ModBlocks.COKE_OVEN)
                .add(ModBlocks.TERRACOTTA_PIPE).add(ModBlocks.STEEL_PIPE)
                .add(ModBlocks.LIMESTONE).add(ModBlocks.LIMESTONE_SLAB).add(ModBlocks.LIMESTONE_STAIRS).add(ModBlocks.LIMESTONE_WALL)
                .add(ModBlocks.IRON_SHEETMETAL).add(ModBlocks.IRON_SHEETMETAL_SLAB).add(ModBlocks.IRON_SHEETMETAL_STAIRS)
                .add(ModBlocks.EXPOSED_IRON_SHEETMETAL).add(ModBlocks.EXPOSED_IRON_SHEETMETAL_SLAB).add(ModBlocks.EXPOSED_IRON_SHEETMETAL_STAIRS)
                .add(ModBlocks.WEATHERED_IRON_SHEETMETAL).add(ModBlocks.WEATHERED_IRON_SHEETMETAL_SLAB).add(ModBlocks.WEATHERED_IRON_SHEETMETAL_STAIRS)
                .add(ModBlocks.RUSTY_IRON_SHEETMETAL).add(ModBlocks.RUSTY_IRON_SHEETMETAL_SLAB).add(ModBlocks.RUSTY_IRON_SHEETMETAL_STAIRS)
                .add(ModBlocks.STEEL_BLOCK).add(ModBlocks.PULVERIZER_MILL);

        getOrCreateTagBuilder(BlockTags.SHOVEL_MINEABLE)
                .add(ModBlocks.GRAVEL_ALUMINIUM_ORE).add(ModBlocks.FIRE_CLAY).add(ModBlocks.CLAY_PIPE);

        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE)
                .add(ModBlocks.CRANK).add(ModBlocks.WOODEN_SHAFT).add(ModBlocks.CONVEYOR_BELT).add(ModBlocks.NAILED_ACACIA_PLANKS)
                .add(ModBlocks.NAILED_BAMBOO_PLANKS).add(ModBlocks.NAILED_BIRCH_PLANKS).add(ModBlocks.NAILED_CHERRY_PLANKS)
                .add(ModBlocks.NAILED_CRIMSON_PLANKS).add(ModBlocks.NAILED_DARK_OAK_PLANKS).add(ModBlocks.NAILED_JUNGLE_PLANKS)
                .add(ModBlocks.NAILED_MANGROVE_PLANKS).add(ModBlocks.NAILED_OAK_PLANKS).add(ModBlocks.NAILED_SPRUCE_PLANKS).add(ModBlocks.NAILED_WARPED_PLANKS);

        getOrCreateTagBuilder(BlockTags.WALLS)
                .add(ModBlocks.LIMESTONE_WALL);
    }
}
