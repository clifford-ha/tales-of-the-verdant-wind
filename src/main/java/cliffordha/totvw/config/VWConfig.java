package cliffordha.totvw.config;

import cliffordha.totvw.TOTVW;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class VWConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("tales-of-the-verdant-wind.json");

    private static VWConfig INSTANCE = new VWConfig();

    public boolean CLIENT_ALLOW_EFFECT_OVERLAYS = true;
    public boolean CLIENT_BLOODLUST_EFFECT_OVERLAY = false;
    public boolean CLIENT_ALLOW_LORE_SPOILERS = false;

    public boolean SYNCED_ENABLE_NOTIFIERS = true;
    public boolean SYNCED_SHOW_ATROCITY_COUNTER = false;
    public int SYNCED_BENEDICTION_HEALTH_THRESHOLD = 30;
    public boolean SYNCED_ALWAYS_TRIGGER_BLESSING = false;
    public boolean SYNCED_WOLF_SHARES_BENEDICTION_STACK = true;
    public boolean SYNCED_TELEPORT_AFTER_SAVE = true;
    public int SYNCED_WOLF_TP_METHOD = 0;
    public int SYNCED_PLAYER_TP_METHOD = 0;
    public boolean SYNCED_WOLF_TP_ALL = false;

    public boolean SERVER_MULTI_ATTACK = true;
    public boolean SERVER_WOLF_DMG_DISTRIBUTION = true;

    public boolean SERVER_SKILL_COOLDOWNS = true;
    public boolean SERVER_ITEM_COOLDOWNS = true;
    public boolean SERVER_OTHER_COOLDOWNS = true;

    public boolean LOG_WINDCORE_ENERGY_CHANGES = false;
    public boolean LOG_WINDCORE_RECORD = false;
    public boolean LOG_WINDCORE_ENTITY_EVENT = false;
    public boolean LOG_WINDCORE_ENTITY_CONVERSION = false;

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                INSTANCE = GSON.fromJson(reader, VWConfig.class);
            } catch (IOException e) {
                INSTANCE = new VWConfig();
            }
        }
    }

    public static void save() {
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException _) {
            TOTVW.sendError("Failed to save config!");
        }
    }
    public static VWConfig get() {
        return INSTANCE;
    }
}
