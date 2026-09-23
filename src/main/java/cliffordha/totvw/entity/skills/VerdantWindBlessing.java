package cliffordha.totvw.entity.skills;

import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.datagen.VWDamageTypes;
import cliffordha.totvw.effect.HavocEffect;
import cliffordha.totvw.entity.player.VWPlayerBehaviors;
import cliffordha.totvw.entity.wolf.VWWolfBehaviors;
import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.VWEffects;
import cliffordha.totvw.registry.VWEnchantments;
import cliffordha.totvw.registry.VWSounds;
import cliffordha.totvw.registry.attachments.Runestone;
import cliffordha.totvw.registry.attachments.PlayerPrefs;
import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import cliffordha.totvw.util.VWUtil;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.BlockPos;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static cliffordha.totvw.util.VWUtil.*;

public class VerdantWindBlessing {
    public static boolean triggerBenediction(LivingEntity entity, DamageSource source, float damage) {
        if (damage < 4) return true;
        if (!(entity.level() instanceof ServerLevel level)) return true;
        double SCAN_LIMIT = VWConfig.get().SERVER_WOLF_PLAYER_SCAN_DISTANCE * 16;
        float HEALTH_THRESHOLD;
        boolean IS_LOW_HEALTH;
        boolean IS_HIGH_DMG;

        if (entity instanceof Player player) {
            HEALTH_THRESHOLD = getHealthThreshold(player);
            IS_HIGH_DMG = damage > player.getHealth();
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
                        VWUtil.sendParticles(HavocEffect.HAVOC_PARTICLE, level, attacker.blockPosition().above(), 32, 1);
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
            IS_HIGH_DMG = damage > wolf.getHealth();
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
        double SCAN_LIMIT = VWConfig.get().SERVER_WOLF_PLAYER_SCAN_DISTANCE * 16;

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
                && player.getAttachedOrElse(PlayerPrefs.BENEDICTION_SHARE_STACK, VWConfig.get().SERVER_WOLF_SHARES_BENEDICTION_STACK)
                && !player.getAttachedOrElse(PlayerPrefs.BENEDICTION_ALWAYS_TRIGGER_BLESSING, VWConfig.get().SERVER_ALWAYS_TRIGGER_BLESSING);
    }
    private static boolean canSaveWolf(Player player) {
        return VWEnchantments.entityEnchantmentLVL(player, EquipmentSlot.CHEST, VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS) > 0
                && player.getAttachedOrElse(PlayerAttachment.CD_BLESSING_OF_THE_VERDANT_WIND, 0) < 1;
    }
    private static float getHealthThreshold(Player player) {
        return player.getAttachedOrElse(PlayerPrefs.BENEDICTION_HEALTH_THRESHOLD,
                VWConfig.get().SERVER_BENEDICTION_HEALTH_THRESHOLD) * 0.01f;
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
    public static boolean reviveWolf(Wolf wolf, ServerLevel level, DamageSource source) {
        AttachmentType<Integer> BENEDICTION_STACK = WolfAttachment.BENEDICTION;
        int STACK_BEFORE = wolf.getAttachedOrElse(BENEDICTION_STACK, 0);
        if (STACK_BEFORE < 1) return false;

        applyBenedictionEffects(wolf, true);

        wolf.dropLeash();
        wolf.unRide();
        wolf.setOrderedToSit(false);

        level.broadcastEntityEvent(wolf, (byte) 35);

        // main
        wolf.setAttached(BENEDICTION_STACK, STACK_BEFORE - 1);

        String name = wolf.getPlainTextName();
        int STACK_AFTER = wolf.getAttachedOrElse(BENEDICTION_STACK, 0);
        if (STACK_AFTER == 0) {
            sendToChat(wolf, VWColors.BLOODLUST_EFFECT_MUTED, name + " used up all Benediction stacks");
        } else {
            sendToChat(wolf, VWColors.VERDANT_WIND_MUTED,STACK_AFTER + " Benediction stack remaining for " + name);
        }

        if (wolf.getOwner() != null && wolf.getOwner() instanceof Player player) {
            Entity attacker = source.getEntity();
            if (attacker != null) {
                sendToChat(player, VWColors.BLOODLUST_EFFECT_MUTED, false, attacker.getPlainTextName() + " tried to kill " + name + ".");
            } else {
                String report = "Someone tried to kill " + name + ".";
                if (source.is(DamageTypes.GENERIC_KILL)) report = name + " was killed by a command.";
                sendToChat(player, VWColors.BLOODLUST_EFFECT_MUTED, false, report);
            }
        }

        if (wolf.isTame()) {
            LivingEntity owner = wolf.getOwner();

            wolf.dropLeash();
            wolf.unRide();
            wolf.setOrderedToSit(false);

            if (owner == null) {
                teleportToSpawn(wolf);
            } else {
                boolean canTP = wolf.canTeleport(wolf.level(), owner.level())
                        && wolf.level().dimensionType() == owner.level().dimensionType()
                        && owner.getAttachedOrElse(PlayerPrefs.BENEDICTION_TELEPORT_AFTER_SAVE, VWConfig.get().SERVER_TELEPORT_AFTER_SAVE);
                if (canTP) {
                    tryTeleport(wolf, owner);
                } else {
                    teleportToSpawn(wolf);
                }
            }
        }
        return true;
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

        int duration = isRevival ? TimeUtil.sec(10) : TimeUtil.sec(7);
        int benediction = isRevival ? TimeUtil.sec(15) : TimeUtil.sec(30);
        addOrStackEffect(entity, VWEffects.WIND_VEIL, duration, 0, true);
        addOrStackEffect(entity, VWEffects.BLESSING_OF_THE_VERDANT_WIND, benediction, 2, true);
        if (isRevival) {
            addOrStackEffect(entity, MobEffects.ABSORPTION, duration, 2, true);
            addOrStackEffect(entity, MobEffects.STRENGTH, duration, 2, true);
        }
    }
    private static void teleportToSpawn(Wolf wolf) {
        if (wolf.hasAttached(WolfAttachment.RESPAWN_POINT)) {
            BlockPos target = wolf.getAttachedOrElse(WolfAttachment.RESPAWN_POINT, wolf.blockPosition());
            if (isNotValidForTP(wolf.level(), target)) return;
            wolf.teleportToAroundBlockPos(target);
        }
    }
    private static void tryTeleport(Wolf wolf, LivingEntity owner) {
        if (wolf.distanceTo(owner) < 14) return;
        BlockPos playerPos = owner.blockPosition();
        BlockPos wolfPos = wolf.blockPosition();
        if (owner.getAttachedOrElse(PlayerPrefs.BENEDICTION_WOLF_TP_METHOD, VWConfig.get().SERVER_WOLF_TP_METHOD) < 1) {
            if (isNotValidForTP(owner.level(), playerPos)) return;
            wolf.teleportToAroundBlockPos(playerPos);
        } else {
            if (isNotValidForTP(wolf.level(), wolfPos)) return;
            owner.teleportTo(wolfPos.getX(), wolfPos.getY(), wolfPos.getZ());
        }
    }
}
