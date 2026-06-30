package com.github.ilja615.iljatech.datagen;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.init.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

// This is for loottables that are not block drop loot tables
public class ModSimpleLootTableProvider extends SimpleFabricLootTableProvider {
    public ModSimpleLootTableProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup, LootContextParamSets.EMPTY);
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer) {
        LootPool.Builder mainPool = LootPool.lootPool();
        mainPool
                .setRolls(ConstantValue.exactly(4))
                .add(LootItem.lootTableItem(ModItems.SALTPETER).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                .add(LootItem.lootTableItem(ModItems.SILICA_DUST));

        LootTable.Builder builder = LootTable.lootTable();
        builder.withPool(mainPool);
        biConsumer.accept(createResourceKey("sifter/sand_fines"), builder);
    }


    static ResourceKey<LootTable> createResourceKey(String path) {
        return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, path));
    }
}
