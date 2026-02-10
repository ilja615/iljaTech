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

public class RollerEntry extends EntryProvider {
    public static final String ID = "roller";

    public RollerEntry(CategoryProvider parent) {
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
                For more advanced woodworking, I will need small metal nails that I can drive into the planks with my hammer.
                I should make a machine capable of stretching metal ingots into rods, so that I can craft nails out of these.
                A design where the items get rolled between two cylinders would do the trick for sure.
                """);

        this.page("page2", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1("iljatech:iron_cylinder")
                .withRecipeId2("iljatech:roller_mill")
        );

        this.page("tutorial1", () -> BookImagePageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withImages(this.modLoc("textures/gui/book/setup_roller_mill.png")))
                .withBorder(false);
        this.pageTitle("Working Setup");
        this.pageText(
                """
                    I can rotate a {0} attached to the side of the roller mill to work it.
                    """,
                this.itemLink(ModBlocks.CRANK)
        );

        this.page("crafting1", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1("iljatech:iron_nails")
                .withText(this.context().pageText())
        );

        this.pageText("""
                Perfect, with these in one hand and my hammer in the other, I will be able to make nailed wooden boards.
                """);

        this.page("tutorial2", () -> BookImagePageModel.create()
                        .withTitle(this.context().pageTitle())
                        .withImages(this.modLoc("textures/gui/book/interaction_hammer_nailed_wooden_board.png")))
                .withBorder(false);
        this.pageTitle("Basic Woodworking");
    }

    @Override
    protected String entryName() {
        return "Roller";
    }

    @Override
    protected String entryDescription() {
        return "Design for a Roller Mill";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return Pair.of(0, 2);
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(ModBlocks.ROLLER_MILL);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}