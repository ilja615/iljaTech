package com.github.ilja615.iljatech;

import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.init.ModItems;
import net.fabricmc.fabric.api.registry.FuelRegistry;

public class IljaTechEvents {
    public static void registerFuels() {
        FuelRegistry.INSTANCE.add(ModItems.COKE, 3200);
        FuelRegistry.INSTANCE.add(ModItems.PEAT, 800);
        FuelRegistry.INSTANCE.add(ModBlocks.SAWDUST, 100);
        FuelRegistry.INSTANCE.add(ModBlocks.ACACIA_FRAME, 300);
        FuelRegistry.INSTANCE.add(ModBlocks.BAMBOO_FRAME, 300);
        FuelRegistry.INSTANCE.add(ModBlocks.BIRCH_FRAME, 300);
        FuelRegistry.INSTANCE.add(ModBlocks.CHERRY_FRAME, 300);
        FuelRegistry.INSTANCE.add(ModBlocks.DARK_OAK_FRAME, 300);
        FuelRegistry.INSTANCE.add(ModBlocks.JUNGLE_FRAME, 300);
        FuelRegistry.INSTANCE.add(ModBlocks.OAK_FRAME, 300);
        FuelRegistry.INSTANCE.add(ModBlocks.SPRUCE_FRAME, 300);
        FuelRegistry.INSTANCE.add(ModBlocks.MANGROVE_FRAME, 300);
        FuelRegistry.INSTANCE.add(ModBlocks.CHAIR, 200);
        FuelRegistry.INSTANCE.add(ModBlocks.CRANK, 100);
        FuelRegistry.INSTANCE.add(ModBlocks.WOODEN_SHAFT, 200);

    }
}
