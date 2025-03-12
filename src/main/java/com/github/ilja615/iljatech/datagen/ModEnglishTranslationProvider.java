package com.github.ilja615.iljatech.datagen;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.blocks.foundry.FoundryBlockEntity;
import com.klikli_dev.modonomicon.api.datagen.AbstractModonomiconLanguageProvider;
import com.klikli_dev.modonomicon.api.datagen.ModonomiconLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.data.DataOutput;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.jetbrains.annotations.NotNull;

public class ModEnglishTranslationProvider extends AbstractModonomiconLanguageProvider {
    public ModEnglishTranslationProvider(DataOutput output, ModonomiconLanguageProvider cachedProvider) {
        super(output, IljaTech.MOD_ID, "en_us", cachedProvider);
    }

    protected void addMiscTranslations(){
        this.add("itemGroup.iljatech.stuff", "iljaTech Stuff");
        this.add("item.iljatech.book", "Book");
        this.add("item.iljatech.boiled_egg", "Boiled Egg");
        this.add("item.iljatech.raw_tin", "Raw Tin Ore");
        this.add("item.iljatech.raw_nickel", "Raw Nickel Ore");
        this.add("item.iljatech.raw_aluminium", "Raw Aluminium Ore");
        this.add("item.iljatech.raw_chrome", "Raw Chrome Ore");
        this.add("item.iljatech.tin_ingot", "Tin Ingot");
        this.add("item.iljatech.nickel_ingot", "Nickel Ingot");
        this.add("item.iljatech.aluminium_ingot", "Aluminium Ingot");
        this.add("item.iljatech.chrome_ingot", "Chrome Ingot");
        this.add("item.iljatech.crushed_iron_ore", "Crushed Raw Iron");
        this.add("item.iljatech.crushed_gold_ore", "Crushed Raw Gold");
        this.add("item.iljatech.crushed_copper_ore", "Crushed Raw Copper");
        this.add("item.iljatech.crushed_tin_ore", "Crushed Raw Tin");
        this.add("item.iljatech.crushed_nickel_ore", "Crushed Raw Nickel");
        this.add("item.iljatech.crushed_aluminium_ore", "Crushed Raw Aluminium");
        this.add("item.iljatech.crushed_chrome_ore", "Crushed Raw Chrome");
        this.add("item.iljatech.iron_hammer", "Iron Hammer");
        this.add("item.iljatech.iron_saw", "Iron Saw");
        this.add("item.iljatech.fire_clay_ball", "Fire Clay Ball");
        this.add("item.iljatech.fire_brick", "Fire Brick");
        this.add("item.iljatech.ash", "Ashes");
        this.add("item.iljatech.bronze_ingot", "Bronze Ingot");
        this.add("item.iljatech.bronze_gear", "Bronze Gear");
        this.add("item.iljatech.steel_ingot", "Steel Ingot");
        this.add("item.iljatech.coke", "Coke");
        this.add("item.iljatech.crushed_coke", "Crushed Coke");
        this.add("item.iljatech.ferrous_slag", "Ferrous Slag");
        this.add("item.iljatech.steel_bloom", "Steel Bloom");
        this.add("block.iljatech.tin_ore", "Tin Ore");
        this.add("block.iljatech.nickel_ore", "Nickel Ore");
        this.add("block.iljatech.chrome_ore", "Chrome Ore");
        this.add("block.iljatech.deepslate_tin_ore", "Deepslate Tin Ore");
        this.add("block.iljatech.deepslate_nickel_ore", "Deepslate Nickel Ore");
        this.add("block.iljatech.deepslate_chrome_ore", "Deepslate Chrome Ore");
        this.add("block.iljatech.sandstone_aluminium_ore", "Sandstone Aluminium Ore");
        this.add("block.iljatech.red_sandstone_aluminium_ore", "Red Sandstone Aluminium Ore");
        this.add("block.iljatech.gravel_aluminium_ore", "Gravel Aluminium Ore");
        this.add("block.iljatech.raw_tin_ore", "Raw Tin Ore Block");
        this.add("block.iljatech.raw_nickel_ore", "Raw Nickel Ore Block");
        this.add("block.iljatech.raw_aluminium_ore", "Raw Aluminium Ore Block");
        this.add("block.iljatech.raw_chrome_ore", "Raw Chrome Ore Block");
        this.add("block.iljatech.nailed_acacia_planks", "Nailed Acacia Planks");
        this.add("block.iljatech.nailed_bamboo_planks", "Nailed Bamboo Planks");
        this.add("block.iljatech.nailed_birch_planks", "Nailed Birch Planks");
        this.add("block.iljatech.nailed_cherry_planks", "Nailed Cherry Planks");
        this.add("block.iljatech.nailed_crimson_planks", "Nailed Crimson Planks");
        this.add("block.iljatech.nailed_dark_oak_planks", "Nailed Dark Oak Planks");
        this.add("block.iljatech.nailed_jungle_planks", "Nailed Jungle Planks");
        this.add("block.iljatech.nailed_mangrove_planks", "Nailed Mangrove Planks");
        this.add("block.iljatech.nailed_oak_planks", "Nailed Oak Planks");
        this.add("block.iljatech.nailed_spruce_planks", "Nailed Spruce Planks");
        this.add("block.iljatech.nailed_warped_planks", "Nailed Warped Planks");
        this.add("block.iljatech.fire_clay", "Fire Clay");
        this.add("block.iljatech.fire_bricks", "Fire Bricks");
        this.add("block.iljatech.limestone", "Limestone");
        this.add("block.iljatech.limestone_slab", "Limestone Slab");
        this.add("block.iljatech.limestone_stairs", "Limestone Stairs");
        this.add("block.iljatech.limestone_wall", "Limestone Wall");
        this.add("block.iljatech.stoked_fire", "Stoked Fire");
        this.add("block.iljatech.bellows", "Bellows");
        this.add("block.iljatech.foundry", "Foundry");
        this.add("block.iljatech.firebox", "Firebox");
        this.add("block.iljatech.crank", "Crank");
        this.add("block.iljatech.gearbox", "Gear Box");
        this.add("block.iljatech.wooden_shaft", "Wooden Shaft");
        this.add("block.iljatech.roller_mill", "Roller Mill");
        this.add("block.iljatech.drill", "Drill");
        this.add("block.iljatech.turbine", "Turbine");
        this.add("block.iljatech.iron_rod", "Iron Rod");
        this.add("block.iljatech.iron_plate", "Iron Plate");
        this.add("item.iljatech.iron_nails", "Iron Nails");
        this.add("block.iljatech.copper_rod", "Copper Rod");
        this.add("block.iljatech.copper_wire", "Copper Wire");
        this.add("effect.iljatech.stunned", "Stunned");
        this.add("subtitles.iljatech.bellows_inhale", "Bellows Inhale");
        this.add("subtitles.iljatech.bellows_exhale", "Bellows Exhale");
        this.add("container.iljatech.foundry", "Foundry");
    }

    @Override
    protected void addTranslations() {
        this.addMiscTranslations();
    }
}
