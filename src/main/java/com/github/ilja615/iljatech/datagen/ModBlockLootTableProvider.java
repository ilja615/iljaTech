package com.github.ilja615.iljatech.datagen;

import com.github.ilja615.iljatech.blocks.FlaxBlock;
import com.github.ilja615.iljatech.blocks.SawDustBlock;
import com.github.ilja615.iljatech.blocks.pulverizermill.PulverizerMillBlock;
import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.init.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.block.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

public class ModBlockLootTableProvider extends FabricBlockLootTableProvider {
    public ModBlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        add(ModBlocks.TIN_ORE, this::tinOreDrops);
        add(ModBlocks.DEEPSLATE_TIN_ORE, this::tinOreDrops);
        add(ModBlocks.NICKEL_ORE, this::nickelOreDrops);
        add(ModBlocks.DEEPSLATE_NICKEL_ORE, this::nickelOreDrops);
        add(ModBlocks.CHROME_ORE, createOreDrop(ModBlocks.CHROME_ORE, ModItems.RAW_CHROME_ORE));
        add(ModBlocks.DEEPSLATE_CHROME_ORE, createOreDrop(ModBlocks.DEEPSLATE_CHROME_ORE, ModItems.RAW_CHROME_ORE));
        add(ModBlocks.SANDSTONE_ALUMINIUM_ORE, createOreDrop(ModBlocks.SANDSTONE_ALUMINIUM_ORE, ModItems.RAW_ALUMINIUM_ORE));
        add(ModBlocks.RED_SANDSTONE_ALUMINIUM_ORE, createOreDrop(ModBlocks.RED_SANDSTONE_ALUMINIUM_ORE, ModItems.RAW_ALUMINIUM_ORE));
        add(ModBlocks.GRAVEL_ALUMINIUM_ORE, createOreDrop(ModBlocks.GRAVEL_ALUMINIUM_ORE, ModItems.RAW_ALUMINIUM_ORE));
        dropSelf(ModBlocks.RAW_TIN_ORE);
        dropSelf(ModBlocks.RAW_NICKEL_ORE);
        dropSelf(ModBlocks.RAW_ALUMINIUM_ORE);
        dropSelf(ModBlocks.RAW_CHROME_ORE);
        dropSelf(ModBlocks.CRANK);
        dropSelf(ModBlocks.GEARBOX);
        dropSelf(ModBlocks.TURBINE);
        dropSelf(ModBlocks.WOODEN_SHAFT);
        dropSelf(ModBlocks.ROLLER_MILL);
        dropSelf(ModBlocks.DRILL);
        dropSelf(ModBlocks.IRON_PLATE);
        dropSelf(ModBlocks.COPPER_ROD);
        dropSelf(ModBlocks.IRON_ROD);
        dropSelf(ModBlocks.COPPER_WIRE);
        dropSelf(ModBlocks.NAILED_ACACIA_PLANKS);
        dropSelf(ModBlocks.NAILED_BAMBOO_PLANKS);
        dropSelf(ModBlocks.NAILED_BIRCH_PLANKS);
        dropSelf(ModBlocks.NAILED_CHERRY_PLANKS);
        dropSelf(ModBlocks.NAILED_CRIMSON_PLANKS);
        dropSelf(ModBlocks.NAILED_DARK_OAK_PLANKS);
        dropSelf(ModBlocks.NAILED_JUNGLE_PLANKS);
        dropSelf(ModBlocks.NAILED_MANGROVE_PLANKS);
        dropSelf(ModBlocks.NAILED_OAK_PLANKS);
        dropSelf(ModBlocks.NAILED_SPRUCE_PLANKS);
        dropSelf(ModBlocks.NAILED_WARPED_PLANKS);
        dropSelf(ModBlocks.BELLOWS);
        dropSelf(ModBlocks.FIRE_BRICKS);
        dropSelf(ModBlocks.CLINKER_BRICKS);
        this.add(ModBlocks.FIRE_CLAY, block -> this.createSingleItemTableWithSilkTouch(block, ModItems.FIRE_CLAY_BALL, ConstantValue.exactly(4.0F)));
        dropSelf(ModBlocks.FIREBOX);
        dropSelf(ModBlocks.FOUNDRY);
        dropOther(ModBlocks.ITEM_HATCH, ModBlocks.COKE_OVEN);
        dropSelf(ModBlocks.COKE_OVEN);
        dropSelf(ModBlocks.LIMESTONE);
        dropSelf(ModBlocks.LIMESTONE_STAIRS);
        dropSelf(ModBlocks.LIMESTONE_WALL);
        createSlabItemTable(ModBlocks.LIMESTONE_SLAB);
        dropSelf(ModBlocks.FUNNEL);
        dropSelf(ModBlocks.WOODEN_FUNNEL);
        dropSelf(ModBlocks.STEEL_PIPE);
        dropSelf(ModBlocks.WOODEN_PIPE);
        dropSelf(ModBlocks.TERRACOTTA_PIPE);
        dropSelf(ModBlocks.CONVEYOR_BELT);
        dropSelf(ModBlocks.IRON_SHEETMETAL);
        createSlabItemTable(ModBlocks.IRON_SHEETMETAL_SLAB);
        dropSelf(ModBlocks.IRON_SHEETMETAL_STAIRS);
        dropSelf(ModBlocks.EXPOSED_IRON_SHEETMETAL);
        createSlabItemTable(ModBlocks.EXPOSED_IRON_SHEETMETAL_SLAB);
        dropSelf(ModBlocks.EXPOSED_IRON_SHEETMETAL_STAIRS);
        dropSelf(ModBlocks.WEATHERED_IRON_SHEETMETAL);
        createSlabItemTable(ModBlocks.WEATHERED_IRON_SHEETMETAL_SLAB);
        dropSelf(ModBlocks.WEATHERED_IRON_SHEETMETAL_STAIRS);
        dropSelf(ModBlocks.RUSTY_IRON_SHEETMETAL);
        createSlabItemTable(ModBlocks.RUSTY_IRON_SHEETMETAL_SLAB);
        dropSelf(ModBlocks.RUSTY_IRON_SHEETMETAL_STAIRS);
        dropSelf(ModBlocks.CHAIR);
        dropSelf(ModBlocks.STEEL_BLOCK);
        pulverizerMillDrops(ModBlocks.PULVERIZER_MILL);
        LootItemCondition.Builder flaxLootConditionBuilder = LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.FLAX_SEEDS).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(FlaxBlock.AGE, 7).hasProperty(FlaxBlock.HALF, DoubleBlockHalf.UPPER));
        add(ModBlocks.FLAX_SEEDS, createCropDrops(ModBlocks.FLAX_SEEDS, ModBlocks.FLAX.asItem(), ModBlocks.FLAX_SEEDS.asItem(), flaxLootConditionBuilder));
        add(ModBlocks.FLAX, (block) -> this.createSinglePropConditionTable(block, DoublePlantBlock.HALF, DoubleBlockHalf.LOWER));
        dropSelf(ModBlocks.SQUEEZER);
        dropSelf(ModBlocks.SPINNING_FRAME);
        dropSelf(ModBlocks.TIN_PLATE);
        dropSelf(ModBlocks.WIND_VANE);
        add(ModBlocks.SAWDUST, (block) -> {
            return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add((LootPoolEntryContainer.Builder) this.applyExplosionDecay(block, LootItem.lootTableItem(block).apply(IntStream.rangeClosed(1, 4).boxed().toList(), (amount) -> {
                return SetItemCountFunction.setCount(ConstantValue.exactly((float)amount)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SawDustBlock.LEVEL, amount)));
            }))));
        });
    }

    public LootTable.Builder pulverizerMillDrops(Block block) {
        return LootTable.lootTable().withPool((LootPool.Builder)this.applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(block).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(PulverizerMillBlock.HALF, Integer.valueOf(2)))))));
    }

    public LootTable.Builder tinOreDrops(Block drop) {
        HolderLookup.RegistryLookup<Enchantment> impl = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(
                drop,
                (LootPoolEntryContainer.Builder<?>)this.applyExplosionDecay(
                        drop,
                        LootItem.lootTableItem(ModItems.RAW_TIN_ORE)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                                .apply(ApplyBonusCount.addOreBonusCount(impl.getOrThrow(Enchantments.FORTUNE)))
                )
        );
    }

    public LootTable.Builder nickelOreDrops(Block drop) {
        HolderLookup.RegistryLookup<Enchantment> impl = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(
                drop,
                (LootPoolEntryContainer.Builder<?>)this.applyExplosionDecay(
                        drop,
                        LootItem.lootTableItem(ModItems.RAW_NICKEL_ORE)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 4.0F)))
                                .apply(ApplyBonusCount.addOreBonusCount(impl.getOrThrow(Enchantments.FORTUNE)))
                )
        );
    }
}
