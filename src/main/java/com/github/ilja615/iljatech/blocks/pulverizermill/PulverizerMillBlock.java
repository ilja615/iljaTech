package com.github.ilja615.iljatech.blocks.pulverizermill;

import com.github.ilja615.iljatech.blocks.rollermill.RollerMillBlockEntity;
import com.github.ilja615.iljatech.energy.MechPwrAccepter;
import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static com.github.ilja615.iljatech.energy.MechPwrAccepter.OnOffPwr.*;

public class PulverizerMillBlock extends HorizontalFacingBlock implements BlockEntityProvider, MechPwrAccepter {
    public static final MapCodec<PulverizerMillBlock> CODEC = createCodec(PulverizerMillBlock::new);
    public static final IntProperty HALF = IntProperty.of("half", 1, 2);

    public PulverizerMillBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(HALF, 1).with(ON_OFF_PWR, OnOffPwr.OFF));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return state.get(HALF) == 1 ? ModBlockEntityTypes.PULVERIZER_MILL.instantiate(pos, state) : null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return state.get(ON_OFF_PWR) != OnOffPwr.OFF && state.get(HALF) == 1? TickableBlockEntity.getTicker(world) : null;
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {return CODEC;}

    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == getDirectionTowardsOtherPart(state.get(HALF), state.get(FACING))) {
            return neighborState.isOf(this) && !Objects.equals(neighborState.get(HALF), state.get(HALF)) ? state.with(ON_OFF_PWR, neighborState.get(ON_OFF_PWR)).with(FACING, neighborState.get(FACING)) : Blocks.AIR.getDefaultState();
        } else {
            return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
        }
    }

    private static Direction getDirectionTowardsOtherPart(int half, Direction direction) {
        return half == 1 ? direction : direction.getOpposite();
    }

    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient && player.isCreative()) {
            int half = state.get(HALF);
            if (half == 1) {
                BlockPos blockPos = pos.offset(getDirectionTowardsOtherPart(half, (Direction)state.get(FACING)));
                BlockState blockState = world.getBlockState(blockPos);
                if (blockState.isOf(this) && blockState.get(HALF) == 2) {
                    world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL | Block.SKIP_DROPS);
                    world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, blockPos, Block.getRawIdFromState(blockState));
                }
            }
        }

        return super.onBreak(world, pos, state, player);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock() && state.get(HALF) == 1) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof RollerMillBlockEntity rollerMillBlockEntity) {
                ItemScatterer.spawn(world, pos, rollerMillBlockEntity.getInventory());
                world.updateComparators(pos, this);
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getHorizontalPlayerFacing();
        BlockPos blockPos = ctx.getBlockPos();
        BlockPos blockPos2 = blockPos.offset(direction);
        World world = ctx.getWorld();
        return world.getBlockState(blockPos2).canReplace(ctx) && world.getWorldBorder().contains(blockPos2) ? (BlockState)this.getDefaultState().with(FACING, direction) : null;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF, ON_OFF_PWR);
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (!world.isClient) {
            BlockPos blockPos = pos.offset((Direction)state.get(FACING));
            world.setBlockState(blockPos, (BlockState)state.with(HALF, 2), Block.NOTIFY_ALL);
            world.updateNeighbors(pos, Blocks.AIR);
            state.updateNeighbors(world, pos, Block.NOTIFY_ALL);
        }
    }

    @Override
    public boolean acceptsPower(World world, BlockPos thisPos, Direction sideFrom) {
        BlockState state = world.getBlockState(thisPos);
        return state.getProperties().contains(ON_OFF_PWR) && state.get(HALF) == 1 && sideFrom.getAxis() != state.get(FACING).getAxis();
    }

    @Override
    public void receivePower(World world, BlockPos thisPos, Direction sideFrom, int amount)
    {
        // Schedules to stop
        world.setBlockState(thisPos, world.getBlockState(thisPos).with(ON_OFF_PWR, ON));
        world.scheduleBlockTick(thisPos, this, 10);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        if (state.getBlock() != this) {
            return;
        }
        if (state.get(ON_OFF_PWR) == SCHEDULED_STOP)
            world.setBlockState(pos, state.with(ON_OFF_PWR, OFF));
        else if (state.get(ON_OFF_PWR) == ON){
            world.scheduleBlockTick(pos, this, 10);
            world.setBlockState(pos, state.with(ON_OFF_PWR, SCHEDULED_STOP));
        }
    }


}
