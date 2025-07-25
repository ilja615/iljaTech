package com.github.ilja615.iljatech.blocks.pipe;

import com.github.ilja615.iljatech.blocks.wire.WireBlock;
import com.github.ilja615.iljatech.blocks.wire.WirePlacementHelper;
import com.github.ilja615.iljatech.blocks.wire.WireShape;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.nio.channels.Pipe;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class PipeBlock  extends Block {
    public static final EnumProperty<PipeShape> PIPE_SHAPE = EnumProperty.of("pipe_shape", PipeShape.class);

    protected static final Map<Direction, VoxelShape> OUTLINE_SHAPES;

    static {
        Map<Direction, VoxelShape> tempMap = new EnumMap<>(Direction.class);

        tempMap.put(Direction.UP, Block.createCuboidShape(3.0d, 13.0d, 3.0d, 13.0d, 16.0d, 13.0d));
        tempMap.put(Direction.DOWN, Block.createCuboidShape(3.0d, 0.0d, 3.0d, 13.0d, 3.0d, 13.0d));
        tempMap.put(Direction.WEST, Block.createCuboidShape(0.0d, 3.0d, 3.0d, 3.0d, 13.0d, 13.0d));
        tempMap.put(Direction.EAST, Block.createCuboidShape(13.0d, 3.0d, 3.0d, 16.0d, 13.0d, 13.0d));
        tempMap.put(Direction.NORTH, Block.createCuboidShape(3.0d, 3.0d, 0.0d, 13.0d, 13.0d, 3.0d));
        tempMap.put(Direction.SOUTH, Block.createCuboidShape(3.0d, 3.0d, 13.0d, 13.0d, 13.0d, 16.0d));

        OUTLINE_SHAPES = Collections.unmodifiableMap(tempMap);
    }
    protected static final VoxelShape CORE_AABB = Block.createCuboidShape(3.0d, 3.0d, 3.0d, 13.0d, 13.0d, 13.0d);

    public PipeBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(PIPE_SHAPE, PipeShape.UP_DOWN));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        PipeShape pipeshape = state.get(PIPE_SHAPE);
        return VoxelShapes.union(CORE_AABB, OUTLINE_SHAPES.get(pipeshape.getDirection1()), OUTLINE_SHAPES.get(pipeshape.getDirection2()));
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
