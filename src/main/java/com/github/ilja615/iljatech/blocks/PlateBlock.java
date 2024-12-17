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
import net.minecraft.state.property.DirectionProperty;
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
    public static final DirectionProperty FACING = Properties.FACING;

    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    protected static final VoxelShape UP_AABB = Block.createCuboidShape(0.0d, 14.0d, 0.0d, 16.0d, 16.0d, 16.0d);
    protected static final VoxelShape DOWN_AABB = Block.createCuboidShape(0.0d, 0.0d, 0.0d, 16.0d, 2.0d, 16.0d);
    protected static final VoxelShape WEST_AABB = Block.createCuboidShape(0.0d, 0.0d, 0.0d, 2.0d, 16.0d, 16.0d);
    protected static final VoxelShape EAST_AABB = Block.createCuboidShape(14.0d, 0.0d, 0.0d, 16.0d, 16.0d, 16.0d);
    protected static final VoxelShape NORTH_AABB = Block.createCuboidShape(0.0d, 0.0d, 0.0d, 16.0d, 16.0d, 2.0d);
    protected static final VoxelShape SOUTH_AABB = Block.createCuboidShape(0.0d, 0.0d, 14.0d, 16.0d, 16.0d, 16.0d);

    public PlateBlock(AbstractBlock.Settings settings)
    {
        super(settings);
        this.setDefaultState(this.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(WATERLOGGED, Boolean.valueOf(false)));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        VoxelShape voxelshape = VoxelShapes.empty();
        if (state.get(FACING) == Direction.UP) {
            voxelshape = VoxelShapes.union(voxelshape, UP_AABB);
        }

        if (state.get(FACING) == Direction.DOWN) {
            voxelshape = VoxelShapes.union(voxelshape, DOWN_AABB);
        }

        if (state.get(FACING) == Direction.NORTH) {
            voxelshape = VoxelShapes.union(voxelshape, NORTH_AABB);
        }

        if (state.get(FACING) == Direction.EAST) {
            voxelshape = VoxelShapes.union(voxelshape, EAST_AABB);
        }

        if (state.get(FACING) == Direction.SOUTH) {
            voxelshape = VoxelShapes.union(voxelshape, SOUTH_AABB);
        }

        if (state.get(FACING) == Direction.WEST) {
            voxelshape = VoxelShapes.union(voxelshape, WEST_AABB);
        }
        return voxelshape;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        if (ctx.getSide().getAxis() == Direction.Axis.Y)
        {
            if (ctx.getHitPos().getX() - blockPos.getX() > 0.75D)
                return this.getDefaultState().with(FACING, Direction.EAST);
            if (ctx.getHitPos().getX() - blockPos.getX() < 0.25D)
                return this.getDefaultState().with(FACING, Direction.WEST);
            if (ctx.getHitPos().getZ() - blockPos.getZ() > 0.75D)
                return this.getDefaultState().with(FACING, Direction.SOUTH);
            if (ctx.getHitPos().getZ() - blockPos.getZ() < 0.25D)
                return this.getDefaultState().with(FACING, Direction.NORTH);
        }
        if (ctx.getSide().getAxis() == Direction.Axis.X)
        {
            if (ctx.getHitPos().getY() - blockPos.getY() > 0.75D)
                return this.getDefaultState().with(FACING, Direction.UP);
            if (ctx.getHitPos().getY() - blockPos.getY() < 0.25D)
                return this.getDefaultState().with(FACING, Direction.DOWN);
            if (ctx.getHitPos().getZ() - blockPos.getZ() > 0.75D)
                return this.getDefaultState().with(FACING, Direction.SOUTH);
            if (ctx.getHitPos().getZ() - blockPos.getZ() < 0.25D)
                return this.getDefaultState().with(FACING, Direction.NORTH);
        }
        if (ctx.getSide().getAxis() == Direction.Axis.Z)
        {
            if (ctx.getHitPos().getY() - blockPos.getY() > 0.75D)
                return this.getDefaultState().with(FACING, Direction.UP);
            if (ctx.getHitPos().getY() - blockPos.getY() < 0.25D)
                return this.getDefaultState().with(FACING, Direction.DOWN);
            if (ctx.getHitPos().getX() - blockPos.getX() > 0.75D)
                return this.getDefaultState().with(FACING, Direction.EAST);
            if (ctx.getHitPos().getX() - blockPos.getX() < 0.25D)
                return this.getDefaultState().with(FACING, Direction.WEST);
        }
        // In case clicked on "middle" of face:
        return this.getDefaultState().with(FACING, ctx.getSide().getOpposite());
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
        builder.add(FACING, WATERLOGGED);
    }
}