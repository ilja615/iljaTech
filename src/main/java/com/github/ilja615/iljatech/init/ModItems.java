package com.github.ilja615.iljatech.init;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.items.HammerItem;
import com.github.ilja615.iljatech.items.SawItem;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.item.ToolMaterial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModItems {
    public static final Item BOILED_EGG = registerSimple("boiled_egg", new Item.Settings().food(ModFoods.BOILED_EGG_COMPONENT).maxCount(16));

    public static final Item RAW_TIN_ORE = registerSimple("raw_tin", new Item.Settings());
    public static final Item RAW_NICKEL_ORE = registerSimple("raw_nickel", new Item.Settings());
    public static final Item RAW_ALUMINIUM_ORE = registerSimple("raw_aluminium", new Item.Settings());
    public static final Item RAW_CHROME_ORE = registerSimple("raw_chrome", new Item.Settings());

    public static final Item TIN_INGOT = registerSimple("tin_ingot", new Item.Settings());
    public static final Item NICKEL_INGOT = registerSimple("nickel_ingot", new Item.Settings());
    public static final Item ALUMINIUM_INGOT = registerSimple("aluminium_ingot", new Item.Settings());
    public static final Item CHROME_INGOT = registerSimple("chrome_ingot", new Item.Settings());

    public static final Item BRONZE_GEAR = registerSimple("bronze_gear", new Item.Settings());

    private static final Identifier IRON_HAMMER_ID = Identifier.of(IljaTech.MOD_ID, "iron_hammer");
    private static final RegistryKey<Item> IRON_HAMMER_KEY = RegistryKey.of(RegistryKeys.ITEM, IRON_HAMMER_ID);
    public static final HammerItem IRON_HAMMER = register(IRON_HAMMER_ID, new HammerItem(new Item.Settings().registryKey(IRON_HAMMER_KEY).maxDamage(128).attributeModifiers(HammerItem.createAttributeModifiers(ToolMaterial.IRON,2.5F, -3.8F))));
    private static final Identifier IRON_SAW_ID = Identifier.of(IljaTech.MOD_ID, "iron_saw");
    private static final RegistryKey<Item> IRON_SAW_KEY = RegistryKey.of(RegistryKeys.ITEM, IRON_SAW_ID);
    public static final SawItem IRON_SAW = register(IRON_SAW_ID, new SawItem(new Item.Settings().registryKey(IRON_SAW_KEY).maxDamage(128)));
    public static final Item IRON_NAILS = registerSimple("iron_nails", new Item.Settings());

    public static Item registerSimple(String name, Item.Settings settings) {
        Identifier id = Identifier.of(IljaTech.MOD_ID, name);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        return Registry.register(Registries.ITEM, id, new Item(settings.registryKey(key)));
    }

    public static <T extends Block> BlockItem registerBlockItem(String name, T registered, Item.Settings settings) {
        Identifier id = Identifier.of(IljaTech.MOD_ID, name);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        return Registry.register(Registries.ITEM, id, new BlockItem(registered, settings.useBlockPrefixedTranslationKey().registryKey(key)));
    }

    public static <T extends Item> T register(Identifier id, T item) {
        return Registry.register(Registries.ITEM, id, item);
    }

    public static final List<ItemConvertible> ITEMGROUP_BLACKLIST = new ArrayList<ItemConvertible>(
            Arrays.asList());

    public static void load() {}
}
