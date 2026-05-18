package com.github.ilja615.iljatech.blocks.conveyorbelt;

import com.github.ilja615.iljatech.blocks.rollermill.RollerMillBlock;
import com.github.ilja615.iljatech.energy.MechPwrAccepter;
import com.github.ilja615.iljatech.energy.MechPwrSender;
import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static com.github.ilja615.iljatech.energy.MechPwrAccepter.OnOffPwr.*;

public class ConveyorBeltBlock extends HorizontalFacingBlock implements BlockEntityProvider, MechPwrAccepter, MechPwrSender {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty CONVEYOR_BELT_STATE = EnumProperty.of("type", ConveyorBeltState.class);

    protected static final VoxelShape TOP_SHAPE = Block.createCuboidShape(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape BOTTOM_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    public static final BooleanProperty POWERED = Properties.POWERED;

    public ConveyorBeltBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(ON_OFF_PWR, OFF)
                .with(CONVEYOR_BELT_STATE, ConveyorBeltState.NORMAL)
                .with(POWERED, false));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntityTypes.CONVEYOR_BELT.instantiate(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return TickableBlockEntity.getTicker(world);
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
        builder.add(FACING, ON_OFF_PWR, CONVEYOR_BELT_STATE, POWERED);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite()).with(POWERED, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()));
    }

    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient) {
            boolean bl = state.get(POWERED);
            if (bl != world.isReceivingRedstonePower(pos)) {
                if (bl) {
                    world.scheduleBlockTick(pos, this, 4);
                } else {
                    world.setBlockState(pos, state.cycle(POWERED), Block.NOTIFY_LISTENERS);
                }
            }

        }
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
    }

    @Override
    public boolean acceptsPower(World world, BlockPos thisPos, Direction sideFrom)
    {
        // Can not be active if there is no space above
        boolean isBlocked = world.getBlockState(thisPos.up()).isFullCube(world, thisPos.up());
        // Can not accept power from its facing direction
        BlockState state = world.getBlockState(thisPos);
        return (!isBlocked && state.getProperties().contains(FACING) && state.get(FACING) != sideFrom && state.getProperties().contains(ON_OFF_PWR));
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        if (state.getBlock() != this) { return; }

        if (state.get(POWERED) && !world.isReceivingRedstonePower(pos)) {
            world.setBlockState(pos, state.cycle(POWERED), Block.NOTIFY_LISTENERS);
            state = state.cycle(POWERED);
        }

        // Check if a stop was scheduled and then stop
        if (state.get(ON_OFF_PWR) == SCHEDULED_STOP) {
            world.setBlockState(pos, state.with(ON_OFF_PWR, OFF));
        } else if (state.get(ON_OFF_PWR) == ON) {
            Direction facing = state.get(FACING);
            if (state.get(POWERED))
                facing = facing.getOpposite();

            // Top slab should just relay upwards
            if (state.get(CONVEYOR_BELT_STATE) == ConveyorBeltState.TOP_SLAB) {
                Block otherHalf = world.getBlockState(pos.offset(Direction.UP)).getBlock();
                if (otherHalf instanceof ConveyorBeltBlock && ((MechPwrAccepter)otherHalf).acceptsPower(world, pos.offset(Direction.UP), Direction.DOWN)) {
                    sendPower(world, pos, Direction.UP, 1);
                    return;
                }
            }

            // Activate the machine above
            Block upBlock = world.getBlockState(pos.up()).getBlock();
            if (upBlock instanceof RollerMillBlock) {
                if (world.getBlockState(pos.up()).get(FACING) != facing)
                    // Align
                    world.setBlockState(pos.up(), world.getBlockState(pos.up()).with(FACING, facing));
                if (((MechPwrAccepter)upBlock).acceptsPower(world, pos.up(), Direction.DOWN))
                    sendPower(world, pos, Direction.UP, 1);
            }

            // Send power to the conveyor belt after this one
            BlockState other = world.getBlockState(pos.offset(facing));
            if (other.isOf(ModBlocks.CONVEYOR_BELT) && ((ConveyorBeltBlock)other.getBlock()).acceptsPower(world, pos.offset(facing), facing.getOpposite())) {
                sendPower(world, pos, facing, 1);
            } else {
                // There was nowhere to relay power to...
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

    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof ConveyorBeltBlockEntity conveyorBeltBlockEntity) {
            int n = 0;
            for (Pair<ItemStack, Vec3d> pair : conveyorBeltBlockEntity.getStacks()) {
                n += pair.getFirst().getCount();
            }
            return Math.min(n, 15);
        }
        return 0;
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if (state.get(CONVEYOR_BELT_STATE) != ConveyorBeltState.BOTTOM_SLAB) {
            return super.canPlaceAt(state, world, pos);
        } else {
            BlockState blockState = world.getBlockState(pos.down());
            return blockState.isOf(this) && blockState.get(CONVEYOR_BELT_STATE) == ConveyorBeltState.TOP_SLAB;
        }
    }

    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            if (player.isCreative()) {
                onBreakInCreative(world, pos, state, player);
            } else {
                dropStacks(state, world, pos, (BlockEntity)null, player, player.getMainHandStack());
                ConveyorBeltState conveyorBeltState = (ConveyorBeltState) state.get(CONVEYOR_BELT_STATE);
                if (conveyorBeltState == ConveyorBeltState.BOTTOM_SLAB) {
                    BlockPos blockPos = pos.down();
                    BlockState blockState = world.getBlockState(blockPos);
                    if (blockState.isOf(state.getBlock()) && blockState.get(CONVEYOR_BELT_STATE) == ConveyorBeltState.TOP_SLAB) {
                        dropStacks(blockState, world, blockPos, (BlockEntity)null, player, player.getMainHandStack());
                        BlockState blockState2 = blockState.getFluidState().isOf(Fluids.WATER) ? Blocks.WATER.getDefaultState() : Blocks.AIR.getDefaultState();
                        world.setBlockState(blockPos, blockState2, Block.NOTIFY_ALL);
                        world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, blockPos, Block.getRawIdFromState(blockState));
                    }
                }
                if (conveyorBeltState == ConveyorBeltState.TOP_SLAB) {
                    BlockPos blockPos = pos.up();
                    BlockState blockState = world.getBlockState(blockPos);
                    if (blockState.isOf(state.getBlock()) && blockState.get(CONVEYOR_BELT_STATE) == ConveyorBeltState.BOTTOM_SLAB) {
                        dropStacks(blockState, world, blockPos, (BlockEntity)null, player, player.getMainHandStack());
                        BlockState blockState2 = blockState.getFluidState().isOf(Fluids.WATER) ? Blocks.WATER.getDefaultState() : Blocks.AIR.getDefaultState();
                        world.setBlockState(blockPos, blockState2, Block.NOTIFY_ALL);
                        world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, blockPos, Block.getRawIdFromState(blockState));
                    }
                }
            }
        }

        return super.onBreak(world, pos, state, player);
    }

    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.afterBreak(world, player, pos, Blocks.AIR.getDefaultState(), blockEntity, tool);
    }

    /**
     * Destroys a bottom half of a tall double block (such as a plant or a door)
     * without dropping an item when broken in creative.
     *
     * @see Block#onBreak(World, BlockPos, BlockState, PlayerEntity)
     */
    protected static void onBreakInCreative(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        ConveyorBeltState conveyorBeltState = (ConveyorBeltState) state.get(CONVEYOR_BELT_STATE);
        if (conveyorBeltState == ConveyorBeltState.BOTTOM_SLAB) {
            BlockPos blockPos = pos.down();
            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.isOf(state.getBlock()) && blockState.get(CONVEYOR_BELT_STATE) == ConveyorBeltState.TOP_SLAB) {
                BlockState blockState2 = blockState.getFluidState().isOf(Fluids.WATER) ? Blocks.WATER.getDefaultState() : Blocks.AIR.getDefaultState();
                world.setBlockState(blockPos, blockState2, Block.NOTIFY_ALL | Block.SKIP_DROPS);
                world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, blockPos, Block.getRawIdFromState(blockState));
            }
        }
        if (conveyorBeltState == ConveyorBeltState.TOP_SLAB) {
            BlockPos blockPos = pos.up();
            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.isOf(state.getBlock()) && blockState.get(CONVEYOR_BELT_STATE) == ConveyorBeltState.BOTTOM_SLAB) {
                BlockState blockState2 = blockState.getFluidState().isOf(Fluids.WATER) ? Blocks.WATER.getDefaultState() : Blocks.AIR.getDefaultState();
                world.setBlockState(blockPos, blockState2, Block.NOTIFY_ALL | Block.SKIP_DROPS);
                world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, blockPos, Block.getRawIdFromState(blockState));
            }
        }
    }
}
