package com.github.ilja615.iljatech.init;

import com.github.ilja615.iljatech.IljaTech;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import java.util.Optional;

public class ModItemGroup {
    private static final Component TITLE = Component.translatable("itemGroup." + IljaTech.MOD_ID + ".stuff");
    public static final CreativeModeTab ITEM_GROUP = register("stuff", FabricItemGroup.builder()
            .title(TITLE)
            .icon(ModItems.BRONZE_GEAR::getDefaultInstance)
            .displayItems((displayContext, entries) -> BuiltInRegistries.ITEM.keySet()
                    .stream()
                    .filter(
                            key -> key.getNamespace().equals(IljaTech.MOD_ID))
                    .map(BuiltInRegistries.ITEM::getOptional)
                    .map(Optional::orElseThrow)
                    .filter(item -> !ModItems.ITEMGROUP_BLACKLIST.contains(item))
                    .forEach(entries::accept))
            .build());

    public static <T extends CreativeModeTab> T register(String name, T itemGroup) {
        return Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, name), itemGroup);
    }

    public static void load() {}
}
