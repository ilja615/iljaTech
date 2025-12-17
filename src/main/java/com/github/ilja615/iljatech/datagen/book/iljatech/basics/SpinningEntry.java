package com.github.ilja615.iljatech.datagen.book.iljatech.basics;

import com.github.ilja615.iljatech.init.ModItems;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;

public class SpinningEntry extends EntryProvider {
    public static final String ID = "spinning";

    public SpinningEntry(CategoryProvider parent) {
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
                If I succeed in extracting the fibers from flax stems, I could then weave these to make cloth.
                """);

    }

    @Override
    protected String entryName() {
        return "Spinning Frame";
    }

    @Override
    protected String entryDescription() {
        return "On the Spinning of Flax into Fibers...";
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