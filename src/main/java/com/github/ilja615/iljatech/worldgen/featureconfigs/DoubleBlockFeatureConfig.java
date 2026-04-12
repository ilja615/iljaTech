package com.github.ilja615.iljatech.worldgen.featureconfigs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record DoubleBlockFeatureConfig(BlockStateProvider bottom, BlockStateProvider top) implements FeatureConfiguration {
    public static final Codec<DoubleBlockFeatureConfig> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(BlockStateProvider.CODEC.fieldOf("bottom").forGetter(DoubleBlockFeatureConfig::bottom),
                        BlockStateProvider.CODEC.fieldOf("top").forGetter(DoubleBlockFeatureConfig::top))
                .apply(instance, DoubleBlockFeatureConfig::new);
    });

}
