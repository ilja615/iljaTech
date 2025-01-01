package com.github.ilja615.iljatech.energy;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public interface MechPwrSender {
    default boolean sendPower(World world, BlockPos thisPos, Direction direction, int amount) {
        BlockPos neighborPos = thisPos.offset(direction);
        Block block = world.getBlockState(neighborPos).getBlock();
        if (block instanceof MechPwrAccepter) {
            if (((MechPwrAccepter) block).acceptsPower(world, neighborPos, direction.getOpposite())) {
                ((MechPwrAccepter) block).receivePower(world, neighborPos, direction.getOpposite(), amount);
                return true;
            }
        }
        return false;
    }

    default void communicateDePowerNeighbors(World world, BlockPos thisPos) {
        for (Direction direction : Direction.values()) {
            BlockPos newPos = thisPos.offset(direction);
            Block block = world.getBlockState(newPos).getBlock();
            if (block instanceof MechPwrAccepter) {
                ((MechPwrAccepter) block).onDePower(world, newPos);
            }
        }
    }
}
