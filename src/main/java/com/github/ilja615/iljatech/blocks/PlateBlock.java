package com.github.ilja615.iljatech.blocks;

import net.minecraft.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class PlateBlock extends Block implements SimpleWaterloggedBlock
{
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape UP_AABB = Block.box(0.0d, 14.0d, 0.0d, 16.0d, 16.0d, 16.0d);
    protected static final VoxelShape DOWN_AABB = Block.box(0.0d, 0.0d, 0.0d, 16.0d, 2.0d, 16.0d);
    protected static final VoxelShape WEST_AABB = Block.box(0.0d, 0.0d, 0.0d, 2.0d, 16.0d, 16.0d);
    protected static final VoxelShape EAST_AABB = Block.box(14.0d, 0.0d, 0.0d, 16.0d, 16.0d, 16.0d);
    protected static final VoxelShape NORTH_AABB = Block.box(0.0d, 0.0d, 0.0d, 16.0d, 16.0d, 2.0d);
    protected static final VoxelShape SOUTH_AABB = Block.box(0.0d, 0.0d, 14.0d, 16.0d, 16.0d, 16.0d);

    public PlateBlock(BlockBehaviour.Properties settings)
    {
        super(settings);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, Boolean.valueOf(false)));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        VoxelShape voxelshape = Shapes.empty();
        if (state.getValue(FACING) == Direction.UP) {
            voxelshape = Shapes.or(voxelshape, UP_AABB);
        }

        if (state.getValue(FACING) == Direction.DOWN) {
            voxelshape = Shapes.or(voxelshape, DOWN_AABB);
        }

        if (state.getValue(FACING) == Direction.NORTH) {
            voxelshape = Shapes.or(voxelshape, NORTH_AABB);
        }

        if (state.getValue(FACING) == Direction.EAST) {
            voxelshape = Shapes.or(voxelshape, EAST_AABB);
        }

        if (state.getValue(FACING) == Direction.SOUTH) {
            voxelshape = Shapes.or(voxelshape, SOUTH_AABB);
        }

        if (state.getValue(FACING) == Direction.WEST) {
            voxelshape = Shapes.or(voxelshape, WEST_AABB);
        }
        return voxelshape;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockPos blockPos = ctx.getClickedPos();
        if (ctx.getClickedFace().getAxis() == Direction.Axis.Y)
        {
            if (ctx.getClickLocation().x() - blockPos.getX() > 0.75D)
                return this.defaultBlockState().setValue(FACING, Direction.EAST);
            if (ctx.getClickLocation().x() - blockPos.getX() < 0.25D)
                return this.defaultBlockState().setValue(FACING, Direction.WEST);
            if (ctx.getClickLocation().z() - blockPos.getZ() > 0.75D)
                return this.defaultBlockState().setValue(FACING, Direction.SOUTH);
            if (ctx.getClickLocation().z() - blockPos.getZ() < 0.25D)
                return this.defaultBlockState().setValue(FACING, Direction.NORTH);
        }
        if (ctx.getClickedFace().getAxis() == Direction.Axis.X)
        {
            if (ctx.getClickLocation().y() - blockPos.getY() > 0.75D)
                return this.defaultBlockState().setValue(FACING, Direction.UP);
            if (ctx.getClickLocation().y() - blockPos.getY() < 0.25D)
                return this.defaultBlockState().setValue(FACING, Direction.DOWN);
            if (ctx.getClickLocation().z() - blockPos.getZ() > 0.75D)
                return this.defaultBlockState().setValue(FACING, Direction.SOUTH);
            if (ctx.getClickLocation().z() - blockPos.getZ() < 0.25D)
                return this.defaultBlockState().setValue(FACING, Direction.NORTH);
        }
        if (ctx.getClickedFace().getAxis() == Direction.Axis.Z)
        {
            if (ctx.getClickLocation().y() - blockPos.getY() > 0.75D)
                return this.defaultBlockState().setValue(FACING, Direction.UP);
            if (ctx.getClickLocation().y() - blockPos.getY() < 0.25D)
                return this.defaultBlockState().setValue(FACING, Direction.DOWN);
            if (ctx.getClickLocation().x() - blockPos.getX() > 0.75D)
                return this.defaultBlockState().setValue(FACING, Direction.EAST);
            if (ctx.getClickLocation().x() - blockPos.getX() < 0.25D)
                return this.defaultBlockState().setValue(FACING, Direction.WEST);
        }
        // In case clicked on "middle" of face:
        return this.defaultBlockState().setValue(FACING, ctx.getClickedFace().getOpposite());
    }

    // Watterlogging stuff

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        switch (type) {
            case LAND:
                return false;
            case WATER:
                return state.getFluidState().is(FluidTags.WATER);
            case AIR:
                return false;
            default:
                return false;
        }
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean placeLiquid(LevelAccessor world, BlockPos pos, BlockState state, FluidState fluidState) {
        return SimpleWaterloggedBlock.super.placeLiquid(world, pos, state, fluidState);
    }

    @Override
    public boolean canPlaceLiquid(@Nullable Player player, BlockGetter world, BlockPos pos, BlockState state, Fluid fluid) {
        return SimpleWaterloggedBlock.super.canPlaceLiquid(player, world, pos, state, fluid);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if ((Boolean)state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }
}