package com.github.ilja615.iljatech.blocks.sifter;

import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.init.ModItems;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class SifterBlockEntity extends BlockEntity implements TickableBlockEntity, SidedInventory {
    private int ticks = 0;
    private static final int[] EMPTY = new int[0];

    private final SimpleInventory inventory = new SimpleInventory(1) {
        @Override
        public void markDirty() {
            super.markDirty();
            update();
        }
    };
    private final InventoryStorage storage = InventoryStorage.of(inventory,null);


    public SifterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.SIFTER, pos, state);
    }

    @Override
    public void tick() {
        if (this.world == null || this.world.isClient())
            return;

        ItemStack stack0 = this.inventory.getStack(0);
        if (stack0.isEmpty()) {
            this.ticks = 0;
            if (!world.getBlockState(pos.up()).isAir()) {
                // TODO : check if is one of the recipe's inputs
                this.inventory.setStack(0, new ItemStack(world.getBlockState(pos.up()).getBlock(), 1));
                world.setBlockState(pos.up(), Blocks.AIR.getDefaultState());
                this.update();
            }
        } else {
            if (world.random.nextFloat() > 0.8f && !this.inventory.getStack(0).isEmpty())
                ((ServerWorld) world).spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, this.inventory.getStack(0)), pos.getX() + 0.5d + (world.random.nextFloat() - 0.5) * 0.25, pos.getY() + 0.45d, pos.getZ() + 0.5d + (world.random.nextFloat() - 0.5) * 0.25, 4, 0.0f, -0.1f, 0.0f, 0.0);

        }
        if (ticks++ > 100) {
            this.ticks = 0;

            this.inventory.setStack(0, this.inventory.getStack(0).copyWithCount(this.inventory.getStack(0).getCount() -1));
            Vec3d outputPos = pos.toCenterPos().add(0.0d, -0.4d, 0.0d);
            ItemStack resultingStack = new ItemStack(ModItems.SULFUR);
            world.spawnEntity(new ItemEntity(world, outputPos.getX(), outputPos.getY(), outputPos.getZ(), resultingStack, 0d, 0d, 0d));
            ((ServerWorld) this.world).getChunkManager().markForUpdate(this. getPos());
            this.update();
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.ticks = nbt.getInt("Ticks");

        NbtList nbtList = nbt.getList("Items", NbtElement.COMPOUND_TYPE);
        for(int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            int j = nbtCompound.getByte("Slot") & 255;
            if (j >= 0 && j < this.inventory.size()) {
                this.inventory.setStack(j, ItemStack.fromNbt(registryLookup, nbtCompound).orElse(ItemStack.EMPTY));
            }
        }
        //Inventories.readNbt(nbt, this.inventory.getHeldStacks(), registryLookup);
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

    @Override
    public int[] getAvailableSlots(Direction side) {
        return EMPTY;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return this.isValid(slot, stack);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.getStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack itemStack = Inventories.splitStack(this.inventory.getHeldStacks(), slot, amount);
        if (!itemStack.isEmpty()) {
            this.markDirty();
        }

        return itemStack;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory.getHeldStacks(), slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        ItemStack itemStack = (ItemStack)this.inventory.getStack(slot);
        boolean bl = !stack.isEmpty() && ItemStack.areItemsAndComponentsEqual(itemStack, stack);
        this.inventory.setStack(slot, stack);
        stack.capCount(this.getMaxCount(stack));
        if (slot == 0 && !bl) {
            this.ticks = 0;
            this.markDirty();
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return Inventory.canPlayerUse(this, player);
    }

    @Override
    public void clear() {
        this.inventory.getHeldStacks().clear();
    }
}
