package com.github.ilja615.iljatech.blocks.conveyorbelt;

import com.github.ilja615.iljatech.blocks.rollermill.RollerMillBlock;
import com.github.ilja615.iljatech.energy.MechPwrAccepter;
import com.github.ilja615.iljatech.energy.MechPwrSender;
import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static com.github.ilja615.iljatech.energy.MechPwrAccepter.OnOffPwr.*;

public class ConveyorBeltBlock extends HorizontalDirectionalBlock implements EntityBlock, MechPwrAccepter, MechPwrSender {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty CONVEYOR_BELT_STATE = EnumProperty.create("type", ConveyorBeltState.class);

    protected static final VoxelShape TOP_SHAPE = Block.box(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape BOTTOM_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public ConveyorBeltBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(ON_OFF_PWR, OFF)
                .setValue(CONVEYOR_BELT_STATE, ConveyorBeltState.NORMAL)
                .setValue(POWERED, false));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntityTypes.CONVEYOR_BELT.create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return TickableBlockEntity.getTicker(world);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        switch (state.getValue(CONVEYOR_BELT_STATE)) {
            case ConveyorBeltState.NORMAL, ConveyorBeltState.DIAGONAL:
            default:
                return Shapes.block();
            case ConveyorBeltState.BOTTOM_SLAB:
                return BOTTOM_SHAPE;
            case ConveyorBeltState.TOP_SLAB:
                return TOP_SHAPE;
        }
    }

    @Override
    protected com.mojang.serialization.MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return null;
    }

    public enum ConveyorBeltState implements StringRepresentable {
        NORMAL("normal"),
        DIAGONAL("diagonal"),
        TOP_SLAB("top_slab"),
        BOTTOM_SLAB("bottom_slab");

        private final String name;

        private ConveyorBeltState(final String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ON_OFF_PWR, CONVEYOR_BELT_STATE, POWERED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(POWERED, ctx.getLevel().hasNeighborSignal(ctx.getClickedPos()));
    }

    protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClientSide) {
            boolean bl = state.getValue(POWERED);
            if (bl != world.hasNeighborSignal(pos)) {
                if (bl) {
                    world.scheduleTick(pos, this, 4);
                } else {
                    world.setBlock(pos, state.cycle(POWERED), Block.UPDATE_CLIENTS);
                }
            }

        }
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, placer, itemStack);

        Direction facing = world.getBlockState(pos).getValue(FACING);
//        BlockState state1 = world.getBlockState(pos.down());

        BlockState state1 = world.getBlockState(pos.relative(facing.getOpposite()).below());
        if (!state1.is(ModBlocks.CONVEYOR_BELT))
            return;
        BlockState state2 = world.getBlockState(pos.relative(facing.getOpposite(), 2).below());
        if (!state2.is(ModBlocks.CONVEYOR_BELT))
            return;
        if (!state1.getValue(CONVEYOR_BELT_STATE).equals(ConveyorBeltState.NORMAL))
            return;
        if (!(state2.getValue(CONVEYOR_BELT_STATE).equals(ConveyorBeltState.NORMAL) || state2.getValue(CONVEYOR_BELT_STATE).equals(ConveyorBeltState.DIAGONAL)))
            return;

        world.setBlock(pos.relative(facing.getOpposite()).below(), state1.setValue(CONVEYOR_BELT_STATE, ConveyorBeltState.TOP_SLAB).setValue(FACING, facing), Block.UPDATE_ALL | Block.UPDATE_KNOWN_SHAPE);
        world.setBlock(pos.relative(facing.getOpposite()), state1.setValue(CONVEYOR_BELT_STATE, ConveyorBeltState.BOTTOM_SLAB).setValue(FACING, facing), Block.UPDATE_ALL | Block.UPDATE_KNOWN_SHAPE);
        world.setBlock(pos, state.setValue(CONVEYOR_BELT_STATE, ConveyorBeltState.DIAGONAL).setValue(FACING, facing), Block.UPDATE_ALL | Block.UPDATE_KNOWN_SHAPE);
        if (world.getBlockState(pos.below()).is(ModBlocks.CONVEYOR_BELT))
            world.setBlock(pos.below(), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
//        world.setBlockState(pos.down(), state1.with(CONVEYOR_BELT_STATE, ConveyorBeltState.TOP_SLAB), Block.NOTIFY_ALL);
//        world.setBlockState(pos, state.with(CONVEYOR_BELT_STATE, ConveyorBeltState.BOTTOM_SLAB), Block.NOTIFY_ALL);
    }

    @Override
    public void receivePower(Level world, BlockPos thisPos, Direction sideFrom, int amount)
    {
        // Schedules to relay the power after right away receiving it
        world.scheduleTick(thisPos, this, 1);
        MechPwrAccepter.super.receivePower(world, thisPos, sideFrom, amount);
    }

    @Override
    public boolean acceptsPower(Level world, BlockPos thisPos, Direction sideFrom)
    {
        // Can not be active if there is no space above
        boolean isBlocked = world.getBlockState(thisPos.above()).isCollisionShapeFullBlock(world, thisPos.above());
        // Can not accept power from its facing direction
        BlockState state = world.getBlockState(thisPos);
        return (!isBlocked && state.getProperties().contains(FACING) && state.getValue(FACING) != sideFrom && state.getProperties().contains(ON_OFF_PWR));
    }

    @Override
    protected void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        super.tick(state, world, pos, random);
        if (state.getBlock() != this) { return; }

        if (state.getValue(POWERED) && !world.hasNeighborSignal(pos)) {
            world.setBlock(pos, state.cycle(POWERED), Block.UPDATE_CLIENTS);
            state = state.cycle(POWERED);
        }

        // Check if a stop was scheduled and then stop
        if (state.getValue(ON_OFF_PWR) == SCHEDULED_STOP) {
            world.setBlockAndUpdate(pos, state.setValue(ON_OFF_PWR, OFF));
        } else if (state.getValue(ON_OFF_PWR) == ON) {
            Direction facing = state.getValue(FACING);
            if (state.getValue(POWERED))
                facing = facing.getOpposite();

            // Top slab should just relay upwards
            if (state.getValue(CONVEYOR_BELT_STATE) == ConveyorBeltState.TOP_SLAB) {
                Block otherHalf = world.getBlockState(pos.relative(Direction.UP)).getBlock();
                if (otherHalf instanceof ConveyorBeltBlock && ((MechPwrAccepter)otherHalf).acceptsPower(world, pos.relative(Direction.UP), Direction.DOWN)) {
                    sendPower(world, pos, Direction.UP, 1);
                    return;
                }
            }

            // Activate the machine above
            Block upBlock = world.getBlockState(pos.above()).getBlock();
            if (upBlock instanceof RollerMillBlock) {
                if (world.getBlockState(pos.above()).getValue(FACING) != facing)
                    // Align
                    world.setBlockAndUpdate(pos.above(), world.getBlockState(pos.above()).setValue(FACING, facing));
                if (((MechPwrAccepter)upBlock).acceptsPower(world, pos.above(), Direction.DOWN))
                    sendPower(world, pos, Direction.UP, 1);
            }

            // Send power to the conveyor belt after this one
            BlockState other = world.getBlockState(pos.relative(facing));
            if (other.is(ModBlocks.CONVEYOR_BELT) && ((ConveyorBeltBlock)other.getBlock()).acceptsPower(world, pos.relative(facing), facing.getOpposite())) {
                sendPower(world, pos, facing, 1);
            } else {
                // There was nowhere to relay power to...
                // Scheduling to stop
                world.setBlockAndUpdate(pos, state.setValue(ON_OFF_PWR, SCHEDULED_STOP));
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
            world.setBlockAndUpdate(thisPos, state.setValue(ON_OFF_PWR, SCHEDULED_STOP));
            world.scheduleTick(thisPos, this, 10);
            return true;
        } else {
            return false;
        }
    }

    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    protected int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof ConveyorBeltBlockEntity conveyorBeltBlockEntity) {
            int n = 0;
            for (Pair<ItemStack, Vec3> pair : conveyorBeltBlockEntity.getStacks()) {
                n += pair.getFirst().getCount();
            }
            return Math.min(n, 15);
        }
        return 0;
    }

    protected boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        if (state.getValue(CONVEYOR_BELT_STATE) != ConveyorBeltState.BOTTOM_SLAB) {
            return super.canSurvive(state, world, pos);
        } else {
            BlockState blockState = world.getBlockState(pos.below());
            return blockState.is(this) && blockState.getValue(CONVEYOR_BELT_STATE) == ConveyorBeltState.TOP_SLAB;
        }
    }

    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (!world.isClientSide) {
            if (player.isCreative()) {
                onBreakInCreative(world, pos, state, player);
            } else {
                dropResources(state, world, pos, (BlockEntity)null, player, player.getMainHandItem());
                ConveyorBeltState conveyorBeltState = (ConveyorBeltState) state.getValue(CONVEYOR_BELT_STATE);
                if (conveyorBeltState == ConveyorBeltState.BOTTOM_SLAB) {
                    BlockPos blockPos = pos.below();
                    BlockState blockState = world.getBlockState(blockPos);
                    if (blockState.is(state.getBlock()) && blockState.getValue(CONVEYOR_BELT_STATE) == ConveyorBeltState.TOP_SLAB) {
                        dropResources(blockState, world, blockPos, (BlockEntity)null, player, player.getMainHandItem());
                        BlockState blockState2 = blockState.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                        world.setBlock(blockPos, blockState2, Block.UPDATE_ALL);
                        world.levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, blockPos, Block.getId(blockState));
                    }
                }
                if (conveyorBeltState == ConveyorBeltState.TOP_SLAB) {
                    BlockPos blockPos = pos.above();
                    BlockState blockState = world.getBlockState(blockPos);
                    if (blockState.is(state.getBlock()) && blockState.getValue(CONVEYOR_BELT_STATE) == ConveyorBeltState.BOTTOM_SLAB) {
                        dropResources(blockState, world, blockPos, (BlockEntity)null, player, player.getMainHandItem());
                        BlockState blockState2 = blockState.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                        world.setBlock(blockPos, blockState2, Block.UPDATE_ALL);
                        world.levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, blockPos, Block.getId(blockState));
                    }
                }
            }
        }

        return super.playerWillDestroy(world, pos, state, player);
    }

    public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(world, player, pos, Blocks.AIR.defaultBlockState(), blockEntity, tool);
    }

    /**
     * Destroys a bottom half of a tall double block (such as a plant or a door)
     * without dropping an item when broken in creative.
     *
     * @see Block#playerWillDestroy(Level, BlockPos, BlockState, Player)
     */
    protected static void onBreakInCreative(Level world, BlockPos pos, BlockState state, Player player) {
        ConveyorBeltState conveyorBeltState = (ConveyorBeltState) state.getValue(CONVEYOR_BELT_STATE);
        if (conveyorBeltState == ConveyorBeltState.BOTTOM_SLAB) {
            BlockPos blockPos = pos.below();
            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.is(state.getBlock()) && blockState.getValue(CONVEYOR_BELT_STATE) == ConveyorBeltState.TOP_SLAB) {
                BlockState blockState2 = blockState.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                world.setBlock(blockPos, blockState2, Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
                world.levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, blockPos, Block.getId(blockState));
            }
        }
        if (conveyorBeltState == ConveyorBeltState.TOP_SLAB) {
            BlockPos blockPos = pos.above();
            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.is(state.getBlock()) && blockState.getValue(CONVEYOR_BELT_STATE) == ConveyorBeltState.BOTTOM_SLAB) {
                BlockState blockState2 = blockState.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                world.setBlock(blockPos, blockState2, Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
                world.levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, blockPos, Block.getId(blockState));
            }
        }
    }
}
