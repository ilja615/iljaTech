package com.github.ilja615.iljatech.blocks;

import com.github.ilja615.iljatech.init.ModParticles;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.GlazedTerracottaBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class BellowsBlock extends HorizontalFacingBlock {
    public static final IntProperty PRESS = IntProperty.of("press", 0, 3);
    public static final MapCodec<BellowsBlock> CODEC = createCodec(BellowsBlock::new);

    public BellowsBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(PRESS, 0));

    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {return CODEC;}

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(PRESS, FACING);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (state.getBlock() != this) { return super.onUse(state, world, pos, player, hit); }

        if (state.get(PRESS) == 0) {
            world.setBlockState(pos, state.with(PRESS, 1));
            world.scheduleBlockTick(pos, this, 6);
            if (!world.isClient) {
                BlockPos offsetpos = pos.offset(state.get(FACING));
                ((ServerWorld) world).spawnParticles(ParticleTypes.POOF, offsetpos.getX() + 0.5d, offsetpos.getY() + 0.5d, offsetpos.getZ() + 0.5d, 5, state.get(FACING).getOffsetX(), state.get(FACING).getOffsetY(), state.get(FACING).getOffsetZ(), 1.0D);
            }
            return ActionResult.SUCCESS;
        }
        if (state.get(PRESS) == 2) {
            world.setBlockState(pos, state.with(PRESS, 3));
            world.scheduleBlockTick(pos, this, 6);
            if (!world.isClient) {
                BlockPos offsetpos = pos.offset(state.get(FACING));
                ((ServerWorld) world).spawnParticles(ParticleTypes.POOF, offsetpos.getX() + 0.5d, offsetpos.getY() + 0.5d, offsetpos.getZ() + 0.5d, 5, state.get(FACING).getOffsetX(), state.get(FACING).getOffsetY(), state.get(FACING).getOffsetZ(), 1.0D);
            }
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hit);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        if (state.getBlock() != this) { return; }

        if (state.get(PRESS) == 1) {
            world.setBlockState(pos, state.with(PRESS, 2));
        } else if (state.get(PRESS) == 3) {
            world.setBlockState(pos, state.with(PRESS, 0));
        }
    }
}
