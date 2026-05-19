package com.github.ilja615.iljatech.blocks.windmill;

import org.joml.Vector2f;

import java.util.Arrays;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

public enum WindDirection implements StringRepresentable {
    N("n", new Vector2f(0, -1), new Direction[]{Direction.NORTH}),
    NE("ne", new Vector2f(0.70710678118f, -0.70710678118f), new Direction[]{Direction.NORTH, Direction.EAST}),
    E("e", new Vector2f(1, 0), new Direction[]{Direction.EAST}),
    SE("se", new Vector2f(0.70710678118f, 0.70710678118f), new Direction[]{Direction.SOUTH, Direction.EAST}),
    S("s", new Vector2f(0, 1),new Direction[]{Direction.SOUTH}),
    SW("sw", new Vector2f(-0.70710678118f, 0.70710678118f), new Direction[]{Direction.SOUTH, Direction.WEST}),
    W("w", new Vector2f(-1, 0),new Direction[]{Direction.WEST}),
    NW("nw", new Vector2f(-0.70710678118f, -0.70710678118f), new Direction[]{Direction.NORTH, Direction.WEST});

    private final String name;
    private final Direction[] directions;
    private final Vector2f unitVector;

    private WindDirection(final String name, final Vector2f unitVector, Direction[] directions) {
        this.name = name;
        this.directions = directions;
        this.unitVector = unitVector;
    }

    public Vector2f getUnitVector() {
        return unitVector;
    }

    public Direction[] getDirections() {
        return directions;
    }

    public boolean alignsWith(Direction direction) {
        return Arrays.asList(directions).contains(direction);
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}