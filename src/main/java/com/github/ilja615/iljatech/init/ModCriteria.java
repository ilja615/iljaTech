package com.github.ilja615.iljatech.init;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.blocks.researchtable.BlueprintingCriterion;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class ModCriteria {

    public static final BlueprintingCriterion BLUEPRINT_UNLOCK = register("blueprint_unlock", new BlueprintingCriterion());

    public static <T extends CriterionTrigger<?>> T register(String id, T criterion) {
        return Registry.register(BuiltInRegistries.TRIGGER_TYPES, ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, id), criterion);
    }

    public static void load() {}
}
