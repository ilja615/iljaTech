package com.github.ilja615.iljatech;

import com.github.ilja615.iljatech.data.provider.ModBlockLootTableProvider;
import com.github.ilja615.iljatech.data.provider.ModBlockTagProvider;
import com.github.ilja615.iljatech.data.provider.ModModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class IljaTechDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(ModModelProvider::new);
		pack.addProvider(ModBlockLootTableProvider::new);
		pack.addProvider(ModBlockTagProvider::new);
	}
}
