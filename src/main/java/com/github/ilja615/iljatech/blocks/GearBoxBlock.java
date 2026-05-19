package com.github.ilja615.iljatech.blocks;

import com.github.ilja615.iljatech.energy.MechPwrAccepter;
import com.github.ilja615.iljatech.energy.MechPwrSender;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class GearBoxBlock extends Block implements MechPwrAccepter, MechPwrSender {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public GearBoxBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(MECH_PWR, 0).setValue(FACING, Direction.UP).setValue(SCHEDULE_STOP, false));
    }

    @Override
    protected void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        if (notify) {
            world.setBlockAndUpdate(pos, state.setValue(MECH_PWR, 0));
        }
        super.onPlace(state, world, pos, oldState, notify);
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        if (ctx.getPlayer().isShiftKeyDown()) {
            return this.defaultBlockState().setValue(FACING, ctx.getNearestLookingDirection().getOpposite());
        } else {
            return this.defaultBlockState().setValue(FACING, ctx.getClickedFace().getOpposite());
        }
    }

    @Override
    public void receivePower(Level world, BlockPos thisPos, Direction sideFrom, int amount)
    {
        // Gearbox schedules to transfer the power after receiving it
        world.scheduleTick(thisPos, this, 10);
        MechPwrAccepter.super.receivePower(world, thisPos, sideFrom, amount);
    }

    @Override
    public boolean acceptsPower(Level world, BlockPos thisPos, Direction sideFrom)
    {
        // Gearbox can only accept power from its input facing direction
        BlockState state = world.getBlockState(thisPos);
        return (state.getProperties().contains(FACING) && state.getValue(FACING) == sideFrom && state.getProperties().contains(MECH_PWR));
    }

    @Override
    protected void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        super.tick(state, world, pos, random);
        if (state.getBlock() != this) { return; }

        // Check if a stop was scheduled and then stop
        if (state.getValue(SCHEDULE_STOP)) {
            world.setBlockAndUpdate(pos, state.setValue(MECH_PWR, 0).setValue(SCHEDULE_STOP, false));
        }
        else if (state.getValue(MECH_PWR) > 0) {
            // Collect a list of all possible power output directions
            ArrayList<Direction> directions = new ArrayList<Direction>(); // Potential directions that power could be outputted to.
            for (Direction dir : Direction.values()) {
                if (dir != state.getValue(FACING)) { // A gearbox can not output to its "input" side.
                    Block other = world.getBlockState(pos.relative(dir)).getBlock();
                    if (other instanceof MechPwrAccepter && ((MechPwrAccepter)other).acceptsPower(world, pos.relative(dir), dir.getOpposite()))
                        directions.add(dir);
                }
            }
            if (!directions.isEmpty()) {
                // Split this block's power to all the listed outputs
                int splitPower = (int) Math.floor(state.getValue(MECH_PWR) / ((double) directions.size()));
                if (splitPower > 0.0d)
                    directions.forEach(direction -> sendPower(world, pos, direction, splitPower));
                else {
                    // Insufficient power, could not output...
                    // Scheduling to stop
                    world.setBlockAndUpdate(pos, state.setValue(SCHEDULE_STOP, true));
                    world.scheduleTick(pos, this, 10);
                }
            } else {
                // There was nowhere to output to...
                // Scheduling to stop
                world.setBlockAndUpdate(pos, state.setValue(SCHEDULE_STOP, true));
                world.scheduleTick(pos, this, 10);
            }
        }
    }

    @Override
    public boolean sendPower(Level world, BlockPos thisPos, Direction face, int amount)
    {
        BlockState state = world.getBlockState(thisPos);
        if (MechPwrSender.super.sendPower(world, thisPos, face, amount))
        {
            // The gearbox sent its power and should schedule to stop now
            world.setBlockAndUpdate(thisPos, state.setValue(SCHEDULE_STOP, true));
            world.scheduleTick(thisPos, this, 10);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, MECH_PWR, SCHEDULE_STOP);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        return Math.min(15,state.getValue(MECH_PWR));
    }
}
