package com.github.ilja615.iljatech.blocks.rusty;

import com.github.ilja615.iljatech.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

import java.util.List;
import java.util.Map;

public interface Rusting {
    default int getRustyLevel(BlockState state) {
        if (state.isOf(ModBlocks.IRON_SHEETMETAL) || state.isOf(ModBlocks.IRON_SHEETMETAL_SLAB) || state.isOf(ModBlocks.IRON_SHEETMETAL_STAIRS))
            return 0;
        if (state.isOf(ModBlocks.EXPOSED_IRON_SHEETMETAL) || state.isOf(ModBlocks.EXPOSED_IRON_SHEETMETAL_SLAB) || state.isOf(ModBlocks.EXPOSED_IRON_SHEETMETAL_STAIRS))
            return 1;
        if (state.isOf(ModBlocks.WEATHERED_IRON_SHEETMETAL) || state.isOf(ModBlocks.WEATHERED_IRON_SHEETMETAL_SLAB) || state.isOf(ModBlocks.WEATHERED_IRON_SHEETMETAL_STAIRS))
            return 2;
        if (state.isOf(ModBlocks.RUSTY_IRON_SHEETMETAL) || state.isOf(ModBlocks.RUSTY_IRON_SHEETMETAL_SLAB) || state.isOf(ModBlocks.RUSTY_IRON_SHEETMETAL_STAIRS))
            return 3;

        return 0;
    }

    default void tryRust(BlockState state, ServerWorld world, BlockPos pos, Random random) {

        Map<Block, Block> RUSTY_MAP = Map.of(
                ModBlocks.IRON_SHEETMETAL, ModBlocks.EXPOSED_IRON_SHEETMETAL,
                ModBlocks.EXPOSED_IRON_SHEETMETAL, ModBlocks.WEATHERED_IRON_SHEETMETAL,
                ModBlocks.WEATHERED_IRON_SHEETMETAL, ModBlocks.RUSTY_IRON_SHEETMETAL,
                ModBlocks.IRON_SHEETMETAL_SLAB, ModBlocks.EXPOSED_IRON_SHEETMETAL_SLAB,
                ModBlocks.EXPOSED_IRON_SHEETMETAL_SLAB, ModBlocks.WEATHERED_IRON_SHEETMETAL_SLAB,
                ModBlocks.WEATHERED_IRON_SHEETMETAL_SLAB, ModBlocks.RUSTY_IRON_SHEETMETAL_SLAB,
                ModBlocks.IRON_SHEETMETAL_STAIRS, ModBlocks.EXPOSED_IRON_SHEETMETAL_STAIRS,
                ModBlocks.EXPOSED_IRON_SHEETMETAL_STAIRS, ModBlocks.WEATHERED_IRON_SHEETMETAL_STAIRS,
                ModBlocks.WEATHERED_IRON_SHEETMETAL_STAIRS, ModBlocks.RUSTY_IRON_SHEETMETAL_STAIRS
        );

        for (int i = 1; i < 50; i++) {
            if (pos.getY() + i >= world.getTopY())
                return;
            if (i == 1 && random.nextFloat() < 0.95f) {
                // The first block only has low chance relatively, giving the rest of the blocks time to catch up
                boolean flag = false;
                for (Direction d : Direction.Type.HORIZONTAL) {
                    if (getRustyLevel(world.getBlockState(pos.up(i).offset(d))) > getRustyLevel(state)) {
                        flag = true;
                    }
                }
                if (!flag)
                    return;
            }
            if (world.hasRain(pos.up(i)) || world.getFluidState(pos.up(i)).getFluid() == Fluids.WATER) {
                Block b = world.getBlockState(pos).getBlock();
                if (RUSTY_MAP.containsKey(b)) {
                    world.setBlockState(pos, RUSTY_MAP.get(b).getStateWithProperties(state));
                }
                return;
            }
            if (getRustyLevel(world.getBlockState(pos.up(i))) <= getRustyLevel(state)) {
                return;
                // It can only rust if the block above it is more rusty
            }
        }
    }
}
