package com.github.ilja615.iljatech.color;

import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

public class SpinningFrameColorProvider implements BlockColorProvider {
    private final static PerlinNoiseSampler noise = new PerlinNoiseSampler(Random.create(832));

    @Override
    public int getColor(BlockState state, @Nullable BlockRenderView world, @Nullable BlockPos pos, int tintIndex) {
        //return 0x6b634d;
        return (int) (0xffffff * noise.sample(pos.getX(), pos.getY(), pos.getZ()));
    }
}
