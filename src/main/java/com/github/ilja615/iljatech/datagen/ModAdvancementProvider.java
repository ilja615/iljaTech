package com.github.ilja615.iljatech.datagen;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.blocks.researchtable.BlueprintingCriterion;
import com.github.ilja615.iljatech.init.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.RecipeCraftedCriterion;
import net.minecraft.item.ItemConvertible;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModAdvancementProvider extends FabricAdvancementProvider {

    public ModAdvancementProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(RegistryWrapper.WrapperLookup registryLookup, Consumer<AdvancementEntry> consumer) {
        generateBlueprintAdvancement(registryLookup, consumer, ModBlocks.BELLOWS);
        generateBlueprintAdvancement(registryLookup, consumer, ModBlocks.FOUNDRY);
        generateBlueprintAdvancement(registryLookup, consumer, ModBlocks.PULVERIZER_MILL);
        generateBlueprintAdvancement(registryLookup, consumer, ModBlocks.COKE_OVEN);

    }

    private AdvancementEntry generateBlueprintAdvancement(RegistryWrapper.WrapperLookup registryLookup, Consumer<AdvancementEntry> consumer, ItemConvertible item) {
        String str = item.asItem().getTranslationKey();
        str = str.substring(str.lastIndexOf(".")+1);
        String name = item.asItem().getName().getString();
        return Advancement.Builder.create()
                .display(
                        item, // The display icon
                        Text.literal("Researched "+name), // The title
                        Text.literal("Unlocked "+str+" with the blueprinting"), // The description
                        Identifier.of("textures/gui/advancements/backgrounds/adventure.png"), // Background image used
                        AdvancementFrame.TASK, // Options: TASK, CHALLENGE, GOAL
                        true, // Show toast top right
                        false, // Announce to chat
                        true // Hidden in the advancement tab
                )
                // The first string used in criterion is the name referenced by other advancements when they want to have 'requirements'
                .criterion(str, BlueprintingCriterion.Conditions.create(ItemPredicate.Builder.create().items(item).build()))
                .build(consumer, IljaTech.MOD_ID + "/blueprint_"+str);
    }
}