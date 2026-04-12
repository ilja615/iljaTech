package com.github.ilja615.iljatech.items;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.init.ModItems;
import com.github.ilja615.iljatech.init.ModSounds;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.*;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import java.util.List;

public class SawItem extends Item implements FabricItem {
    public SawItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    public boolean hasCraftingRemainingItem() {
        return true;
    }

    @Override
    public ItemStack getRecipeRemainder(ItemStack stack) {
        if (stack.getDamageValue() < stack.getMaxDamage() - 1) {
            ItemStack moreDamaged = stack.copy();
            moreDamaged.setDamageValue(stack.getDamageValue() + 1);
            return moreDamaged;
        }
        return ItemStack. EMPTY;
    }

    public void onUseTick(Level world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (remainingUseTicks >= 0 && user instanceof Player playerEntity) {
            HitResult hitResult = this.getHitResult(playerEntity);
            if (hitResult instanceof BlockHitResult blockHitResult) {
                BlockPos blockPos = blockHitResult.getBlockPos();
                BlockState blockState = world.getBlockState(blockPos);
                if (hitResult.getType() == HitResult.Type.BLOCK && (blockState.is(BlockTags.PLANKS))) {
                    int i = this.getUseDuration(stack, user) - remainingUseTicks + 1;
                    boolean bl = i % 10 == 5;
                    if (bl) {
                        if (blockState.shouldSpawnTerrainParticles() && blockState.getRenderShape() != RenderShape.INVISIBLE) {
                            BlockParticleOption blockStateParticleEffect = new BlockParticleOption(ParticleTypes.BLOCK, blockState);
                            Direction direction = blockHitResult.getDirection();
                            Vec3 vec3d = blockHitResult.getLocation().relative(direction, 0.25d);
                            for(int k = 0; k < world.getRandom().nextInt(2,4); ++k) {
                                world.addParticle(blockStateParticleEffect, vec3d.x + world.random.nextDouble()*0.25 - 0.125, vec3d.y+ world.random.nextDouble()*0.25 - 0.125, vec3d.z+ world.random.nextDouble()*0.25 - 0.125,  world.random.nextDouble()*0.1 - 0.05, 0,  world.random.nextDouble()*0.1 - 0.05);
                            }
                        }

                        world.playSound(null, blockPos, ModSounds.SAW, SoundSource.PLAYERS, 1f, 1f);
                    }

                    if (!world.isClientSide() && remainingUseTicks == 1) {
                        String key = blockState.getBlock().getDescriptionId();
                        String name = key.substring(key.lastIndexOf(".") + 1, key.length() - 7); // Removre also the last 7 characters: "_planks"
                        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, "frame_" + name);

                        world.setBlockAndUpdate(blockPos, BuiltInRegistries.BLOCK.get(id).defaultBlockState());

                        EquipmentSlot equipmentSlot = stack.equals(playerEntity.getItemBySlot(EquipmentSlot.OFFHAND)) ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
                        stack.hurtAndBreak(1, user, equipmentSlot);
                    }

                    return;
                }
            }

            user.releaseUsingItem();
        } else {
            user.releaseUsingItem();
        }
    }

    private HitResult getHitResult(Player user) {
        return ProjectileUtil.getHitResultOnViewVector(user, (entity) -> {
            return !entity.isSpectator() && entity.isPickable();
        }, user.blockInteractionRange());
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return 30;
    }

    public InteractionResult useOn(UseOnContext context) {
        Player playerEntity = context.getPlayer();
        if (playerEntity != null && this.getHitResult(playerEntity).getType() == HitResult.Type.BLOCK) {
            playerEntity.startUsingItem(context.getHand());
        }

        return InteractionResult.CONSUME;
    }
}
