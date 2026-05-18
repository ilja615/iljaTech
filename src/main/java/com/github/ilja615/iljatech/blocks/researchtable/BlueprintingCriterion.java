package com.github.ilja615.iljatech.blocks.researchtable;

import com.github.ilja615.iljatech.init.ModCriteria;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.ItemCriterion;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LocationCheckLootCondition;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.Optional;

public class BlueprintingCriterion extends AbstractCriterion<BlueprintingCriterion.Conditions> {
    public Codec<BlueprintingCriterion.Conditions> getConditionsCodec() {
        return BlueprintingCriterion.Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, ItemStack stack) {
        this.trigger(player, (conditions) -> {
            return conditions.test(stack);
        });
    }

    public static record Conditions(Optional<LootContextPredicate> player, ItemPredicate item) implements AbstractCriterion.Conditions {
        public static final Codec<BlueprintingCriterion.Conditions> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(
                    EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player")
                            .forGetter(BlueprintingCriterion.Conditions::player),
                    ItemPredicate.CODEC.fieldOf("item")
                            .forGetter(BlueprintingCriterion.Conditions::item))
                    .apply(instance, BlueprintingCriterion.Conditions::new);
        });

        public static AdvancementCriterion<Conditions> create(ItemPredicate itemPredicate) {
            return ModCriteria.BLUEPRINT_UNLOCK.create(new Conditions(Optional.empty(), itemPredicate));
        }

        public boolean test(ItemStack stack) {
            return item.test(stack);
        }

        public void validate(LootContextPredicateValidator validator) {
            AbstractCriterion.Conditions.super.validate(validator);
        }
    }
}
