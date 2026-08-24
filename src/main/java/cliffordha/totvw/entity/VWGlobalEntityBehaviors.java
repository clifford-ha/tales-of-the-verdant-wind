package cliffordha.totvw.entity;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.entity.player.VWPlayerBehaviors;
import cliffordha.totvw.entity.wolf.VWWolfBehaviors;
import cliffordha.totvw.registry.*;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

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
                    if (!player.getAttachedOrElse(VWAttachments.Player.PLAYER_IS_DEV_MODE, false)) {
                        player.setAttached(VWAttachments.Player.PLAYER_IS_DEV_MODE, true);
                    }
                });
            }
        });
    }

    private static void onDamageOrDeathEvent() {
        ServerLivingEntityEvents.AFTER_DEATH.register(
                (victim, damageSource) -> atrocityProcessor(victim, damageSource, true)
        );
        ServerLivingEntityEvents.AFTER_DAMAGE.register(
                (victim, damageSource, _, _, _) -> atrocityProcessor(victim, damageSource, false)
        );
        ServerLivingEntityEvents.ALLOW_DEATH.register(
                VWGlobalEntityBehaviors::revivePlayerByProxy
        );
    }

    private static boolean revivePlayerByProxy(LivingEntity entity, DamageSource damageSource, float v) {
        if (!VWConfig.get().SERVER_WOLF_SHARES_BENEDICTION_STACK) return true;
        if (entity instanceof Player player) {
            if (damageSource.is(DamageTypes.GENERIC_KILL)) return true;
            Level getLevel = player.level();
            ServerLevel level = (ServerLevel) getLevel;
            double distance = VWConfig.get().SERVER_WOLF_PLAYER_SCAN_DISTANCE * 16;

            AttachmentType<Integer> BENEDICTION_STACK = VWAttachments.Wolf.WOLF_BENEDICTION;

            List<Wolf> wolves = level.getEntities(EntityTypes.WOLF, player.getBoundingBox().inflate(distance), wolf ->
                    wolf.getOwner() != null && wolf.getOwner().is(player) && wolf.getAttachedOrElse(BENEDICTION_STACK, 0) > 1);
            if (wolves.isEmpty()) return true;

            int random = wolves.size() == 1 ? 0 : level.getRandom().nextIntBetweenInclusive(0, wolves.size() - 1);
            Wolf wolf = wolves.get(Math.max(random, 0));

            int benediction = wolf.getAttachedOrElse(BENEDICTION_STACK, 0);

            player.setHealth(player.getMaxHealth() * 0.5f);
            player.removeAllEffects();

            addEffect(player, MobEffects.RESISTANCE, 20 * 3, 255);
            addEffect(player, VWEffects.BLESSING_OF_THE_VERDANT_WIND, 20 * 10, 2);
            addEffect(player, MobEffects.ABSORPTION, 20 * 10, 2);

            wolf.setAttached(BENEDICTION_STACK, benediction - 1);
            if (VWConfig.get().SERVER_TELEPORT_AFTER_SAVE) {
                wolf.dropLeash();
                wolf.unRide();
                wolf.setOrderedToSit(false);

                if (player.distanceTo(wolf) > 32) {
                    player.teleportTo(wolf.getX(), wolf.getY() + 1, wolf.getZ());
                } else if (player.distanceTo(wolf) > 4) {
                    wolf.teleportToAroundBlockPos(player.blockPosition());
                }
            }
            String name = wolf.getPlainTextName();
            int STACK_AFTER = wolf.getAttachedOrElse(BENEDICTION_STACK, 0);
            if (STACK_AFTER == 0) {
                sendToChat(wolf, VWColors.BLOODLUST_EFFECT_MUTED, name + " used up all Benediction stacks");
            } else {
                sendToChat(wolf, VWColors.VERDANT_WIND_MUTED,STACK_AFTER + " Benediction stack remaining for " + name);
            }

            wolf.makeSound(new SoundEvent(Identifier.withDefaultNamespace("entity.wolf.whine"), Optional.of(16.0f)));
            level.broadcastEntityEvent(player, (byte) 35);
            return false;
        }
        return true;
    }

    private static void atrocityProcessor(LivingEntity victim, DamageSource damageSource, boolean death) {
        if (victim == null) return;

        if (death && victim instanceof Player player) {
            if (!(player.level() instanceof ServerLevel)) return;
            player.removeAttached(VWAttachments.Player.PLAYER_WOLF_ATROCITY_COUNT);
            player.removeAttached(VWAttachments.Player.PLAYER_VILLAGER_ATROCITY_COUNT);
            player.removeAttached(VWAttachments.Player.PLAYER_RECEIVED_ENCHANTMENTS_HANDBOOK);
            return;
        }

        Entity attacker = damageSource.getEntity();
        if (!(attacker instanceof Player player)) return;
        if (!(player.level() instanceof ServerLevel level)) return;

        float multiplier = setDifficultyBasedValue(level, 0.5f, 0.75f, 1.0f, 2.0f);

        int maybeAddMore = level.getRandom().nextIntBetweenInclusive(0, 3);
        int deduction = Mth.ceil(3 * multiplier) + maybeAddMore;
        int finalDeduction = death ? deduction * 4 : deduction;

        if (victim instanceof Wolf wolf) {
            AttachmentType<Integer> WOLF_COUNTER = VWAttachments.Player.PLAYER_WOLF_ATROCITY_COUNT;
            boolean maybeForgive = wolf.getOwner() != null && wolf.getOwner().is(player) && level.getRandom().nextBoolean();
            if (maybeForgive) return;

            int current = player.getAttachedOrElse(WOLF_COUNTER, 0);
            player.setAttached(WOLF_COUNTER, current + finalDeduction);

            if (!VWConfig.get().CLIENT_SHOW_ATROCITY_COUNTER) return;
            sendToChat(player, VWColors.BLOODLUST_EFFECT_MUTED, true, "Wolf atrocity count: " + player.getAttachedOrElse(WOLF_COUNTER, 0));
        } else if (victim instanceof Villager || victim instanceof WanderingTrader) {
            AttachmentType<Integer> VILLAGER_COUNTER = VWAttachments.Player.PLAYER_VILLAGER_ATROCITY_COUNT;

            int current = player.getAttachedOrElse(VILLAGER_COUNTER, 0);
            player.setAttached(VILLAGER_COUNTER, current + finalDeduction);

            if (!VWConfig.get().CLIENT_SHOW_ATROCITY_COUNTER) return;
            sendToChat(player, VWColors.BLOODLUST_EFFECT_MUTED, true, "Villager atrocity count: " + player.getAttachedOrElse(VILLAGER_COUNTER, 0));
        }
    }

    private VWGlobalEntityBehaviors() {}
}
