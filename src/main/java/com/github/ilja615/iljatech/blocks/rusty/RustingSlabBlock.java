package com.github.ilja615.iljatech.blocks.rusty;

import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class RustingSlabBlock extends SlabBlock implements Rusting {
    public RustingSlabBlock(Settings settings) {
        super(settings);
    }

    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        tryRust(state, world, pos, random);
    }

    protected boolean hasRandomTicks(BlockState state) {
        return getRustyLevel(state) < 3;
    }
}
