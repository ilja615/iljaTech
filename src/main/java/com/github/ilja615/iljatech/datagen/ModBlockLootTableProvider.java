package com.github.ilja615.iljatech.datagen;

import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.init.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModBlockLootTableProvider extends FabricBlockLootTableProvider {
    public ModBlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(ModBlocks.TIN_ORE, this::tinOreDrops);
        addDrop(ModBlocks.DEEPSLATE_TIN_ORE, this::tinOreDrops);
        addDrop(ModBlocks.NICKEL_ORE, this::nickelOreDrops);
        addDrop(ModBlocks.DEEPSLATE_NICKEL_ORE, this::nickelOreDrops);
        addDrop(ModBlocks.CHROME_ORE, oreDrops(ModBlocks.CHROME_ORE, ModItems.RAW_CHROME_ORE));
        addDrop(ModBlocks.DEEPSLATE_CHROME_ORE, oreDrops(ModBlocks.DEEPSLATE_CHROME_ORE, ModItems.RAW_CHROME_ORE));
        addDrop(ModBlocks.SANDSTONE_ALUMINIUM_ORE, oreDrops(ModBlocks.SANDSTONE_ALUMINIUM_ORE, ModItems.RAW_ALUMINIUM_ORE));
        addDrop(ModBlocks.RED_SANDSTONE_ALUMINIUM_ORE, oreDrops(ModBlocks.RED_SANDSTONE_ALUMINIUM_ORE, ModItems.RAW_ALUMINIUM_ORE));
        addDrop(ModBlocks.GRAVEL_ALUMINIUM_ORE, oreDrops(ModBlocks.GRAVEL_ALUMINIUM_ORE, ModItems.RAW_ALUMINIUM_ORE));
        addDrop(ModBlocks.RAW_TIN_ORE);
        addDrop(ModBlocks.RAW_NICKEL_ORE);
        addDrop(ModBlocks.RAW_ALUMINIUM_ORE);
        addDrop(ModBlocks.RAW_CHROME_ORE);
        addDrop(ModBlocks.CRANK);
        addDrop(ModBlocks.GEARBOX);
        addDrop(ModBlocks.TURBINE);
        addDrop(ModBlocks.WOODEN_SHAFT);
        addDrop(ModBlocks.ROLLER_MILL);
        addDrop(ModBlocks.DRILL);
        addDrop(ModBlocks.IRON_PLATE);
        addDrop(ModBlocks.COPPER_ROD);
        addDrop(ModBlocks.IRON_ROD);
        addDrop(ModBlocks.COPPER_WIRE);
        addDrop(ModBlocks.NAILED_ACACIA_PLANKS);
        addDrop(ModBlocks.NAILED_BAMBOO_PLANKS);
        addDrop(ModBlocks.NAILED_BIRCH_PLANKS);
        addDrop(ModBlocks.NAILED_CHERRY_PLANKS);
        addDrop(ModBlocks.NAILED_CRIMSON_PLANKS);
        addDrop(ModBlocks.NAILED_DARK_OAK_PLANKS);
        addDrop(ModBlocks.NAILED_JUNGLE_PLANKS);
        addDrop(ModBlocks.NAILED_MANGROVE_PLANKS);
        addDrop(ModBlocks.NAILED_OAK_PLANKS);
        addDrop(ModBlocks.NAILED_SPRUCE_PLANKS);
        addDrop(ModBlocks.NAILED_WARPED_PLANKS);
        addDrop(ModBlocks.BELLOWS);
        addDrop(ModBlocks.FIRE_BRICKS);
        addDrop(ModBlocks.CLINKER_BRICKS);
        this.addDrop(ModBlocks.FIRE_CLAY, block -> this.drops(block, ModItems.FIRE_CLAY_BALL, ConstantLootNumberProvider.create(4.0F)));
        addDrop(ModBlocks.FIREBOX);
        addDrop(ModBlocks.FOUNDRY);
        addDrop(ModBlocks.ITEM_HATCH);
        addDrop(ModBlocks.COKE_OVEN);
        addDrop(ModBlocks.LIMESTONE);
        addDrop(ModBlocks.LIMESTONE_STAIRS);
        addDrop(ModBlocks.LIMESTONE_WALL);
        slabDrops(ModBlocks.LIMESTONE_SLAB);
        addDrop(ModBlocks.FUNNEL);
        addDrop(ModBlocks.STEEL_PIPE);
        addDrop(ModBlocks.CONVEYOR_BELT);
        addDrop(ModBlocks.IRON_SHEETMETAL);
        slabDrops(ModBlocks.IRON_SHEETMETAL_SLAB);
        addDrop(ModBlocks.IRON_SHEETMETAL_STAIRS);
        addDrop(ModBlocks.WEATHERED_IRON_SHEETMETAL);
        slabDrops(ModBlocks.WEATHERED_IRON_SHEETMETAL_SLAB);
        addDrop(ModBlocks.WEATHERED_IRON_SHEETMETAL_STAIRS);
        addDrop(ModBlocks.RUSTY_IRON_SHEETMETAL);
        slabDrops(ModBlocks.RUSTY_IRON_SHEETMETAL_SLAB);
        addDrop(ModBlocks.RUSTY_IRON_SHEETMETAL_STAIRS);
        addDrop(ModBlocks.WOODEN_SCAFFOLDING);
    }

    public LootTable.Builder tinOreDrops(Block drop) {
        RegistryWrapper.Impl<Enchantment> impl = this.registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
        return this.dropsWithSilkTouch(
                drop,
                (LootPoolEntry.Builder<?>)this.applyExplosionDecay(
                        drop,
                        ItemEntry.builder(ModItems.RAW_TIN_ORE)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F)))
                                .apply(ApplyBonusLootFunction.oreDrops(impl.getOrThrow(Enchantments.FORTUNE)))
                )
        );
    }

    public LootTable.Builder nickelOreDrops(Block drop) {
        RegistryWrapper.Impl<Enchantment> impl = this.registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
        return this.dropsWithSilkTouch(
                drop,
                (LootPoolEntry.Builder<?>)this.applyExplosionDecay(
                        drop,
                        ItemEntry.builder(ModItems.RAW_NICKEL_ORE)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 4.0F)))
                                .apply(ApplyBonusLootFunction.oreDrops(impl.getOrThrow(Enchantments.FORTUNE)))
                )
        );
    }
}
