package com.github.ilja615.iljatech.blocks;

import com.github.ilja615.iljatech.energy.MechPwrAccepter;
import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.init.ModParticles;
import com.github.ilja615.iljatech.init.ModSounds;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.enums.SlabType;
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

public class BellowsBlock extends HorizontalFacingBlock implements MechPwrAccepter {
    public static final int BLOW_DISTANCE = 5;
    public static final float BLOW_PARTICLE_SPEED = 0.5f;
    public static final IntProperty PRESS = IntProperty.of("press", 0, 3);
    public static final MapCodec<BellowsBlock> CODEC = createCodec(BellowsBlock::new);
    protected static final VoxelShape SHAPE_1 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 14.0, 16.0);
    protected static final VoxelShape SHAPE_2 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 11.0, 16.0);

    public BellowsBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(PRESS, 2));
    }

    @Override
    public boolean acceptsPower(World world, BlockPos thisPos, Direction sideFrom) {
        return world.getBlockState(thisPos).get(PRESS) == 0;
    }

    @Override
    public void receivePower(World world, BlockPos thisPos, Direction sideFrom, int amount) {
        BlockState state = world.getBlockState(thisPos);
        world.setBlockState(thisPos, state.with(PRESS, 1));
        world.scheduleBlockTick(thisPos, this, 5);
        exhale(world, thisPos, state.get(FACING), BLOW_DISTANCE, BLOW_PARTICLE_SPEED);
        if (!world.isClient) {
            world.playSound(null, thisPos, ModSounds.BELLOWS_EXHALE, SoundCategory.PLAYERS, 1.0f, 1.0f);
        }
        MechPwrAccepter.super.receivePower(world, thisPos, sideFrom, amount);
    }

    @Override
    public void onDePower(World world, BlockPos thisPos) {
        if (world.getBlockState(thisPos).get(PRESS) == 2) {
            BlockState state = world.getBlockState(thisPos);
            world.setBlockState(thisPos, state.with(PRESS, 3));
            world.scheduleBlockTick(thisPos, this, 5);
            if (!world.isClient) {
                world.playSound(null, thisPos, ModSounds.BELLOWS_INHALE, SoundCategory.PLAYERS, 1.0f, 1.0f);
            }
            MechPwrAccepter.super.onDePower(world, thisPos);
        }
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {return CODEC;}

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(PRESS, FACING);
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
            world.setBlockState(pos, state.with(PRESS, 1));
            world.scheduleBlockTick(pos, this, 5);
            exhale(world, pos, state.get(FACING), BLOW_DISTANCE, BLOW_PARTICLE_SPEED);
            if (!world.isClient) {
                world.playSound(null, pos, ModSounds.BELLOWS_EXHALE, SoundCategory.PLAYERS, 1.0f, 1.0f);
            }
            return ActionResult.SUCCESS;
        }
        if (state.get(PRESS) == 2) {
            world.setBlockState(pos, state.with(PRESS, 3));
            world.scheduleBlockTick(pos, this, 5);
            if (!world.isClient) {
                world.playSound(null, pos, ModSounds.BELLOWS_INHALE, SoundCategory.PLAYERS, 1.0f, 1.0f);
            }
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hit);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        if (state.getBlock() != this) { return; }

        if (state.get(PRESS) == 1) {
            world.setBlockState(pos, state.with(PRESS, 2));
        } else if (state.get(PRESS) == 3) {
            world.setBlockState(pos, state.with(PRESS, 0));
        }
    }

    public static void exhale(World world, BlockPos startPosition, Direction direction, int length, float speed) {
        for (int i = 1; i < length + 1; i++) {
            // The wind goes to the next block
            BlockPos newPos = startPosition.offset(direction, i);

            // The wind can stoke a fires.
            if (world.getBlockState(newPos).getBlock() == Blocks.FIRE || world.getBlockState(newPos).isOf(ModBlocks.STOKED_FIRE))
                world.setBlockState(newPos, ModBlocks.STOKED_FIRE.getDefaultState().with(StokedFireBlock.STOKED, 3), 3);

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
