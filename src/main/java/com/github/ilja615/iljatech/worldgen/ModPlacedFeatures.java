package com.github.ilja615.iljatech.worldgen;

import com.github.ilja615.iljatech.IljaTech;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.*;

import java.util.List;

public class ModPlacedFeatures {
    public static final RegistryKey<PlacedFeature> OVERWORLD_TIN_ORE_KEY = registerKey("overworld_tin_ore");
    public static final RegistryKey<PlacedFeature> OVERWORLD_NICKEL_ORE_KEY = registerKey("overworld_nickel_ore");
    public static final RegistryKey<PlacedFeature> OVERWORLD_SANDSTONE_ALUMINIUM_ORE_KEY = registerKey("overworld_sandstone_aluminium_ore");
    public static final RegistryKey<PlacedFeature> OVERWORLD_GRAVEL_ALUMINIUM_ORE_KEY = registerKey("overworld_gravel_aluminium_ore");
    public static final RegistryKey<PlacedFeature> OVERWORLD_LARGE_GRAVEL_ALUMINIUM_ORE_KEY = registerKey("overworld_large_gravel_aluminium_ore");
    public static final RegistryKey<PlacedFeature> OVERWORLD_CHROME_ORE_KEY = registerKey("overworld_chrome_ore");

    public static void bootStrap(Registerable<PlacedFeature> context) {
        RegistryEntryLookup<ConfiguredFeature<?, ?>> registryLookup = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

        context.register(OVERWORLD_TIN_ORE_KEY, new PlacedFeature(
                registryLookup.getOrThrow(ModConfiguredFeatures.OVERWORLD_TIN_ORE_KEY),
                List.copyOf(Modifiers.modifiersWithCount(4,
                        HeightRangePlacementModifier.trapezoid(YOffset.fixed(32), YOffset.fixed(128))))));
        context.register(OVERWORLD_NICKEL_ORE_KEY, new PlacedFeature(
                registryLookup.getOrThrow(ModConfiguredFeatures.OVERWORLD_NICKEL_ORE_KEY),
                List.copyOf(Modifiers.modifiersWithCount(9,
                        HeightRangePlacementModifier.trapezoid(YOffset.fixed(-24), YOffset.fixed(56))))));
        context.register(OVERWORLD_SANDSTONE_ALUMINIUM_ORE_KEY, new PlacedFeature(
                registryLookup.getOrThrow(ModConfiguredFeatures.OVERWORLD_SANDSTONE_ALUMINIUM_ORE_KEY),
                List.copyOf(Modifiers.modifiersWithCount(70,
                        HeightRangePlacementModifier.uniform(YOffset.fixed(48), YOffset.fixed(96))))));
        context.register(OVERWORLD_GRAVEL_ALUMINIUM_ORE_KEY, new PlacedFeature(
                registryLookup.getOrThrow(ModConfiguredFeatures.OVERWORLD_GRAVEL_ALUMINIUM_ORE_KEY),
                List.copyOf(Modifiers.modifiersWithCount(70,
                        HeightRangePlacementModifier.uniform(YOffset.fixed(-64), YOffset.fixed(180))))));
        context.register(OVERWORLD_LARGE_GRAVEL_ALUMINIUM_ORE_KEY, new PlacedFeature(
                registryLookup.getOrThrow(ModConfiguredFeatures.OVERWORLD_LARGE_GRAVEL_ALUMINIUM_ORE_KEY),
                List.copyOf(Modifiers.modifiersWithCount(2,
                        HeightRangePlacementModifier.uniform(YOffset.fixed(-64), YOffset.fixed(180))))));
        context.register(OVERWORLD_CHROME_ORE_KEY, new PlacedFeature(
                registryLookup.getOrThrow(ModConfiguredFeatures.OVERWORLD_CHROME_ORE_KEY),
                List.copyOf(Modifiers.modifiersWithCount(4,
                        HeightRangePlacementModifier.trapezoid(YOffset.aboveBottom(-80), YOffset.aboveBottom(80))))));
    }

    private static RegistryKey<PlacedFeature> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(IljaTech.MOD_ID, name));
    }

    public static class Modifiers {
        private static List<PlacementModifier> modifiers(PlacementModifier countModifier, PlacementModifier heightModifier) {
            return List.of(countModifier, SquarePlacementModifier.of(), heightModifier, BiomePlacementModifier.of());
        }

        private static List<PlacementModifier> modifiersWithCount(int count, PlacementModifier heightModifier) {
            return modifiers(CountPlacementModifier.of(count), heightModifier);
        }

        private static List<PlacementModifier> modifiersWithRarity(int chance, PlacementModifier heightModifier) {
            return modifiers(RarityFilterPlacementModifier.of(chance), heightModifier);
        }
    }
}
