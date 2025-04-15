package com.github.ilja615.iljatech.blocks.hatch;

import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.init.ModScreenHandlerTypes;
import com.github.ilja615.iljatech.network.BlockPosPayload;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;

public class ItemHatchScreenHandler extends ScreenHandler {
    private final ItemHatchBlockEntity blockEntity;
    private final ScreenHandlerContext context;

    // Client Constructor
    public ItemHatchScreenHandler(int syncId, PlayerInventory playerInventory, BlockPosPayload payload) {
        this(syncId, playerInventory, (ItemHatchBlockEntity) playerInventory.player.getWorld().getBlockEntity(payload.pos()), new SimpleInventory(5));
    }

    // Main Constructor - (Directly called from server)
    public ItemHatchScreenHandler(int syncId, PlayerInventory playerInventory, ItemHatchBlockEntity blockEntity, SimpleInventory inventory) {
        super(ModScreenHandlerTypes.ITEM_HATCH, syncId);

        this.blockEntity = blockEntity;
        this.context = ScreenHandlerContext.create(this.blockEntity.getWorld(), this.blockEntity.getPos());

        for (int i = 0; i < 5; i++) {
            addSlot(new Slot(inventory, i, 44 + (i * 18), 20));
        }

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    private void addPlayerInventory(PlayerInventory playerInv) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInv, 9 + (column + (row * 9)), 8 + (column * 18), 51 + (row * 18)));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInv) {
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInv, column, 8 + (column * 18), 109));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(this.context, player, ModBlocks.ITEM_HATCH);
    }

    public ItemHatchBlockEntity getBlockEntity() {
        return this.blockEntity;
    }
}
