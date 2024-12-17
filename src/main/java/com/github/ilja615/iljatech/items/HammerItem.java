package com.github.ilja615.iljatech.items;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.init.ModEffects;
import com.github.ilja615.iljatech.init.ModItems;
import com.github.ilja615.iljatech.init.ModParticles;
import com.github.ilja615.iljatech.init.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HammerItem extends Item {

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

    public HammerItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }


    @Override
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.addStatusEffect(new StatusEffectInstance(ModEffects.STUNNED, 30));
        stack.damage(1, attacker, EquipmentSlot.MAINHAND);
        if (!attacker.getWorld().isClient) {
            attacker.getWorld().playSound(null, target.getBlockPos(), ModSounds.HAMMER, SoundCategory.PLAYERS, 2.5f, 1.5f);
        }
        super.postDamageEntity(stack, target, attacker);
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
