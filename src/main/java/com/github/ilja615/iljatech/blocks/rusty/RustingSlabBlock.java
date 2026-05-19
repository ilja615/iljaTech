package com.github.ilja615.iljatech.blocks.rusty;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;

public class RustingSlabBlock extends SlabBlock implements Rusting {
    public RustingSlabBlock(Properties settings) {
        super(settings);
    }

    protected void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        tryRust(state, world, pos, random);
    }

    protected boolean isRandomlyTicking(BlockState state) {
        return getRustyLevel(state) < 3;
    }
}
