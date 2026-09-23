package cliffordha.totvw.entity.wolf;

import cliffordha.totvw.registry.attachments.AttachmentUtil;
import cliffordha.totvw.registry.attachments.Runestone;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.wolf.Wolf;

public record WolfStats(
        String name,
        String UUID,
        String soulID,
        String familyID,

        String owner,
        String ownerUUID,

        String isVerdant,
        String benedictionStack,
        String attackCycle,
        String trySavePoints,
        String returnPoint,
        String runestoneType
) {
    public static WolfStats valueOf(Wolf wolf) {
        var hasSoulID = wolf.getAttachedOrElse(WolfAttachment.SOUL_ID, AttachmentUtil.EMPTY_UUID);
        String SOUL_ID = hasSoulID != AttachmentUtil.EMPTY_UUID ? String.valueOf(wolf.getAttached(WolfAttachment.SOUL_ID)) : "None";
        var hasFamilyID = wolf.getAttachedOrElse(WolfAttachment.FAMILY_ID, AttachmentUtil.EMPTY_UUID);
        String FAMILY_ID = hasFamilyID != AttachmentUtil.EMPTY_UUID ? String.valueOf(wolf.getAttached(WolfAttachment.FAMILY_ID)) : "None";
        String OWNER = wolf.getOwner() != null ? wolf.getOwner().getPlainTextName() : "None";
        String OWNER_UUID = wolf.getOwner() != null ? String.valueOf(wolf.getOwner().getUUID()) : "None";

        String IS_VERDANT = wolf.getAttachedOrElse(WolfAttachment.IS_VERDANT_TYPE, false) + "";
        String BENEDICTION_STACK = wolf.getAttachedOrElse(WolfAttachment.BENEDICTION, 0) + "";
        String ATTACK_CYCLE = wolf.getAttachedOrElse(WolfAttachment.ATTACK_CYCLE, 0) + "";
        String TRY_SAVE_POINTS = wolf.getAttachedOrElse(WolfAttachment.TRY_SAVE_POINTS, 0) + "";
        String RUNESTONE_TYPE = wolf.getAttachedOrElse(WolfAttachment.RUNESTONE_TYPE, Runestone.EMPTY).getName();
        BlockPos returnPoint = wolf.getAttachedOrElse(WolfAttachment.RESPAWN_POINT, BlockPos.ZERO);
        String RETURN_POINT = returnPoint != BlockPos.ZERO ? returnPoint.getX() + ", " + returnPoint.getY() + ", " + returnPoint.getZ() : "None";
        return new WolfStats(
                wolf.getPlainTextName(),
                wolf.getStringUUID(),
                SOUL_ID,
                FAMILY_ID,

                OWNER,
                OWNER_UUID,

                IS_VERDANT,
                BENEDICTION_STACK,
                ATTACK_CYCLE,
                TRY_SAVE_POINTS,
                RETURN_POINT,
                RUNESTONE_TYPE
        );
    }
}
