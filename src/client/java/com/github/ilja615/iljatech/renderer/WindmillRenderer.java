package com.github.ilja615.iljatech.renderer;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.blocks.rollermill.RollerMillBlockEntity;
import com.github.ilja615.iljatech.blocks.windmill.WindmillBlock;
import com.github.ilja615.iljatech.blocks.windmill.WindmillBlockEntity;
import com.github.ilja615.iljatech.blocks.windmill.WindmillSailBlock;
import com.github.ilja615.iljatech.energy.MechPwrAccepter;
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
import net.minecraft.util.math.Direction;
import org.joml.Quaternionf;

import java.util.ArrayList;

public class WindmillRenderer  implements BlockEntityRenderer<WindmillBlockEntity> {
    private final BlockEntityRendererFactory.Context context;
    private final static String[] Z_FRAME_0 = new String[]{
            "      21     ",
            "      211    ",
            "      211    ",
            "      211    ",
            " 111  21     ",
            "11111 2      ",
            "222222*222222",
            "      2 11111",
            "     12  111 ",
            "    112      ",
            "    112      ",
            "    112      ",
            "     12      "};
    private final static String[] Z_FRAME_1 = new String[]{
            "             ",
            "    211      ",
            "    2111     ",
            "     211     ",
            "     21   22 ",
            "  11  2 2211 ",
            " 11112*21111 ",
            " 1122 2  11  ",
            " 22   12     ",
            "     112     ",
            "     1112    ",
            "      112    ",
            "             "};
    private final static String[] Z_FRAME_2 = new String[]{
            "             ",
            "   11        ",
            "  2111    2  ",
            "   211   211 ",
            "    21  2111 ",
            "     2 2111  ",
            "      *      ",
            "  1112 2     ",
            " 1112  12    ",
            " 112   112   ",
            "  2    1112  ",
            "        11   ",
            "             "};
    private final static String[] Z_FRAME_3 = new String[]{
            "             ",
            "       21    ",
            "       211   ",
            "  11  2111   ",
            " 1111 211    ",
            " 2211 2      ",
            "   222*222   ",
            "      2 1122 ",
            "    112 1111 ",
            "   1112  11  ",
            "   112       ",
            "    12       ",
            "             "};
    private int time = 0;

    public WindmillRenderer(BlockEntityRendererFactory.Context ctx) {
        context = ctx;
    }

    @Override
    public void render(WindmillBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        int n = Z_FRAME_0.length;
        for (int i = 0; i < n; i++) {
            String row;
            if (entity.getCachedState().get(MechPwrAccepter.ON_OFF_PWR) == MechPwrAccepter.OnOffPwr.ON)
                time = (int) entity.getWorld().getTime() % 16;
            if (time < 4) {
                row = Z_FRAME_0[i];
            } else if (time < 8) {
                row = Z_FRAME_1[i];
            } else if (time < 12) {
                row = Z_FRAME_2[i];
            } else {
                row = Z_FRAME_3[i];
            }

            Direction.Axis a = entity.getCachedState().get(WindmillBlock.FACING).getAxis();
            int m = entity.getCachedState().get(WindmillBlock.FACING).getDirection().offset();
            for (int j = 0; j < n; j++) {
                char ch = row.toCharArray()[j];
                int x = a == Direction.Axis.X ? 0 : m * (i - (n - 1) / 2);
                int y = j-(n-1)/2;
                int z = a == Direction.Axis.Z ? 0 : m * (i - (n - 1) / 2);
                if (ch == '1') {
                    matrices.push();
                    matrices.translate(x,y, z);
                    this.context.getRenderManager().renderBlock(ModBlocks.WINDMILL_SAIL.getDefaultState().with(WindmillSailBlock.FACING, entity.getCachedState().get(WindmillBlock.FACING)), entity.getPos(), entity.getWorld(), matrices, vertexConsumers.getBuffer(RenderLayer.getCutout()), false, entity.getWorld().random);
                    matrices.pop();
                }
                if (ch == '2') {
                    matrices.push();
                    matrices.translate(x,y, z);
                    this.context.getRenderManager().renderBlock(ModBlocks.WINDMILL_SAIL_REINFORCED.getDefaultState().with(WindmillSailBlock.FACING, entity.getCachedState().get(WindmillBlock.FACING)), entity.getPos(), entity.getWorld(), matrices, vertexConsumers.getBuffer(RenderLayer.getCutout()), false, entity.getWorld().random);
                    matrices.pop();
                }
            }
        }
    }
}
