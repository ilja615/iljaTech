package com.github.ilja615.iljatech.blocks.pulverizermill;

import com.github.ilja615.iljatech.blocks.rollermill.RollerMillBlockEntity;
import com.github.ilja615.iljatech.energy.MechPwrAccepter;
import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static com.github.ilja615.iljatech.energy.MechPwrAccepter.OnOffPwr.*;

public class PulverizerMillBlock extends HorizontalDirectionalBlock implements EntityBlock, MechPwrAccepter {
    public static final MapCodec<PulverizerMillBlock> CODEC = simpleCodec(PulverizerMillBlock::new);
    public static final IntegerProperty HALF = IntegerProperty.create("half", 1, 2);

    public PulverizerMillBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, 1).setValue(ON_OFF_PWR, OnOffPwr.OFF));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(HALF) == 1 ? ModBlockEntityTypes.PULVERIZER_MILL.create(pos, state) : null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return state.getValue(ON_OFF_PWR) != OnOffPwr.OFF && state.getValue(HALF) == 1? TickableBlockEntity.getTicker(world) : null;
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {return CODEC;}

    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (direction == getDirectionTowardsOtherPart(state.getValue(HALF), state.getValue(FACING))) {
            return neighborState.is(this) && !Objects.equals(neighborState.getValue(HALF), state.getValue(HALF)) ? state.setValue(ON_OFF_PWR, neighborState.getValue(ON_OFF_PWR)).setValue(FACING, neighborState.getValue(FACING)) : Blocks.AIR.defaultBlockState();
        } else {
            return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
        }
    }

    private static Direction getDirectionTowardsOtherPart(int half, Direction direction) {
        return half == 1 ? direction : direction.getOpposite();
    }

    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (!world.isClientSide && player.isCreative()) {
            int half = state.getValue(HALF);
            if (half == 1) {
                BlockPos blockPos = pos.relative(getDirectionTowardsOtherPart(half, (Direction)state.getValue(FACING)));
                BlockState blockState = world.getBlockState(blockPos);
                if (blockState.is(this) && blockState.getValue(HALF) == 2) {
                    world.setBlock(blockPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
                    world.levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, blockPos, Block.getId(blockState));
                }
            }
        }

        return super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    protected void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock() && state.getValue(HALF) == 1) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof RollerMillBlockEntity rollerMillBlockEntity) {
                Containers.dropContents(world, pos, rollerMillBlockEntity.getInventory());
                world.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, world, pos, newState, moved);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction direction = ctx.getHorizontalDirection();
        BlockPos blockPos = ctx.getClickedPos();
        BlockPos blockPos2 = blockPos.relative(direction);
        Level world = ctx.getLevel();
        return world.getBlockState(blockPos2).canBeReplaced(ctx) && world.getWorldBorder().isWithinBounds(blockPos2) ? (BlockState)this.defaultBlockState().setValue(FACING, direction) : null;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF, ON_OFF_PWR);
    }

    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, placer, itemStack);
        if (!world.isClientSide) {
            BlockPos blockPos = pos.relative((Direction)state.getValue(FACING));
            world.setBlock(blockPos, (BlockState)state.setValue(HALF, 2), Block.UPDATE_ALL);
            world.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(world, pos, Block.UPDATE_ALL);
        }
    }

    @Override
    public boolean acceptsPower(Level world, BlockPos thisPos, Direction sideFrom) {
        BlockState state = world.getBlockState(thisPos);
        return state.getProperties().contains(ON_OFF_PWR) && state.getValue(HALF) == 1 && sideFrom.getAxis() != state.getValue(FACING).getAxis();
    }

    @Override
    public void receivePower(Level world, BlockPos thisPos, Direction sideFrom, int amount)
    {
        // Schedules to stop
        world.setBlockAndUpdate(thisPos, world.getBlockState(thisPos).setValue(ON_OFF_PWR, ON));
        world.scheduleTick(thisPos, this, 10);
    }

    @Override
    protected void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        super.tick(state, world, pos, random);
        if (state.getBlock() != this) {
            return;
        }
        if (state.getValue(ON_OFF_PWR) == SCHEDULED_STOP)
            world.setBlockAndUpdate(pos, state.setValue(ON_OFF_PWR, OFF));
        else if (state.getValue(ON_OFF_PWR) == ON){
            world.scheduleTick(pos, this, 10);
            world.setBlockAndUpdate(pos, state.setValue(ON_OFF_PWR, SCHEDULED_STOP));
        }
    }


}
