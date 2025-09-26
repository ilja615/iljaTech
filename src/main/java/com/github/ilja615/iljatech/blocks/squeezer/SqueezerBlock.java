package com.github.ilja615.iljatech.blocks.squeezer;

import com.github.ilja615.iljatech.energy.MechPwrAccepter;
import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static com.github.ilja615.iljatech.energy.MechPwrAccepter.OnOffPwr.*;
import static com.github.ilja615.iljatech.energy.MechPwrAccepter.OnOffPwr.SCHEDULED_STOP;

public class SqueezerBlock  extends Block implements BlockEntityProvider, MechPwrAccepter {
    public static final IntProperty PRESS = IntProperty.of("press", 0, 3);
    protected static final VoxelShape SHAPE_1 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 14.0, 16.0);
    protected static final VoxelShape SHAPE_2 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 11.0, 16.0);

    public SqueezerBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(PRESS, 0).with(ON_OFF_PWR, OFF));
    }

    @Override
    public boolean acceptsPower(World world, BlockPos thisPos, Direction sideFrom) {
        return world.getBlockState(thisPos).getProperties().contains(ON_OFF_PWR);
    }

    @Override
    public void receivePower(World world, BlockPos thisPos, Direction sideFrom, int amount) {
        // When the squeezer is powered, it will squeeze
        if (world.getBlockState(thisPos).get(PRESS) == 0) {
            this.squeeze(world.getBlockState(thisPos), world, thisPos);
        }

        // Schedules to stop
        world.setBlockState(thisPos, world.getBlockState(thisPos).with(ON_OFF_PWR, ON));
        world.scheduleBlockTick(thisPos, this, 10);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        if (state.getBlock() != this) {
            return;
        }
        if (state.get(ON_OFF_PWR) == SCHEDULED_STOP) {
            world.setBlockState(pos, state.with(ON_OFF_PWR, OFF));

            // When the squeezer is de-powered, it will decompress
            if (world.getBlockState(pos).get(PRESS) == 2) {
                this.decompress(world.getBlockState(pos), world, pos);
            }
        }
        else if (state.get(ON_OFF_PWR) == ON){
            world.scheduleBlockTick(pos, this, 10);
            world.setBlockState(pos, state.with(ON_OFF_PWR, SCHEDULED_STOP));
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(PRESS, ON_OFF_PWR);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        int press = state.get(PRESS);
        switch (press) {
            case 0:
                return VoxelShapes.fullCube();
            case 1:
                return SHAPE_1;
            case 2:
                return SHAPE_2;
            case 3:
                return SHAPE_1;
            default:
                return VoxelShapes.fullCube();
        }
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if(!world.isClient) {
            if(world.getBlockEntity(pos) instanceof SqueezerBlockEntity squeezerBlockEntity) {
                player.openHandledScreen(squeezerBlockEntity);
            }
        }

        return ActionResult.success(world.isClient);
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
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntityTypes.SQUEEZER.instantiate(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return TickableBlockEntity.getTicker(world);
    }

    // Squeeze
    public void squeeze(BlockState state, World world, BlockPos pos) {
        if (state.get(PRESS) == 0) {
            world.setBlockState(pos, state.with(PRESS, 1));
            if (world.getBlockEntity(pos) instanceof SqueezerBlockEntity squeezerBlockEntity) {
                squeezerBlockEntity.setTicks(5);
            }
        }
    }

    // Decompress
    public void decompress(BlockState state, World world, BlockPos pos) {
        if (state.get(PRESS) == 2) {
            world.setBlockState(pos, state.with(PRESS, 3));
            if (world.getBlockEntity(pos) instanceof SqueezerBlockEntity squeezerBlockEntity) {
                squeezerBlockEntity.setTicks(5);
            }
        }
    }
}
