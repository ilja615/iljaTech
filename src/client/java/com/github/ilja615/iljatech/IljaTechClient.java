package com.github.ilja615.iljatech;

import com.github.ilja615.iljatech.blocks.windmill.Wind;
import com.github.ilja615.iljatech.color.SpinningFrameColorProvider;
import com.github.ilja615.iljatech.init.*;
import com.github.ilja615.iljatech.network.WindRandomizerSeedS2CPayload;
import com.github.ilja615.iljatech.particles.StarParticle;
import com.github.ilja615.iljatech.particles.SteamParticle;
import com.github.ilja615.iljatech.particles.WindLeadingParticle;
import com.github.ilja615.iljatech.particles.WindParticle;
import com.github.ilja615.iljatech.renderer.ConveyorBeltRenderer;
import com.github.ilja615.iljatech.renderer.RollerMillRenderer;
import com.github.ilja615.iljatech.renderer.WindmillRenderer;
import com.github.ilja615.iljatech.screen.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.util.Identifier;

public class IljaTechClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// Block render layers
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
				ModBlocks.DRILL, ModBlocks.ROLLER_MILL, ModBlocks.COPPER_WIRE, ModBlocks.STOKED_FIRE, ModBlocks.COKE_OVEN,
				ModBlocks.WOODEN_SCAFFOLDING, ModBlocks.PULVERIZER_MILL, ModBlocks.FLAX_SEEDS, ModBlocks.FLAX, ModBlocks.SPINNING_FRAME,
				ModBlocks.ACACIA_FRAME, ModBlocks.BAMBOO_FRAME, ModBlocks.BIRCH_FRAME, ModBlocks.CHERRY_FRAME, ModBlocks.CRIMSON_FRAME,
				ModBlocks.DARK_OAK_FRAME, ModBlocks.JUNGLE_FRAME, ModBlocks.MANGROVE_FRAME, ModBlocks.OAK_FRAME, ModBlocks.SPRUCE_FRAME, ModBlocks.WARPED_FRAME,
				ModBlocks.SAWDUST);

		// Block entity renderers
		BlockEntityRendererFactories.register(ModBlockEntityTypes.ROLLER_MILL, RollerMillRenderer::new);
		BlockEntityRendererFactories.register(ModBlockEntityTypes.WINDMILL, WindmillRenderer::new);
		BlockEntityRendererFactories.register(ModBlockEntityTypes.CONVEYOR_BELT, ConveyorBeltRenderer::new);

		// Register particle factories
		ParticleFactoryRegistry.getInstance().register(ModParticles.STAR, StarParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(ModParticles.STEAM, SteamParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(ModParticles.WIND, WindParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(ModParticles.WIND_LEADING, WindLeadingParticle.Factory::new);

		// Bind screen to Handler
		HandledScreens.register(ModScreenHandlerTypes.FOUNDRY, FoundryScreen::new);
		HandledScreens.register(ModScreenHandlerTypes.COKE_OVEN, CokeOvenScreen::new);
		HandledScreens.register(ModScreenHandlerTypes.ITEM_HATCH, ItemHatchScreen::new);
		HandledScreens.register(ModScreenHandlerTypes.SQUEEZER, SqueezerScreen::new);
		HandledScreens.register(ModScreenHandlerTypes.CARPENTRY, CarpentryScreen::new);

		// Register fluid render handlers
		FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_CREOSOTE_OIL, ModFluids.FLOWING_CREOSOTE_OIL, new SimpleFluidRenderHandler(
				Identifier.of("iljatech:block/oil_still"),
				Identifier.of("iljatech:block/oil_flowing"),
				0x452514
		));
		FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_SEED_OIL, ModFluids.FLOWING_SEED_OIL, new SimpleFluidRenderHandler(
				Identifier.of("iljatech:block/oil_still"),
				Identifier.of("iljatech:block/oil_flowing"),
				0xd9be77
		));

		// Fluid render layers
		BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),
				ModFluids.STILL_CREOSOTE_OIL, ModFluids.FLOWING_CREOSOTE_OIL,
				ModFluids.STILL_SEED_OIL, ModFluids.FLOWING_SEED_OIL);

		// Color
		ColorProviderRegistry.BLOCK.register(new SpinningFrameColorProvider(), ModBlocks.SPINNING_FRAME);

		// Packet receiver
		ClientPlayNetworking.registerGlobalReceiver(WindRandomizerSeedS2CPayload.ID, (payload, context) -> {
			Wind.seed = payload.seed();
		});
	}
}