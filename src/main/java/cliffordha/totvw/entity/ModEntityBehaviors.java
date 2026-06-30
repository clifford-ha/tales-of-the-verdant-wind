package cliffordha.totvw.entity;

import cliffordha.totvw.entity.player.InteractionData;
import cliffordha.totvw.registry.ModAttachments;
import cliffordha.totvw.registry.ModColors;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

import static cliffordha.totvw.entity.skill.ConfigTools.notifyFromPlayer;

public final class ModEntityBehaviors {
    public static void register() {
        initializeTrust();
    }

    private static void initializeTrust() {
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

            int TRUSTEE_POINTS = attacker.getAttachedOrElse(ModAttachments.TRUST_POINTS, 0);
            int CONFIDANT_POINTS = victim.getAttachedOrElse(ModAttachments.TRUST_POINTS, 0);

            boolean breakTrusteeTrust = attacker.hasAttached(ModAttachments.INTERACTION_DATA) && Objects.equals(attacker.getAttached(ModAttachments.INTERACTION_DATA), new InteractionData(victimConfidant, trusteeAttacker));
            boolean breakConfidantTrust = victim.hasAttached(ModAttachments.INTERACTION_DATA) && Objects.equals(victim.getAttached(ModAttachments.INTERACTION_DATA), new InteractionData(confidant, trustee));

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
                    attacker.setAttached(ModAttachments.TRUST_POINTS, trustPoints - 1);
                } else {
                    victim.setAttached(ModAttachments.TRUST_POINTS, trustPoints - 1);
                }
            } else {
                if (config == 0) {
                    attacker.removeAttached(ModAttachments.INTERACTION_DATA);
                    attacker.removeAttached(ModAttachments.TRUST_POINTS);
                } else {
                    victim.removeAttached(ModAttachments.INTERACTION_DATA);
                    victim.removeAttached(ModAttachments.TRUST_POINTS);
                }
                if (attacker instanceof Player player) {
                    notifyFromPlayer(player, ModColors.GRAY,
                            victim.getName().getString() + " has lost trust");
                }
            }
        }
    }

    private ModEntityBehaviors() {}
}
