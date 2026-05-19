package com.github.ilja615.iljatech.blocks.researchtable;

import com.github.ilja615.iljatech.init.ModCriteria;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.CriterionValidator;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class BlueprintingCriterion extends SimpleCriterionTrigger<BlueprintingCriterion.Conditions> {
    public Codec<BlueprintingCriterion.Conditions> codec() {
        return BlueprintingCriterion.Conditions.CODEC;
    }

    public void trigger(ServerPlayer player, ItemStack stack) {
        this.trigger(player, (conditions) -> {
            return conditions.test(stack);
        });
    }

    public static record Conditions(Optional<ContextAwarePredicate> player, ItemPredicate item) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<BlueprintingCriterion.Conditions> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(
                    EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player")
                            .forGetter(BlueprintingCriterion.Conditions::player),
                    ItemPredicate.CODEC.fieldOf("item")
                            .forGetter(BlueprintingCriterion.Conditions::item))
                    .apply(instance, BlueprintingCriterion.Conditions::new);
        });

        public static Criterion<Conditions> create(ItemPredicate itemPredicate) {
            return ModCriteria.BLUEPRINT_UNLOCK.createCriterion(new Conditions(Optional.empty(), itemPredicate));
        }

        public boolean test(ItemStack stack) {
            return item.test(stack);
        }

        public void validate(CriterionValidator validator) {
            SimpleCriterionTrigger.SimpleInstance.super.validate(validator);
        }
    }
}
