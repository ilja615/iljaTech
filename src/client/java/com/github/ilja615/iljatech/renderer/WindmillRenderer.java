package com.github.ilja615.iljatech.renderer;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.blocks.rollermill.RollerMillBlockEntity;
import com.github.ilja615.iljatech.blocks.windmill.WindmillBlockEntity;
import com.github.ilja615.iljatech.init.ModBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class WindmillRenderer  implements BlockEntityRenderer<WindmillBlockEntity> {
    private final BlockEntityRendererFactory.Context context;
    private final static String[] Z_FRAME_0 = new String[]{
                "0001000",
                "0001000",
                "0001000",
                "111*111",
                "0001000",
                "0001000",
                "0001000"};
    private final static String[] Z_FRAME_1 = new String[]{
            "1000001",
            "0100010",
            "0010100",
            "000*000",
            "0010100",
            "0100010",
            "1000001"};
    public WindmillRenderer(BlockEntityRendererFactory.Context ctx) {
        context = ctx;
    }

    @Override
    public void render(WindmillBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        int n = Z_FRAME_0.length;
        for (int i = 0; i < n; i++) {
            String row;
            if (entity.getTicks() < 40) {
                row = Z_FRAME_0[i];
            } else {
                row = Z_FRAME_1[i];
            }

            for (int j = 0; j < n; j++) {
                char ch = row.toCharArray()[j];
                if (ch == '1') {
                    matrices.push();
                    matrices.translate(i-(n-1)/2,j-(n-1)/2,0);
                    this.context.getRenderManager().renderBlock(ModBlocks.WINDMILL_BLADE.getDefaultState(), entity.getPos(), entity.getWorld(), matrices, vertexConsumers.getBuffer(RenderLayer.getCutout()), false, entity.getWorld().random);
                    matrices.pop();
                }
            }
        }
    }
}
