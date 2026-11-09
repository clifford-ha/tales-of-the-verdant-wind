package cliffordha.totvw.entity;

import cliffordha.totvw.enchantment.ArcticWindsEffect;
import cliffordha.totvw.enchantment.WitheringEffect;
import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.VWParticles;
import cliffordha.totvw.registry.attachments.VWAttachment;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import cliffordha.totvw.util.ExtUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import static cliffordha.totvw.util.ExtUtil.isDimension;
import static cliffordha.totvw.util.ExtUtil.sendParticles;

public class LivingEntityTickMixin {
    public static void tick(ServerLevel level, LivingEntity entity) {
        if (entity instanceof Wolf wolf && wolf.hasAttached(WolfAttachment.FRAGMENT_DATA)) {
            sendParticles(WitheringEffect.FRAGMENT_PARTICLE, level, wolf.blockPosition(), 1, 0.7);
        }

        int affliction = ArcticWindsEffect.afflictionStack(entity);
        boolean frostBound = ArcticWindsEffect.frostBoundStack(entity) > 0;
        BlockPos frostBoundPos = entity.getAttachedOrElse(VWAttachment.FROST_BOUND_POS, entity.blockPosition());
        if (frostBound) {
            entity.snapTo(new Vec3(frostBoundPos));
            entity.setDeltaMovement(Vec3.ZERO);
            ExtUtil.sendParticles(VWParticles.EXT_SNOWFLAKE, level, entity.blockPosition(), 4, 1.5);
        }

        int lifted = entity.getAttachedOrElse(VWAttachment.LIFT_DURATION, 0);
        if (lifted > 0) {
            entity.setAttached(VWAttachment.LIFT_DURATION, lifted -1);
        }

        if (level.getGameTime() % 20 == 0) {
            int frostboundTimer = entity.getAttachedOrElse(VWAttachment.FROST_BOUND_TIMER, 0);
            if (frostboundTimer > 0) {
                ArcticWindsEffect.setFrostBound(entity, -1);

            } else if (entity.hasAttached(VWAttachment.FROST_BOUND_POS)) {
                entity.removeAttached(VWAttachment.FROST_BOUND_POS);
            }
        }

        if (level.getGameTime() % 100 == 0) {
            if (affliction > 0 && !ArcticWindsEffect.inColdEnv(entity)) {
                ArcticWindsEffect.setAffliction(entity, -1);
            }

            boolean temperateEnv = level.getBiome(entity.blockPosition()).value().getBaseTemperature() > 1.8f;
            int scorchingHeat = entity.getAttachedOrElse(VWAttachment.SCORCHING_HEAT, 0);
            if (!(isDimension(entity, Level.NETHER) || temperateEnv) && scorchingHeat > 0) {
                entity.setAttached(VWAttachment.SCORCHING_HEAT, scorchingHeat - 1);
            }
        }
    }
}
