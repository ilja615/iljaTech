package com.github.ilja615.iljatech.effects;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.init.ModParticles;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class StunnedEffect extends MobEffect {
    public StunnedEffect() {
        super(MobEffectCategory.HARMFUL, 0xfbd132);
        addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, "stunned"), -50.0F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        addAttributeModifier(Attributes.JUMP_STRENGTH, ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, "stunned"), -50.0F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.isAlwaysTicking()) {
            entity.setSpeed(0.0f);
        }
        if (entity.level().random.nextFloat() < 0.2f && !entity.level().isClientSide) {
            ((ServerLevel) entity.level()).sendParticles(ModParticles.STAR, entity.getX(), entity.getY() + entity.getBbHeight() + 0.5d, entity.getZ() - 0.25d, 1, 0.0D, 0.0D, 0.0D, 1.0D);
        }

        return super.applyEffectTick(entity, amplifier);
    }
}