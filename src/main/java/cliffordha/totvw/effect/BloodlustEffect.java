package cliffordha.totvw.effect;

import cliffordha.totvw.datagen.VWDamageTypes;
import cliffordha.totvw.entity.skills.RunestoneEffects;
import cliffordha.totvw.registry.VWEffects;
import cliffordha.totvw.registry.VWEnchantments;
import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.VWIdentifiers;

import cliffordha.totvw.registry.attachments.Runestone;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.List;

import static cliffordha.totvw.registry.VWEffects.*;
import static cliffordha.totvw.registry.VWEnchantments.entityEnchantmentLVL;
import static cliffordha.totvw.util.VWUtil.*;

public class BloodlustEffect extends MobEffect {
    private final Identifier ID = VWIdentifiers.EFFECT_BLOODLUST;
    private final Identifier ID_ADDITIONAL = VWIdentifiers.EFFECT_BLOODLUST_ADDITIONAL;

    public BloodlustEffect() {
        super(MobEffectCategory.HARMFUL, VWColors.BLOODLUST_EFFECT);
    }

    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
        onEffectAdded(entity, amplifier);
    }

    @Override
    public void onEffectAdded(LivingEntity entity, int amplifier) {
        AttributeMap attributes = entity.getAttributes();

        double baseAtkAdditional = 2;
        double atkMultiplier = Math.min(0.2 + (amplifier * 0.2), 1.2);
        double speedMultiplier = Math.min(0.15 + (amplifier * 0.15), 0.45);
        double armorReduction = (entity instanceof Wolf wolf && VWEnchantments.getMight(wolf) > 0) ? -(0.10 + (amplifier * 0.10)) : -(0.30 + (amplifier * 0.30));

        if (amplifier > 1) {
            addModifier(attributes, ID_ADDITIONAL,
                    Attributes.ATTACK_DAMAGE, baseAtkAdditional, AttributeModifier.Operation.ADD_VALUE);
        }

        addModifier(attributes, ID,
                Attributes.ATTACK_DAMAGE, atkMultiplier, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

        addModifier(attributes, ID,
                Attributes.MOVEMENT_SPEED, speedMultiplier, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);

        addModifier(attributes, ID,
                Attributes.ARMOR, armorReduction, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    @Override
    public void onMobRemoved(ServerLevel level, LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
        removeModifiers(entity);
    }

    @Override
    public void onEffectRemoved(MobEffectInstance effectInstance, LivingEntity entity) {
        removeModifiers(entity);
    }

    @Override
    public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity entity, int amplifier) {
        getDamage(entity, serverLevel, amplifier);
        return super.applyEffectTick(serverLevel, entity, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int tickCount, int amplification) {
        return true;
    }

    private void getDamage(LivingEntity entity, ServerLevel serverLevel, int amplifier) {
        if (entity.getHealth() > 4.0f) {
            boolean HAS_STRONG_ENCHANTMENT = entityEnchantmentLVL(entity, Enchantments.PROTECTION) > 3;

            float inflictDMG = Math.min(0.10f + (amplifier * 0.10f), 0.30f);
            if (serverLevel.getGameTime() % 60 == 0) {
                float damage = (entity.getHealth() * inflictDMG) + 1;
                if (HAS_STRONG_ENCHANTMENT) {
                    damage = 1;
                }
                if (damage >= entity.getHealth()) {
                    convertEffect(entity, amplifier);
                } else {
                    if (entity instanceof Wolf wolf) {
                        checkWolf(wolf, serverLevel, damage);
                    } else if (entity instanceof Player player) {
                        checkPlayer(player, serverLevel, damage);
                    } else {
                        applyFinalDamage(entity, serverLevel, damage);
                    }
                }
            }
        } else {
            entity.removeEffect(VWEffects.BLOODLUST);
        }
    }
    private void checkWolf(Wolf wolf, ServerLevel level, float damage) {
        Runestone runestone = wolf.getAttachedOrElse(RunestoneEffects.RUNESTONE_TYPE, Runestone.EMPTY);
        boolean DMG_CHANCE = level.getRandom().nextFloat() < 0.60f;
        if (runestone == Runestone.HAVOC && wolf.getTarget() != null && wolf.getTarget() instanceof LivingEntity target) {
            if (!DMG_CHANCE) return;
            applyFinalDamage(target, level, damage);
        } else {
            boolean ACTIVE_BENEDICTION = VWEnchantments.getBenediction(wolf);
            boolean ACTIVE_MIGHT = VWEnchantments.getMight(wolf) > 0;
            if (ACTIVE_MIGHT && ACTIVE_BENEDICTION) return;
            if (ACTIVE_BENEDICTION && !DMG_CHANCE) return;
            applyFinalDamage(wolf, level, damage);
        }
    }
    private void checkPlayer(Player player, ServerLevel level, float damage) {
        boolean DMG_CHANCE = level.getRandom().nextFloat() < 0.60f;
        boolean ACTIVE_BENEDICTION = entityEnchantmentLVL(player, EquipmentSlot.CHEST, VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS) > 0;
        if (ACTIVE_BENEDICTION && DMG_CHANCE) return;
        applyFinalDamage(player, level, damage);
    }
    private void applyFinalDamage(LivingEntity entity, ServerLevel level, float damage) {
        entity.hurtServer(level, VWDamageTypes.bloodlust(level), damage);
    }
    private void convertEffect(LivingEntity entity, int amp) {
        if (!entity.hasEffect(MobEffects.WEAKNESS)) {
            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 240 * (1 + amp), amp));
        }
        entity.removeEffect(VWEffects.BLOODLUST);
    }
    private void removeModifiers(LivingEntity entity) {
        removeAllModifiers(entity, ID,
                List.of(
                        Attributes.ATTACK_DAMAGE,
                        Attributes.MOVEMENT_SPEED,
                        Attributes.ARMOR
                )
        );
        removeAllModifiers(entity, ID_ADDITIONAL, List.of(Attributes.ATTACK_DAMAGE));
    }
}