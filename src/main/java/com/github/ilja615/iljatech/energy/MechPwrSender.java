package com.github.ilja615.iljatech.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public interface MechPwrSender {
    default boolean sendPower(Level world, BlockPos thisPos, Direction direction, int amount) {
        BlockPos neighborPos = thisPos.relative(direction);
        Block block = world.getBlockState(neighborPos).getBlock();
        if (block instanceof MechPwrAccepter) {
            if (((MechPwrAccepter) block).acceptsPower(world, neighborPos, direction.getOpposite())) {
                ((MechPwrAccepter) block).receivePower(world, neighborPos, direction.getOpposite(), amount);
                return true;
            }
        }
        return false;
    }
}
