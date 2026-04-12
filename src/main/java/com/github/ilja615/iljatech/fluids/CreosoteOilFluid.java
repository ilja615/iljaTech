package com.github.ilja615.iljatech.fluids;

import com.github.ilja615.iljatech.init.ModFluids;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public abstract class CreosoteOilFluid extends AbstractFluid {
    @Override
    public Fluid getSource() {
        return ModFluids.STILL_CREOSOTE_OIL;
    }

    @Override
    public Fluid getFlowing() {
        return ModFluids.FLOWING_CREOSOTE_OIL;
    }

    @Override
    public Item getBucket() {
        return ModFluids.CREOSOTE_OIL_BUCKET;
    }

    @Override
    protected BlockState createLegacyBlock(FluidState fluidState) {
        return ModFluids.CREOSOTE_OIL_BLOCK.defaultBlockState().setValue(BlockStateProperties.LEVEL, getLegacyLevel(fluidState));
    }

    public static class Flowing extends CreosoteOilFluid {
        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getAmount(FluidState fluidState) {
            return fluidState.getValue(LEVEL);
        }

        @Override
        public boolean isSource(FluidState fluidState) {
            return false;
        }
    }

    public static class Still extends CreosoteOilFluid {
        @Override
        public int getAmount(FluidState fluidState) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState fluidState) {
            return true;
        }
    }
}