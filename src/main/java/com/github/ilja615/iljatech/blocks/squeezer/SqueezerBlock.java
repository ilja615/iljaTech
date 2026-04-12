package com.github.ilja615.iljatech.blocks.squeezer;

import com.github.ilja615.iljatech.energy.MechPwrAccepter;
import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import net.minecraft.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import static com.github.ilja615.iljatech.energy.MechPwrAccepter.OnOffPwr.*;
import static com.github.ilja615.iljatech.energy.MechPwrAccepter.OnOffPwr.SCHEDULED_STOP;

public class SqueezerBlock  extends Block implements EntityBlock, MechPwrAccepter {
    public static final IntegerProperty PRESS = IntegerProperty.create("press", 0, 3);
    protected static final VoxelShape SHAPE_1_BASE = Block.box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0);
    protected static final VoxelShape SHAPE_2_BASE = Block.box(0.0, 0.0, 0.0, 16.0, 11.0, 16.0);
    protected static final VoxelShape ROD_0 = Block.box(0.0, 0.0, 0.0, 2.0, 16.0, 2.0);
    protected static final VoxelShape ROD_1 = Block.box(14.0, 0.0, 0.0, 16.0, 16.0, 2.0);
    protected static final VoxelShape ROD_2 = Block.box(0.0, 0.0, 14.0, 2.0, 16.0, 16.0);
    protected static final VoxelShape ROD_3 = Block.box(14.0, 0.0, 14.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape SHAPE_1 = Shapes.or(ROD_0, ROD_1, ROD_2, ROD_3, SHAPE_1_BASE);
    protected static final VoxelShape SHAPE_2 = Shapes.or(ROD_0, ROD_1, ROD_2, ROD_3, SHAPE_2_BASE);

    public SqueezerBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(PRESS, 0).setValue(ON_OFF_PWR, OFF));
    }

    @Override
    public boolean acceptsPower(Level world, BlockPos thisPos, Direction sideFrom) {
        return world.getBlockState(thisPos).getProperties().contains(ON_OFF_PWR);
    }

    @Override
    public void receivePower(Level world, BlockPos thisPos, Direction sideFrom, int amount) {
        // When the squeezer is powered, it will squeeze
        if (world.getBlockState(thisPos).getValue(PRESS) == 0) {
            this.squeeze(world.getBlockState(thisPos), world, thisPos);
        }

        // Schedules to stop
        world.setBlockAndUpdate(thisPos, world.getBlockState(thisPos).setValue(ON_OFF_PWR, ON));
        world.scheduleTick(thisPos, this, 10);
    }

    @Override
    protected void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        super.tick(state, world, pos, random);
        if (state.getBlock() != this) {
            return;
        }
        if (state.getValue(ON_OFF_PWR) == SCHEDULED_STOP) {
            world.setBlockAndUpdate(pos, state.setValue(ON_OFF_PWR, OFF));

            // When the squeezer is de-powered, it will decompress
            if (world.getBlockState(pos).getValue(PRESS) == 2) {
                this.decompress(world.getBlockState(pos), world, pos);
            }
        }
        else if (state.getValue(ON_OFF_PWR) == ON){
            world.scheduleTick(pos, this, 10);
            world.setBlockAndUpdate(pos, state.setValue(ON_OFF_PWR, SCHEDULED_STOP));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PRESS, ON_OFF_PWR);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        int press = state.getValue(PRESS);
        switch (press) {
            case 0:
                return Shapes.block();
            case 1:
                return SHAPE_1;
            case 2:
                return SHAPE_2;
            case 3:
                return SHAPE_1;
            default:
                return Shapes.block();
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if(!world.isClientSide) {
            if(world.getBlockEntity(pos) instanceof SqueezerBlockEntity squeezerBlockEntity) {
                player.openMenu(squeezerBlockEntity);
            }
        }

        return InteractionResult.sidedSuccess(world.isClientSide);
    }
//
//    @Override
//    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
//        if (state.getBlock() != this) { return super.onUse(state, world, pos, player, hit); }
//
//        if (state.get(PRESS) == 0) {
//            this.squeeze(state, world, pos);
//            return ActionResult.SUCCESS;
//        }
//        if (state.get(PRESS) == 2) {
//            this.decompress(state, world, pos);
//            return ActionResult.SUCCESS;
//        }
//        return super.onUse(state, world, pos, player, hit);
//    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntityTypes.SQUEEZER.create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return TickableBlockEntity.getTicker(world);
    }

    // Squeeze
    public void squeeze(BlockState state, Level world, BlockPos pos) {
        if (state.getValue(PRESS) == 0) {
            world.setBlockAndUpdate(pos, state.setValue(PRESS, 1));
            if (world.getBlockEntity(pos) instanceof SqueezerBlockEntity squeezerBlockEntity) {
                squeezerBlockEntity.setTicks(5);
            }
        }
    }

    // Decompress
    public void decompress(BlockState state, Level world, BlockPos pos) {
        if (state.getValue(PRESS) == 2) {
            world.setBlockAndUpdate(pos, state.setValue(PRESS, 3));
            if (world.getBlockEntity(pos) instanceof SqueezerBlockEntity squeezerBlockEntity) {
                squeezerBlockEntity.setTicks(5);
            }
        }
    }
}
