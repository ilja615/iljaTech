package com.github.ilja615.iljatech;

import com.github.ilja615.iljatech.blocks.windmill.Wind;
import com.github.ilja615.iljatech.color.SpinningFrameColorProvider;
import com.github.ilja615.iljatech.init.*;
import com.github.ilja615.iljatech.network.WindRandomizerSeedS2CPayload;
import com.github.ilja615.iljatech.particles.StarParticle;
import com.github.ilja615.iljatech.particles.SteamParticle;
import com.github.ilja615.iljatech.particles.WindLeadingParticle;
import com.github.ilja615.iljatech.particles.WindParticle;
import com.github.ilja615.iljatech.renderer.*;
import com.github.ilja615.iljatech.screen.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;

public class IljaTechClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// Block render layers
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(),
				ModBlocks.DRILL, ModBlocks.ROLLER_MILL, ModBlocks.COPPER_WIRE, ModBlocks.STOKED_FIRE, ModBlocks.COKE_OVEN,
				ModBlocks.PULVERIZER_MILL, ModBlocks.FLAX_SEEDS, ModBlocks.FLAX, ModBlocks.SPINNING_FRAME,
				ModBlocks.ACACIA_FRAME, ModBlocks.BAMBOO_FRAME, ModBlocks.BIRCH_FRAME, ModBlocks.CHERRY_FRAME, ModBlocks.CRIMSON_FRAME,
				ModBlocks.DARK_OAK_FRAME, ModBlocks.JUNGLE_FRAME, ModBlocks.MANGROVE_FRAME, ModBlocks.OAK_FRAME, ModBlocks.SPRUCE_FRAME, ModBlocks.WARPED_FRAME,
				ModBlocks.SAWDUST, ModBlocks.RESEARCH_TABLE, ModBlocks.BLUEPRINT_TABLE, ModBlocks.RUBBLE);

		BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.translucent(),
				ModBlocks.WINDMILL_SAIL);

		// Block entity renderers
		BlockEntityRenderers.register(ModBlockEntityTypes.ROLLER_MILL, RollerMillRenderer::new);
		BlockEntityRenderers.register(ModBlockEntityTypes.WINDMILL, WindmillRenderer::new);
		BlockEntityRenderers.register(ModBlockEntityTypes.CONVEYOR_BELT, ConveyorBeltRenderer::new);
		BlockEntityRenderers.register(ModBlockEntityTypes.SIFTER, SifterRenderer::new);
		BlockEntityRenderers.register(ModBlockEntityTypes.RUBBLE, RubbleRenderer::new);

		// Register particle factories
		ParticleFactoryRegistry.getInstance().register(ModParticles.STAR, StarParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(ModParticles.STEAM, SteamParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(ModParticles.WIND, WindParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(ModParticles.WIND_LEADING, WindLeadingParticle.Factory::new);

		// Bind screen to Handler
		MenuScreens.register(ModScreenHandlerTypes.FOUNDRY, FoundryScreen::new);
		MenuScreens.register(ModScreenHandlerTypes.COKE_OVEN, CokeOvenScreen::new);
		MenuScreens.register(ModScreenHandlerTypes.ITEM_HATCH, ItemHatchScreen::new);
		MenuScreens.register(ModScreenHandlerTypes.SQUEEZER, SqueezerScreen::new);
		MenuScreens.register(ModScreenHandlerTypes.CARPENTRY, CarpentryScreen::new);
		MenuScreens.register(ModScreenHandlerTypes.RESEARCH, BlueprintSelectionScreen::new);

		// Register fluid render handlers
		FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_CREOSOTE_OIL, ModFluids.FLOWING_CREOSOTE_OIL, new SimpleFluidRenderHandler(
				ResourceLocation.parse("iljatech:block/oil_still"),
				ResourceLocation.parse("iljatech:block/oil_flowing"),
				0x452514
		));
		FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_SEED_OIL, ModFluids.FLOWING_SEED_OIL, new SimpleFluidRenderHandler(
				ResourceLocation.parse("iljatech:block/oil_still"),
				ResourceLocation.parse("iljatech:block/oil_flowing"),
				0xd9be77
		));

		// Fluid render layers
		BlockRenderLayerMap.INSTANCE.putFluids(RenderType.translucent(),
				ModFluids.STILL_CREOSOTE_OIL, ModFluids.FLOWING_CREOSOTE_OIL,
				ModFluids.STILL_SEED_OIL, ModFluids.FLOWING_SEED_OIL);

		// Color
		ColorProviderRegistry.BLOCK.register(new SpinningFrameColorProvider(), ModBlocks.SPINNING_FRAME);

		// Packet receiver
		ClientPlayNetworking.registerGlobalReceiver(WindRandomizerSeedS2CPayload.ID, (payload, context) -> {
			Wind.seed = payload.seed();
		});

		// Entity Renderers
		EntityRendererRegistry.register(ModEntities.SEAT, SeatEntityRenderer::new);
	}
}