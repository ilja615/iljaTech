package com.github.ilja615.iljatech.renderer;

import com.github.ilja615.iljatech.blocks.conveyorbelt.ConveyorBeltBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import org.joml.Quaternionf;

import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ConveyorBeltRenderer implements BlockEntityRenderer<ConveyorBeltBlockEntity> {
    private final BlockEntityRendererProvider.Context context;
    private static final float SIZE = 0.75F;

    public ConveyorBeltRenderer(BlockEntityRendererProvider.Context ctx) {
        context = ctx;
    }

    @Override
    public void render(ConveyorBeltBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        List<Pair<ItemStack, Vec3>> stacks = entity.getStacks();
        Level world = entity.getLevel();

        stacks.forEach(pair -> {
            ItemStack itemStack = pair.getFirst();
            Vec3 itemPos = pair.getSecond();
            if (!itemStack.isEmpty()) {
                matrices.pushPose();
                matrices.translate(itemPos.x(), itemPos.y(), itemPos.z());
                matrices.scale(SIZE, SIZE, SIZE);
                this.context.getItemRenderer().renderStatic(itemStack, ItemDisplayContext.FIXED,
                        light, overlay, matrices, vertexConsumers, world, 0);
                matrices.popPose();
            }
        });
    }
}
