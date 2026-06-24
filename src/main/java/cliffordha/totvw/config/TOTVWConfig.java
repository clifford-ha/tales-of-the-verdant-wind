package cliffordha.totvw.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.shedaniel.cloth.clothconfig.shadowed.com.moandjiezana.toml.Toml;
import me.shedaniel.cloth.clothconfig.shadowed.com.moandjiezana.toml.TomlWriter;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.*;

public class TOTVWConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("tales-of-the-verdant-wind.json");

    private static TOTVWConfig INSTANCE = new TOTVWConfig();


    public boolean useNewLanguageSet = false;
    public int maxWolfPlayerDistance = 16;
    public boolean wolfArmorDamageDistribution = true;
    public int lowHealthThreshold = 30;
    public boolean useNotifiers = true;
    public boolean attachmentSkillCD = true;
    public boolean otherAttachmentCD = true;
    public boolean newSounds = true;
    public boolean bloodlustScreenOverlay = false;

    public boolean sendLog = false;


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