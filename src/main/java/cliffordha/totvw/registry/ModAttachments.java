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

    public static class Player {
        public static final AttachmentType<Integer> CD_BLESSING_OF_THE_VERDANT_WIND = registerInt("cd_player_blessing_of_the_verdant_wind");
        public static final AttachmentType<Integer> NOTIFY_BLESSING_OF_THE_VERDANT_WIND = registerInt("notify_player_blessing_of_the_verdant_wind");
    }
    public static class Wolf {
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

    public static void registerModAttachments() {
        TOTVW.sendLog("Custom Attachments");
    }
}