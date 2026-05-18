package com.github.ilja615.iljatech.blocks;

import com.github.ilja615.iljatech.entities.SeatEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChairBlock extends HorizontalFacingBlock {

    protected static final VoxelShape BASE_SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 8.0, 14.0);
    protected static final Map<Direction, VoxelShape> SHAPE_FROM_DIRECTION;
    static {
        Map<Direction, VoxelShape> aMap = new HashMap<>();
        aMap.put(Direction.NORTH, VoxelShapes.union(BASE_SHAPE,Block.createCuboidShape(2.0, 8.0, 12.0, 14.0, 16.0, 14.0)));
        aMap.put(Direction.EAST, VoxelShapes.union(BASE_SHAPE,Block.createCuboidShape(2.0, 8.0, 2.0, 4.0, 16.0, 14.0)));
        aMap.put(Direction.SOUTH, VoxelShapes.union(BASE_SHAPE,Block.createCuboidShape(2.0, 8.0, 2.0, 14.0, 16.0, 4.0)));
        aMap.put(Direction.WEST, VoxelShapes.union(BASE_SHAPE,Block.createCuboidShape(12.0, 8.0, 2.0, 14.0, 16.0, 14.0)));
        SHAPE_FROM_DIRECTION = Collections.unmodifiableMap(aMap);
    }

    public ChairBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {return null;}

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if(!world.isClient())
        {
            List<SeatEntity> seats = world.getEntitiesByClass(SeatEntity.class, new Box(pos), EntityPredicates.VALID_ENTITY);
            if(seats.isEmpty())
            {
                SeatEntity seat = new SeatEntity(world, pos, state.get(FACING));
                world.spawnEntity(seat);
                if (player.startRiding(seat, false)) {
                    return ActionResult.SUCCESS;
                }
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE_FROM_DIRECTION.get(state.get(FACING));
    }
}
