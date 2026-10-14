package cliffordha.totvw.entity;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.entity.player.PlayerAtrocityCounter;
import cliffordha.totvw.entity.skills.RevivalByProxy;
import cliffordha.totvw.entity.player.VWPlayerBehaviors;
import cliffordha.totvw.entity.skills.RunestoneEffects;
import cliffordha.totvw.entity.skills.VerdantWindBlessing;
import cliffordha.totvw.entity.wolf.VWWolfBehaviors;
import cliffordha.totvw.registry.*;
import cliffordha.totvw.registry.attachments.HavocType;
import cliffordha.totvw.registry.attachments.VWAttachments;
import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import cliffordha.totvw.tag.VWBiomeTags;

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
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import oshi.util.tuples.Pair;

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
                serverLevel.getEntities(EntityType.PLAYER, _ -> true).forEach(player -> {
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

        boolean checkWardenStat = entity instanceof Warden warden
                && isInBiome(warden, VWBiomeTags.IS_VERDANT_BIOMES);

        Player playerWardenSlayer = attacker instanceof Player getPlayer ? getPlayer : (attacker instanceof Wolf wolf && wolf.getOwner() instanceof Player owner ? owner : null);
        if (checkWardenStat && playerWardenSlayer != null && playerWardenSlayer.level() instanceof ServerLevel level) {
            int acquisition = playerWardenSlayer.getAttachedOrElse(PlayerAttachment.GENESIS_RUNESTONE_ACQUISITION_COUNT, 0);
            boolean shouldGrant = VWEnchantments.getBenediction(playerWardenSlayer) && acquisition < 2;
            if (shouldGrant) {
                playerWardenSlayer.getInventory().add(new ItemStack(VWItems.GENESIS_RUNESTONE_PLATE));
                playerWardenSlayer.setAttached(PlayerAttachment.GENESIS_RUNESTONE_ACQUISITION_COUNT, acquisition + 1);

                sendParticles(ParticleTypes.EXPLOSION_EMITTER, level, playerWardenSlayer.blockPosition(), 12, 3);
                sendToChat(playerWardenSlayer, VWColors.VERDANT_WIND, false, "Verdant Wind's Benediction: You have been granted a Genesis Runestone Plate.");
            }
        }

        if (entity instanceof ElderGuardian guardian && attacker instanceof Player player && player.level() instanceof ServerLevel level) {
            List<Wolf> tamedWolf = level.getEntitiesOfClass(Wolf.class, scanArea(guardian, 16), t -> t.getOwner() == player);
            if (!tamedWolf.isEmpty()) {
                addToInventory(player, VWItems.TETHER_RUNESTONE_PLATE);
                for (Wolf wolf : tamedWolf) {
                    wolf.setHealth(wolf.getMaxHealth());
                }
                player.setHealth(player.getMaxHealth());

            } else if (level.getRandom().nextFloat() < 0.07f) {
                addToInventory(player, VWItems.TETHER_RUNESTONE_PLATE);
            }
        }

        if (attacker instanceof Player player) {
            if (entity.is(EntityType.WOLF) || entity.is(EntityType.VILLAGER) || entity.is(EntityType.WANDERING_TRADER)) {
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
        }
    }
    private static void afterDamageEvent(LivingEntity victim, DamageSource damageSource, float dmg) {
        if (victim == null) return;
        Entity attacker = damageSource.getEntity();

        if (attacker instanceof Player player) {
            if (victim.is(EntityType.WOLF) || victim.is(EntityType.VILLAGER) || victim.is(EntityType.WANDERING_TRADER)) {
                PlayerAtrocityCounter.processAtrocity(player, victim, false);
            }
        }
        if (victim instanceof Player player) {
            if (player.hasEffect(VWEffects.HAVOC) && attacker instanceof LivingEntity) {
                RunestoneEffects.getHavocForPlayer(player, (LivingEntity) attacker, dmg);
            }
            if (attacker instanceof Player attackerPlayer) {
                updateNearbyWolvesOnDamage(player, attackerPlayer);
            }
        }
        if (victim instanceof Wolf wolf && attacker instanceof Player player) {
            boolean isOwner = wolf.getOwner() != null && wolf.getOwner() == player;
            if (!isOwner) {
                WolfAttachment.addPlayerToAggressors(wolf, player);
            }
        }
    }
    private static boolean allowDamageEvent(LivingEntity entity, DamageSource source, float damage) {
        Entity attacker = source.getEntity();
        if (entity.hasEffect(VWEffects.WIND_VEIL)) {
            return windVeilEffect(entity, source, damage);
        }

        if (attacker instanceof Player attackerPlayer && entity instanceof Player player) {
            if (PlayerAttachment.trustOther(attackerPlayer, player)) return false;
        }

        if (entity instanceof Wolf wolf) {
            boolean negateFreezingDMG = source.is(DamageTypeTags.IS_FREEZING) && VWEnchantments.getIgnition(wolf) > 0;
            boolean disableAccidentalDMG = wolf.getOwner() instanceof LivingEntity owner && attacker instanceof LivingEntity mob && owner == mob;

            if (negateFreezingDMG) {
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
        if (source instanceof LivingEntity attacker && !attacker.is(EntityType.WOLF)) {
            if (entity instanceof Player || entity instanceof Wolf) {
                RandomSource random = level.getRandom();
                DamageSource dmg = level.damageSources().magic();
                if (random.nextFloat() < 0.6f) {
                    if (VWEnchantments.getBenediction(entity)) {
                        attacker.hurtServer(level, dmg, Math.max(2, attacker.getHealth() * 0.15f));
                        if (attacker instanceof Player player && !player.getMainHandItem().isEmpty()) {
                            player.getCooldowns().addCooldown(player.getMainHandItem(), 60);
                        }
                    }
                    attacker.knockback(Math.max(1, attacker.getMaxHealth() * 0.05f), - attacker.getYHeadRot(), - entity.getYHeadRot());
                    level.playSound(null, entity.blockPosition(), SoundEvents.THORNS_HIT, SoundSource.PLAYERS);

                    for (int i = 0; i < random.nextIntBetweenInclusive(1, 5); i++) {
                        level.sendParticles(ParticleTypes.ENCHANT, entity.getX(), entity.getY(), entity.getZ(), 3, 0, 0, 0, 0);
                    }
                }
                if (entity instanceof Player player) {
                    if (player.hasEffect(VWEffects.HAVOC) && attacker instanceof LivingEntity) {
                        RunestoneEffects.getHavocForPlayer(player, attacker, damage);
                    }
                    List<Wolf> wolves = level.getEntities(EntityType.WOLF, scanArea(player, 16), wolf -> wolf.getOwner() == player);

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
    private static void updateNearbyWolvesOnDamage(Player player, Player attacker) {
        ServerLevel level = (ServerLevel) player.level();
        List<Wolf> wolves = level.getEntitiesOfClass(Wolf.class, scanArea(player, 16), wolf -> wolf.getOwner() == player);

        if (wolves.isEmpty()) return;

        for (Wolf wolf : wolves) {
            List<Pair<String, UUID>> trustedPlayers = new ArrayList<>(WolfAttachment.getTrustedPlayers(wolf));
            UUID attackerUUID = attacker.getAttachedOrElse(VWAttachments.WOLF_PLAYER_SHARED_ID, attacker.getUUID());

            for (Pair<String, UUID> trusted : trustedPlayers) {
                if (trusted.getB().equals(attackerUUID)) {
                    trustedPlayers.remove(trusted);
                    wolf.setAttached(WolfAttachment.TRUSTED_PLAYERS, trustedPlayers);
                    sendToChat(player, VWColors.BLOODLUST_EFFECT_MUTED, false, wolf.getPlainTextName() + " no longer trust " + trusted.getA());
                    break;
                }
            }
        }
    }
}
