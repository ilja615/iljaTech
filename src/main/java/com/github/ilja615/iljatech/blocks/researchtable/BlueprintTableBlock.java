package com.github.ilja615.iljatech.blocks.researchtable;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.network.BlockPosPayload;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.screen.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class BlueprintTableBlock extends HorizontalDirectionalBlock implements ExtendedScreenHandlerFactory<BlockPosPayload> {
    private BlockPos pos;
    public static final Component TITLE = Component.translatable("container." + IljaTech.MOD_ID + ".research");

    public BlueprintTableBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            this.pos = pos;
            player.openMenu(this);

//            player.modifyAttached(ModDataAttachments.RESEARCH_PNTS, currentValue -> 1 + (currentValue == null ? 0 : currentValue));
//            if (player.isSneaking())
//                player.setAttached(ModDataAttachments.RESEARCH_PNTS, 0);
//            player.sendMessage(Text.literal("Research pnts: "+player.getAttached(ModDataAttachments.RESEARCH_PNTS)));
            return InteractionResult.CONSUME;
        }
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection());
    }


    @Override
    public BlockPosPayload getScreenOpeningData(ServerPlayer player) {
        return new BlockPosPayload(pos);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new BlueprintTableScreenHandler(syncId, playerInventory, pos);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {return null;}

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public Component getDisplayName() {
        return TITLE;
    }
}
