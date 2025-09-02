package com.github.ilja615.iljatech.blocks.rusty;

import com.github.ilja615.iljatech.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class RustingBlock extends Block implements Rusting {

    public RustingBlock(Settings settings) {
        super(settings);
    }

    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        tryRust(state, world, pos, random);
    }

    protected boolean hasRandomTicks(BlockState state) {
        return getRustyLevel(state) < 3;
    }
}
