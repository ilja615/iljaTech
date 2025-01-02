package com.github.ilja615.iljatech.blocks.turbine;

import com.github.ilja615.iljatech.energy.MechPwrAccepter;
import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.init.ModParticles;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class TurbineBlockEntity extends BlockEntity implements TickableBlockEntity {
    private int ticks = 0;
    private float steamY = -1f;

    public TurbineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.TURBINE, pos, state);
    }

    @Override
    public void tick() {
        if (this.world == null || this.world.isClient)
            return;

        if (steamY != -1.0f) {
            if (steamY < this.pos.getY()) {
                // The steam will travel up to the turbine
                steamY += 0.03f; // Blocks per tick
                if (this.world.random.nextFloat() < 0.2f) {
                    ((ServerWorld) world).spawnParticles(ModParticles.STEAM, this.pos.getX() + 0.5f, steamY, this.pos.getZ() + 0.5f, 4, 0.0f, 0.0f, 0.0f, 0.0);
                }
            }
            if (steamY >= this.pos.getY()) {
                // The steam reached the turbine
                steamY = -1.0f; // Resets the steam
                this.ticks = 200; // Starts the turbine
            }
        }

        if (ticks > 0) {
            this.ticks--;
            Block other = this.world.getBlockState(pos.up()).getBlock();
            if (other instanceof MechPwrAccepter && ((MechPwrAccepter)other).acceptsPower(this.world, pos.up(), Direction.DOWN)) {
                ((TurbineBlock)this.world.getBlockState(pos).getBlock()).sendPower(this.world, pos, Direction.UP, 8);
            }
        }

        // The code for correctly updating the blockState of the TurbineBlock
        BlockState state = world.getBlockState(pos);
        if (this.ticks > 5 && state.get(MechPwrAccepter.ON_OFF_PWR) == MechPwrAccepter.OnOffPwr.OFF) {
            world.setBlockState(pos, state.with(MechPwrAccepter.ON_OFF_PWR, MechPwrAccepter.OnOffPwr.ON));
        } else if (this.ticks <= 5 && this.ticks > 0 && state.get(MechPwrAccepter.ON_OFF_PWR) != MechPwrAccepter.OnOffPwr.SCHEDULED_STOP) {
            world.setBlockState(pos, state.with(MechPwrAccepter.ON_OFF_PWR, MechPwrAccepter.OnOffPwr.SCHEDULED_STOP));
        } else if (this.ticks <= 0 && state.get(MechPwrAccepter.ON_OFF_PWR) != MechPwrAccepter.OnOffPwr.OFF) {
            world.setBlockState(pos, state.with(MechPwrAccepter.ON_OFF_PWR, MechPwrAccepter.OnOffPwr.OFF));
            this.ticks = 0;
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

    public float getSteamY() {
        return steamY;
    }

    public void setSteamY(float startY) {
        this.steamY = startY;
    }
}
