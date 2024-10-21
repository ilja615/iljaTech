package com.github.ilja615.iljatech;

import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.renderer.RollerMillRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class IljaTechClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// Block render layers
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
				ModBlocks.DRILL, ModBlocks.ROLLER_MILL, ModBlocks.COPPER_WIRE);

		// Block entity renderers
		BlockEntityRendererFactories.register(ModBlockEntityTypes.ROLLER_MILL, RollerMillRenderer::new);
	}
}