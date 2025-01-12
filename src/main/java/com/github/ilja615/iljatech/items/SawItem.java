package com.github.ilja615.iljatech.items;

import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SawItem extends Item implements FabricItem {
    public SawItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasRecipeRemainder() {
        return true;
    }

    @Override
    public ItemStack getRecipeRemainder(ItemStack stack) {
        if (stack.getDamage() < stack.getMaxDamage() - 1) {
            ItemStack moreDamaged = stack.copy();
            moreDamaged.setDamage(stack.getDamage() + 1);
            return moreDamaged;
        }
        return ItemStack. EMPTY;
    }
}
