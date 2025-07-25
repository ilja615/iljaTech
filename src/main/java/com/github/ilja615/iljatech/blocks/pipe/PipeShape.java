package com.github.ilja615.iljatech.blocks.pipe;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;

public enum PipeShape implements StringIdentifiable {
    // Straight pipes
    NORTH_SOUTH("north_south", Direction.NORTH, Direction.SOUTH),
    EAST_WEST("east_west", Direction.EAST, Direction.WEST),
    UP_DOWN("up_down", Direction.UP, Direction.DOWN),

    // Horizontal bend pipes
    SOUTH_EAST("south_east", Direction.SOUTH, Direction.EAST),
    SOUTH_WEST("south_west", Direction.SOUTH, Direction.WEST),
    NORTH_WEST("north_west", Direction.NORTH, Direction.WEST),
    NORTH_EAST("north_east", Direction.NORTH, Direction.EAST),

    // Up bend pipes
    NORTH_UP("north_up", Direction.NORTH, Direction.UP),
    EAST_UP("east_up", Direction.EAST, Direction.UP),
    SOUTH_UP("south_up", Direction.SOUTH, Direction.UP),
    WEST_UP("west_up", Direction.WEST, Direction.UP),

    // Down bend pipes
    NORTH_DOWN("north_down", Direction.NORTH, Direction.DOWN),
    EAST_DOWN("east_down", Direction.EAST, Direction.DOWN),
    SOUTH_DOWN("south_down", Direction.SOUTH, Direction.DOWN),
    WEST_DOWN("west_down", Direction.WEST, Direction.DOWN);


    private final String name;
    private final Direction direction1;
    private final Direction direction2;

    private PipeShape(final String name, Direction direction1, Direction direction2) {
        this.name = name;
        this.direction1 = direction1;
        this.direction2 = direction2;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public Direction getDirection1() {
        return direction1;
    }

    public Direction getDirection2() {
        return direction2;
    }

    public boolean connects(Direction direction) {
        return this.direction1 == direction || this.direction2 == direction;
    }
}