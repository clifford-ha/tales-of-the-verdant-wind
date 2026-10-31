package cliffordha.totvw.config;

import cliffordha.totvw.TOTVW;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class VWConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
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

    public boolean SERVER_WOLF_DMG_DISTRIBUTION = true;
    public boolean SERVER_WOLF_BEHAVIOUR_EXTENSIONS = true;

    public boolean SERVER_SKILL_COOLDOWNS = true;
    public boolean SERVER_ITEM_COOLDOWNS = true;
    public boolean SERVER_OTHER_COOLDOWNS = true;

    public boolean LOG_WINDCORE_ENERGY_CHANGES = false;
    public boolean LOG_WINDCORE_RECORD = false;
    public boolean LOG_WINDCORE_ENTITY_EVENT = false;
    public boolean LOG_WINDCORE_ENTITY_CONVERSION = false;

    public static VWConfig get() {
        return INSTANCE;
    }


    public static void save() {
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException _) {
            TOTVW.sendError("Failed to save config!");
        }
    }
    public static void loadAndVerify() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                INSTANCE = GSON.fromJson(reader, VWConfig.class);
            } catch (IOException e) {
                INSTANCE = new VWConfig();
            }
        } else {
            save();
        }

        boolean requiresFixing = false;

        try (BufferedReader reader = Files.newBufferedReader(CONFIG_PATH)) {
            INSTANCE = GSON.fromJson(reader, VWConfig.class);

            if (INSTANCE == null) {
                INSTANCE = new VWConfig();
                requiresFixing = true;
            }
        } catch (JsonSyntaxException e) {
            TOTVW.sendWarning("Config file is malformed! Resetting to default values.");
            INSTANCE = new VWConfig();
            requiresFixing = true;

        } catch (IOException e) {
            TOTVW.sendError("Failed to read config file!");

        }

        if (INSTANCE.verifyAndSanitizeValues()) {
            requiresFixing = true;
        }

        if (requiresFixing) {
            save();
        }
    }
    private boolean verifyAndSanitizeValues() {
        int count = 0;

        if (this.SYNCED_BENEDICTION_HEALTH_THRESHOLD > 50) {
            this.SYNCED_BENEDICTION_HEALTH_THRESHOLD = 50;
            count++;
        }

        if (this.SYNCED_WOLF_TP_METHOD > 1) {
            this.SYNCED_WOLF_TP_METHOD = 1;
            count++;
        }
        if (this.SYNCED_PLAYER_TP_METHOD > 1) {
            this.SYNCED_PLAYER_TP_METHOD = 1;
            count++;
        }

        return count > 0;
    }
}
