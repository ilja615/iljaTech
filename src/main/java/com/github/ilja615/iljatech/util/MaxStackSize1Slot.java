package com.github.ilja615.iljatech.util;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public class MaxStackSize1Slot extends Slot {

    public MaxStackSize1Slot(Container inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
