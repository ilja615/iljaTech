package com.github.ilja615.iljatech.blocks.wire;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class WireBlock extends Block {
    protected static final VoxelShape FLAT_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
    protected static final VoxelShape HALF_BLOCK_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    public static final EnumProperty<WireShape> WIRE_SHAPE = EnumProperty.create("wire_shape", WireShape.class);
    public static final IntegerProperty DISTANCE = IntegerProperty.create("distance", 0, 7);

    public WireBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(WIRE_SHAPE, WireShape.NORTH_SOUTH).setValue(DISTANCE, 7));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        WireShape wireShape = state.getBlock() instanceof WireBlock ? state.getValue(WIRE_SHAPE) : null;
        return wireShape != null && wireShape.isAscending() ? HALF_BLOCK_AABB : FLAT_AABB;
    }

    public static boolean isWire(Level world, BlockPos pos) {
        return isWire(world.getBlockState(pos));
    }

    public static boolean isWire(BlockState blockState) {
        return blockState.getBlock() instanceof WireBlock;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState blockstate = this.defaultBlockState();
        return updateBlockState(ctx.getLevel(), ctx.getClickedPos(), blockstate, true);
    }

    protected BlockState updateBlockState(Level world, BlockPos pos, BlockState state, boolean notify) {
        if (world.isClientSide) {
            return state;
        } else {
            WireShape wireshape = state.getValue(WIRE_SHAPE);
            BlockState returnState = (new WirePlacementHelper(world, pos, state)).place(notify, wireshape).getState();
            int i = calculateDistance(world, pos);
            return returnState.setValue(DISTANCE, i);
        }
    }

    @Override
    protected void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClientSide) {
            this.updateBlockState(world, pos, state, false);
            world.scheduleTick(pos, this, 1);
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {        if (!world.isClientSide && world.getBlockState(pos).is(this)) {
            this.updateBlockState(world, pos, state, false);
            world.scheduleTick(pos, this, 1);
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {        if (!world.isClientSide()) {
            world.scheduleTick(pos, this, 1);
        }
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);    }

    @Override
    protected void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (!moved) {
            super.onRemove(state, world, pos, newState, moved);
            if ((state.getValue(WIRE_SHAPE)).isAscending()) {
                world.updateNeighborsAt(pos.above(), this);
            }
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        int i = calculateDistance(world, pos);
        BlockState blockState = state.setValue(DISTANCE, i);
        if (blockState.getValue(DISTANCE) == 7) {
            FallingBlockEntity.fall(world, pos, blockState);
        } else if (state != blockState) {
            world.setBlock(pos, blockState, Block.UPDATE_ALL);
        }
    }

    public static int calculateDistance(BlockGetter world, BlockPos pos) {
        BlockPos.MutableBlockPos mutable = pos.mutable().move(Direction.DOWN);
        BlockState blockState = world.getBlockState(mutable);
        int i = 7;
        if (blockState.getBlock() instanceof WireBlock) {
            i = blockState.getValue(DISTANCE);
        } else if (blockState.isFaceSturdy(world, mutable, Direction.UP)) {
            return 0;
        }

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockState blockState2 = world.getBlockState(mutable.setWithOffset(pos, direction));
            if (blockState2.getBlock() instanceof WireBlock) {
                i = Math.min(i, blockState2.getValue(DISTANCE) + 1);
                if (i == 1) {
                    break;
                }
            }
        }

        return i;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WIRE_SHAPE, DISTANCE);
    }
}
