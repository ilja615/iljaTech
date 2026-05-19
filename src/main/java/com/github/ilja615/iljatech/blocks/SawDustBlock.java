package com.github.ilja615.iljatech.blocks;

import com.github.ilja615.iljatech.blocks.windmill.WindDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class SawDustBlock extends Block {
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 1, 4);

    protected static final VoxelShape[] LAYERS_TO_SHAPE = new VoxelShape[]{
            Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0)};

    public SawDustBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 1));
    }

    protected boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        int i = state.getValue(LEVEL);
        if (context.getItemInHand().is(this.asItem()) && i < 4) {
            if (context.replacingClickedOnBlock()) {
                return context.getClickedFace() == Direction.UP;
            } else {
                return true;
            }
        } else {
            return i == 1;
        }
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState blockState = ctx.getLevel().getBlockState(ctx.getClickedPos());
        if (blockState.is(this)) {
            int i = blockState.getValue(LEVEL);
            return blockState.setValue(LEVEL, Math.min(4, i + 1));
        } else {
            return super.getStateForPlacement(ctx);
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return LAYERS_TO_SHAPE[state.getValue(LEVEL) - 1];
    }

    protected VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return LAYERS_TO_SHAPE[state.getValue(LEVEL) - 1];
    }

    protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter world, BlockPos pos) {
        return LAYERS_TO_SHAPE[state.getValue(LEVEL) - 1];
    }

    protected VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return LAYERS_TO_SHAPE[state.getValue(LEVEL) - 1];
    }
}
