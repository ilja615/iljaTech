package com.github.ilja615.iljatech.blocks.cokeoven;

import com.github.ilja615.iljatech.blocks.firebox.FireboxBlock;
import com.github.ilja615.iljatech.blocks.cokeoven.CokeOvenBlockEntity;
import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.init.ModScreenHandlerTypes;
import com.github.ilja615.iljatech.network.BlockPosPayload;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;

public class CokeOvenScreenHandler extends ScreenHandler {
    private final CokeOvenBlockEntity blockEntity;
    private final ScreenHandlerContext context;

    // Client Constructor
    public CokeOvenScreenHandler(int syncId, PlayerInventory playerInventory, BlockPosPayload payload) {
        this(syncId, playerInventory, (CokeOvenBlockEntity) playerInventory.player.getWorld().getBlockEntity(payload.pos()));
    }

    // Main Constructor - (Directly called from server)
    public CokeOvenScreenHandler(int syncId, PlayerInventory playerInventory, CokeOvenBlockEntity blockEntity) {
        super(ModScreenHandlerTypes.COKE_OVEN, syncId);

        this.blockEntity = blockEntity;
        this.context = ScreenHandlerContext.create(this.blockEntity.getWorld(), this.blockEntity.getPos());

        addSlot(new Slot(this.blockEntity.getInventory(), 0, 44, 24));
        addSlot(new Slot(this.blockEntity.getInventory(), 1, 116, 24));


        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
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
        return canUse(this.context, player, ModBlocks.COKE_OVEN);
    }

    public CokeOvenBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    public float getProgress() {
        return (float) this.blockEntity.getTicks() / 100.0f;
    }

    public FireboxBlock.Lit getLitState() {
        return FireboxBlock.Lit.OFF;
    }
}
