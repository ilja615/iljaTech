package com.github.ilja615.iljatech.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SawItem extends Item {
    public SawItem(Settings settings) {
        super(settings);
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
