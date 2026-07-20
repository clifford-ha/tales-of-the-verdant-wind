package cliffordha.totvw.entity;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.VWAttachments;
import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.VWEffects;
import cliffordha.totvw.tag.VWBiomeTags;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.player.Player;

import static cliffordha.totvw.entity.skill.VWSkillProcessor.notifyFromPlayer;

public final class VWGlobalEntityBehaviors {
    private static final Identifier VERDANT_OMEN_ID = Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verdant_omen");

    public static void register() {
        trust();
        applyVerdantOmen();
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
    }

    private static void atrocityProcessor(LivingEntity victim, DamageSource damageSource) {
        //if (victim == null) return;
        Entity directEntity = damageSource.getEntity();
        if (!(directEntity instanceof Player player)) return;
        if (!(player.level() instanceof ServerLevel)) return;

        if (victim instanceof Villager || victim instanceof WanderingTrader) {
            int current = player.getAttachedOrElse(VWAttachments.Player.PLAYER_VILLAGER_ATROCITY_COUNT, 0);
            int deduction = victim.isAlive() ? 3 : 12;
            player.setAttached(VWAttachments.Player.PLAYER_VILLAGER_ATROCITY_COUNT, current + deduction);
            int recount = player.getAttachedOrElse(VWAttachments.Player.PLAYER_VILLAGER_ATROCITY_COUNT, 0);
            notifyFromPlayer(player, VWColors.BLOODLUST_EFFECT_MUTED, true, "Atrocity count: " + recount);
        }
    }

    private static void applyVerdantOmen() {
        ServerTickEvents.END_SERVER_TICK.register((MinecraftServer server) -> {
            for (var level : server.getAllLevels()) {
                if (level.getGameTime() % 20 == 0) {
                    level.getAllEntities().forEach(entity -> {
                        if (entity instanceof Monster monster
                                && monster.getAttachedOrElse(VWAttachments.ENTITY_HAS_VERDANT_OMEN, false)) {
                        //if (entity instanceof Monster monster
                                //&& !monster.getAttachedOrElse(VWAttachments.ENTITY_HAS_VERDANT_OMEN, false)
                                //&& monster.level().getBiome(entity.blockPosition()).is(VWBiomeTags.IS_VERDANT_BIOMES)) {

                            float healthDecrease = monster.is(EntityType.WARDEN) ? 0.0f : -0.3f;
                            monster.getAttribute(Attributes.MAX_HEALTH).addOrReplacePermanentModifier(
                                    new AttributeModifier(
                                            VERDANT_OMEN_ID,
                                            healthDecrease,
                                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                                    )
                            );
                            monster.getAttribute(Attributes.ATTACK_DAMAGE).addOrReplacePermanentModifier(
                                    new AttributeModifier(
                                            VERDANT_OMEN_ID,
                                            -1,
                                            AttributeModifier.Operation.ADD_VALUE
                                    )
                            );
                            monster.getAttribute(Attributes.MOVEMENT_SPEED).addOrReplacePermanentModifier(
                                    new AttributeModifier(
                                            VERDANT_OMEN_ID,
                                            -0.25f,
                                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                                    )
                            );
                            monster.getAttribute(Attributes.ATTACK_KNOCKBACK).addOrReplacePermanentModifier(
                                    new AttributeModifier(
                                            VERDANT_OMEN_ID,
                                            -0.1f,
                                            AttributeModifier.Operation.ADD_VALUE
                                    )
                            );
                            //monster.setAttached(VWAttachments.ENTITY_HAS_VERDANT_OMEN, true);
                        }
                    });
                }
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
                    notifyFromPlayer(player, VWColors.GRAY, victim.getName().getString() + " has lost trust");
                }
            }
        }
    }

    private VWGlobalEntityBehaviors() {}
}
