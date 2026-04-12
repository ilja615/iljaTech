package com.github.ilja615.iljatech.blocks.sifter;

import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.init.ModItems;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SifterBlockEntity extends BlockEntity implements TickableBlockEntity, WorldlyContainer {
    private int ticks = 0;
    private static final int[] EMPTY = new int[0];

    private final SimpleContainer inventory = new SimpleContainer(1) {
        @Override
        public void setChanged() {
            super.setChanged();
            update();
        }
    };
    private final InventoryStorage storage = InventoryStorage.of(inventory,null);


    public SifterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.SIFTER, pos, state);
    }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide())
            return;

        ItemStack stack0 = this.inventory.getItem(0);
        if (stack0.isEmpty()) {
            this.ticks = 0;
            if (!level.getBlockState(worldPosition.above()).isAir()) {
                // TODO : check if is one of the recipe's inputs
                this.inventory.setItem(0, new ItemStack(level.getBlockState(worldPosition.above()).getBlock(), 1));
                level.setBlockAndUpdate(worldPosition.above(), Blocks.AIR.defaultBlockState());
                this.update();
            }
        } else {
            if (level.random.nextFloat() > 0.8f && !this.inventory.getItem(0).isEmpty())
                ((ServerLevel) level).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, this.inventory.getItem(0)), worldPosition.getX() + 0.5d + (level.random.nextFloat() - 0.5) * 0.25, worldPosition.getY() + 0.45d, worldPosition.getZ() + 0.5d + (level.random.nextFloat() - 0.5) * 0.25, 4, 0.0f, -0.1f, 0.0f, 0.0);

        }
        if (ticks++ > 100) {
            this.ticks = 0;

            this.inventory.setItem(0, this.inventory.getItem(0).copyWithCount(this.inventory.getItem(0).getCount() -1));
            Vec3 outputPos = worldPosition.getCenter().add(0.0d, -0.4d, 0.0d);
            ItemStack resultingStack = new ItemStack(ModItems.SULFUR);
            level.addFreshEntity(new ItemEntity(level, outputPos.x(), outputPos.y(), outputPos.z(), resultingStack, 0d, 0d, 0d));
            ((ServerLevel) this.level).getChunkSource().blockChanged(this. getBlockPos());
            this.update();
        }
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        this.ticks = nbt.getInt("Ticks");

        ListTag nbtList = nbt.getList("Items", Tag.TAG_COMPOUND);
        for(int i = 0; i < nbtList.size(); ++i) {
            CompoundTag nbtCompound = nbtList.getCompound(i);
            int j = nbtCompound.getByte("Slot") & 255;
            if (j >= 0 && j < this.inventory.getContainerSize()) {
                this.inventory.setItem(j, ItemStack.parse(registryLookup, nbtCompound).orElse(ItemStack.EMPTY));
            }
        }
        //Inventories.readNbt(nbt, this.inventory.getHeldStacks(), registryLookup);
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

    private void update() {
        getUpdatePacket();
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

    @Override
    public int[] getSlotsForFace(Direction side) {
        return EMPTY;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) {
        return this.canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return false;
    }

    @Override
    public int getContainerSize() {
        return this.inventory.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.inventory.getItem(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack itemStack = ContainerHelper.removeItem(this.inventory.getItems(), slot, amount);
        if (!itemStack.isEmpty()) {
            this.setChanged();
        }

        return itemStack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.inventory.getItems(), slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        ItemStack itemStack = (ItemStack)this.inventory.getItem(slot);
        boolean bl = !stack.isEmpty() && ItemStack.isSameItemSameComponents(itemStack, stack);
        this.inventory.setItem(slot, stack);
        stack.limitSize(this.getMaxStackSize(stack));
        if (slot == 0 && !bl) {
            this.ticks = 0;
            this.setChanged();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.inventory.getItems().clear();
    }
}
