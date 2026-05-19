package com.github.ilja615.iljatech.renderer;

import com.github.ilja615.iljatech.entities.SeatEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class SeatEntityRenderer extends EntityRenderer<SeatEntity>
{
    public SeatEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public ResourceLocation getTextureLocation(SeatEntity entity) {
        return null;
    }
}