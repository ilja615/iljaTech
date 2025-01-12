package com.github.ilja615.iljatech.items;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.init.ModEffects;
import com.github.ilja615.iljatech.init.ModItems;
import com.github.ilja615.iljatech.init.ModParticles;
import com.github.ilja615.iljatech.init.ModSounds;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static net.minecraft.item.Item.BASE_ATTACK_DAMAGE_MODIFIER_ID;
import static net.minecraft.item.Item.BASE_ATTACK_SPEED_MODIFIER_ID;

public class HammerItem extends Item implements FabricItem {

    private static final Map<Block, Block> BLOCK_CRACKING_MAP;
    static {
        Map<Block, Block> aMap = new HashMap<>();
        aMap.put(Blocks.STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS);
        aMap.put(Blocks.NETHER_BRICKS, Blocks.CRACKED_NETHER_BRICKS);
        aMap.put(Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);
        aMap.put(Blocks.DEEPSLATE_BRICKS, Blocks.CRACKED_DEEPSLATE_BRICKS);
        aMap.put(Blocks.DEEPSLATE_TILES, Blocks.CRACKED_DEEPSLATE_TILES);
        aMap.put(Blocks.STONE, Blocks.COBBLESTONE);
        aMap.put(Blocks.COBBLESTONE, Blocks.GRAVEL);
        BLOCK_CRACKING_MAP = Collections.unmodifiableMap(aMap);
    }

    public HammerItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity playerAttacker) {
            // I want to make it so it only works when the weapon cooldown was fully charged but idk how.
//            System.out.println(playerAttacker.getAttackCooldownProgress(0.0F));
            target.addStatusEffect(new StatusEffectInstance(ModEffects.STUNNED, 30));
        }
        stack.damage(1, attacker, EquipmentSlot.MAINHAND);
        if (!attacker.getWorld().isClient) {
            attacker.getWorld().playSound(null, target.getBlockPos(), ModSounds.HAMMER, SoundCategory.PLAYERS, 2.5f, 1.5f);
        }
        return super.postHit(stack, target, attacker);
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
//
//
//    @Override
//    public ItemStack getRecipeRemainder(ItemStack stack) {
//        if (stack.getDamage() < stack.getMaxDamage() - 1) {
//            ItemStack moreDamaged = stack.copy();
//            moreDamaged.setDamage(stack.getDamage() + 1);
//            return moreDamaged;
//        }
//        return ItemStack. EMPTY;
//    }

    public static AttributeModifiersComponent createAttributeModifiers(ToolMaterial material, float baseAttackDamage, float attackSpeed) {
        return AttributeModifiersComponent.builder()
            .add(
                EntityAttributes.GENERIC_ATTACK_DAMAGE,
                new EntityAttributeModifier(
                        BASE_ATTACK_DAMAGE_MODIFIER_ID, (double)((float)baseAttackDamage + material.getAttackDamage()), EntityAttributeModifier.Operation.ADD_VALUE
                ),
                AttributeModifierSlot.MAINHAND
            )
            .add(
                EntityAttributes.GENERIC_ATTACK_SPEED,
                new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, (double)attackSpeed, EntityAttributeModifier.Operation.ADD_VALUE),
                AttributeModifierSlot.MAINHAND
            )
            .build();
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        if (context.getHand() != Hand.MAIN_HAND || !context.getPlayer().getStackInHand(Hand.MAIN_HAND).isOf(ModItems.IRON_HAMMER))
        {
            return super.useOnBlock(context);
        }
        if (state.isIn(BlockTags.PLANKS)) {
            // It requires nails to make nailed planks
            if (context.getPlayer().getStackInHand(Hand.OFF_HAND) != null && context.getPlayer().getStackInHand(Hand.OFF_HAND).isOf(ModItems.IRON_NAILS)) {
                context.getPlayer().getStackInHand(Hand.OFF_HAND).decrementUnlessCreative(1, context.getPlayer());
                String key = state.getBlock().getTranslationKey();
                String name = key.substring(key.lastIndexOf(".") + 1);
                Identifier id = Identifier.of(IljaTech.MOD_ID, "nailed_" + name);

                world.setBlockState(pos, Registries.BLOCK.get(id).getDefaultState());
                context.getStack().damage(1, context.getPlayer(), EquipmentSlot.MAINHAND);
                if (!world.isClient) {
                    world.playSound(null, context.getBlockPos(), ModSounds.HAMMER, SoundCategory.PLAYERS, 1f, 1f);
                }
                return ActionResult.SUCCESS;
            }
        }
        if (BLOCK_CRACKING_MAP.containsKey(state.getBlock())) {
            world.setBlockState(pos, BLOCK_CRACKING_MAP.get(state.getBlock()).getDefaultState());
            context.getStack().damage(1, context.getPlayer(), EquipmentSlot.MAINHAND);
            if (!world.isClient) {
                world.playSound(null, context.getBlockPos(), ModSounds.HAMMER, SoundCategory.PLAYERS, 1f, 1f);
            }
            return ActionResult.SUCCESS;
        }
        return super.useOnBlock(context);
    }
}
