package com.github.ilja615.iljatech.datagen;

import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.init.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

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
        itemModelGenerator.register(ModItems.BRONZE_GEAR, Models.GENERATED);

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
    }
}
