package com.github.ilja615.iljatech.blocks.rollermill;

import com.github.ilja615.iljatech.blocks.conveyorbelt.ConveyorBeltBlockEntity;
import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.init.ModRecipeTypes;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class RollerMillBlockEntity extends BlockEntity implements TickableBlockEntity {
    private int ticks = 0;

    // TODO: Might change it to SidedInventory later
    private final SimpleContainer inventory = new SimpleContainer(1) {
        @Override
        public void setChanged() {
            super.setChanged();
            update();
        }
    };
    private final InventoryStorage storage = InventoryStorage.of(inventory,null);

    public RollerMillBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.ROLLER_MILL, pos, state);
    }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide())
            return;

        ItemStack stack0 = this.inventory.getItem(0);
        if (stack0.isEmpty()) {
            this.ticks = 0;
        }
        if (ticks++ > 100) {
            this.ticks = 0;

            Direction direction = this.getBlockState().getValue(RollerMillBlock.FACING);
            List<RecipeHolder<RollingRecipe>> recipes = level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.ROLLING_TYPE);
            for (RecipeHolder<RollingRecipe> rr : recipes)
            {
                RollingRecipe r = rr.value();
                ItemStack resultingStack = r.output().copy();
                if (r.stack().getItems()[0].isEmpty())
                    continue;

                if (r.stack().getItems()[0].getItem() == stack0.getItem())
                {
                    this.inventory.setItem(0, this.inventory.getItem(0).copyWithCount(this.inventory.getItem(0).getCount() -1));
                    Vec3 outputPos = worldPosition.relative(direction).getCenter();
                    level.addFreshEntity(new ItemEntity(level, outputPos.x(), outputPos.y(), outputPos.z(), resultingStack, 0d, 0d, 0d));
                    ((ServerLevel) this.level).getChunkSource().blockChanged(this. getBlockPos());
                    this.update();
                    break;
                }
            }
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
