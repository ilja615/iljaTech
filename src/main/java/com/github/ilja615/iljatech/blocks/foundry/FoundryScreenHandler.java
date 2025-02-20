package com.github.ilja615.iljatech.blocks.foundry;

import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.init.ModScreenHandlerTypes;
import com.github.ilja615.iljatech.network.BlockPosPayload;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.FurnaceOutputSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class FoundryScreenHandler extends ScreenHandler {
    private final FoundryBlockEntity blockEntity;
    private final ScreenHandlerContext context;

    // Client Constructor
    public FoundryScreenHandler(int syncId, PlayerInventory playerInventory, BlockPosPayload payload) {
        this(syncId, playerInventory, (FoundryBlockEntity) playerInventory.player.getWorld().getBlockEntity(payload.pos()));
    }

    // Main Constructor - (Directly called from server)
    public FoundryScreenHandler(int syncId, PlayerInventory playerInventory, FoundryBlockEntity blockEntity) {
        super(ModScreenHandlerTypes.FOUNDRY, syncId);

        this.blockEntity = blockEntity;
        this.context = ScreenHandlerContext.create(this.blockEntity.getWorld(), this.blockEntity.getPos());

        addSlot(new Slot(this.blockEntity.getInventory(), 0, 34, 35));
        addSlot(new Slot(this.blockEntity.getInventory(), 1, 52, 35));
        addSlot(new Slot(this.blockEntity.getInventory(), 2, 62, 11));
        addSlot(new Slot(this.blockEntity.getInventory(), 3, 124, 35));
        addSlot(new Slot(this.blockEntity.getInventory(), 4, 124, 58));

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
        return canUse(this.context, player, ModBlocks.FOUNDRY);
    }

    public FoundryBlockEntity getBlockEntity() {
        return this.blockEntity;
    }
}
