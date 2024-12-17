package com.github.ilja615.iljatech.blocks;

import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class PlateBlock extends Block implements Waterloggable
{
    public static final BooleanProperty NORTH = Properties.NORTH;
    public static final BooleanProperty EAST = Properties.EAST;
    public static final BooleanProperty SOUTH = Properties.SOUTH;
    public static final BooleanProperty WEST = Properties.WEST;
    public static final BooleanProperty UP = Properties.UP;
    public static final BooleanProperty DOWN = Properties.DOWN;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public static final Map<Direction, BooleanProperty> FACING_PROPERTIES = ConnectingBlock.FACING_PROPERTIES;

    protected static final VoxelShape UP_AABB = Block.createCuboidShape(0.0d, 14.0d, 0.0d, 16.0d, 16.0d, 16.0d);
    protected static final VoxelShape DOWN_AABB = Block.createCuboidShape(0.0d, 0.0d, 0.0d, 16.0d, 2.0d, 16.0d);
    protected static final VoxelShape WEST_AABB = Block.createCuboidShape(0.0d, 0.0d, 0.0d, 2.0d, 16.0d, 16.0d);
    protected static final VoxelShape EAST_AABB = Block.createCuboidShape(14.0d, 0.0d, 0.0d, 16.0d, 16.0d, 16.0d);
    protected static final VoxelShape NORTH_AABB = Block.createCuboidShape(0.0d, 0.0d, 0.0d, 16.0d, 16.0d, 2.0d);
    protected static final VoxelShape SOUTH_AABB = Block.createCuboidShape(0.0d, 0.0d, 14.0d, 16.0d, 16.0d, 16.0d);

    public PlateBlock(AbstractBlock.Settings settings)
    {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(UP, Boolean.valueOf(false)).with(DOWN, Boolean.valueOf(false))
                .with(NORTH, Boolean.valueOf(false)).with(EAST, Boolean.valueOf(false))
                .with(SOUTH, Boolean.valueOf(false)).with(WEST, Boolean.valueOf(false))
                .with(WATERLOGGED, Boolean.valueOf(false)));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        VoxelShape voxelshape = VoxelShapes.empty();
        if (state.get(UP)) {
            voxelshape = VoxelShapes.union(voxelshape, UP_AABB);
        }

        if (state.get(DOWN)) {
            voxelshape = VoxelShapes.union(voxelshape, DOWN_AABB);
        }

        if (state.get(NORTH)) {
            voxelshape = VoxelShapes.union(voxelshape, NORTH_AABB);
        }

        if (state.get(EAST)) {
            voxelshape = VoxelShapes.union(voxelshape, EAST_AABB);
        }

        if (state.get(SOUTH)) {
            voxelshape = VoxelShapes.union(voxelshape, SOUTH_AABB);
        }

        if (state.get(WEST)) {
            voxelshape = VoxelShapes.union(voxelshape, WEST_AABB);
        }
        return voxelshape;
    }

    public static BooleanProperty getPropertyFor(Direction side) {
        return FACING_PROPERTIES.get(side);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        BlockState blockState = ctx.getWorld().getBlockState(blockPos);
        if (blockState.isOf(this))
        {
            // For adding more plates to an existing block
            if (ctx.getSide().getAxis() == Direction.Axis.Y)
            {
                if (ctx.getHitPos().getX() - blockPos.getX() > 0.875D)
                    return blockState.with(EAST, true);
                if (ctx.getHitPos().getX() - blockPos.getX() < 0.125D)
                    return blockState.with(WEST, true);
                if (ctx.getHitPos().getZ() - blockPos.getZ() > 0.875D)
                    return blockState.with(SOUTH, true);
                if (ctx.getHitPos().getZ() - blockPos.getZ() < 0.125D)
                    return blockState.with(NORTH, true);
            }
            if (ctx.getSide().getAxis() == Direction.Axis.X)
            {
                if (ctx.getHitPos().getY() - blockPos.getY() > 0.875D)
                    return blockState.with(UP, true);
                if (ctx.getHitPos().getY() - blockPos.getY() < 0.125D)
                    return blockState.with(DOWN, true);
                if (ctx.getHitPos().getZ() - blockPos.getZ() > 0.875D)
                    return blockState.with(SOUTH, true);
                if (ctx.getHitPos().getZ() - blockPos.getZ() < 0.125D)
                    return blockState.with(NORTH, true);
            }
            if (ctx.getSide().getAxis() == Direction.Axis.Z)
            {
                if (ctx.getHitPos().getY() - blockPos.getY() > 0.875D)
                    return blockState.with(UP, true);
                if (ctx.getHitPos().getY() - blockPos.getY() < 0.125D)
                    return blockState.with(DOWN, true);
                if (ctx.getHitPos().getX() - blockPos.getX() > 0.875D)
                    return blockState.with(EAST, true);
                if (ctx.getHitPos().getX() - blockPos.getX() < 0.125D)
                    return blockState.with(WEST, true);
            }
            // Default:
            if (!blockState.get(FACING_PROPERTIES.get(ctx.getSide().getOpposite())))
                return blockState.with(FACING_PROPERTIES.get(ctx.getSide().getOpposite()), true);
            else
                return blockState;
        } else {
            // For placing a new block:
            if (ctx.getSide().getAxis() == Direction.Axis.Y)
            {
                if (ctx.getHitPos().getX() - blockPos.getX() > 0.875D)
                    return this.getDefaultState().with(EAST, true);
                if (ctx.getHitPos().getX() - blockPos.getX() < 0.125D)
                    return this.getDefaultState().with(WEST, true);
                if (ctx.getHitPos().getZ() - blockPos.getZ() > 0.875D)
                    return this.getDefaultState().with(SOUTH, true);
                if (ctx.getHitPos().getZ() - blockPos.getZ() < 0.125D)
                    return this.getDefaultState().with(NORTH, true);
                return this.getDefaultState().with(FACING_PROPERTIES.get(ctx.getSide().getOpposite()), true);
            }
            if (ctx.getSide().getAxis() == Direction.Axis.X)
            {
                if (ctx.getHitPos().getY() - blockPos.getY() > 0.875D)
                    return this.getDefaultState().with(UP, true);
                if (ctx.getHitPos().getY() - blockPos.getY() < 0.125D)
                    return this.getDefaultState().with(DOWN, true);
                if (ctx.getHitPos().getZ() - blockPos.getZ() > 0.875D)
                    return this.getDefaultState().with(SOUTH, true);
                if (ctx.getHitPos().getZ() - blockPos.getZ() < 0.125D)
                    return this.getDefaultState().with(NORTH, true);
                return this.getDefaultState().with(FACING_PROPERTIES.get(ctx.getSide().getOpposite()), true);
            }
            if (ctx.getSide().getAxis() == Direction.Axis.Z)
            {
                if (ctx.getHitPos().getY() - blockPos.getY() > 0.875D)
                    return this.getDefaultState().with(UP, true);
                if (ctx.getHitPos().getY() - blockPos.getY() < 0.125D)
                    return this.getDefaultState().with(DOWN, true);
                if (ctx.getHitPos().getX() - blockPos.getX() > 0.875D)
                    return this.getDefaultState().with(EAST, true);
                if (ctx.getHitPos().getX() - blockPos.getX() < 0.125D)
                    return this.getDefaultState().with(WEST, true);
                return this.getDefaultState().with(FACING_PROPERTIES.get(ctx.getSide().getOpposite()), true);
            }
            return this.getDefaultState().with(FACING_PROPERTIES.get(ctx.getSide()), true);
        }
    }

    // Watterlogging stuff

    @Override
    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        switch (type) {
            case LAND:
                return false;
            case WATER:
                return state.getFluidState().isIn(FluidTags.WATER);
            case AIR:
                return false;
            default:
                return false;
        }
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        return Waterloggable.super.tryFillWithFluid(world, pos, state, fluidState);
    }

    @Override
    public boolean canFillWithFluid(@Nullable PlayerEntity player, BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        return Waterloggable.super.canFillWithFluid(player, world, pos, state, fluid);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(
            BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
    ) {
        if ((Boolean)state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST, WATERLOGGED);
    }
}