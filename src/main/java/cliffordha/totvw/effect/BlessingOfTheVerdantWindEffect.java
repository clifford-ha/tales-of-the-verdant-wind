package cliffordha.totvw.effect;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.ModParticleEffects;
import cliffordha.totvw.registry.ModColors;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class BlessingOfTheVerdantWindEffect extends MobEffect {
    private static final Identifier ATK_DMG_ID = Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "blessing_attack_damage");
    private static final Identifier BURNING_TIME_REDUCTION_ID = Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "blessing_damage_reduction");
    private static final Identifier HEALTH_INCREASE_ID = Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "blessing_health_increase");

    public BlessingOfTheVerdantWindEffect() {
        super(MobEffectCategory.BENEFICIAL, ModColors.VERDANT_WIND);
    }

    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
        onEffectAdded(entity, amplifier);
    }


    @Override
    public void onEffectAdded(LivingEntity entity, int amplifier) {
        ModParticleEffects.spawnBlessingParticlesEntity(entity, 1);
        AttributeMap attributes = entity.getAttributes();

        double atkDamage = 0.15 + (amplifier * 0.15);
        double burnTime = 0.2 + (amplifier * 0.2);

        double maxHealthIncrease = Math.min(amplifier, 2);
        double health = 0.2 + (maxHealthIncrease * 0.2);
        if (attributes.hasAttribute(Attributes.ATTACK_DAMAGE)) {
            attributes.getInstance(Attributes.ATTACK_DAMAGE).addOrReplacePermanentModifier(
                    new AttributeModifier(
                            ATK_DMG_ID,
                            atkDamage,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
            );
        }
        if (attributes.hasAttribute(Attributes.BURNING_TIME)) {
            attributes.getInstance(Attributes.BURNING_TIME).addOrReplacePermanentModifier(
                    new AttributeModifier(
                            BURNING_TIME_REDUCTION_ID,
                            - burnTime,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
            );
        }
        if (attributes.hasAttribute(Attributes.MAX_HEALTH)) {
            attributes.getInstance(Attributes.MAX_HEALTH).addOrReplacePermanentModifier(
                    new AttributeModifier(
                            HEALTH_INCREASE_ID,
                            health,
                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            );
            entity.heal(6.0f);
        }
    }

    @Override
    public boolean isBeneficial() {
        return super.isBeneficial();
    }

    @Override
    public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity entity, int amplifier) {
        if (serverLevel.getRandom().nextInt(20 * 6) == 0) {
            entity.heal(3.0f + (amplifier * 2.0f));
            ModParticleEffects.benedictionEnvironmentParticleEntity(entity);
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
        AttributeMap attributes = entity.getAttributes();

        if (attributes.hasAttribute(Attributes.ATTACK_DAMAGE)) {
            attributes.getInstance(Attributes.ATTACK_DAMAGE).removeModifier(ATK_DMG_ID);
        }
        if (attributes.hasAttribute(Attributes.BURNING_TIME)) {
            attributes.getInstance(Attributes.BURNING_TIME).removeModifier(BURNING_TIME_REDUCTION_ID);
        }
        if (attributes.hasAttribute(Attributes.MAX_HEALTH)) {
            attributes.getInstance(Attributes.MAX_HEALTH).removeModifier(HEALTH_INCREASE_ID);
        }
    }
}