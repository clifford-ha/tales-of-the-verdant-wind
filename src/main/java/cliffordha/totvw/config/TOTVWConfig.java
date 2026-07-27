package cliffordha.totvw.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;

@Config(name = "tales-of-the-verdant-wind")
public class TOTVWConfig implements ConfigData {

    public boolean ENABLE_NEW_LANGUAGE_SET = false;
    public boolean BLOODLUST_SCREEN_OVERLAY = false;

    public int MAX_WOLF_PLAYER_SCAN_DISTANCE = 16;
    public boolean WOLF_ARMOR_DMG_DISTRIBUTION = true;
    public int LOW_HEALTH_THRESHOLD = 30;
    public boolean ENABLE_NOTIFIERS = true;
    public boolean ATTACHMENT_SKILL_CD = true;
    public boolean OTHER_ATTACHMENT_CD = true;
    public boolean ENABLE_NEW_SOUNDS = true;

    public boolean DEBUG_PRINT_LOGS = false;
    public boolean BLOCK_UPDATE_WIND_CORE = false;

    public static boolean conditionalToggle(boolean parent, boolean toggle) {
        return parent && toggle;
    }

    public static TOTVWConfig get() {
        return AutoConfig.getConfigHolder(TOTVWConfig.class).getConfig();
    }

    public static void load() {
        AutoConfig.register(TOTVWConfig.class, Toml4jConfigSerializer::new);
    }

    public static void save() {
        AutoConfig.getConfigHolder(TOTVWConfig.class).save();
    }
}