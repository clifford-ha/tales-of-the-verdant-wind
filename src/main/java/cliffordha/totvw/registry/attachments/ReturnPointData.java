package cliffordha.totvw.registry.attachments;

import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import cliffordha.totvw.worldgen.dimension.VWDimensions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public record ReturnPointData(ResourceKey<Level> levelKey, BlockPos pos, Boolean recordAfter) {
    public static final Codec<ReturnPointData> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Level.RESOURCE_KEY_CODEC.fieldOf("levelKey").forGetter(ReturnPointData::levelKey),
                    BlockPos.CODEC.fieldOf("pos").forGetter(ReturnPointData::pos),
                    Codec.BOOL.optionalFieldOf("recordAfter", false).forGetter(ReturnPointData::recordAfter)
            ).apply(instance, ReturnPointData::new)
    );
    public static final ReturnPointData DEFAULT = new ReturnPointData(VWDimensions.NONE, BlockPos.ZERO, true);

    public boolean isValid() {
        return this.levelKey() != VWDimensions.NONE && this.levelKey() != null && this.pos() != BlockPos.ZERO && this.pos != null;
    }
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
    public static ReturnPointData forCurrent(LivingEntity entity) {
        return entity.getAttachedOrElse(WolfAttachment.RETURN_POINT, DEFAULT);
    }
    public static boolean isValid(ReturnPointData data) {
        if (data.levelKey() == null || data.pos() == null) return false;
        return !data.pos().equals(BlockPos.ZERO);
    }
    public static boolean returnToTargetOrDefault(LivingEntity entity, ReturnPointData data, boolean playSound) {
        ResourceKey<Level> levelKey = data.levelKey();
        ResourceKey<Level> currentLevelKey = entity.level().dimension();
        if (data.isValid()) {
            if (levelKey.equals(currentLevelKey)) {
                entity.teleportTo(data.pos().getX(), data.pos().getY(), data.pos().getZ());

            } else {
                ServerLevel newLevel = entity.level().getServer().getLevel(levelKey);
                if (newLevel == null) return false;

                return teleportTo(entity, levelKey, playSound);
            }
        } else {
            return teleportTo(entity, currentLevelKey, playSound);
        }
        return false;
    }
    private static boolean teleportTo(LivingEntity from, ResourceKey<Level> level, boolean playSound) {
        ServerLevel newLevel = from.level().getServer().getLevel(level);
        if (newLevel == null) return false;

        BlockPos newPos = newLevel.getRespawnData().pos();
        TeleportTransition.PostTeleportTransition transition = TeleportTransition.PLACE_PORTAL_TICKET;
        if (playSound) {
            transition.then(TeleportTransition.PLAY_PORTAL_SOUND);
        }
        from.teleport(
                new TeleportTransition(
                        newLevel,
                        new Vec3(newPos.getX(), newPos.getY(), newPos.getZ()),
                        Vec3.ZERO,
                        newLevel.getRespawnData().yaw(),
                        newLevel.getRespawnData().pitch(),
                        transition
                )
        );
        return true;
    }
}
