package com.github.ilja615.iljatech.fluids;

import com.github.ilja615.iljatech.init.ModFluids;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;

public abstract class CreosoteOilFluid extends AbstractFluid {
    @Override
    public Fluid getStill() {
        return ModFluids.STILL_CREOSOTE_OIL;
    }

    @Override
    public Fluid getFlowing() {
        return ModFluids.FLOWING_CREOSOTE_OIL;
    }

    @Override
    public Item getBucketItem() {
        return ModFluids.CREOSOTE_OIL_BUCKET;
    }

    @Override
    protected BlockState toBlockState(FluidState fluidState) {
        return ModFluids.CREOSOTE_OIL_BLOCK.getDefaultState().with(Properties.LEVEL_15, getBlockStateLevel(fluidState));
    }

    public static class Flowing extends CreosoteOilFluid {
        @Override
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
            super.appendProperties(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getLevel(FluidState fluidState) {
            return fluidState.get(LEVEL);
        }

        @Override
        public boolean isStill(FluidState fluidState) {
            return false;
        }
    }

    public static class Still extends CreosoteOilFluid {
        @Override
        public int getLevel(FluidState fluidState) {
            return 8;
        }

        @Override
        public boolean isStill(FluidState fluidState) {
            return true;
        }
    }
}