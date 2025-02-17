package com.github.ilja615.iljatech.datagen.book;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.datagen.book.iljatech.BoilingCategory;
import com.klikli_dev.modonomicon.api.datagen.ModonomiconLanguageProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookCommandModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookModel;
import com.klikli_dev.modonomicon.datagen.book.demo.ConditionalCategory;
import com.klikli_dev.modonomicon.datagen.book.demo.FeaturesCategory;
import com.klikli_dev.modonomicon.datagen.book.demo.FormattingCategory;
import com.klikli_dev.modonomicon.datagen.book.demo.IndexModeCategory;
import com.klikli_dev.modonomicon.datagen.book.demo.features.ConditionRootEntry;
import net.minecraft.util.Identifier;

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
        var boilingCategory = this.add(new BoilingCategory(this).generate());
    }

    @Override
    protected String bookName() {
        return "IljaTech Book";
    }

    @Override
    protected String bookTooltip() {
        return "A book for IljaTech research";
    }

    @Override
    protected BookModel additionalSetup(BookModel book) {
        return book.withModel(Identifier.of(IljaTech.MOD_ID, "book"))
                .withBookTextOffsetX(5)
                .withBookTextOffsetY(0)
                .withBookTextOffsetWidth(-5)
                .withAllowOpenBooksWithInvalidLinks(true);
    }
}