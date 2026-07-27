package cliffordha.totvw.config;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.VWColors;
import me.shedaniel.clothconfig2.api.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TOTVWConfigScreen {
    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal(TOTVW.MOD_NAME).withColor(VWColors.VERDANT_WIND))
                .transparentBackground()
                .setSavingRunnable(TOTVWConfig::save);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory client = builder.getOrCreateCategory(
                Component.literal("Client"));

        /*
        general.addEntry(
                entryBuilder.startBooleanToggle(
                                Component.literal("Use New Language Set"),
                                TOTVWConfig.get().useNewLanguageSet)
                        .setDefaultValue(false)
                        .setTooltip(text("When circumstances are met, certain item \ntooltips will show untranslated version of the text"))
                        .setSaveConsumer(value -> TOTVWConfig.get().useNewLanguageSet = value)
                        .build()
        );*/
        client.addEntry(
                entryBuilder.startBooleanToggle(
                                Component.literal("Use New Sounds"),
                                TOTVWConfig.get().ENABLE_NEW_SOUNDS)
                        .setDefaultValue(true)
                        .setTooltip(text("Wolf howls when within certain biomes\nOther sounds will be added in the future"))
                        .setSaveConsumer(value -> TOTVWConfig.get().ENABLE_NEW_SOUNDS = value)
                        .build()
        );
        client.addEntry(
                entryBuilder.startBooleanToggle(
                                Component.literal("Bloodlust Effect Screen Overlay"),
                                TOTVWConfig.get().BLOODLUST_SCREEN_OVERLAY)
                        .setDefaultValue(false)
                        .setTooltip(text("When the player has the Bloodlust Effect, an overlay\nwill be displayed on the whole screen.\nThe strength of the overlay depends on the player's health.\n\n§8§oDisabled by default for safety purposes."))
                        .setSaveConsumer(value -> TOTVWConfig.get().BLOODLUST_SCREEN_OVERLAY = value)
                        .build()
        );



        ConfigCategory server = builder.getOrCreateCategory(Component.literal("Server"));
        server.addEntry(
                entryBuilder.startBooleanToggle(
                                Component.literal("Wolf Armor Damage Distribution"),
                                TOTVWConfig.get().WOLF_ARMOR_DMG_DISTRIBUTION)
                        .setDefaultValue(true)
                        .setTooltip(text("Allow distributing damage to both the armor and the wolf"))
                        .setSaveConsumer(value -> TOTVWConfig.get().WOLF_ARMOR_DMG_DISTRIBUTION = value)
                        .build()
        );
        server.addEntry(
                entryBuilder.startBooleanToggle(
                                Component.literal("Use Notifiers"),
                                TOTVWConfig.get().ENABLE_NOTIFIERS)
                        .setDefaultValue(true)
                        .setTooltip(text("When enabled, notifications will be sent to chat or overlay"))
                        .setSaveConsumer(value -> TOTVWConfig.get().ENABLE_NOTIFIERS = value)
                        .build()
        );

        var benedictionSettings = entryBuilder.startSubCategory(Component.literal("Enchantment: Benediction of the Verdant Mountains"));
        benedictionSettings.add(
                entryBuilder.startIntSlider(
                                Component.literal("Max Wolf/Player Scan"),
                                TOTVWConfig.get().MAX_WOLF_PLAYER_SCAN_DISTANCE, 1, 64)
                        .setDefaultValue(16)
                        .setTooltip(text("Max chunk scan distance for enchantment effects to trigger.\n§8§oHigher values may affect performance!"))
                        .setSaveConsumer(value -> TOTVWConfig.get().MAX_WOLF_PLAYER_SCAN_DISTANCE = value)
                        .build()
        );
        benedictionSettings.add(
                entryBuilder.startIntSlider(
                                Component.literal("Low Health Threshold"),
                                TOTVWConfig.get().LOW_HEALTH_THRESHOLD, 15, 90)
                        .setDefaultValue(30)
                        .setTooltip(text("Trigger Benediction when health threshold is met"))
                        .setSaveConsumer(value -> TOTVWConfig.get().LOW_HEALTH_THRESHOLD = value)
                        .build()
        );
        server.addEntry(benedictionSettings.build());

        var enchantmentSkillSettings = entryBuilder.startSubCategory(Component.literal("Enchantment Skills"));
        enchantmentSkillSettings.add(
                entryBuilder.startBooleanToggle(
                                Component.literal("Attachment Cooldowns"),
                                TOTVWConfig.get().ATTACHMENT_SKILL_CD)
                        .setDefaultValue(true)
                        .setTooltip(text("Used for skill and item cooldowns"))
                        .setSaveConsumer(value -> TOTVWConfig.get().ATTACHMENT_SKILL_CD = value)
                        .build()
        );
        enchantmentSkillSettings.add(
                entryBuilder.startBooleanToggle(
                                Component.literal("Other Attachment Cooldowns"),
                                TOTVWConfig.get().OTHER_ATTACHMENT_CD)
                        .setDefaultValue(true)
                        .setTooltip(text("Used for other time-based cooldowns"))
                        .setSaveConsumer(value -> TOTVWConfig.get().OTHER_ATTACHMENT_CD = value)
                        .build()
        );
        server.addEntry(enchantmentSkillSettings.build());


        ConfigCategory debug = builder.getOrCreateCategory(Component.literal("Debug"));
        debug.addEntry(
                entryBuilder.startBooleanToggle(
                                Component.literal("SEND LOGS"),
                                TOTVWConfig.get().DEBUG_PRINT_LOGS)
                        .setDefaultValue(false)
                        .setSaveConsumer(value -> TOTVWConfig.get().DEBUG_PRINT_LOGS = value)
                        .build()
        );

        var debugCategory = entryBuilder.startSubCategory(Component.literal("Block Updates"));
        debugCategory.add(entryBuilder.startBooleanToggle(
                                Component.literal("Lodestone Wind Core"),
                                TOTVWConfig.get().BLOCK_UPDATE_WIND_CORE)
                        .setDefaultValue(false)
                        .setSaveConsumer(value -> TOTVWConfig.get().BLOCK_UPDATE_WIND_CORE = value)
                        .build()
                );
        debug.addEntry(debugCategory.build());

        return builder.build();
    }
    private static Component text(String text) {
        return Component.literal(text).withColor(VWColors.DEFAULT_MUTED);
    }
}