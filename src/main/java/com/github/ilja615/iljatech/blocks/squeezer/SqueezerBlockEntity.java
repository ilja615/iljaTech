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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SqueezerBlockEntity extends BlockEntity implements TickableBlockEntity, ExtendedScreenHandlerFactory<BlockPosPayload> {
    private int ticks = 0;
    public static final Component TITLE = Component.translatable("container." + IljaTech.MOD_ID + ".squeezer");

    private final SimpleContainer inventory = new SimpleContainer(2) {
        @Override
        public void setChanged() {
            super.setChanged();
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
        if (this.level == null || this.level.isClientSide)
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

            BlockState state = level.getBlockState(worldPosition);
            if (!state.is(ModBlocks.SQUEEZER)) { return; }

            if (ticks == 0) {
                if (state.getValue(SqueezerBlock.PRESS) == 1) {
                    level.setBlockAndUpdate(worldPosition, state.setValue(SqueezerBlock.PRESS, 2));

                    List<RecipeHolder<SqueezingRecipe>> recipes = level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.SQUEEZING_TYPE);
                    for (RecipeHolder<SqueezingRecipe> rr : recipes)
                    {
                        SqueezingRecipe r = rr.value();

                        if (r.matches(new SqueezingRecipe.InputContainer(inventory.getItem(0)), level))
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
                                inventory.getItem(0).shrink(1);
                            }
                            break;
                        }
                    }
                } else if (state.getValue(SqueezerBlock.PRESS) == 3) {
                    level.setBlockAndUpdate(worldPosition, state.setValue(SqueezerBlock.PRESS, 0));
                }
            }
        }
    }

    private void update() {
        setChanged();
        if (level != null)
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    public boolean isValid(ItemStack stack, int slot) {
        if(stack.isEmpty()) return true;

        Storage<FluidVariant> storage = ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM);
        return storage != null;
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        this.ticks = nbt.getInt("Ticks");

        if (nbt.contains("Inventory", Tag.TAG_COMPOUND))
            ContainerHelper.loadAllItems(nbt.getCompound("Inventory"), this.inventory.getItems(), registryLookup);

        if (nbt.contains("FluidTank", Tag.TAG_COMPOUND))
            this.fluidStorage.readNbt(nbt.getCompound("FluidTank"), registryLookup);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        nbt.putInt("Ticks", this.ticks);

        var inventoryNbt = new CompoundTag();
        ContainerHelper.saveAllItems(inventoryNbt, this.inventory.getItems(), registryLookup);
        nbt.put("Inventory", inventoryNbt);

        var fluidNbt = new CompoundTag();
        this.fluidStorage.writeNbt(fluidNbt, registryLookup);
        nbt.put("FluidTank", fluidNbt);
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

    public void setTicks(int amountTicksTime) {
        this.ticks = amountTicksTime;
    }

    public InventoryStorage getInventoryProvider(Direction direction) {
        return storage;
    }

    public SimpleContainer getInventory() {
        return this.inventory;
    }


    public SingleFluidStorage getFluidTankProvider(Direction direction) {
        return this.fluidStorage;
    }

    public SingleFluidStorage getFluidStorage() {
        return this.fluidStorage;
    }

    @Override
    public BlockPosPayload getScreenOpeningData(ServerPlayer player) {
        return new BlockPosPayload(this.worldPosition);
    }

    @Override
    public Component getDisplayName() {
        return TITLE;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new SqueezerScreenHandler(syncId, playerInventory, this, this.inventory);
    }
}
