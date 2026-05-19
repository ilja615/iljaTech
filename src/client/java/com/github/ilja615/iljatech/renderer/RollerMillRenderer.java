package com.github.ilja615.iljatech.renderer;

import com.github.ilja615.iljatech.blocks.rollermill.RollerMillBlock;
import com.github.ilja615.iljatech.blocks.rollermill.RollerMillBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RollerMillRenderer implements BlockEntityRenderer<RollerMillBlockEntity> {
    private final BlockEntityRendererProvider.Context context;
    private static final float SIZE = 0.75F;

    private Direction direction = null;

    public RollerMillRenderer(BlockEntityRendererProvider.Context ctx) {
        context = ctx;
    }

    @Override
    public void render(RollerMillBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        SimpleContainer inventory = entity.getInventory();
        Level world = entity.getLevel();

        ItemStack stack0 = inventory.getItem(0);
        if (!stack0.isEmpty()) {
            matrices.pushPose();
            direction = entity.getBlockState().getValue(RollerMillBlock.FACING);
            matrices.translate(0.5d, 0.5d, 0.5d);
            matrices.translate(direction.getStepX() * 0.01 * (entity.getTicks() - 50), 0d, direction.getStepZ() * 0.01 * (entity.getTicks() - 50));
            matrices.scale(SIZE, SIZE, SIZE);
            this.context.getItemRenderer().renderStatic(stack0, ItemDisplayContext.FIXED,
                    light, overlay, matrices, vertexConsumers, world, 0);
            matrices.popPose();
        }
    }
}
