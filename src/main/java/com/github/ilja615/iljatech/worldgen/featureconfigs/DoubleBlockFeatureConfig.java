package com.github.ilja615.iljatech.worldgen.featureconfigs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

import java.util.function.Function;

public record DoubleBlockFeatureConfig(BlockStateProvider bottom, BlockStateProvider top) implements FeatureConfig {
    public static final Codec<DoubleBlockFeatureConfig> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(BlockStateProvider.TYPE_CODEC.fieldOf("bottom").forGetter(DoubleBlockFeatureConfig::bottom),
                        BlockStateProvider.TYPE_CODEC.fieldOf("top").forGetter(DoubleBlockFeatureConfig::top))
                .apply(instance, DoubleBlockFeatureConfig::new);
    });

}
