package com.github.ilja615.iljatech.renderer;

import com.github.ilja615.iljatech.blocks.rollermill.RollerMillBlock;
import com.github.ilja615.iljatech.blocks.rollermill.RollerMillBlockEntity;
import com.github.ilja615.iljatech.blocks.sifter.SifterBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SifterRenderer implements BlockEntityRenderer<SifterBlockEntity> {
    private final BlockEntityRendererProvider.Context context;
    private static final float SIZE = 1.998F; // bit smaller than 2.0 otherwise there is Z-fighting

    private Direction direction = null;

    public SifterRenderer(BlockEntityRendererProvider.Context ctx) {
        context = ctx;
    }

    @Override
    public void render(SifterBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        SimpleContainer inventory = entity.getInventory();
        Level world = entity.getLevel();

        ItemStack stack0 = inventory.getItem(0);
        if (!stack0.isEmpty()) {
            matrices.pushPose();
            double yProgress = Math.ceil(0.08 * (entity.getTicks()-5))*0.125;
            matrices.translate(0.5d, 1.5d - yProgress, 0.5d);
            matrices.scale(SIZE, SIZE, SIZE);
            this.context.getItemRenderer().renderStatic(stack0, ItemDisplayContext.FIXED,
                    light, overlay, matrices, vertexConsumers, world, 0);
            matrices.popPose();
        }
    }
}
