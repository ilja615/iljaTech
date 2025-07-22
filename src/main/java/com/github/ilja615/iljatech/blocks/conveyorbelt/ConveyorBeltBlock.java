package com.github.ilja615.iljatech.blocks.conveyorbelt;

import com.github.ilja615.iljatech.energy.MechPwrAccepter;
import com.github.ilja615.iljatech.energy.MechPwrSender;
import com.github.ilja615.iljatech.init.ModBlocks;
import net.minecraft.block.*;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

import static com.github.ilja615.iljatech.energy.MechPwrAccepter.OnOffPwr.*;

public class ConveyorBeltBlock extends HorizontalFacingBlock implements MechPwrAccepter, MechPwrSender {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty CONVEYOR_BELT_STATE = EnumProperty.of("type", ConveyorBeltState.class);

    protected static final VoxelShape TOP_SHAPE = Block.createCuboidShape(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape BOTTOM_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    Box ITEM_AREA_SHAPE = (Box)Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 10.0, 16.0).getBoundingBoxes().get(0);

    public ConveyorBeltBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(ON_OFF_PWR, OFF)
                .with(CONVEYOR_BELT_STATE, ConveyorBeltState.NORMAL));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(CONVEYOR_BELT_STATE)) {
            case ConveyorBeltState.NORMAL, ConveyorBeltState.DIAGONAL:
            default:
                return VoxelShapes.fullCube();
            case ConveyorBeltState.BOTTOM_SLAB:
                return BOTTOM_SHAPE;
            case ConveyorBeltState.TOP_SLAB:
                return TOP_SHAPE;
        }
    }

    @Override
    protected com.mojang.serialization.MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return null;
    }

    public enum ConveyorBeltState implements StringIdentifiable {
        NORMAL("normal"),
        DIAGONAL("diagonal"),
        TOP_SLAB("top_slab"),
        BOTTOM_SLAB("bottom_slab");

        private final String name;

        private ConveyorBeltState(final String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, ON_OFF_PWR, CONVEYOR_BELT_STATE);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        Direction facing = world.getBlockState(pos).get(FACING);
//        BlockState state1 = world.getBlockState(pos.down());

        BlockState state1 = world.getBlockState(pos.offset(facing.getOpposite()).down());
        if (!state1.isOf(ModBlocks.CONVEYOR_BELT))
            return;
        BlockState state2 = world.getBlockState(pos.offset(facing.getOpposite(), 2).down());
        if (!state2.isOf(ModBlocks.CONVEYOR_BELT))
            return;
        if (!state1.get(CONVEYOR_BELT_STATE).equals(ConveyorBeltState.NORMAL))
            return;
        if (!(state2.get(CONVEYOR_BELT_STATE).equals(ConveyorBeltState.NORMAL) || state2.get(CONVEYOR_BELT_STATE).equals(ConveyorBeltState.DIAGONAL)))
            return;

        world.setBlockState(pos.offset(facing.getOpposite()).down(), state1.with(CONVEYOR_BELT_STATE, ConveyorBeltState.TOP_SLAB).with(FACING, facing), Block.NOTIFY_ALL | Block.FORCE_STATE);
        world.setBlockState(pos.offset(facing.getOpposite()), state1.with(CONVEYOR_BELT_STATE, ConveyorBeltState.BOTTOM_SLAB).with(FACING, facing), Block.NOTIFY_ALL | Block.FORCE_STATE);
        world.setBlockState(pos, state.with(CONVEYOR_BELT_STATE, ConveyorBeltState.DIAGONAL).with(FACING, facing), Block.NOTIFY_ALL | Block.FORCE_STATE);
        if (world.getBlockState(pos.down()).isOf(ModBlocks.CONVEYOR_BELT))
            world.setBlockState(pos.down(), Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
//        world.setBlockState(pos.down(), state1.with(CONVEYOR_BELT_STATE, ConveyorBeltState.TOP_SLAB), Block.NOTIFY_ALL);
//        world.setBlockState(pos, state.with(CONVEYOR_BELT_STATE, ConveyorBeltState.BOTTOM_SLAB), Block.NOTIFY_ALL);
    }

    @Override
    public void receivePower(World world, BlockPos thisPos, Direction sideFrom, int amount)
    {
        // Schedules to relay the power after right away receiving it
        world.scheduleBlockTick(thisPos, this, 1);
        MechPwrAccepter.super.receivePower(world, thisPos, sideFrom, amount);

        // Push items
        Direction dir = world.getBlockState(thisPos).get(FACING);
        BlockState state1 = world.getBlockState(thisPos.offset(dir));
        boolean up = state1.isOf(ModBlocks.CONVEYOR_BELT) && (state1.get(CONVEYOR_BELT_STATE) == ConveyorBeltState.TOP_SLAB || state1.get(CONVEYOR_BELT_STATE) == ConveyorBeltState.DIAGONAL) && state1.get(FACING) == dir;
        Box box = ITEM_AREA_SHAPE.offset(thisPos.getX(), thisPos.getY() + 0.5d, thisPos.getZ());
        for (ItemEntity itemEntity : world.getEntitiesByClass(ItemEntity.class, box, EntityPredicates.VALID_ENTITY)) {
            itemEntity.move(MovementType.PISTON, Vec3d.of(dir.getVector()).multiply(0.2d));
            if (up && itemEntity.horizontalCollision) {
                itemEntity.move(MovementType.PISTON, Vec3d.of(Direction.UP.getVector()).multiply(0.5d));
                itemEntity.move(MovementType.PISTON, Vec3d.of(dir.getVector()).multiply(0.2d));
            }
        }
    }

    @Override
    public boolean acceptsPower(World world, BlockPos thisPos, Direction sideFrom)
    {
        // Can not accept power from its facing direction
        BlockState state = world.getBlockState(thisPos);
        return (state.getProperties().contains(FACING) && state.get(FACING) != sideFrom && state.getProperties().contains(ON_OFF_PWR));
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        if (state.getBlock() != this) { return; }

        // Check if a stop was scheduled and then stop
        if (state.get(ON_OFF_PWR) == SCHEDULED_STOP) {
            world.setBlockState(pos, state.with(ON_OFF_PWR, OFF));
        }
        else if (state.get(ON_OFF_PWR) == ON) {
            // Relay power to other conveyor belts
            Direction dir = state.get(FACING);
            if (state.get(CONVEYOR_BELT_STATE) == ConveyorBeltState.TOP_SLAB)
                // Top slab should relay upwards
                dir = Direction.UP;

            Block other = world.getBlockState(pos.offset(dir)).getBlock();

            if (other instanceof MechPwrAccepter && ((MechPwrAccepter)other).acceptsPower(world, pos.offset(dir), dir.getOpposite())) {
                // Can only relay to other conveyor belts
                if (other instanceof ConveyorBeltBlock)
                    sendPower(world, pos, dir, 1);
            } else {
                // There was nowhere to output to...
                // Scheduling to stop
                world.setBlockState(pos, state.with(ON_OFF_PWR, SCHEDULED_STOP));
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
            world.setBlockState(thisPos, state.with(ON_OFF_PWR, SCHEDULED_STOP));
            world.scheduleBlockTick(thisPos, this, 10);
            return true;
        } else {
            return false;
        }
    }
}
