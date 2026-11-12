package cliffordha.totvw.registry.attachments;

import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import cliffordha.totvw.worldgen.dimension.VWDimensions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public record ReturnPointData(ResourceKey<Level> levelKey, BlockPos pos, Boolean recordAfter) {
    public static final Codec<ReturnPointData> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Level.RESOURCE_KEY_CODEC.fieldOf("level_key").forGetter(ReturnPointData::levelKey),
                    BlockPos.CODEC.fieldOf("pos").forGetter(ReturnPointData::pos),
                    Codec.BOOL.optionalFieldOf("record_after", false).forGetter(ReturnPointData::recordAfter)
            ).apply(instance, ReturnPointData::new)
    );
    public static final ReturnPointData DEFAULT = new ReturnPointData(VWDimensions.NONE, BlockPos.ZERO, true);

    public @Nullable ServerLevel getDimensionLevel(ServerLevel level) {
        return level.getServer().getLevel(this.levelKey());
    }
    public @Nullable BlockPos getPos() {
        return this.pos();
    }
    public static ReturnPointData changeReturnMethod(ReturnPointData oldData, boolean value) {
        return new ReturnPointData(oldData.levelKey(), oldData.pos(), value);
    }
    public static ReturnPointData removeReturnPoint(ReturnPointData oldData) {
        return new ReturnPointData(VWDimensions.NONE, BlockPos.ZERO, oldData.recordAfter());
    }
    public static ReturnPointData of(LivingEntity entity) {
        return entity.getAttachedOrElse(WolfAttachment.RETURN_POINT, DEFAULT);
    }
    public boolean isValid() {
        return this.levelKey() != null && this.levelKey() != VWDimensions.NONE
                && this.pos() != null && !this.pos().equals(BlockPos.ZERO);
    }

    public static boolean isValid(ReturnPointData data) {
        return data.isValid();
    }

    public static @Nullable Entity returnToTargetOrDefault(LivingEntity entity, ReturnPointData data, boolean playSound) {
        if (!(entity.level() instanceof ServerLevel currentLevel)) return null;

        if (data.isValid()) {
            if (data.levelKey().equals(currentLevel.dimension())) {
                entity.teleportTo(data.pos().getX() + 0.5, data.pos().getY(), data.pos().getZ() + 0.5);
                return entity;
            }
            ServerLevel newLevel = currentLevel.getServer().getLevel(data.levelKey());
            if (newLevel == null) return null;
            return teleportTo(entity, newLevel, data.pos(), playSound);
        }
        return teleportTo(entity, currentLevel, currentLevel.getRespawnData().pos(), playSound);
    }

    private static @Nullable Entity teleportTo(LivingEntity from, ServerLevel newLevel, BlockPos pos, boolean playSound) {
        TeleportTransition.PostTeleportTransition post = playSound
                ? TeleportTransition.PLAY_PORTAL_SOUND.then(TeleportTransition.PLACE_PORTAL_TICKET)
                : TeleportTransition.PLACE_PORTAL_TICKET;
        return from.teleport(new TeleportTransition(
                newLevel,
                Vec3.atBottomCenterOf(pos),
                Vec3.ZERO,
                newLevel.getRespawnData().yaw(),
                newLevel.getRespawnData().pitch(),
                post
        ));
    }
}
