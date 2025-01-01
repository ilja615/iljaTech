package com.github.ilja615.iljatech.blocks.bellows;

import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

public class BellowsBlockEntity extends BlockEntity implements TickableBlockEntity {
    private int ticks = 0;
    private int schedule_stop = 0;

    public BellowsBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.BELLOWS, pos, state);
    }

    @Override
    public void tick() {
        if (this.world == null || this.world.isClient)
            return;

        if (ticks > 0) {
            this.ticks--;

            BlockState state = world.getBlockState(pos);
            if (!state.isOf(ModBlocks.BELLOWS)) { return; }

            if (ticks == 0) {
                if (state.get(BellowsBlock.PRESS) == 1) {
                    world.setBlockState(pos, state.with(BellowsBlock.PRESS, 2));
                } else if (state.get(BellowsBlock.PRESS) == 3) {
                    world.setBlockState(pos, state.with(BellowsBlock.PRESS, 0));
                }
            }
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.ticks = nbt.getInt("Ticks");
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("Ticks", this.ticks);
    }

    public int getTicks() {
        return ticks;
    }

    public void setTicks(int amountTicksTime) {
        this.ticks = amountTicksTime;
    }

    public void scheduleStopTicks(int amountTicksTime) {
        this.schedule_stop = amountTicksTime;
    }
}
