package com.github.ilja615.iljatech.blocks.bellows;

import com.github.ilja615.iljatech.blocks.StokedFireBlock;
import com.github.ilja615.iljatech.blocks.firebox.FireboxBlock;
import com.github.ilja615.iljatech.blocks.firebox.FireboxBlockEntity;
import com.github.ilja615.iljatech.energy.MechPwrAccepter;
import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.init.ModSounds;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
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

public class BellowsBlock extends HorizontalDirectionalBlock implements EntityBlock, MechPwrAccepter {
    public static final int BLOW_DISTANCE = 5;
    public static final float BLOW_PARTICLE_SPEED = 0.5f;
    public static final IntegerProperty PRESS = IntegerProperty.create("press", 0, 3);
    public static final MapCodec<BellowsBlock> CODEC = simpleCodec(BellowsBlock::new);
    protected static final VoxelShape SHAPE_1 = Block.box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0);
    protected static final VoxelShape SHAPE_2 = Block.box(0.0, 0.0, 0.0, 16.0, 11.0, 16.0);

    public BellowsBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(PRESS, 2).setValue(ON_OFF_PWR, OFF));
    }

    @Override
    public boolean acceptsPower(Level world, BlockPos thisPos, Direction sideFrom) {
        return world.getBlockState(thisPos).getProperties().contains(ON_OFF_PWR);
    }

    @Override
    public void receivePower(Level world, BlockPos thisPos, Direction sideFrom, int amount) {
        // When the bellows is powered, it will exhale
        if (world.getBlockState(thisPos).getValue(PRESS) == 0) {
            this.exhale(world.getBlockState(thisPos), world, thisPos);
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

            // When the bellows is de-powered, it will inhale
            if (world.getBlockState(pos).getValue(PRESS) == 2) {
                this.inhale(world.getBlockState(pos), world, pos);
            }
        }
        else if (state.getValue(ON_OFF_PWR) == ON){
            world.scheduleTick(pos, this, 10);
            world.setBlockAndUpdate(pos, state.setValue(ON_OFF_PWR, SCHEDULED_STOP));
        }
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {return CODEC;}

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PRESS, FACING, ON_OFF_PWR);
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
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (state.getBlock() != this) { return super.useWithoutItem(state, world, pos, player, hit); }

        if (state.getValue(PRESS) == 0) {
            this.exhale(state, world, pos);
            return InteractionResult.SUCCESS;
        }
        if (state.getValue(PRESS) == 2) {
            this.inhale(state, world, pos);
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, world, pos, player, hit);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntityTypes.BELLOWS.create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return TickableBlockEntity.getTicker(world);
    }

    // Contract
    public void exhale(BlockState state, Level world, BlockPos pos) {
        if (state.getValue(PRESS) == 0) {
            blowWind(world, pos, state.getValue(FACING), BLOW_DISTANCE, BLOW_PARTICLE_SPEED);
            world.setBlockAndUpdate(pos, state.setValue(PRESS, 1));
            if (world.getBlockEntity(pos) instanceof BellowsBlockEntity bellowsBlockEntity) {
                bellowsBlockEntity.setTicks(5);
            }
            if (!world.isClientSide) {
                world.playSound(null, pos, ModSounds.BELLOWS_EXHALE, SoundSource.PLAYERS, 1.0f, 1.0f);
            }
        }
    }

    // Expand
    public void inhale(BlockState state, Level world, BlockPos pos) {
        if (state.getValue(PRESS) == 2) {
            world.setBlockAndUpdate(pos, state.setValue(PRESS, 3));
            if (world.getBlockEntity(pos) instanceof BellowsBlockEntity bellowsBlockEntity) {
                bellowsBlockEntity.setTicks(5);
            }
            if (!world.isClientSide) {
                world.playSound(null, pos, ModSounds.BELLOWS_INHALE, SoundSource.PLAYERS, 1.0f, 1.0f);
            }
        }
    }

    public static void blowWind(Level world, BlockPos startPosition, Direction direction, int length, float speed) {
        for (int i = 1; i < length + 1; i++) {
            // The wind goes to the next block
            BlockPos newPos = startPosition.relative(direction, i);

            // The wind can stoke a fires.
            if (world.getBlockState(newPos).getBlock() == Blocks.FIRE || world.getBlockState(newPos).is(ModBlocks.STOKED_FIRE))
                world.setBlock(newPos, ModBlocks.STOKED_FIRE.defaultBlockState().setValue(StokedFireBlock.STOKED, 3), 3);

            // The wind can stoke a fire in firebox
            if (world.getBlockState(newPos).getBlock() == ModBlocks.FIREBOX && world.getBlockEntity(newPos) instanceof FireboxBlockEntity fireboxBlockEntity)
                if (world.getBlockState(newPos).getValue(FireboxBlock.LIT) != FireboxBlock.Lit.OFF)
                    fireboxBlockEntity.setStokedTicks(80);

            // The wind stops when there was a block that fully occupies the block face
            if (Block.isFaceFull(world.getBlockState(newPos).getCollisionShape(world, newPos), direction.getOpposite()))
                break;
            else {
                if (!world.isClientSide) {
                    ((ServerLevel) world).sendParticles(ParticleTypes.POOF, newPos.getX() + world.random.nextFloat() * 0.5f + 0.25f, newPos.getY() + world.random.nextFloat() * 0.5f + 0.25f, newPos.getZ() + world.random.nextFloat() * 0.5f + 0.25f, 5, direction.getStepX() * speed, direction.getStepY() * speed, direction.getStepZ() * speed, 0.0);
                }
            }
        }
    }
}
