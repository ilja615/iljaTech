package com.github.ilja615.iljatech.energy;

import net.minecraft.block.BlockState;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public interface MechPwrAccepter {
    public static final IntProperty MECH_PWR = IntProperty.of("mech_pwr", 0, 16);
    public static final BooleanProperty SCHEDULE_STOP = BooleanProperty.of("schedule_stop");
    public static final EnumProperty<OnOffPwr> ON_OFF_PWR = EnumProperty.of("on_off_pwr", OnOffPwr.class);

    public enum OnOffPwr implements StringIdentifiable {
        ON("on"),
        OFF("off"),
        SCHEDULED_STOP("scheduled_stop");

        private final String name;

        private OnOffPwr(final String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }
    }

    // If the block is able to receive power or not
    default boolean acceptsPower(World world, BlockPos thisPos, Direction sideFrom)
    {
        BlockState state = world.getBlockState(thisPos);
        return (state.getProperties().contains(MECH_PWR));
    }

    // What the block will do upon receiving power
    default void receivePower(World world, BlockPos thisPos, Direction sideFrom, int amount)
    {
        if (world.getBlockState(thisPos).getProperties().contains(MECH_PWR)) {
            amount = Math.min(amount, 16); // Can not send more than 16 power
            world.setBlockState(thisPos, world.getBlockState(thisPos).with(MECH_PWR, amount));
        }
    };
}
