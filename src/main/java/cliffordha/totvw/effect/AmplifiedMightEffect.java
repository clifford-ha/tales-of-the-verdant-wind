package cliffordha.totvw.effect;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.ModColors;

import cliffordha.totvw.registry.ModEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import static cliffordha.totvw.entity.TConstants.*;

public class AmplifiedMightEffect extends MobEffect {
    public AmplifiedMightEffect() {
        super(MobEffectCategory.BENEFICIAL, ModColors.MIGHT_EFFECT);
    }
    private static final Identifier MIGHT_ARMOR_TOUGHNESS_ID = Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "amp_might_armor_toughness");
    private static final Identifier MIGHT_KNOCKBACK_RESISTANCE_ID = Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "amp_might_knockback_resistance");
    private static final Identifier MIGHT_ARMOR_ID = Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "might_armor");
    private static final Identifier MIGHT_MAX_ABSORPTION_ID = Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "might_max_absorption");
    private static final Identifier MIGHT_JUMP_STRENGTH_ID = Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "might_jump_strength");
    private static final Identifier MIGHT_FALL_DMG_REDUCTION_ID = Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "might_fall_dmg_reduction");

    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
        onEffectAdded(entity, amplifier);
    }


    @Override
    public void onEffectAdded(LivingEntity entity, int amplifier) {
        AttributeMap attributes = entity.getAttributes();
        if (entity instanceof Wolf wolf) {
            ItemStack bodyArmor = wolf.getBodyArmorItem();
            if (bodyArmor.isEmpty()) return;

            int mightLevel = wolfEnchantmentLVL(wolf, ModEnchantments.WOLF_EFFECT_MIGHT);
            if (mightLevel <= 0) return;

            if (attributes.hasAttribute(Attributes.ARMOR)) {
                attributes.getInstance(Attributes.ARMOR).addOrReplacePermanentModifier(
                        new AttributeModifier(
                                MIGHT_ARMOR_ID,
                                4 + (2 * amplifier),
                                AttributeModifier.Operation.ADD_VALUE
                        )
                );
            }
            if (attributes.hasAttribute(Attributes.JUMP_STRENGTH)) {
                attributes.getInstance(Attributes.JUMP_STRENGTH).addOrReplacePermanentModifier(
                        new AttributeModifier(
                                MIGHT_JUMP_STRENGTH_ID,
                                (0.05 + (amplifier * 0.05)),
                                AttributeModifier.Operation.ADD_VALUE
                        )
                );
            }
            if (attributes.hasAttribute(Attributes.FALL_DAMAGE_MULTIPLIER)) {
                attributes.getInstance(Attributes.FALL_DAMAGE_MULTIPLIER).addOrReplacePermanentModifier(
                        new AttributeModifier(
                                MIGHT_FALL_DMG_REDUCTION_ID,
                                -(0.20 + (amplifier * 0.20)),
                                AttributeModifier.Operation.ADD_VALUE
                        )
                );
            }
        }
        double armorToughness = 0.3 + (amplifier * 0.2);
        if (attributes.hasAttribute(Attributes.ARMOR_TOUGHNESS)) {
            attributes.getInstance(Attributes.ARMOR_TOUGHNESS).addOrReplacePermanentModifier(
                    new AttributeModifier(
                            MIGHT_ARMOR_TOUGHNESS_ID,
                            armorToughness + 1,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
            );
        }
        if (attributes.hasAttribute(Attributes.KNOCKBACK_RESISTANCE)) {
            attributes.getInstance(Attributes.KNOCKBACK_RESISTANCE).addOrReplacePermanentModifier(
                    new AttributeModifier(
                            MIGHT_KNOCKBACK_RESISTANCE_ID,
                            1.25 + (amplifier * 1.25),
                            AttributeModifier.Operation.ADD_VALUE
                    )
            );
        }
        if (attributes.hasAttribute(Attributes.MAX_ABSORPTION)) {
            attributes.getInstance(Attributes.MAX_ABSORPTION).addOrReplacePermanentModifier(
                    new AttributeModifier(
                            MIGHT_MAX_ABSORPTION_ID,
                            1.15 + (amplifier * 0.15),
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
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
        if (attributes.hasAttribute(Attributes.ARMOR_TOUGHNESS)) {
            attributes.getInstance(Attributes.ARMOR_TOUGHNESS).removeModifier(MIGHT_ARMOR_TOUGHNESS_ID);
        }
        if (attributes.hasAttribute(Attributes.KNOCKBACK_RESISTANCE)) {
            attributes.getInstance(Attributes.KNOCKBACK_RESISTANCE).removeModifier(MIGHT_KNOCKBACK_RESISTANCE_ID);
        }
        if (attributes.hasAttribute(Attributes.ARMOR)) {
            attributes.getInstance(Attributes.ARMOR).removeModifier(MIGHT_ARMOR_ID);
        }
        if (attributes.hasAttribute(Attributes.MAX_ABSORPTION)) {
            attributes.getInstance(Attributes.MAX_ABSORPTION).removeModifier(MIGHT_MAX_ABSORPTION_ID);
        }
        if (attributes.hasAttribute(Attributes.JUMP_STRENGTH)) {
            attributes.getInstance(Attributes.JUMP_STRENGTH).removeModifier(MIGHT_JUMP_STRENGTH_ID);
        }
        if (attributes.hasAttribute(Attributes.FALL_DAMAGE_MULTIPLIER)) {
            attributes.getInstance(Attributes.FALL_DAMAGE_MULTIPLIER).removeModifier(MIGHT_FALL_DMG_REDUCTION_ID);
        }
    }
}