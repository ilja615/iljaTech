package com.github.ilja615.iljatech.effects;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.init.ModParticles;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class StunnedEffect extends StatusEffect {
    public StunnedEffect() {
        super(StatusEffectCategory.HARMFUL, 0xfbd132);
        addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, Identifier.of(IljaTech.MOD_ID, "stunned"), -50.0F, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        addAttributeModifier(EntityAttributes.GENERIC_JUMP_STRENGTH, Identifier.of(IljaTech.MOD_ID, "stunned"), -50.0F, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (!entity.isPlayer()) {
            entity.setMovementSpeed(0.0f);
        }
        if (entity.getWorld().random.nextFloat() < 0.2f && !entity.getWorld().isClient) {
            ((ServerWorld) entity.getWorld()).spawnParticles(ModParticles.STAR, entity.getX(), entity.getY() + entity.getHeight() + 0.5d, entity.getZ() - 0.25d, 1, 0.0D, 0.0D, 0.0D, 1.0D);
        }

        return super.applyUpdateEffect(entity, amplifier);
    }
}