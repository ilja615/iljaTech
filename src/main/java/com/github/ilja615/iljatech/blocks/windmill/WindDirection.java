package com.github.ilja615.iljatech.blocks.windmill;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;

public enum WindDirection implements StringIdentifiable {
    N("n", new Vec2f(0, -1), new Direction[]{Direction.NORTH}),
    NE("ne", new Vec2f(0.70710678118f, -0.70710678118f), new Direction[]{Direction.NORTH, Direction.EAST}),
    E("e", new Vec2f(1, 0), new Direction[]{Direction.EAST}),
    SE("se", new Vec2f(0.70710678118f, 0.70710678118f), new Direction[]{Direction.SOUTH, Direction.EAST}),
    S("s", new Vec2f(0, 1),new Direction[]{Direction.SOUTH}),
    SW("sw", new Vec2f(-0.70710678118f, 0.70710678118f), new Direction[]{Direction.SOUTH, Direction.WEST}),
    W("w", new Vec2f(-1, 0),new Direction[]{Direction.WEST}),
    NW("nw", new Vec2f(-0.70710678118f, -0.70710678118f), new Direction[]{Direction.NORTH, Direction.WEST});

    private final String name;
    private final Direction[] directions;
    private final Vec2f unitVector;

    private WindDirection(final String name, final Vec2f unitVector, Direction[] directions) {
        this.name = name;
        this.directions = directions;
        this.unitVector = unitVector;
    }

    public Vec2f getUnitVector() {
        return unitVector;
    }

    @Override
    public String asString() {
        return this.name;
    }
}