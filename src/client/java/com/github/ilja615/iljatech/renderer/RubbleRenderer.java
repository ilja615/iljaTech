package com.github.ilja615.iljatech.renderer;

import com.github.ilja615.iljatech.blocks.sifter.RubbleBlockEntity;
import com.github.ilja615.iljatech.blocks.sifter.SifterBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Rotations;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class RubbleRenderer  implements BlockEntityRenderer<RubbleBlockEntity> {

    private final BlockEntityRendererProvider.Context context;

    public RubbleRenderer(BlockEntityRendererProvider.Context ctx) {
        context = ctx;
    }

    @Override
    public void render(RubbleBlockEntity blockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
        SimpleContainer inventory = blockEntity.getInventory();
        Level world = blockEntity.getLevel();

        inventory.items.forEach(itemStack -> {
            if (!itemStack.isEmpty()) {
                poseStack.pushPose();
                poseStack.translate(0.5d, 0.128d, 0.5d);
                poseStack.mulPose(Axis.XP.rotationDegrees(90));
                poseStack.mulPose(Axis.ZP.rotationDegrees(180));
                poseStack.scale(1f, 1f, 1f);
                this.context.getItemRenderer().renderStatic(itemStack, ItemDisplayContext.FIXED,
                        i, j, poseStack, multiBufferSource, world, 0);
                poseStack.popPose();
            }
        });
    }
}
