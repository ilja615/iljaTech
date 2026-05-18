package com.github.ilja615.iljatech.init;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.blocks.researchtable.BlueprintingCriterion;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModCriteria {

    public static final BlueprintingCriterion BLUEPRINT_UNLOCK = register("blueprint_unlock", new BlueprintingCriterion());

    public static <T extends Criterion<?>> T register(String id, T criterion) {
        return Registry.register(Registries.CRITERION, Identifier.of(IljaTech.MOD_ID, id), criterion);
    }

    public static void load() {}
}
