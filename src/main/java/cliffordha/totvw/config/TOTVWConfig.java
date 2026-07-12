package cliffordha.totvw.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.*;

public class TOTVWConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("tales-of-the-verdant-wind.json");

    private static TOTVWConfig INSTANCE = new TOTVWConfig();


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


    public static TOTVWConfig get() {
        return INSTANCE;
    }

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                INSTANCE = GSON.fromJson(reader, TOTVWConfig.class);
            } catch (IOException e) {
                INSTANCE = new TOTVWConfig();
            }
        }
    }

    public static void save() {
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}