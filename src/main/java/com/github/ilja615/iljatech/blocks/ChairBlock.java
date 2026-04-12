package com.github.ilja615.iljatech.blocks;

import com.github.ilja615.iljatech.entities.SeatEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChairBlock extends HorizontalDirectionalBlock {

    protected static final VoxelShape BASE_SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 8.0, 14.0);
    protected static final Map<Direction, VoxelShape> SHAPE_FROM_DIRECTION;
    static {
        Map<Direction, VoxelShape> aMap = new HashMap<>();
        aMap.put(Direction.NORTH, Shapes.or(BASE_SHAPE,Block.box(2.0, 8.0, 12.0, 14.0, 16.0, 14.0)));
        aMap.put(Direction.EAST, Shapes.or(BASE_SHAPE,Block.box(2.0, 8.0, 2.0, 4.0, 16.0, 14.0)));
        aMap.put(Direction.SOUTH, Shapes.or(BASE_SHAPE,Block.box(2.0, 8.0, 2.0, 14.0, 16.0, 4.0)));
        aMap.put(Direction.WEST, Shapes.or(BASE_SHAPE,Block.box(12.0, 8.0, 2.0, 14.0, 16.0, 14.0)));
        SHAPE_FROM_DIRECTION = Collections.unmodifiableMap(aMap);
    }

    public ChairBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {return null;}

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if(!world.isClientSide())
        {
            List<SeatEntity> seats = world.getEntitiesOfClass(SeatEntity.class, new AABB(pos), EntitySelector.ENTITY_STILL_ALIVE);
            if(seats.isEmpty())
            {
                SeatEntity seat = new SeatEntity(world, pos, state.getValue(FACING));
                world.addFreshEntity(seat);
                if (player.startRiding(seat, false)) {
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE_FROM_DIRECTION.get(state.getValue(FACING));
    }
}
