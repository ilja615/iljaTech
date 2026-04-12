package com.github.ilja615.iljatech.blocks.rusty;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

public class RustingStairsBlock extends StairBlock implements Rusting {
    public RustingStairsBlock(BlockState baseBlockState, Properties settings) {
        super(baseBlockState, settings);
    }

    protected void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        tryRust(state, world, pos, random);
    }

    protected boolean isRandomlyTicking(BlockState state) {
        return getRustyLevel(state) < 3;
    }
}
