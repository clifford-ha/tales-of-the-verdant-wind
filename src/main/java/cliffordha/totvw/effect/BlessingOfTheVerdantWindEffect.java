package cliffordha.totvw.effect;

import cliffordha.totvw.registry.VWIdentifiers;
import cliffordha.totvw.registry.VWParticleEffects;
import cliffordha.totvw.registry.VWColors;

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
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;

import java.util.List;

import static cliffordha.totvw.registry.VWEffects.*;

public class BlessingOfTheVerdantWindEffect extends MobEffect {
    public BlessingOfTheVerdantWindEffect() {
        super(MobEffectCategory.BENEFICIAL, VWColors.VERDANT_WIND);
    }

    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
        onEffectAdded(entity, amplifier);
    }


    @Override
    public void onEffectAdded(LivingEntity entity, int amplifier) {
        if (entity instanceof Monster) return;
        if (entity instanceof Enemy) return;
        if (!entity.hasEffect(MobEffects.INVISIBILITY)) {
            VWParticleEffects.spawnBlessingParticlesEntity(entity, 1);
        }
        AttributeMap attributes = entity.getAttributes();

        double atkDamage = 2;
        double burnTime = 0.2 + (amplifier * 0.2);
        double health = Math.min(0.2 + (amplifier * 0.2), 0.5);

        addModifier(attributes, VWIdentifiers.EFFECT_BLESSING_OF_THE_VERDANT_WIND,
                Attributes.ATTACK_DAMAGE, atkDamage, AttributeModifier.Operation.ADD_VALUE);

        addModifier(attributes, VWIdentifiers.EFFECT_BLESSING_OF_THE_VERDANT_WIND,
                Attributes.BURNING_TIME, - burnTime, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

        addModifier(attributes, VWIdentifiers.EFFECT_BLESSING_OF_THE_VERDANT_WIND,
                Attributes.MAX_HEALTH, health, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);

        entity.heal(6.0f);
    }

    @Override
    public boolean isBeneficial() {
        return true;
    }

    @Override
    public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity entity, int amplifier) {
        float heal = (entity instanceof Monster) ? 0f : 3.0f + (amplifier * 2.0f);
        if (serverLevel.getRandom().nextInt(60) == 0) {
            entity.heal(heal);
            if (!entity.hasEffect(MobEffects.INVISIBILITY)) {
                VWParticleEffects.benedictionEnvironmentParticleEntity(entity);
            }
        }
        return super.applyEffectTick(serverLevel, entity, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int tickCount, int amplification) {
        return true;
    }

    @Override
    public void onMobRemoved(ServerLevel level, LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
        removeModifiers(entity);
    }

    @Override
    public void onEffectRemoved(MobEffectInstance effectInstance, LivingEntity entity) {
        removeModifiers(entity);
    }

    private void removeModifiers(LivingEntity entity) {
        removeAllModifiers(entity, VWIdentifiers.EFFECT_BLESSING_OF_THE_VERDANT_WIND,
                List.of(
                        Attributes.ATTACK_DAMAGE,
                        Attributes.BURNING_TIME,
                        Attributes.MAX_HEALTH
                )
        );
    }
}