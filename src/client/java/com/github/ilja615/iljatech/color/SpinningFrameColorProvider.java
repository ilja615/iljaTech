package com.github.ilja615.iljatech.color;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SpinningFrameColorProvider implements BlockColor {

        //return (int) (0xffffff * noise.sample(pos.getX(), pos.getY(), pos.getZ()));


    @Override
    public int getColor(BlockState blockState, @Nullable BlockAndTintGetter blockAndTintGetter, @Nullable BlockPos blockPos, int i) {
        return 0x6b634d;
    }
}
