package cliffordha.totvw.entity;

import cliffordha.totvw.entity.skills.ArcticWindsEffect;
import cliffordha.totvw.registry.VWParticles;
import cliffordha.totvw.registry.attachments.VWAttachment;
import cliffordha.totvw.util.ExtUtil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class GlobalServerTickEvents {
    public static void register() {
        onServerTick();
    }
    private static void onServerTick() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (var serverLevel : server.getAllLevels()) {
                for (Entity mob : serverLevel.getAllEntities()) {
                    if (!(mob instanceof LivingEntity entity)) continue;
                    instantTick(serverLevel, entity);
                    if (serverLevel.getGameTime() % 20 == 0) {
                        mainTickThread(serverLevel, entity);
                    }
                }
            }
        });
    }
    private static void instantTick(ServerLevel level, LivingEntity entity) {
        int affliction = ArcticWindsEffect.afflictionStack(entity);
        boolean frostBound = ArcticWindsEffect.frostBoundStack(entity) > 0;
        BlockPos frostBoundPos = entity.getAttachedOrElse(VWAttachment.FROST_BOUND_POS, entity.blockPosition());
        if (frostBound) {
            entity.snapTo(new Vec3(frostBoundPos));
            entity.setDeltaMovement(Vec3.ZERO);
            ExtUtil.sendParticles(VWParticles.EXT_SNOWFLAKE, level, entity.blockPosition(), 2, 1.5);
        }

        if (level.getGameTime() % 20 == 0) {
            if (affliction > 0) {
                ArcticWindsEffect.afflictionTick(level, entity);
            }
            if (entity.getAttachedOrElse(VWAttachment.FROST_BOUND_TIMER, 0) > 0) {
                ArcticWindsEffect.setFrostBound(entity, -1);
            } else {
                entity.removeAttached(VWAttachment.FROST_BOUND_POS);
            }
        }

        if (level.getGameTime() % 100 == 0) {
            if (affliction > 0 && !ArcticWindsEffect.inColdEnv(entity)) {
                ArcticWindsEffect.setAffliction(entity, -1);
            }
        }
    }
    private static void mainTickThread(ServerLevel level, LivingEntity entity) {
    }
}
