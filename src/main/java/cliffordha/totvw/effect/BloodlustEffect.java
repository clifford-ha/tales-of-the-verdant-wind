package cliffordha.totvw.effect;

import cliffordha.totvw.datagen.VWDamageTypes;
import cliffordha.totvw.registry.VWEffects;
import cliffordha.totvw.registry.VWEnchantments;
import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.VWIdentifiers;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.wolf.Wolf;

import java.util.List;

import static cliffordha.totvw.registry.VWEffects.*;
import static cliffordha.totvw.util.VWGlobalUtil.wolfEnchantmentLVL;

public class BloodlustEffect extends MobEffect {
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

        double atkMultiplier = 0.2 + (amplifier * 0.2);
        double speedMultiplier = 0.15 + (Math.min(amplifier, 2) * 0.15);

        addModifier(attributes, VWIdentifiers.EFFECT_BLOODLUST,
                Attributes.ATTACK_DAMAGE, atkMultiplier, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);

        addModifier(attributes, VWIdentifiers.EFFECT_BLOODLUST,
                Attributes.MOVEMENT_SPEED, speedMultiplier, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);

        if (entity instanceof Wolf wolf && wolf.isWearingBodyArmor() && wolfEnchantmentLVL(wolf, VWEnchantments.WOLF_EFFECT_MIGHT) > 0) {
            addModifier(attributes, VWIdentifiers.EFFECT_BLOODLUST,
                    Attributes.ARMOR, -(0.10 + (amplifier * 0.10)), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        } else {
            addModifier(attributes, VWIdentifiers.EFFECT_BLOODLUST,
                    Attributes.ARMOR, -(0.30 + (amplifier * 0.30)), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        }
    }

    @Override
    public boolean isBeneficial() {
        return false;
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
        if (entity.getHealth() > 3.0f) {
            float inflictDMG = 0.10f + (Math.min(amplifier, 0.3f) * 0.10f);
            if (serverLevel.getRandom().nextInt(60) == 0) {
                float damage = entity.getHealth() * inflictDMG;
                entity.hurtServer(serverLevel, VWDamageTypes.create(serverLevel, VWDamageTypes.BLOODLUST), damage);
            }
        } else {
            if (!entity.hasEffect(MobEffects.WEAKNESS)) {
                entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0));
            }
            entity.removeEffect(VWEffects.BLOODLUST);
        }
        return super.applyEffectTick(serverLevel, entity, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int tickCount, int amplification) {
        return true;
    }

    private void removeModifiers(LivingEntity entity) {
        removeAllModifiers(entity, VWIdentifiers.EFFECT_BLOODLUST,
                List.of(
                        Attributes.ATTACK_DAMAGE,
                        Attributes.MOVEMENT_SPEED,
                        Attributes.ARMOR
                )
        );
    }
}