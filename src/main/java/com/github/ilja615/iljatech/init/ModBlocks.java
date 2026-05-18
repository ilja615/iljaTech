package com.github.ilja615.iljatech.init;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.blocks.*;
import com.github.ilja615.iljatech.blocks.bellows.BellowsBlock;
import com.github.ilja615.iljatech.blocks.carpentry.CarpentryBlock;
import com.github.ilja615.iljatech.blocks.cokeoven.CokeOvenBlock;
import com.github.ilja615.iljatech.blocks.conveyorbelt.ConveyorBeltBlock;
import com.github.ilja615.iljatech.blocks.firebox.FireboxBlock;
import com.github.ilja615.iljatech.blocks.foundry.FoundryBlock;
import com.github.ilja615.iljatech.blocks.funnel.FunnelBlock;
import com.github.ilja615.iljatech.blocks.hatch.ItemHatchBlock;
import com.github.ilja615.iljatech.blocks.pipe.PipeBlock;
import com.github.ilja615.iljatech.blocks.pulverizermill.PulverizerMillBlock;
import com.github.ilja615.iljatech.blocks.researchtable.BlueprintTableBlock;
import com.github.ilja615.iljatech.blocks.researchtable.ResearchTableBlock;
import com.github.ilja615.iljatech.blocks.rollermill.RollerMillBlock;
import com.github.ilja615.iljatech.blocks.rusty.RustingBlock;
import com.github.ilja615.iljatech.blocks.rusty.RustingSlabBlock;
import com.github.ilja615.iljatech.blocks.rusty.RustingStairsBlock;
import com.github.ilja615.iljatech.blocks.sifter.SifterBlock;
import com.github.ilja615.iljatech.blocks.spinningframe.SpinningFrameBlock;
import com.github.ilja615.iljatech.blocks.squeezer.SqueezerBlock;
import com.github.ilja615.iljatech.blocks.stampinghammer.HammerBlock;
import com.github.ilja615.iljatech.blocks.turbine.TurbineBlock;
import com.github.ilja615.iljatech.blocks.windmill.WindVaneBlock;
import com.github.ilja615.iljatech.blocks.windmill.WindmillBlock;
import com.github.ilja615.iljatech.blocks.windmill.WindmillSailBlock;
import com.github.ilja615.iljatech.blocks.wire.WireBlock;
import net.minecraft.block.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class ModBlocks {
    public static final Block TIN_ORE = registerWithItem("tin_ore", new DropExperienceBlock(ConstantInt.of(0), BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_ORE)));
    public static final Block NICKEL_ORE = registerWithItem("nickel_ore", new DropExperienceBlock(ConstantInt.of(0), BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_ORE)));
    public static final Block CHROME_ORE = registerWithItem("chrome_ore", new DropExperienceBlock(ConstantInt.of(0), BlockBehaviour.Properties.ofFullCopy(Blocks.GOLD_ORE)));
    public static final Block DEEPSLATE_TIN_ORE = registerWithItem("deepslate_tin_ore", new DropExperienceBlock(ConstantInt.of(0), BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_COPPER_ORE)));
    public static final Block DEEPSLATE_NICKEL_ORE = registerWithItem("deepslate_nickel_ore", new DropExperienceBlock(ConstantInt.of(0), BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_IRON_ORE)));
    public static final Block DEEPSLATE_CHROME_ORE = registerWithItem("deepslate_chrome_ore", new DropExperienceBlock(ConstantInt.of(0), BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_GOLD_ORE)));
    public static final Block SANDSTONE_ALUMINIUM_ORE = registerWithItem("sandstone_aluminium_ore", new DropExperienceBlock(ConstantInt.of(0), BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE)));
    public static final Block RED_SANDSTONE_ALUMINIUM_ORE = registerWithItem("red_sandstone_aluminium_ore", new DropExperienceBlock(ConstantInt.of(0), BlockBehaviour.Properties.ofFullCopy(Blocks.RED_SANDSTONE)));
    public static final Block GRAVEL_ALUMINIUM_ORE = registerWithItem("gravel_aluminium_ore", new ColoredFallingBlock((new ColorRGBA(-8356741)), BlockBehaviour.Properties.ofFullCopy(Blocks.GRAVEL).requiresCorrectToolForDrops()));
    public static final Block RAW_TIN_ORE = registerWithItem("raw_tin_ore", new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.RAW_COPPER_BLOCK).mapColor(MapColor.COLOR_BLACK)));
    public static final Block RAW_NICKEL_ORE = registerWithItem("raw_nickel_ore", new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.RAW_IRON_BLOCK).mapColor(MapColor.COLOR_BROWN)));
    public static final Block RAW_ALUMINIUM_ORE = registerWithItem("raw_aluminium_ore", new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.RAW_GOLD_BLOCK).mapColor(MapColor.COLOR_BROWN)));
    public static final Block RAW_CHROME_ORE = registerWithItem("raw_chrome_ore", new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.RAW_GOLD_BLOCK).mapColor(MapColor.COLOR_LIGHT_GRAY)));

    public static final Block NAILED_ACACIA_PLANKS = registerWithItem("nailed_acacia_planks", new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_PLANKS)));
    public static final Block NAILED_BAMBOO_PLANKS = registerWithItem("nailed_bamboo_planks", new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.BAMBOO_PLANKS)));
    public static final Block NAILED_BIRCH_PLANKS = registerWithItem("nailed_birch_planks", new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.BIRCH_PLANKS)));
    public static final Block NAILED_CHERRY_PLANKS = registerWithItem("nailed_cherry_planks", new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.CHERRY_PLANKS)));
    public static final Block NAILED_CRIMSON_PLANKS = registerWithItem("nailed_crimson_planks", new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.CRIMSON_PLANKS)));
    public static final Block NAILED_DARK_OAK_PLANKS = registerWithItem("nailed_dark_oak_planks", new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.DARK_OAK_PLANKS)));
    public static final Block NAILED_JUNGLE_PLANKS = registerWithItem("nailed_jungle_planks", new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.JUNGLE_PLANKS)));
    public static final Block NAILED_MANGROVE_PLANKS = registerWithItem("nailed_mangrove_planks", new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.MANGROVE_PLANKS)));
    public static final Block NAILED_OAK_PLANKS = registerWithItem("nailed_oak_planks", new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));
    public static final Block NAILED_SPRUCE_PLANKS = registerWithItem("nailed_spruce_planks", new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_PLANKS)));
    public static final Block NAILED_WARPED_PLANKS = registerWithItem("nailed_warped_planks", new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.WARPED_PLANKS)));

    public static final Block ACACIA_FRAME = registerWithItem("frame_acacia", new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_PLANKS).noOcclusion()));
    public static final Block BAMBOO_FRAME = registerWithItem("frame_bamboo", new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.BAMBOO_PLANKS).noOcclusion()));
    public static final Block BIRCH_FRAME = registerWithItem("frame_birch", new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.BIRCH_PLANKS).noOcclusion()));
    public static final Block CHERRY_FRAME = registerWithItem("frame_cherry", new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.CHERRY_PLANKS).noOcclusion()));
    public static final Block CRIMSON_FRAME = registerWithItem("frame_crimson", new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.CRIMSON_PLANKS).noOcclusion()));
    public static final Block DARK_OAK_FRAME = registerWithItem("frame_dark_oak", new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.DARK_OAK_PLANKS).noOcclusion()));
    public static final Block JUNGLE_FRAME = registerWithItem("frame_jungle", new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.JUNGLE_PLANKS).noOcclusion()));
    public static final Block MANGROVE_FRAME = registerWithItem("frame_mangrove", new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.MANGROVE_PLANKS).noOcclusion()));
    public static final Block OAK_FRAME = registerWithItem("frame_oak", new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).noOcclusion()));
    public static final Block SPRUCE_FRAME = registerWithItem("frame_spruce", new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_PLANKS).noOcclusion()));
    public static final Block WARPED_FRAME = registerWithItem("frame_warped", new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.WARPED_PLANKS).noOcclusion()));

    public static final Block SAWDUST = registerWithItem("sawdust", new SawDustBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).noOcclusion()));
    public static final Block CARPENTRY = registerWithItem("carpentry", new CarpentryBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));

    public static final Block BELLOWS = registerWithItem("bellows", new BellowsBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(3.0F, 6.0F).sound(SoundType.WOOD).noOcclusion()));
    public static final Block STOKED_FIRE = registerWithItem("stoked_fire", new StokedFireBlock(BlockBehaviour.Properties.of().mapColor(MapColor.FIRE).replaceable().noCollission().instabreak().lightLevel(state -> 15).sound(SoundType.WOOL).pushReaction(PushReaction.DESTROY)));
    public static final Block FOUNDRY = registerWithItem("foundry", new FoundryBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS).mapColor(MapColor.TERRACOTTA_WHITE).lightLevel(state -> state.getValue(FoundryBlock.LIT) ? 8 : 0)));
    public static final Block FIREBOX = registerWithItem("firebox", new FireboxBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS).mapColor(MapColor.TERRACOTTA_WHITE).lightLevel(state -> state.getValue(FireboxBlock.LIT).luminance)));
    public static final Block COKE_OVEN = registerWithItem("coke_oven", new CokeOvenBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS).mapColor(MapColor.CRIMSON_NYLIUM).noOcclusion()));
    public static final Block ITEM_HATCH = registerWithItem("item_hatch", new ItemHatchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    public static final Block FIRE_CLAY = registerWithItem("fire_clay", new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.CLAY).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final Block FIRE_BRICKS = registerWithItem("fire_bricks", new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final Block CLINKER_BRICKS = registerWithItem("clinker_bricks", new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS).mapColor(MapColor.CRIMSON_NYLIUM)));
    public static final Block LIMESTONE = registerWithItem("limestone", new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final Block LIMESTONE_SLAB = registerWithItem("limestone_slab", new SlabBlock(BlockBehaviour.Properties.ofFullCopy(LIMESTONE)));
    public static final Block LIMESTONE_STAIRS = registerWithItem("limestone_stairs", new StairBlock(LIMESTONE.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(LIMESTONE)));
    public static final Block LIMESTONE_WALL = registerWithItem("limestone_wall", new WallBlock(BlockBehaviour.Properties.ofFullCopy(LIMESTONE)));

    public static final Block IRON_SHEETMETAL = registerWithItem("iron_sheetmetal", new RustingBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(2.0F, 4.0F).mapColor(MapColor.METAL).sound(SoundType.COPPER)));
    public static final Block IRON_SHEETMETAL_SLAB = registerWithItem("iron_sheetmetal_slab", new RustingSlabBlock(BlockBehaviour.Properties.ofFullCopy(IRON_SHEETMETAL)));
    public static final Block IRON_SHEETMETAL_STAIRS = registerWithItem("iron_sheetmetal_stairs", new RustingStairsBlock(IRON_SHEETMETAL.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(IRON_SHEETMETAL)));
    public static final Block EXPOSED_IRON_SHEETMETAL = registerWithItem("exposed_iron_sheetmetal", new RustingBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(2.0F, 4.0F).mapColor(MapColor.TERRACOTTA_LIGHT_GRAY).sound(SoundType.COPPER)));
    public static final Block EXPOSED_IRON_SHEETMETAL_SLAB = registerWithItem("exposed_iron_sheetmetal_slab", new RustingSlabBlock(BlockBehaviour.Properties.ofFullCopy(EXPOSED_IRON_SHEETMETAL)));
    public static final Block EXPOSED_IRON_SHEETMETAL_STAIRS = registerWithItem("exposed_iron_sheetmetal_stairs", new RustingStairsBlock(EXPOSED_IRON_SHEETMETAL.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(EXPOSED_IRON_SHEETMETAL)));
    public static final Block WEATHERED_IRON_SHEETMETAL = registerWithItem("weathered_iron_sheetmetal", new RustingBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(2.0F, 4.0F).mapColor(MapColor.DIRT).sound(SoundType.COPPER)));
    public static final Block WEATHERED_IRON_SHEETMETAL_SLAB = registerWithItem("weathered_iron_sheetmetal_slab", new RustingSlabBlock(BlockBehaviour.Properties.ofFullCopy(WEATHERED_IRON_SHEETMETAL)));
    public static final Block WEATHERED_IRON_SHEETMETAL_STAIRS = registerWithItem("weathered_iron_sheetmetal_stairs", new RustingStairsBlock(WEATHERED_IRON_SHEETMETAL.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(WEATHERED_IRON_SHEETMETAL)));
    public static final Block RUSTY_IRON_SHEETMETAL = registerWithItem("rusty_iron_sheetmetal", new RustingBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(2.0F, 4.0F).mapColor(MapColor.COLOR_BROWN).sound(SoundType.COPPER)));
    public static final Block RUSTY_IRON_SHEETMETAL_SLAB = registerWithItem("rusty_iron_sheetmetal_slab", new RustingSlabBlock(BlockBehaviour.Properties.ofFullCopy(RUSTY_IRON_SHEETMETAL)));
    public static final Block RUSTY_IRON_SHEETMETAL_STAIRS = registerWithItem("rusty_iron_sheetmetal_stairs", new RustingStairsBlock(RUSTY_IRON_SHEETMETAL.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(RUSTY_IRON_SHEETMETAL)));

    public static final Block CRANK = registerWithItem("crank", new CrankBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(1.0F, 0.5F).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY).noCollission()));
    public static final Block GEARBOX = registerWithItem("gearbox", new GearBoxBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundType.COPPER)));
    public static final Block WOODEN_SHAFT = registerWithItem("wooden_shaft", new WoodenShaftBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(1.0F, 0.5F).sound(SoundType.WOOD).noOcclusion()));
    public static final Block ROLLER_MILL = registerWithItem("roller_mill", new RollerMillBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundType.COPPER)));
    public static final Block DRILL = registerWithItem("drill", new DrillBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundType.METAL).noOcclusion()));
    public static final Block TURBINE = registerWithItem("turbine", new TurbineBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundType.COPPER)));
    public static final Block WINDMILL = registerWithItem("windmill", new WindmillBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(1.0F, 0.5F).sound(SoundType.WOOD).noOcclusion()));
    public static final Block WINDMILL_SAIL = registerWithItem("windmill_sail", new WindmillSailBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(1.0F, 0.5F).sound(SoundType.WOOD).noOcclusion()));
    public static final Block CONVEYOR_BELT = registerWithItem("conveyor_belt", new ConveyorBeltBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(1.0F, 0.5F).sound(SoundType.WOOD).noOcclusion()));
    public static final Block PULVERIZER_MILL = registerWithItem("pulverizer_mill", new PulverizerMillBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundType.METAL).noOcclusion()));
    public static final Block SQUEEZER = registerWithItem("squeezer", new SqueezerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(3.0F, 6.0F).sound(SoundType.WOOD).noOcclusion()));
    public static final Block SPINNING_FRAME = registerWithItem("spinning_frame", new SpinningFrameBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(1.0F, 3.0F).sound(SoundType.WOOD)));
    public static final Block WIND_VANE = registerWithItem("wind_vane", new WindVaneBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(1.0F, 0.5F).sound(SoundType.WOOD).noOcclusion()));
    public static final Block SIFTER = registerWithItem("sifter", new SifterBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundType.COPPER).noOcclusion()));

    public static final Block CHAIR = registerWithItem("chair", new ChairBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(1.0F, 4.0F).sound(SoundType.WOOD).noOcclusion()));
    public static final Block RESEARCH_TABLE = registerWithItem("research_table", new ResearchTableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).strength(3.0F, 6.0F).sound(SoundType.WOOD).noOcclusion()));
    public static final Block BLUEPRINT_TABLE = registerWithItem("blueprint_table", new BlueprintTableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).strength(3.0F, 6.0F).sound(SoundType.WOOD).noOcclusion()));

    public static final Block STEEL_PIPE = registerWithItem("steel_pipe", new PipeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundType.COPPER).noOcclusion()));
    public static final Block FUNNEL = registerWithItem("funnel", new FunnelBlock(BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).requiresCorrectToolForDrops().strength(1.0F, 2.0F).sound(SoundType.DECORATED_POT).noOcclusion()));
    public static final Block WOODEN_FUNNEL = registerWithItem("wooden_funnel", new FunnelBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).requiresCorrectToolForDrops().strength(1.0F, 4.0F).sound(SoundType.WOOD).noOcclusion()));
    public static final Block TERRACOTTA_PIPE = registerWithItem("terracotta_pipe", new PipeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).sound(SoundType.DECORATED_POT).requiresCorrectToolForDrops().strength(1.25F, 2.1F).noOcclusion()));
    public static final Block WOODEN_PIPE = registerWithItem("wooden_pipe", new PipeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).sound(SoundType.WOOD).requiresCorrectToolForDrops().strength(1.25F, 4.2F).noOcclusion()));

    public static final Block COPPER_ROD = registerWithItem("copper_rod", new AxisRodBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).requiresCorrectToolForDrops().strength(1.0F, 2.0F).sound(SoundType.COPPER)));
    public static final Block COPPER_WIRE = registerWithItem("copper_wire", new WireBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.RAIL)));
    public static final Block IRON_PLATE = registerWithItem("iron_plate", new PlateBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(2.5F, 6.0F).sound(SoundType.METAL)));
    public static final Block IRON_ROD = registerWithItem("iron_rod", new AxisRodBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(1.0F, 2.0F).sound(SoundType.METAL)));
    public static final Block TIN_PLATE = registerWithItem("tin_plate", new PlateBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(2.5F, 6.0F).sound(SoundType.METAL)));

    public static final Block STEEL_BLOCK = registerWithItem("steel_block", new HammerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(6.0F, 12.0F).mapColor(MapColor.DEEPSLATE).sound(SoundType.ANVIL)));

    public static final Block FLAX_SEEDS = registerWithItem("flax_seeds", new FlaxBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).noCollission().randomTicks().instabreak().sound(SoundType.CROP).pushReaction(PushReaction.DESTROY)));
    public static final Block FLAX = registerWithItem("flax", new DoublePlantBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).noCollission().instabreak().sound(SoundType.CROP).pushReaction(PushReaction.DESTROY)));

    public static <T extends Block> T register(String name, T block) {
        return Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, name), block);
    }

    public static <T extends Block> T registerWithItem(String name, T block, Item.Properties settings) {
        T registered = register(name, block);
        ModItems.registerBlockItem(name, registered, settings);
        return registered;
    }

    public static <T extends Block> T registerWithItem(String name, T block) {
        return registerWithItem(name, block, new Item.Properties());
    }

    public static void load() {}
}
