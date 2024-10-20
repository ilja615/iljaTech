package com.github.ilja615.iljatech.blocks.rollermill;

import com.github.ilja615.iljatech.energy.MechPwrAccepter;
import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static com.github.ilja615.iljatech.energy.MechPwrAccepter.OnOffPwr.*;
import static com.github.ilja615.iljatech.energy.MechPwrAccepter.OnOffPwr.SCHEDULED_STOP;

public class RollerMillBlock extends Block implements BlockEntityProvider, MechPwrAccepter {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    public RollerMillBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(ON_OFF_PWR, OFF).with(FACING, Direction.NORTH));
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient)
        {
            if (world.getBlockEntity(pos) instanceof RollerMillBlockEntity blockEntity && player != null) {
                player.sendMessage(Text.of(
                        "ticks: " + blockEntity.getTicks() +
                        ", item0: " + blockEntity.getInventory().getStack(0).toString() +
                        ", item1: " + blockEntity.getInventory().getStack(1).toString()), true);
                return ActionResult.SUCCESS;
            }
        }
        return super.onUse(state, world, pos, player, hit);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntityTypes.ROLLER_MILL.instantiate(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return state.get(ON_OFF_PWR) != OnOffPwr.OFF ? TickableBlockEntity.getTicker(world) : null;
    }

    @Override
    public void receivePower(World world, BlockPos thisPos, Direction sideFrom, int amount)
    {
        // Schedules to stop
        world.setBlockState(thisPos, world.getBlockState(thisPos).with(ON_OFF_PWR, ON));
        world.scheduleBlockTick(thisPos, this, 10);
    }

    @Override
    public boolean acceptsPower(World world, BlockPos thisPos, Direction sideFrom) {
        return world.getBlockState(thisPos).getProperties().contains(ON_OFF_PWR);
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

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof RollerMillBlockEntity rollerMillBlockEntity) {
                ItemScatterer.spawn(world, pos, rollerMillBlockEntity.getInventory());
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, ON_OFF_PWR);
    }
}
