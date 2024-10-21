package com.github.ilja615.iljatech.blocks.wire;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class WireBlock extends Block {
    protected static final VoxelShape FLAT_AABB = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
    protected static final VoxelShape HALF_BLOCK_AABB = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    public static final EnumProperty<WireShape> WIRE_SHAPE = EnumProperty.of("wire_shape", WireShape.class);
    public static final IntProperty DISTANCE = IntProperty.of("distance", 0, 7);

    public WireBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(WIRE_SHAPE, WireShape.NORTH_SOUTH).with(DISTANCE, 7));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        WireShape wireShape = state.getBlock() instanceof WireBlock ? state.get(WIRE_SHAPE) : null;
        return wireShape != null && wireShape.isAscending() ? HALF_BLOCK_AABB : FLAT_AABB;
    }

    public static boolean isWire(World world, BlockPos pos) {
        return isWire(world.getBlockState(pos));
    }

    public static boolean isWire(BlockState blockState) {
        return blockState.getBlock() instanceof WireBlock;
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
            WireShape wireshape = state.get(WIRE_SHAPE);
            BlockState returnState = (new WirePlacementHelper(world, pos, state)).place(notify, wireshape).getState();
            int i = calculateDistance(world, pos);
            return returnState.with(DISTANCE, i);
        }
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient) {
            this.updateBlockState(world, pos, state, false);
            world.scheduleBlockTick(pos, this, 1);
        }
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient && world.getBlockState(pos).isOf(this)) {
            this.updateBlockState(world, pos, state, false);
            world.scheduleBlockTick(pos, this, 1);
        }
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!world.isClient()) {
            world.scheduleBlockTick(pos, this, 1);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!moved) {
            super.onStateReplaced(state, world, pos, newState, moved);
            if ((state.get(WIRE_SHAPE)).isAscending()) {
                world.updateNeighborsAlways(pos.up(), this);
            }
        }
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int i = calculateDistance(world, pos);
        BlockState blockState = state.with(DISTANCE, i);
        if (blockState.get(DISTANCE) == 7) {
            FallingBlockEntity.spawnFromBlock(world, pos, blockState);
        } else if (state != blockState) {
            world.setBlockState(pos, blockState, Block.NOTIFY_ALL);
        }
    }

    public static int calculateDistance(BlockView world, BlockPos pos) {
        BlockPos.Mutable mutable = pos.mutableCopy().move(Direction.DOWN);
        BlockState blockState = world.getBlockState(mutable);
        int i = 7;
        if (blockState.getBlock() instanceof WireBlock) {
            i = blockState.get(DISTANCE);
        } else if (blockState.isSideSolidFullSquare(world, mutable, Direction.UP)) {
            return 0;
        }

        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockState blockState2 = world.getBlockState(mutable.set(pos, direction));
            if (blockState2.getBlock() instanceof WireBlock) {
                i = Math.min(i, blockState2.get(DISTANCE) + 1);
                if (i == 1) {
                    break;
                }
            }
        }

        return i;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WIRE_SHAPE, DISTANCE);
    }
}
