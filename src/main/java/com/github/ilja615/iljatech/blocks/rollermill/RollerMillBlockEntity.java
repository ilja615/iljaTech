package com.github.ilja615.iljatech.blocks.rollermill;

import com.github.ilja615.iljatech.blocks.conveyorbelt.ConveyorBeltBlockEntity;
import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.init.ModRecipeTypes;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class RollerMillBlockEntity extends BlockEntity implements TickableBlockEntity {
    private int ticks = 0;

    // TODO: Might change it to SidedInventory later
    private final SimpleInventory inventory = new SimpleInventory(1) {
        @Override
        public void markDirty() {
            super.markDirty();
            update();
        }
    };
    private final InventoryStorage storage = InventoryStorage.of(inventory,null);

    public RollerMillBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.ROLLER_MILL, pos, state);
    }

    @Override
    public void tick() {
        if (this.world == null)
            return;

        ItemStack stack0 = this.inventory.getStack(0);
        if (stack0.isEmpty()) {
            this.ticks = 0;
        }
        if (ticks++ > 100) {
            this.ticks = 0;

            Direction direction = this.getCachedState().get(RollerMillBlock.FACING);
            List<RecipeEntry<RollingRecipe>> recipes = world.getRecipeManager().listAllOfType(ModRecipeTypes.ROLLING_TYPE);
            for (RecipeEntry<RollingRecipe> rr : recipes)
            {
                RollingRecipe r = rr.value();
                ItemStack resultingStack = r.output().copy();
                if (r.stack().getMatchingStacks()[0].isEmpty())
                    continue;

                if (r.stack().getMatchingStacks()[0].getItem() == stack0.getItem())
                {
                    this.inventory.getStack(0).decrement(1);
                    Vec3d outputPos = pos.offset(direction).toCenterPos();
                    world.spawnEntity(new ItemEntity(world, outputPos.getX(), outputPos.getY(), outputPos.getZ(), resultingStack, 0d, 0d, 0d));
                    ((ServerWorld) this.world).getChunkManager().markForUpdate(this. getPos());
                    this.update();
                    break;
                }
            }
        }
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
