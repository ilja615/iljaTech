package com.github.ilja615.iljatech.blocks.windmill;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;

public enum WindDirection implements StringIdentifiable {
    N("n", new Direction[]{Direction.NORTH}),
    NE("ne", new Direction[]{Direction.NORTH, Direction.EAST}),
    E("e", new Direction[]{Direction.EAST}),
    SE("se", new Direction[]{Direction.SOUTH, Direction.EAST}),
    S("s", new Direction[]{Direction.SOUTH}),
    SW("sw", new Direction[]{Direction.SOUTH, Direction.WEST}),
    W("w", new Direction[]{Direction.WEST}),
    NW("nw", new Direction[]{Direction.NORTH, Direction.WEST});

    private final String name;
    private final Direction[] directions;

    private WindDirection(final String name, Direction[] directions) {
        this.name = name;
        this.directions = directions;
    }

    @Override
    public String asString() {
        return this.name;
    }
}