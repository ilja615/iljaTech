package com.github.ilja615.iljatech.blocks.squeezer;

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

public class SqueezerScreenHandler extends ScreenHandler {
    private final SqueezerBlockEntity blockEntity;
    private final ScreenHandlerContext context;

    // Client Constructor
    public SqueezerScreenHandler(int syncId, PlayerInventory playerInventory, BlockPosPayload payload) {
        this(syncId, playerInventory, (SqueezerBlockEntity) playerInventory.player.getWorld().getBlockEntity(payload.pos()), new SimpleInventory(2));
    }

    // Main Constructor - (Directly called from server)
    public SqueezerScreenHandler(int syncId, PlayerInventory playerInventory, SqueezerBlockEntity blockEntity, SimpleInventory inventory) {
        super(ModScreenHandlerTypes.SQUEEZER, syncId);

        this.blockEntity = blockEntity;
        this.context = ScreenHandlerContext.create(this.blockEntity.getWorld(), this.blockEntity.getPos());

        addSlot(new Slot(inventory, 0, 44, 24));
        addSlot(new Slot(inventory, 1, 116, 24) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return blockEntity.isValid(stack, 1);
            }
        });

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
        return canUse(this.context, player, ModBlocks.SQUEEZER);
    }

    public SqueezerBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    public int getTicks() {
        return this.blockEntity.getTicks();
    }
}
