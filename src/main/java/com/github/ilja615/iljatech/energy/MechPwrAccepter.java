package com.github.ilja615.iljatech.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public interface MechPwrAccepter {
    public static final IntegerProperty MECH_PWR = IntegerProperty.create("mech_pwr", 0, 16);
    public static final BooleanProperty SCHEDULE_STOP = BooleanProperty.create("schedule_stop");
    public static final EnumProperty<OnOffPwr> ON_OFF_PWR = EnumProperty.create("on_off_pwr", OnOffPwr.class);

    public enum OnOffPwr implements StringRepresentable {
        ON("on"),
        OFF("off"),
        SCHEDULED_STOP("scheduled_stop");

        private final String name;

        private OnOffPwr(final String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    // If the block is able to receive power or not
    default boolean acceptsPower(Level world, BlockPos thisPos, Direction sideFrom) {
        BlockState state = world.getBlockState(thisPos);
        return (state.getProperties().contains(MECH_PWR)) || (state.getProperties().contains(ON_OFF_PWR));
    }

    // What the block will do upon receiving power
    default void receivePower(Level world, BlockPos thisPos, Direction sideFrom, int amount) {
        if (amount <= 0) return;
        if (world.getBlockState(thisPos).getProperties().contains(MECH_PWR)) {
            amount = Math.min(amount, 16); // Can not send more than 16 power
            world.setBlockAndUpdate(thisPos, world.getBlockState(thisPos).setValue(MECH_PWR, amount));
        }
        if (world.getBlockState(thisPos).getProperties().contains(ON_OFF_PWR)) {
            world.setBlockAndUpdate(thisPos, world.getBlockState(thisPos).setValue(ON_OFF_PWR, OnOffPwr.ON));
        }
        if (world.getBlockState(thisPos).getProperties().contains(SCHEDULE_STOP)) {
            // Remove any scheduled stop, since the power was just added
            world.setBlockAndUpdate(thisPos, world.getBlockState(thisPos).setValue(SCHEDULE_STOP, false));
        }
    }
}
