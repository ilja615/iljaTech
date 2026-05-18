package com.github.ilja615.iljatech.blocks.sifter;

import com.github.ilja615.iljatech.blocks.spinningframe.SpinningFrameBlockEntity;
import com.github.ilja615.iljatech.energy.MechPwrAccepter;
import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static com.github.ilja615.iljatech.energy.MechPwrAccepter.OnOffPwr.*;

public class SifterBlock extends Block implements BlockEntityProvider, MechPwrAccepter {

    protected static final VoxelShape MAIN = Block.createCuboidShape(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape LEG1A = Block.createCuboidShape(0.0, 0.0, 0.0, 4.0, 8.0, 2.0);
    protected static final VoxelShape LEG1B = Block.createCuboidShape(0.0, 0.0, 2.0, 2.0, 8.0, 4.0);
    protected static final VoxelShape LEG2A = Block.createCuboidShape(12.0, 0.0, 0.0, 16.0, 8.0, 2.0);
    protected static final VoxelShape LEG2B = Block.createCuboidShape(14.0, 0.0, 2.0, 16.0, 8.0, 4.0);
    protected static final VoxelShape LEG3B = Block.createCuboidShape(0.0, 0.0, 12.0, 2.0, 8.0, 14.0);
    protected static final VoxelShape LEG4B = Block.createCuboidShape(14.0, 0.0, 12.0, 16.0, 8.0, 14.0);
    protected static final VoxelShape LEG3A = Block.createCuboidShape(0.0, 0.0, 14.0, 4.0, 8.0, 16.0);
    protected static final VoxelShape LEG4A = Block.createCuboidShape(12.0, 0.0, 14.0, 16.0, 8.0, 16.0);

    private static final VoxelShape FULL = VoxelShapes.union(MAIN, LEG1A, LEG1B, LEG2A, LEG2B, LEG3A, LEG3B, LEG4A, LEG4B);

    public SifterBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(ON_OFF_PWR, OFF));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return FULL;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient)
        {
            if (world.getBlockEntity(pos) instanceof SpinningFrameBlockEntity blockEntity && player != null) {
                //player.sendMessage(Text.of("ticks: " + blockEntity.getTicks()), true);
                return ActionResult.SUCCESS;
            }
        }
        return super.onUse(state, world, pos, player, hit);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntityTypes.SIFTER.instantiate(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return state.get(ON_OFF_PWR) != OnOffPwr.OFF ? TickableBlockEntity.getTicker(world) : null;
    }

    @Override
    public void receivePower(World world, BlockPos thisPos, Direction sideFrom, int amount)
    {
        // Schedules to stop
        world.setBlockState(thisPos, world.getBlockState(thisPos).with(ON_OFF_PWR, ON));
        world.scheduleBlockTick(thisPos, this, 10);
    }

    @Override
    public boolean acceptsPower(World world, BlockPos thisPos, Direction sideFrom) {
        return world.getBlockState(thisPos).getProperties().contains(ON_OFF_PWR);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        if (state.getBlock() != this) {
            return;
        }
        if (state.get(ON_OFF_PWR) == SCHEDULED_STOP)
            world.setBlockState(pos, state.with(ON_OFF_PWR, OFF));
        else if (state.get(ON_OFF_PWR) == ON){
            world.scheduleBlockTick(pos, this, 10);
            world.setBlockState(pos, state.with(ON_OFF_PWR, SCHEDULED_STOP));
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ON_OFF_PWR);
    }
}
