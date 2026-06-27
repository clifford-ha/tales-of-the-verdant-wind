package cliffordha.totvw.effect;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.datagen.ModDamageTypes;
import cliffordha.totvw.registry.ModEffects;
import cliffordha.totvw.registry.ModEnchantments;
import cliffordha.totvw.registry.ModColors;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class BloodlustEffect extends MobEffect {
    private static final Identifier ATK_DMG_ID = Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "bloodlust_attack_damage");
    private static final Identifier MOVEMENT_SPEED_ID = Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "bloodlust_movement_speed");
    private static final Identifier ARMOR_REDUCTION_ID = Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "bloodlust_armor_reduction");

    public BloodlustEffect() {
        super(MobEffectCategory.HARMFUL, ModColors.BLOODLUST_EFFECT);
    }

    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
        onEffectAdded(entity, amplifier);
    }


    @Override
    public void onEffectAdded(LivingEntity entity, int amplifier) {
        AttributeMap attributes = entity.getAttributes();

        double atkMultiplier = 0.2 + (amplifier * 0.2);
        if (attributes.hasAttribute(Attributes.ATTACK_DAMAGE)) {
            attributes.getInstance(Attributes.ATTACK_DAMAGE).addOrReplacePermanentModifier(
                    new AttributeModifier(
                            ATK_DMG_ID,
                            atkMultiplier,
                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            );
        }
        double speedMultiplier = 0.15 + (Math.min(amplifier, 2) * 0.15);
        if (attributes.hasAttribute(Attributes.MOVEMENT_SPEED)) {
            attributes.getInstance(Attributes.MOVEMENT_SPEED).addOrReplacePermanentModifier(
                    new AttributeModifier(
                            MOVEMENT_SPEED_ID,
                            speedMultiplier,
                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            );
        }
        if (entity instanceof Wolf wolf) {
            ItemStack bodyArmor = wolf.getBodyArmorItem();
            if (bodyArmor.isEmpty()) return;

            RegistryAccess registryAccess = wolf.level().registryAccess();
            Registry<Enchantment> enchantmentRegistry = registryAccess.lookupOrThrow(Registries.ENCHANTMENT);

            int mightLevel = bodyArmor.getEnchantments().getLevel(enchantmentRegistry.getOrThrow(ModEnchantments.WOLF_EFFECT_MIGHT));
            if (mightLevel <= 0) return;
            if (attributes.hasAttribute(Attributes.ARMOR)) {
                attributes.getInstance(Attributes.ARMOR).addOrReplacePermanentModifier(
                        new AttributeModifier(
                                ARMOR_REDUCTION_ID,
                                -(0.10 + (amplifier * 0.10)),
                                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                        )
                );
            }
        } else {
            if (attributes.hasAttribute(Attributes.ARMOR)) {
                attributes.getInstance(Attributes.ARMOR).addOrReplacePermanentModifier(
                        new AttributeModifier(
                                ARMOR_REDUCTION_ID,
                                -(0.30 + (amplifier * 0.30)),
                                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                        )
                );
            }
        }
    }

    @Override
    public boolean isBeneficial() {
        return super.isBeneficial();
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
        float inflictDMG = 0.10f + (Math.min(amplifier, 0.3f) * 0.10f);
        if (entity.getHealth() <= 6.0f && entity.hasEffect(MobEffects.REGENERATION)
                || entity.hasEffect(MobEffects.ABSORPTION)
                || entity.hasEffect(ModEffects.BLESSING_OF_THE_VERDANT_WIND)) return false;

        if (serverLevel.getRandom().nextInt(60) == 0) {
            float damage = entity.getHealth() * inflictDMG;
            entity.hurtServer(serverLevel, ModDamageTypes.create(serverLevel, ModDamageTypes.BLOODLUST), damage);
        }
        return super.applyEffectTick(serverLevel, entity, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int tickCount, int amplification) {
        return true;
    }

    private void removeModifiers(LivingEntity entity) {
        AttributeMap attributes = entity.getAttributes();

        if (attributes.hasAttribute(Attributes.ATTACK_DAMAGE)) {
            attributes.getInstance(Attributes.ATTACK_DAMAGE).removeModifier(ATK_DMG_ID);
        }
        if (attributes.hasAttribute(Attributes.MOVEMENT_SPEED)) {
            attributes.getInstance(Attributes.MOVEMENT_SPEED).removeModifier(MOVEMENT_SPEED_ID);
        }
        if (attributes.hasAttribute(Attributes.ARMOR)) {
            attributes.getInstance(Attributes.ARMOR).removeModifier(ARMOR_REDUCTION_ID);
        }
    }
}