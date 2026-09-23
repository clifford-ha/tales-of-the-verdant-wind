package cliffordha.totvw.registry.attachments.entity;

import cliffordha.totvw.registry.attachments.HavocType;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import oshi.util.tuples.Pair;

import java.util.List;
import java.util.UUID;

import static cliffordha.totvw.registry.attachments.AttachmentUtil.*;

public class PlayerAttachment {
    private static final String P = "player_";
    
    public static final AttachmentType<Boolean> IS_DEV_MODE = registerBool(P + "is_dev_mode", false);
    public static final AttachmentType<Integer> RANDOM_INT_10 = registerInt(P + "random_int_10", false);

    public static final AttachmentType<List<CompoundTag>> WOLF_SOULS = registerCompoundList(P + "wolf_souls", true);

    public static final AttachmentType<Integer> RECEIVED_ENCHANTMENTS_HANDBOOK = registerInt(P + "received_enchantments_handbook", false);
    public static final AttachmentType<Integer> RECEIVED_EFFECTS_HANDBOOK = registerInt(P + "received_effects_handbook", false);
    public static final AttachmentType<Integer> RECEIVED_ITEMS_HANDBOOK = registerInt(P + "received_items_handbook", false);
    public static final AttachmentType<Integer> RECEIVED_FEATURES_HANDBOOK = registerInt(P + "received_features_handbook", false);

    public static final AttachmentType<Integer> CD_BLESSING_OF_THE_VERDANT_WIND = registerInt(P + "cd_blessing_of_the_verdant_wind", true);
    public static final AttachmentType<Integer> CD_HAVOC = registerInt(P + "cd_havoc", false);
    public static final AttachmentType<HavocType> HAVOC_TYPE = registerHavocType(P + "havoc_type", false);
    public static final AttachmentType<Integer> HAVOC_USAGE_COUNT = registerInt(P + "havoc_usage_count", false);
    public static final AttachmentType<Integer> NOTIFY_BLESSING_OF_THE_VERDANT_WIND = registerInt(P + "notify_blessing_of_the_verdant_wind", false);

    public static final AttachmentType<Integer> VILLAGER_ATROCITY_COUNT = registerInt(P + "villager_atrocity_count", false);
    public static final AttachmentType<Integer> WOLF_ATROCITY_COUNT = registerInt(P + "wolf_atrocity_count", false);

    public static final AttachmentType<BlockPos> RESPAWN_POINT = registerBlockPos(P + "respawn_point", true);
    public static final AttachmentType<List<Pair<String, UUID>>> TRUSTED_PLAYERS = registerListPair(P + "trusted_players", true);
}
