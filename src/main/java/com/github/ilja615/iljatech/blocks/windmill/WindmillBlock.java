package com.github.ilja615.iljatech.blocks.windmill;

import com.github.ilja615.iljatech.energy.MechPwrAccepter;
import com.github.ilja615.iljatech.energy.MechPwrSender;
import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static com.github.ilja615.iljatech.energy.MechPwrAccepter.ON_OFF_PWR;
import static com.github.ilja615.iljatech.energy.MechPwrAccepter.OnOffPwr.OFF;

public class WindmillBlock extends Block implements BlockEntityProvider, MechPwrSender {
    public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;

    public WindmillBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(ON_OFF_PWR, OFF).with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, ON_OFF_PWR);
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
}
