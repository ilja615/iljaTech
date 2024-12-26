package com.github.ilja615.iljatech.init;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.blocks.*;
import com.github.ilja615.iljatech.blocks.AxisRodBlock;
import com.github.ilja615.iljatech.blocks.rollermill.RollerMillBlock;
import com.github.ilja615.iljatech.blocks.wire.WireBlock;
import net.minecraft.block.*;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ColorCode;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.ConstantIntProvider;

public class ModBlocks {
    public static final Block TIN_ORE = registerWithItem("tin_ore", new ExperienceDroppingBlock(ConstantIntProvider.create(0), AbstractBlock.Settings.copy(Blocks.COPPER_ORE).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IljaTech.MOD_ID, "tin_ore")))));
    public static final Block NICKEL_ORE = registerWithItem("nickel_ore", new ExperienceDroppingBlock(ConstantIntProvider.create(0), AbstractBlock.Settings.copy(Blocks.IRON_ORE).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IljaTech.MOD_ID, "nickel_ore")))));
    public static final Block CHROME_ORE = registerWithItem("chrome_ore", new ExperienceDroppingBlock(ConstantIntProvider.create(0), AbstractBlock.Settings.copy(Blocks.GOLD_ORE).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IljaTech.MOD_ID, "chrome_ore")))));
    public static final Block DEEPSLATE_TIN_ORE = registerWithItem("deepslate_tin_ore", new ExperienceDroppingBlock(ConstantIntProvider.create(0), AbstractBlock.Settings.copy(Blocks.DEEPSLATE_COPPER_ORE).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IljaTech.MOD_ID, "deepslate_tin_ore")))));
    public static final Block DEEPSLATE_NICKEL_ORE = registerWithItem("deepslate_nickel_ore", new ExperienceDroppingBlock(ConstantIntProvider.create(0), AbstractBlock.Settings.copy(Blocks.DEEPSLATE_IRON_ORE).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IljaTech.MOD_ID, "deepslate_nickel_ore")))));
    public static final Block DEEPSLATE_CHROME_ORE = registerWithItem("deepslate_chrome_ore", new ExperienceDroppingBlock(ConstantIntProvider.create(0), AbstractBlock.Settings.copy(Blocks.DEEPSLATE_GOLD_ORE).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IljaTech.MOD_ID, "deepslate_chrome_ore")))));
    public static final Block SANDSTONE_ALUMINIUM_ORE = registerWithItem("sandstone_aluminium_ore", new ExperienceDroppingBlock(ConstantIntProvider.create(0), AbstractBlock.Settings.copy(Blocks.SANDSTONE).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IljaTech.MOD_ID, "sandstone_aluminium_ore")))));
    public static final Block RED_SANDSTONE_ALUMINIUM_ORE = registerWithItem("red_sandstone_aluminium_ore", new ExperienceDroppingBlock(ConstantIntProvider.create(0), AbstractBlock.Settings.copy(Blocks.RED_SANDSTONE).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IljaTech.MOD_ID, "red_sandstone_aluminium_ore")))));
    public static final Block GRAVEL_ALUMINIUM_ORE = registerWithItem("gravel_aluminium_ore", new ColoredFallingBlock((new ColorCode(-8356741)), AbstractBlock.Settings.copy(Blocks.GRAVEL).requiresTool().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IljaTech.MOD_ID, "gravel_aluminium_ore")))));
    public static final Block RAW_TIN_ORE = registerWithItem("raw_tin_ore", new Block(AbstractBlock.Settings.copy(Blocks.RAW_COPPER_BLOCK).mapColor(MapColor.BLACK).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IljaTech.MOD_ID, "raw_tin_ore")))));
    public static final Block RAW_NICKEL_ORE = registerWithItem("raw_nickel_ore", new Block(AbstractBlock.Settings.copy(Blocks.RAW_IRON_BLOCK).mapColor(MapColor.BROWN).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IljaTech.MOD_ID, "raw_nickel_ore")))));
    public static final Block RAW_ALUMINIUM_ORE = registerWithItem("raw_aluminium_ore", new Block(AbstractBlock.Settings.copy(Blocks.RAW_GOLD_BLOCK).mapColor(MapColor.BROWN).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IljaTech.MOD_ID, "raw_aluminium_ore")))));
    public static final Block RAW_CHROME_ORE = registerWithItem("raw_chrome_ore", new Block(AbstractBlock.Settings.copy(Blocks.RAW_GOLD_BLOCK).mapColor(MapColor.LIGHT_GRAY).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IljaTech.MOD_ID, "raw_chrome_ore")))));

    public static final Block NAILED_ACACIA_PLANKS = registerWithItem("nailed_acacia_planks", new Block(AbstractBlock.Settings.copy(Blocks.ACACIA_PLANKS).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IljaTech.MOD_ID, "nailed_acacia_planks")))));
    public static final Block NAILED_BAMBOO_PLANKS = registerWithItem("nailed_bamboo_planks", new Block(AbstractBlock.Settings.copy(Blocks.BAMBOO_PLANKS).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IljaTech.MOD_ID, "nailed_bamboo_planks")))));
    public static final Block NAILED_BIRCH_PLANKS = registerWithItem("nailed_birch_planks", new Block(AbstractBlock.Settings.copy(Blocks.BIRCH_PLANKS).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IljaTech.MOD_ID, "nailed_birch_planks")))));
    public static final Block NAILED_CHERRY_PLANKS = registerWithItem("nailed_cherry_planks", new Block(AbstractBlock.Settings.copy(Blocks.CHERRY_PLANKS).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IljaTech.MOD_ID, "nailed_cherry_planks")))));
    public static final Block NAILED_CRIMSON_PLANKS = registerWithItem("nailed_crimson_planks", new Block(AbstractBlock.Settings.copy(Blocks.CRIMSON_PLANKS).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IljaTech.MOD_ID, "nailed_crimson_planks")))));
    public static final Block NAILED_DARK_OAK_PLANKS = registerWithItem("nailed_dark_oak_planks", new Block(AbstractBlock.Settings.copy(Blocks.DARK_OAK_PLANKS).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IljaTech.MOD_ID, "nailed_dark_oak_planks")))));
    public static final Block NAILED_JUNGLE_PLANKS = registerWithItem("nailed_jungle_planks", new Block(AbstractBlock.Settings.copy(Blocks.JUNGLE_PLANKS).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IljaTech.MOD_ID, "nailed_jungle_planks")))));
    public static final Block NAILED_MANGROVE_PLANKS = registerWithItem("nailed_mangrove_planks", new Block(AbstractBlock.Settings.copy(Blocks.MANGROVE_PLANKS).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IljaTech.MOD_ID, "nailed_mangrove_planks")))));
    public static final Block NAILED_OAK_PLANKS = registerWithItem("nailed_oak_planks", new Block(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IljaTech.MOD_ID, "nailed_oak_planks")))));
    public static final Block NAILED_SPRUCE_PLANKS = registerWithItem("nailed_spruce_planks", new Block(AbstractBlock.Settings.copy(Blocks.SPRUCE_PLANKS).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IljaTech.MOD_ID, "nailed_spruce_planks")))));
    public static final Block NAILED_WARPED_PLANKS = registerWithItem("nailed_warped_planks", new Block(AbstractBlock.Settings.copy(Blocks.WARPED_PLANKS).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IljaTech.MOD_ID, "nailed_warped_planks")))));

    public static final Block CRANK = registerWithItem("crank", new CrankBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASS).strength(1.0F, 0.5F).sounds(BlockSoundGroup.WOOD).pistonBehavior(PistonBehavior.DESTROY).noCollision().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IljaTech.MOD_ID, "crank")))));
    public static final Block GEARBOX = registerWithItem("gearbox", new GearBoxBlock(AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.COPPER).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IljaTech.MOD_ID, "gearbox")))));
    public static final Block WOODEN_SHAFT = registerWithItem("wooden_shaft", new WoodenShaftBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.0F, 0.5F).sounds(BlockSoundGroup.WOOD).nonOpaque().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IljaTech.MOD_ID, "wooden_shaft")))));
    public static final Block ROLLER_MILL = registerWithItem("roller_mill", new RollerMillBlock(AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.COPPER).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IljaTech.MOD_ID, "rolling_mill")))));
    public static final Block DRILL = registerWithItem("drill", new DrillBlock(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL).nonOpaque().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IljaTech.MOD_ID, "drill")))));
    public static final Block BELLOWS = registerWithItem("bellows", new BellowsBlock(AbstractBlock.Settings.create().mapColor(MapColor.BROWN).strength(3.0F, 6.0F).sounds(BlockSoundGroup.WOOD).nonOpaque().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IljaTech.MOD_ID, "bellows")))));

    public static final Block COPPER_ROD = registerWithItem("copper_rod", new AxisRodBlock(AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).requiresTool().strength(1.0F, 2.0F).sounds(BlockSoundGroup.COPPER).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IljaTech.MOD_ID, "copper_rod")))));
    public static final Block COPPER_WIRE = registerWithItem("copper_wire", new WireBlock(AbstractBlock.Settings.copy(Blocks.RAIL).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IljaTech.MOD_ID, "copper_wire")))));
    public static final Block IRON_PLATE = registerWithItem("iron_plate", new PlateBlock(AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).requiresTool().strength(2.5F, 6.0F).sounds(BlockSoundGroup.METAL).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IljaTech.MOD_ID, "iron_plate")))));
    public static final Block IRON_ROD = registerWithItem("iron_rod", new AxisRodBlock(AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).requiresTool().strength(1.0F, 2.0F).sounds(BlockSoundGroup.METAL).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IljaTech.MOD_ID, "iron_rod")))));

    public static <T extends Block> T register(String name, T block) {
        return Registry.register(Registries.BLOCK, Identifier.of(IljaTech.MOD_ID, name), block);
    }

    public static <T extends Block> T registerWithItem(String name, T block, Item.Settings settings) {
        T registered = register(name, block);
        ModItems.registerBlockItem(name, registered, settings);
        return registered;
    }

    public static <T extends Block> T registerWithItem(String name, T block) {
        return registerWithItem(name, block, new Item.Settings());
    }

    public static void load() {}
}
