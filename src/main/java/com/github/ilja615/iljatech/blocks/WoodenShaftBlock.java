package com.github.ilja615.iljatech.blocks;

import com.github.ilja615.iljatech.energy.MechPwrAccepter;
import com.github.ilja615.iljatech.energy.MechPwrSender;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WoodenShaftBlock extends Block implements MechPwrAccepter, MechPwrSender
{
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    protected static final VoxelShape Y_SHAPE = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
    protected static final VoxelShape Z_SHAPE = Block.box(6.0, 6.0, 0.0, 10.0, 10.0, 16.0);
    protected static final VoxelShape X_SHAPE = Block.box(0.0, 6.0, 6.0, 16.0, 10.0, 10.0);

    public WoodenShaftBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(MECH_PWR, 0).setValue(FACING, Direction.UP).setValue(SCHEDULE_STOP, false));
    }

    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        switch ((state.getValue(FACING)).getAxis()) {
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
        BlockState state = world.getBlockState(thisPos);
        if (state.getProperties().contains(FACING) && state.getProperties().contains(MECH_PWR)) {
            if (state.getValue(FACING) == sideFrom) {
                return true;
            }
            if (state.getValue(FACING) == sideFrom.getOpposite() && state.getValue(MECH_PWR) == 0 && !state.getValue(SCHEDULE_STOP)) {
                world.setBlockAndUpdate(thisPos, state.setValue(FACING, sideFrom));
                return true;
            }
        }
        return false;
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
            // Output to the other side
            Direction dir = state.getValue(FACING).getOpposite();
            Block other = world.getBlockState(pos.relative(dir)).getBlock();

            if (other instanceof MechPwrAccepter && ((MechPwrAccepter)other).acceptsPower(world, pos.relative(dir), dir.getOpposite())) {
                sendPower(world, pos, dir, state.getValue(MECH_PWR));
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
}
