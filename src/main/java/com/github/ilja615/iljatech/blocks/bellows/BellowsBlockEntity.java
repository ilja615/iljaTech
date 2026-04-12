package com.github.ilja615.iljatech.blocks.bellows;

import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BellowsBlockEntity extends BlockEntity implements TickableBlockEntity {
    private int ticks = 0;

    public BellowsBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.BELLOWS, pos, state);
    }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide)
            return;

        if (ticks > 0) {
            this.ticks--;

            BlockState state = level.getBlockState(worldPosition);
            if (!state.is(ModBlocks.BELLOWS)) { return; }

            if (ticks == 0) {
                if (state.getValue(BellowsBlock.PRESS) == 1) {
                    level.setBlockAndUpdate(worldPosition, state.setValue(BellowsBlock.PRESS, 2));
                } else if (state.getValue(BellowsBlock.PRESS) == 3) {
                    level.setBlockAndUpdate(worldPosition, state.setValue(BellowsBlock.PRESS, 0));
                }
            }
        }
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        this.ticks = nbt.getInt("Ticks");
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        nbt.putInt("Ticks", this.ticks);
    }

    public int getTicks() {
        return ticks;
    }

    public void setTicks(int amountTicksTime) {
        this.ticks = amountTicksTime;
    }
}
