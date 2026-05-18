package com.github.ilja615.iljatech.blocks.rollermill;

import com.github.ilja615.iljatech.energy.MechPwrAccepter;
import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import net.minecraft.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import static com.github.ilja615.iljatech.energy.MechPwrAccepter.OnOffPwr.*;
import static com.github.ilja615.iljatech.energy.MechPwrAccepter.OnOffPwr.SCHEDULED_STOP;

public class RollerMillBlock extends Block implements EntityBlock, MechPwrAccepter {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    protected static final VoxelShape SIDE_SHAPE_1_X = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 0.01);
    protected static final VoxelShape SIDE_SHAPE_2_X = Block.box(0.0, 0.0, 15.99, 16.0, 16.0, 16.0);
    protected static final VoxelShape SIDE_SHAPE_1_Z = Block.box(0.0, 0.0, 0.0, 0.01, 16.0, 16.0);
    protected static final VoxelShape SIDE_SHAPE_2_Z = Block.box(15.99, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape ROLLER_1_X = Block.box(5.0, 2.0, 0.5, 11.0, 8.0, 15.5);
    protected static final VoxelShape ROLLER_2_X = Block.box(5.0, 10.0, 0.5, 11.0, 16.0, 15.5);
    protected static final VoxelShape ROLLER_1_Z = Block.box(0.5, 2.0, 5.0, 15.5, 8.0, 11.0);
    protected static final VoxelShape ROLLER_2_Z = Block.box(0.5, 10.0, 5.0, 15.5, 16.0, 11.0);
    private static final VoxelShape X_AXIS_SHAPE = Shapes.or(SIDE_SHAPE_1_X, SIDE_SHAPE_2_X, ROLLER_1_X, ROLLER_2_X);
    private static final VoxelShape Z_AXIS_SHAPE = Shapes.or(SIDE_SHAPE_1_Z, SIDE_SHAPE_2_Z, ROLLER_1_Z, ROLLER_2_Z);

    public RollerMillBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(ON_OFF_PWR, OFF).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (!world.isClientSide)
        {
            if (world.getBlockEntity(pos) instanceof RollerMillBlockEntity blockEntity && player != null) {
                //player.sendMessage(Text.of("ticks: " + blockEntity.getTicks()), true);
                return InteractionResult.SUCCESS;
            }
        }
        return super.useWithoutItem(state, world, pos, player, hit);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntityTypes.ROLLER_MILL.create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return state.getValue(ON_OFF_PWR) != OnOffPwr.OFF ? TickableBlockEntity.getTicker(world) : null;
    }

    @Override
    public void receivePower(Level world, BlockPos thisPos, Direction sideFrom, int amount)
    {
        // Schedules to stop
        world.setBlockAndUpdate(thisPos, world.getBlockState(thisPos).setValue(ON_OFF_PWR, ON));
        world.scheduleTick(thisPos, this, 10);
    }

    @Override
    public boolean acceptsPower(Level world, BlockPos thisPos, Direction sideFrom) {
        return world.getBlockState(thisPos).getProperties().contains(ON_OFF_PWR);
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

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof RollerMillBlockEntity rollerMillBlockEntity) {
                Containers.dropContents(world, pos, rollerMillBlockEntity.getInventory());
                world.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, world, pos, newState, moved);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        return direction.getAxis() == Direction.Axis.X ? X_AXIS_SHAPE : Z_AXIS_SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ON_OFF_PWR);
    }
}
