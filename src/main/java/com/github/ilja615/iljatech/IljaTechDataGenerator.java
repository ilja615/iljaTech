package com.github.ilja615.iljatech;

import com.github.ilja615.iljatech.datagen.*;
import com.github.ilja615.iljatech.datagen.book.IljaTechBook;
import com.github.ilja615.iljatech.worldgen.ModConfiguredFeatures;
import com.github.ilja615.iljatech.worldgen.ModPlacedFeatures;
import com.github.ilja615.iljatech.worldgen.ModWorldGenerator;
import com.klikli_dev.modonomicon.api.datagen.FabricBookProvider;
import com.klikli_dev.modonomicon.api.datagen.LanguageProviderCache;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

public class IljaTechDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(ModModelProvider::new);
		pack.addProvider(ModBlockLootTableProvider::new);
		pack.addProvider(ModBlockTagProvider::new);
		pack.addProvider(ModWorldGenerator::new);
		pack.addProvider(ModRecipeProvider::new);
		pack.addProvider((FabricDataOutput output) -> new ModMultiblockProvider(output));
		pack.addProvider(ModAdvancementProvider::new);

		//We use a language cache that the book provider can write into
		var enUsCache = new LanguageProviderCache("en_us");
		pack.addProvider(FabricBookProvider.of(
				new IljaTechBook(IljaTech.MOD_ID, enUsCache)
		));

		//Important: lang provider needs to be added after the book provider, so it can read the texts added by the book provider out of the cache
		pack.addProvider((FabricDataOutput output) -> new ModEnglishTranslationProvider(output, enUsCache));

	}

	@Override
	public void buildRegistry(RegistrySetBuilder registryBuilder) {
		DataGeneratorEntrypoint.super.buildRegistry(registryBuilder);
		registryBuilder.add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootStrap);
		registryBuilder.add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootStrap);
	}
}
