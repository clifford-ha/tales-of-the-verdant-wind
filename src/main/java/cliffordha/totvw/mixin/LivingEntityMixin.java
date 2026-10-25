package cliffordha.totvw.mixin;

import cliffordha.totvw.entity.skills.ArcticWindsEffect;
import cliffordha.totvw.registry.VWParticles;
import cliffordha.totvw.registry.attachments.VWAttachments;
import cliffordha.totvw.util.VWUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
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
        int affliction = entity.getAttachedOrElse(VWAttachments.ARCTIC_AFFLICTION, 0);
        boolean frostBound = entity.getAttachedOrElse(VWAttachments.FROST_BOUND_TIMER, 0) > 0;
        BlockPos pos = entity.getAttachedOrElse(VWAttachments.FROST_BOUND_POS, BlockPos.ZERO);
        if (entity instanceof Mob mob) {
            if (frostBound) {
                if (!pos.equals(BlockPos.ZERO)) {
                    mob.snapTo(new Vec3(pos));
                    mob.setDeltaMovement(Vec3.ZERO);
                }
                mob.setJumping(false);
                mob.setOnGround(true);
                VWUtil.sendParticles(VWParticles.EXT_SNOWFLAKE, level, mob.blockPosition(), 2, 1.5);
            }
        } else if (entity instanceof Player player) {
            if (frostBound) {
                if (!pos.equals(BlockPos.ZERO)) {
                    player.snapTo(new Vec3(pos));
                    player.setDeltaMovement(Vec3.ZERO);
                }
                VWUtil.sendParticles(VWParticles.EXT_SNOWFLAKE, level, player.blockPosition(), 2, 1.5);
            }
        }

        if (level.getGameTime() % 20 == 0) {

            BlockPos isBoundPos = entity.getAttachedOrElse(VWAttachments.FROST_BOUND_POS, BlockPos.ZERO);
            if (affliction > 0) {
                ArcticWindsEffect.afflictionTick(level, entity);
            }
            if (entity.getAttachedOrElse(VWAttachments.FROST_BOUND_TIMER, 0) > 0) {
                ArcticWindsEffect.setFrostBound(entity, -1);
            } else if (!isBoundPos.equals(BlockPos.ZERO)) {
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
