package com.github.ilja615.iljatech.init;

import com.github.ilja615.iljatech.IljaTech;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModItems {
    public static final Item BOILED_EGG = register("boiled_egg", new Item(new Item.Settings().food(ModFoods.BOILED_EGG_COMPONENT).maxCount(16)));

    public static final Item RAW_TIN_ORE = register("raw_tin", new Item(new Item.Settings()));

    public static final Item TIN_INGOT = register("tin_ingot", new Item(new Item.Settings()));
    public static final Item NICKEL_INGOT = register("nickel_ingot", new Item(new Item.Settings()));
    public static final Item BRONZE_INGOT = register("bronze_ingot", new Item(new Item.Settings()));

    public static final Item BRONZE_GEAR = register("bronze_gear", new Item(new Item.Settings()));

    public static <T extends Item> T register(String name, T item) {
        return Registry.register(Registries.ITEM, Identifier.of(IljaTech.MOD_ID, name), item);
    }

    public static final List<ItemConvertible> ITEMGROUP_BLACKLIST = new ArrayList<ItemConvertible>(
            Arrays.asList(BRONZE_INGOT, BRONZE_GEAR, NICKEL_INGOT));

    public static void load() {}
}
