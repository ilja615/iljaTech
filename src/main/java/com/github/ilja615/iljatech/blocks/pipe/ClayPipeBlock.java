package com.github.ilja615.iljatech.blocks.pipe;

import com.github.ilja615.iljatech.init.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ClayPipeBlock extends PipeBlock {
    public ClayPipeBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient) {
            if (world.getBlockState(pos.down()).getBlock() instanceof CampfireBlock && world.getBlockState(pos.down()).get(CampfireBlock.LIT)) {
                world.scheduleBlockTick(pos, this, world.random.nextBetween(5, 11) * 20);
            }
        }
        super.onBlockAdded(state, world, pos, oldState, notify);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);

        PipeShape shape = state.get(PipeBlock.PIPE_SHAPE);
        world.setBlockState(pos, ModBlocks.TERRACOTTA_PIPE.getDefaultState().with(PipeBlock.PIPE_SHAPE, shape));

        PipeShape pipeShape = world.getBlockState(pos).get(PipeBlock.PIPE_SHAPE);
        Direction pipeDir1 = pipeShape.getDirection1();
        Direction pipeDir2 = pipeShape.getDirection2();

        if (world.getBlockState(pos.offset(pipeDir1)).getBlock() instanceof ClayPipeBlock) {
            shape = world.getBlockState(pos.offset(pipeDir1)).get(PipeBlock.PIPE_SHAPE);
            world.setBlockState(pos.offset(pipeDir1), ModBlocks.TERRACOTTA_PIPE.getDefaultState().with(PipeBlock.PIPE_SHAPE, shape));
        }
        if (world.getBlockState(pos.offset(pipeDir2)).getBlock() instanceof ClayPipeBlock) {
            shape = world.getBlockState(pos.offset(pipeDir2)).get(PipeBlock.PIPE_SHAPE);
            world.setBlockState(pos.offset(pipeDir2), ModBlocks.TERRACOTTA_PIPE.getDefaultState().with(PipeBlock.PIPE_SHAPE, shape));
        }
    }
}
