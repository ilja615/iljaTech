package com.github.ilja615.iljatech.blocks.turbine;

import com.github.ilja615.iljatech.energy.MechPwrAccepter;
import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.init.ModParticles;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TurbineBlockEntity extends BlockEntity implements TickableBlockEntity {
    private int ticks = 0;
    private float steamY = -1f;

    public TurbineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.TURBINE, pos, state);
    }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide)
            return;

        if (steamY != -1.0f) {
            if (steamY < this.worldPosition.getY()) {
                // The steam will travel up to the turbine
                steamY += 0.03f; // Blocks per tick
                if (this.level.random.nextFloat() < 0.2f) {
                    ((ServerLevel) level).sendParticles(ModParticles.STEAM, this.worldPosition.getX() + 0.5f, steamY, this.worldPosition.getZ() + 0.5f, 4, 0.0f, 0.0f, 0.0f, 0.0);
                }
            }
            if (steamY >= this.worldPosition.getY()) {
                // The steam reached the turbine
                steamY = -1.0f; // Resets the steam
                this.ticks = 200; // Starts the turbine
            }
        }

        if (ticks > 0) {
            this.ticks--;
            Block other = this.level.getBlockState(worldPosition.above()).getBlock();
            if (other instanceof MechPwrAccepter && ((MechPwrAccepter)other).acceptsPower(this.level, worldPosition.above(), Direction.DOWN)) {
                ((TurbineBlock)this.level.getBlockState(worldPosition).getBlock()).sendPower(this.level, worldPosition, Direction.UP, 8);
            }
        }

        // The code for correctly updating the blockState of the TurbineBlock
        BlockState state = level.getBlockState(worldPosition);
        if (this.ticks > 5 && state.getValue(MechPwrAccepter.ON_OFF_PWR) == MechPwrAccepter.OnOffPwr.OFF) {
            level.setBlockAndUpdate(worldPosition, state.setValue(MechPwrAccepter.ON_OFF_PWR, MechPwrAccepter.OnOffPwr.ON));
        } else if (this.ticks <= 5 && this.ticks > 0 && state.getValue(MechPwrAccepter.ON_OFF_PWR) != MechPwrAccepter.OnOffPwr.SCHEDULED_STOP) {
            level.setBlockAndUpdate(worldPosition, state.setValue(MechPwrAccepter.ON_OFF_PWR, MechPwrAccepter.OnOffPwr.SCHEDULED_STOP));
        } else if (this.ticks <= 0 && state.getValue(MechPwrAccepter.ON_OFF_PWR) != MechPwrAccepter.OnOffPwr.OFF) {
            level.setBlockAndUpdate(worldPosition, state.setValue(MechPwrAccepter.ON_OFF_PWR, MechPwrAccepter.OnOffPwr.OFF));
            this.ticks = 0;
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

    public float getSteamY() {
        return steamY;
    }

    public void setSteamY(float startY) {
        this.steamY = startY;
    }
}
