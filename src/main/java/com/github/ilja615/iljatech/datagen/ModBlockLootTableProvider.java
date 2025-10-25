package com.github.ilja615.iljatech.datagen;

import com.github.ilja615.iljatech.blocks.FlaxBlock;
import com.github.ilja615.iljatech.blocks.SawDustBlock;
import com.github.ilja615.iljatech.blocks.pulverizermill.PulverizerMillBlock;
import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.init.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.*;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.AlternativeEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

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
        addDrop(ModBlocks.EXPOSED_IRON_SHEETMETAL);
        slabDrops(ModBlocks.EXPOSED_IRON_SHEETMETAL_SLAB);
        addDrop(ModBlocks.EXPOSED_IRON_SHEETMETAL_STAIRS);
        addDrop(ModBlocks.WEATHERED_IRON_SHEETMETAL);
        slabDrops(ModBlocks.WEATHERED_IRON_SHEETMETAL_SLAB);
        addDrop(ModBlocks.WEATHERED_IRON_SHEETMETAL_STAIRS);
        addDrop(ModBlocks.RUSTY_IRON_SHEETMETAL);
        slabDrops(ModBlocks.RUSTY_IRON_SHEETMETAL_SLAB);
        addDrop(ModBlocks.RUSTY_IRON_SHEETMETAL_STAIRS);
        addDrop(ModBlocks.WOODEN_SCAFFOLDING);
        addDrop(ModBlocks.STEEL_BLOCK);
        pulverizerMillDrops(ModBlocks.PULVERIZER_MILL);
        LootCondition.Builder flaxLootConditionBuilder = BlockStatePropertyLootCondition.builder(ModBlocks.FLAX_SEEDS).properties(StatePredicate.Builder.create().exactMatch(FlaxBlock.AGE, 7).exactMatch(FlaxBlock.HALF, DoubleBlockHalf.UPPER));
        addDrop(ModBlocks.FLAX_SEEDS, cropDrops(ModBlocks.FLAX_SEEDS, ModBlocks.FLAX.asItem(), ModBlocks.FLAX_SEEDS.asItem(), flaxLootConditionBuilder));
        addDrop(ModBlocks.FLAX, (block) -> this.dropsWithProperty(block, TallPlantBlock.HALF, DoubleBlockHalf.LOWER));
        addDrop(ModBlocks.SQUEEZER);
        addDrop(ModBlocks.SPINNING_FRAME);
        addDrop(ModBlocks.TIN_PLATE);
        addDrop(ModBlocks.WIND_VANE);
        addDrop(ModBlocks.SAWDUST, (block) -> {
            return LootTable.builder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with((LootPoolEntry.Builder) this.applyExplosionDecay(block, ItemEntry.builder(block).apply(IntStream.rangeClosed(1, 4).boxed().toList(), (amount) -> {
                return SetCountLootFunction.builder(ConstantLootNumberProvider.create((float)amount)).conditionally(BlockStatePropertyLootCondition.builder(block).properties(StatePredicate.Builder.create().exactMatch(SawDustBlock.LEVEL, amount)));
            }))));
        });
    }

    public LootTable.Builder pulverizerMillDrops(Block block) {
        return LootTable.builder().pool((LootPool.Builder)this.addSurvivesExplosionCondition(block, LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(block).conditionally(BlockStatePropertyLootCondition.builder(block).properties(StatePredicate.Builder.create().exactMatch(PulverizerMillBlock.HALF, Integer.valueOf(2)))))));
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
