package com.github.ilja615.iljatech.blocks.carpentry;

import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class CarpentryBlock extends Block implements EntityBlock {
    public CarpentryBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if(!world.isClientSide) {
            if(world.getBlockEntity(pos) instanceof CarpentryBlockEntity carpentryBlockEntity) {
                player.openMenu(carpentryBlockEntity);
                carpentryBlockEntity.checkRecipes();
            }
        }

        return InteractionResult.sidedSuccess(world.isClientSide);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntityTypes.CARPENTRY.create(pos, state);
    }

    @Override
    protected void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CarpentryBlockEntity carpentryBlockEntity) {
                Containers.dropContents(world, pos, carpentryBlockEntity.getInventory());
                world.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, world, pos, newState, moved);
    }
}
