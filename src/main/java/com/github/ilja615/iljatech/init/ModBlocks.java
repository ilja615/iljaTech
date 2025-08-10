package com.github.ilja615.iljatech.init;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.blocks.*;
import com.github.ilja615.iljatech.blocks.AxisRodBlock;
import com.github.ilja615.iljatech.blocks.bellows.BellowsBlock;
import com.github.ilja615.iljatech.blocks.cokeoven.CokeOvenBlock;
import com.github.ilja615.iljatech.blocks.conveyorbelt.ConveyorBeltBlock;
import com.github.ilja615.iljatech.blocks.firebox.FireboxBlock;
import com.github.ilja615.iljatech.blocks.foundry.FoundryBlock;
import com.github.ilja615.iljatech.blocks.funnel.FunnelBlock;
import com.github.ilja615.iljatech.blocks.hatch.ItemHatchBlock;
import com.github.ilja615.iljatech.blocks.pipe.ClayPipeBlock;
import com.github.ilja615.iljatech.blocks.pipe.PipeBlock;
import com.github.ilja615.iljatech.blocks.rollermill.RollerMillBlock;
import com.github.ilja615.iljatech.blocks.turbine.TurbineBlock;
import com.github.ilja615.iljatech.blocks.windmill.WindmillBlock;
import com.github.ilja615.iljatech.blocks.wire.WireBlock;
import net.minecraft.block.*;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
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
    public static final Block RED_SANDSTONE_ALUMINIUM_ORE = registerWithItem("red_sandstone_aluminium_ore", new ExperienceDroppingBlock(ConstantIntProvider.create(0), AbstractBlock.Settings.copy(Blocks.RED_SANDSTONE)));
    public static final Block GRAVEL_ALUMINIUM_ORE = registerWithItem("gravel_aluminium_ore", new ColoredFallingBlock((new ColorCode(-8356741)), AbstractBlock.Settings.copy(Blocks.GRAVEL).requiresTool()));
    public static final Block RAW_TIN_ORE = registerWithItem("raw_tin_ore", new Block(AbstractBlock.Settings.copy(Blocks.RAW_COPPER_BLOCK).mapColor(MapColor.BLACK)));
    public static final Block RAW_NICKEL_ORE = registerWithItem("raw_nickel_ore", new Block(AbstractBlock.Settings.copy(Blocks.RAW_IRON_BLOCK).mapColor(MapColor.BROWN)));
    public static final Block RAW_ALUMINIUM_ORE = registerWithItem("raw_aluminium_ore", new Block(AbstractBlock.Settings.copy(Blocks.RAW_GOLD_BLOCK).mapColor(MapColor.BROWN)));
    public static final Block RAW_CHROME_ORE = registerWithItem("raw_chrome_ore", new Block(AbstractBlock.Settings.copy(Blocks.RAW_GOLD_BLOCK).mapColor(MapColor.LIGHT_GRAY)));

    public static final Block NAILED_ACACIA_PLANKS = registerWithItem("nailed_acacia_planks", new Block(AbstractBlock.Settings.copy(Blocks.ACACIA_PLANKS)));
    public static final Block NAILED_BAMBOO_PLANKS = registerWithItem("nailed_bamboo_planks", new Block(AbstractBlock.Settings.copy(Blocks.BAMBOO_PLANKS)));
    public static final Block NAILED_BIRCH_PLANKS = registerWithItem("nailed_birch_planks", new Block(AbstractBlock.Settings.copy(Blocks.BIRCH_PLANKS)));
    public static final Block NAILED_CHERRY_PLANKS = registerWithItem("nailed_cherry_planks", new Block(AbstractBlock.Settings.copy(Blocks.CHERRY_PLANKS)));
    public static final Block NAILED_CRIMSON_PLANKS = registerWithItem("nailed_crimson_planks", new Block(AbstractBlock.Settings.copy(Blocks.CRIMSON_PLANKS)));
    public static final Block NAILED_DARK_OAK_PLANKS = registerWithItem("nailed_dark_oak_planks", new Block(AbstractBlock.Settings.copy(Blocks.DARK_OAK_PLANKS)));
    public static final Block NAILED_JUNGLE_PLANKS = registerWithItem("nailed_jungle_planks", new Block(AbstractBlock.Settings.copy(Blocks.JUNGLE_PLANKS)));
    public static final Block NAILED_MANGROVE_PLANKS = registerWithItem("nailed_mangrove_planks", new Block(AbstractBlock.Settings.copy(Blocks.MANGROVE_PLANKS)));
    public static final Block NAILED_OAK_PLANKS = registerWithItem("nailed_oak_planks", new Block(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS)));
    public static final Block NAILED_SPRUCE_PLANKS = registerWithItem("nailed_spruce_planks", new Block(AbstractBlock.Settings.copy(Blocks.SPRUCE_PLANKS)));
    public static final Block NAILED_WARPED_PLANKS = registerWithItem("nailed_warped_planks", new Block(AbstractBlock.Settings.copy(Blocks.WARPED_PLANKS)));

    public static final Block BELLOWS = registerWithItem("bellows", new BellowsBlock(AbstractBlock.Settings.create().mapColor(MapColor.BROWN).strength(3.0F, 6.0F).sounds(BlockSoundGroup.WOOD).nonOpaque()));
    public static final Block STOKED_FIRE = registerWithItem("stoked_fire", new StokedFireBlock(AbstractBlock.Settings.create().mapColor(MapColor.BRIGHT_RED).replaceable().noCollision().breakInstantly().luminance(state -> 15).sounds(BlockSoundGroup.WOOL).pistonBehavior(PistonBehavior.DESTROY)));
    public static final Block FOUNDRY = registerWithItem("foundry", new FoundryBlock(AbstractBlock.Settings.copy(Blocks.BRICKS).mapColor(MapColor.TERRACOTTA_WHITE).luminance(state -> state.get(FoundryBlock.LIT) ? 8 : 0)));
    public static final Block FIREBOX = registerWithItem("firebox", new FireboxBlock(AbstractBlock.Settings.copy(Blocks.BRICKS).mapColor(MapColor.TERRACOTTA_WHITE).luminance(state -> state.get(FireboxBlock.LIT).luminance)));
    public static final Block COKE_OVEN = registerWithItem("coke_oven", new CokeOvenBlock(AbstractBlock.Settings.copy(Blocks.BRICKS).mapColor(MapColor.DULL_RED).nonOpaque()));
    public static final Block ITEM_HATCH = registerWithItem("item_hatch", new ItemHatchBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)));

    public static final Block FIRE_CLAY = registerWithItem("fire_clay", new Block(AbstractBlock.Settings.copy(Blocks.CLAY).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final Block FIRE_BRICKS = registerWithItem("fire_bricks", new Block(AbstractBlock.Settings.copy(Blocks.BRICKS).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final Block CLINKER_BRICKS = registerWithItem("clinker_bricks", new Block(AbstractBlock.Settings.copy(Blocks.BRICKS).mapColor(MapColor.DULL_RED)));
    public static final Block LIMESTONE = registerWithItem("limestone", new Block(AbstractBlock.Settings.copy(Blocks.STONE).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final Block LIMESTONE_SLAB = registerWithItem("limestone_slab", new SlabBlock(AbstractBlock.Settings.copy(LIMESTONE)));
    public static final Block LIMESTONE_STAIRS = registerWithItem("limestone_stairs", new StairsBlock(LIMESTONE.getDefaultState(), AbstractBlock.Settings.copy(LIMESTONE)));
    public static final Block LIMESTONE_WALL = registerWithItem("limestone_wall", new WallBlock(AbstractBlock.Settings.copy(LIMESTONE)));

    public static final Block CRANK = registerWithItem("crank", new CrankBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASS).strength(1.0F, 0.5F).sounds(BlockSoundGroup.WOOD).pistonBehavior(PistonBehavior.DESTROY).noCollision()));
    public static final Block GEARBOX = registerWithItem("gearbox", new GearBoxBlock(AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.COPPER)));
    public static final Block WOODEN_SHAFT = registerWithItem("wooden_shaft", new WoodenShaftBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.0F, 0.5F).sounds(BlockSoundGroup.WOOD).nonOpaque()));
    public static final Block ROLLER_MILL = registerWithItem("roller_mill", new RollerMillBlock(AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.COPPER)));
    public static final Block DRILL = registerWithItem("drill", new DrillBlock(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL).nonOpaque()));
    public static final Block TURBINE = registerWithItem("turbine", new TurbineBlock(AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.COPPER)));
    public static final Block WINDMILL = registerWithItem("windmill", new WindmillBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASS).strength(1.0F, 0.5F).sounds(BlockSoundGroup.WOOD)));
    public static final Block WINDMILL_BLADE = registerWithItem("windmill_blade", new Block(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASS).strength(1.0F, 0.5F).sounds(BlockSoundGroup.WOOD).nonOpaque()));
    public static final Block CONVEYOR_BELT = registerWithItem("conveyor_belt", new ConveyorBeltBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASS).strength(1.0F, 0.5F).sounds(BlockSoundGroup.WOOD).nonOpaque()));

    public static final Block STEEL_PIPE = registerWithItem("steel_pipe", new PipeBlock(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.COPPER).nonOpaque()));
    public static final Block FUNNEL = registerWithItem("funnel", new FunnelBlock(AbstractBlock.Settings.create().mapColor(MapColor.DIRT_BROWN).requiresTool().strength(1.0F, 2.0F).sounds(BlockSoundGroup.DECORATED_POT).nonOpaque()));
    public static final Block CLAY_PIPE = registerWithItem("clay_pipe", new ClayPipeBlock(AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_BLUE_GRAY).strength(0.6F).sounds(BlockSoundGroup.GRAVEL).nonOpaque()));
    public static final Block TERRACOTTA_PIPE = registerWithItem("terracotta_pipe", new PipeBlock(AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).sounds(BlockSoundGroup.DECORATED_POT).requiresTool().strength(1.25F, 4.2F).nonOpaque()));

    public static final Block COPPER_ROD = registerWithItem("copper_rod", new AxisRodBlock(AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).requiresTool().strength(1.0F, 2.0F).sounds(BlockSoundGroup.COPPER)));
    public static final Block COPPER_WIRE = registerWithItem("copper_wire", new WireBlock(AbstractBlock.Settings.copy(Blocks.RAIL)));
    public static final Block IRON_PLATE = registerWithItem("iron_plate", new PlateBlock(AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).requiresTool().strength(2.5F, 6.0F).sounds(BlockSoundGroup.METAL)));
    public static final Block IRON_ROD = registerWithItem("iron_rod", new AxisRodBlock(AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).requiresTool().strength(1.0F, 2.0F).sounds(BlockSoundGroup.METAL)));

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
