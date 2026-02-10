package com.github.ilja615.iljatech.blocks.researchtable;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.network.BlockPosPayload;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BlueprintTableBlock extends HorizontalFacingBlock implements ExtendedScreenHandlerFactory<BlockPosPayload> {
    private BlockPos pos;
    public static final Text TITLE = Text.translatable("container." + IljaTech.MOD_ID + ".research");

    public BlueprintTableBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            this.pos = pos;
            player.openHandledScreen(this);

//            player.modifyAttached(ModDataAttachments.RESEARCH_PNTS, currentValue -> 1 + (currentValue == null ? 0 : currentValue));
//            if (player.isSneaking())
//                player.setAttached(ModDataAttachments.RESEARCH_PNTS, 0);
//            player.sendMessage(Text.literal("Research pnts: "+player.getAttached(ModDataAttachments.RESEARCH_PNTS)));
            return ActionResult.CONSUME;
        }
    }
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }


    @Override
    public BlockPosPayload getScreenOpeningData(ServerPlayerEntity player) {
        return new BlockPosPayload(pos);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new BlueprintTableScreenHandler(syncId, playerInventory, pos);
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {return null;}

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public Text getDisplayName() {
        return TITLE;
    }
}
