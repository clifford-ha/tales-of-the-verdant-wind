package cliffordha.totvw.registry;

import cliffordha.totvw.TOTVW;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {
    public static final SoundEvent WOLF_HOWL_A = registerSoundEvent("alex-jauk-howling-wolf-268894");
    public static final SoundEvent WOLF_HOWL_B1 = registerSoundEvent("dragon-studio-wolf-howl-2-359870");
    public static final SoundEvent WOLF_HOWL_B2 = registerSoundEvent("dragon-studio-wolf-howl-359873");
    public static final SoundEvent WOLF_HOWL_B3 = registerSoundEvent("dragon-studio-howling-wolves-515977");

    public static final SoundEvent WOLF_SKILL_PARALYZE = registerSoundEvent("dragon-studio-sci-fi-portal-jump-05-416165");

    public static final SoundEvent NOTIFY = registerSoundEvent("dragon-studio-notification-bell-sound-1-376885");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    public static void registerModSounds() {
        TOTVW.sendLog("Sounds");
    }
}
