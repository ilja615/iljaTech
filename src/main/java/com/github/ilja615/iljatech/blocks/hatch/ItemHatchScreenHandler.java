package com.github.ilja615.iljatech.blocks.hatch;

import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.init.ModScreenHandlerTypes;
import com.github.ilja615.iljatech.network.BlockPosPayload;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ItemHatchScreenHandler extends AbstractContainerMenu {
    private final ItemHatchBlockEntity blockEntity;
    private final ContainerLevelAccess context;

    // Client Constructor
    public ItemHatchScreenHandler(int syncId, Inventory playerInventory, BlockPosPayload payload) {
        this(syncId, playerInventory, (ItemHatchBlockEntity) playerInventory.player.level().getBlockEntity(payload.pos()), new SimpleContainer(5));
    }

    // Main Constructor - (Directly called from server)
    public ItemHatchScreenHandler(int syncId, Inventory playerInventory, ItemHatchBlockEntity blockEntity, SimpleContainer inventory) {
        super(ModScreenHandlerTypes.ITEM_HATCH, syncId);

        this.blockEntity = blockEntity;
        this.context = ContainerLevelAccess.create(this.blockEntity.getLevel(), this.blockEntity.getBlockPos());

        for (int i = 0; i < 5; i++) {
            addSlot(new Slot(inventory, i, 44 + (i * 18), 20));
        }

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    private void addPlayerInventory(Inventory playerInv) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInv, 9 + (column + (row * 9)), 8 + (column * 18), 51 + (row * 18)));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInv) {
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInv, column, 8 + (column * 18), 109));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.context, player, ModBlocks.ITEM_HATCH);
    }

    public ItemHatchBlockEntity getBlockEntity() {
        return this.blockEntity;
    }
}
