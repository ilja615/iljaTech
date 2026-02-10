package com.github.ilja615.iljatech.datagen.book.iljatech.pyrometallurgy;

import com.github.ilja615.iljatech.init.ModItems;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;

public class TinEntry extends EntryProvider {
    public static final String ID = "tin";

    public TinEntry(CategoryProvider parent) {
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
                I dug up some dark mineral, which appears to be rather common.
                Purifying the ore to its metallic form should be easy enough, as it needs only a little heat before it melts.
                The material is soft and easily pliable compared to other metals.
                I am sure this will be useful, but I don't know yet for what.
                """);

    }

    @Override
    protected String entryName() {
        return "Tin";
    }

    @Override
    protected String entryDescription() {
        return "On the soft and pliable metal...";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(ModItems.RAW_TIN_ORE);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}