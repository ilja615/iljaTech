package com.github.ilja615.iljatech.renderer;

import com.github.ilja615.iljatech.blocks.rollermill.RollerMillBlock;
import com.github.ilja615.iljatech.blocks.rollermill.RollerMillBlockEntity;
import com.github.ilja615.iljatech.blocks.sifter.SifterBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class SifterRenderer implements BlockEntityRenderer<SifterBlockEntity> {
    private final BlockEntityRendererFactory.Context context;
    private static final float SIZE = 1.998F; // bit smaller than 2.0 otherwise there is Z-fighting

    private Direction direction = null;

    public SifterRenderer(BlockEntityRendererFactory.Context ctx) {
        context = ctx;
    }

    @Override
    public void render(SifterBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        SimpleInventory inventory = entity.getInventory();
        World world = entity.getWorld();

        ItemStack stack0 = inventory.getStack(0);
        if (!stack0.isEmpty()) {
            matrices.push();
            double yProgress = Math.ceil(0.08 * (entity.getTicks()-5))*0.125;
            matrices.translate(0.5d, 1.5d - yProgress, 0.5d);
            matrices.scale(SIZE, SIZE, SIZE);
            this.context.getItemRenderer().renderItem(stack0, ModelTransformationMode.FIXED,
                    light, overlay, matrices, vertexConsumers, world, 0);
            matrices.pop();
        }
    }
}
