package com.github.ilja615.iljatech.datagen.book;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.datagen.book.iljatech.BasicsCategory;
import com.klikli_dev.modonomicon.api.datagen.ModonomiconLanguageProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookModel;
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
        var basicsCategory = this.add(new BasicsCategory(this).generate());
    }

    @Override
    protected String bookName() {
        return "Engineer's Journal (Unfinished)";
    }

    @Override
    protected String bookTooltip() {
        return "A collection of my notes, on\n how the world around me behaves\n and my designs of technology that leverage this...";

    }

    @Override
    protected BookModel additionalSetup(BookModel book) {
        return book.withModel(Identifier.of(IljaTech.MOD_ID, "book"))
                .withBookTextOffsetX(5)
                .withBookTextOffsetY(0)
                .withBookTextOffsetWidth(-5)
                .withAutoAddReadConditions(true)
                .withAllowOpenBooksWithInvalidLinks(true);
    }
}