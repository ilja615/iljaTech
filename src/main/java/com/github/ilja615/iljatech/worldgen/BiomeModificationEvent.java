package com.github.ilja615.iljatech.worldgen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.gen.GenerationStep;

public class BiomeModificationEvent {
    public static void load() {
        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                ModPlacedFeatures.OVERWORLD_TIN_ORE_KEY
        );
        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                ModPlacedFeatures.OVERWORLD_NICKEL_ORE_KEY
        );
        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                ModPlacedFeatures.OVERWORLD_SANDSTONE_ALUMINIUM_ORE_KEY
        );
        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                ModPlacedFeatures.OVERWORLD_GRAVEL_ALUMINIUM_ORE_KEY
        );
        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                ModPlacedFeatures.OVERWORLD_LARGE_GRAVEL_ALUMINIUM_ORE_KEY
        );
        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                ModPlacedFeatures.OVERWORLD_CHROME_ORE_KEY
        );
        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                ModPlacedFeatures.OVERWORLD_FIRE_CLAY_KEY
        );
    }
}
