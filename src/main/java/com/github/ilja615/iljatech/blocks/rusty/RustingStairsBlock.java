package com.github.ilja615.iljatech.blocks.rusty;

import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class RustingStairsBlock extends StairsBlock implements Rusting {
    public RustingStairsBlock(BlockState baseBlockState, Settings settings) {
        super(baseBlockState, settings);
    }

    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        tryRust(state, world, pos, random);
    }

    protected boolean hasRandomTicks(BlockState state) {
        return getRustyLevel(state) < 3;
    }
}
