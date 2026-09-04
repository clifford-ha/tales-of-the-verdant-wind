package cliffordha.totvw.registry.attachments;

import cliffordha.totvw.TOTVW;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import java.util.List;

import static cliffordha.totvw.registry.attachments.AttachmentUtil.*;

public class VWAttachments {
    public static final AttachmentType<Boolean> ENTITY_HAS_VERDANT_OMEN = registerBool("entity_has_verdant_omen");

    public static class windCore {
        public static final AttachmentType<Integer> ENTITY_PRESSURE_DIFFERENCE = registerInt("entity_pressure_difference");
        public static final AttachmentType<Boolean> ENTITY_HAS_IMPLODED = registerBool("entity_has_imploded");
    }
    public static class wolf {
        public static final AttachmentType<Boolean> WOLF_IS_VERDANT_TYPE = registerBool("wolf_is_verdant_type");
        public static final AttachmentType<Boolean> WOLF_IS_VILLAGE_GUARD = registerBool("wolf_is_village_guard");

        public static final AttachmentType<Integer> WOLF_TIMER_AIR_SUPPLY = registerInt("wolf_timer_air_supply");
        public static final AttachmentType<Integer> WOLF_NOTIFY_AIR_SUPPLY = registerInt("wolf_notify_air_supply");

        public static final AttachmentType<Integer> WOLF_TRY_SAVE_POINTS = registerInt("wolf_try_save_points");
        public static final AttachmentType<Integer> WOLF_TRY_SAVE_STATUS = registerInt("wolf_try_save_pending_points");

        public static final AttachmentType<Integer> WOLF_CD_BLESSING_OF_THE_VERDANT_WIND = registerInt("wolf_cd_blessing_of_the_verdant_wind");
        public static final AttachmentType<Integer> WOLF_CD_BLOODLUST_SKILL_PARALYZE = registerInt("wolf_cd_bloodlust_skill_paralyze");
        public static final AttachmentType<Integer> WOLF_CD_MIGHT_SKILL_RUPTURE = registerInt("wolf_cd_might_skill_rupture");
        public static final AttachmentType<Integer> WOLF_CD_IGNORE_HIGH_DAMAGE = registerInt("wolf_cd_ignored_insurmountable_damage");

        public static final AttachmentType<Integer> WOLF_NOTIFY_MIGHT_SKILL_RUPTURE = registerInt("wolf_notify_might_skill_rupture");
        public static final AttachmentType<Integer> WOLF_NOTIFY_BLOODLUST_SKILL_PARALYZE = registerInt("wolf_notify_bloodlust_skill_paralyze");
        public static final AttachmentType<Integer> WOLF_NOTIFY_BLESSING_OF_THE_VERDANT_WIND = registerInt("wolf_notify_blessing_of_the_verdant_wind");

        public static final AttachmentType<Integer> WOLF_BENEDICTION = registerInt("wolf_benediction");

        public static final AttachmentType<BlockPos> WOLF_RESPAWN_POINT = registerBlockPos("wolf_respawn_point");
        public static final AttachmentType<String> WOLF_PARENTS_ID = registerString("wolf_parents_id");
        public static final AttachmentType<String> WOLF_BABY_ID = registerString("wolf_baby_id");
    }
    public static class player {
        public static final AttachmentType<Boolean> PLAYER_IS_DEV_MODE = registerBool("player_is_dev_mode");

        public static final AttachmentType<Integer> PLAYER_RANDOM_INT_10 = registerInt("player_random_int_10");

        /** Sync copy for PLAYER_WOLF_SOULS_COUNTER to avoid lag spikes **/
        public static final AttachmentType<List<CompoundTag>> PLAYER_WOLF_SOULS = registerCompoundList("player_wolf_souls");
        public static final AttachmentType<Integer> PLAYER_WOLF_SOULS_COUNTER = registerInt("player_wolf_souls_counter");

        public static final AttachmentType<Integer> PLAYER_RECEIVED_ENCHANTMENTS_HANDBOOK = registerInt("player_received_enchantments_handbook");
        public static final AttachmentType<Integer> PLAYER_RECEIVED_EFFECTS_HANDBOOK = registerInt("player_received_effects_handbook");
        public static final AttachmentType<Integer> PLAYER_RECEIVED_ITEMS_HANDBOOK = registerInt("player_received_items_handbook");
        public static final AttachmentType<Integer> PLAYER_RECEIVED_FEATURES_HANDBOOK = registerInt("player_received_features_handbook");

        public static final AttachmentType<Integer> PLAYER_CD_BLESSING_OF_THE_VERDANT_WIND = registerInt("player_cd_blessing_of_the_verdant_wind");
        public static final AttachmentType<Integer> PLAYER_NOTIFY_BLESSING_OF_THE_VERDANT_WIND = registerInt("player_notify_blessing_of_the_verdant_wind");
        public static final AttachmentType<Integer> PLAYER_VILLAGER_ATROCITY_COUNT = registerInt("player_villager_atrocity_count");
        public static final AttachmentType<Integer> PLAYER_WOLF_ATROCITY_COUNT = registerInt("player_wolf_atrocity_count");
        public static final AttachmentType<BlockPos> PLAYER_RESPAWN_POINT = registerBlockPos("player_respawn_point");
    }
    public static class villager {
        public static final AttachmentType<Boolean> VILLAGER_IS_VERDANT_TYPE = registerBool("villager_is_verdant_type");

        public static final AttachmentType<Integer> VILLAGER_CD_HEAL_OTHERS = registerInt("villager_cd_heal_others");
        public static final AttachmentType<Integer> VILLAGER_CD_HEAL_WOLF = registerInt("villager_cd_heal_wolf");
        public static final AttachmentType<Integer> VILLAGER_CD_HEAL_IRON_GOLEM = registerInt("villager_cd_heal_iron_golem");
        public static final AttachmentType<Integer> VILLAGER_CD_DISCOUNT_REROLL = registerInt("villager_cd_discount_reroll");

        public static final AttachmentType<Float> VILLAGER_DISCOUNT_MODIFIER = registerFloat("villager_discount_modifier");
    }




    public static void register() {
        final List<AttachmentType<?>> GLOBAL_ATTACHMENTS = List.of(
                ENTITY_HAS_VERDANT_OMEN
        );
        final List<AttachmentType<?>> WOLF_ATTACHMENTS = List.of(
                wolf.WOLF_IS_VERDANT_TYPE,
                wolf.WOLF_IS_VILLAGE_GUARD,
                wolf.WOLF_TIMER_AIR_SUPPLY,
                wolf.WOLF_NOTIFY_AIR_SUPPLY,
                wolf.WOLF_CD_BLESSING_OF_THE_VERDANT_WIND,
                wolf.WOLF_CD_BLOODLUST_SKILL_PARALYZE,
                wolf.WOLF_CD_MIGHT_SKILL_RUPTURE,
                wolf.WOLF_NOTIFY_MIGHT_SKILL_RUPTURE,
                wolf.WOLF_NOTIFY_BLOODLUST_SKILL_PARALYZE,
                wolf.WOLF_NOTIFY_BLESSING_OF_THE_VERDANT_WIND,
                wolf.WOLF_BENEDICTION,
                wolf.WOLF_TRY_SAVE_POINTS,
                wolf.WOLF_TRY_SAVE_STATUS,
                wolf.WOLF_PARENTS_ID,
                wolf.WOLF_RESPAWN_POINT
        );
        final List<AttachmentType<?>> VILLAGER_ATTACHMENTS = List.of(
                villager.VILLAGER_IS_VERDANT_TYPE,
                villager.VILLAGER_CD_HEAL_OTHERS,
                villager.VILLAGER_CD_HEAL_WOLF,
                villager.VILLAGER_CD_HEAL_IRON_GOLEM,
                villager.VILLAGER_CD_DISCOUNT_REROLL,
                villager.VILLAGER_DISCOUNT_MODIFIER
        );
        final List<AttachmentType<?>> PLAYER_ATTACHMENTS = List.of(
                player.PLAYER_IS_DEV_MODE,

                player.PLAYER_RECEIVED_ENCHANTMENTS_HANDBOOK,
                player.PLAYER_RECEIVED_EFFECTS_HANDBOOK,
                player.PLAYER_RECEIVED_ITEMS_HANDBOOK,

                player.PLAYER_WOLF_SOULS,
                player.PLAYER_WOLF_SOULS_COUNTER,
                player.PLAYER_VILLAGER_ATROCITY_COUNT,
                player.PLAYER_WOLF_ATROCITY_COUNT,
                player.PLAYER_CD_BLESSING_OF_THE_VERDANT_WIND,
                player.PLAYER_NOTIFY_BLESSING_OF_THE_VERDANT_WIND,
                player.PLAYER_RESPAWN_POINT
        );
        final List<AttachmentType<?>> BLOCK_ATTACHMENTS = List.of(
                windCore.ENTITY_PRESSURE_DIFFERENCE,
                windCore.ENTITY_HAS_IMPLODED
        );

        int TOTAL = GLOBAL_ATTACHMENTS.size() + WOLF_ATTACHMENTS.size() + VILLAGER_ATTACHMENTS.size() + PLAYER_ATTACHMENTS.size() + BLOCK_ATTACHMENTS.size();

        TOTVW.sendClassRegisterLog(
                "Custom Attachments (" +
                        "Global: " + GLOBAL_ATTACHMENTS.size() + VWPlayerPrefs.register() + ", " +
                        "Wolf: " + WOLF_ATTACHMENTS.size() + ", " +
                        "Villager: " + VILLAGER_ATTACHMENTS.size() + ", " +
                        "Player: " + PLAYER_ATTACHMENTS.size() + ", " +
                        "Blocks: " + BLOCK_ATTACHMENTS.size() + ") " +
                        TOTAL + " in total has been"
        );
    }
}