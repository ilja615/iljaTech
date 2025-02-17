package com.github.ilja615.iljatech.datagen.book.iljatech.boiling;

import com.github.ilja615.iljatech.init.ModItems;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;

public class BoiledEggEntry extends EntryProvider {
    public static final String ID = "egg";

    public BoiledEggEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("page1", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
        );

        this.pageTitle("BOILING Egg");
        // \s tells java to keep the spaces at the end of the line. Otherwise it will remove.
        // Due to markdown using multiple spaces to indicate a line break, we need to keep the spaces.
        this.pageText("""
                This is how you can boil an egg.   \s
                You should put it in a cauldron   \s
                and then heat it.
                """);

        this.page("page2", () -> BookTextPageModel.create()
                .withText(this.context().pageText())
        );
        this.pageText("""
                You can heat it with a   \s
                stoked fire or a {0}
                """,
                this.color("Firebox!", 0x55FF55)
        );
    }

    @Override
    protected String entryName() {
        return "Boiling Egg Entry";
    }

    @Override
    protected String entryDescription() {
        return "An entry showcasing boiled egg.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(ModItems.BOILED_EGG);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}