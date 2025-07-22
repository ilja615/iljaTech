package com.github.ilja615.iljatech.blocks.conveyorbelt;

import com.github.ilja615.iljatech.init.ModBlocks;
import net.minecraft.block.*;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ConveyorBeltBlock extends HorizontalFacingBlock {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final EnumProperty CONVEYOR_BELT_STATE = EnumProperty.of("type", ConveyorBeltState.class);

    protected static final VoxelShape TOP_SHAPE = Block.createCuboidShape(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape BOTTOM_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);

    public ConveyorBeltBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(POWERED, false)
                .with(CONVEYOR_BELT_STATE, ConveyorBeltState.NORMAL));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(CONVEYOR_BELT_STATE)) {
            case ConveyorBeltState.NORMAL, ConveyorBeltState.DIAGONAL:
            default:
                return VoxelShapes.fullCube();
            case ConveyorBeltState.BOTTOM_SLAB:
                return BOTTOM_SHAPE;
            case ConveyorBeltState.TOP_SLAB:
                return TOP_SHAPE;
        }
    }

    @Override
    protected com.mojang.serialization.MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return null;
    }

    public enum ConveyorBeltState implements StringIdentifiable {
        NORMAL("normal"),
        DIAGONAL("diagonal"),
        TOP_SLAB("top_slab"),
        BOTTOM_SLAB("bottom_slab");

        private final String name;

        private ConveyorBeltState(final String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, CONVEYOR_BELT_STATE);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        Direction facing = world.getBlockState(pos).get(FACING);
//        BlockState state1 = world.getBlockState(pos.down());

        BlockState state1 = world.getBlockState(pos.offset(facing.getOpposite()).down());
        if (!state1.isOf(ModBlocks.CONVEYOR_BELT))
            return;
        BlockState state2 = world.getBlockState(pos.offset(facing.getOpposite(), 2).down());
        if (!state2.isOf(ModBlocks.CONVEYOR_BELT))
            return;
        if (!state1.get(CONVEYOR_BELT_STATE).equals(ConveyorBeltState.NORMAL))
            return;
        if (!state2.get(CONVEYOR_BELT_STATE).equals(ConveyorBeltState.NORMAL))
            return;

        world.setBlockState(pos.offset(facing.getOpposite()).down(), state1.with(CONVEYOR_BELT_STATE, ConveyorBeltState.TOP_SLAB).with(FACING, facing), Block.NOTIFY_ALL | Block.FORCE_STATE);
        world.setBlockState(pos.offset(facing.getOpposite()), state1.with(CONVEYOR_BELT_STATE, ConveyorBeltState.BOTTOM_SLAB).with(FACING, facing), Block.NOTIFY_ALL | Block.FORCE_STATE);
        world.setBlockState(pos, state.with(CONVEYOR_BELT_STATE, ConveyorBeltState.DIAGONAL).with(FACING, facing), Block.NOTIFY_ALL | Block.FORCE_STATE);
        if (world.getBlockState(pos.down()).isOf(ModBlocks.CONVEYOR_BELT))
            world.setBlockState(pos.down(), Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
//        world.setBlockState(pos.down(), state1.with(CONVEYOR_BELT_STATE, ConveyorBeltState.TOP_SLAB), Block.NOTIFY_ALL);
//        world.setBlockState(pos, state.with(CONVEYOR_BELT_STATE, ConveyorBeltState.BOTTOM_SLAB), Block.NOTIFY_ALL);
    }
}
