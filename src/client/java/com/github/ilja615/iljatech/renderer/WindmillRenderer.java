package com.github.ilja615.iljatech.renderer;

import com.github.ilja615.iljatech.blocks.windmill.WindmillBlock;
import com.github.ilja615.iljatech.blocks.windmill.WindmillBlockEntity;
import com.github.ilja615.iljatech.blocks.windmill.WindmillSailBlock;
import com.github.ilja615.iljatech.energy.MechPwrAccepter;
import com.github.ilja615.iljatech.init.ModBlocks;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

public class WindmillRenderer  implements BlockEntityRenderer<WindmillBlockEntity> {
    private final BlockEntityRendererProvider.Context context;
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

    public WindmillRenderer(BlockEntityRendererProvider.Context ctx) {
        context = ctx;
    }

    @Override
    public void render(WindmillBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
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

            Direction.Axis a = entity.getBlockState().getValue(WindmillBlock.FACING).getAxis();
            int m = entity.getBlockState().getValue(WindmillBlock.FACING).getAxisDirection().getStep();
            boolean on = entity.getBlockState().getValue(MechPwrAccepter.ON_OFF_PWR) == MechPwrAccepter.OnOffPwr.ON;
            for (int j = 0; j < n; j++) {
                char ch = row.toCharArray()[j];
                int x = a == Direction.Axis.X ? 0 : m * (i - (n - 1) / 2);
                int y = j-(n-1)/2;
                int z = a == Direction.Axis.Z ? 0 : m * (i - (n - 1) / 2);
                if (ch == '0' || ch == '2') {
                    matrices.pushPose();
                    matrices.translate(x,y, z);
                    this.context.getBlockRenderDispatcher().renderBatched(ModBlocks.WINDMILL_SAIL.defaultBlockState().setValue(WindmillSailBlock.FACING, entity.getBlockState().getValue(WindmillBlock.FACING)), entity.getBlockPos(), entity.getLevel(), matrices, vertexConsumers.getBuffer(RenderType.cutout()), false, entity.getLevel().random);
                    matrices.popPose();
                }
                if (on && ch == '1') {
                    matrices.pushPose();
                    matrices.translate(x,y, z);
                    this.context.getBlockRenderDispatcher().renderBatched(ModBlocks.WINDMILL_SAIL.defaultBlockState().setValue(WindmillSailBlock.VARIANT, 1).setValue(WindmillSailBlock.FACING, entity.getBlockState().getValue(WindmillBlock.FACING)), entity.getBlockPos(), entity.getLevel(), matrices, vertexConsumers.getBuffer(RenderType.translucent()), false, entity.getLevel().random);
                    matrices.popPose();
                }
            }
        }
    }
}
