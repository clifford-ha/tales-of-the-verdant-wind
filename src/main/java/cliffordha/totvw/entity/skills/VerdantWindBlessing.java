package cliffordha.totvw.entity.skills;

import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.datagen.VWDamageTypes;
import cliffordha.totvw.effect.HavocEffect;
import cliffordha.totvw.entity.player.VWPlayerBehaviors;
import cliffordha.totvw.entity.skill.PlayerSkills;
import cliffordha.totvw.entity.skill.SkillCooldowns;
import cliffordha.totvw.entity.skill.SkillManager;
import cliffordha.totvw.entity.wolf.VWWolfBehaviors;
import cliffordha.totvw.entity.skill.WolfSkills;
import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.VWEffects;
import cliffordha.totvw.registry.VWEnchantments;
import cliffordha.totvw.registry.VWSounds;
import cliffordha.totvw.registry.attachments.Runestone;
import cliffordha.totvw.registry.attachments.PlayerPrefs;
import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import cliffordha.totvw.tag.VWBiomeTags;
import cliffordha.totvw.util.ExtUtil;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static cliffordha.totvw.util.ExtUtil.*;

public class VerdantWindBlessing {
    public static boolean triggerBenediction(LivingEntity entity, DamageSource source, float damage) {
        if (damage < 4) return true;
        if (!(entity.level() instanceof ServerLevel level)) return true;
        double SCAN_LIMIT = level.getServer().getPlayerList().getSimulationDistance() * 16;
        float HEALTH_THRESHOLD;
        boolean IS_LOW_HEALTH;
        boolean IS_HIGH_DMG;

        if (entity instanceof Player player) {
            HEALTH_THRESHOLD = getHealthThreshold(player);
            IS_HIGH_DMG = damage > player.getHealth() + player.getAbsorptionAmount();
            IS_LOW_HEALTH = player.getHealth() <= player.getMaxHealth() * HEALTH_THRESHOLD;

            if (IS_HIGH_DMG || IS_LOW_HEALTH) {
                List<Wolf> wolves = getWolvesInRange(level, player, SCAN_LIMIT);
                if (wolves.isEmpty()) return true;

                Wolf wolf = wolves.getFirst();
                if (wolfCanShareBenediction(wolf, player)) return true;
                VWWolfBehaviors.runPlayerBlessing(wolf, player, level);
                return false;
            }

        } else if (entity instanceof Wolf wolf) {
            Entity attacker = source.getEntity();

            boolean ACTIVE_BENEDICTION = VWEnchantments.getBenediction(wolf);
            if (!source.is(DamageTypes.GENERIC_KILL) && damage > wolf.getMaxHealth() * 0.5f && ACTIVE_BENEDICTION) {
                AttachmentType<Integer> IGNORE_DMG_CD = WolfAttachment.CD_IGNORE_HIGH_DAMAGE;
                int IGNORE_DMG = wolf.getAttachedOrElse(IGNORE_DMG_CD, 0);
                if (IGNORE_DMG < 1) {
                    if (Runestone.hasHavoc(wolf) && attacker != null) {
                        ExtUtil.sendParticles(HavocEffect.HAVOC_PARTICLE, level, attacker.blockPosition().above(), 32, 1);
                        playSound(wolf, VWSounds.WOLF_SKILL_PARALYZE, SoundSource.PLAYERS, false);
                        attacker.hurtServer(level, VWDamageTypes.havoc(level), Math.min(12f, damage * 0.5f));
                    } else {
                        ItemStack armor = wolf.getBodyArmorItem();
                        armor.hurtAndBreak(12, wolf, EquipmentSlot.BODY);
                    }
                    wolf.setAttached(IGNORE_DMG_CD, 15);
                    sendToChat(wolf, false, wolf.getPlainTextName() + " ignored " + (double) damage + " damage");
                    return false;
                }
            }

            if (wolf.isBaby() && attacker != null) {
                if (Runestone.hasHavoc(wolf)) {
                    attacker.hurtServer(level, VWDamageTypes.havoc(level), Mth.ceil(damage * 0.5f));
                } else {
                    List<Wolf> scan = level.getEntitiesOfClass(
                        Wolf.class,
                        wolf.getBoundingBox().inflate(16),
                        t -> WolfAttachment.isFamilyRelated(t, wolf) && Runestone.hasHavoc(t));

                    if (!scan.isEmpty()) {
                        attacker.hurtServer(level, VWDamageTypes.havoc(level), Mth.ceil(damage * 0.5f));
                    }
                }
            }

            LivingEntity owner = wolf.getOwner();
            if (owner == null) return true;
            if (!(owner instanceof Player player)) return true;

            HEALTH_THRESHOLD = getHealthThreshold(player);
            IS_HIGH_DMG = damage > wolf.getHealth() + wolf.getAbsorptionAmount();
            IS_LOW_HEALTH = wolf.getHealth() <= wolf.getMaxHealth() * HEALTH_THRESHOLD;

            if (IS_HIGH_DMG || IS_LOW_HEALTH) {
                if (canSaveWolf(player)) {
                    if (wolf.distanceTo(owner) > SCAN_LIMIT) return true;
                    VWPlayerBehaviors.runWolfBlessing(player, wolf, level);
                    return false;
                }
            }
        }
        return true;
    }
    public static void triggerBenedictionFromTick(LivingEntity entity) {
        if (!(entity.level() instanceof ServerLevel level)) return;
        double SCAN_LIMIT = level.getServer().getPlayerList().getSimulationDistance() * 16;

        if (entity instanceof Player player) {
            List<Wolf> wolves = getWolvesInRange(level, player, SCAN_LIMIT);
            if (wolves.isEmpty()) return;

            Wolf wolf = wolves.getFirst();
            if (wolfCanShareBenediction(wolf, player)) return;
            VWWolfBehaviors.runPlayerBlessing(wolf, player, level);

        } else if (entity instanceof Wolf wolf) {
            LivingEntity owner = wolf.getOwner();
            if (owner == null) return;
            if (!(owner instanceof Player player)) return;

            if (canSaveWolf(player)) {
                if (wolf.distanceTo(owner) > SCAN_LIMIT) return;
                VWPlayerBehaviors.runWolfBlessing(player, wolf, level);
            }
        }
    }
    private static boolean wolfCanShareBenediction(Wolf wolf, Player player) {
        return wolf.getAttachedOrElse(WolfAttachment.BENEDICTION, 0) > 1
                && player.getAttachedOrElse(PlayerPrefs.BENEDICTION_SHARE_STACK, VWConfig.get().SYNCED_WOLF_SHARES_BENEDICTION_STACK)
                && !player.getAttachedOrElse(PlayerPrefs.BENEDICTION_ALWAYS_TRIGGER_BLESSING, VWConfig.get().SYNCED_ALWAYS_TRIGGER_BLESSING);
    }
    private static boolean canSaveWolf(Player player) {
        return VWEnchantments.entityEnchantmentLVL(player, EquipmentSlot.CHEST, VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS) > 0
                && player.getAttachedOrElse(PlayerAttachment.CD_BLESSING_OF_THE_VERDANT_WIND, 0) < 1;
    }
    private static float getHealthThreshold(Player player) {
        return player.getAttachedOrElse(PlayerPrefs.BENEDICTION_HEALTH_THRESHOLD,
                VWConfig.get().SYNCED_BENEDICTION_HEALTH_THRESHOLD) * 0.01f;
    }
    private static List<Wolf> getWolvesInRange(ServerLevel level, Player player, double range) {
        List<Wolf> get = level.getEntities(
                EntityTypes.WOLF,
                player.getBoundingBox().inflate(range),
                wolf -> wolf.getOwner() == player
                        && VWEnchantments.getBenediction(wolf)
                        && wolf.getAttachedOrElse(WolfAttachment.CD_BLESSING_OF_THE_VERDANT_WIND, 0) < 1
        );
        return get.isEmpty() ? new ArrayList<>() : get;
    }
    public static void applyBenedictionEffects(LivingEntity entity, boolean isRevival) {
        if (isRevival) {
            entity.removeAllEffects();
            float health = entity.getHealth();
            float maxHealth = entity.getMaxHealth() * 0.5f;
            if (health < maxHealth) {
                entity.setHealth(maxHealth);
            }
        } else {
            Collection<MobEffectInstance> effects = entity.getActiveEffects();
            List<Holder<MobEffect>> effectsToRemove = new ArrayList<>();
            if (!effects.isEmpty()) {
                for (MobEffectInstance effect : effects) {
                    if (!effect.getEffect().value().isBeneficial()) {
                        effectsToRemove.add(effect.getEffect());
                    }
                }
            }
            for (Holder<MobEffect> effect : effectsToRemove) {
                removeEffect(entity, effect);
            }
        }

        int duration = isRevival ? 10 : 5;
        int benediction = isRevival ? 15 : 30;
        addEffect(entity, VWEffects.WIND_VEIL, duration, 0);
        addOrStackEffect(entity, VWEffects.BLESSING_OF_THE_VERDANT_WIND, benediction, 2, true);
        if (isRevival) {
            addOrStackEffect(entity, MobEffects.ABSORPTION, duration, 2, true);
            addOrStackEffect(entity, MobEffects.STRENGTH, duration, 2, true);
        }
    }
    public static float triggerHeal(LivingEntity granter, LivingEntity grantee) {
        float triggerHeal;
        if (isInBiome(granter, VWBiomeTags.IS_VERDANT_BIOMES)) {
            triggerHeal = Math.round((granter.getHealth() * 0.5f) + (grantee.getMaxHealth() * 0.5f));
        } else {
            triggerHeal = Math.round((granter.getHealth() * 0.5f) + (grantee.getMaxHealth() * 0.3f));}
        return triggerHeal;
    }
    public static void verdantBlessingAfterEffects(LevelAccessor level, LivingEntity entity) {
        int amp = entity.getHealth() >= entity.getMaxHealth() * 0.5f ? 0 : 1;
        addHiddenEffect(entity, MobEffects.WEAKNESS, 60, amp);

        if (!VWConfig.get().SERVER_SKILL_COOLDOWNS) return;
        int cooldown = cooldownFromBase(level, SkillCooldowns.BASE_BLESSING);

        String name = "[" + entity.getName().getString() + "] ";
        String constructor = name + "Verdant Wind's Blessing is now on cooldown for " + cooldown + " seconds.";
        if (entity instanceof Wolf wolf) {
            SkillManager.startCooldown(wolf, WolfSkills.VERDANT_BLESSING, cooldown);
            sendToChat(wolf, VWColors.VERDANT_WIND, constructor);
        } else if (entity instanceof Player player) {
            SkillManager.startCooldown(player, PlayerSkills.VERDANT_BLESSING, cooldown);
            sendToChat(player, VWColors.VERDANT_WIND, constructor);
        }
    }


}
