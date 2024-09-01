package com.github.ilja615.iljatech.init;

import com.github.ilja615.iljatech.IljaTech;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static <T extends Item> T register(String name, T item) {
        return Registry.register(Registries.ITEM, Identifier.of(IljaTech.MOD_ID), item);
    }

    public static void load() {}
}
