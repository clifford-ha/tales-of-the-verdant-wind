package cliffordha.totvw.entity.wolf;

import cliffordha.totvw.registry.attachments.AttachmentUtil;
import cliffordha.totvw.registry.attachments.Runestone;
import cliffordha.totvw.registry.attachments.VWAttachments;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import cliffordha.totvw.worldgen.dimension.VWDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.wolf.Wolf;
import oshi.util.tuples.Pair;

import java.util.List;
import java.util.UUID;

public record WolfStats(
        String name,
        String UUID,
        String soulID,
        String familyID,

        String owner,
        String ownerUUID,
        String sharedUUID,

        String isVerdant,
        String benedictionStack,
        String attackCycle,
        int trySavePoints,
        String respawnDimension,
        String returnPoint,
        Runestone runestoneType,
        boolean hasArcticAffliction,

        List<Pair<String, UUID>> trustedPlayers,
        List<Pair<String, UUID>> aggressors
) {
    public static WolfStats valueOf(Wolf wolf) {
        var hasSoulID = wolf.getAttachedOrElse(WolfAttachment.SOUL_ID, AttachmentUtil.EMPTY_UUID);
        String SOUL_ID = hasSoulID != AttachmentUtil.EMPTY_UUID ? String.valueOf(wolf.getAttached(WolfAttachment.SOUL_ID)) : "None";
        var hasFamilyID = wolf.getAttachedOrElse(WolfAttachment.FAMILY_ID, AttachmentUtil.EMPTY_UUID);
        String FAMILY_ID = hasFamilyID != AttachmentUtil.EMPTY_UUID ? String.valueOf(wolf.getAttached(WolfAttachment.FAMILY_ID)) : "None";
        String OWNER = wolf.getOwner() != null ? wolf.getOwner().getPlainTextName() : "None";
        String OWNER_UUID = wolf.getOwner() != null ? String.valueOf(wolf.getOwner().getUUID()) : "None";
        String SHARED_UUID = wolf.getOwner() != null ? String.valueOf(VWAttachments.getWolfPlayerSharedId(wolf)): "None";

        String IS_VERDANT = wolf.getAttachedOrElse(WolfAttachment.IS_VERDANT_TYPE, false) + "";
        String BENEDICTION_STACK = wolf.getAttachedOrElse(WolfAttachment.BENEDICTION, 0) + "";
        String ATTACK_CYCLE = wolf.getAttachedOrElse(WolfAttachment.ATTACK_CYCLE, 0) + "";
        BlockPos returnPoint = wolf.getAttachedOrElse(WolfAttachment.RESPAWN_POINT, BlockPos.ZERO);
        String RETURN_POINT = returnPoint != BlockPos.ZERO ? returnPoint.getX() + ", " + returnPoint.getY() + ", " + returnPoint.getZ() : "None";
        return new WolfStats(
                wolf.getPlainTextName(),
                wolf.getStringUUID(),
                SOUL_ID,
                FAMILY_ID,

                OWNER,
                OWNER_UUID,
                SHARED_UUID,

                IS_VERDANT,
                BENEDICTION_STACK,
                ATTACK_CYCLE,
                wolf.getAttachedOrElse(WolfAttachment.TRY_SAVE_POINTS, 0),
                String.valueOf(wolf.getAttachedOrElse(WolfAttachment.RESPAWN_DIMENSION, VWDimensions.NONE).identifier()),
                RETURN_POINT,
                wolf.getAttachedOrElse(WolfAttachment.RUNESTONE_TYPE, Runestone.EMPTY),
                wolf.getAttachedOrElse(VWAttachments.ARCTIC_AFFLICTION, 0) > 0,

                WolfAttachment.getTrustedPlayers(wolf),
                WolfAttachment.getAggressors(wolf)
        );
    }
}
