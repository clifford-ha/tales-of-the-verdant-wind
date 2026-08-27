package cliffordha.totvw.effect;

import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.VWEnchantments;
import cliffordha.totvw.registry.VWIdentifiers;

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
import net.minecraft.world.entity.animal.wolf.Wolf;

import java.util.List;

import static cliffordha.totvw.registry.VWEffects.*;
import static cliffordha.totvw.util.VWUtil.*;

public class AmplifiedMightEffect extends MobEffect {
    private final Identifier ID = VWIdentifiers.EFFECT_AMPLIFIED_MIGHT;
    private final AttributeModifier.Operation ADD_VALUE = AttributeModifier.Operation.ADD_VALUE;
    private final AttributeModifier.Operation ADD_MULTIPLIED_TOTAL = AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;

    public AmplifiedMightEffect() {
        super(MobEffectCategory.BENEFICIAL, VWColors.MIGHT_EFFECT);
    }
    
    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
        onEffectAdded(entity, amplifier);
    }

    @Override
    public void onEffectAdded(LivingEntity entity, int amplifier) {
        AttributeMap attributes = entity.getAttributes();
        if (entity instanceof Wolf wolf && wolfEnchantmentLVL(wolf, VWEnchantments.WOLF_EFFECT_MIGHT) > 0) {
            addModifier(attributes, ID, Attributes.ARMOR,  4 + (2 * amplifier), ADD_VALUE);

            addModifier(attributes, ID, Attributes.JUMP_STRENGTH, 0.05 + (amplifier * 0.05), ADD_VALUE);

            addModifier(attributes, ID, Attributes.FALL_DAMAGE_MULTIPLIER, -(0.20 + (amplifier * 0.20)), ADD_VALUE);

            if (amplifier > 1) {
                addModifier(attributes, ID, Attributes.ENTITY_INTERACTION_RANGE, 2, ADD_VALUE);
            }
        }

        double armorToughness = 0.3 + (amplifier * 0.2);

        addModifier(attributes, ID, Attributes.ARMOR_TOUGHNESS, armorToughness + 1, ADD_MULTIPLIED_TOTAL);

        addModifier(attributes, ID, Attributes.KNOCKBACK_RESISTANCE, 1.25 + (amplifier * 1.25), ADD_VALUE);

        addModifier(attributes, ID, Attributes.MAX_ABSORPTION, 1.15 + (amplifier * 0.15), ADD_MULTIPLIED_TOTAL);
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
        removeAllModifiers(entity, ID,
                List.of(
                        Attributes.ARMOR_TOUGHNESS,
                        Attributes.KNOCKBACK_RESISTANCE,
                        Attributes.ENTITY_INTERACTION_RANGE,
                        Attributes.ARMOR,
                        Attributes.MAX_ABSORPTION,
                        Attributes.JUMP_STRENGTH,
                        Attributes.FALL_DAMAGE_MULTIPLIER
                )
        );
    }
}