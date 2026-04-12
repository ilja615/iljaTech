package com.github.ilja615.iljatech.init;

import com.github.ilja615.iljatech.IljaTech;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> CLINKER_BRICK_OR_ITEM_HATCH = createTag("clinker_brick_or_item_hatch");

        private static TagKey<Block> createTag(String name) {
            return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, name));
        }
    }

    public static class Items {
        private static TagKey<Item> createTag(String name) {
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, name));
        }
    }
}