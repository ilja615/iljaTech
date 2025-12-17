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

public class SqueezerEntry extends EntryProvider {
    public static final String ID = "squeezer";

    public SqueezerEntry(CategoryProvider parent) {
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
                With the hand crank finished, I wanted to see what else I could do with simple rotational force.
                While browsing my storage, I noticed a greasy smear on the bottom of the chest, seemingly coming from some seeds that had been crushed under the weight of the other items.
                Curious... If pressure alone can do that, then perhaps I can squeeze out more of this oily substance.
                """);

        this.page("page2", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1("iljatech:squeezer")
                .withText(this.context().pageText())
        );

        this.pageText("""
                A small machine should do the trick.
                A basin where I feed the seeds and collect the oil, and a movable plate which I will press down, by applying mechanical power with the crank.
                By releasing power it should come up again.
                """);
    }

    @Override
    protected String entryName() {
        return "Squeezer";
    }

    @Override
    protected String entryDescription() {
        return "On the Squeezing Press Device...";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(ModBlocks.SQUEEZER);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}