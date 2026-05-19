package com.github.ilja615.iljatech.blocks.cokeoven;

import com.github.ilja615.iljatech.blocks.firebox.FireboxBlock;
import com.github.ilja615.iljatech.blocks.cokeoven.CokeOvenBlockEntity;
import com.github.ilja615.iljatech.blocks.foundry.FoundryBlockEntity;
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

public class CokeOvenScreenHandler extends AbstractContainerMenu {
    private final CokeOvenBlockEntity blockEntity;
    private final ContainerLevelAccess context;

    // Client Constructor
    public CokeOvenScreenHandler(int syncId, Inventory playerInventory, BlockPosPayload payload) {
        this(syncId, playerInventory, (CokeOvenBlockEntity) playerInventory.player.level().getBlockEntity(payload.pos()), new SimpleContainer(5));
    }

    // Main Constructor - (Directly called from server)
    public CokeOvenScreenHandler(int syncId, Inventory playerInventory, CokeOvenBlockEntity blockEntity, SimpleContainer inventory) {
        super(ModScreenHandlerTypes.COKE_OVEN, syncId);

        this.blockEntity = blockEntity;
        this.context = ContainerLevelAccess.create(this.blockEntity.getLevel(), this.blockEntity.getBlockPos());

        addSlot(new Slot(inventory, 0, 44, 24));
        addSlot(new Slot(inventory, 1, 116, 24));
        addSlot(new Slot(inventory, 2, 116, 42) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return blockEntity.isValid(stack, 2);
            }
        });

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
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
        return stillValid(this.context, player, ModBlocks.COKE_OVEN);
    }

    public CokeOvenBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    public int getTicks() {
        return this.blockEntity.getTicks();
    }

    public FireboxBlock.Lit getLitState() {
        return FireboxBlock.Lit.OFF;
    }
}
