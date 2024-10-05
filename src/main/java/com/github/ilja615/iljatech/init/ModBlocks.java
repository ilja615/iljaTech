package com.github.ilja615.iljatech.init;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.blocks.CrankBlock;
import com.github.ilja615.iljatech.blocks.GearBoxBlock;
import com.github.ilja615.iljatech.blocks.WoodenShaftBlock;
import net.minecraft.block.*;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ColorCode;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.ConstantIntProvider;

public class ModBlocks {
    public static final Block TIN_ORE = registerWithItem("tin_ore", new ExperienceDroppingBlock(ConstantIntProvider.create(0), AbstractBlock.Settings.copy(Blocks.COPPER_ORE)));
    public static final Block NICKEL_ORE = registerWithItem("nickel_ore", new ExperienceDroppingBlock(ConstantIntProvider.create(0), AbstractBlock.Settings.copy(Blocks.IRON_ORE)));
    public static final Block CHROME_ORE = registerWithItem("chrome_ore", new ExperienceDroppingBlock(ConstantIntProvider.create(0), AbstractBlock.Settings.copy(Blocks.GOLD_ORE)));
    public static final Block DEEPSLATE_TIN_ORE = registerWithItem("deepslate_tin_ore", new ExperienceDroppingBlock(ConstantIntProvider.create(0), AbstractBlock.Settings.copy(Blocks.DEEPSLATE_COPPER_ORE)));
    public static final Block DEEPSLATE_NICKEL_ORE = registerWithItem("deepslate_nickel_ore", new ExperienceDroppingBlock(ConstantIntProvider.create(0), AbstractBlock.Settings.copy(Blocks.DEEPSLATE_IRON_ORE)));
    public static final Block DEEPSLATE_CHROME_ORE = registerWithItem("deepslate_chrome_ore", new ExperienceDroppingBlock(ConstantIntProvider.create(0), AbstractBlock.Settings.copy(Blocks.DEEPSLATE_GOLD_ORE)));
    public static final Block SANDSTONE_ALUMINIUM_ORE = registerWithItem("sandstone_aluminium_ore", new ExperienceDroppingBlock(ConstantIntProvider.create(0), AbstractBlock.Settings.copy(Blocks.SANDSTONE)));
    public static final Block GRAVEL_ALUMINIUM_ORE = registerWithItem("gravel_aluminium_ore", new ColoredFallingBlock((new ColorCode(-8356741)), AbstractBlock.Settings.copy(Blocks.GRAVEL).requiresTool()));
    public static final Block RAW_TIN_ORE = registerWithItem("raw_tin_ore", new Block(AbstractBlock.Settings.copy(Blocks.RAW_COPPER_BLOCK).mapColor(MapColor.BLACK)));
    public static final Block RAW_NICKEL_ORE = registerWithItem("raw_nickel_ore", new Block(AbstractBlock.Settings.copy(Blocks.RAW_IRON_BLOCK).mapColor(MapColor.BROWN)));
    public static final Block RAW_ALUMINIUM_ORE = registerWithItem("raw_aluminium_ore", new Block(AbstractBlock.Settings.copy(Blocks.RAW_GOLD_BLOCK).mapColor(MapColor.BROWN)));
    public static final Block RAW_CHROME_ORE = registerWithItem("raw_chrome_ore", new Block(AbstractBlock.Settings.copy(Blocks.RAW_GOLD_BLOCK).mapColor(MapColor.LIGHT_GRAY)));

    public static final Block CRANK = registerWithItem("crank", new CrankBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASS).strength(1.0F, 0.5F).sounds(BlockSoundGroup.WOOD).pistonBehavior(PistonBehavior.DESTROY).noCollision()));
    public static final Block GEARBOX = registerWithItem("gearbox", new GearBoxBlock(AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.COPPER)));
    public static final Block WOODEN_SHAFT = registerWithItem("wooden_shaft", new WoodenShaftBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).requiresTool().strength(1.0F, 0.5F).sounds(BlockSoundGroup.WOOD)));

    public static <T extends Block> T register(String name, T block) {
        return Registry.register(Registries.BLOCK, Identifier.of(IljaTech.MOD_ID, name), block);
    }

    public static <T extends Block> T registerWithItem(String name, T block, Item.Settings settings) {
        T registered = register(name, block);
        ModItems.register(name, new BlockItem(registered, settings));
        return registered;
    }

    public static <T extends Block> T registerWithItem(String name, T block) {
        T registered = register(name, block);
        ModItems.register(name, new BlockItem(registered, new Item.Settings()));
        return registered;
    }

    public static void load() {}
}
