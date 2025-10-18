package com.github.ilja615.iljatech.items;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.init.ModItems;
import com.github.ilja615.iljatech.init.ModSounds;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BrushableBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BrushableBlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

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

    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (remainingUseTicks >= 0 && user instanceof PlayerEntity playerEntity) {
            HitResult hitResult = this.getHitResult(playerEntity);
            if (hitResult instanceof BlockHitResult blockHitResult) {
                BlockPos blockPos = blockHitResult.getBlockPos();
                BlockState blockState = world.getBlockState(blockPos);
                if (hitResult.getType() == HitResult.Type.BLOCK && (blockState.isIn(BlockTags.PLANKS))) {
                    int i = this.getMaxUseTime(stack, user) - remainingUseTicks + 1;
                    boolean bl = i % 10 == 5;
                    if (bl) {
                        if (blockState.hasBlockBreakParticles() && blockState.getRenderType() != BlockRenderType.INVISIBLE) {
                            BlockStateParticleEffect blockStateParticleEffect = new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState);
                            Direction direction = blockHitResult.getSide();
                            Vec3d vec3d = blockHitResult.getPos().offset(direction, 0.25d);
                            for(int k = 0; k < world.getRandom().nextBetweenExclusive(2,4); ++k) {
                                world.addParticle(blockStateParticleEffect, vec3d.x + world.random.nextDouble()*0.25 - 0.125, vec3d.y+ world.random.nextDouble()*0.25 - 0.125, vec3d.z+ world.random.nextDouble()*0.25 - 0.125,  world.random.nextDouble()*0.1 - 0.05, 0,  world.random.nextDouble()*0.1 - 0.05);
                            }
                        }

                        world.playSound(null, blockPos, ModSounds.SAW, SoundCategory.PLAYERS, 1f, 1f);
                    }

                    if (!world.isClient() && remainingUseTicks == 1) {
                        String key = blockState.getBlock().getTranslationKey();
                        String name = key.substring(key.lastIndexOf(".") + 1, key.length() - 7); // Removre also the last 7 characters: "_planks"
                        Identifier id = Identifier.of(IljaTech.MOD_ID, "frame_" + name);

                        world.setBlockState(blockPos, Registries.BLOCK.get(id).getDefaultState());

                        EquipmentSlot equipmentSlot = stack.equals(playerEntity.getEquippedStack(EquipmentSlot.OFFHAND)) ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
                        stack.damage(1, user, equipmentSlot);
                    }

                    return;
                }
            }

            user.stopUsingItem();
        } else {
            user.stopUsingItem();
        }
    }

    private HitResult getHitResult(PlayerEntity user) {
        return ProjectileUtil.getCollision(user, (entity) -> {
            return !entity.isSpectator() && entity.canHit();
        }, user.getBlockInteractionRange());
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 30;
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity playerEntity = context.getPlayer();
        if (playerEntity != null && this.getHitResult(playerEntity).getType() == HitResult.Type.BLOCK) {
            playerEntity.setCurrentHand(context.getHand());
        }

        return ActionResult.CONSUME;
    }
}
