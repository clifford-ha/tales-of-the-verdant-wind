package cliffordha.totvw.enchantment;

import cliffordha.totvw.datagen.VWDamageTypes;
import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.VWEnchantments;
import cliffordha.totvw.registry.attachments.Runestone;
import cliffordha.totvw.registry.attachments.VWAttachment;
import cliffordha.totvw.util.ExtUtil;
import cliffordha.totvw.util.TextPopup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;

import static cliffordha.totvw.util.ExtUtil.*;

public class IgnitionEffect {
    public static void onAttack(ServerLevel level, Wolf wolf, LivingEntity victim, boolean exclusive) {
        int ACTIVE_IGNITION = VWEnchantments.getIgnition(wolf);
        int burnTime = ACTIVE_IGNITION * 3;

        int heatMultiplier = isDimension(wolf, Level.NETHER)
                || level.getBiome(wolf.blockPosition()).value().getBaseTemperature() > 1.8f ? 2 : 1;

        int bonus = Runestone.hasHavoc(wolf) ? 3 : 0;
        int computedTime = (burnTime * heatMultiplier) + bonus;

        if (ACTIVE_IGNITION >= 3) {
            removeEffect(victim, MobEffects.FIRE_RESISTANCE);
        }

        int scorchingPoint = victim.getAttachedOrElse(VWAttachment.SCORCHING_HEAT, 0);
        float scorchingDamage = 2;

        int arcticAffliction = ArcticWindsEffect.afflictionStack(victim);
        if (scorchingPoint > 6 && random.nextFloat() < arcticAffliction * 0.035) {
            ArcticWindsEffect.applyThermalBurn(level, wolf, victim, computedTime);
            return;
        }

        if (victim.fireImmune()) {
            scorchingDamage += (scorchingPoint + 1) * 2;

        } else {
            scorchingDamage += ACTIVE_IGNITION;
            if (level.isRainingAt(wolf.blockPosition()) && scorchingPoint >= 6) {
                ArcticWindsEffect.applyThermalBurn(level, wolf, victim, scorchingDamage);
                victim.setAttached(VWAttachment.SCORCHING_HEAT, scorchingPoint - (ACTIVE_IGNITION - 1));
                return;
            }

            victim.igniteForSeconds(computedTime + scorchingPoint);

        }
        if (scorchingPoint >= 9) {
            scorchingDamage += ACTIVE_IGNITION * computedTime;
            sendParticles(ParticleTypes.LAVA, level, victim.blockPosition(), 9, 0.5);
            victim.removeAttached(VWAttachment.SCORCHING_HEAT);
            ExtUtil.playSound(victim, SoundEvents.FIRE_EXTINGUISH, SoundSource.HOSTILE, false);

        } else {
            victim.setAttached(VWAttachment.SCORCHING_HEAT, Math.min(scorchingPoint + ACTIVE_IGNITION, 9));
        }

        if (scorchingDamage > 4) {
            if (scorchingDamage > 30) {
                TextPopup.showBold(wolf, victim, "Scorched! " + Mth.floor(scorchingDamage), VWColors.IGNITION, 1.3f + random.nextFloat());
            } else {
                TextPopup.show(wolf, victim, "Scorched! " + Mth.floor(scorchingDamage), VWColors.IGNITION);
            }
            sendParticles(ParticleTypes.LAVA, level, victim.blockPosition(), 9, 0.5);
        }

        victim.hurtServer(level, VWDamageTypes.scorchingHeat(level), scorchingDamage);
    }
}
