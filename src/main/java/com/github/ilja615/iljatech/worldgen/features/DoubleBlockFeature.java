package com.github.ilja615.iljatech.worldgen.features;

import com.github.ilja615.iljatech.worldgen.featureconfigs.DoubleBlockFeatureConfig;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class DoubleBlockFeature extends Feature<DoubleBlockFeatureConfig> {
    public DoubleBlockFeature(Codec<DoubleBlockFeatureConfig> codec) {
        super(codec);
    }

    public boolean place(FeaturePlaceContext<DoubleBlockFeatureConfig> context) {
        DoubleBlockFeatureConfig DoubleBlockFeatureConfig = (DoubleBlockFeatureConfig)context.config();
        WorldGenLevel structureWorldAccess = context.level();
        BlockPos blockPos = context.origin();
        BlockState bottom = DoubleBlockFeatureConfig.bottom().getState(context.random(), blockPos);

        structureWorldAccess.setBlock(blockPos, bottom, Block.UPDATE_CLIENTS);
        BlockState top = DoubleBlockFeatureConfig.top().getState(context.random(), blockPos.above());
        structureWorldAccess.setBlock(blockPos.above(), top, Block.UPDATE_CLIENTS);
        return true;
    }
}