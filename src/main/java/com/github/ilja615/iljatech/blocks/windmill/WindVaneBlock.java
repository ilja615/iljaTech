package com.github.ilja615.iljatech.blocks.windmill;

import com.github.ilja615.iljatech.init.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;

public class WindVaneBlock extends Block {
    public static final EnumProperty<WindDirection> WIND_DIRECTION = EnumProperty.create("wind_direction", WindDirection.class);
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    protected static final VoxelShape SHAPE = Block.box(7.0, 0, 7.0, 9.0, 16.0, 9.0);

    public WindVaneBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER).setValue(WIND_DIRECTION, WindDirection.N));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    protected boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return super.canSurvive(state, world, pos);
        } else {
            BlockState blockState = world.getBlockState(pos.below());
            return blockState.is(this) && blockState.getValue(HALF) == DoubleBlockHalf.LOWER;
        }
    }

    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        world.setBlock(pos.above(), (BlockState)state.setValue(HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
    }

    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        DoubleBlockHalf doubleBlockHalf = (DoubleBlockHalf)state.getValue(HALF);
        if (direction.getAxis() == Direction.Axis.Y && doubleBlockHalf == DoubleBlockHalf.LOWER == (direction == Direction.UP) && (!neighborState.is(this) || neighborState.getValue(HALF) == doubleBlockHalf)) {
            return Blocks.AIR.defaultBlockState();
        } else {
            return doubleBlockHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, neighborState, world, pos, neighborPos);
        }
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        if (world instanceof ServerLevel) {
            Vector2f vector = Wind.getWindVectorAt(world, world.getChunkAt(pos).getPos().x, world.getChunkAt(pos).getPos().z);
            Tuple<WindDirection, Double> wind = Wind.getWindFromVector(vector);
            return this.defaultBlockState().setValue(WIND_DIRECTION, wind.getA());
        }
        return super.getStateForPlacement(ctx);
    }

    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (!world.isClientSide) {
            if (player.isCreative()) {
                onBreakInCreative(world, pos, state, player);
            } else {
                dropResources(state, world, pos, (BlockEntity)null, player, player.getMainHandItem());
            }
        }

        return super.playerWillDestroy(world, pos, state, player);
    }

    public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(world, player, pos, Blocks.AIR.defaultBlockState(), blockEntity, tool);
    }

    protected static void onBreakInCreative(Level world, BlockPos pos, BlockState state, Player player) {
        DoubleBlockHalf doubleBlockHalf = state.getValue(HALF);
        if (doubleBlockHalf == DoubleBlockHalf.UPPER) {
            BlockPos blockPos = pos.below();
            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.is(state.getBlock()) && blockState.getValue(HALF) == DoubleBlockHalf.LOWER) {
                BlockState blockState2 = blockState.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                world.setBlock(blockPos, blockState2, Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
                world.levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, blockPos, Block.getId(blockState));
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WIND_DIRECTION, HALF);
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        super.animateTick(state, world, pos, random);
        if (state.getValue(HALF) != DoubleBlockHalf.UPPER)
            return;
        if (world.random.nextFloat() < 0.1f) {
//            Vector2f d = Wind.getWindDirectionAt(null,(int) pos.getX() >> 4, (int) pos.getZ() >> 4).getUnitVector();
//            WindParticleEffect parameters = new WindParticleEffect(d);
            world.addParticle(ModParticles.WIND_LEADING, pos.getX() + 0.5d, pos.getY() + 0.8d, pos.getZ() + 0.5d, 0.0, 0.0, 0.0);
        }
    }
}
