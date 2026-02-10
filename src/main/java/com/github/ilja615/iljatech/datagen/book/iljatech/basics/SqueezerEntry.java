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
                I noticed a greasy substance exists inside seeds.
                I am curious if by applying enough pressure, I can squeeze this out.
                A small machine should do the trick.
                It should consist of a basin where I put the seeds and let the oil accumulate, and a movable plate which I
                """);

        this.page("crafting1", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1("iljatech:squeezer")
                .withText(this.context().pageText())
        );

        this.pageText("""
                will press down, by applying mechanical power with the crank.
                By releasing power it should come up again.
                """);

        this.page("tutorial1", () -> BookImagePageModel.create()
                        .withTitle(this.context().pageTitle())
                        .withImages(this.modLoc("textures/gui/book/setup_squeezer.png")))
                .withBorder(false);
        this.pageTitle("Working Setup");


                this.page("page2", () -> BookTextPageModel.create()
                .withText(this.context().pageText())
        );

        this.pageTitle(this.entryName());
        this.pageText(
                """
                    When I want to press a lot of seeds, I should repeatedly power and de-power the squeezer in an {0} way to make it go up and down.
                    """,
                this.color("alternating", 0xFF5555)
        );
    }

    @Override
    protected String entryName() {
        return "Squeezer";
    }

    @Override
    protected String entryDescription() {
        return "Design for a Squeezing Press";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return Pair.of(0, 2);
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