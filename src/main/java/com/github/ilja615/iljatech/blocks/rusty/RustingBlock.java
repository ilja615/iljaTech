package com.github.ilja615.iljatech.blocks.rusty;

import com.github.ilja615.iljatech.init.ModBlocks;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class RustingBlock extends Block implements Rusting {

    public RustingBlock(Properties settings) {
        super(settings);
    }

    protected void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        tryRust(state, world, pos, random);
    }

    protected boolean isRandomlyTicking(BlockState state) {
        return getRustyLevel(state) < 3;
    }
}
