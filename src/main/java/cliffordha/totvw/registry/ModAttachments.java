package cliffordha.totvw.registry;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.entity.player.InteractionData;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.Identifier;

public class ModAttachments {
    public static final AttachmentType<InteractionData> INTERACTION_DATA = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "interaction_data"),
            builder -> builder.persistent(InteractionData.CODEC).initializer(InteractionData::create)
    );

    public static final AttachmentType<Integer> TRUST_POINTS = registerInt("betrayal_strikes");
    public static final AttachmentType<Integer> TRUST_COOLDOWN = registerInt("trust_cooldown");
    public static final AttachmentType<Boolean> HAS_VERDANT_OMEN = registerBool("has_verdant_omen");

    public static class Player {
        public static final AttachmentType<Integer> CD_BLESSING_OF_THE_VERDANT_WIND = registerInt("cd_player_blessing_of_the_verdant_wind");
        public static final AttachmentType<Integer> NOTIFY_BLESSING_OF_THE_VERDANT_WIND = registerInt("notify_player_blessing_of_the_verdant_wind");
    }
    public static class Wolf {
        public static final AttachmentType<Boolean> IS_VERDANT_TYPE = registerBool("is_verdant_type");
        public static final AttachmentType<Boolean> IS_VILLAGE_GUARD = registerBool("is_village_guard");
        public static final AttachmentType<Boolean> HAS_TRIED_PROTECTING_VILLAGER = registerBool("has_tried_protecting_villager");

        public static final AttachmentType<Integer> TIMER_AIR_SUPPLY = registerInt("timer_wolf_air_supply");
        public static final AttachmentType<Integer> NOTIFY_AIR_SUPPLY = registerInt("air_supply_notify");


        public static final AttachmentType<Integer> CD_BLESSING_OF_THE_VERDANT_WIND = registerInt("cd_wolf_blessing_of_the_verdant_wind");
        public static final AttachmentType<Integer> CD_BLOODLUST_SKILL_PARALYZE = registerInt("cd_wolf_might_skill_paralyze");
        public static final AttachmentType<Integer> CD_MIGHT_SKILL_RUPTURE = registerInt("cd_wolf_bloodlust_skill_rupture");


        public static final AttachmentType<Integer> NOTIFY_MIGHT_SKILL_RUPTURE = registerInt("notify_wolf_bloodlust_skill_rupture");
        public static final AttachmentType<Integer> NOTIFY_BLOODLUST_SKILL_PARALYZE = registerInt("notify_wolf_might_skill_paralyze");
        public static final AttachmentType<Integer> NOTIFY_BLESSING_OF_THE_VERDANT_WIND = registerInt("notify_wolf_blessing_of_the_verdant_wind");

        public static final AttachmentType<Integer> WOLF_BENEDICTION = registerInt("wolf_benediction");
        public static final AttachmentType<Integer> VERDANT_BIOME_PERMANENT_BLESSING = registerInt("verdant_biome_permanent_biome");
    }

    public static class Villager {
        public static final AttachmentType<Boolean> IS_VERDANT_TYPE = registerBool("is_verdant_type");
        public static final AttachmentType<Boolean> HAS_SUMMONED_WOLF = registerBool("has_summoned_wolf");
        public static final AttachmentType<Integer> CD_DISCOUNT_REROLL = registerInt("cd_discount_reroll");
        public static final AttachmentType<Float> DISCOUNT_MODIFIER = registerFloat("discount_modifier");
    }

    private static AttachmentType<Integer> registerInt(String name) {
        return AttachmentRegistry.create(
                Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name),
                builder -> builder.persistent(Codec.INT).initializer(() -> 0)
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

    public static void registerModAttachments() {
        AttachmentType<?>[] attachmentTypes = {
                INTERACTION_DATA,
                TRUST_POINTS,
                TRUST_COOLDOWN,
                HAS_VERDANT_OMEN,

                Player.CD_BLESSING_OF_THE_VERDANT_WIND,
                Player.NOTIFY_BLESSING_OF_THE_VERDANT_WIND,

                Wolf.IS_VERDANT_TYPE,
                Wolf.IS_VILLAGE_GUARD,
                Wolf.TIMER_AIR_SUPPLY,
                Wolf.NOTIFY_AIR_SUPPLY,
                Wolf.CD_BLESSING_OF_THE_VERDANT_WIND,
                Wolf.CD_BLOODLUST_SKILL_PARALYZE,
                Wolf.CD_MIGHT_SKILL_RUPTURE,
                Wolf.NOTIFY_MIGHT_SKILL_RUPTURE,
                Wolf.NOTIFY_BLOODLUST_SKILL_PARALYZE,
                Wolf.NOTIFY_BLESSING_OF_THE_VERDANT_WIND,
                Wolf.WOLF_BENEDICTION,
                Wolf.VERDANT_BIOME_PERMANENT_BLESSING,

                Villager.IS_VERDANT_TYPE,
                Villager.CD_DISCOUNT_REROLL,
                Villager.DISCOUNT_MODIFIER
        };
        TOTVW.sendLog("Custom Attachments: " + attachmentTypes.length);
    }
}