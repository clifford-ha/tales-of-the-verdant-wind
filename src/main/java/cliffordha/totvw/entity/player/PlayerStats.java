package cliffordha.totvw.entity.player;

import cliffordha.totvw.registry.attachments.AttachmentUtil;
import cliffordha.totvw.registry.attachments.HavocType;
import cliffordha.totvw.registry.attachments.VWAttachments;
import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import oshi.util.tuples.Pair;

import java.util.List;
import java.util.UUID;

public record PlayerStats(
        String name,
        String UUID,
        String sharedUUID,

        int wolfSouls,
        int wolfAtrocityCount,
        int villagerAtrocityCount,

        int genesisRunestoneAcquisitionCount,

        HavocType havocType,
        int havocUsageCount,

        List<Pair<String, UUID>> trustedPlayers,

        boolean hasReceivedEnchantmentsHandbook,
        boolean hasReceivedItemsHandbook,
        boolean hasReceivedFeaturesHandbook,
        boolean hasReceivedEffectsHandbook
) {
    public static PlayerStats valueOf(Player player) {
        var sharedID = player.getAttachedOrElse(VWAttachments.WOLF_PLAYER_SHARED_ID, AttachmentUtil.EMPTY_UUID);
        String SHARED_UUID = sharedID != AttachmentUtil.EMPTY_UUID ? String.valueOf(sharedID) : "None";
        List<CompoundTag> souls = player.getAttachedOrElse(PlayerAttachment.WOLF_SOULS, List.of());

        boolean enchantmentHandbook = player.getAttachedOrElse(PlayerAttachment.RECEIVED_ENCHANTMENTS_HANDBOOK, 0) > 0;
        boolean itemHandbook = player.getAttachedOrElse(PlayerAttachment.RECEIVED_ITEMS_HANDBOOK, 0) > 0;
        boolean featureHandbook = player.getAttachedOrElse(PlayerAttachment.RECEIVED_FEATURES_HANDBOOK, 0) > 0;
        boolean effectHandbook = player.getAttachedOrElse(PlayerAttachment.RECEIVED_EFFECTS_HANDBOOK, 0) > 0;

        return new PlayerStats(
                player.getPlainTextName(),
                player.getStringUUID(),
                SHARED_UUID,

                souls.size(),
                player.getAttachedOrElse(PlayerAttachment.WOLF_ATROCITY_COUNT, 0),
                player.getAttachedOrElse(PlayerAttachment.VILLAGER_ATROCITY_COUNT, 0),

                player.getAttachedOrElse(PlayerAttachment.GENESIS_RUNESTONE_ACQUISITION_COUNT, 0),

                HavocType.getType(player),
                HavocType.getUsage(player),

                PlayerAttachment.getTrustedPlayers(player),

                enchantmentHandbook,
                itemHandbook,
                featureHandbook,
                effectHandbook
        );
    }
}
