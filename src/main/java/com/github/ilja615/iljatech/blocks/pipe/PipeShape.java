package com.github.ilja615.iljatech.blocks.pipe;

import net.minecraft.util.StringIdentifiable;

public enum PipeShape implements StringIdentifiable {
    NORTH_SOUTH("north_south"),
    EAST_WEST("east_west"),
    UP_DOWN("up_down"),
    SOUTH_EAST("south_east"),
    SOUTH_WEST("south_west"),
    NORTH_WEST("north_west"),
    NORTH_EAST("north_east"),
    NORTH_UP("north_up"),
    EAST_UP("east_up"),
    SOUTH_UP("south_up"),
    WEST_UP("west_up"),
    NORTH_DOWN("north_down"),
    EAST_DOWN("east_down"),
    SOUTH_DOWN("south_down"),
    WEST_DOWN("west_down");

    private final String name;

    private PipeShape(final String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}