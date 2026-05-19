package com.github.ilja615.iljatech.blocks.researchtable;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.init.ModDataAttachments;
import com.github.ilja615.iljatech.network.BlockPosPayload;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ResearchTableBlock extends HorizontalDirectionalBlock {
    public ResearchTableBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            player.modifyAttached(ModDataAttachments.RESEARCH_PNTS, currentValue -> 1 + (currentValue == null ? 0 : currentValue));
            if (player.isShiftKeyDown())
                player.setAttached(ModDataAttachments.RESEARCH_PNTS, 0);
            player.sendSystemMessage(Component.literal("Research pts: "+player.getAttached(ModDataAttachments.RESEARCH_PNTS)));
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection());
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {return null;}

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
