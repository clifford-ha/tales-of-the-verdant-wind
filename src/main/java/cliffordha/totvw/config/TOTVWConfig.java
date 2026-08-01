package cliffordha.totvw.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;

@Config(name = "tales-of-the-verdant-wind")
public class TOTVWConfig implements ConfigData {

    public boolean CLIENT_TRANSLATE_LANGUAGE = false;
    public boolean CLIENT_BLOODLUST_EFFECT_OVERLAY = false;
    public boolean CLIENT_ENABLE_NOTIFIERS = true;
    public boolean CLIENT_MOD_SOUNDS = true;



    public boolean SERVER_WOLF_DMG_DISTRIBUTION = true;

    public int SERVER_WOLF_PLAYER_SCAN_DISTANCE = 16;
    public int SERVER_BENEDICTION_HEALTH_THRESHOLD = 30;
    public int SERVER_MAX_WOLF_BENEDICTION_STACK = 3;
    public boolean SERVER_ALWAYS_TRIGGER_BLESSING = false;
    public boolean SERVER_WOLF_SHARES_BENEDICTION_STACK = true;
    public boolean SERVER_TELEPORT_AFTER_SAVE = true;
    public boolean SERVER_TELL_OWNER_WHO_HURT_WOLF = true;

    public boolean SERVER_SKILL_COOLDOWNS = true;
    public boolean SERVER_ITEM_COOLDOWNS = true;
    public boolean SERVER_OTHER_COOLDOWNS = true;



    public boolean DEBUG_PRINT_LOGS = false;
    public boolean BLOCK_UPDATE_WIND_CORE_LOGS = false;
    public boolean MIXIN_UPDATE_LOGS = false;

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