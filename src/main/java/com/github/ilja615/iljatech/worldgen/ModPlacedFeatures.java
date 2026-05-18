package com.github.ilja615.iljatech.worldgen;

import com.github.ilja615.iljatech.IljaTech;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.gen.placementmodifier.*;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.material.Fluids;
import java.util.List;

public class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> OVERWORLD_TIN_ORE_KEY = registerKey("overworld_tin_ore");
    public static final ResourceKey<PlacedFeature> OVERWORLD_NICKEL_ORE_KEY = registerKey("overworld_nickel_ore");
    public static final ResourceKey<PlacedFeature> OVERWORLD_SANDSTONE_ALUMINIUM_ORE_KEY = registerKey("overworld_sandstone_aluminium_ore");
    public static final ResourceKey<PlacedFeature> OVERWORLD_GRAVEL_ALUMINIUM_ORE_KEY = registerKey("overworld_gravel_aluminium_ore");
    public static final ResourceKey<PlacedFeature> OVERWORLD_LARGE_GRAVEL_ALUMINIUM_ORE_KEY = registerKey("overworld_large_gravel_aluminium_ore");
    public static final ResourceKey<PlacedFeature> OVERWORLD_CHROME_ORE_KEY = registerKey("overworld_chrome_ore");
    public static final ResourceKey<PlacedFeature> OVERWORLD_FIRE_CLAY_KEY = registerKey("overworld_fire_clay");
    public static final ResourceKey<PlacedFeature> OVERWORLD_LIMESTONE_KEY = registerKey("overworld_limestone");
    public static final ResourceKey<PlacedFeature> OVERWORLD_FLAX_KEY = registerKey("overworld_flax");

    public static void bootStrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> registryLookup = context.lookup(Registries.CONFIGURED_FEATURE);

        context.register(OVERWORLD_TIN_ORE_KEY, new PlacedFeature(
                registryLookup.getOrThrow(ModConfiguredFeatures.OVERWORLD_TIN_ORE_KEY),
                List.copyOf(Modifiers.modifiersWithCount(4,
                        HeightRangePlacement.triangle(VerticalAnchor.absolute(32), VerticalAnchor.absolute(128))))));
        context.register(OVERWORLD_NICKEL_ORE_KEY, new PlacedFeature(
                registryLookup.getOrThrow(ModConfiguredFeatures.OVERWORLD_NICKEL_ORE_KEY),
                List.copyOf(Modifiers.modifiersWithCount(9,
                        HeightRangePlacement.triangle(VerticalAnchor.absolute(-24), VerticalAnchor.absolute(56))))));
        context.register(OVERWORLD_SANDSTONE_ALUMINIUM_ORE_KEY, new PlacedFeature(
                registryLookup.getOrThrow(ModConfiguredFeatures.OVERWORLD_SANDSTONE_ALUMINIUM_ORE_KEY),
                List.copyOf(Modifiers.modifiersWithCount(70,
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(48), VerticalAnchor.absolute(96))))));
        context.register(OVERWORLD_GRAVEL_ALUMINIUM_ORE_KEY, new PlacedFeature(
                registryLookup.getOrThrow(ModConfiguredFeatures.OVERWORLD_GRAVEL_ALUMINIUM_ORE_KEY),
                List.copyOf(Modifiers.modifiersWithCount(70,
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(180))))));
        context.register(OVERWORLD_LARGE_GRAVEL_ALUMINIUM_ORE_KEY, new PlacedFeature(
                registryLookup.getOrThrow(ModConfiguredFeatures.OVERWORLD_LARGE_GRAVEL_ALUMINIUM_ORE_KEY),
                List.copyOf(Modifiers.modifiersWithCount(2,
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(180))))));
        context.register(OVERWORLD_CHROME_ORE_KEY, new PlacedFeature(
                registryLookup.getOrThrow(ModConfiguredFeatures.OVERWORLD_CHROME_ORE_KEY),
                List.copyOf(Modifiers.modifiersWithCount(4,
                        HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80))))));
        context.register(OVERWORLD_FIRE_CLAY_KEY, new PlacedFeature(
                registryLookup.getOrThrow(ModConfiguredFeatures.OVERWORLD_FIRE_CLAY_KEY),
                List.of(InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BlockPredicateFilter.forPredicate(BlockPredicate.matchesFluids(Fluids.WATER)), BiomeFilter.biome())));
        context.register(OVERWORLD_LIMESTONE_KEY, new PlacedFeature(
                registryLookup.getOrThrow(ModConfiguredFeatures.OVERWORLD_LIMESTONE_KEY),
                List.copyOf(Modifiers.modifiersWithRarity(4,
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-32), VerticalAnchor.absolute(180))))));
        context.register(OVERWORLD_FLAX_KEY, new PlacedFeature(
                registryLookup.getOrThrow(ModConfiguredFeatures.OVERWORLD_FLAX_KEY),
                List.of(RarityFilter.onAverageOnceEvery(4), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome())));

    }

    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, name));
    }

    public static class Modifiers {
        private static List<PlacementModifier> modifiers(PlacementModifier countModifier, PlacementModifier heightModifier) {
            return List.of(countModifier, InSquarePlacement.spread(), heightModifier, BiomeFilter.biome());
        }

        private static List<PlacementModifier> modifiersWithCount(int count, PlacementModifier heightModifier) {
            return modifiers(CountPlacement.of(count), heightModifier);
        }

        private static List<PlacementModifier> modifiersWithRarity(int chance, PlacementModifier heightModifier) {
            return modifiers(RarityFilter.onAverageOnceEvery(chance), heightModifier);
        }
    }
}
