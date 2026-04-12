package com.github.ilja615.iljatech.blocks.firebox;

import com.github.ilja615.iljatech.energy.Heat;
import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class FireboxBlockEntity extends BlockEntity implements TickableBlockEntity {
    private int ticks = 0;
    private int stokedTicks = 0;

    private final SimpleContainer inventory = new SimpleContainer(1) {
        @Override
        public void setChanged() {
            super.setChanged();
            update();
        }
    };
    private final InventoryStorage storage = InventoryStorage.of(inventory,null);

    public FireboxBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.FIREBOX, pos, state);
    }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide)
            return;

        if (ticks > 0) {
            this.ticks--;
            if (ticks % 100 == 0)
            {
                Heat.emitHeat(level, worldPosition.above());
                int ash_level = level.getBlockState(worldPosition).getValue(FireboxBlock.ASH_LEVEL);
                level.setBlock(worldPosition, level.getBlockState(worldPosition).setValue(FireboxBlock.ASH_LEVEL, Math.min(ash_level + 1, 5)), 3);
            }
        }
        if (stokedTicks > 0) {
            this.stokedTicks--;
        }
        ItemStack itemstack = this.inventory.getItem(0);
        if (ticks == 0)
        {
            if (FuelRegistry.INSTANCE.get(itemstack.getItem()) != null && FuelRegistry.INSTANCE.get(itemstack.getItem()) > 0)
                ticks = FuelRegistry.INSTANCE.get(itemstack.getItem());
            if (ticks > 0)
            {
                if (!itemstack.isEmpty()) {
                    itemstack.shrink(1);
                    this.inventory.setItem(0, itemstack);
                }
            }
        }

        if (ticks > 0) {
            if (level.getBlockState(worldPosition).getValue(FireboxBlock.ASH_LEVEL) == 5) {
                level.setBlock(worldPosition, level.getBlockState(worldPosition).setValue(FireboxBlock.LIT, FireboxBlock.Lit.CHOKING), 3);
            } else {
                if (stokedTicks > 0) {
                    level.setBlock(worldPosition, level.getBlockState(worldPosition).setValue(FireboxBlock.LIT, FireboxBlock.Lit.STOKED), 3);
                } else {
                    level.setBlock(worldPosition, level.getBlockState(worldPosition).setValue(FireboxBlock.LIT, FireboxBlock.Lit.ON), 3);
                }
            }
        } else {
            level.setBlock(worldPosition, level.getBlockState(worldPosition).setValue(FireboxBlock.LIT, FireboxBlock.Lit.OFF), 3);
        }

        this.update();
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        this.ticks = nbt.getInt("Ticks");
        ContainerHelper.loadAllItems(nbt, this.inventory.getItems(), registryLookup);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        nbt.putInt("Ticks", this.ticks);
        ContainerHelper.saveAllItems(nbt, this.inventory.getItems(), registryLookup);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        var nbt = super.getUpdateTag(registryLookup);
        saveAdditional(nbt, registryLookup);
        return nbt;
    }

    public int getTicks() {
        return ticks;
    }

    public int getStokedTicks() {
        return stokedTicks;
    }

    public void setStokedTicks(int a) {
        this.stokedTicks = a;
    }

    private void update() {
        setChanged();
        if (level != null)
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    public InventoryStorage getInventoryProvider(Direction direction) {
        return storage;
    }

    public SimpleContainer getInventory() {
        return this.inventory;
    }
}