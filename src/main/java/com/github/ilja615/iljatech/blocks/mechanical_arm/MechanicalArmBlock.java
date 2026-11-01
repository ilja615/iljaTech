package com.github.ilja615.iljatech.blocks.mechanical_arm;

import com.github.ilja615.iljatech.energy.MechPwrAccepter;
import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import static com.github.ilja615.iljatech.energy.MechPwrAccepter.OnOffPwr.*;

public class MechanicalArmBlock extends HorizontalDirectionalBlock implements EntityBlock, MechPwrAccepter {

    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

    public MechanicalArmBlock(BlockBehaviour.Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return null;
    }


    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if(!world.isClientSide) {
            if(world.getBlockEntity(pos) instanceof MechanicalArmBlockEntity mechanicalArmBlockEntity) {
                player.openMenu(mechanicalArmBlockEntity);
            }
        }

        return InteractionResult.sidedSuccess(world.isClientSide);
    }

//    @Nullable
//    @Override
//    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
//        return TickableBlockEntity.getTicker(world);
//    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntityTypes.MECHANICAL_ARM.create(pos, state);
    }

    @Override
    protected void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof MechanicalArmBlockEntity mechanicalArmBlockEntity) {
                Containers.dropContents(world, pos, mechanicalArmBlockEntity.getInventory());
                world.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, world, pos, newState, moved);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ON_OFF_PWR, TRIGGERED);
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
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean moving) {
        boolean powered = level.hasNeighborSignal(pos);
        boolean triggered = state.getValue(TRIGGERED);

        if (powered && !triggered) {
            level.setBlock(pos, state.setValue(TRIGGERED, true), 2);
            activate(level, pos);
        } else if (!powered && triggered) {
            level.setBlock(pos, state.setValue(TRIGGERED, false), 2);
        }
    }

    protected void activate(Level level, BlockPos pos) {
        if (level.isClientSide)
            return;

        ServerLevel serverLevel = (ServerLevel) level;
        Direction direction = level.getBlockState(pos).getValue(FACING);
        BlockPos facingPos = pos.relative(direction);
        BlockState stateAtPos = serverLevel.getBlockState(facingPos);

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof MechanicalArmBlockEntity mechanicalArmBlockEntity))
            return;

        FakePlayer p = getFakePlayer(serverLevel);
        p.setGameMode(GameType.SURVIVAL);
        p.setItemInHand(InteractionHand.MAIN_HAND, mechanicalArmBlockEntity.getInventory().getItem(0));

        if (stateAtPos.getShape(serverLevel, facingPos).isEmpty()) {
            BlockHitResult hitResult = serverLevel.clip(new ClipContext(pos.getCenter(), facingPos.getCenter(), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, p));
            hitResult = new BlockHitResult(hitResult.getLocation(), hitResult.getDirection(), facingPos, hitResult.isInside());

            p.gameMode.useItemOn(p, serverLevel, p.getItemInHand(InteractionHand.MAIN_HAND), InteractionHand.MAIN_HAND, hitResult);
        } else {
            Vec3 hitLocation = Vec3.atCenterOf(facingPos).relative(direction.getOpposite(), 0.5);
            BlockHitResult hitResult = new BlockHitResult(hitLocation, direction.getOpposite(), facingPos, false);

            p.gameMode.useItemOn(p, serverLevel, p.getItemInHand(InteractionHand.MAIN_HAND), InteractionHand.MAIN_HAND, hitResult);
        }

        // put resulting stack back into the inventory
        mechanicalArmBlockEntity.getInventory().setItem(0, p.getItemInHand(InteractionHand.MAIN_HAND));
    }

    private FakePlayer getFakePlayer(Level level) {
        if (level.isClientSide) {
            return null;
        }

        return FakePlayer.get((ServerLevel) level);
    }
}