package cliffordha.totvw.config;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.ModColors;
import me.shedaniel.clothconfig2.api.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TOTVWConfigScreen {
    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal(TOTVW.MOD_NAME).withColor(ModColors.VERDANT_WIND))
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
                                TOTVWConfig.get().newSounds)
                        .setDefaultValue(true)
                        .setTooltip(text("Wolf howls when within certain biomes\nOther sounds will be added in the future"))
                        .setSaveConsumer(value -> TOTVWConfig.get().newSounds = value)
                        .build()
        );
        client.addEntry(
                entryBuilder.startBooleanToggle(
                                Component.literal("Bloodlust Effect Screen Overlay"),
                                TOTVWConfig.get().bloodlustScreenOverlay)
                        .setDefaultValue(false)
                        .setTooltip(text("When the player has the Bloodlust Effect, an overlay\nwill be displayed on the whole screen.\nThe strength of the overlay depends on the player's health.\n\n§8§oDisabled by default for safety purposes."))
                        .setSaveConsumer(value -> TOTVWConfig.get().bloodlustScreenOverlay = value)
                        .build()
        );



        ConfigCategory server = builder.getOrCreateCategory(
                Component.literal("Server"));

        server.addEntry(
                entryBuilder.startIntSlider(
                                Component.literal("Max Wolf/Player Scan"),
                                TOTVWConfig.get().maxWolfPlayerDistance, 1, 64)
                        .setDefaultValue(16)
                        .setTooltip(text("Set a max chunk scan distance for enchantment effects to trigger.\n§8§oHigher values may affect performance!"))
                        .setSaveConsumer(value -> TOTVWConfig.get().maxWolfPlayerDistance = value)
                        .build()
        );
        server.addEntry(
                entryBuilder.startIntSlider(
                                Component.literal("Low Health Threshold"),
                                TOTVWConfig.get().lowHealthThreshold, 15, 90)
                        .setDefaultValue(30)
                        .setTooltip(text("Set a health threshold for when to trigger enchantment effects."))
                        .setSaveConsumer(value -> TOTVWConfig.get().lowHealthThreshold = value)
                        .build()
        );
        server.addEntry(
                entryBuilder.startBooleanToggle(
                                Component.literal("Wolf Armor Damage Distribution"),
                                TOTVWConfig.get().wolfArmorDamageDistribution)
                        .setDefaultValue(true)
                        .setTooltip(text("Allow distributing damage to both the armor and the wolf"))
                        .setSaveConsumer(value -> TOTVWConfig.get().wolfArmorDamageDistribution = value)
                        .build()
        );
        server.addEntry(
                entryBuilder.startBooleanToggle(
                                Component.literal("Use Notifiers"),
                                TOTVWConfig.get().useNotifiers)
                        .setDefaultValue(true)
                        .setTooltip(text("Some enchantments and effects will send \na message to the player when activated."))
                        .setSaveConsumer(value -> TOTVWConfig.get().useNotifiers = value)
                        .build()
        );
        server.addEntry(
                entryBuilder.startBooleanToggle(
                                Component.literal("Attachment Cooldowns"),
                                TOTVWConfig.get().attachmentSkillCD)
                        .setDefaultValue(true)
                        .setTooltip(text("Used for skill and item cooldowns"))
                        .setSaveConsumer(value -> TOTVWConfig.get().attachmentSkillCD = value)
                        .build()
        );
        server.addEntry(
                entryBuilder.startBooleanToggle(
                                Component.literal("Other Attachment Cooldowns"),
                                TOTVWConfig.get().otherAttachmentCD)
                        .setDefaultValue(true)
                        .setTooltip(text("Used for other time-based cooldowns"))
                        .setSaveConsumer(value -> TOTVWConfig.get().otherAttachmentCD = value)
                        .build()
        );



        ConfigCategory debug = builder.getOrCreateCategory(
                Component.literal("Debug"));

        debug.addEntry(
                entryBuilder.startBooleanToggle(
                                Component.literal("SEND LOGS"),
                                TOTVWConfig.get().sendLog)
                        .setDefaultValue(false)
                        .setSaveConsumer(value -> TOTVWConfig.get().sendLog = value)
                        .build()
        );

        return builder.build();
    }
    private static Component text(String text) {
        return Component.literal(text).withColor(ModColors.DEFAULT_MUTED);
    }
}