package com.github.ilja615.iljatech.blocks.windmill;

import com.github.ilja615.iljatech.energy.MechPwrAccepter;
import com.github.ilja615.iljatech.energy.MechPwrSender;
import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.init.ModParticles;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;

import static com.github.ilja615.iljatech.energy.MechPwrAccepter.MECH_PWR;
import static com.github.ilja615.iljatech.energy.MechPwrAccepter.ON_OFF_PWR;
import static com.github.ilja615.iljatech.energy.MechPwrAccepter.OnOffPwr.OFF;
import static com.github.ilja615.iljatech.energy.MechPwrAccepter.OnOffPwr.ON;

public class WindmillBlock extends HorizontalFacingBlock implements BlockEntityProvider, MechPwrSender {
    public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;

    public WindmillBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(ON_OFF_PWR, OFF).with(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return null;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, ON_OFF_PWR);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntityTypes.WINDMILL.instantiate(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return TickableBlockEntity.getTicker(world);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world instanceof ServerWorld) {
            Vector2f vector = Wind.getWindVectorAt(world, world.getWorldChunk(pos).getPos().x, world.getWorldChunk(pos).getPos().z);
            Pair<WindDirection, Double> wind = Wind.getWindFromVector(vector);
            if (wind.getLeft().alignsWith(state.get(FACING).getOpposite()))
                toggle(state, world, pos);
            else
                world.setBlockState(pos, state.with(ON_OFF_PWR, OFF));
        }
        return ActionResult.SUCCESS;
    }

    private void toggle(BlockState state, World world, BlockPos pos) {
        if (state.get(ON_OFF_PWR) != ON)
            world.setBlockState(pos, state.with(ON_OFF_PWR, ON));
        else
            world.setBlockState(pos, state.with(ON_OFF_PWR, OFF));
    }
}
