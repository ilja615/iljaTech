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
            "      201    ",
            "      2001   ",
            "      2001   ",
            " 11_  200_   ",
            "1000_ 20_    ",
            "00000_2_     ",
            "222222*222222",
            "     _2_00000",
            "    _02 _0001",
            "   _002  _11 ",
            "   1002      ",
            "   1002      ",
            "    102      "};
    private final static String[] Z_FRAME_1 = new String[]{
            "     11      ",
            "    2001     ",
            "    2000_    ",
            "     200_    ",
            "  __ 20_  22 ",
            " 100__2_22001",
            "100002*200001",
            "10022_2__001 ",
            " 22  _02 __  ",
            "    _002     ",
            "    _0002    ",
            "     1002    ",
            "      11     "};
    private final static String[] Z_FRAME_2 = new String[]{
            "    1        ",
            "   001       ",
            "  2000_   2  ",
            "   200_  200 ",
            "    20_ 20001",
            "     2_20001 ",
            "  ____*____  ",
            " 10002_2     ",
            "10002 _02    ",
            " 002  _002   ",
            "  2   _0002  ",
            "       100   ",
            "        1    "};
    private final static String[] Z_FRAME_3 = new String[]{
            "             ",
            "  11    201  ",
            " 100_   2001 ",
            " 0000_ 20001 ",
            " 2200_ 200_  ",
            "   222_2__   ",
            "     _*_     ",
            "   __2_222   ",
            "  _002 _0022 ",
            " 10002 _0000 ",
            " 1002   _001 ",
            "  102    11  ",
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
            time = entity.getTicks();
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
            boolean on = entity.getCachedState().get(MechPwrAccepter.ON_OFF_PWR) == MechPwrAccepter.OnOffPwr.ON;
            for (int j = 0; j < n; j++) {
                char ch = row.toCharArray()[j];
                int x = a == Direction.Axis.X ? 0 : m * (i - (n - 1) / 2);
                int y = j-(n-1)/2;
                int z = a == Direction.Axis.Z ? 0 : m * (i - (n - 1) / 2);
                if (ch == '0' || ch == '2') {
                    matrices.push();
                    matrices.translate(x,y, z);
                    this.context.getRenderManager().renderBlock(ModBlocks.WINDMILL_SAIL.getDefaultState().with(WindmillSailBlock.FACING, entity.getCachedState().get(WindmillBlock.FACING)), entity.getPos(), entity.getWorld(), matrices, vertexConsumers.getBuffer(RenderLayer.getCutout()), false, entity.getWorld().random);
                    matrices.pop();
                }
                if (on && ch == '1') {
                    matrices.push();
                    matrices.translate(x,y, z);
                    this.context.getRenderManager().renderBlock(ModBlocks.WINDMILL_SAIL.getDefaultState().with(WindmillSailBlock.VARIANT, 1).with(WindmillSailBlock.FACING, entity.getCachedState().get(WindmillBlock.FACING)), entity.getPos(), entity.getWorld(), matrices, vertexConsumers.getBuffer(RenderLayer.getTranslucent()), false, entity.getWorld().random);
                    matrices.pop();
                }
            }
        }
    }
}
