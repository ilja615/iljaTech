package com.github.ilja615.iljatech;

import com.github.ilja615.iljatech.datagen.ModBlockLootTableProvider;
import com.github.ilja615.iljatech.datagen.ModBlockTagProvider;
import com.github.ilja615.iljatech.datagen.ModModelProvider;
import com.github.ilja615.iljatech.datagen.ModWorldGenerator;
import com.github.ilja615.iljatech.init.worldgen.ModConfiguredFeatures;
import com.github.ilja615.iljatech.init.worldgen.ModPlacedFeatures;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;

public class IljaTechDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(ModModelProvider::new);
		pack.addProvider(ModBlockLootTableProvider::new);
		pack.addProvider(ModBlockTagProvider::new);
		pack.addProvider(ModWorldGenerator::new);
	}

	@Override
	public void buildRegistry(RegistryBuilder registryBuilder) {
		DataGeneratorEntrypoint.super.buildRegistry(registryBuilder);
		registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, ModConfiguredFeatures::bootStrap);
		registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, ModPlacedFeatures::bootStrap);
	}
}
