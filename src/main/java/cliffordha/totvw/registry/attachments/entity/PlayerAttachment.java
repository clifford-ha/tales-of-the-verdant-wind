package cliffordha.totvw.registry.attachments.entity;

import cliffordha.totvw.registry.VWItems;
import cliffordha.totvw.registry.attachments.HavocType;
import cliffordha.totvw.registry.attachments.ArmorData;
import cliffordha.totvw.registry.attachments.NameHolder;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static cliffordha.totvw.registry.attachments.AttachmentUtil.*;

public class PlayerAttachment {
    private static final String P = "player_";
    
    public static final AttachmentType<Boolean> IS_DEV_MODE = registerBool(P + "is_dev_mode", false);
    public static final AttachmentType<Integer> RANDOM_INT_10 = registerInt(P + "random_int_10", false);

    public static final AttachmentType<Integer> RECEIVED_ENCHANTMENTS_HANDBOOK = registerInt(P + "received_enchantments_handbook", false);
    public static final AttachmentType<Integer> RECEIVED_EFFECTS_HANDBOOK = registerInt(P + "received_effects_handbook", false);
    public static final AttachmentType<Integer> RECEIVED_ITEMS_HANDBOOK = registerInt(P + "received_items_handbook", false);
    public static final AttachmentType<Integer> RECEIVED_FEATURES_HANDBOOK = registerInt(P + "received_features_handbook", false);

    public static final AttachmentType<List<CompoundTag>> WOLF_SOULS = registerCompoundList(P + "wolf_souls", true);

    public static final AttachmentType<Integer> GENESIS_RUNESTONE_ACQUISITION_COUNT = registerInt(P + "genesis_runestone_acquisition_count", true);

    public static final AttachmentType<Integer> CD_BLESSING_OF_THE_VERDANT_WIND = registerInt(P + "cd_blessing_of_the_verdant_wind", true);
    public static final AttachmentType<Integer> CD_HAVOC = registerInt(P + "cd_havoc", false);
    public static final AttachmentType<HavocType> HAVOC_TYPE = registerHavocType(P + "havoc_type", false);
    public static final AttachmentType<Integer> HAVOC_USAGE_COUNT = registerInt(P + "havoc_usage_count", false);
    public static final AttachmentType<Integer> NOTIFY_BLESSING_OF_THE_VERDANT_WIND = registerInt(P + "notify_blessing_of_the_verdant_wind", false);

    public static final AttachmentType<Integer> VILLAGER_ATROCITY_COUNT = registerInt(P + "villager_atrocity_count", false);
    public static final AttachmentType<Integer> WOLF_ATROCITY_COUNT = registerInt(P + "wolf_atrocity_count", false);
    public static final AttachmentType<List<NameHolder>> TRUSTED_PLAYERS = registerTrustData(P + "trusted_players", true);

    public static final AttachmentType<List<ArmorData>> EQUIPMENTS = registerArmorList(P + "equipments");

    public static final AttachmentType<Integer> EXPLOSION_RES = registerInt(P + "explosion_resistance_points", true);

    public static @Nullable ItemStack getArmorFor(Player player, EquipmentSlot slot) {
        List<ArmorData> armors = player.getAttachedOrElse(EQUIPMENTS, new ArrayList<>());
        if (armors.isEmpty()) return null;
        for (ArmorData data : armors) {
            if (data.slot().equals(slot) && !data.getItemStack().is(VWItems.EMPTY)) {
                return data.getItemStack();
            }
        }
        return null;
    }
    public static void updateArmorList(Player player) {
        List<ArmorData> armors = new ArrayList<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = player.hasItemInSlot(slot) ? player.getItemBySlot(slot) : null;
            if (stack != null) {
                armors.add(new ArmorData(
                        stack.getDisplayName(),
                        BuiltInRegistries.ITEM.getKey(stack.getItem()),
                        stack.getMaxDamage(),
                        stack.getDamageValue(),
                        slot
                ));
            }
        }
        player.setAttached(EQUIPMENTS, armors);
    }
    public static int getDamageValue(ItemStack stack) {
        if (!stack.is(VWItems.EMPTY) && stack.isDamageableItem()) {
            return stack.getDamageValue();
        }
        return 0;
    }
    public static int getMaxDamageValue(ItemStack stack) {
        if (stack != null && !stack.is(VWItems.EMPTY) && stack.isDamageableItem()) {
            return stack.getMaxDamage();
        }
        return 0;
    }
    public static List<ArmorData> getArmorList(Player player) {
        List<ArmorData> armors = new ArrayList<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = player.getItemBySlot(slot);
            if (getMaxDamageValue(stack) > 0) {
                armors.add(new ArmorData(
                        stack.getItemName(),
                        ArmorData.getID(stack),
                        stack.getMaxDamage(),
                        stack.getDamageValue(),
                        slot
                ));
            }
        }
        List<EquipmentSlot> slotOrder = List.of(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND,
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);
        armors.sort(Comparator.comparing(ArmorData::slot, Comparator.comparing(slotOrder::indexOf)));
        return armors;
    }
    public static boolean hasWolfSoul(Player player) {
        return !player.getAttachedOrElse(WOLF_SOULS, List.of()).isEmpty();
    }

    public static Pair<String, UUID> getNameAndUUID(LivingEntity entity) {
        return new Pair<>(entity.getPlainTextName(), entity.getUUID());
    }
    public static int getWolfAtrocityCount(Player player) {
        return player.getAttachedOrElse(WOLF_ATROCITY_COUNT, 0);
    }
    public static int getVillagerAtrocityCount(Player player) {
        return player.getAttachedOrElse(VILLAGER_ATROCITY_COUNT, 0);
    }
    public static List<NameHolder> getTrustedPlayers(Player player) {
        return player.getAttachedOrElse(TRUSTED_PLAYERS, List.of());
    }
    public static boolean isTrusted(Player player, Player other) {
        List<NameHolder> playerData = getTrustedPlayers(player);
        List<NameHolder> otherData = getTrustedPlayers(other);

        List<NameHolder> checkA = playerData.stream().filter(data -> data.uuid().equals(other.getUUID())).toList();
        List<NameHolder> checkB = otherData.stream().filter(data -> data.uuid().equals(player.getUUID())).toList();

        return !checkA.isEmpty() && !checkB.isEmpty();
    }
}
