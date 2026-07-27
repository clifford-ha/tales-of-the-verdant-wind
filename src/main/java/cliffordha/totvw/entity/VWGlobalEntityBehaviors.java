package cliffordha.totvw.entity;

import cliffordha.totvw.entity.skill.VWSkillProcessor;
import cliffordha.totvw.registry.*;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

import static cliffordha.totvw.entity.skill.VWSkillProcessor.sendToChat;
import static cliffordha.totvw.util.VWGlobalUtil.addEffect;

public final class VWGlobalEntityBehaviors {
    public static void register() {
        trust();
        onDamageEvent();
    }

    private static void onDamageEvent() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register(
                (victim, damageSource, _, _, _) ->
                        atrocityProcessor(victim, damageSource)
        );
        ServerLivingEntityEvents.AFTER_DEATH.register(
                VWGlobalEntityBehaviors::atrocityProcessor
        );
        ServerLivingEntityEvents.ALLOW_DEATH.register(
                VWGlobalEntityBehaviors::savePlayerFromDamnation
        );
    }

    private static boolean savePlayerFromDamnation(LivingEntity entity, DamageSource damageSource, float v) {
        if (entity instanceof Player player) {
            Level var = player.level();
            ServerLevel level = (ServerLevel) var;
            List<Wolf> wolves = level.getEntities(EntityType.WOLF, player.getBoundingBox().inflate(128), wolf ->
                    wolf.getOwner() != null && wolf.getOwner().is(player) && wolf.getAttachedOrElse(VWAttachments.Wolf.WOLF_BENEDICTION, 0) > 1);
            if (wolves.isEmpty()) return true;
            Wolf wolf = wolves.getFirst();

            int benediction = wolf.getAttachedOrElse(VWAttachments.Wolf.WOLF_BENEDICTION, 0);
            player.setHealth(player.getMaxHealth() * 0.5f);
            player.removeAllEffects();

            addEffect(player, MobEffects.RESISTANCE, 20 * 3, 255);
            addEffect(player, VWEffects.BLESSING_OF_THE_VERDANT_WIND, 20 * 10, 2);
            addEffect(player, MobEffects.ABSORPTION, 20 * 10, 2);

            wolf.setAttached(VWAttachments.Wolf.WOLF_BENEDICTION, benediction - 1);

            if (player.canTeleport(player.level(), wolf.level()) || wolf.canTeleport(wolf.level(), player.level())) {
                if (player.distanceTo(wolf) > 16) {
                    player.teleportTo(wolf.getX(), wolf.getY() + 1, wolf.getZ());
                } else {
                    wolf.dropLeash();
                    wolf.setOrderedToSit(false);
                    wolf.tryToTeleportToOwner();
                }
            }

            wolf.makeSound(new SoundEvent(Identifier.withDefaultNamespace("entity.wolf.whine"), Optional.of(16.0f)));
            level.broadcastEntityEvent(player, (byte) 35);
            return false;
        }
        return true;
    }

    private static void atrocityProcessor(LivingEntity victim, DamageSource damageSource) {
        if (victim == null) return;
        Entity directEntity = damageSource.getEntity();
        if (!(directEntity instanceof Player player)) return;
        if (!(player.level() instanceof ServerLevel level)) return;

        float multiplier;
        Difficulty difficulty = level.getDifficulty();
        switch (difficulty) {
            case PEACEFUL -> multiplier = 0.5f;
            case EASY -> multiplier = 0.75f;
            case HARD -> multiplier = 2.0f;
            default -> multiplier = 1.0f;
        }
        int maybeAddMore = level.getRandom().nextIntBetweenInclusive(0, 3);
        int deduction = Mth.ceil(Math.max(3 * multiplier, 0)) + maybeAddMore;

        if (victim instanceof Wolf wolf) {
            AttachmentType<Integer> WOLF_COUNTER = VWAttachments.Player.PLAYER_WOLF_ATROCITY_COUNT;
            boolean maybeForgive = wolf.getOwner() != null && wolf.getOwner().is(player) && level.getRandom().nextBoolean();
            if (maybeForgive) return;

            int current = player.getAttachedOrElse(WOLF_COUNTER, 0);
            player.setAttached(WOLF_COUNTER, current + deduction);
            sendToChat(player, VWColors.BLOODLUST_EFFECT_MUTED, true, "Atrocity count: " + player.getAttachedOrElse(WOLF_COUNTER, 0));
        } else if (victim instanceof Villager || victim instanceof WanderingTrader) {
            AttachmentType<Integer> VILLAGER_COUNTER = VWAttachments.Player.PLAYER_VILLAGER_ATROCITY_COUNT;

            int current = player.getAttachedOrElse(VILLAGER_COUNTER, 0);
            player.setAttached(VILLAGER_COUNTER, current + deduction);
            sendToChat(player, VWColors.BLOODLUST_EFFECT_MUTED, true, "Atrocity count: " + player.getAttachedOrElse(VILLAGER_COUNTER, 0));
        }
    }

    private static void applyVerdantOmen() {
        ServerTickEvents.END_SERVER_TICK.register((MinecraftServer server) -> {
            for (var level : server.getAllLevels()) {
                level.getAllEntities().forEach(entity -> {
                    if (entity instanceof Monster monster) {
                        if (monster.hasEffect(VWEffects.BLESSING_OF_THE_VERDANT_WIND)) {
                            float damage = (monster.getMaxHealth() * 0.15f) * (1 + monster.getEffect(VWEffects.BLESSING_OF_THE_VERDANT_WIND).getAmplifier());
                            monster.hurtServer(level, level.damageSources().magic(), damage);
                            monster.removeEffect(VWEffects.BLESSING_OF_THE_VERDANT_WIND);
                        }
                    }
                });
            }
        });
    }

    private static void trust() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register((victim, damageSource, _, _, _) -> {
            Entity attacker = damageSource.getEntity();
            if (attacker == null) return;
            if (!(victim.level() instanceof ServerLevel)) return;

            String thisMob = attacker.getStringUUID();
            String interactedWith = victim.getStringUUID();

            String reThisMob = victim.getStringUUID();
            String reInteractedWith = attacker.getStringUUID();

            int TRUSTEE_POINTS = attacker.getAttachedOrElse(VWAttachments.ENTITY_TRUST_POINTS, 0);
            int CONFIDANT_POINTS = victim.getAttachedOrElse(VWAttachments.ENTITY_TRUST_POINTS, 0);

            VWTrustInteractionData empty = new VWTrustInteractionData("", "");
            VWTrustInteractionData data = new VWTrustInteractionData(thisMob, interactedWith);
            VWTrustInteractionData reData = new VWTrustInteractionData(reThisMob, reInteractedWith);

            boolean checkAttacker = attacker.getAttachedOrElse(VWAttachments.ENTITY_TRUSTED_MOB_DATA, empty).equals(reData);
            boolean checkMob = victim.getAttachedOrElse(VWAttachments.ENTITY_TRUSTED_MOB_DATA, empty).equals(data);

            processTrust(victim, checkAttacker, TRUSTEE_POINTS, 0, damageSource);
            processTrust(victim, checkMob, CONFIDANT_POINTS, 1, damageSource);
        });
    }

    private static void processTrust(LivingEntity victim, boolean attachment, int trustPoints, int config, DamageSource damageSource) {
        Entity attacker = damageSource.getEntity();
        if (attacker == null) return;
        if (!(victim.level() instanceof ServerLevel)) return;

        if (attachment) {
            if (trustPoints > 0) {
                if (config == 0) {
                    attacker.setAttached(VWAttachments.ENTITY_TRUST_POINTS, trustPoints - 1);
                } else {
                    victim.setAttached(VWAttachments.ENTITY_TRUST_POINTS, trustPoints - 1);
                }
            } else {
                if (config == 0) {
                    attacker.removeAttached(VWAttachments.ENTITY_TRUSTED_MOB_DATA);
                    attacker.removeAttached(VWAttachments.ENTITY_TRUST_POINTS);
                } else {
                    victim.removeAttached(VWAttachments.ENTITY_TRUSTED_MOB_DATA);
                    victim.removeAttached(VWAttachments.ENTITY_TRUST_POINTS);
                }
                if (attacker instanceof Player player) {
                    VWSkillProcessor.sendToChat(player, VWColors.GRAY, victim.getName().getString() + " has lost trust");
                }
            }
        }
    }

    private VWGlobalEntityBehaviors() {}
}
