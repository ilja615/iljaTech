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
                .add(ModBlocks.GEARBOX).add(ModBlocks.DRILL);

        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.CHROME_ORE).add(ModBlocks.DEEPSLATE_CHROME_ORE).add(ModBlocks.RAW_CHROME_ORE);

        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(ModBlocks.TIN_ORE).add(ModBlocks.DEEPSLATE_TIN_ORE).add(ModBlocks.RAW_TIN_ORE)
                .add(ModBlocks.NICKEL_ORE).add(ModBlocks.DEEPSLATE_NICKEL_ORE).add(ModBlocks.RAW_NICKEL_ORE)
                .add(ModBlocks.SANDSTONE_ALUMINIUM_ORE).add(ModBlocks.RED_SANDSTONE_ALUMINIUM_ORE).add(ModBlocks.RAW_ALUMINIUM_ORE)
                .add(ModBlocks.CHROME_ORE).add(ModBlocks.DEEPSLATE_CHROME_ORE).add(ModBlocks.RAW_CHROME_ORE)
                .add(ModBlocks.GEARBOX).add(ModBlocks.ROLLER_MILL).add(ModBlocks.DRILL);

        getOrCreateTagBuilder(BlockTags.SHOVEL_MINEABLE)
                .add(ModBlocks.GRAVEL_ALUMINIUM_ORE);

        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE)
                .add(ModBlocks.CRANK).add(ModBlocks.WOODEN_SHAFT);
    }
}
