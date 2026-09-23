package cliffordha.totvw.entity;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.effect.HavocEffect;
import cliffordha.totvw.entity.player.PlayerAtrocityCounter;
import cliffordha.totvw.entity.skills.RevivalByProxy;
import cliffordha.totvw.entity.player.VWPlayerBehaviors;
import cliffordha.totvw.entity.skills.RunestoneEffects;
import cliffordha.totvw.entity.skills.VerdantWindBlessing;
import cliffordha.totvw.entity.wolf.VWWolfBehaviors;
import cliffordha.totvw.registry.*;
import cliffordha.totvw.registry.attachments.HavocType;
import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static cliffordha.totvw.TOTVW.sendClassRegisterLog;
import static cliffordha.totvw.util.VWUtil.*;

public class VWGlobalEntityBehaviors {
    public static void register() {
        onDamageOrDeathEvent();

        if (TOTVW.IN_DEVELOPMENT) {
            developmentTick();
        }

        VWPlayerBehaviors.registerModPlayerBehaviors();
        VWWolfBehaviors.registerModWolfBehaviors();
        sendClassRegisterLog("Custom Entity Behaviors");
    }


    private static void developmentTick() {
        ServerTickEvents.END_SERVER_TICK.register((MinecraftServer server) -> {
            for (var serverLevel : server.getAllLevels()) {
                serverLevel.getEntities(EntityTypes.PLAYER, _ -> true).forEach(player -> {
                    if (!player.entityTags().contains(player.getStringUUID() + "-reminderStamp")) {
                        sendToChat(player, VWColors.VERDANT_WIND, false, "TOTVW mod version is a development build.");
                        player.entityTags().add(player.getStringUUID() + "-reminderStamp");
                    }
                    if (!player.getAttachedOrElse(PlayerAttachment.IS_DEV_MODE, false)) {
                        player.setAttached(PlayerAttachment.IS_DEV_MODE, true);
                    }
                });
            }
        });
    }

    private static void onDamageOrDeathEvent() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(
                VWGlobalEntityBehaviors::allowDamageEvent
        );
        ServerLivingEntityEvents.AFTER_DAMAGE.register(
                (victim, damageSource, _, damage, _) -> afterDamageEvent(victim, damageSource, damage)
        );
        ServerLivingEntityEvents.AFTER_DEATH.register(
                VWGlobalEntityBehaviors::afterDeathEvent
        );
        ServerLivingEntityEvents.AFTER_DEATH.register(
                RunestoneEffects::reapplyLinkStatus
        );
        ServerLivingEntityEvents.ALLOW_DEATH.register(
                RevivalByProxy::revivePlayerIfPossible
        );
    }
    private static void afterDeathEvent(LivingEntity entity, DamageSource damageSource) {
        Entity attacker = damageSource.getEntity();

        if (attacker instanceof Player player) {
            if (entity.is(EntityTypes.WOLF) || entity.is(EntityTypes.VILLAGER) || entity.is(EntityTypes.WANDERING_TRADER)) {
                PlayerAtrocityCounter.processAtrocity(player, entity, true);
            }
        }
        if (entity instanceof Player player) {
            if (attacker instanceof Player attackerPlayer) {
                updateNearbyWolvesOnDamage(player, attackerPlayer);
            }

            HavocType type = HavocType.getType(player);
            boolean hasType = HavocType.isAnnihilation(player) || HavocType.isVoid(player);
            boolean havocPenalty = player.hasEffect(VWEffects.HAVOC) && hasType;

            if (havocPenalty && attacker instanceof LivingEntity getAttacker) {
                RunestoneEffects.triggerHavocPenalty(getAttacker, type);
            }

            //always last
            removePlayerAttachmentsOnDeath(player);
        }
    }
    private static void afterDamageEvent(LivingEntity victim, DamageSource damageSource, float dmg) {
        if (victim == null) return;
        Entity attacker = damageSource.getEntity();

        if (attacker instanceof Player player) {
            if (victim.is(EntityTypes.WOLF) || victim.is(EntityTypes.VILLAGER) || victim.is(EntityTypes.WANDERING_TRADER)) {
                PlayerAtrocityCounter.processAtrocity(player, victim, false);
            }
        }
        if (victim instanceof Player player) {
            if (player.hasEffect(VWEffects.HAVOC) && attacker instanceof LivingEntity) {
                RunestoneEffects.getHavocForPlayer(player, (LivingEntity) attacker, dmg);
            }
            if (attacker instanceof Player attackerPlayer) {
                if (dmg > 3) {
                    updateNearbyWolvesOnDamage(player, attackerPlayer);
                }
            }
        }
    }
    private static boolean allowDamageEvent(LivingEntity entity, DamageSource source, float damage) {
        Entity attacker = source.getEntity();
        if (entity.hasEffect(VWEffects.WIND_VEIL)) {
            return windVeilEffect(entity, source, damage);
        }

        if (entity instanceof Wolf wolf) {
            boolean disableFireDMG = source.is(DamageTypeTags.IS_FREEZING) && VWEnchantments.getIgnition(wolf) > 0;
            boolean disableAccidentalDMG = wolf.getOwner() instanceof LivingEntity owner && attacker instanceof LivingEntity mob && owner == mob;

            if (disableFireDMG) {
                return false;
            }
            if (disableAccidentalDMG) {
                return false;
            }

            return VerdantWindBlessing.triggerBenediction(wolf, source, damage);

        } else if (entity instanceof Player player) {
            boolean disableAccidentalDMG = attacker instanceof Wolf wolf && wolf.getOwner() == player;
            if (disableAccidentalDMG) {
                return false;
            }
            return VerdantWindBlessing.triggerBenediction(player, source, damage);

        } else {
            return true;
        }
    }


    private static boolean windVeilEffect(LivingEntity entity, DamageSource damageSource, float damage) {
        if (!(entity.level() instanceof ServerLevel level)) return true;
        Entity source = damageSource.getEntity();
        if (entity instanceof Monster) return true;
        if (source instanceof LivingEntity attacker && !attacker.is(EntityTypes.WOLF)) {
            if (entity instanceof Player || entity instanceof Wolf) {
                RandomSource random = level.getRandom();
                DamageSource dmg = level.damageSources().magic();
                if (random.nextFloat() < 0.6f) {
                    if (VWEnchantments.getBenediction(entity)) {
                        attacker.hurtServer(level, dmg, Math.max(2, attacker.getHealth() * 0.15f));
                    }
                    attacker.knockback(Math.max(1, attacker.getMaxHealth() * 0.05f), - attacker.getYHeadRot(), - entity.getYHeadRot(), dmg, 0);
                    level.playSound(null, entity.blockPosition(), SoundEvents.THORNS_HIT, SoundSource.PLAYERS);

                    for (int i = 0; i < random.nextIntBetweenInclusive(1, 5); i++) {
                        level.sendParticles(ParticleTypes.ENCHANT, entity.getX(), entity.getY(), entity.getZ(), 3, 0, 0, 0, 0);
                    }
                }
                if (entity instanceof Player player) {
                    if (player.hasEffect(VWEffects.HAVOC) && attacker instanceof LivingEntity) {
                        RunestoneEffects.getHavocForPlayer(player, attacker, damage);
                    }
                    List<Wolf> wolves = level.getEntities(EntityTypes.WOLF, player.getBoundingBox().inflate(16), wolf -> wolf.getOwner() == player);

                    if (!wolves.isEmpty()) {
                        for (Wolf wolf : wolves) {
                            wolf.setTarget(attacker);
                        }
                    }
                }
            }
        }
        return false;
    }
    public static void removePlayerAttachmentsOnDeath(Player player) {
        if (!(player.level() instanceof ServerLevel)) return;
        HavocEffect.removeHavoc(player);
        player.removeAttached(PlayerAttachment.WOLF_ATROCITY_COUNT);
        player.removeAttached(PlayerAttachment.VILLAGER_ATROCITY_COUNT);

        player.removeAttached(PlayerAttachment.RECEIVED_ENCHANTMENTS_HANDBOOK);
        player.removeAttached(PlayerAttachment.RECEIVED_EFFECTS_HANDBOOK);
        player.removeAttached(PlayerAttachment.RECEIVED_ITEMS_HANDBOOK);
        player.removeAttached(PlayerAttachment.RECEIVED_FEATURES_HANDBOOK);
    }
    private static void updateNearbyWolvesOnDamage(Player player, Player attacker) {
        ServerLevel level = (ServerLevel) player.level();
        List<Wolf> wolves = level.getEntitiesOfClass(Wolf.class, scanArea(player, 16), wolf -> wolf.getOwner() == player);

        if (wolves.isEmpty()) return;

        for (Wolf wolf : wolves) {
            UUID attackerUUID = attacker.getUUID();

            if (WolfAttachment.isPlayerTrusted(wolf, attacker)) {
                WolfAttachment.removePlayerTrust(wolf, attacker);
            }
            List<UUID> aggressors = new ArrayList<>(wolf.getAttachedOrElse(WolfAttachment.AGGRESSOR_LIST, List.of()));
            if (!aggressors.contains(attackerUUID)) {
                aggressors.add(attackerUUID);
                wolf.setAttached(WolfAttachment.AGGRESSOR_LIST, aggressors);
            }
        }
    }
}
