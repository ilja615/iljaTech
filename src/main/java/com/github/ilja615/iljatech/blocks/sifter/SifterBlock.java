package com.github.ilja615.iljatech.blocks.sifter;

import com.github.ilja615.iljatech.blocks.spinningframe.SpinningFrameBlockEntity;
import com.github.ilja615.iljatech.energy.MechPwrAccepter;
import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import static com.github.ilja615.iljatech.energy.MechPwrAccepter.OnOffPwr.*;

public class SifterBlock extends Block implements EntityBlock, MechPwrAccepter {

    protected static final VoxelShape MAIN = Block.box(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape LEG1A = Block.box(0.0, 0.0, 0.0, 4.0, 8.0, 2.0);
    protected static final VoxelShape LEG1B = Block.box(0.0, 0.0, 2.0, 2.0, 8.0, 4.0);
    protected static final VoxelShape LEG2A = Block.box(12.0, 0.0, 0.0, 16.0, 8.0, 2.0);
    protected static final VoxelShape LEG2B = Block.box(14.0, 0.0, 2.0, 16.0, 8.0, 4.0);
    protected static final VoxelShape LEG3B = Block.box(0.0, 0.0, 12.0, 2.0, 8.0, 14.0);
    protected static final VoxelShape LEG4B = Block.box(14.0, 0.0, 12.0, 16.0, 8.0, 14.0);
    protected static final VoxelShape LEG3A = Block.box(0.0, 0.0, 14.0, 4.0, 8.0, 16.0);
    protected static final VoxelShape LEG4A = Block.box(12.0, 0.0, 14.0, 16.0, 8.0, 16.0);

    private static final VoxelShape FULL = Shapes.or(MAIN, LEG1A, LEG1B, LEG2A, LEG2B, LEG3A, LEG3B, LEG4A, LEG4B);

    public SifterBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(ON_OFF_PWR, OFF));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return FULL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (!world.isClientSide)
        {
            if (world.getBlockEntity(pos) instanceof SpinningFrameBlockEntity blockEntity && player != null) {
                //player.sendMessage(Text.of("ticks: " + blockEntity.getTicks()), true);
                return InteractionResult.SUCCESS;
            }
        }
        return super.useWithoutItem(state, world, pos, player, hit);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntityTypes.SIFTER.create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return state.getValue(ON_OFF_PWR) != OnOffPwr.OFF ? TickableBlockEntity.getTicker(world) : null;
    }

    @Override
    public void receivePower(Level world, BlockPos thisPos, Direction sideFrom, int amount)
    {
        // Schedules to stop
        world.setBlockAndUpdate(thisPos, world.getBlockState(thisPos).setValue(ON_OFF_PWR, ON));
        world.scheduleTick(thisPos, this, 10);
    }

    @Override
    public boolean acceptsPower(Level world, BlockPos thisPos, Direction sideFrom) {
        return world.getBlockState(thisPos).getProperties().contains(ON_OFF_PWR);
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
        builder.add(ON_OFF_PWR);
    }
}
