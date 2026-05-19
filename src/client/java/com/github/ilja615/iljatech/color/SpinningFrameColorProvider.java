package com.github.ilja615.iljatech.color;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import org.jetbrains.annotations.Nullable;

public class SpinningFrameColorProvider implements BlockColor {
    private final static ImprovedNoise noise = new ImprovedNoise(RandomSource.create(832));

    @Override
    public int getColor(BlockState state, @Nullable BlockAndTintGetter world, @Nullable BlockPos pos, int tintIndex) {
        //return 0x6b634d;
        return (int) (0xffffff * noise.noise(pos.getX(), pos.getY(), pos.getZ()));
    }
}
