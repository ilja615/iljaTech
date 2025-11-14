package com.github.ilja615.iljatech;

import com.github.ilja615.iljatech.blocks.windmill.Wind;
import com.github.ilja615.iljatech.init.*;
import com.github.ilja615.iljatech.worldgen.BiomeModificationEvent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.Heightmap;
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

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> {
			entries.addAfter(Items.PUMPKIN_PIE ,ModItems.BOILED_EGG);
		});

		ModBlockEntityTypes.registerStorages();

		ServerTickEvents.START_WORLD_TICK.register(world -> {
			if (world.random.nextFloat() < 1.0f) {
				world.getPlayers().forEach(player -> {
					BlockPos origin = player.getBlockPos().add(world.random.nextInt(33) - 16, 0, + world.random.nextInt(33) - 16);
					int x = origin.getX();
					int z = origin.getZ();
					Vec2f wind = Wind.getWindDirectionUnitVectorAt(world, x >> 4, z >> 4);
					world.spawnParticles(ModParticles.WIND, x + world.random.nextDouble() - 0.5d, world.getTopY(Heightmap.Type.WORLD_SURFACE_WG, x, z) + world.random.nextDouble() * 2.0d + 1.0d, z + world.random.nextDouble() - 0.5d,
							0, wind.x, 0.0d, wind.y, 0.5d);
				});
			}
		});
	}
}