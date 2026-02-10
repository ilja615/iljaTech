package com.github.ilja615.iljatech.datagen.book.iljatech.pyrometallurgy;

import com.github.ilja615.iljatech.init.ModItems;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;

public class SteelEntry extends EntryProvider {
    public static final String ID = "steel";

    public SteelEntry(CategoryProvider parent) {
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
                ...
                """);

    }

    @Override
    protected String entryName() {
        return "Steel";
    }

    @Override
    protected String entryDescription() {
        return "...";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return Pair.of(0, 1);
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(ModItems.STEEL_INGOT);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}