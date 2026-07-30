package cliffordha.totvw.entity;

import cliffordha.totvw.config.TOTVWConfig;
import cliffordha.totvw.entity.player.VWPlayerBehaviors;
import cliffordha.totvw.entity.wolf.VWWolfBehaviors;
import cliffordha.totvw.registry.*;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
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
import static cliffordha.totvw.entity.skill.VWSkillProcessor.sendToChat;
import static cliffordha.totvw.util.VWUtil.addEffect;

public class VWGlobalEntityBehaviors {
    public static void register() {
        onDamageEvent();

        VWPlayerBehaviors.registerModPlayerBehaviors();
        VWWolfBehaviors.registerModWolfBehaviors();
        sendClassRegisterLog("Custom Entity Behaviors");
    }

    private static void onDamageEvent() {
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
        if (!TOTVWConfig.get().SERVER_WOLF_SHARES_BENEDICTION_STACK) return true;
        if (entity instanceof Player player) {
            if (player.isCreative() && damageSource.is(DamageTypes.GENERIC_KILL)) return true;
            Level getLevel = player.level();
            ServerLevel level = (ServerLevel) getLevel;
            double distance = TOTVWConfig.get().SERVER_WOLF_PLAYER_SCAN_DISTANCE * 16;

            List<Wolf> wolves = level.getEntities(EntityType.WOLF, player.getBoundingBox().inflate(distance), wolf ->
                    wolf.getOwner() != null && wolf.getOwner().is(player) && wolf.getAttachedOrElse(VWAttachments.Wolf.WOLF_BENEDICTION, 0) > 1);
            if (wolves.isEmpty()) return true;

            int random = wolves.size() == 1 ? 0 : level.getRandom().nextIntBetweenInclusive(0, wolves.size() - 1);
            Wolf wolf = wolves.get(Math.max(random, 0));

            int benediction = wolf.getAttachedOrElse(VWAttachments.Wolf.WOLF_BENEDICTION, 0);

            player.setHealth(player.getMaxHealth() * 0.5f);
            player.removeAllEffects();

            addEffect(player, MobEffects.RESISTANCE, 20 * 3, 255);
            addEffect(player, VWEffects.BLESSING_OF_THE_VERDANT_WIND, 20 * 10, 2);
            addEffect(player, MobEffects.ABSORPTION, 20 * 10, 2);

            wolf.setAttached(VWAttachments.Wolf.WOLF_BENEDICTION, benediction - 1);
            if (TOTVWConfig.get().SERVER_TELEPORT_AFTER_SAVE) {
                wolf.dropLeash();
                wolf.unRide();
                wolf.setOrderedToSit(false);

                if (player.distanceTo(wolf) > 16) {
                    player.teleportTo(wolf.getX(), wolf.getY() + 1, wolf.getZ());
                } else {
                    wolf.tryToTeleportToOwner();
                }
            }

            wolf.makeSound(new SoundEvent(Identifier.withDefaultNamespace("entity.wolf.whine"), Optional.of(16.0f)));
            level.broadcastEntityEvent(player, (byte) 35);
            return false;
        }
        return true;
    }

    private static void atrocityProcessor(LivingEntity victim, DamageSource damageSource, boolean death) {
        if (victim == null) return;

        if (victim instanceof Player player) {
            if (!(player.level() instanceof ServerLevel)) return;
            player.removeAttached(VWAttachments.Player.WOLF_ATROCITY_COUNT);
            player.removeAttached(VWAttachments.Player.PLAYER_VILLAGER_ATROCITY_COUNT);
            return;
        }

        Entity attacker = damageSource.getEntity();
        if (!(attacker instanceof Player player)) return;
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
        int deduction = Mth.ceil(3 * multiplier) + maybeAddMore;
        int finalDeduction = death ? deduction * 4 : deduction;

        if (victim instanceof Wolf wolf) {
            AttachmentType<Integer> WOLF_COUNTER = VWAttachments.Player.WOLF_ATROCITY_COUNT;
            boolean maybeForgive = wolf.getOwner() != null && wolf.getOwner().is(player) && level.getRandom().nextBoolean();
            if (maybeForgive) return;

            int current = player.getAttachedOrElse(WOLF_COUNTER, 0);
            player.setAttached(WOLF_COUNTER, current + finalDeduction);
            sendToChat(player, VWColors.BLOODLUST_EFFECT_MUTED, true, "Wolf atrocity count: " + player.getAttachedOrElse(WOLF_COUNTER, 0));
        } else if (victim instanceof Villager || victim instanceof WanderingTrader) {
            AttachmentType<Integer> VILLAGER_COUNTER = VWAttachments.Player.PLAYER_VILLAGER_ATROCITY_COUNT;

            int current = player.getAttachedOrElse(VILLAGER_COUNTER, 0);
            player.setAttached(VILLAGER_COUNTER, current + finalDeduction);
            sendToChat(player, VWColors.BLOODLUST_EFFECT_MUTED, true, "Villager atrocity count: " + player.getAttachedOrElse(VILLAGER_COUNTER, 0));
        }
    }

    private VWGlobalEntityBehaviors() {}
}
