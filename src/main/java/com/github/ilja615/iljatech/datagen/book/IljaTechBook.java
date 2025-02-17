package com.github.ilja615.iljatech.datagen.book;

import com.klikli_dev.modonomicon.api.datagen.ModonomiconLanguageProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookCommandModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookModel;
import com.klikli_dev.modonomicon.datagen.book.demo.ConditionalCategory;
import com.klikli_dev.modonomicon.datagen.book.demo.FeaturesCategory;
import com.klikli_dev.modonomicon.datagen.book.demo.FormattingCategory;
import com.klikli_dev.modonomicon.datagen.book.demo.IndexModeCategory;
import com.klikli_dev.modonomicon.datagen.book.demo.features.ConditionRootEntry;

public class IljaTechBook extends SingleBookSubProvider {

    public static final String ID = "iljatech";

    public IljaTechBook(String modid, ModonomiconLanguageProvider lang) {
        super(ID, modid, lang);
    }

    @Override
    protected void registerDefaultMacros() {
        //currently no macros
    }

    @Override
    protected void generateCategories() {
        //for the two big categories we use the category provider
        var featuresCategory = this.add(new FeaturesCategory(this).generate());
        var formattingCategory = this.add(new FormattingCategory(this).generate());

        var conditionalCategory = this.add(new ConditionalCategory(this).generate())
                .withCondition(this.condition().entryRead(this.modLoc(FeaturesCategory.ID, ConditionRootEntry.ID)));

        var indexModeCategory = this.add(new IndexModeCategory(this).generate());
    }

    @Override
    protected String bookName() {
        return "IljaTech Book";
    }

    @Override
    protected String bookTooltip() {
        return "A book for IljaTech research";
    }
}