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
import net.minecraft.util.Tuple;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import java.util.Optional;

public class CarpentryScreenHandler extends AbstractContainerMenu {
    private final CarpentryBlockEntity blockEntity;
    private final SimpleContainer inventory;
    private final ContainerLevelAccess context;

    // Client Constructor
    public CarpentryScreenHandler(int syncId, Inventory playerInventory, BlockPosPayload payload) {
        super(ModScreenHandlerTypes.CARPENTRY, syncId);

        this.blockEntity = (CarpentryBlockEntity) playerInventory.player.level().getBlockEntity(payload.pos());
        this.context = ContainerLevelAccess.create(this.blockEntity.getLevel(), this.blockEntity.getBlockPos());
        this.inventory = new SimpleContainer(7);

        addSlots(playerInventory);
    }

    // Main Constructor - (Directly called from server)
    public CarpentryScreenHandler(int syncId, Inventory playerInventory, CarpentryBlockEntity blockEntity, SimpleContainer inventory) {
        super(ModScreenHandlerTypes.CARPENTRY, syncId);

        this.blockEntity = blockEntity;
        this.context = ContainerLevelAccess.create(this.blockEntity.getLevel(), this.blockEntity.getBlockPos());
        this.inventory = inventory;

        addSlots(playerInventory);
    }

    private void addSlots(Inventory playerInventory) {
        addSlot(new MaxStackSize1Slot(inventory, 0, 44, 26));
        addSlot(new MaxStackSize1Slot(inventory, 1, 62, 35));
        addSlot(new MaxStackSize1Slot(inventory, 2, 26, 35));
        addSlot(new MaxStackSize1Slot(inventory, 3, 44, 44));
        addSlot(new Slot(inventory, 4, 8, 17)); // Nails slot
        addSlot(new Slot(inventory, 5, 148, 35){ // Output slot
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        addSlot(new FluidItemSlot(inventory, blockEntity.getFluidStorage(), blockEntity::update, 6, 103, 17)); // Fluid slot

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    @Override
    public void clicked(int slotIndex, int button, ClickType actionType, Player player) {
        ItemStack cursorStack = this.getCarried();
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
                                        this.inventory.setChanged();
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
                                this.inventory.setChanged();
                                this.blockEntity.update();
                            }
                        }
                    }
                }
            }
        } else {
            super.clicked(slotIndex, button, actionType, player);
        }
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        switch (id) {
            case 0 -> {hammer(); return true;}
            case 1 -> {saw(); return true;}
            case 2 -> {finish(); return true;}
            case 3 -> {this.inventory.setChanged(); blockEntity.checkRecipes(); return true;}
            default -> {
                return false;
            }
        }
    }

    public void hammer() {
        this.blockEntity.hammer();
        this.inventory.setChanged();
    }

    public void saw() {
        this.blockEntity.saw();
        this.inventory.setChanged();
    }

    public void finish() {
        this.blockEntity.finish();
        this.inventory.setChanged();
    }

    public Tuple<Boolean, String> canHammer() {
        if (!inventory.getItem(4).isEmpty() && inventory.getItem(4).is(ModItems.IRON_NAILS)) {
            return new Tuple<>(true, "");
        } else {
            return new Tuple<>(false, "Missing Nails");
        }
    }

    public Tuple<Boolean, String> canFinish() {
        this.inventory.setChanged();
        String craftingStatus = this.blockEntity.getCraftingStatus();
        if (craftingStatus.isEmpty()) {
            return new Tuple<>(true, "");
        } else {
            return new Tuple<>(false, craftingStatus);
        }
    }

    private void addPlayerInventory(Inventory playerInv) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInv, 9 + (column + (row * 9)), 8 + (column * 18), 84 + (row * 18)));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInv) {
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInv, column, 8 + (column * 18), 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.context, player, ModBlocks.CARPENTRY);
    }

    public CarpentryBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

//    private static class CarpentryInventory extends SimpleInventory {
//        private final ScreenHandler handler;
//
//        public CarpentryInventory(CarpentryScreenHandler handler, int size) {
//            super(size);
//            this.handler = handler;
//        }
//
//        @Override
//        public ItemStack addStack(ItemStack stack) {
//            this.handler.onContentChanged(this);
//            return super.addStack(stack);
//        }
//
//        @Override
//        public ItemStack removeStack(int slot) {
//            this.handler.onContentChanged(this);
//            return super.removeStack(slot);
//        }
//
//        @Override
//        public void setStack(int slot, ItemStack stack) {
//            this.handler.onContentChanged(this);
//            super.setStack(slot, stack);
//        }
//    }
}