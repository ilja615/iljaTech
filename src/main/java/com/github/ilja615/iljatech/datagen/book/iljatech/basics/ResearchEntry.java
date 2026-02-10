package com.github.ilja615.iljatech.datagen.book.iljatech.basics;

import com.github.ilja615.iljatech.init.ModBlocks;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;

public class ResearchEntry extends EntryProvider {
    public static final String ID = "research";

    public ResearchEntry(CategoryProvider parent) {
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
                My next efforts should focus towards making a desk.
                """);

    }

    @Override
    protected String entryName() {
        return "Research Table";
    }

    @Override
    protected String entryDescription() {
        return "Design for a Research Desk";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return Pair.of(0, 1);
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(ModBlocks.RESEARCH_TABLE);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}