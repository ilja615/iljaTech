package com.github.ilja615.iljatech.datagen.book.iljatech.basics;

import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.init.ModItems;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookImagePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;

public class HammerSawEntry extends EntryProvider {
    public static final String ID = "hammersaw";

    public HammerSawEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("page1", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
        );

        this.pageTitle(this.entryName());
        // \s tells java to keep the spaces at the end of the line. Otherwise it will remove.
        // Due to markdown using multiple spaces to indicate a line break, we need to keep the spaces.
        this.pageText("""
                I have found that working raw materials with my bare hands is simply too limiting.
                I must first equip myself with proper tools. A simple hammer seems to be the most practical starting point: with enough force, I can crush rocks, or flatten ingots of metal into plates.
                """);

        this.page("page2", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1("iljatech:iron_hammer")
                .withRecipeId2("iljatech:iron_plate")

        );

        this.page("page3", () -> BookCraftingRecipePageModel.create()
                .withText(this.context().pageText())
                .withRecipeId2("iljatech:iron_saw")
        );
        this.pageText("""
                        Out of these plates, in turn, I can craft a sharp saw. It will allow me to cut wood with precision."""
        );

        this.page("tutorial1", () -> BookImagePageModel.create()
                        .withTitle(this.context().pageTitle())
                        .withImages(this.modLoc("textures/gui/book/interaction_saw_wooden_frame.png")))
                .withBorder(false);
        this.pageTitle("Basic Woodworking");
    }

    @Override
    protected String entryName() {
        return "Hammer and Saw";
    }

    @Override
    protected String entryDescription() {
        return "On the simple tools I will need...";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(ModItems.IRON_SAW);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}