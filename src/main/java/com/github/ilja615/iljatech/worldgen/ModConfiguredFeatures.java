package com.github.ilja615.iljatech.worldgen;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.blocks.FlaxBlock;
import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.init.ModFeatures;
import com.github.ilja615.iljatech.worldgen.featureconfigs.DoubleBlockFeatureConfig;
import com.github.ilja615.iljatech.worldgen.features.DoubleBlockFeature;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedBlockStateProvider;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import java.util.List;

public class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_TIN_ORE_KEY = registerKey("overworld_tin_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_NICKEL_ORE_KEY = registerKey("overworld_nickel_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_SANDSTONE_ALUMINIUM_ORE_KEY = registerKey("overworld_sandstone_aluminium_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_GRAVEL_ALUMINIUM_ORE_KEY = registerKey("overworld_gravel_aluminium_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_LARGE_GRAVEL_ALUMINIUM_ORE_KEY = registerKey("overworld_large_gravel_aluminium_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_CHROME_ORE_KEY = registerKey("overworld_chrome_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_FIRE_CLAY_KEY = registerKey("overworld_fire_clay");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_LIMESTONE_KEY = registerKey("overworld_limestone");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_FLAX_KEY = registerKey("overworld_flax");

    public static void bootStrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        HolderGetter<Feature<?>> registryLookup = context.lookup(Registries.FEATURE);

        RuleTest stoneOreReplaceables = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslateOreReplaceables = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        RuleTest sandStoneOreReplaceables = new BlockMatchTest(Blocks.SANDSTONE);
        RuleTest gravelOreReplaceables = new BlockMatchTest(Blocks.GRAVEL);
        RuleTest ruleTest = new TagMatchTest(BlockTags.BASE_STONE_OVERWORLD);

        List<OreConfiguration.TargetBlockState> overworldTinTargets = List.of(
                OreConfiguration.target(stoneOreReplaceables, ModBlocks.TIN_ORE.defaultBlockState()),
                OreConfiguration.target(deepslateOreReplaceables, ModBlocks.DEEPSLATE_TIN_ORE.defaultBlockState()));
        List<OreConfiguration.TargetBlockState> overworldNickelTargets = List.of(
                OreConfiguration.target(stoneOreReplaceables, ModBlocks.NICKEL_ORE.defaultBlockState()),
                OreConfiguration.target(deepslateOreReplaceables, ModBlocks.DEEPSLATE_NICKEL_ORE.defaultBlockState()));
        List<OreConfiguration.TargetBlockState> overworldSandstoneAluminiumTargets = List.of(
                OreConfiguration.target(sandStoneOreReplaceables, ModBlocks.SANDSTONE_ALUMINIUM_ORE.defaultBlockState()));
        List<OreConfiguration.TargetBlockState> overworldGravelAluminiumTargets = List.of(
                OreConfiguration.target(gravelOreReplaceables, ModBlocks.GRAVEL_ALUMINIUM_ORE.defaultBlockState()));
        List<OreConfiguration.TargetBlockState> overworldChromeTargets = List.of(
                OreConfiguration.target(stoneOreReplaceables, ModBlocks.CHROME_ORE.defaultBlockState()),
                OreConfiguration.target(deepslateOreReplaceables, ModBlocks.DEEPSLATE_CHROME_ORE.defaultBlockState()));

        context.register(OVERWORLD_TIN_ORE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(overworldTinTargets, 14)));
        context.register(OVERWORLD_NICKEL_ORE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(overworldNickelTargets, 8)));
        context.register(OVERWORLD_SANDSTONE_ALUMINIUM_ORE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(overworldSandstoneAluminiumTargets, 5)));
        context.register(OVERWORLD_GRAVEL_ALUMINIUM_ORE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(overworldGravelAluminiumTargets, 6)));
        context.register(OVERWORLD_LARGE_GRAVEL_ALUMINIUM_ORE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(overworldGravelAluminiumTargets, 48)));
        context.register(OVERWORLD_CHROME_ORE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(overworldChromeTargets, 5)));
        context.register(OVERWORLD_FIRE_CLAY_KEY, new ConfiguredFeature<>(Feature.DISK, new DiskConfiguration(
                        RuleBasedBlockStateProvider.simple(ModBlocks.FIRE_CLAY), BlockPredicate.matchesBlocks(List.of(Blocks.DIRT, ModBlocks.FIRE_CLAY)), UniformInt.of(2, 3), 1)));
        context.register(OVERWORLD_LIMESTONE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(ruleTest, ModBlocks.LIMESTONE.defaultBlockState(), 64)));
        context.register(OVERWORLD_FLAX_KEY, new ConfiguredFeature<>(Feature.RANDOM_PATCH, FeatureUtils.simplePatchConfiguration(
                ((DoubleBlockFeature) ModFeatures.DOUBLE_BLOCK),
                new DoubleBlockFeatureConfig(BlockStateProvider.simple(ModBlocks.FLAX_SEEDS.defaultBlockState().setValue(FlaxBlock.AGE, 7).setValue(FlaxBlock.HALF, DoubleBlockHalf.LOWER)),
                        BlockStateProvider.simple(ModBlocks.FLAX_SEEDS.defaultBlockState().setValue(FlaxBlock.AGE, 7).setValue(FlaxBlock.HALF, DoubleBlockHalf.UPPER))), List.of(Blocks.GRASS_BLOCK),
                48)));

    }

    private static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, name));
    }


}
