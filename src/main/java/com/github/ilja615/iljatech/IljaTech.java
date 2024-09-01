package com.github.ilja615.iljatech;

import com.github.ilja615.iljatech.init.ModItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IljaTech implements ModInitializer {
	public static final String MOD_ID = "iljatech";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initialization!");

		ModItems.load();
	}
}