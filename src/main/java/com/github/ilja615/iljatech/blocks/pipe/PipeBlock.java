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
import net.minecraft.util.math.Direction;
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
        Direction dir1 = ctx.getSide().getOpposite();
        Direction dir2 = ctx.getPlayerLookDirection().getOpposite();
        PipeShape pipeshape = null;
        if ((dir1 == Direction.NORTH && dir2 == Direction.SOUTH) || (dir2 == Direction.NORTH && dir1 == Direction.SOUTH))
            pipeshape = PipeShape.NORTH_SOUTH;
        if ((dir1 == Direction.NORTH && dir2 == Direction.EAST) || (dir2 == Direction.NORTH && dir1 == Direction.EAST))
            pipeshape = PipeShape.NORTH_EAST;
        if ((dir1 == Direction.NORTH && dir2 == Direction.WEST) || (dir2 == Direction.NORTH && dir1 == Direction.WEST))
            pipeshape = PipeShape.NORTH_WEST;
        if ((dir1 == Direction.EAST && dir2 == Direction.WEST) || (dir2 == Direction.EAST && dir1 == Direction.WEST))
            pipeshape = PipeShape.EAST_WEST;
        if ((dir1 == Direction.UP && dir2 == Direction.DOWN) || (dir2 == Direction.UP && dir1 == Direction.DOWN))
            pipeshape = PipeShape.UP_DOWN;
        if ((dir1 == Direction.SOUTH && dir2 == Direction.EAST) || (dir2 == Direction.SOUTH && dir1 == Direction.EAST))
            pipeshape = PipeShape.SOUTH_EAST;
        if ((dir1 == Direction.SOUTH && dir2 == Direction.WEST) || (dir2 == Direction.SOUTH && dir1 == Direction.WEST))
            pipeshape = PipeShape.SOUTH_WEST;
        if ((dir1 == Direction.NORTH && dir2 == Direction.UP) || (dir2 == Direction.NORTH && dir1 == Direction.UP))
            pipeshape = PipeShape.NORTH_UP;
        if ((dir1 == Direction.NORTH && dir2 == Direction.DOWN) || (dir2 == Direction.NORTH && dir1 == Direction.DOWN))
            pipeshape = PipeShape.NORTH_DOWN;
        if ((dir1 == Direction.EAST && dir2 == Direction.UP) || (dir2 == Direction.EAST && dir1 == Direction.UP))
            pipeshape = PipeShape.EAST_UP;
        if ((dir1 == Direction.EAST && dir2 == Direction.DOWN) || (dir2 == Direction.EAST && dir1 == Direction.DOWN))
            pipeshape = PipeShape.EAST_DOWN;
        if ((dir1 == Direction.SOUTH && dir2 == Direction.UP) || (dir2 == Direction.SOUTH && dir1 == Direction.UP))
            pipeshape = PipeShape.SOUTH_UP;
        if ((dir1 == Direction.SOUTH && dir2 == Direction.DOWN) || (dir2 == Direction.SOUTH && dir1 == Direction.DOWN))
            pipeshape = PipeShape.SOUTH_DOWN;
        if ((dir1 == Direction.WEST && dir2 == Direction.UP) || (dir2 == Direction.WEST && dir1 == Direction.UP))
            pipeshape = PipeShape.WEST_UP;
        if ((dir1 == Direction.WEST && dir2 == Direction.DOWN) || (dir2 == Direction.WEST && dir1 == Direction.DOWN))
            pipeshape = PipeShape.WEST_DOWN;

        return blockstate.with(PIPE_SHAPE, pipeshape);
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
