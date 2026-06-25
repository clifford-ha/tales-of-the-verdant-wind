package cliffordha.totvw.registry;

import net.minecraft.core.particles.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public class ModParticleEffects {
    public static void spawnBlessingParticlesEntity(LivingEntity entity, int frequency) {
        if (!(entity.level() instanceof ServerLevel serverLevel)) return;
        DustParticleOptions dust = new DustParticleOptions(
                0x00ffbf,
                1.0f
        );
        for (int i = 0; i < (16 * (frequency + 1)); i++) {
            serverLevel.sendParticles(
                    dust,
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    1,
                    1,
                    1,
                    1,
                    6
            );
        }
    }
    public static void triggerBenedictionParticles(LivingEntity entity, int frequency) {
        if (!(entity.level() instanceof ServerLevel level)) return;
        triggerParticles(entity,
                ModParticles.BENEDICTION_TRIGGER_PARTICLE,
                frequency);
    }
    public static void triggerMightParalyzeParticles(LivingEntity entity, int frequency) {
        triggerParticles(entity,
                ModParticles.MIGHT_PARALYZE_PARTICLE,
                frequency);
    }
    public static void benedictionEnvironmentParticleEntity(LivingEntity entity) {
        if (!(entity.level() instanceof ServerLevel serverLevel)) return;
        serverLevel.sendParticles(
                ModParticles.BENEDICTION_TRIGGER_PARTICLE,
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                16,
                1,
                1,
                1,
                0
        );
    }

    private static void triggerParticles(LivingEntity entity, ParticleOptions particle, int frequency) {
        if (!(entity.level() instanceof ServerLevel serverLevel)) return;
        for (int i = 0; i < (16 * (frequency + 1)); i++) {
            serverLevel.sendParticles(
                    particle,
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    1,
                    0.5,
                    0.5,
                    0.5,
                    0
            );
        }
    }
}
