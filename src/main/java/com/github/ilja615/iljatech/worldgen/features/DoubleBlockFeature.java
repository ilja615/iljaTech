package com.github.ilja615.iljatech.worldgen.features;

import com.github.ilja615.iljatech.worldgen.featureconfigs.DoubleBlockFeatureConfig;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class DoubleBlockFeature extends Feature<DoubleBlockFeatureConfig> {
    public DoubleBlockFeature(Codec<DoubleBlockFeatureConfig> codec) {
        super(codec);
    }

    public boolean generate(FeatureContext<DoubleBlockFeatureConfig> context) {
        DoubleBlockFeatureConfig DoubleBlockFeatureConfig = (DoubleBlockFeatureConfig)context.getConfig();
        StructureWorldAccess structureWorldAccess = context.getWorld();
        BlockPos blockPos = context.getOrigin();
        BlockState bottom = DoubleBlockFeatureConfig.bottom().get(context.getRandom(), blockPos);

        structureWorldAccess.setBlockState(blockPos, bottom, Block.NOTIFY_LISTENERS);
        BlockState top = DoubleBlockFeatureConfig.top().get(context.getRandom(), blockPos.up());
        structureWorldAccess.setBlockState(blockPos.up(), top, Block.NOTIFY_LISTENERS);
        return true;
    }
}