package com.github.ilja615.iljatech.blocks.foundry;

import com.github.ilja615.iljatech.blocks.firebox.FireboxBlock;
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
import org.jetbrains.annotations.Nullable;

public class FoundryScreenHandler extends AbstractContainerMenu {
    private final FoundryBlockEntity blockEntity;
    private final ContainerLevelAccess context;

    // Client Constructor
    public FoundryScreenHandler(int syncId, Inventory playerInventory, BlockPosPayload payload) {
        this(syncId, playerInventory, (FoundryBlockEntity) playerInventory.player.level().getBlockEntity(payload.pos()), new SimpleContainer(5));
    }

    // Main Constructor - (Directly called from server)
    public FoundryScreenHandler(int syncId, Inventory playerInventory, FoundryBlockEntity blockEntity, SimpleContainer inventory) {
        super(ModScreenHandlerTypes.FOUNDRY, syncId);

        this.blockEntity = blockEntity;
        this.context = ContainerLevelAccess.create(this.blockEntity.getLevel(), this.blockEntity.getBlockPos());

        addSlot(new Slot(inventory, 0, 34, 35));
        addSlot(new Slot(inventory, 1, 52, 35));
        addSlot(new Slot(inventory, 2, 62, 11));
        addSlot(new Slot(inventory, 3, 124, 35));
        addSlot(new Slot(inventory, 4, 124, 58));

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
        return stillValid(this.context, player, ModBlocks.FOUNDRY);
    }

    public FoundryBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    public float getProgress() {
        return this.blockEntity.getMaxTicks() == 0 ? 0.0f : (float) this.blockEntity.getTicks() / this.blockEntity.getMaxTicks();
    }

    public FireboxBlock.Lit getLitState() {
        return this.blockEntity.validateHeatMultiblock();
    }
}
