package cliffordha.totvw.entity;

import cliffordha.totvw.effect.HavocEffect;
import cliffordha.totvw.entity.events.DevelopmentEvents;
import cliffordha.totvw.entity.events.InteractionEvents;
import cliffordha.totvw.entity.player.AtrocityManager;
import cliffordha.totvw.entity.skills.*;
import cliffordha.totvw.entity.player.VWPlayerBehaviors;
import cliffordha.totvw.entity.wolf.VWWolfBehaviors;
import cliffordha.totvw.item.events.VWItemBlessings;
import cliffordha.totvw.networking.VWNetworking;
import cliffordha.totvw.registry.*;
import cliffordha.totvw.registry.attachments.HavocType;
import cliffordha.totvw.registry.attachments.NameHolder;
import cliffordha.totvw.registry.attachments.VWAttachment;
import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import cliffordha.totvw.tag.VWBiomeTags;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.entity.event.v1.effect.EffectEventContext;
import net.fabricmc.fabric.api.entity.event.v1.effect.ServerMobEffectEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import static cliffordha.totvw.TOTVW.sendClassRegisterLog;
import static cliffordha.totvw.util.ExtUtil.*;

public class GlobalEntityEvents {
    public static void register() {
        registerGlobalEvents();
        DevelopmentEvents.register();

        VWPlayerBehaviors.registerModPlayerBehaviors();
        VWWolfBehaviors.registerModWolfBehaviors();
        sendClassRegisterLog("Entity Events & Behaviors");
    }

    private static void registerGlobalEvents() {
        UseEntityCallback.EVENT.register( (player, level, hand, entity, _)
                -> InteractionEvents.onEntityInteractEvent(player, entity, hand));

        UseItemCallback.EVENT.register((player, level, _)
                -> InteractionEvents.onUseItemEvent(player));

        ServerMobEffectEvents.AFTER_REMOVE.register(GlobalEntityEvents::onMobEffectsRemoved);
        ServerEntityEvents.EQUIPMENT_CHANGE.register(GlobalEntityEvents::onEquipmentChange);
        ServerPlayerEvents.AFTER_RESPAWN.register(GlobalEntityEvents::afterRespawnEvent);

        ServerLivingEntityEvents.ALLOW_DAMAGE.register(GlobalEntityEvents::allowDamageEvent);
        ServerLivingEntityEvents.AFTER_DAMAGE.register(
                (victim, damageSource, _, damage, _)
                        -> afterDamageEvent(victim, damageSource, damage));

        ServerLivingEntityEvents.AFTER_DEATH.register(GlobalEntityEvents::afterDeathEvent);
        ServerLivingEntityEvents.AFTER_DEATH.register(RunestoneEffects::reapplyLinkStatus);
        ServerLivingEntityEvents.ALLOW_DEATH.register(RevivalByProxy::allowPlayerDeath);
    }
    private static void afterRespawnEvent(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
        if (alive) {
            WolfBenedictionHelper.loadWolfFilesIfPresent(newPlayer, newPlayer.level());
        }
    }
    private static void onEquipmentChange(LivingEntity entity, EquipmentSlot slot, ItemStack from, ItemStack to) {
        if (!(entity.level() instanceof ServerLevel)) return;
        if (entity instanceof Player player) VWNetworking.syncPlayerArmor(player);
        if (entity instanceof Wolf wolf && wolf.isTame()) VWNetworking.syncWolfArmor(wolf);
    }
    private static void onMobEffectsRemoved(MobEffectInstance instance, LivingEntity entity, EffectEventContext ctx) {
        if (!(entity.level() instanceof ServerLevel)) return;
        MobEffect value = instance.getEffect().value();
        if (entity instanceof Player player) {
            if (value instanceof HavocEffect) {
                HavocEffect.removeHavoc(player);

            } else if (instance.getEffect().is(Predicate.isEqual(MobEffects.NAUSEA))) {
                player.removeAttached(PlayerAttachment.EXPLOSION_RES);
            }
        }
        if (entity instanceof Wolf wolf) VWNetworking.syncWolfEffects(wolf);
    }

    private static void afterDeathEvent(LivingEntity entity, DamageSource damageSource) {
        if (!(entity.level() instanceof ServerLevel level)) return;
        Entity attacker = damageSource.getEntity();

        boolean checkWardenStat = entity instanceof Warden warden
                && isInBiome(warden, VWBiomeTags.IS_VERDANT_BIOMES);

        Player playerWardenSlayer = attacker instanceof Player getPlayer ? getPlayer : (attacker instanceof Wolf wolf && wolf.getOwner() instanceof Player owner ? owner : null);
        if (checkWardenStat && playerWardenSlayer != null) {
            int acquisition = playerWardenSlayer.getAttachedOrElse(PlayerAttachment.GENESIS_RUNESTONE_ACQUISITION_COUNT, 0);
            boolean shouldGrant = VWEnchantments.getBenediction(playerWardenSlayer) && acquisition < 2;
            if (shouldGrant) {
                playerWardenSlayer.getInventory().add(new ItemStack(VWItems.GENESIS_RUNESTONE_PLATE));
                playerWardenSlayer.setAttached(PlayerAttachment.GENESIS_RUNESTONE_ACQUISITION_COUNT, acquisition + 1);

                sendParticles(ParticleTypes.EXPLOSION_EMITTER, level, playerWardenSlayer.blockPosition(), 12, 3);
                sendToChat(playerWardenSlayer, VWColors.VERDANT_WIND, false, "Verdant Wind's Benediction: You have been granted a Genesis Runestone Plate.");
            }
        }

        if (entity instanceof ElderGuardian guardian && attacker instanceof Player player) {
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
            if (entity.is(EntityTypes.WOLF) || entity.is(EntityTypes.VILLAGER) || entity.is(EntityTypes.WANDERING_TRADER)) {
                AtrocityManager.processAtrocity(player, entity, true);
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
            if (victim.is(EntityTypes.WOLF) || victim.is(EntityTypes.VILLAGER) || victim.is(EntityTypes.WANDERING_TRADER)) {
                AtrocityManager.processAtrocity(player, victim, false);
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

        if (attacker instanceof LivingEntity mob && mob.getAttachedOrElse(VWAttachment.FROST_BOUND_TIMER, 0) > 0) {
            return false;
        }

        if (attacker instanceof Player attackerPlayer && entity instanceof Player player) {
            if (PlayerAttachment.isTrusted(attackerPlayer, player)) return false;
        }

        if (entity instanceof Wolf wolf) {
            boolean isPeer = attacker instanceof Wolf peer && wolf.getOwner() == peer.getOwner();
            boolean isAccidentalDMG = wolf.getOwner() instanceof LivingEntity owner && attacker instanceof LivingEntity mob && owner == mob;
            boolean fromFireOrIce = source.is(DamageTypeTags.IS_FIRE) || source.is(DamageTypeTags.IS_FREEZING);

            if (isPeer || isAccidentalDMG) {
                return false;
            }

            if (fromFireOrIce && ArcticWindsEffect.negateFreezingOrFireDMG(wolf, source)) {
                return false;
            }

            boolean allow = VerdantWindBlessing.triggerBenediction(wolf, source, damage);
            if (!allow) retaliate(wolf, source);
            return allow;

        } else if (entity instanceof Player player) {
            boolean disableAccidentalDMG = attacker instanceof Wolf wolf && wolf.getOwner() == player;

            if (source.is(DamageTypeTags.IS_EXPLOSION) && VWItemBlessings.negateExplosionDamage(player)) {
                return false;

            } else if (disableAccidentalDMG) {
                return false;

            } else if (ArcticWindsEffect.negateFireDamageForOwner(player, (ServerLevel) player.level(), source, damage)) {
                return false;

            } else {
                boolean allow = VerdantWindBlessing.triggerBenediction(player, source, damage);
                if (!allow) retaliate(player, source);
                return allow;
            }

        } else {
            return true;
        }
    }


    private static boolean windVeilEffect(LivingEntity entity, DamageSource damageSource, float damage) {
        if (!(entity.level() instanceof ServerLevel level)) return true;
        if (entity instanceof Monster) return true;

        if (damageSource.getEntity() instanceof LivingEntity attacker
                && !attacker.is(EntityTypes.WOLF)
                && (entity instanceof Player || entity instanceof Wolf)) {
            RandomSource random = level.getRandom();
            DamageSource dmg = level.damageSources().magic();

            if (VWEnchantments.getBenediction(entity) && random.nextFloat() < 0.6f) {
                attacker.hurtServer(level, dmg, Math.max(2, attacker.getHealth() * 0.15f));
                if (attacker instanceof Player player && !player.getMainHandItem().isEmpty()) {
                    player.getCooldowns().addCooldown(player.getMainHandItem(), 60);
                }
                attacker.knockback(Math.max(1, attacker.getMaxHealth() * 0.05f), -attacker.getYHeadRot(), -entity.getYHeadRot(), dmg, 0);
                level.playSound(null, entity.blockPosition(), SoundEvents.THORNS_HIT, SoundSource.PLAYERS);
                for (int i = 0; i < random.nextIntBetweenInclusive(1, 5); i++) {
                    level.sendParticles(ParticleTypes.ENCHANT, entity.getX(), entity.getY(), entity.getZ(), 3, 0, 0, 0, 0);
                }
            }

            if (entity instanceof Player player && player.hasEffect(VWEffects.HAVOC)) {
                RunestoneEffects.getHavocForPlayer(player, attacker, damage);
            }
            retaliate(entity, damageSource);
        }
        return false;
    }
    private static void retaliate(LivingEntity victim, DamageSource source) {
        if (!(victim.level() instanceof ServerLevel level)) return;
        if (!(source.getEntity() instanceof LivingEntity attacker) || attacker.is(EntityTypes.WOLF)) return;

        if (victim instanceof Wolf wolf) {
            wolf.setTarget(attacker);

        } else if (victim instanceof Player player) {
            for (Wolf wolf : level.getEntities(EntityTypes.WOLF, scanArea(player, 16), w -> w.getOwner() == player)) {
                wolf.setTarget(attacker);
            }
        }
    }
    private static void updateNearbyWolvesOnDamage(Player player, Player attacker) {
        ServerLevel level = (ServerLevel) player.level();
        List<Wolf> wolves = level.getEntitiesOfClass(Wolf.class, scanArea(player, 16), wolf -> wolf.getOwner() == player);

        if (wolves.isEmpty()) return;

        for (Wolf wolf : wolves) {
            List<NameHolder> trustedPlayers = new ArrayList<>(WolfAttachment.getTrustedPlayers(wolf));
            UUID attackerUUID = attacker.getUUID();

            for (NameHolder trusted : trustedPlayers) {
                if (trusted.uuid().equals(attackerUUID)) {
                    trustedPlayers.remove(trusted);
                    wolf.setAttached(WolfAttachment.TRUSTED_PLAYERS, trustedPlayers);
                    sendToChat(player, VWColors.BLOODLUST_MUTED, false, wolf.getPlainTextName() + " no longer trust " + trusted.name());
                    break;
                }
            }
        }
    }
}
