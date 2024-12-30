package com.github.ilja615.iljatech.init;

import com.github.ilja615.iljatech.IljaTech;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static SoundEvent HAMMER = register("hammer");
    public static SoundEvent BELLOWS_INHALE = register("bellows_inhale");
    public static SoundEvent BELLOWS_EXHALE = register("bellows_exhale");

    public static SoundEvent register(String name) {
        return Registry.register(Registries.SOUND_EVENT, Identifier.of(IljaTech.MOD_ID, name), SoundEvent.of(Identifier.of(IljaTech.MOD_ID, name)));
    }

    public static void load() {}
}
