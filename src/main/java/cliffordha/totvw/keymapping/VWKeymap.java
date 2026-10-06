package cliffordha.totvw.keymapping;

import cliffordha.totvw.TOTVW;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;

public class VWKeymap {
    private static final String PREFIX = "key.tales_of_the_verdant_wind.";
    public static final String WOLF_CONFIG_KEY = registerKey("wolf_config");

    public static final KeyMapping WOLF_CONFIG = KeyMappingHelper.registerKeyMapping(
            new KeyMapping(WOLF_CONFIG_KEY, InputConstants.KEY_TAB, KeyMapping.Category.GAMEPLAY));


    public static boolean isWolfConfigKeyDown() {
        return !WOLF_CONFIG.isUnbound() && WOLF_CONFIG.isDown();
    }

    public static void register() {
        TOTVW.sendClassRegisterLog("KeyMappings");
    }
    private static String registerKey(String key) {
        return PREFIX + key;
    }
}
