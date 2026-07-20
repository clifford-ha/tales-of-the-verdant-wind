package cliffordha.totvw.effect;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.VWColors;

import cliffordha.totvw.registry.VWEnchantments;
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
import net.minecraft.world.item.ItemStack;

import java.util.List;

import static cliffordha.totvw.registry.VWEffects.*;
import static cliffordha.totvw.util.VWGlobalUtil.*;

public class AmplifiedMightEffect extends MobEffect {
    public AmplifiedMightEffect() {
        super(MobEffectCategory.BENEFICIAL, VWColors.MIGHT_EFFECT);
    }
    private static final Identifier AMPLIFIED_MIGHT_EFFECT = Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "amplified_might_effect_modifier");
    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
        onEffectAdded(entity, amplifier);
    }


    @Override
    public void onEffectAdded(LivingEntity entity, int amplifier) {
        AttributeMap attributes = entity.getAttributes();
        if (entity instanceof Wolf wolf && wolfEnchantmentLVL(wolf, VWEnchantments.WOLF_EFFECT_MIGHT) > 0) {
            addModifier(attributes, AMPLIFIED_MIGHT_EFFECT,
                    Attributes.ARMOR,  4 + (2 * amplifier), AttributeModifier.Operation.ADD_VALUE);

            addModifier(attributes, AMPLIFIED_MIGHT_EFFECT,
                    Attributes.JUMP_STRENGTH, 0.05 + (amplifier * 0.05), AttributeModifier.Operation.ADD_VALUE);

            addModifier(attributes, AMPLIFIED_MIGHT_EFFECT,
                    Attributes.FALL_DAMAGE_MULTIPLIER, -(0.20 + (amplifier * 0.20)), AttributeModifier.Operation.ADD_VALUE);
        }

        double armorToughness = 0.3 + (amplifier * 0.2);

        addModifier(attributes, AMPLIFIED_MIGHT_EFFECT,
                Attributes.ARMOR_TOUGHNESS, armorToughness + 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

        addModifier(attributes, AMPLIFIED_MIGHT_EFFECT,
                Attributes.KNOCKBACK_RESISTANCE, 1.25 + (amplifier * 1.25), AttributeModifier.Operation.ADD_VALUE);

        addModifier(attributes, AMPLIFIED_MIGHT_EFFECT,
                Attributes.MAX_ABSORPTION, 1.15 + (amplifier * 0.15), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
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
        removeAllModifiers(entity, AMPLIFIED_MIGHT_EFFECT,
                List.of(
                        Attributes.ARMOR_TOUGHNESS,
                        Attributes.KNOCKBACK_RESISTANCE,
                        Attributes.ARMOR,
                        Attributes.MAX_ABSORPTION,
                        Attributes.JUMP_STRENGTH,
                        Attributes.FALL_DAMAGE_MULTIPLIER
                )
        );
    }
}