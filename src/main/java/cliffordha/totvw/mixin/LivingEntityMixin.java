package cliffordha.totvw.mixin;

import cliffordha.totvw.entity.skills.ArcticWindsEffect;
import cliffordha.totvw.registry.VWParticles;
import cliffordha.totvw.registry.attachments.VWAttachments;
import cliffordha.totvw.util.ExtUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!(entity.level() instanceof ServerLevel level)) return;

        int affliction = ArcticWindsEffect.afflictionStack(entity);
        boolean frostBound = ArcticWindsEffect.frostBoundStack(entity) > 0;
        BlockPos frostBoundPos = entity.getAttachedOrElse(VWAttachments.FROST_BOUND_POS, null);
        if (frostBound) {
            if (frostBoundPos != null) {
                entity.snapTo(new Vec3(frostBoundPos));
            }
            entity.setDeltaMovement(Vec3.ZERO);
            ExtUtil.sendParticles(VWParticles.EXT_SNOWFLAKE, level, entity.blockPosition(), 2, 1.5);
        }

        if (level.getGameTime() % 20 == 0) {
            if (affliction > 0) {
                ArcticWindsEffect.afflictionTick(level, entity);
            }
            if (entity.getAttachedOrElse(VWAttachments.FROST_BOUND_TIMER, 0) > 0) {
                ArcticWindsEffect.setFrostBound(entity, -1);
            } else {
                entity.removeAttached(VWAttachments.FROST_BOUND_POS);
            }
        }

        if (level.getGameTime() % 100 == 0) {
            if (affliction > 0 && !ArcticWindsEffect.inColdEnv(entity)) {
                ArcticWindsEffect.setAffliction(entity, -1);
            }
        }
    }
}
