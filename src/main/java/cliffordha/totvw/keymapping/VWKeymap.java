package cliffordha.totvw.keymapping;

import cliffordha.totvw.TOTVW;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;

public class VWKeymap {
    private static final String PREFIX = "key.tales_of_the_verdant_wind.";
    public static final String TOTVW_CONFIG_SCREEN = registerKey("wolf_config");
    public static final String TOTVW_STAT_SCREEN = registerKey("status_screen");

    public static final KeyMapping WOLF_CONFIG = KeyMappingHelper.registerKeyMapping(
            new KeyMapping(TOTVW_CONFIG_SCREEN, InputConstants.KEY_LBRACKET, KeyMapping.Category.GAMEPLAY));

    public static final KeyMapping GET_STATUS = KeyMappingHelper.registerKeyMapping(
            new KeyMapping(TOTVW_STAT_SCREEN, InputConstants.UNKNOWN.getValue(), KeyMapping.Category.GAMEPLAY));


    public static boolean isWolfConfigKeyDown() {
        return !WOLF_CONFIG.isUnbound() && WOLF_CONFIG.isDown();
    }
    public static boolean isStatusScreenKeyDown() {
        return !GET_STATUS.isUnbound() && GET_STATUS.isDown();
    }

    public static void register() {
        TOTVW.sendClassRegisterLog("KeyMappings");
    }
    private static String registerKey(String key) {
        return PREFIX + key;
    }
}
