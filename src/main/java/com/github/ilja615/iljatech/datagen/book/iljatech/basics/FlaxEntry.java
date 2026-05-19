package com.github.ilja615.iljatech.datagen.book.iljatech.basics;

import com.github.ilja615.iljatech.init.ModBlocks;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSpotlightPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class FlaxEntry extends EntryProvider {
    public static final String ID = "flax";

    public FlaxEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("page1", () -> BookSpotlightPageModel.create()
                .withTitle(this.context().pageTitle())
                .withItem(Ingredient.of(ModBlocks.FLAX))
                .withText(this.context().pageText())
        );

        this.pageTitle(this.entryName());
        this.pageText("""
                When I found this plant and cut it down, its stems seem to be very tough.
                Upon a closer look, I can see how the stem is composed of individual fibers.
                The strength of these fibers must be what made the stems so tough.
                """);

        this.page("crafting1", () -> BookTextPageModel.create()
                .withText(this.context().pageText())
        );

        this.pageText("""
                Also, its blue flowers will be a good source for the {0} that I need for my blueprints...
                """,
                this.itemLink(Items.BLUE_DYE));
    }

    @Override
    protected String entryName() {
        return "Flax";
    }

    @Override
    protected String entryDescription() {
        return "On the Crop Flax...";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(ModBlocks.FLAX);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}