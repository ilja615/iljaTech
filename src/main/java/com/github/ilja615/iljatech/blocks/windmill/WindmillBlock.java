package com.github.ilja615.iljatech.blocks.windmill;

import com.github.ilja615.iljatech.energy.MechPwrAccepter;
import com.github.ilja615.iljatech.energy.MechPwrSender;
import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.init.ModParticles;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;

import static com.github.ilja615.iljatech.energy.MechPwrAccepter.MECH_PWR;
import static com.github.ilja615.iljatech.energy.MechPwrAccepter.ON_OFF_PWR;
import static com.github.ilja615.iljatech.energy.MechPwrAccepter.OnOffPwr.OFF;
import static com.github.ilja615.iljatech.energy.MechPwrAccepter.OnOffPwr.ON;

public class WindmillBlock extends HorizontalDirectionalBlock implements EntityBlock, MechPwrSender {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

    public WindmillBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(ON_OFF_PWR, OFF).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ON_OFF_PWR);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntityTypes.WINDMILL.create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return TickableBlockEntity.getTicker(world);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (world instanceof ServerLevel) {
            Vector2f vector = Wind.getWindVectorAt(world, world.getChunkAt(pos).getPos().x, world.getChunkAt(pos).getPos().z);
            Tuple<WindDirection, Double> wind = Wind.getWindFromVector(vector);
            if (wind.getA().alignsWith(state.getValue(FACING).getOpposite()))
                toggle(state, world, pos);
            else
                world.setBlockAndUpdate(pos, state.setValue(ON_OFF_PWR, OFF));
        }
        return InteractionResult.SUCCESS;
    }

    private void toggle(BlockState state, Level world, BlockPos pos) {
        if (state.getValue(ON_OFF_PWR) != ON)
            world.setBlockAndUpdate(pos, state.setValue(ON_OFF_PWR, ON));
        else
            world.setBlockAndUpdate(pos, state.setValue(ON_OFF_PWR, OFF));
    }
}
