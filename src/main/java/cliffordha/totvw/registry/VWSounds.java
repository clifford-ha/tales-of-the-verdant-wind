package cliffordha.totvw.registry;

import cliffordha.totvw.TOTVW;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class VWSounds {
    public static final SoundEvent WOLF_HOWL_A = registerSoundEvent("wolf_howl_a");
    public static final SoundEvent WOLF_HOWL_B1 = registerSoundEvent("wolf_howl_b1");
    public static final SoundEvent WOLF_HOWL_B2 = registerSoundEvent("wolf_howl_b2");
    public static final SoundEvent WOLF_HOWL_B3 = registerSoundEvent("wolf_howl_b3");

    public static final SoundEvent WOLF_SKILL_PARALYZE = registerSoundEvent( "wolf_skill_paralyze");

    public static final SoundEvent LODESTONE_WIND_CORE_AMBIENT = registerSoundEvent( "lodestone_wind_core_ambient");

    public static final SoundEvent NOTIFY = registerSoundEvent("notify");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    public static void register() {
        TOTVW.sendClassRegisterLog("Sounds");
    }

    /*

    ID | original file name
    ---------------------------
    || Alex Jauk
    wolf_howl_a = alex-jauk-howling-wolf-268894

    || Dragon Studio
    wolf_howl_b1 = dragon-studio-wolf-howl-2-359870
    wolf_howl_b2 = dragon-studio-wolf-howl-359873
    wolf_howl_b3 = dragon-studio-howling-wolves-515977
    wolf_skill_paralyze = dragon-studio-sci-fi-portal-jump-05-416165
    lodestone_wind_core_ambient = dragon-studio-blizzard-wind-463217
    notify = dragon-studio-notification-bell-sound-1-376885

     */
}
