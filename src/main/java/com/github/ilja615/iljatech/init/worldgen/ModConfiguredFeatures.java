package com.github.ilja615.iljatech.init.worldgen;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.init.ModBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;

import java.util.List;

public class ModConfiguredFeatures {
    public static final RegistryKey<ConfiguredFeature<?, ?>> OVERWORLD_TIN_ORE_KEY = registerKey("overworld_tin_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> OVERWORLD_NICKEL_ORE_KEY = registerKey("overworld_nickel_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> OVERWORLD_SANDSTONE_ALUMINIUM_ORE_KEY = registerKey("overworld_sandstone_aluminium_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> OVERWORLD_GRAVEL_ALUMINIUM_ORE_KEY = registerKey("overworld_gravel_aluminium_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> OVERWORLD_LARGE_GRAVEL_ALUMINIUM_ORE_KEY = registerKey("overworld_large_gravel_aluminium_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> OVERWORLD_CHROME_ORE_KEY = registerKey("overworld_chrome_ore");

    public static void bootStrap(Registerable<ConfiguredFeature<?, ?>> context) {
        RuleTest stoneOreReplaceables = new TagMatchRuleTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslateOreReplaceables = new TagMatchRuleTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        RuleTest sandStoneOreReplaceables = new BlockMatchRuleTest(Blocks.SANDSTONE);
        RuleTest gravelOreReplaceables = new BlockMatchRuleTest(Blocks.GRAVEL);


        List<OreFeatureConfig.Target> overworldTinTargets = List.of(
                OreFeatureConfig.createTarget(stoneOreReplaceables, ModBlocks.TIN_ORE.getDefaultState()),
                OreFeatureConfig.createTarget(deepslateOreReplaceables, ModBlocks.DEEPSLATE_TIN_ORE.getDefaultState()));
        List<OreFeatureConfig.Target> overworldNickelTargets = List.of(
                OreFeatureConfig.createTarget(stoneOreReplaceables, ModBlocks.NICKEL_ORE.getDefaultState()),
                OreFeatureConfig.createTarget(deepslateOreReplaceables, ModBlocks.DEEPSLATE_NICKEL_ORE.getDefaultState()));
        List<OreFeatureConfig.Target> overworldSandstoneAluminiumTargets = List.of(
                OreFeatureConfig.createTarget(sandStoneOreReplaceables, ModBlocks.SANDSTONE_ALUMINIUM_ORE.getDefaultState()));
        List<OreFeatureConfig.Target> overworldGravelAluminiumTargets = List.of(
                OreFeatureConfig.createTarget(gravelOreReplaceables, ModBlocks.GRAVEL_ALUMINIUM_ORE.getDefaultState()));
        List<OreFeatureConfig.Target> overworldChromeTargets = List.of(
                OreFeatureConfig.createTarget(stoneOreReplaceables, ModBlocks.CHROME_ORE.getDefaultState()),
                OreFeatureConfig.createTarget(deepslateOreReplaceables, ModBlocks.DEEPSLATE_CHROME_ORE.getDefaultState()));

        context.register(OVERWORLD_TIN_ORE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreFeatureConfig(overworldTinTargets, 14)));
        context.register(OVERWORLD_NICKEL_ORE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreFeatureConfig(overworldNickelTargets, 8)));
        context.register(OVERWORLD_SANDSTONE_ALUMINIUM_ORE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreFeatureConfig(overworldSandstoneAluminiumTargets, 5)));
        context.register(OVERWORLD_GRAVEL_ALUMINIUM_ORE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreFeatureConfig(overworldGravelAluminiumTargets, 6)));
        context.register(OVERWORLD_LARGE_GRAVEL_ALUMINIUM_ORE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreFeatureConfig(overworldGravelAluminiumTargets, 48)));
        context.register(OVERWORLD_CHROME_ORE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreFeatureConfig(overworldChromeTargets, 5)));
    }

    private static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Identifier.of(IljaTech.MOD_ID, name));
    }


}
