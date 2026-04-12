package com.github.ilja615.iljatech.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AxisRodBlock extends Block {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    protected static final VoxelShape Y_SHAPE = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
    protected static final VoxelShape Z_SHAPE = Block.box(6.0, 6.0, 0.0, 10.0, 10.0, 16.0);
    protected static final VoxelShape X_SHAPE = Block.box(0.0, 6.0, 6.0, 16.0, 10.0, 10.0);

    public AxisRodBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.Y));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        switch (state.getValue(AXIS)) {
            case X:
            default:
                return X_SHAPE;
            case Z:
                return Z_SHAPE;
            case Y:
                return Y_SHAPE;
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction direction = ctx.getClickedFace();
        BlockState blockState = ctx.getLevel().getBlockState(ctx.getClickedPos().relative(direction.getOpposite()));
        return this.defaultBlockState().setValue(AXIS, direction.getAxis());
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }
}
