package com.github.ilja615.iljatech.blocks.carpentry;

import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.init.ModFluids;
import com.github.ilja615.iljatech.init.ModItems;
import com.github.ilja615.iljatech.init.ModScreenHandlerTypes;
import com.github.ilja615.iljatech.network.BlockPosPayload;
import com.github.ilja615.iljatech.util.FluidItemSlot;
import com.github.ilja615.iljatech.util.MaxStackSize1Slot;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Pair;

import java.util.Optional;

public class CarpentryScreenHandler extends ScreenHandler {
    private final CarpentryBlockEntity blockEntity;
    private final SimpleInventory inventory;
    private final ScreenHandlerContext context;

    // Client Constructor
    public CarpentryScreenHandler(int syncId, PlayerInventory playerInventory, BlockPosPayload payload) {
        this(syncId, playerInventory, (CarpentryBlockEntity) playerInventory.player.getWorld().getBlockEntity(payload.pos()), new SimpleInventory(7));
    }

    // Main Constructor - (Directly called from server)
    public CarpentryScreenHandler(int syncId, PlayerInventory playerInventory, CarpentryBlockEntity blockEntity, SimpleInventory inventory) {
        super(ModScreenHandlerTypes.CARPENTRY, syncId);

        this.blockEntity = blockEntity;
        this.context = ScreenHandlerContext.create(this.blockEntity.getWorld(), this.blockEntity.getPos());
        this.inventory = inventory;

        if (blockEntity.getLayout() == 0) {
            addSlot(new MaxStackSize1Slot(inventory, 0, 44, 35));
            addSlot(new MaxStackSize1Slot(inventory, 1, 62, 35));
            addSlot(new MaxStackSize1Slot(inventory, 2, 44, 53));
            addSlot(new MaxStackSize1Slot(inventory, 3, 62, 53));
        }
        if (blockEntity.getLayout() == 1) {
            addSlot(new MaxStackSize1Slot(inventory, 0, 53, 35));
            addSlot(new MaxStackSize1Slot(inventory, 1, 71, 44));
            addSlot(new MaxStackSize1Slot(inventory, 2, 35, 44));
            addSlot(new MaxStackSize1Slot(inventory, 3, 53, 53));
        }
        addSlot(new Slot(inventory, 4, 26, 17)); // Nails slot
        addSlot(new Slot(inventory, 5, 140, 44){ // Output slot
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }
        });
        addSlot(new FluidItemSlot(inventory, blockEntity.getFluidStorage(), blockEntity::update, 6, 109, 17)); // Fluid slot

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        ItemStack cursorStack = this.getCursorStack();
        if (slotIndex == 6) {
            Storage<FluidVariant> itemFluidStorage = ContainerItemContext.ofPlayerCursor(player, this).find(FluidStorage.ITEM);
            if (itemFluidStorage != null) {
                if (button == 0) {
                    // Left click is try add fluid into slot
                    itemFluidStorage.iterator().forEachRemaining(fluidVariantStorageView -> {
                        if (fluidVariantStorageView.getAmount() > 0.0f) {
                            long acceptedAmount = 0;
                            long transferredAmount = 0;
                            try(Transaction transaction = Transaction.openOuter()) {
                                acceptedAmount = this.blockEntity.getFluidStorage().insert(fluidVariantStorageView.getResource(), fluidVariantStorageView.getAmount(), transaction);
                                System.out.println(acceptedAmount);
                                if (acceptedAmount > 0.0f) {
                                    transferredAmount = itemFluidStorage.extract(fluidVariantStorageView.getResource(), acceptedAmount, transaction);
                                    System.out.println(transferredAmount);
                                    if (transferredAmount == acceptedAmount) {
                                        transaction.commit();
                                        this.inventory.markDirty();
                                        this.blockEntity.update();
                                    }
                                }
                            }
                        }
                    });
                } else if (button == 1 && this.blockEntity.getFluidStorage().getAmount() > 0.0f) {
                    // Right click is try retreive fluid from slot
                    long acceptedAmount = 0;
                    long transferredAmount = 0;
                    try(Transaction transaction = Transaction.openOuter()) {
                        acceptedAmount = itemFluidStorage.insert(this.blockEntity.getFluidStorage().variant, FluidConstants.BUCKET, transaction);
                        System.out.println(acceptedAmount);
                        if (acceptedAmount > 0.0f) {
                            transferredAmount = this.blockEntity.getFluidStorage().extract(this.blockEntity.getFluidStorage().variant, acceptedAmount, transaction);
                            System.out.println(transferredAmount);
                            if (transferredAmount == acceptedAmount) {
                                transaction.commit();
                                this.inventory.markDirty();
                                this.blockEntity.update();
                            }
                        }
                    }
                }
            }
        } else {
            super.onSlotClick(slotIndex, button, actionType, player);
        }
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        switch (id) {
            case 0 -> {hammer(); return true;}
            case 1 -> {saw(); return true;}
            case 2 -> {grid(); return true;}
            case 3 -> {finish(); return true;}
            default -> {
                return false;
            }
        }
    }

    public void changeLayoutSlots(int layout) {
        if (layout == 0) {
            this.slots.set(0,new MaxStackSize1Slot(inventory, 0, 44, 35));
            this.slots.set(1,new MaxStackSize1Slot(inventory, 1, 62, 35));
            this.slots.set(2,new MaxStackSize1Slot(inventory, 2, 44, 53));
            this.slots.set(3,new MaxStackSize1Slot(inventory, 3, 62, 53));
        }
        if (layout == 1) {
            this.slots.set(0,new MaxStackSize1Slot(inventory, 0, 53, 35));
            this.slots.set(1,new MaxStackSize1Slot(inventory, 1, 71, 44));
            this.slots.set(2,new MaxStackSize1Slot(inventory, 2, 35, 44));
            this.slots.set(3,new MaxStackSize1Slot(inventory, 3, 53, 53));
        }
        this.inventory.markDirty();
    }

    public void hammer() {
        this.blockEntity.hammer();
        this.inventory.markDirty();
    }

    public void saw() {
        this.blockEntity.saw();
        this.inventory.markDirty();
    }

    public void finish() {
        this.blockEntity.finish();
        this.inventory.markDirty();
    }

    public void grid() {
        this.blockEntity.grid();
        this.inventory.markDirty();
    }

    public Pair<Boolean, String> canHammer() {
        if (!inventory.getStack(4).isEmpty() && inventory.getStack(4).isOf(ModItems.IRON_NAILS)) {
            return new Pair<>(true, "");
        } else {
            return new Pair<>(false, "Missing Nails");
        }
    }

    public Pair<Boolean, String> canFinish() {
        this.inventory.markDirty();
        this.blockEntity.checkRecipes();
        String craftingStatus = this.blockEntity.getCraftingStatus();
        if (craftingStatus.isEmpty()) {
            return new Pair<>(true, "");
        } else {
            return new Pair<>(false, craftingStatus);
        }
    }

    private void addPlayerInventory(PlayerInventory playerInv) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInv, 9 + (column + (row * 9)), 8 + (column * 18), 84 + (row * 18)));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInv) {
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInv, column, 8 + (column * 18), 142));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(this.context, player, ModBlocks.CARPENTRY);
    }

    public CarpentryBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    public int getLayout() {
        return this.blockEntity.getLayout();
    }
}