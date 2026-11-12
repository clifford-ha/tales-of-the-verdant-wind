package cliffordha.totvw.entity;

import cliffordha.totvw.effect.HavocEffect;
import cliffordha.totvw.enchantment.ArcticWindsEffect;
import cliffordha.totvw.enchantment.WitheringEffect;
import cliffordha.totvw.entity.events.InteractionEvent;
import cliffordha.totvw.entity.player.AtrocityManager;
import cliffordha.totvw.entity.skills.*;
import cliffordha.totvw.entity.player.VWPlayerBehaviors;
import cliffordha.totvw.entity.wolf.VWWolfBehaviors;
import cliffordha.totvw.item.events.VWItemBlessings;
import cliffordha.totvw.networking.VWNetworking;
import cliffordha.totvw.registry.*;
import cliffordha.totvw.registry.attachments.HavocData;
import cliffordha.totvw.registry.attachments.HavocType;
import cliffordha.totvw.registry.attachments.VWAttachment;
import cliffordha.totvw.registry.attachments.WolfFragmentData;
import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import cliffordha.totvw.tag.VWBiomeTags;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.entity.event.v1.effect.EffectEventContext;
import net.fabricmc.fabric.api.entity.event.v1.effect.ServerMobEffectEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
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

import java.util.List;
import java.util.function.Predicate;

import static cliffordha.totvw.TOTVW.sendClassRegisterLog;
import static cliffordha.totvw.util.ExtUtil.*;

public class GlobalEntityEvents {
    public static void register() {
        registerGlobalEvents();

        VWPlayerBehaviors.registerModPlayerBehaviors();
        VWWolfBehaviors.registerModWolfBehaviors();
        sendClassRegisterLog("Entity Events & Behaviors");
    }

    private static void registerGlobalEvents() {
        ServerLifecycleEvents.SERVER_STARTED.register(GlobalEntityEvents::onServerStarted);
        ServerLevelEvents.UNLOAD.register(GlobalEntityEvents::onLevelUnload);

        UseEntityCallback.EVENT.register( (player, level, hand, entity, _)
                -> InteractionEvent.onEntityInteractEvent(player, entity, hand));

        UseItemCallback.EVENT.register((player, level, _)
                -> InteractionEvent.onUseItemEvent(player));

        ServerEntityEvents.EQUIPMENT_CHANGE.register(GlobalEntityEvents::onEquipmentChanged);
        ServerMobEffectEvents.AFTER_REMOVE.register(GlobalEntityEvents::onMobEffectsRemoved);

        ServerLivingEntityEvents.ALLOW_DAMAGE.register(GlobalEntityEvents::allowDamageEvent);
        ServerLivingEntityEvents.AFTER_DAMAGE.register(
                (victim, damageSource, _, damage, _)
                        -> afterDamageEvent(victim, damageSource, damage));

        ServerLivingEntityEvents.ALLOW_DEATH.register(RevivalByProxy::allowPlayerDeath);
        ServerLivingEntityEvents.AFTER_DEATH.register(GlobalEntityEvents::afterDeathEvent);
        ServerLivingEntityEvents.AFTER_DEATH.register(RunestoneEffects::reapplyLinkStatus);

        ServerPlayerEvents.AFTER_RESPAWN.register(GlobalEntityEvents::afterRespawnEvent);
    }

    private static void onLevelUnload(MinecraftServer server, ServerLevel level) {
        WitheringEffect.unloadPreviouslySummoned(level);
    }

    private static void onServerStarted(MinecraftServer server) {
        for (ServerLevel level : server.getAllLevels()) {
            WitheringEffect.unloadPreviouslySummoned(level);
        }
    }

    private static void onEquipmentChanged(LivingEntity entity, EquipmentSlot equipmentSlot, ItemStack stack, ItemStack stack1) {
        if (!(entity.level() instanceof ServerLevel level)) return;

        if (entity instanceof Wolf wolf) {
            VWNetworking.syncWolfArmor(wolf);
        } else if (entity instanceof Player player) VWNetworking.syncPlayerArmor(player);
    }

    private static void afterRespawnEvent(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
        if (alive) {
            WolfBenedictionHelper.loadWolfFilesIfPresent(newPlayer, newPlayer.level());
        }
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
        } else if (entity instanceof Wolf wolf) VWNetworking.syncWolfEffects(wolf);
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
            if (entity.is(EntityType.WOLF) || entity.is(EntityType.VILLAGER) || entity.is(EntityType.WANDERING_TRADER)) {
                AtrocityManager.processAtrocity(player, entity, true);
            }
        }
        if (entity instanceof Player player) {
            if (attacker instanceof Player attackerPlayer) {
                updateNearbyWolvesOnDamage(player, attackerPlayer, true);
            }

            HavocType type = HavocData.getType(player);
            boolean hasType = HavocType.isAnnihilation(player) || HavocType.isVoid(player);
            boolean havocPenalty = player.hasEffect(VWEffects.HAVOC) && hasType;

            if (havocPenalty && attacker instanceof LivingEntity getAttacker) {
                RunestoneEffects.triggerHavocPenalty(getAttacker, type);
            }
        }
    }
    private static void afterDamageEvent(LivingEntity victim, DamageSource damageSource, float damage) {
        if (victim == null) return;
        Entity attacker = damageSource.getEntity();

        if (attacker instanceof Player player) {
            if (victim.is(EntityType.WOLF) || victim.is(EntityType.VILLAGER) || victim.is(EntityType.WANDERING_TRADER)) {
                AtrocityManager.processAtrocity(player, victim, false);
            }
        }
        if (victim instanceof Player player) {
            if (attacker instanceof Player attackerPlayer) {
                updateNearbyWolvesOnDamage(player, attackerPlayer, false);
                PlayerAttachment.removeTrustedPlayer(player, attackerPlayer);
                PlayerAttachment.removeTrustedPlayer(attackerPlayer, player);
            }
        }
        if (victim instanceof Wolf wolf && attacker instanceof Player player) {
            if (wolf.getOwner() == player) return;

            if (wolf.isTame() && wolf.getOwner() != null && wolf.getOwner() != player) {
                WolfAttachment.addPlayerToAggressors(wolf, player);
            }
        }
    }
    private static boolean allowDamageEvent(LivingEntity entity, DamageSource source, float damage) {
        Entity attacker = source.getEntity();

        if (entity.hasEffect(VWEffects.WIND_VEIL)) {
            return windVeilEffect(entity, source, damage);
        }

        boolean attackerIsLive = attacker instanceof LivingEntity;

        if (attackerIsLive) {
            if (ArcticWindsEffect.frostBoundStack(attacker) > 0) return false;
            if (attacker.getAttachedOrElse(VWAttachment.LIFT_DURATION, 0) > 0) return false;
        }

        if (entity instanceof Wolf wolf) {
            boolean isPeer = attacker instanceof Wolf peer && wolf.getOwner() == peer.getOwner();
            boolean isAccidentalDMG = wolf.getOwner() instanceof LivingEntity owner && attacker instanceof LivingEntity mob && owner == mob;
            boolean fromFireOrIce = source.is(DamageTypeTags.IS_FIRE) || source.is(DamageTypeTags.IS_FREEZING);

            if (isPeer || isAccidentalDMG) {
                return false;
            }

            if (wolf.hasAttached(WolfAttachment.FRAGMENT_DATA)) {
                if (!(attacker instanceof LivingEntity)) return false;
                if (WolfFragmentData.getHits(wolf) <= 0) {
                    WitheringEffect.unloadFragmentedWolf((ServerLevel) wolf.level(), wolf);
                    return false;
                }
                WolfFragmentData.decrementHit(wolf);
                return false;
            }

            if (attacker instanceof Player player) {
                if (wolf.getOwner() instanceof Player owner && PlayerAttachment.isTrusted(owner, player)) {
                    return false;

                } else {
                    return !WolfAttachment.isTrusted(wolf, player);
                }
            }

            if (fromFireOrIce && ArcticWindsEffect.negateFreezingOrFireDMG(wolf, source)) {
                return false;
            }

            boolean allow = VerdantWindBlessing.triggerBenediction(wolf, source, damage);
            if (!allow) retaliate(wolf, source);
            return allow;

        } else if (entity instanceof Player player) {
            boolean disableAccidentalDMG = attacker instanceof Wolf wolf && wolf.getOwner() == player;

            if (attacker instanceof Player other && PlayerAttachment.isTrusted(player, other)) {
                return false;
            }

            if (attacker instanceof Wolf wolf && WolfAttachment.isTrusted(wolf, player)) {
                return false;
            }

            if (source.is(DamageTypeTags.IS_EXPLOSION) && VWItemBlessings.negateExplosionDamage(player)) {
                return false;

            } else if (disableAccidentalDMG) {
                return false;

            } else if (source.is(DamageTypeTags.IS_FIRE) && ArcticWindsEffect.negateFireDamageForOwner(player, (ServerLevel) player.level(), source, damage)) {
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
                && !attacker.is(EntityType.WOLF)
                && (entity instanceof Player || entity instanceof Wolf)) {
            RandomSource random = level.getRandom();
            DamageSource dmg = level.damageSources().magic();

            if (VWEnchantments.getBenediction(entity) && random.nextFloat() < 0.6f) {
                attacker.hurtServer(level, dmg, Math.max(2, attacker.getHealth() * 0.15f));
                if (attacker instanceof Player player && !player.getMainHandItem().isEmpty()) {
                    player.getCooldowns().addCooldown(player.getMainHandItem(), 60);
                }
                attacker.knockback(Math.max(1, attacker.getMaxHealth() * 0.05f), -attacker.getYHeadRot(), -entity.getYHeadRot());
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
        if (!(source.getEntity() instanceof LivingEntity attacker) || attacker.is(EntityType.WOLF)) return;

        if (victim instanceof Wolf wolf) {
            wolf.setTarget(attacker);

        } else if (victim instanceof Player player) {
            for (Wolf wolf : level.getEntities(EntityType.WOLF, scanArea(player, 16), w -> w.getOwner() == player)) {
                wolf.setTarget(attacker);
            }
        }
    }
    private static void updateNearbyWolvesOnDamage(Player player, Player attacker, boolean isDeath) {
        if (!(player.level() instanceof ServerLevel level)) return;

        if (isDeath) {
            for (ServerLevel serverLevel : level.getServer().getAllLevels()) {
                serverLevel.getAllEntities().forEach(e -> {
                    if (e instanceof Wolf wolf && wolf.getOwner() == player) {
                        WolfAttachment.removeTrustedPlayer(wolf, attacker);
                    }
                });
            }
            return;
        }

        List<Wolf> wolves = level.getEntitiesOfClass(Wolf.class, scanArea(player, 16), wolf -> wolf.getOwner() == player);
        if (wolves.isEmpty()) return;

        for (Wolf wolf : wolves) {
            WolfAttachment.removeTrustedPlayer(wolf, attacker);
        }
    }
}
