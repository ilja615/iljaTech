package com.github.ilja615.iljatech.blocks.mechanical_arm;

import com.github.ilja615.iljatech.blocks.carpentry.CarpentryBlockEntity;
import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.init.ModItems;
import com.github.ilja615.iljatech.init.ModScreenHandlerTypes;
import com.github.ilja615.iljatech.network.BlockPosPayload;
import com.github.ilja615.iljatech.util.FluidItemSlot;
import com.github.ilja615.iljatech.util.MaxStackSize1Slot;
import net.minecraft.util.Tuple;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class MechanicalArmScreenHandler extends AbstractContainerMenu {
    private final MechanicalArmBlockEntity blockEntity;
    private final SimpleContainer inventory;
    private final ContainerLevelAccess context;

    // Client Constructor
    public MechanicalArmScreenHandler(int syncId, Inventory playerInventory, BlockPosPayload payload) {
        super(ModScreenHandlerTypes.CARPENTRY, syncId);

        this.blockEntity = (MechanicalArmBlockEntity) playerInventory.player.level().getBlockEntity(payload.pos());
        this.context = ContainerLevelAccess.create(this.blockEntity.getLevel(), this.blockEntity.getBlockPos());
        this.inventory = new SimpleContainer(7);

        addSlots(playerInventory);
    }

    // Main Constructor - (Directly called from server)
    public MechanicalArmScreenHandler(int syncId, Inventory playerInventory, MechanicalArmBlockEntity blockEntity, SimpleContainer inventory) {
        super(ModScreenHandlerTypes.MECHANICAL_ARM, syncId);

        this.blockEntity = blockEntity;
        this.context = ContainerLevelAccess.create(this.blockEntity.getLevel(), this.blockEntity.getBlockPos());
        this.inventory = inventory;

        addSlots(playerInventory);
    }

    private void addSlots(Inventory playerInventory) {
        addSlot(new Slot(inventory, 0, 80, 20));

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
        return stillValid(this.context, player, ModBlocks.MECHANICAL_ARM);
    }

    public MechanicalArmBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

}
