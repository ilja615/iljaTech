package com.github.ilja615.iljatech.init;

import com.github.ilja615.iljatech.IljaTech;
import com.klikli_dev.modonomicon.util.StreamCodecs;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;

public class ModDataAttachments {
    // Research
    public static final AttachmentType<Integer> RESEARCH_PNTS = AttachmentRegistry.create(
            ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, "research_pnts"),
            builder -> builder
                    .initializer(() -> 0) // The default value of the Attachment
                    .persistent(Codec.INT)
                    .copyOnDeath()
                    .syncWith(
                            ByteBufCodecs.INT,
                            AttachmentSyncPredicate.all() // Dictates who to send the data to.
                    )
    );


    public static void load() {}
}
