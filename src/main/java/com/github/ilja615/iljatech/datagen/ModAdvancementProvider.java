package com.github.ilja615.iljatech.datagen;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.blocks.researchtable.BlueprintingCriterion;
import com.github.ilja615.iljatech.init.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModAdvancementProvider extends FabricAdvancementProvider {

    public ModAdvancementProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(HolderLookup.Provider registryLookup, Consumer<AdvancementHolder> consumer) {
        generateBlueprintAdvancement(registryLookup, consumer, ModBlocks.BELLOWS);
        generateBlueprintAdvancement(registryLookup, consumer, ModBlocks.FOUNDRY);
        generateBlueprintAdvancement(registryLookup, consumer, ModBlocks.PULVERIZER_MILL);
        generateBlueprintAdvancement(registryLookup, consumer, ModBlocks.COKE_OVEN);

    }

    private AdvancementHolder generateBlueprintAdvancement(HolderLookup.Provider registryLookup, Consumer<AdvancementHolder> consumer, ItemLike item) {
        String str = item.asItem().getDescriptionId();
        str = str.substring(str.lastIndexOf(".")+1);
        String name = item.asItem().getDescription().getString();
        return Advancement.Builder.advancement()
                .display(
                        item, // The display icon
                        Component.literal(name+" Researched"), // The title
                        Component.literal("Unlocked "+str+" with the blueprinting"), // The description
                        ResourceLocation.parse("textures/gui/advancements/backgrounds/adventure.png"), // Background image used
                        AdvancementType.TASK, // Options: TASK, CHALLENGE, GOAL
                        true, // Show toast top right
                        false, // Announce to chat
                        true // Hidden in the advancement tab
                )
                // The first string used in criterion is the name referenced by other advancements when they want to have 'requirements'
                .addCriterion(str, BlueprintingCriterion.Conditions.create(ItemPredicate.Builder.item().of(item).build()))
                .save(consumer, IljaTech.MOD_ID + "/blueprint_"+str);
    }
}