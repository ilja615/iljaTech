package com.github.ilja615.iljatech.init;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.worldgen.featureconfigs.DoubleBlockFeatureConfig;
import com.github.ilja615.iljatech.worldgen.features.DoubleBlockFeature;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.registry.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.level.levelgen.feature.Feature;

public class ModFeatures {
    public static final Feature<?> DOUBLE_BLOCK = register("double_block", new DoubleBlockFeature(DoubleBlockFeatureConfig.CODEC));

    public static <T extends Feature<?>> T register(String name, T feature) {
        return Registry.register(BuiltInRegistries.FEATURE, ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, name), feature);
    }

    public static void load() {}
}
