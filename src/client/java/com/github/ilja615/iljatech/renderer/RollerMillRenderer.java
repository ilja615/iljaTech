package com.github.ilja615.iljatech.renderer;

import com.github.ilja615.iljatech.blocks.rollermill.RollerMillBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;

public class RollerMillRenderer implements BlockEntityRenderer<RollerMillBlockEntity> {
    private final BlockEntityRendererFactory.Context context;
    private static final float SIZE = 0.75F;

    public RollerMillRenderer(BlockEntityRendererFactory.Context ctx) {
        context = ctx;
    }

    @Override
    public void render(RollerMillBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        SimpleInventory inventory = entity.getInventory();
        World world = entity.getWorld();

        ItemStack stack0 = inventory.getStack(0);
        if (!stack0.isEmpty()) {
            matrices.push();
            matrices.translate(0.5d, 1.1d - (0.004d * entity.getTicks()), 0.5d);
            matrices.scale(SIZE, SIZE, SIZE);
            this.context.getItemRenderer().renderItem(stack0, ModelTransformationMode.FIXED,
                    light, overlay, matrices, vertexConsumers, world, 0);
            matrices.pop();
        }
        ItemStack stack1 = inventory.getStack(1);
        if (!stack1.isEmpty()) {
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_X.rotation(90.0f));
            matrices.translate(0.5d, 0.505f, 0.5d);
            matrices.scale(SIZE, SIZE, SIZE);
            this.context.getItemRenderer().renderItem(stack1, ModelTransformationMode.FIXED,
                    light, overlay, matrices, vertexConsumers, world, 0);
            matrices.pop();
        }
    }
}
