package com.github.ilja615.iljatech.blocks.windmill;

import com.github.ilja615.iljatech.blocks.turbine.TurbineBlock;
import com.github.ilja615.iljatech.energy.MechPwrAccepter;
import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.init.ModParticles;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class WindmillBlockEntity extends BlockEntity implements TickableBlockEntity {
    private int ticks = 0;

    public WindmillBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.WINDMILL, pos, state);
    }

    @Override
    public void tick() {
        if (world.isClient)
            return;
        if (getCachedState().get(MechPwrAccepter.ON_OFF_PWR) != MechPwrAccepter.OnOffPwr.ON)
            return;
        ticks++;
        update();
        if (ticks >= 16) {
            ticks = 0;
            Direction dir = getCachedState().get(WindmillBlock.FACING).getOpposite();
            Block other = this.world.getBlockState(pos.offset(dir)).getBlock();
            if (other instanceof MechPwrAccepter && ((MechPwrAccepter) other).acceptsPower(this.world, pos.offset(dir), dir.getOpposite())) {
                ((WindmillBlock) this.world.getBlockState(pos).getBlock()).sendPower(this.world, pos, dir, 4);
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
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.ticks = nbt.getInt("Ticks");
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("Ticks", this.ticks);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public int getTicks() {
        return ticks;
    }

    private void update() {
        markDirty();
        if (world != null)
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
    }
}
