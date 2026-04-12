package com.github.ilja615.iljatech.blocks;

import com.github.ilja615.iljatech.energy.MechPwrSender;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class CrankBlock extends Block implements MechPwrSender {
    public static final IntegerProperty ROTATION = IntegerProperty.create("rotation", 0, 7);
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public CrankBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(ROTATION, 0).setValue(FACING, Direction.UP));

    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return FaceAttachedHorizontalDirectionalBlock.canAttach(world, pos, state.getValue(FACING));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (state.getBlock() == this)
        {
            sendPower(world, pos, state.getValue(FACING), 4);
            world.setBlock(pos, state.setValue(ROTATION, (state.getValue(ROTATION)+1)%8), Block.UPDATE_ALL);
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, world, pos, player, hit);
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState blockState = this.defaultBlockState().setValue(FACING, ctx.getClickedFace().getOpposite());

        if (blockState.canSurvive(ctx.getLevel(), ctx.getClickedPos())) {
            return blockState;
        }

        return null;
    }

    @Override
    protected BlockState updateShape(
            BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos
    ) {        return state.getValue(FACING) == direction && !state.canSurvive(world, pos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ROTATION);
    }
}
