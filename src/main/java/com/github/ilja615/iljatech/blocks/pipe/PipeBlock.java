package com.github.ilja615.iljatech.blocks.pipe;

import com.github.ilja615.iljatech.blocks.wire.WireBlock;
import com.github.ilja615.iljatech.blocks.wire.WirePlacementHelper;
import com.github.ilja615.iljatech.blocks.wire.WireShape;
import java.nio.channels.Pipe;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PipeBlock  extends Block {
    public static final EnumProperty<PipeShape> PIPE_SHAPE = EnumProperty.create("pipe_shape", PipeShape.class);

    protected static final Map<Direction, VoxelShape> OUTLINE_SHAPES;

    static {
        Map<Direction, VoxelShape> tempMap = new EnumMap<>(Direction.class);

        tempMap.put(Direction.UP, Block.box(3.0d, 13.0d, 3.0d, 13.0d, 16.0d, 13.0d));
        tempMap.put(Direction.DOWN, Block.box(3.0d, 0.0d, 3.0d, 13.0d, 3.0d, 13.0d));
        tempMap.put(Direction.WEST, Block.box(0.0d, 3.0d, 3.0d, 3.0d, 13.0d, 13.0d));
        tempMap.put(Direction.EAST, Block.box(13.0d, 3.0d, 3.0d, 16.0d, 13.0d, 13.0d));
        tempMap.put(Direction.NORTH, Block.box(3.0d, 3.0d, 0.0d, 13.0d, 13.0d, 3.0d));
        tempMap.put(Direction.SOUTH, Block.box(3.0d, 3.0d, 13.0d, 13.0d, 13.0d, 16.0d));

        OUTLINE_SHAPES = Collections.unmodifiableMap(tempMap);
    }
    protected static final VoxelShape CORE_AABB = Block.box(3.0d, 3.0d, 3.0d, 13.0d, 13.0d, 13.0d);

    public PipeBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(PIPE_SHAPE, PipeShape.UP_DOWN));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        PipeShape pipeshape = state.getValue(PIPE_SHAPE);
        return Shapes.or(CORE_AABB, OUTLINE_SHAPES.get(pipeshape.getDirection1()), OUTLINE_SHAPES.get(pipeshape.getDirection2()));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState blockstate = this.defaultBlockState();
        Direction dir1 = ctx.getClickedFace().getOpposite();
        Direction dir2 = ctx.getNearestLookingDirection().getOpposite();
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

        return blockstate.setValue(PIPE_SHAPE, pipeshape);
    }

    public static boolean isPipe(Level world, BlockPos pos) {
        return isPipe(world.getBlockState(pos));
    }

    public static boolean isPipe(BlockState blockState) {
        return blockState.getBlock() instanceof PipeBlock;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PIPE_SHAPE);
    }
}
