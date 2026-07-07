package cliffordha.totvw.entity;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.entity.player.InteractionData;
import cliffordha.totvw.registry.ModAttachments;
import cliffordha.totvw.registry.ModColors;
import cliffordha.totvw.registry.ModEffects;
import cliffordha.totvw.tag.ModBiomeTags;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

import static cliffordha.totvw.entity.skill.ConfigTools.notifyFromPlayer;

public final class ModEntityOtherBehaviors {
    private static final Identifier VERDANT_OMEN_ID = Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verdant_omen");

    public static void register() {
        trust();
        applyVerdanOmen();
        removeVerdantBlessings();
    }

    private static void removeVerdantBlessings() {
        ServerTickEvents.END_SERVER_TICK.register((MinecraftServer server) -> {
            for (var serverLevel : server.getAllLevels()) {
                serverLevel.getAllEntities().forEach(entity -> {
                    if (entity instanceof Monster monster) {
                        if (monster.hasEffect(ModEffects.BLESSING_OF_THE_VERDANT_WIND)) {
                            float damage = (monster.getMaxHealth() * 0.15f) * (1 + monster.getEffect(ModEffects.BLESSING_OF_THE_VERDANT_WIND).getAmplifier());
                            monster.hurtServer(serverLevel, serverLevel.damageSources().magic(), damage);
                            monster.removeEffect(ModEffects.BLESSING_OF_THE_VERDANT_WIND);
                        }
                    }
                });
            }
        });
    }

    private static void applyVerdanOmen() {
        ServerTickEvents.END_SERVER_TICK.register((MinecraftServer server) -> {
            for (var serverLevel : server.getAllLevels()) {
                serverLevel.getAllEntities().forEach(entity -> {
                    if (!entity.getAttachedOrElse(ModAttachments.ENTITY_HAS_VERDANT_OMEN, false)) {
                        if (!entity.level().getBiome(entity.blockPosition()).is(ModBiomeTags.IS_VERDANT_BIOMES)) return;
                        if (entity instanceof Enemy enemy && enemy instanceof Monster monster) {

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
                            monster.setAttached(ModAttachments.ENTITY_HAS_VERDANT_OMEN, true);
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

            // when trustee attacks the confidant
            String victimConfidant = victim.getName().getString() + victim.getStringUUID();
            String trusteeAttacker = "-" + attacker.getName().getString() + attacker.getStringUUID();

            // when confidant attacks the trustee
            String confidant = attacker.getName().getString() + attacker.getStringUUID();
            String trustee = "-" + victim.getName().getString() + victim.getStringUUID();

            int TRUSTEE_POINTS = attacker.getAttachedOrElse(ModAttachments.ENTITY_TRUST_POINTS, 0);
            int CONFIDANT_POINTS = victim.getAttachedOrElse(ModAttachments.ENTITY_TRUST_POINTS, 0);

            boolean breakTrusteeTrust = attacker.hasAttached(ModAttachments.ENTITY_INTERACTION_DATA) && Objects.equals(attacker.getAttached(ModAttachments.ENTITY_INTERACTION_DATA), new InteractionData(victimConfidant, trusteeAttacker));
            boolean breakConfidantTrust = victim.hasAttached(ModAttachments.ENTITY_INTERACTION_DATA) && Objects.equals(victim.getAttached(ModAttachments.ENTITY_INTERACTION_DATA), new InteractionData(confidant, trustee));

            processTrust(victim, breakTrusteeTrust, TRUSTEE_POINTS, 0, damageSource);
            processTrust(victim, breakConfidantTrust, CONFIDANT_POINTS, 1, damageSource);
        });
    }

    private static void processTrust(LivingEntity victim, boolean who, int trustPoints, int config, DamageSource damageSource) {
        Entity attacker = damageSource.getEntity();
        if (attacker == null) return;
        if (!(victim.level() instanceof ServerLevel)) return;

        if (who) {
            if (trustPoints > 0) {
                if (config == 0) {
                    attacker.setAttached(ModAttachments.ENTITY_TRUST_POINTS, trustPoints - 1);
                } else {
                    victim.setAttached(ModAttachments.ENTITY_TRUST_POINTS, trustPoints - 1);
                }
            } else {
                if (config == 0) {
                    attacker.removeAttached(ModAttachments.ENTITY_INTERACTION_DATA);
                    attacker.removeAttached(ModAttachments.ENTITY_TRUST_POINTS);
                } else {
                    victim.removeAttached(ModAttachments.ENTITY_INTERACTION_DATA);
                    victim.removeAttached(ModAttachments.ENTITY_TRUST_POINTS);
                }
                if (attacker instanceof Player player) {
                    notifyFromPlayer(player, ModColors.GRAY,
                            victim.getName().getString() + " has lost trust");
                }
            }
        }
    }

    private ModEntityOtherBehaviors() {}
}
