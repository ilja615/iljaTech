package com.github.ilja615.iljatech.blocks;

import com.github.ilja615.iljatech.energy.MechPwrAccepter;
import com.github.ilja615.iljatech.energy.MechPwrSender;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import java.util.ArrayList;

public class GearBoxBlock extends Block implements MechPwrAccepter, MechPwrSender
{
    public static final DirectionProperty FACING = Properties.FACING;

    public GearBoxBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(MECH_PWR, 0).with(FACING, Direction.UP).with(SCHEDULE_STOP, false));
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (notify) {
            world.setBlockState(pos, state.with(MECH_PWR, 0));
        }
        super.onBlockAdded(state, world, pos, oldState, notify);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        if (ctx.getPlayer().isSneaking()) {
            return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
        } else {
            return this.getDefaultState().with(FACING, ctx.getSide().getOpposite());
        }
    }

    @Override
    public void receivePower(World world, BlockPos thisPos, Direction sideFrom, int amount)
    {
        // Gearbox schedules to transfer the power after receiving it
        world.scheduleBlockTick(thisPos, this, 10);
        MechPwrAccepter.super.receivePower(world, thisPos, sideFrom, amount);
    }

    @Override
    public boolean acceptsPower(World world, BlockPos thisPos, Direction sideFrom)
    {
        // Gearbox can only accept power from its input facing direction
        BlockState state = world.getBlockState(thisPos);
        return (state.getProperties().contains(FACING) && state.get(FACING) == sideFrom && state.getProperties().contains(MECH_PWR));
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        if (state.getBlock() != this) { return; }

        // Check if a stop was scheduled
        if (state.get(SCHEDULE_STOP)) {
            world.setBlockState(pos, state.with(MECH_PWR, 0).with(SCHEDULE_STOP, false));
        }
        else if (state.get(MECH_PWR) > 0) {
            // Collect a list of all possible power output directions
            ArrayList<Direction> directions = new ArrayList<Direction>(); // Potential directions that power could be outputted to.
            for (Direction dir : Direction.values()) {
                if (dir != state.get(FACING)) { // A gearbox can not output to its "input" side.
                    Block other = world.getBlockState(pos.offset(dir)).getBlock();
                    if (other instanceof MechPwrAccepter && ((MechPwrAccepter)other).acceptsPower(world, pos.offset(dir), dir.getOpposite()))
                        directions.add(dir);
                }
            }
            if (!directions.isEmpty()) {
                // Split this block's power to all the listed outputs
                int splitPower = (int) Math.floor(state.get(MECH_PWR) / ((double) directions.size()));
                if (splitPower > 0.0d)
                    directions.forEach(direction -> sendPower(world, pos, direction, splitPower));
                else {
                    // Insufficient power, could not output...
                    // Scheduling to stop
                    world.setBlockState(pos, state.with(SCHEDULE_STOP, true));
                    world.scheduleBlockTick(pos, this, 10);
                }
            } else {
                // There was nowhere to output to...
                // Scheduling to stop
                world.setBlockState(pos, state.with(SCHEDULE_STOP, true));
                world.scheduleBlockTick(pos, this, 10);
            }
        }
    }

    @Override
    public boolean sendPower(World world, BlockPos thisPos, Direction face, int amount)
    {
        BlockState state = world.getBlockState(thisPos);
        if (MechPwrSender.super.sendPower(world, thisPos, face, amount))
        {
            // The gearbox sent its power and should schedule to stop now
            world.setBlockState(thisPos, state.with(SCHEDULE_STOP, true));
            world.scheduleBlockTick(thisPos, this, 10);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, MECH_PWR, SCHEDULE_STOP);
    }

    @Override
    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return Math.min(15,state.get(MECH_PWR));
    }
}
