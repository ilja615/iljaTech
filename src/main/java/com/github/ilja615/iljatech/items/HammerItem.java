package com.github.ilja615.iljatech.items;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.init.ModEffects;
import com.github.ilja615.iljatech.init.ModItems;
import com.github.ilja615.iljatech.init.ModParticles;
import com.github.ilja615.iljatech.init.ModSounds;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.item.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static net.minecraft.world.item.Item.BASE_ATTACK_DAMAGE_ID;
import static net.minecraft.world.item.Item.BASE_ATTACK_SPEED_ID;

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

    public HammerItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player playerAttacker) {
            // I want to make it so it only works when the weapon cooldown was fully charged but idk how.
//            System.out.println(playerAttacker.getAttackCooldownProgress(0.0F));
            target.addEffect(new MobEffectInstance(ModEffects.STUNNED, 30));
        }
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
        if (!attacker.level().isClientSide) {
            attacker.level().playSound(null, target.blockPosition(), ModSounds.HAMMER, SoundSource.PLAYERS, 2.5f, 1.5f);
        }
        return super.hurtEnemy(stack, target, attacker);
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

    public static ItemAttributeModifiers createAttributeModifiers(Tier material, float baseAttackDamage, float attackSpeed) {
        return ItemAttributeModifiers.builder()
            .add(
                Attributes.ATTACK_DAMAGE,
                new AttributeModifier(
                        BASE_ATTACK_DAMAGE_ID, (double)((float)baseAttackDamage + material.getAttackDamageBonus()), AttributeModifier.Operation.ADD_VALUE
                ),
                EquipmentSlotGroup.MAINHAND
            )
            .add(
                Attributes.ATTACK_SPEED,
                new AttributeModifier(BASE_ATTACK_SPEED_ID, (double)attackSpeed, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND
            )
            .build();
    }

    public void onUseTick(Level world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (remainingUseTicks >= 0 && user instanceof Player playerEntity) {
            HitResult hitResult = this.getHitResult(playerEntity);
            if (hitResult instanceof BlockHitResult blockHitResult) {
                BlockPos blockPos = blockHitResult.getBlockPos();
                BlockState blockState = world.getBlockState(blockPos);
                if (hitResult.getType() == HitResult.Type.BLOCK && (blockState.is(BlockTags.PLANKS))) {
                    if (playerEntity.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof HammerItem && playerEntity.getItemInHand(InteractionHand.OFF_HAND) != null && playerEntity.getItemInHand(InteractionHand.OFF_HAND).is(ModItems.IRON_NAILS)) {
                        int i = this.getUseDuration(stack, user) - remainingUseTicks + 1;
                        boolean bl = i % 10 == 5;
                        if (bl) {
                            if (blockState.shouldSpawnTerrainParticles() && blockState.getRenderShape() != RenderShape.INVISIBLE) {
                                BlockParticleOption blockStateParticleEffect = new BlockParticleOption(ParticleTypes.BLOCK, blockState);
                                Direction direction = blockHitResult.getDirection();
                                Vec3 vec3d = blockHitResult.getLocation().relative(direction, 0.25d);
                                for (int k = 0; k < world.getRandom().nextInt(2, 4); ++k) {
                                    world.addParticle(blockStateParticleEffect, vec3d.x + world.random.nextDouble() * 0.25 - 0.125, vec3d.y + world.random.nextDouble() * 0.25 - 0.125, vec3d.z + world.random.nextDouble() * 0.25 - 0.125, world.random.nextDouble() * 0.1 - 0.05, 0, world.random.nextDouble() * 0.1 - 0.05);
                                }
                            }

                            world.playSound(null, blockPos, ModSounds.HAMMER, SoundSource.PLAYERS, 1f, 1f);
                        }

                        if (!world.isClientSide() && remainingUseTicks == 1) {
                            playerEntity.getItemInHand(InteractionHand.OFF_HAND).consume(1, playerEntity);

                            String key = blockState.getBlock().getDescriptionId();
                            String name = key.substring(key.lastIndexOf(".") + 1);
                            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, "nailed_" + name);

                            world.setBlockAndUpdate(blockPos, BuiltInRegistries.BLOCK.get(id).defaultBlockState());

                            stack.hurtAndBreak(1, playerEntity, EquipmentSlot.MAINHAND);
                        }
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
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);
        if (BLOCK_CRACKING_MAP.containsKey(state.getBlock())) {
            world.setBlockAndUpdate(pos, BLOCK_CRACKING_MAP.get(state.getBlock()).defaultBlockState());
            context.getItemInHand().hurtAndBreak(1, context.getPlayer(), EquipmentSlot.MAINHAND);
            if (!world.isClientSide) {
                world.playSound(null, context.getClickedPos(), ModSounds.HAMMER, SoundSource.PLAYERS, 1f, 1f);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }
}
