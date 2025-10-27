package com.github.ilja615.iljatech.blocks.carpentry;

import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.init.ModScreenHandlerTypes;
import com.github.ilja615.iljatech.network.BlockPosPayload;
import com.github.ilja615.iljatech.util.MaxStackSize1Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;

public class CarpentryScreenHandler extends ScreenHandler {
    private final CarpentryBlockEntity blockEntity;
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

        addSlot(new MaxStackSize1Slot(inventory, 0, 17, 35));
        addSlot(new MaxStackSize1Slot(inventory, 1, 35, 35));
        addSlot(new MaxStackSize1Slot(inventory, 2, 17, 53));
        addSlot(new MaxStackSize1Slot(inventory, 3, 35, 53));
        addSlot(new Slot(inventory, 4, 62, 17)); // Nails slot
        addSlot(new Slot(inventory, 5, 140, 35){ // Output slot
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }
        });
        addSlot(new Slot(inventory, 6, 26, 17) { // Fluid slot
            @Override
            public boolean canInsert(ItemStack stack) {
                return blockEntity.isValid(stack, 6);
            }
        });

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    public void hammer() {
        this.slots.set(0, new MaxStackSize1Slot(this.getSlot(0).inventory, 0, 17, 35));
        this.slots.set(1, new MaxStackSize1Slot(this.getSlot(1).inventory, 1, 35, 35));
        this.slots.set(2, new MaxStackSize1Slot(this.getSlot(2).inventory, 2, 17, 53));
        this.slots.set(3, new MaxStackSize1Slot(this.getSlot(3).inventory, 3, 35, 53));
        this.blockEntity.hammer();
    }

    public void saw() {
        this.slots.set(0, new MaxStackSize1Slot(this.getSlot(0).inventory, 0, 26, 35));
        this.slots.set(1, new MaxStackSize1Slot(this.getSlot(1).inventory, 1, 44, 44));
        this.slots.set(2, new MaxStackSize1Slot(this.getSlot(2).inventory, 2, 26, 53));
        this.slots.set(3, new MaxStackSize1Slot(this.getSlot(3).inventory, 3, 8, 44));
        this.blockEntity.saw();
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