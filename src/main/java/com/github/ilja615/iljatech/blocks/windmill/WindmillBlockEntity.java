package com.github.ilja615.iljatech.blocks.windmill;

import com.github.ilja615.iljatech.blocks.turbine.TurbineBlock;
import com.github.ilja615.iljatech.energy.MechPwrAccepter;
import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.init.ModParticles;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class WindmillBlockEntity extends BlockEntity implements TickableBlockEntity {
    private int ticks = 0;

    public WindmillBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.WINDMILL, pos, state);
    }

    @Override
    public void tick() {
        if (level.isClientSide)
            return;
        if (getBlockState().getValue(MechPwrAccepter.ON_OFF_PWR) != MechPwrAccepter.OnOffPwr.ON)
            return;
        ticks++;
        update();
        if (ticks >= 16) {
            ticks = 0;
            Direction dir = getBlockState().getValue(WindmillBlock.FACING).getOpposite();
            Block other = this.level.getBlockState(worldPosition.relative(dir)).getBlock();
            if (other instanceof MechPwrAccepter && ((MechPwrAccepter) other).acceptsPower(this.level, worldPosition.relative(dir), dir.getOpposite())) {
                ((WindmillBlock) this.level.getBlockState(worldPosition).getBlock()).sendPower(this.level, worldPosition, dir, 4);
            }
        }
//        if (world.random.nextFloat() < 0.5f) {
//            Direction dir = getCachedState().get(WindmillBlock.FACING);
//            int x = dir.getAxis() == Direction.Axis.X ? 0 : world.random.nextInt(12) - 6;
//            int z = dir.getAxis() == Direction.Axis.Z ? 0 : world.random.nextInt(12) - 6;
//            double y = Math.sqrt(36 - x*x - z*z) * (world.random.nextInt(2)*2-1);
//            if (world instanceof ServerWorld serverWorld)
//                serverWorld.spawnParticles(ModParticles.WIND_LEADING, pos.getX() + x + 0.5d, pos.getY() + Math.floor(y) + 0.5d, pos.getZ() + z + 0.5d, 1, 0.0, 0.0, 0.0, 0.0);
//        }
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

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        return saveWithoutMetadata(registryLookup);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public int getTicks() {
        return ticks;
    }

    private void update() {
        setChanged();
        if (level != null)
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }
}
