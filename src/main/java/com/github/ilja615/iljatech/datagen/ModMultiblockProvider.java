package com.github.ilja615.iljatech.datagen;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.init.ModBlocks;
import com.klikli_dev.modonomicon.api.datagen.MultiblockProvider;
import net.minecraft.data.DataOutput;

public class ModMultiblockProvider extends MultiblockProvider {
    public ModMultiblockProvider(DataOutput packOutput) {
        super(packOutput, IljaTech.MOD_ID);
    }

    @Override
    public void buildMultiblocks() {
        this.add(this.modLoc("placement/foundry"),
                new DenseMultiblockBuilder()
                        .layer("###", "#0#", "###")
                        .layer("###", "###", "#F#")
                        .layer("###", "###", "###")
                        .block('#', () -> ModBlocks.FIRE_BRICKS)
                        .block('0', () -> ModBlocks.FIRE_BRICKS)
                        .block('F', () -> ModBlocks.FOUNDRY)
                        .build()
        );
    }
}
