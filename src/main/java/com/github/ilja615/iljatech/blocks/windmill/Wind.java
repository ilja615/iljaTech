package com.github.ilja615.iljatech.blocks.windmill;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class Wind {
    public static PerlinNoiseSampler X_NOISE = null;
    public static PerlinNoiseSampler Z_NOISE = null;

    public static long seed;

    private static final double TAN1_8 = 0.41421356237;
    private static final double TAN3_8 = 2.41421356237;
    private static final double TAN5_8 = -2.41421356237;
    private static final double TAN7_8 = -0.41421356237;

    public static Pair<WindDirection, Double> getWindFromVector(Vec2f vector) {
        double u = (double) vector.x;
        double v = (double) vector.y;

        if (Math.abs(u) < 1e-6 && Math.abs(v) < 1e-6)
            return new Pair<WindDirection, Double>(WindDirection.N, 0.0d);

        WindDirection wd = null;
        if (v < TAN5_8*u && v < TAN3_8*u)
            wd = WindDirection.N;
        else if (v >= TAN5_8*u && v < TAN7_8*u)
            wd = WindDirection.NE;
        else if (v >= TAN7_8*u && v < TAN1_8*u)
            wd = WindDirection.E;
        else if (v >= TAN1_8*u && v < TAN3_8*u)
            wd = WindDirection.SE;
        else if (v >= TAN3_8*u && v >= TAN5_8*u)
            wd = WindDirection.S;
        else if (v >= TAN7_8*u && v < TAN5_8*u)
            wd = WindDirection.SW;
        else if (v >= TAN1_8*u && v < TAN7_8*u)
            wd = WindDirection.W;
        else if (v >= TAN3_8*u && v < TAN1_8*u)
            wd = WindDirection.NW;
        else
            return new Pair<WindDirection, Double>(WindDirection.N, 0.0d);

        return new Pair<WindDirection, Double>(wd, 1.0d);
    }

    public static Vec2f
    getWindVectorAt(World world, int chunkX, int chunkZ) {
        if (world != null && world instanceof ServerWorld serverWorld)
            seed = serverWorld.getSeed();

        if (X_NOISE == null || Z_NOISE == null) {
            X_NOISE = new PerlinNoiseSampler(Random.create(seed));
            Z_NOISE = new PerlinNoiseSampler(Random.create(-1*seed));
        }

        double u = X_NOISE.sample(chunkX / 16.0d, 64.0, chunkZ / 16.0d);
        double v = Z_NOISE.sample(chunkX / 16.0d, 64.0, chunkZ / 16.0d);

        return new Vec2f((float) u, (float) v);
    }

    public static Vec2f getWindDirectionUnitVectorAt(World world, int chunkX, int chunkZ) {
        if (world != null && world instanceof ServerWorld serverWorld)
            seed = serverWorld.getSeed();
        Vec2f raw = getWindVectorAt(world, chunkX, chunkZ);
        WindDirection windDirection = getWindFromVector(raw).getLeft();
        return windDirection.getUnitVector();
    }
}
