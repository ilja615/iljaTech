package com.github.ilja615.iljatech.util;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;

public interface TickableBlockEntity {
    void tick();

    static <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pworld) {
        return pworld.isClientSide ? null : (world, pos, state, blockEntity) -> {
            if (blockEntity instanceof TickableBlockEntity tbe) {
                tbe.tick();
            }
        };
    }
}
