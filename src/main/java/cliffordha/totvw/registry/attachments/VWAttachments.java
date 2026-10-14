package cliffordha.totvw.registry.attachments;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.attachments.entity.*;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static cliffordha.totvw.registry.attachments.AttachmentUtil.*;

public class VWAttachments {
    public static final AttachmentType<Boolean> HAS_VERDANT_OMEN = registerBool("has_verdant_omen", false);

    public static final AttachmentType<Integer> PRESSURE_DIFFERENCE = registerInt("pressure_difference", false);
    public static final AttachmentType<Boolean> HAS_IMPLODED = registerBool("has_imploded", false);

    public static final AttachmentType<Integer> VERDANT_BLOOM_STACK = registerInt("verdant_bloom_stack", false);

    public static final AttachmentType<BlockPos> LAST_OVERWORLD_POS = registerBlockPos("last_overworld_pos", true);
    public static final AttachmentType<BlockPos> LAST_NOLAYAN_POS = registerBlockPos("last_nolayan_pos", true);
    public static final AttachmentType<Boolean> HAS_ENTERED_NOLAYAN = registerBool("has_entered_nolayan", true);

    public static final AttachmentType<UUID> WOLF_PLAYER_SHARED_ID = registerUUID("wolf_player_shared_id", true);


    public static UUID getWolfPlayerSharedId(LivingEntity entity) {
        return entity.getAttachedOrElse(WOLF_PLAYER_SHARED_ID, entity.getUUID());
    }



    public static final List<AttachmentType<?>> WOLF_ATTACHMENTS = List.of(
            WolfAttachment.IS_VERDANT_TYPE,
            WolfAttachment.IS_VILLAGE_GUARD,
            WolfAttachment.TIMER_AIR_SUPPLY,
            WolfAttachment.NOTIFY_AIR_SUPPLY,
            WolfAttachment.CD_BLESSING_OF_THE_VERDANT_WIND,
            WolfAttachment.CD_BLOODLUST_SKILL_PARALYZE,
            WolfAttachment.CD_MIGHT_SKILL_RUPTURE,
            WolfAttachment.CD_IGNORE_HIGH_DAMAGE,
            WolfAttachment.NOTIFY_MIGHT_SKILL_RUPTURE,
            WolfAttachment.NOTIFY_BLOODLUST_SKILL_PARALYZE,
            WolfAttachment.NOTIFY_BLESSING_OF_THE_VERDANT_WIND,
            WolfAttachment.BENEDICTION,
            WolfAttachment.SOUL_ID,
            WolfAttachment.FAMILY_ID,
            WolfAttachment.TRY_SAVE_POINTS,
            WolfAttachment.TRY_SAVE_STATUS,
            WolfAttachment.RESPAWN_POINT,
            WolfAttachment.AGGRESSOR_LIST,
            WolfAttachment.TRUSTED_PLAYERS,
            WolfAttachment.TETHERED_ENTITY_TYPES,
            WolfAttachment.RUNESTONE_TYPE,
            WolfAttachment.TETHER_ENTITY_BLACKLIST,
            WolfAttachment.ATTACK_CYCLE
    );
    public static final List<AttachmentType<?>> PLAYER_ATTACHMENTS = List.of(
            PlayerAttachment.IS_DEV_MODE,

            PlayerAttachment.RECEIVED_ENCHANTMENTS_HANDBOOK,
            PlayerAttachment.RECEIVED_EFFECTS_HANDBOOK,
            PlayerAttachment.RECEIVED_ITEMS_HANDBOOK,
            PlayerAttachment.RECEIVED_FEATURES_HANDBOOK,

            PlayerAttachment.GENESIS_RUNESTONE_ACQUISITION_COUNT,
            PlayerAttachment.WOLF_SOULS,
            PlayerAttachment.VILLAGER_ATROCITY_COUNT,
            PlayerAttachment.WOLF_ATROCITY_COUNT,
            PlayerAttachment.CD_BLESSING_OF_THE_VERDANT_WIND,
            PlayerAttachment.CD_HAVOC,
            PlayerAttachment.HAVOC_TYPE,
            PlayerAttachment.HAVOC_USAGE_COUNT,
            PlayerAttachment.NOTIFY_BLESSING_OF_THE_VERDANT_WIND,
            PlayerAttachment.RESPAWN_POINT,
            PlayerAttachment.TRUSTED_PLAYERS
    );
    public static final List<AttachmentType<?>> VILLAGER_ATTACHMENTS = List.of(
            VillagerAttachment.IS_VERDANT_TYPE,
            VillagerAttachment.CD_HEAL_OTHERS,
            VillagerAttachment.CD_HEAL_WOLF,
            VillagerAttachment.CD_HEAL_IRON_GOLEM,
            VillagerAttachment.CD_DISCOUNT_REROLL,
            VillagerAttachment.DISCOUNT_MODIFIER
    );
    public static final List<AttachmentType<?>> MISC_ATTACHMENTS = List.of(
            HAS_VERDANT_OMEN,
            PRESSURE_DIFFERENCE,
            HAS_IMPLODED,
            VERDANT_BLOOM_STACK,
            LAST_OVERWORLD_POS,
            LAST_NOLAYAN_POS,
            HAS_ENTERED_NOLAYAN
    );
    public static final List<AttachmentType<?>> PLAYER_PREFS = List.of(
            PlayerPrefs.SHOW_ATROCITY_COUNTER,
            PlayerPrefs.ENABLE_NOTIFIERS,

            PlayerPrefs.BENEDICTION_HEALTH_THRESHOLD,
            PlayerPrefs.BENEDICTION_SHARE_STACK,
            PlayerPrefs.BENEDICTION_ALWAYS_TRIGGER_BLESSING,
            PlayerPrefs.BENEDICTION_TELEPORT_AFTER_SAVE,
            PlayerPrefs.BENEDICTION_WOLF_TP_METHOD,
            PlayerPrefs.BENEDICTION_PLAYER_TP_METHOD,
            PlayerPrefs.BENEDICTION_WOLF_TP_ALL,
            PlayerPrefs.BENEDICTION_FORCE_TP
    );
    public static final int TOTAL = WOLF_ATTACHMENTS.size() + PLAYER_ATTACHMENTS.size() + VILLAGER_ATTACHMENTS.size() + MISC_ATTACHMENTS.size() + PLAYER_PREFS.size();


    public static void register() {
        TOTVW.sendClassRegisterLog(
                "Custom Attachments (" +
                        "Wolf: " + WOLF_ATTACHMENTS.size() + ", " +
                        "Villager: " + VILLAGER_ATTACHMENTS.size() + ", " +
                        "Player: " + PLAYER_ATTACHMENTS.size() + ", " +
                        "Misc: " + MISC_ATTACHMENTS.size() + ", " +
                        "PlayerPrefs: " + PLAYER_PREFS.size() + "): " +
                        TOTAL + " in total has been"
        );
    }
}