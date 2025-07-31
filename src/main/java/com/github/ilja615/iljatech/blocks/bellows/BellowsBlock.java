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
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
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

public class BellowsBlock extends HorizontalFacingBlock implements BlockEntityProvider, MechPwrAccepter {
    public static final int BLOW_DISTANCE = 5;
    public static final float BLOW_PARTICLE_SPEED = 0.5f;
    public static final IntProperty PRESS = IntProperty.of("press", 0, 3);
    public static final MapCodec<BellowsBlock> CODEC = createCodec(BellowsBlock::new);
    protected static final VoxelShape SHAPE_1 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 14.0, 16.0);
    protected static final VoxelShape SHAPE_2 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 11.0, 16.0);

    public BellowsBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(PRESS, 2).with(ON_OFF_PWR, OFF));
    }

    @Override
    public boolean acceptsPower(World world, BlockPos thisPos, Direction sideFrom) {
        return world.getBlockState(thisPos).getProperties().contains(ON_OFF_PWR);
    }

    @Override
    public void receivePower(World world, BlockPos thisPos, Direction sideFrom, int amount) {
        // When the bellows is powered, it will exhale
        if (world.getBlockState(thisPos).get(PRESS) == 0) {
            this.exhale(world.getBlockState(thisPos), world, thisPos);
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

            // When the bellows is de-powered, it will inhale
            if (world.getBlockState(pos).get(PRESS) == 2) {
                this.inhale(world.getBlockState(pos), world, pos);
            }
        }
        else if (state.get(ON_OFF_PWR) == ON){
            world.scheduleBlockTick(pos, this, 10);
            world.setBlockState(pos, state.with(ON_OFF_PWR, SCHEDULED_STOP));
        }
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {return CODEC;}

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(PRESS, FACING, ON_OFF_PWR);
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
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (state.getBlock() != this) { return super.onUse(state, world, pos, player, hit); }

        if (state.get(PRESS) == 0) {
            this.exhale(state, world, pos);
            return ActionResult.SUCCESS;
        }
        if (state.get(PRESS) == 2) {
            this.inhale(state, world, pos);
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hit);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntityTypes.BELLOWS.instantiate(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return TickableBlockEntity.getTicker(world);
    }

    // Contract
    public void exhale(BlockState state, World world, BlockPos pos) {
        if (state.get(PRESS) == 0) {
            blowWind(world, pos, state.get(FACING), BLOW_DISTANCE, BLOW_PARTICLE_SPEED);
            world.setBlockState(pos, state.with(PRESS, 1));
            if (world.getBlockEntity(pos) instanceof BellowsBlockEntity bellowsBlockEntity) {
                bellowsBlockEntity.setTicks(5);
            }
            if (!world.isClient) {
                world.playSound(null, pos, ModSounds.BELLOWS_EXHALE, SoundCategory.PLAYERS, 1.0f, 1.0f);
            }
        }
    }

    // Expand
    public void inhale(BlockState state, World world, BlockPos pos) {
        if (state.get(PRESS) == 2) {
            world.setBlockState(pos, state.with(PRESS, 3));
            if (world.getBlockEntity(pos) instanceof BellowsBlockEntity bellowsBlockEntity) {
                bellowsBlockEntity.setTicks(5);
            }
            if (!world.isClient) {
                world.playSound(null, pos, ModSounds.BELLOWS_INHALE, SoundCategory.PLAYERS, 1.0f, 1.0f);
            }
        }
    }

    public static void blowWind(World world, BlockPos startPosition, Direction direction, int length, float speed) {
        for (int i = 1; i < length + 1; i++) {
            // The wind goes to the next block
            BlockPos newPos = startPosition.offset(direction, i);

            // The wind can stoke a fires.
            if (world.getBlockState(newPos).getBlock() == Blocks.FIRE || world.getBlockState(newPos).isOf(ModBlocks.STOKED_FIRE))
                world.setBlockState(newPos, ModBlocks.STOKED_FIRE.getDefaultState().with(StokedFireBlock.STOKED, 3), 3);

            // The wind can stoke a fire in firebox
            if (world.getBlockState(newPos).getBlock() == ModBlocks.FIREBOX && world.getBlockEntity(newPos) instanceof FireboxBlockEntity fireboxBlockEntity)
                if (world.getBlockState(newPos).get(FireboxBlock.LIT) != FireboxBlock.Lit.OFF)
                    fireboxBlockEntity.setStokedTicks(80);

            // The wind stops when there was a block that fully occupies the block face
            if (Block.isFaceFullSquare(world.getBlockState(newPos).getCollisionShape(world, newPos), direction.getOpposite()))
                break;
            else {
                if (!world.isClient) {
                    ((ServerWorld) world).spawnParticles(ParticleTypes.POOF, newPos.getX() + world.random.nextFloat() * 0.5f + 0.25f, newPos.getY() + world.random.nextFloat() * 0.5f + 0.25f, newPos.getZ() + world.random.nextFloat() * 0.5f + 0.25f, 5, direction.getOffsetX() * speed, direction.getOffsetY() * speed, direction.getOffsetZ() * speed, 0.0);
                }
            }
        }
    }
}
