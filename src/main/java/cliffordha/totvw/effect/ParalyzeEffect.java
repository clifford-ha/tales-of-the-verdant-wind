package cliffordha.totvw.effect;

import cliffordha.totvw.TOTVW;
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

public class ParalyzeEffect extends MobEffect {
    public ParalyzeEffect() {super(MobEffectCategory.NEUTRAL, ModColors.PARALYZE);}
    private static final Identifier PARALYZE_ATTACK_DMG_ID = Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "paralyze_attack_dmg");
    private static final Identifier PARALYZE_KNOCKBACK_RESISTANCE_ID = Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "paralyze_knockback_resistance");
    private static final Identifier PARALYZE_ARMOR_ID = Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "paralyze_armor");
    private static final Identifier PARALYZE_MAX_ABSORPTION_ID = Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "paralyze_max_absorption");
    private static final Identifier PARALYZE_ATTACK_KNOCKBACK_ID = Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "paralyze_attack_knockback");
    private static final Identifier PARALYZE_MOVEMENT_ID = Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "paralyze_movement_speed");
    private static final Identifier PARALYZE_JUMP_ID = Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "paralyze_jump_strength");

    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
        onEffectAdded(entity, amplifier);
    }


    @Override
    public void onEffectAdded(LivingEntity entity, int amplifier) {
        AttributeMap attributes = entity.getAttributes();
        if (attributes.hasAttribute(Attributes.ARMOR)) {
            attributes.getInstance(Attributes.ARMOR).addOrReplacePermanentModifier(
                    new AttributeModifier(
                            PARALYZE_ARMOR_ID,
                            -10000,
                            AttributeModifier.Operation.ADD_VALUE
                    )
            );
        }
        if (attributes.hasAttribute(Attributes.KNOCKBACK_RESISTANCE)) {
            attributes.getInstance(Attributes.KNOCKBACK_RESISTANCE).addOrReplacePermanentModifier(
                    new AttributeModifier(
                            PARALYZE_KNOCKBACK_RESISTANCE_ID,
                            -10000,
                            AttributeModifier.Operation.ADD_VALUE
                    )
            );
        }
        if (attributes.hasAttribute(Attributes.MAX_ABSORPTION)) {
            attributes.getInstance(Attributes.MAX_ABSORPTION).addOrReplacePermanentModifier(
                    new AttributeModifier(
                            PARALYZE_MAX_ABSORPTION_ID,
                            -10000,
                            AttributeModifier.Operation.ADD_VALUE
                    )
            );
        }
        if (attributes.hasAttribute(Attributes.MOVEMENT_SPEED)) {
            attributes.getInstance(Attributes.MOVEMENT_SPEED).addOrReplacePermanentModifier(
                    new AttributeModifier(
                            PARALYZE_MOVEMENT_ID,
                            -10000,
                            AttributeModifier.Operation.ADD_VALUE
                    )
            );
        }
        if (attributes.hasAttribute(Attributes.ATTACK_DAMAGE)) {
            attributes.getInstance(Attributes.ATTACK_DAMAGE).addOrReplacePermanentModifier(
                    new AttributeModifier(
                            PARALYZE_ATTACK_DMG_ID,
                            -10000,
                            AttributeModifier.Operation.ADD_VALUE
                    )
            );
        }
        if (attributes.hasAttribute(Attributes.ATTACK_KNOCKBACK)) {
            attributes.getInstance(Attributes.ATTACK_KNOCKBACK).addOrReplacePermanentModifier(
                    new AttributeModifier(
                            PARALYZE_ATTACK_KNOCKBACK_ID,
                            -10000,
                            AttributeModifier.Operation.ADD_VALUE
                    )
            );
        }
        if (attributes.hasAttribute(Attributes.JUMP_STRENGTH)) {
            attributes.getInstance(Attributes.JUMP_STRENGTH).addOrReplacePermanentModifier(
                    new AttributeModifier(
                            PARALYZE_JUMP_ID,
                            -10000,
                            AttributeModifier.Operation.ADD_VALUE
                    )
            );
        }
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
        if (attributes.hasAttribute(Attributes.ARMOR)) {
            attributes.getInstance(Attributes.ARMOR).removeModifier(PARALYZE_ARMOR_ID);
        }
        if (attributes.hasAttribute(Attributes.KNOCKBACK_RESISTANCE)) {
            attributes.getInstance(Attributes.KNOCKBACK_RESISTANCE).removeModifier(PARALYZE_KNOCKBACK_RESISTANCE_ID);
        }
        if (attributes.hasAttribute(Attributes.MAX_ABSORPTION)) {
            attributes.getInstance(Attributes.MAX_ABSORPTION).removeModifier(PARALYZE_MAX_ABSORPTION_ID);
        }
        if (attributes.hasAttribute(Attributes.MOVEMENT_SPEED)) {
            attributes.getInstance(Attributes.MOVEMENT_SPEED).removeModifier(PARALYZE_MOVEMENT_ID);
        }
        if (attributes.hasAttribute(Attributes.ATTACK_DAMAGE)) {
            attributes.getInstance(Attributes.ATTACK_DAMAGE).removeModifier(PARALYZE_ATTACK_DMG_ID);
        }
        if (attributes.hasAttribute(Attributes.ATTACK_KNOCKBACK)) {
            attributes.getInstance(Attributes.ATTACK_KNOCKBACK).removeModifier(PARALYZE_ATTACK_KNOCKBACK_ID);
        }
        if (attributes.hasAttribute(Attributes.JUMP_STRENGTH)) {
            attributes.getInstance(Attributes.JUMP_STRENGTH).removeModifier(PARALYZE_JUMP_ID);
        }
    }
}