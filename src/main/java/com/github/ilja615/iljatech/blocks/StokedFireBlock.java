package com.github.ilja615.iljatech.blocks;

import com.github.ilja615.iljatech.energy.Heat;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class StokedFireBlock extends AbstractFireBlock {
    public static final IntProperty STOKED = IntProperty.of("stoked", 0, 3);

    public StokedFireBlock(Settings settings) {
        super(settings, 2);
        this.setDefaultState(this.getDefaultState().with(STOKED, 3));
    }

    @Override
    protected MapCodec<? extends AbstractFireBlock> getCodec() {
        return null;
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        Heat.emitHeat(world, pos.up());
        world.scheduleBlockTick(pos, this, 20);
        super.onBlockAdded(state, world, pos, oldState, notify);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        if (!state.getProperties().contains(STOKED)) return;
        int stoked = state.get(STOKED);
        if (stoked == 0)
        {
            world.setBlockState(pos, Blocks.FIRE.getDefaultState());
        } else {
            world.setBlockState(pos, state.with(STOKED, Math.max(0, stoked - 1)));
            world.scheduleBlockTick(pos, this, 20);
            Heat.emitHeat(world, pos.up());
        }
    }

    @Override
    protected boolean isFlammable(BlockState state) {
        return true;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(STOKED);
    }
}
