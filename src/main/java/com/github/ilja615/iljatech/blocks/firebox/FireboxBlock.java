package com.github.ilja615.iljatech.blocks.firebox;

import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.init.ModItems;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FireboxBlock extends HorizontalFacingBlock implements BlockEntityProvider {
    public static final EnumProperty<Lit> LIT = EnumProperty.of("lit", Lit.class);
    public static final IntProperty ASH_LEVEL = IntProperty.of("ash_level", 0, 5);

    public FireboxBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(LIT, Lit.OFF).with(ASH_LEVEL, 0));
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return null;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT, ASH_LEVEL);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntityTypes.FIREBOX.instantiate(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return TickableBlockEntity.getTicker(world);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Item item = stack.getItem();
        if (hit.getSide() == state.get(FACING)) { // Check if front is the clicked side
            if (stack.isIn(ItemTags.SHOVELS) && state.get(ASH_LEVEL) > 0) {
                BlockPos newPos = pos.offset(state.get(FACING));
                world.spawnEntity(new ItemEntity(world, newPos.getX() + 0.5d, newPos.getY() + 0.5d, newPos.getZ() + 0.5d, new ItemStack(ModItems.ASH, state.get(ASH_LEVEL))));
                world.setBlockState(pos, state.with(ASH_LEVEL, 0));
                return ItemActionResult.SUCCESS;
            }

            if (FuelRegistry.INSTANCE.get(item) != null && FuelRegistry.INSTANCE.get(item) > 0) {
                if (item.hasRecipeRemainder())
                    return ItemActionResult.FAIL;

                if (world.getBlockEntity(pos) instanceof FireboxBlockEntity fireboxBlockEntity) {
                    if (fireboxBlockEntity.getInventory().getStack(0).isOf(item)) {
                        int itsContent = fireboxBlockEntity.getInventory().getStack(0).getCount();

                        if (itsContent + stack.getCount() <= item.getMaxCount()) {
                            fireboxBlockEntity.getInventory().setStack(0, new ItemStack(item, itsContent + stack.getCount()));
                            fireboxBlockEntity.markDirty();
                            stack.decrement(stack.getCount());
                            return ItemActionResult.SUCCESS;
                        } else if (itsContent < item.getMaxCount()) {
                            int toBeAdded = item.getMaxCount() - itsContent;
                            if (toBeAdded < stack.getCount()) {
                                fireboxBlockEntity.getInventory().setStack(0, new ItemStack(item, itsContent + toBeAdded));
                                fireboxBlockEntity.markDirty();
                                stack.decrement(toBeAdded);
                                return ItemActionResult.SUCCESS;
                            }
                        }
                    } else if (fireboxBlockEntity.getInventory().getStack(0).isEmpty()) {
                        fireboxBlockEntity.getInventory().setStack(0, stack.copy());
                        fireboxBlockEntity.markDirty();
                        stack.decrement(stack.getCount());
                        return ItemActionResult.SUCCESS;
                    }
                }
            }
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof FireboxBlockEntity fireboxBlockEntity) {
                ItemScatterer.spawn(world, pos, fireboxBlockEntity.getInventory());
                world.updateComparators(pos, this);
            }
            if (state.get(ASH_LEVEL) > 0) {
                BlockPos front = pos.offset(state.get(FACING));
                ItemScatterer.spawn(world, front.getX(), front.getY(), front.getZ(), new ItemStack(ModItems.ASH, state.get(ASH_LEVEL)));
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    public enum Lit implements StringIdentifiable {
        ON("on", 13),
        OFF("off", 0),
        STOKED("stoked", 13),
        CHOKING("choking", 6);

        private final String name;
        public int luminance;

        private Lit(final String name, final int lum) {
            this.name = name;
            this.luminance = lum;
        }

        @Override
        public String asString() {
            return this.name;
        }
    }
}