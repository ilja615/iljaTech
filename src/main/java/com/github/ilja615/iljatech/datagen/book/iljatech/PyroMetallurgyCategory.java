package com.github.ilja615.iljatech.datagen.book.iljatech;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.datagen.book.iljatech.basics.*;
import com.github.ilja615.iljatech.datagen.book.iljatech.pyrometallurgy.*;
import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.init.ModItems;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryParentModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import net.minecraft.util.Identifier;

public class PyroMetallurgyCategory extends CategoryProvider {
    public static final String ID = "pyrometallurgy";

    public PyroMetallurgyCategory(SingleBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        return new String[]{
                "______________________________",
                "______________________________",
                "__________________t___________",
                "___________b__ƃ_____ḅ__ś______",
                "______________________________",
                "___________c__ḃ__f________n___",
                "______________________________",
                "______________p_____s__ṕ______",
                "__________________l___________",
                "______________________________",
                "______________________________"
        };
    }

    @Override
    protected void generateEntries() {
        var fireClayEntry = this.add(new FireClayEntry(this).generate('c'));

        var bellowsEntry = this.add(new BellowsEntry(this).generate('b'));

        var fireBoxEntry = this.add(new FireBoxEntry(this).generate('ḃ'));
        fireBoxEntry.withParent(fireClayEntry);

        var boilingEntry = this.add(new BoilingEntry(this).generate('ƃ'));
        boilingEntry.withParent(bellowsEntry).withParent(fireBoxEntry);

        var foundryEntry = this.add(new FoundryEntry(this).generate('f'));
        foundryEntry.withParent(fireBoxEntry);

        var tinEntry = this.add(new TinEntry(this).generate('t'));

        var bronzeEntry = this.add(new BronzeEntry(this).generate('ḅ'));
        bronzeEntry.withParent(BookEntryParentModel.create( foundryEntry.getId()).withLineReversed(true))
                .withParent(BookEntryParentModel.create( tinEntry.getId()).withLineReversed(true));

        var pyrolysisEntry = this.add(new PyrolysisEntry(this).generate('p'));
        pyrolysisEntry.withParent(fireBoxEntry);

        var limeStoneEntry = this.add(new LimestoneEntry(this).generate('l'));

        var steelEntry = this.add(new SteelEntry(this).generate('s'));
        steelEntry.withParent(pyrolysisEntry)
                .withParent(BookEntryParentModel.create( limeStoneEntry.getId()).withLineReversed(true))
                .withParent(BookEntryParentModel.create( foundryEntry.getId()).withLineReversed(true));

        var pulverizerMillEntry = this.add(new PulverizerMillEntry(this).generate('ṕ'));
        pulverizerMillEntry.withParent(steelEntry);
        var sifterEntry = this.add(new SifterEntry(this).generate('ś'));
        sifterEntry.withParent(bronzeEntry);

        var nickelEntry = this.add(new NickelEntry(this).generate('n'));
        nickelEntry.withParent(pulverizerMillEntry)
                .withParent(BookEntryParentModel.create( sifterEntry.getId()).withLineReversed(true));
    }

    @Override
    protected BookCategoryModel additionalSetup(BookCategoryModel category) {
        return super.additionalSetup(category)
                .withEntryTextures(Identifier.of(IljaTech.MOD_ID, "textures/gui/book/entry_textures.png"))
                .withBackground(Identifier.of(IljaTech.MOD_ID, "textures/gui/book/book_background.png"));
    }

    @Override
    protected String categoryName() {
        return "PyroMetallurgy";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(ModBlocks.FIREBOX);
    }

    @Override
    public String categoryId() {
        return ID;
    }
}
