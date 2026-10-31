package cliffordha.totvw.mixin;

import cliffordha.totvw.entity.skills.ArcticWindsEffect;
import cliffordha.totvw.registry.attachments.VWAttachment;
import cliffordha.totvw.util.ExtUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.AbstractWindCharge;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(AbstractWindCharge.class)
public class WindChargeMixin {

    @Inject(method = "onHitEntity", at = @At("HEAD"))
    private void onHit(EntityHitResult hitResult, CallbackInfo ci) {
        if (hitResult.getEntity() instanceof LivingEntity entity && entity.level() instanceof ServerLevel level) {
            int pressureDiff = entity.getAttachedOrElse(VWAttachment.PRESSURE_DIFFERENCE, 0);
            int arcticAffliction = ArcticWindsEffect.afflictionStack(entity);

            RandomSource random = level.getRandom();

            if (pressureDiff > 0) {
                if (random.nextFloat() < (pressureDiff * 0.01f) + 0.3f) {
                    double kickback = Math.min(pressureDiff * 0.05f, 3.2);
                    applyUpwardKick(entity, level, kickback);

                    List<Monster> monsters = level.getEntitiesOfClass(
                            Monster.class,
                            ExtUtil.scanArea(entity, 3),
                            test -> test.getAttachedOrElse(VWAttachment.PRESSURE_DIFFERENCE, 0) > 0
                    );
                    if (monsters.isEmpty()) {
                        for (Monster monster : monsters) {
                            this.onIndirectHit(monster, level);
                        }
                    }
                }
            }
            float chance = ExtUtil.isInBiome(entity, BiomeTags.SPAWNS_COLD_VARIANT_FARM_ANIMALS) ? 0.6f : 0.3f;
            if (arcticAffliction > 0 && random.nextFloat() < chance) {
                List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, ExtUtil.scanArea(entity, 5), LivingEntity::isAlive);
                if (entities.isEmpty()) return;

                for (LivingEntity livingEntity : entities) {
                    int current = livingEntity.getAttachedOrElse(VWAttachment.ARCTIC_AFFLICTION, 0);
                    livingEntity.setAttached(VWAttachment.ARCTIC_AFFLICTION, current + 2);
                    ExtUtil.sendParticles(ParticleTypes.SNOWFLAKE, level, entity.blockPosition(), 2, 4f, -10f);
                }
                ExtUtil.sendParticles(ParticleTypes.EXPLOSION_EMITTER, level, entity.blockPosition(), 1, 4f);
            }
        }
    }

    @Unique
    private void onIndirectHit(LivingEntity entity, ServerLevel level) {
        int pressure = entity.getAttachedOrElse(VWAttachment.PRESSURE_DIFFERENCE, 0);
        RandomSource random = level.getRandom();
        if (random.nextFloat() < (pressure * 0.01f) + 0.3f) {
            double kickback = Math.min(pressure * 0.05f, 3.2);
            applyUpwardKick(entity, level, kickback);
        }
    }

    @Unique
    private static void applyUpwardKick(LivingEntity entity, ServerLevel level, double kickback) {
        entity.setDeltaMovement(
                entity.getDeltaMovement().add(0, kickback, 0)
        );
        if (kickback > 0.5) {
            ExtUtil.sendParticles(ParticleTypes.EXPLOSION_EMITTER, level, entity.blockPosition(), 1, 2.5f);
        }
    }
}
