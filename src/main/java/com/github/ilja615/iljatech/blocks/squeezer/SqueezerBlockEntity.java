package com.github.ilja615.iljatech.blocks.squeezer;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.blocks.cokeoven.CokeOvenBlock;
import com.github.ilja615.iljatech.blocks.cokeoven.CokingRecipe;
import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.init.ModFluids;
import com.github.ilja615.iljatech.init.ModRecipeTypes;
import com.github.ilja615.iljatech.network.BlockPosPayload;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SqueezerBlockEntity extends BlockEntity implements TickableBlockEntity, ExtendedScreenHandlerFactory<BlockPosPayload> {
    private int ticks = 0;
    public static final Text TITLE = Text.translatable("container." + IljaTech.MOD_ID + ".squeezer");

    private final SimpleInventory inventory = new SimpleInventory(2) {
        @Override
        public void markDirty() {
            super.markDirty();
            update();
        }
    };
    private final InventoryStorage storage = InventoryStorage.of(inventory,null);

    private final SingleFluidStorage fluidStorage = SingleFluidStorage.withFixedCapacity(
            FluidConstants.BUCKET * 16,
            this::update);
    private final ContainerItemContext fluidItemContext = ContainerItemContext.ofSingleSlot(InventoryStorage.of(inventory, null).getSlot(1));


    public SqueezerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.SQUEEZER, pos, state);
    }

    @Override
    public void tick() {
        if (this.world == null || this.world.isClient)
            return;

        // Transfer fluids to the bucket or item if there is one
        Storage<FluidVariant> itemFluidStorage  = this.fluidItemContext.find(FluidStorage.ITEM);
        if (itemFluidStorage != null && fluidStorage.amount >= FluidConstants.BUCKET) {
            long acceptedAmount = 0;
            long transferredAmount = 0;
            try(Transaction transaction = Transaction.openOuter()) {
                acceptedAmount = itemFluidStorage.insert(FluidVariant.of(ModFluids.STILL_SEED_OIL), FluidConstants.BUCKET, transaction);
                System.out.println(acceptedAmount);
                if (acceptedAmount == FluidConstants.BUCKET) {
                    transferredAmount = fluidStorage.extract(FluidVariant.of(ModFluids.STILL_SEED_OIL), FluidConstants.BUCKET, transaction);
                    System.out.println(transferredAmount);
                    if (transferredAmount == FluidConstants.BUCKET) {
                        transaction.commit();
                        update();
                    }
                }
            }
        }

        if (ticks > 0) {
            this.ticks--;

            BlockState state = world.getBlockState(pos);
            if (!state.isOf(ModBlocks.SQUEEZER)) { return; }

            if (ticks == 0) {
                if (state.get(SqueezerBlock.PRESS) == 1) {
                    world.setBlockState(pos, state.with(SqueezerBlock.PRESS, 2));

                    List<RecipeEntry<SqueezingRecipe>> recipes = world.getRecipeManager().listAllOfType(ModRecipeTypes.SQUEEZING_TYPE);
                    for (RecipeEntry<SqueezingRecipe> rr : recipes)
                    {
                        SqueezingRecipe r = rr.value();

                        if (r.matches(new SqueezingRecipe.InputContainer(inventory.getStack(0)), world))
                        {
                            // The recipe is finished. The output is handled.
                            long insertedAmount = 0;
                            try(Transaction transaction = Transaction.openOuter()) {
                                insertedAmount = fluidStorage.insert(r.fluidOutput(), r.fluidAmount(), transaction);
                                if (insertedAmount > 0) {
                                    transaction.commit();
                                    update();
                                }
                            }
                            if (insertedAmount > 0) {
                                inventory.getStack(0).decrement(1);
                            }
                            break;
                        }
                    }
                } else if (state.get(SqueezerBlock.PRESS) == 3) {
                    world.setBlockState(pos, state.with(SqueezerBlock.PRESS, 0));
                }
            }
        }
    }

    private void update() {
        markDirty();
        if (world != null)
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
    }

    public boolean isValid(ItemStack stack, int slot) {
        if(stack.isEmpty()) return true;

        Storage<FluidVariant> storage = ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM);
        return storage != null;
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.ticks = nbt.getInt("Ticks");

        if (nbt.contains("Inventory", NbtElement.COMPOUND_TYPE))
            Inventories.readNbt(nbt.getCompound("Inventory"), this.inventory.getHeldStacks(), registryLookup);

        if (nbt.contains("FluidTank", NbtElement.COMPOUND_TYPE))
            this.fluidStorage.readNbt(nbt.getCompound("FluidTank"), registryLookup);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("Ticks", this.ticks);

        var inventoryNbt = new NbtCompound();
        Inventories.writeNbt(inventoryNbt, this.inventory.getHeldStacks(), registryLookup);
        nbt.put("Inventory", inventoryNbt);

        var fluidNbt = new NbtCompound();
        this.fluidStorage.writeNbt(fluidNbt, registryLookup);
        nbt.put("FluidTank", fluidNbt);
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

    public void setTicks(int amountTicksTime) {
        this.ticks = amountTicksTime;
    }

    public InventoryStorage getInventoryProvider(Direction direction) {
        return storage;
    }

    public SimpleInventory getInventory() {
        return this.inventory;
    }


    public SingleFluidStorage getFluidTankProvider(Direction direction) {
        return this.fluidStorage;
    }

    public SingleFluidStorage getFluidStorage() {
        return this.fluidStorage;
    }

    @Override
    public BlockPosPayload getScreenOpeningData(ServerPlayerEntity player) {
        return new BlockPosPayload(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return TITLE;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new SqueezerScreenHandler(syncId, playerInventory, this, this.inventory);
    }
}
