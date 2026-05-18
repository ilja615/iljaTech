package com.github.ilja615.iljatech.blocks;

import com.github.ilja615.iljatech.energy.MechPwrAccepter;
import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.List;

import static com.github.ilja615.iljatech.energy.MechPwrAccepter.OnOffPwr.*;

public class DrillBlock extends Block implements MechPwrAccepter {
    public static final EnumProperty<Direction> FACING = Properties.FACING;

    public DrillBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(ON_OFF_PWR, OFF).with(FACING, Direction.UP));
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getSide());
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return (WallMountedBlock.canPlaceAt(world, pos, state.get(FACING).getOpposite())
        || world.getBlockState(pos.offset(state.get(FACING).getOpposite())).isOf(Blocks.HONEY_BLOCK));
    }

    @Override
    protected BlockState getStateForNeighborUpdate(
            BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
    ) {        return state.get(FACING) == direction && !state.canPlaceAt(world, pos)
            ? Blocks.AIR.getDefaultState()
            : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (notify) {
            world.setBlockState(pos, state.with(ON_OFF_PWR, SCHEDULED_STOP));
            world.scheduleBlockTick(pos, this, 10);
            if (!world.isClient) {
                mine((ServerWorld) world, pos);
            }
        }
        super.onBlockAdded(state, world, pos, oldState, notify);
    }

    private void mine(ServerWorld world, BlockPos thisPos) {
        BlockState thisState = world.getBlockState(thisPos);
        if (thisState.getBlock() instanceof DrillBlock) {
            BlockPos miningPos = thisPos.offset(thisState.get(FACING));
            if (miningPos.getY() > world.getBottomY() && miningPos.getY() < world.getTopY() && world.getWorldBorder().contains(miningPos)) {
                BlockState state = world.getBlockState(miningPos);
                if (!state.isAir() && state.getHardness(world, miningPos) >= 0 && !state.isIn(BlockTags.INCORRECT_FOR_IRON_TOOL)) {
                    List<ItemStack> drops = state.getDroppedStacks(
                            new LootContextParameterSet.Builder(world)
                                    .add(LootContextParameters.TOOL, Items.IRON_PICKAXE.getDefaultStack())
                                    .add(LootContextParameters.ORIGIN, miningPos.toCenterPos())
                    );
                    world.breakBlock(miningPos, false);
                    if (!drops.isEmpty()) {
                        for (ItemStack drop : drops) {
                            ItemScatterer.spawn(world, miningPos.getX()+0.5D, miningPos.getY()+0.5D, miningPos.getZ()+0.5D, drop);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void receivePower(World world, BlockPos thisPos, Direction sideFrom, int amount)
    {
        // Schedules to stop
        world.setBlockState(thisPos, world.getBlockState(thisPos).with(ON_OFF_PWR, ON));
        world.scheduleBlockTick(thisPos, this, 10);
        if (!world.isClient) {
            mine((ServerWorld) world, thisPos);
        };
    }

    @Override
    public boolean acceptsPower(World world, BlockPos thisPos, Direction sideFrom)
    {
        // Drill can only accept power from the back and not when it's already on
        BlockState state = world.getBlockState(thisPos);
        return (state.getProperties().contains(FACING) && state.get(FACING).getOpposite() == sideFrom &&
                state.getProperties().contains(ON_OFF_PWR));
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
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, ON_OFF_PWR);
    }
}
