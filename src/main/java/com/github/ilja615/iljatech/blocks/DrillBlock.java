package com.github.ilja615.iljatech.blocks;

import com.github.ilja615.iljatech.energy.MechPwrAccepter;
import net.minecraft.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import java.util.List;

import static com.github.ilja615.iljatech.energy.MechPwrAccepter.OnOffPwr.*;

public class DrillBlock extends Block implements MechPwrAccepter {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    public DrillBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(ON_OFF_PWR, OFF).setValue(FACING, Direction.UP));
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getClickedFace());
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return (FaceAttachedHorizontalDirectionalBlock.canAttach(world, pos, state.getValue(FACING).getOpposite())
        || world.getBlockState(pos.relative(state.getValue(FACING).getOpposite())).is(Blocks.HONEY_BLOCK));
    }

    @Override
    protected BlockState updateShape(
            BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos
    ) {        return state.getValue(FACING) == direction && !state.canSurvive(world, pos)
            ? Blocks.AIR.defaultBlockState()
            : super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        if (notify) {
            world.setBlockAndUpdate(pos, state.setValue(ON_OFF_PWR, SCHEDULED_STOP));
            world.scheduleTick(pos, this, 10);
            if (!world.isClientSide) {
                mine((ServerLevel) world, pos);
            }
        }
        super.onPlace(state, world, pos, oldState, notify);
    }

    private void mine(ServerLevel world, BlockPos thisPos) {
        BlockState thisState = world.getBlockState(thisPos);
        if (thisState.getBlock() instanceof DrillBlock) {
            BlockPos miningPos = thisPos.relative(thisState.getValue(FACING));
            if (miningPos.getY() > world.getMinBuildHeight() && miningPos.getY() < world.getMaxBuildHeight() && world.getWorldBorder().isWithinBounds(miningPos)) {
                BlockState state = world.getBlockState(miningPos);
                if (!state.isAir() && state.getDestroySpeed(world, miningPos) >= 0 && !state.is(BlockTags.INCORRECT_FOR_IRON_TOOL)) {
                    List<ItemStack> drops = state.getDrops(
                            new LootParams.Builder(world)
                                    .withParameter(LootContextParams.TOOL, Items.IRON_PICKAXE.getDefaultInstance())
                                    .withParameter(LootContextParams.ORIGIN, miningPos.getCenter())
                    );
                    world.destroyBlock(miningPos, false);
                    if (!drops.isEmpty()) {
                        for (ItemStack drop : drops) {
                            Containers.dropItemStack(world, miningPos.getX()+0.5D, miningPos.getY()+0.5D, miningPos.getZ()+0.5D, drop);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void receivePower(Level world, BlockPos thisPos, Direction sideFrom, int amount)
    {
        // Schedules to stop
        world.setBlockAndUpdate(thisPos, world.getBlockState(thisPos).setValue(ON_OFF_PWR, ON));
        world.scheduleTick(thisPos, this, 10);
        if (!world.isClientSide) {
            mine((ServerLevel) world, thisPos);
        };
    }

    @Override
    public boolean acceptsPower(Level world, BlockPos thisPos, Direction sideFrom)
    {
        // Drill can only accept power from the back and not when it's already on
        BlockState state = world.getBlockState(thisPos);
        return (state.getProperties().contains(FACING) && state.getValue(FACING).getOpposite() == sideFrom &&
                state.getProperties().contains(ON_OFF_PWR));
    }

    @Override
    protected void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        super.tick(state, world, pos, random);
        if (state.getBlock() != this) {
            return;
        }
        if (state.getValue(ON_OFF_PWR) == SCHEDULED_STOP)
            world.setBlockAndUpdate(pos, state.setValue(ON_OFF_PWR, OFF));
        else if (state.getValue(ON_OFF_PWR) == ON){
            world.scheduleTick(pos, this, 10);
            world.setBlockAndUpdate(pos, state.setValue(ON_OFF_PWR, SCHEDULED_STOP));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ON_OFF_PWR);
    }
}
