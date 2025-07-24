package com.github.ilja615.iljatech.blocks.pipe;

import com.github.ilja615.iljatech.blocks.wire.WireBlock;
import com.github.ilja615.iljatech.blocks.wire.WirePlacementHelper;
import com.github.ilja615.iljatech.blocks.wire.WireShape;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.nio.channels.Pipe;

public class PipeBlock  extends Block {
    public static final EnumProperty<PipeShape> PIPE_SHAPE = EnumProperty.of("pipe_shape", PipeShape.class);

    public PipeBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(PIPE_SHAPE, PipeShape.UP_DOWN));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockstate = this.getDefaultState();
        return updateBlockState(ctx.getWorld(), ctx.getBlockPos(), blockstate, true);
    }

    protected BlockState updateBlockState(World world, BlockPos pos, BlockState state, boolean notify) {
        if (world.isClient) {
            return state;
        } else {
            PipeShape pipeshape = state.get(PIPE_SHAPE);
            BlockState returnState = (new PipePlacementHelper(world, pos, state)).place(notify, pipeshape).getState();
            return returnState;
        }
    }

    public static boolean isPipe(World world, BlockPos pos) {
        return isPipe(world.getBlockState(pos));
    }

    public static boolean isPipe(BlockState blockState) {
        return blockState.getBlock() instanceof PipeBlock;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(PIPE_SHAPE);
    }
}
