package cliffordha.totvw.registry;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.entity.VWTrustInteractionData;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.Identifier;

public class VWAttachments {
    public static final AttachmentType<VWTrustInteractionData> ENTITY_TRUSTED_MOB_DATA = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "entity_trusted_mob_data"),
            builder -> builder.persistent(VWTrustInteractionData.CODEC).initializer(VWTrustInteractionData::create)
    );

    public static final AttachmentType<Boolean> ENTITY_IS_PARALYZED = registerBool("entity_is_paralyzed");
    public static final AttachmentType<Integer> ENTITY_TRUST_POINTS = registerInt("entity_trust_points");
    public static final AttachmentType<Integer> ENTITY_TRUST_COOLDOWN = registerInt("entity_trust_cooldown");
    public static final AttachmentType<Boolean> ENTITY_HAS_VERDANT_OMEN = registerBool("entity_has_verdant_omen");

    public static class Player {
        public static final AttachmentType<Boolean> PLAYER_IS_DEV_MODE = registerBool("player_is_dev_mode");
        public static final AttachmentType<Integer> PLAYER_CD_BLESSING_OF_THE_VERDANT_WIND = registerInt("player_cd_blessing_of_the_verdant_wind");
        public static final AttachmentType<Integer> PLAYER_NOTIFY_BLESSING_OF_THE_VERDANT_WIND = registerInt("player_notify_blessing_of_the_verdant_wind");
        public static final AttachmentType<Integer> PLAYER_VILLAGER_ATROCITY_COUNT = registerInt("player_villager_atrocity_count");
        public static final AttachmentType<Integer> PLAYER_WOLF_ATROCITY_COUNT = registerInt("player_wolf_atrocity_count");
    }
    public static class Wolf {
        public static final AttachmentType<Boolean> WOLF_IS_VERDANT_TYPE = registerBool("wolf_is_verdant_type");
        public static final AttachmentType<Boolean> WOLF_IS_VILLAGE_GUARD = registerBool("wolf_is_village_guard");

        public static final AttachmentType<Integer> WOLF_TIMER_AIR_SUPPLY = registerInt("wolf_timer_air_supply");
        public static final AttachmentType<Integer> WOLF_NOTIFY_AIR_SUPPLY = registerInt("wolf_notify_air_supply");

        public static final AttachmentType<Integer> WOLF_TRY_SAVE_POINTS = registerInt("wolf_try_save_points");
        public static final AttachmentType<Integer> WOLF_TRY_SAVE_STATUS = registerInt("wolf_try_save_pending_points");

        public static final AttachmentType<Integer> WOLF_CD_BLESSING_OF_THE_VERDANT_WIND = registerInt("wolf_cd_blessing_of_the_verdant_wind");
        public static final AttachmentType<Integer> WOLF_CD_BLOODLUST_SKILL_PARALYZE = registerInt("wolf_cd_bloodlust_skill_paralyze");
        public static final AttachmentType<Integer> WOLF_CD_MIGHT_SKILL_RUPTURE = registerInt("wolf_cd_might_skill_rupture");


        public static final AttachmentType<Integer> WOLF_NOTIFY_MIGHT_SKILL_RUPTURE = registerInt("wolf_notify_might_skill_rupture");
        public static final AttachmentType<Integer> WOLF_NOTIFY_BLOODLUST_SKILL_PARALYZE = registerInt("wolf_notify_bloodlust_skill_paralyze");
        public static final AttachmentType<Integer> WOLF_NOTIFY_BLESSING_OF_THE_VERDANT_WIND = registerInt("wolf_notify_blessing_of_the_verdant_wind");

        public static final AttachmentType<Integer> WOLF_BENEDICTION = registerInt("wolf_benediction");
        public static final AttachmentType<Integer> WOLF_PERMANENT_VERDANT_BLESSING = registerInt("wolf_permanent_verdant_blessing");
    }

    public static class Villager {
        public static final AttachmentType<Boolean> VILLAGER_IS_VERDANT_TYPE = registerBool("villager_is_verdant_type");

        public static final AttachmentType<Integer> VILLAGER_CD_HEAL_OTHERS = registerInt("villager_cd_heal_others");
        public static final AttachmentType<Integer> VILLAGER_CD_HEAL_WOLF = registerInt("villager_cd_heal_wolf");
        public static final AttachmentType<Integer> VILLAGER_CD_HEAL_IRON_GOLEM = registerInt("villager_cd_heal_iron_golem");
        public static final AttachmentType<Integer> VILLAGER_CD_DISCOUNT_REROLL = registerInt("villager_cd_discount_reroll");

        public static final AttachmentType<Float> VILLAGER_DISCOUNT_MODIFIER = registerFloat("villager_discount_modifier");
    }

    private static AttachmentType<Integer> registerInt(String name) {
        return AttachmentRegistry.create(
                Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name),
                builder -> builder.persistent(Codec.INT).initializer(() -> 0)
        );
    }
    private static AttachmentType<Long> registerLong(String name) {
        return AttachmentRegistry.create(
                Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name),
                builder -> builder.persistent(Codec.LONG).initializer(() -> 0L)
        );
    }
    private static AttachmentType<Boolean> registerBool(String name) {
        return AttachmentRegistry.create(
                Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name),
                builder -> builder.persistent(Codec.BOOL).initializer(() -> false)
        );
    }
    private static AttachmentType<Float> registerFloat(String name) {
        return AttachmentRegistry.create(
                Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name),
                builder -> builder.persistent(Codec.FLOAT).initializer(() -> 0.0f)
        );
    }

    public static void register() {
        AttachmentType<?>[] globalAttachments = {
                ENTITY_TRUSTED_MOB_DATA,
                ENTITY_IS_PARALYZED,
                ENTITY_TRUST_POINTS,
                ENTITY_TRUST_COOLDOWN,
                ENTITY_HAS_VERDANT_OMEN,
        };

        AttachmentType<?>[] playerAttachments = {
                Player.PLAYER_IS_DEV_MODE,
                Player.PLAYER_VILLAGER_ATROCITY_COUNT,
                Player.PLAYER_WOLF_ATROCITY_COUNT,
                Player.PLAYER_CD_BLESSING_OF_THE_VERDANT_WIND,
                Player.PLAYER_NOTIFY_BLESSING_OF_THE_VERDANT_WIND,
        };

        AttachmentType<?>[] wolfAttachments = {
                Wolf.WOLF_IS_VERDANT_TYPE,
                Wolf.WOLF_IS_VILLAGE_GUARD,
                Wolf.WOLF_TIMER_AIR_SUPPLY,
                Wolf.WOLF_NOTIFY_AIR_SUPPLY,
                Wolf.WOLF_CD_BLESSING_OF_THE_VERDANT_WIND,
                Wolf.WOLF_CD_BLOODLUST_SKILL_PARALYZE,
                Wolf.WOLF_CD_MIGHT_SKILL_RUPTURE,
                Wolf.WOLF_NOTIFY_MIGHT_SKILL_RUPTURE,
                Wolf.WOLF_NOTIFY_BLOODLUST_SKILL_PARALYZE,
                Wolf.WOLF_NOTIFY_BLESSING_OF_THE_VERDANT_WIND,
                Wolf.WOLF_BENEDICTION,
                Wolf.WOLF_PERMANENT_VERDANT_BLESSING,
                Wolf.WOLF_TRY_SAVE_POINTS,
                Wolf.WOLF_TRY_SAVE_STATUS,
        };

        AttachmentType<?>[] villagerAttachments = {
                Villager.VILLAGER_IS_VERDANT_TYPE,
                Villager.VILLAGER_CD_HEAL_OTHERS,
                Villager.VILLAGER_CD_HEAL_WOLF,
                Villager.VILLAGER_CD_HEAL_IRON_GOLEM,
                Villager.VILLAGER_CD_DISCOUNT_REROLL,
                Villager.VILLAGER_DISCOUNT_MODIFIER,
        };
        int total = globalAttachments.length + playerAttachments.length + wolfAttachments.length + villagerAttachments.length;

        TOTVW.sendClassRegisterLog(
                "Custom Attachments (" +
                        "Global: " + globalAttachments.length + ", " +
                        "Player: " + playerAttachments.length + ", " +
                        "Wolf: " + wolfAttachments.length + ", " +
                        "Villager: " + villagerAttachments.length + " ) " +
                        total + " in total has been"
        );
    }
}