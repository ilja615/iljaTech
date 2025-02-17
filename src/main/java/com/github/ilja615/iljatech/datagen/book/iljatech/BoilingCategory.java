package com.github.ilja615.iljatech.datagen.book.iljatech;

import com.github.ilja615.iljatech.datagen.book.iljatech.boiling.BoiledEggEntry;
import com.github.ilja615.iljatech.init.ModItems;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.datagen.book.demo.formatting.AdvancedFormattingEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.formatting.AlwaysLockedEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.formatting.BasicFormattingEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.formatting.LinkFormattingEntry;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;

public class BoilingCategory extends CategoryProvider {
    public static final String ID = "boiling";

    public BoilingCategory(SingleBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        return new String[]{
                "_____________________",
                "_____________________",
                "__________l__________",
                "_____________________",
                "_____________________"
        };
    }

    @Override
    protected void generateEntries() {
        var boiledEggEntry = this.add(new BoiledEggEntry(this).generate('l'));
    }

    @Override
    protected BookCategoryModel additionalSetup(BookCategoryModel category) {
        //When first opening the category, open the basic formatting entry automatically.
        return category.withEntryToOpen(this.modLoc(ID, BasicFormattingEntry.ID), true);
    }

    @Override
    protected String categoryName() {
        return "Boiling Category";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return null;
    }

    @Override
    public String categoryId() {
        return ID;
    }
}
