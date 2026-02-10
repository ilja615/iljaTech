package com.github.ilja615.iljatech.datagen.book.iljatech.basics;

import com.github.ilja615.iljatech.init.ModBlocks;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;

public class CarpentryEntry extends EntryProvider {
    public static final String ID = "carpentry";

    public CarpentryEntry(CategoryProvider parent) {
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
                My latest invention was a success, but for making more complicated designs I am going to need a proper workspace.
                I should first make a carpentry bench where I can more efficiently use my hammer and saw.
                I should also be able to preserve the wood with the new oil I have extracted.
                """);

    }

    @Override
    protected String entryName() {
        return "Carpentry";
    }

    @Override
    protected String entryDescription() {
        return "On Advanced Woodworking...";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(ModBlocks.CARPENTRY);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}