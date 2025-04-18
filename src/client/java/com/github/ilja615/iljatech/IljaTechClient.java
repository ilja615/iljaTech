package com.github.ilja615.iljatech;

import com.github.ilja615.iljatech.init.*;
import com.github.ilja615.iljatech.particles.StarParticle;
import com.github.ilja615.iljatech.particles.SteamParticle;
import com.github.ilja615.iljatech.renderer.RollerMillRenderer;
import com.github.ilja615.iljatech.screen.CokeOvenScreen;
import com.github.ilja615.iljatech.screen.FoundryScreen;
import com.github.ilja615.iljatech.screen.ItemHatchScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

public class IljaTechClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// Block render layers
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
				ModBlocks.DRILL, ModBlocks.ROLLER_MILL, ModBlocks.COPPER_WIRE, ModBlocks.STOKED_FIRE, ModBlocks.COKE_OVEN);

		// Block entity renderers
		BlockEntityRendererFactories.register(ModBlockEntityTypes.ROLLER_MILL, RollerMillRenderer::new);

		// Register particle factories
		ParticleFactoryRegistry.getInstance().register(ModParticles.STAR, StarParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(ModParticles.STEAM, SteamParticle.Factory::new);

		// Bind screen to Handler
		HandledScreens.register(ModScreenHandlerTypes.FOUNDRY, FoundryScreen::new);
		HandledScreens.register(ModScreenHandlerTypes.COKE_OVEN, CokeOvenScreen::new);
		HandledScreens.register(ModScreenHandlerTypes.ITEM_HATCH, ItemHatchScreen::new);

		// Register fluid render handlers
		FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_CREOSOTE_OIL, ModFluids.FLOWING_CREOSOTE_OIL, new SimpleFluidRenderHandler(
				Identifier.of("iljatech:block/oil_still"),
				Identifier.of("iljatech:block/oil_flowing"),
				0x452514
		));

		// Fluid render layers
		BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),
				ModFluids.STILL_CREOSOTE_OIL, ModFluids.FLOWING_CREOSOTE_OIL);
	}
}