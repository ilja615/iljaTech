package com.github.ilja615.iljatech;

import com.github.ilja615.iljatech.blocks.windmill.Wind;
import com.github.ilja615.iljatech.init.*;
import com.github.ilja615.iljatech.network.WindRandomizerSeedS2CPayload;
import com.github.ilja615.iljatech.worldgen.BiomeModificationEvent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.Heightmap;
import org.joml.Vector2f;
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
		ModRecipeTypes.registerIngredientTypes();
		ModFluids.load();
		ModNetworking.load();
		ModEntities.load();
		ModDataAttachments.load();
		ModCriteria.load();
		ModFeatures.load();

		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(entries -> {
			entries.addAfter(Items.PUMPKIN_PIE ,ModItems.BOILED_EGG);
		});

		ModBlockEntityTypes.registerStorages();

		// TODO: move the wind code elsewhere
		ServerTickEvents.START_WORLD_TICK.register(world -> {
			if (world.random.nextFloat() < 0.005f) {
				world.players().forEach(player -> {
					BlockPos origin = player.blockPosition().offset(world.random.nextInt(65) - 32, 0, + world.random.nextInt(65) - 32);
					int x = origin.getX();
					int z = origin.getZ();
					Vector2f wind = Wind.getWindDirectionUnitVectorAt(world, x >> 4, z >> 4);
					world.sendParticles(ModParticles.WIND_LEADING, x + world.random.nextDouble() - 0.5d, world.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) + world.random.nextDouble() * 2.0d + 3.0d, z + world.random.nextDouble() - 0.5d,
							0, wind.x, 0.0d, wind.y, 0.5d);
				});
			}
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			long seed = server.overworld().getSeed();
			handler.send(new ClientboundCustomPayloadPacket(new WindRandomizerSeedS2CPayload(seed)));
		});
	}
}