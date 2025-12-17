package com.github.ilja615.iljatech.datagen.book.iljatech;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.datagen.book.iljatech.basics.*;
import com.github.ilja615.iljatech.init.ModItems;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import net.minecraft.util.Identifier;

public class BasicsCategory extends CategoryProvider {
    public static final String ID = "basics";

    public BasicsCategory(SingleBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        return new String[]{
                "_____________________",
                "_____________________",
                "_________m__s________",
                "_____________________",
                "______i__t_____c__r__",
                "_____________________",
                "_______________f__ş__",
                "_____________________",
                "_____________________"
        };
    }

    @Override
    protected void generateEntries() {
        var introEntry = this.add(new IntroEntry(this).generate('i'));
        var hammerSawEntry = this.add(new HammerSawEntry(this).generate('t'));
        hammerSawEntry.withParent(introEntry);

        var mechanicsEntry = this.add(new MechanicsEntry(this).generate('m'));
        mechanicsEntry.withParent(hammerSawEntry);

        var squeezerEntry = this.add(new SqueezerEntry(this).generate('s'));
        squeezerEntry.withParent(mechanicsEntry);

        var carpentryEntry = this.add(new CarpentryEntry(this).generate('c'));
        carpentryEntry.withParent(squeezerEntry);
        var researchEntry = this.add(new ResearchEntry(this).generate('r'));
        researchEntry.withParent(carpentryEntry);

        var flaxEntry = this.add(new FlaxEntry(this).generate('f'));
        var spinningEntry = this.add(new SpinningEntry(this).generate('ş'));
        spinningEntry.withParent(flaxEntry);
        spinningEntry.addParent(this.parent(carpentryEntry).withLineReversed(true));
    }

    @Override
    protected BookCategoryModel additionalSetup(BookCategoryModel category) {
        return super.additionalSetup(category);
//                .withEntryTextures(Identifier.of(IljaTech.MOD_ID, "textures/gui/entry_textures.png"))
//                .withBackground(Identifier.of(IljaTech.MOD_ID, "textures/gui/book_background.png"));
    }

    @Override
    protected String categoryName() {
        return "Early Fundamentals...";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(ModItems.IRON_HAMMER);
    }

    @Override
    public String categoryId() {
        return ID;
    }
}
