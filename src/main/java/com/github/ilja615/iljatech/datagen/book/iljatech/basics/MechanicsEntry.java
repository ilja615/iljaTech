package com.github.ilja615.iljatech.datagen.book.iljatech.basics;

import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.init.ModItems;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;

public class MechanicsEntry extends EntryProvider {
    public static final String ID = "mech";

    public MechanicsEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("page1", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
        );

        this.pageTitle(this.entryName());
        this.pageText("""
            Rotational force seems to be the simplest form of useful work.
            With a hand-crank I can turn axles to drive more complex machinery.   \s
            """
        );

        this.page("page2", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1("iljatech:crank")
                .withRecipeId2("iljatech:wooden_shaft")
        );
    }

    @Override
    protected String entryName() {
        return "Harnessing Rotation";
    }

    @Override
    protected String entryDescription() {
        return "On the Basics of Mechanical Power...";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(ModBlocks.CRANK);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}