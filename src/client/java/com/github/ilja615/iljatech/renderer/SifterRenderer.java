package com.github.ilja615.iljatech.renderer;

import com.github.ilja615.iljatech.blocks.sifter.SifterBlockEntity;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Map;

public class SifterRenderer implements BlockEntityRenderer<SifterBlockEntity> {
    private final BlockEntityRendererProvider.Context context;

    public SifterRenderer(BlockEntityRendererProvider.Context ctx) {
        context = ctx;
    }

    @Override
    public void render(SifterBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource multiBufferSource, int light, int overlay) {
        SimpleContainer inventory = entity.getInventory();
        Level world = entity.getLevel();
        if (world == null) {
            return;
        }

        if (inventory.isEmpty()) {

        }
        BlockPos blockPos = entity.getBlockPos();
        boolean bl2 = Minecraft.useAmbientOcclusion();
//        int i = world.getBrightness(LightLayer.SKY, blockPos);
//        int j = world.getBrightness(LightLayer.BLOCK, blockPos);
//        int newLight = i << 20 | j << 4;
        ItemStack stack0 = inventory.getItem(0);

        if (stack0.getItem() instanceof BlockItem blockItem) {
            matrices.pushPose();
            RenderType layer = RenderType.solid();
            VertexConsumer vertexConsumer = multiBufferSource.getBuffer(layer);
            PoseStack.Pose entry = matrices.last();
            TextureAtlasSprite sprite = this.context.getBlockRenderDispatcher().getBlockModel(blockItem.getBlock().defaultBlockState()).getParticleIcon();

            float steps = 4.0f;
            float height = 1.0f - ((float )Math.floor(entity.getTicks()/100.0f * steps) / steps);

            float minU = sprite.getU(0.0f);
            float maxU = sprite.getU(1.0f);
            float minV = sprite.getV(0.0f);
            float midV = sprite.getV(height);
            float maxV = sprite.getV(1.0f);

            int newLight = 15728880;

            // front face
            newLight = LevelRenderer.getLightColor(world, entity.getBlockState(), entity.getBlockPos().south());
            float f = world.getShade(Direction.SOUTH, true);
            drawQuad(vertexConsumer, entry, 0f, 1.0f, 0.001f, 1f, 1.0f + height, 0.001f, minU, minV, maxU, midV, newLight, overlay, f, 0, 0, 1);

            // back face
            newLight = LevelRenderer.getLightColor(world, entity.getBlockState(), entity.getBlockPos().north());
            f = world.getShade(Direction.NORTH, true);
            drawQuad(vertexConsumer, entry, 1f, 1.0f, 0.999f, 0f, 1.0f + height, 0.999f,  minU, minV, maxU, midV, newLight, overlay, f, 0, 0, -1);

            // left face
            newLight = LevelRenderer.getLightColor(world, entity.getBlockState(), entity.getBlockPos().west());
            f = world.getShade(Direction.WEST, true);
            drawQuad(vertexConsumer, entry, 0.001f, 1.0f, 1f, 0.001f, 1.0f + height, 0f,  minU, minV, maxU, midV, newLight, overlay, f, -1, 0, 0);

            // right face
            newLight = LevelRenderer.getLightColor(world, entity.getBlockState(), entity.getBlockPos().east());
            f = world.getShade(Direction.EAST, true);
            drawQuad(vertexConsumer, entry, 0.999f, 1.0f, 0f, 0.999f, 1.0f + height, 1f,  minU, minV, maxU, midV, newLight, overlay, f, 1, 0, 0);

            // top face
            newLight = LevelRenderer.getLightColor(world, entity.getBlockState(), entity.getBlockPos().above());
            f = world.getShade(Direction.UP, true);
            drawQuad(vertexConsumer, entry, 0f, 1.0f + height, 0f, 0f, 1.0f + height, 1f, 1f, 1.0f + height, 1f, 1f, 1.0f + height, 0f,  minU, minV, maxU, maxV, newLight, overlay, f, 0, 1, 0);

            matrices.popPose();
        }
    }
        private static void drawQuad(VertexConsumer vertexConsumer, PoseStack.Pose entry, float x1, float y1, float z1, float x2, float y2, float z2, float minU, float minV, float maxU, float maxV, int light, int overlay, float shade, int nx, int ny, int nz) {
            drawQuad(vertexConsumer, entry, x1, y1, z1, x1, y2, z1, x2, y2, z2, x2, y1, z2, minU, minV, maxU, maxV, light, overlay, shade, nx, ny, nz);
        }
    private static void drawQuad(VertexConsumer vertexConsumer, PoseStack.Pose entry,
                                 float x1, float y1, float z1,
                                 float x2, float y2, float z2,
                                 float x3, float y3, float z3,
                                 float x4, float y4, float z4,
                                 float minU, float minV, float maxU, float maxV, int light, int overlay, float shade,
                                 float nx, float ny, float nz) {

        try (MemoryStack memoryStack = MemoryStack.stackPush()) {
            ByteBuffer byteBuffer = memoryStack.malloc(DefaultVertexFormat.BLOCK.getVertexSize());
            IntBuffer intBuffer = byteBuffer.asIntBuffer();

            float r = byteBuffer.get(12) & 255;
            float s = byteBuffer.get(13) & 255;
            float t = byteBuffer.get(14) & 255;
            float u = r * shade;
            float v = s * shade;
            float w = t * shade;

            int x = FastColor.ARGB32.color(255, (int) u, (int) v, (int) w);

            vertexConsumer.addVertex(entry, x1, y1, z1)
                    .setColor(x)
                    .setUv(minU, minV)
                    .setLight(light)
                    .setOverlay(overlay)
                    .setNormal(nx, ny, nz);

            vertexConsumer.addVertex(entry, x2, y2, z2)
                    .setColor(x)
                    .setUv(minU, maxV)
                    .setLight(light)
                    .setOverlay(overlay)
                    .setNormal(nx, ny, nz);

            vertexConsumer.addVertex(entry, x3, y3, z3)
                    .setColor(x)
                    .setUv(maxU, maxV)
                    .setLight(light)
                    .setOverlay(overlay)
                    .setNormal(nx, ny, nz);

            vertexConsumer.addVertex(entry, x4, y4, z4)
                    .setColor(x)
                    .setUv(maxU, minV)
                    .setLight(light)
                    .setOverlay(overlay)
                    .setNormal(nx, ny, nz);
        }
    }
}
