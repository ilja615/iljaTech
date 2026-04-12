package com.github.ilja615.iljatech.init;

import com.github.ilja615.iljatech.IljaTech;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {
    public static SoundEvent HAMMER = register("hammer");
    public static SoundEvent BELLOWS_INHALE = register("bellows_inhale");
    public static SoundEvent BELLOWS_EXHALE = register("bellows_exhale");
    public static SoundEvent STAMPING_HAMMER_BLOCK = register("stamping_hammer_block");
    public static SoundEvent ORE_CRUSHING = register("ore_crushing");
    public static SoundEvent SAW = register("saw");

    public static SoundEvent register(String name) {
        return Registry.register(BuiltInRegistries.SOUND_EVENT, ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, name), SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, name)));
    }

    public static void load() {}
}
