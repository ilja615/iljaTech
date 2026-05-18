package com.github.ilja615.iljatech.renderer;

import com.github.ilja615.iljatech.blocks.conveyorbelt.ConveyorBeltBlockEntity;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Quaternionf;

import java.util.List;
import java.util.Map;

public class ConveyorBeltRenderer implements BlockEntityRenderer<ConveyorBeltBlockEntity> {
    private final BlockEntityRendererFactory.Context context;
    private static final float SIZE = 0.75F;

    public ConveyorBeltRenderer(BlockEntityRendererFactory.Context ctx) {
        context = ctx;
    }

    @Override
    public void render(ConveyorBeltBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        List<Pair<ItemStack, Vec3d>> stacks = entity.getStacks();
        World world = entity.getWorld();

        stacks.forEach(pair -> {
            ItemStack itemStack = pair.getFirst();
            Vec3d itemPos = pair.getSecond();
            if (!itemStack.isEmpty()) {
                matrices.push();
                matrices.translate(itemPos.getX(), itemPos.getY(), itemPos.getZ());
                matrices.scale(SIZE, SIZE, SIZE);
                this.context.getItemRenderer().renderItem(itemStack, ModelTransformationMode.FIXED,
                        light, overlay, matrices, vertexConsumers, world, 0);
                matrices.pop();
            }
        });
    }
}
