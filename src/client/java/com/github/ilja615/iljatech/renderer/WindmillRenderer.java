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
                "  01100  ",
                " 0011000 ",
                "000110000",
                "000010111",
                "1111*1111",
                "111010000",
                "000011000",
                " 0001100 ",
                "  00110  "};
    private final static String[] Z_FRAME_1 = new String[]{
            "  00110  ",
            " 0001100 ",
            "000011000",
            "111010000",
            "1111*1111",
            "000010111",
            "000110000",
            " 0011000 ",
            "  01100  "};
    private final static String[] Z_FRAME_2 = new String[]{
            "  00001  ",
            " 1000111 ",
            "111001100",
            "011101000",
            "0000*0000",
            "000101110",
            "001100111",
            " 1110001 ",
            "  10000  "};
    private final static String[] Z_FRAME_3 = new String[]{
            "  10000  ",
            " 1110001 ",
            "001100111",
            "000101110",
            "0000*0000",
            "011101000",
            "111001100",
            " 1000111 ",
            "  00001  "};
    public WindmillRenderer(BlockEntityRendererFactory.Context ctx) {
        context = ctx;
    }

    @Override
    public void render(WindmillBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        int n = Z_FRAME_0.length;
        for (int i = 0; i < n; i++) {
            String row;
            long time = entity.getWorld().getTime() % 20;
            if (time < 5) {
                row = Z_FRAME_0[i];
            } else if (time < 10) {
                row = Z_FRAME_1[i];
            } else if (time < 15) {
                row = Z_FRAME_2[i];
            } else {
                row = Z_FRAME_3[i];
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
