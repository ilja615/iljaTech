package com.github.ilja615.iljatech.datagen.book.iljatech;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.datagen.book.iljatech.basics.*;
import com.github.ilja615.iljatech.init.ModItems;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryParentModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import net.minecraft.resources.ResourceLocation;

public class BasicsCategory extends CategoryProvider {
    public static final String ID = "basics";

    public BasicsCategory(SingleBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        return new String[]{
                "_____________________",
                "____________ŕ________",
                "_________m___________",
                "____________s__c__r__",
                "___i__t______________",
                "_____________f_______",
                "_______________ş_____",
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
        mechanicsEntry.withParent(BookEntryParentModel.create( hammerSawEntry.getId()).withLineReversed(true));

        var squeezerEntry = this.add(new SqueezerEntry(this).generate('s'));
        squeezerEntry.withParent(BookEntryParentModel.create( mechanicsEntry.getId()).withLineReversed(false));
        var rollerEntry = this.add(new RollerEntry(this).generate('ŕ'));
        rollerEntry.withParent(mechanicsEntry);

        var carpentryEntry = this.add(new CarpentryEntry(this).generate('c'));
        carpentryEntry.withParent(squeezerEntry);
        carpentryEntry.withParent(BookEntryParentModel.create( rollerEntry.getId()).withLineReversed(false));
        var researchEntry = this.add(new ResearchEntry(this).generate('r'));
        researchEntry.withParent(carpentryEntry);

        var flaxEntry = this.add(new FlaxEntry(this).generate('f'));
        var spinningEntry = this.add(new SpinningEntry(this).generate('ş'));
        spinningEntry.withParent(carpentryEntry);
        spinningEntry.addParent(this.parent(flaxEntry).withLineReversed(false));
    }

    @Override
    protected BookCategoryModel additionalSetup(BookCategoryModel category) {
        return super.additionalSetup(category)
                .withEntryTextures(ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, "textures/gui/book/entry_textures.png"))
                .withBackground(ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, "textures/gui/book/book_background.png"));
    }

    @Override
    protected String categoryName() {
        return "Early Fundamentals";
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
