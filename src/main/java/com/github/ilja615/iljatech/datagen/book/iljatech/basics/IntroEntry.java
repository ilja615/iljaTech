package com.github.ilja615.iljatech.datagen.book.iljatech.basics;

import com.github.ilja615.iljatech.init.ModItems;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;

public class IntroEntry  extends EntryProvider {
    public static final String ID = "intro";

    public IntroEntry(CategoryProvider parent) {
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
                The world around me is fascinating, and yet full of unanswered questions...   \s
                Why does metal soften at the kiss of a flame?   \s
                Why does wood bend one way but snap in another?   \s
                By studying why different materials and forms of energy behave and interact as they do, I will be able to leverage them.
                """);

        this.page("page2", () -> BookTextPageModel.create()
                .withText(this.context().pageText())
        );

        this.pageText("""
                So, I decided to put together this book, in which I can keep writing my notes as I learn, in one collected place.
                Perhaps in doing so, I will also gain clarity on the path forward; everything I still must learn.
                """);
    }

    @Override
    protected String entryName() {
        return "Research Notes";
    }

    @Override
    protected String entryDescription() {
        return "On this notebook...";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(ModItems.BOOK);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}