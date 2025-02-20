package com.github.ilja615.iljatech;

import com.github.ilja615.iljatech.blocks.rollermill.RollerMillBlockEntity;
import com.github.ilja615.iljatech.init.*;
import com.github.ilja615.iljatech.worldgen.BiomeModificationEvent;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IljaTech implements ModInitializer {
	public static final String MOD_ID = "iljatech";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initialization!");

		ModItems.load();
		ModBlocks.load();
		ModItemGroup.load();
		BiomeModificationEvent.load();
		ModBlockEntityTypes.load();
		ModEffects.load();
		ModSounds.load();
		ModScreenHandlerTypes.load();
		ModRecipeTypes.load();

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> {
			entries.addAfter(Items.PUMPKIN_PIE ,ModItems.BOILED_EGG);
		});

		ModBlockEntityTypes.registerStorages();
	}
}