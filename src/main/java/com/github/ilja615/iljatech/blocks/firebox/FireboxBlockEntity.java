package com.github.ilja615.iljatech.blocks.firebox;

import com.github.ilja615.iljatech.energy.Heat;
import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class FireboxBlockEntity extends BlockEntity implements TickableBlockEntity {
    private int ticks = 0;
    private int stokedTicks = 0;

    private final SimpleInventory inventory = new SimpleInventory(1) {
        @Override
        public void markDirty() {
            super.markDirty();
            update();
        }
    };
    private final InventoryStorage storage = InventoryStorage.of(inventory,null);

    public FireboxBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.FIREBOX, pos, state);
    }

    @Override
    public void tick() {
        if (this.world == null || this.world.isClient)
            return;

        if (ticks > 0) {
            this.ticks--;
            if (ticks % 100 == 0)
            {
                Heat.emitHeat(world, pos.up());
                int ash_level = world.getBlockState(pos).get(FireboxBlock.ASH_LEVEL);
                world.setBlockState(pos, world.getBlockState(pos).with(FireboxBlock.ASH_LEVEL, Math.min(ash_level + 1, 5)), 3);
            }
        }
        if (stokedTicks > 0) {
            this.stokedTicks--;
        }
        ItemStack itemstack = this.inventory.getStack(0);
        if (ticks == 0)
        {
            if (FuelRegistry.INSTANCE.get(itemstack.getItem()) != null && FuelRegistry.INSTANCE.get(itemstack.getItem()) > 0)
                ticks = FuelRegistry.INSTANCE.get(itemstack.getItem());
            if (ticks > 0)
            {
                if (!itemstack.isEmpty()) {
                    itemstack.decrement(1);
                    this.inventory.setStack(0, itemstack);
                }
            }
        }

        if (ticks > 0) {
            if (world.getBlockState(pos).get(FireboxBlock.ASH_LEVEL) == 5) {
                world.setBlockState(pos, world.getBlockState(pos).with(FireboxBlock.LIT, FireboxBlock.Lit.CHOKING), 3);
            } else {
                if (stokedTicks > 0) {
                    world.setBlockState(pos, world.getBlockState(pos).with(FireboxBlock.LIT, FireboxBlock.Lit.STOKED), 3);
                } else {
                    world.setBlockState(pos, world.getBlockState(pos).with(FireboxBlock.LIT, FireboxBlock.Lit.ON), 3);
                }
            }
        } else {
            world.setBlockState(pos, world.getBlockState(pos).with(FireboxBlock.LIT, FireboxBlock.Lit.OFF), 3);
        }

        this.update();
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.ticks = nbt.getInt("Ticks");
        Inventories.readNbt(nbt, this.inventory.getHeldStacks(), registryLookup);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("Ticks", this.ticks);
        Inventories.writeNbt(nbt, this.inventory.getHeldStacks(), registryLookup);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        var nbt = super.toInitialChunkDataNbt(registryLookup);
        writeNbt(nbt, registryLookup);
        return nbt;
    }

    public int getTicks() {
        return ticks;
    }

    public int getStokedTicks() {
        return ticks;
    }

    public void setStokedTicks(int a) {
        this.stokedTicks = a;
    }

    private void update() {
        markDirty();
        if (world != null)
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
    }

    public InventoryStorage getInventoryProvider(Direction direction) {
        return storage;
    }

    public SimpleInventory getInventory() {
        return this.inventory;
    }
}