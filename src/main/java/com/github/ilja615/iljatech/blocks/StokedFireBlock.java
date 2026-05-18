package com.github.ilja615.iljatech.blocks;

import com.github.ilja615.iljatech.energy.Heat;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class StokedFireBlock extends BaseFireBlock {
    public static final IntegerProperty STOKED = IntegerProperty.create("stoked", 0, 3);

    public StokedFireBlock(Properties settings) {
        super(settings, 2);
        this.registerDefaultState(this.defaultBlockState().setValue(STOKED, 3));
    }

    @Override
    protected MapCodec<? extends BaseFireBlock> codec() {
        return null;
    }

    @Override
    protected void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        Heat.emitHeat(world, pos.above());
        world.scheduleTick(pos, this, 60);
        super.onPlace(state, world, pos, oldState, notify);
    }

    @Override
    protected void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        super.tick(state, world, pos, random);
        if (!state.getProperties().contains(STOKED)) return;
        int stoked = state.getValue(STOKED);
        if (stoked == 0)
        {
            world.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
        } else {
            world.setBlockAndUpdate(pos, state.setValue(STOKED, Math.max(0, stoked - 1)));
            world.scheduleTick(pos, this, 60);
            Heat.emitHeat(world, pos.above());
        }
    } 

    @Override
    protected boolean canBurn(BlockState state) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STOKED);
    }
}
