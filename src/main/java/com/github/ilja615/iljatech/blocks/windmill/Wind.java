package com.github.ilja615.iljatech.blocks.windmill;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import org.joml.Vector2f;

public class Wind {
    public static ImprovedNoise X_NOISE = null;
    public static ImprovedNoise Z_NOISE = null;

    public static long seed;

    private static final double TAN1_8 = 0.41421356237;
    private static final double TAN3_8 = 2.41421356237;
    private static final double TAN5_8 = -2.41421356237;
    private static final double TAN7_8 = -0.41421356237;

    public static Tuple<WindDirection, Double> getWindFromVector(Vector2f vector) {
        double u = (double) vector.x;
        double v = (double) vector.y;

        if (Math.abs(u) < 1e-6 && Math.abs(v) < 1e-6)
            return new Tuple<WindDirection, Double>(WindDirection.N, 0.0d);

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
            return new Tuple<WindDirection, Double>(WindDirection.N, 0.0d);

        return new Tuple<WindDirection, Double>(wd, 1.0d);
    }

    public static Vector2f
    getWindVectorAt(Level world, int chunkX, int chunkZ) {
        if (world != null && world instanceof ServerLevel serverWorld)
            seed = serverWorld.getSeed();

        if (X_NOISE == null || Z_NOISE == null) {
            X_NOISE = new ImprovedNoise(RandomSource.create(seed));
            Z_NOISE = new ImprovedNoise(RandomSource.create(-1*seed));
        }

        double u = X_NOISE.noise(chunkX / 16.0d, 64.0, chunkZ / 16.0d);
        double v = Z_NOISE.noise(chunkX / 16.0d, 64.0, chunkZ / 16.0d);

        return new Vector2f((float) u, (float) v);
    }

    public static Vector2f getWindDirectionUnitVectorAt(Level world, int chunkX, int chunkZ) {
        if (world != null && world instanceof ServerLevel serverWorld)
            seed = serverWorld.getSeed();
        Vector2f raw = getWindVectorAt(world, chunkX, chunkZ);
        WindDirection windDirection = getWindFromVector(raw).getA();
        return windDirection.getUnitVector();
    }

    public static WindDirection getWindDirectionAt(Level world, int chunkX, int chunkZ) {
        if (world != null && world instanceof ServerLevel serverWorld)
            seed = serverWorld.getSeed();
        Vector2f raw = getWindVectorAt(world, chunkX, chunkZ);
        WindDirection windDirection = getWindFromVector(raw).getA();
        return windDirection;
    }
}
