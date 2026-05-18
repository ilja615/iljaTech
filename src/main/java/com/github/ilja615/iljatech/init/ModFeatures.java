package com.github.ilja615.iljatech.init;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.worldgen.featureconfigs.DoubleBlockFeatureConfig;
import com.github.ilja615.iljatech.worldgen.features.DoubleBlockFeature;
import net.minecraft.block.Block;
import net.minecraft.registry.*;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.*;

public class ModFeatures {
    public static final Feature<?> DOUBLE_BLOCK = register("double_block", new DoubleBlockFeature(DoubleBlockFeatureConfig.CODEC));

    public static <T extends Feature<?>> T register(String name, T feature) {
        return Registry.register(Registries.FEATURE, Identifier.of(IljaTech.MOD_ID, name), feature);
    }

    public static void load() {}
}
