package com.github.ilja615.iljatech.blocks.squeezer;

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

public class SqueezerScreenHandler extends AbstractContainerMenu {
    private final SqueezerBlockEntity blockEntity;
    private final ContainerLevelAccess context;

    // Client Constructor
    public SqueezerScreenHandler(int syncId, Inventory playerInventory, BlockPosPayload payload) {
        this(syncId, playerInventory, (SqueezerBlockEntity) playerInventory.player.level().getBlockEntity(payload.pos()), new SimpleContainer(2));
    }

    // Main Constructor - (Directly called from server)
    public SqueezerScreenHandler(int syncId, Inventory playerInventory, SqueezerBlockEntity blockEntity, SimpleContainer inventory) {
        super(ModScreenHandlerTypes.SQUEEZER, syncId);

        this.blockEntity = blockEntity;
        this.context = ContainerLevelAccess.create(this.blockEntity.getLevel(), this.blockEntity.getBlockPos());

        addSlot(new Slot(inventory, 0, 44, 24));
        addSlot(new Slot(inventory, 1, 116, 24) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return blockEntity.isValid(stack, 1);
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
        return stillValid(this.context, player, ModBlocks.SQUEEZER);
    }

    public SqueezerBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    public int getTicks() {
        return this.blockEntity.getTicks();
    }
}
