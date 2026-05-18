package com.github.ilja615.iljatech.util;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;

public class MaxStackSize1Slot extends Slot {

    public MaxStackSize1Slot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public int getMaxItemCount() {
        return 1;
    }
}
