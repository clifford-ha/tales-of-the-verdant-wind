package cliffordha.totvw.entity.status;

import cliffordha.totvw.registry.attachments.AttachmentUtil;
import cliffordha.totvw.registry.attachments.HavocType;
import cliffordha.totvw.registry.attachments.VWAttachments;
import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import oshi.util.tuples.Pair;

import java.util.List;
import java.util.UUID;

import static cliffordha.totvw.registry.attachments.entity.PlayerAttachment.*;

public record PlayerStats(
        String name,
        String UUID,
        String sharedUUID,

        boolean hasHelmet,
        boolean hasChestplate,
        boolean hasLeggings,
        boolean hasBoots,
        boolean hasWeaponInMainhand,
        boolean hasShield,

        int helmetDurability,
        int chestplateDurability,
        int leggingsDurability,
        int bootsDurability,
        int mainhandDurability,
        int offHandDurability,

        int maxHelmetDurability,
        int maxChestplateDurability,
        int maxLeggingsDurability,
        int maxBootsDurability,
        int maxMainhandDurability,
        int maxShieldDurability,

        int wolfSouls,
        int wolfAtrocityCount,
        int villagerAtrocityCount,

        int genesisRunestoneAcquisitionCount,

        HavocType havocType,
        int havocUsageCount,

        List<Pair<String, UUID>> trustedPlayers,

        boolean receivedEnchantmentsHandbook,
        boolean receivedItemsHandbook,
        boolean receivedFeaturesHandbook,
        boolean receivedEffectsHandbook
) {
    public static PlayerStats valueOf(Player player) {
        var sharedID = player.getAttachedOrElse(VWAttachments.WOLF_PLAYER_SHARED_ID, AttachmentUtil.EMPTY_UUID);
        String SHARED_UUID = sharedID != AttachmentUtil.EMPTY_UUID ? String.valueOf(sharedID) : "None";
        List<CompoundTag> souls = player.getAttachedOrElse(PlayerAttachment.WOLF_SOULS, List.of());

        boolean enchantmentHandbook = player.getAttachedOrElse(PlayerAttachment.RECEIVED_ENCHANTMENTS_HANDBOOK, 0) > 0;
        boolean itemHandbook = player.getAttachedOrElse(PlayerAttachment.RECEIVED_ITEMS_HANDBOOK, 0) > 0;
        boolean featureHandbook = player.getAttachedOrElse(PlayerAttachment.RECEIVED_FEATURES_HANDBOOK, 0) > 0;
        boolean effectHandbook = player.getAttachedOrElse(PlayerAttachment.RECEIVED_EFFECTS_HANDBOOK, 0) > 0;

        ItemStack helmet = getArmorFor(player, EquipmentSlot.HEAD);
        ItemStack chestplate = getArmorFor(player, EquipmentSlot.CHEST);
        ItemStack leggings = getArmorFor(player, EquipmentSlot.LEGS);
        ItemStack boots = getArmorFor(player, EquipmentSlot.FEET);
        ItemStack mainhand = getArmorFor(player, EquipmentSlot.MAINHAND);
        ItemStack shield = getArmorFor(player, EquipmentSlot.OFFHAND);

        boolean hasHelmet = helmet != null;
        boolean hasChestplate = chestplate != null;
        boolean hasLeggings = leggings != null;
        boolean hasBoots = boots != null;
        boolean hasItemInMainhand = mainhand != null;
        boolean hasOffhandItem = shield != null;

        int helmetDurability = hasHelmet ? getDamageValue(helmet) : 0;
        int chestplateDurability = hasChestplate ? getDamageValue(chestplate) : 0;
        int leggingsDurability = hasLeggings ? getDamageValue(leggings) : 0;
        int bootsDurability = hasBoots ? getDamageValue(boots) : 0;
        int mainhandDurability = hasItemInMainhand ? getDamageValue(mainhand) : 0;
        int offHandDurability = hasOffhandItem ? getDamageValue(shield) : 0;

        int maxHelmetDurability = hasHelmet ? getMaxDamageValue(helmet) : 0;
        int maxChestplateDurability = hasChestplate ? getMaxDamageValue(chestplate) : 0;
        int maxLeggingsDurability = hasLeggings ? getMaxDamageValue(leggings) : 0;
        int maxBootsDurability = hasBoots ? getMaxDamageValue(boots) : 0;
        int maxMainhandDurability = hasItemInMainhand ? getMaxDamageValue(mainhand) : 0;
        int maxOffhandDurability = hasOffhandItem ? getMaxDamageValue(shield) : 0;

        return new PlayerStats(
                player.getPlainTextName(),
                player.getStringUUID(),
                SHARED_UUID,

                hasHelmet,
                hasChestplate,
                hasLeggings,
                hasBoots,
                hasItemInMainhand,
                hasOffhandItem,

                helmetDurability,
                chestplateDurability,
                leggingsDurability,
                bootsDurability,
                mainhandDurability,
                offHandDurability,

                maxHelmetDurability,
                maxChestplateDurability,
                maxLeggingsDurability,
                maxBootsDurability,
                maxMainhandDurability,
                maxOffhandDurability,

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
