package com.github.ilja615.iljatech.blocks.firebox;

import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.init.ModItems;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class FireboxBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final EnumProperty<Lit> LIT = EnumProperty.create("lit", Lit.class);
    public static final IntegerProperty ASH_LEVEL = IntegerProperty.create("ash_level", 0, 5);

    public FireboxBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, Lit.OFF).setValue(ASH_LEVEL, 0));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return null;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT, ASH_LEVEL);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntityTypes.FIREBOX.create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return TickableBlockEntity.getTicker(world);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        Item item = stack.getItem();
        if (hit.getDirection() == state.getValue(FACING)) { // Check if front is the clicked side
            if (stack.is(ItemTags.SHOVELS) && state.getValue(ASH_LEVEL) > 0) {
                BlockPos newPos = pos.relative(state.getValue(FACING));
                world.addFreshEntity(new ItemEntity(world, newPos.getX() + 0.5d, newPos.getY() + 0.5d, newPos.getZ() + 0.5d, new ItemStack(ModItems.ASH, state.getValue(ASH_LEVEL))));
                world.setBlockAndUpdate(pos, state.setValue(ASH_LEVEL, 0));
                return ItemInteractionResult.SUCCESS;
            }

            if (FuelRegistry.INSTANCE.get(item) != null && FuelRegistry.INSTANCE.get(item) > 0) {
                if (item.hasCraftingRemainingItem())
                    return ItemInteractionResult.FAIL;

                if (world.getBlockEntity(pos) instanceof FireboxBlockEntity fireboxBlockEntity) {
                    if (fireboxBlockEntity.getInventory().getItem(0).is(item)) {
                        int itsContent = fireboxBlockEntity.getInventory().getItem(0).getCount();

                        if (itsContent + stack.getCount() <= item.getDefaultMaxStackSize()) {
                            fireboxBlockEntity.getInventory().setItem(0, new ItemStack(item, itsContent + stack.getCount()));
                            fireboxBlockEntity.setChanged();
                            stack.shrink(stack.getCount());
                            return ItemInteractionResult.SUCCESS;
                        } else if (itsContent < item.getDefaultMaxStackSize()) {
                            int toBeAdded = item.getDefaultMaxStackSize() - itsContent;
                            if (toBeAdded < stack.getCount()) {
                                fireboxBlockEntity.getInventory().setItem(0, new ItemStack(item, itsContent + toBeAdded));
                                fireboxBlockEntity.setChanged();
                                stack.shrink(toBeAdded);
                                return ItemInteractionResult.SUCCESS;
                            }
                        }
                    } else if (fireboxBlockEntity.getInventory().getItem(0).isEmpty()) {
                        fireboxBlockEntity.getInventory().setItem(0, stack.copy());
                        fireboxBlockEntity.setChanged();
                        stack.shrink(stack.getCount());
                        return ItemInteractionResult.SUCCESS;
                    }
                }
            }
        }
        return super.useItemOn(stack, state, world, pos, player, hand, hit);
    }

    @Override
    protected void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof FireboxBlockEntity fireboxBlockEntity) {
                Containers.dropContents(world, pos, fireboxBlockEntity.getInventory());
                world.updateNeighbourForOutputSignal(pos, this);
            }
            if (state.getValue(ASH_LEVEL) > 0) {
                BlockPos front = pos.relative(state.getValue(FACING));
                Containers.dropItemStack(world, front.getX(), front.getY(), front.getZ(), new ItemStack(ModItems.ASH, state.getValue(ASH_LEVEL)));
            }
        }
        super.onRemove(state, world, pos, newState, moved);
    }

    public enum Lit implements StringRepresentable {
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
        public String getSerializedName() {
            return this.name;
        }
    }
}