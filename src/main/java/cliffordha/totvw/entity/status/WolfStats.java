package cliffordha.totvw.entity.status;

import cliffordha.totvw.registry.attachments.*;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import cliffordha.totvw.worldgen.dimension.VWDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.wolf.Wolf;

import java.util.List;

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
        int trySavePoints,
        String respawnDimension,
        String returnPoint,
        Runestone runestoneType,
        boolean hasArcticAffliction,

        List<NameHolder> trustedPlayers,
        List<NameHolder> aggressors
) {
    public static WolfStats valueOf(Wolf wolf) {
        var hasSoulID = wolf.getAttachedOrElse(WolfAttachment.SOUL_ID, AttachmentUtil.EMPTY_UUID);
        String SOUL_ID = hasSoulID != AttachmentUtil.EMPTY_UUID ? String.valueOf(wolf.getAttached(WolfAttachment.SOUL_ID)) : "None";
        var hasFamilyID = wolf.getAttachedOrElse(WolfAttachment.FAMILY_ID, AttachmentUtil.EMPTY_UUID);
        String FAMILY_ID = hasFamilyID != AttachmentUtil.EMPTY_UUID ? String.valueOf(wolf.getAttached(WolfAttachment.FAMILY_ID)) : "None";
        String OWNER = wolf.getOwner() != null ? wolf.getOwner().getPlainTextName() : "None";
        String OWNER_UUID = wolf.getOwner() != null ? wolf.getOwner().getStringUUID() : "None";

        String IS_VERDANT = wolf.getAttachedOrElse(WolfAttachment.IS_VERDANT_TYPE, false) + "";
        String BENEDICTION_STACK = wolf.getAttachedOrElse(WolfAttachment.BENEDICTION, 0) + "";
        String ATTACK_CYCLE = wolf.getAttachedOrElse(WolfAttachment.ATTACK_CYCLE, 0) + "";
        ReturnPointData returnPoint = wolf.getAttachedOrElse(WolfAttachment.RETURN_POINT, ReturnPointData.DEFAULT);
        boolean hasReturnPoint = returnPoint.levelKey() != VWDimensions.NONE && returnPoint.pos() != BlockPos.ZERO;
        String RETURN_POINT = hasReturnPoint ? returnPoint.pos().getX() + ", " + returnPoint.pos().getY() + ", " + returnPoint.pos().getZ() : "None";
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
                wolf.getAttachedOrElse(WolfAttachment.TRY_SAVE_POINTS, 0),
                returnPoint.levelKey().identifier().toString(),
                RETURN_POINT,
                wolf.getAttachedOrElse(WolfAttachment.RUNESTONE_TYPE, Runestone.EMPTY),
                wolf.getAttachedOrElse(VWAttachment.ARCTIC_AFFLICTION, 0) > 0,

                WolfAttachment.getTrustedPlayers(wolf),
                WolfAttachment.getAggressors(wolf)
        );
    }
}
