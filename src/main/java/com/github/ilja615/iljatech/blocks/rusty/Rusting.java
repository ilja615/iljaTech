package com.github.ilja615.iljatech.blocks.rusty;

import com.github.ilja615.iljatech.init.ModBlocks;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public interface Rusting {
    default int getRustyLevel(BlockState state) {
        if (state.is(ModBlocks.IRON_SHEETMETAL) || state.is(ModBlocks.IRON_SHEETMETAL_SLAB) || state.is(ModBlocks.IRON_SHEETMETAL_STAIRS))
            return 0;
        if (state.is(ModBlocks.EXPOSED_IRON_SHEETMETAL) || state.is(ModBlocks.EXPOSED_IRON_SHEETMETAL_SLAB) || state.is(ModBlocks.EXPOSED_IRON_SHEETMETAL_STAIRS))
            return 1;
        if (state.is(ModBlocks.WEATHERED_IRON_SHEETMETAL) || state.is(ModBlocks.WEATHERED_IRON_SHEETMETAL_SLAB) || state.is(ModBlocks.WEATHERED_IRON_SHEETMETAL_STAIRS))
            return 2;
        if (state.is(ModBlocks.RUSTY_IRON_SHEETMETAL) || state.is(ModBlocks.RUSTY_IRON_SHEETMETAL_SLAB) || state.is(ModBlocks.RUSTY_IRON_SHEETMETAL_STAIRS))
            return 3;

        return 0;
    }

    default void tryRust(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {

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
            if (pos.getY() + i >= world.getMaxBuildHeight())
                return;
            if (i == 1 && random.nextFloat() < 0.95f) {
                // The first block only has low chance relatively, giving the rest of the blocks time to catch up
                boolean flag = false;
                for (Direction d : Direction.Plane.HORIZONTAL) {
                    if (getRustyLevel(world.getBlockState(pos.above(i).relative(d))) > getRustyLevel(state)) {
                        flag = true;
                    }
                }
                if (!flag)
                    return;
            }
            if (world.isRainingAt(pos.above(i)) || world.getFluidState(pos.above(i)).getType() == Fluids.WATER) {
                Block b = world.getBlockState(pos).getBlock();
                if (RUSTY_MAP.containsKey(b)) {
                    world.setBlockAndUpdate(pos, RUSTY_MAP.get(b).withPropertiesOf(state));
                }
                return;
            }
            if (getRustyLevel(world.getBlockState(pos.above(i))) <= getRustyLevel(state)) {
                return;
                // It can only rust if the block above it is more rusty
            }
        }
    }
}
