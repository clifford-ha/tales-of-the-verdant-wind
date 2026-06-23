package cliffordha.totvw.registry;

import cliffordha.totvw.TOTVW;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.Identifier;

public class ModAttachments {
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

    private static AttachmentType<Integer> registerInt(String name) {
        return AttachmentRegistry.create(
                Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name),
                builder -> builder.persistent(Codec.INT).initializer(() -> 0)
        );
    }

    public static void registerModAttachments() {
        TOTVW.sendLog("Custom Attachments");
    }
}