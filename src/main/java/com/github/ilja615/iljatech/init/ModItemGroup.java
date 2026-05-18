package com.github.ilja615.iljatech.init;

import com.github.ilja615.iljatech.IljaTech;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class ModItemGroup {
    private static final Text TITLE = Text.translatable("itemGroup." + IljaTech.MOD_ID + ".stuff");
    public static final ItemGroup ITEM_GROUP = register("stuff", FabricItemGroup.builder()
            .displayName(TITLE)
            .icon(ModItems.BRONZE_GEAR::getDefaultStack)
            .entries((displayContext, entries) -> Registries.ITEM.getIds()
                    .stream()
                    .filter(
                            key -> key.getNamespace().equals(IljaTech.MOD_ID))
                    .map(Registries.ITEM::getOrEmpty)
                    .map(Optional::orElseThrow)
                    .filter(item -> !ModItems.ITEMGROUP_BLACKLIST.contains(item))
                    .forEach(entries::add))
            .build());

    public static <T extends ItemGroup> T register(String name, T itemGroup) {
        return Registry.register(Registries.ITEM_GROUP, Identifier.of(IljaTech.MOD_ID, name), itemGroup);
    }

    public static void load() {}
}
