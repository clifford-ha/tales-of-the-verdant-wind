package cliffordha.totvw.config;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.TalesOfTheVerdantWind;
import cliffordha.totvw.registry.VWColors;
import me.shedaniel.clothconfig2.api.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Arrays;

public class TOTVWConfigScreen {
    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal(TOTVW.MOD_NAME).withColor(VWColors.VERDANT_WIND))
                .transparentBackground()
                .setSavingRunnable(TOTVWConfig::save);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory client = builder.getOrCreateCategory(Component.literal("Client"));

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
                                TOTVWConfig.get().CLIENT_MOD_SOUNDS)
                        .setDefaultValue(true)
                        .setTooltip(text(
                                """
                                        When enabled, custom sounds will be used
                                        for various events. Option still in
                                        development and may be removed in the future."""
                        ))
                        .setSaveConsumer(value -> TOTVWConfig.get().CLIENT_MOD_SOUNDS = value)
                        .build()
        );
        client.addEntry(
                entryBuilder.startBooleanToggle(
                                Component.literal("Bloodlust Effect Screen Overlay"),
                                TOTVWConfig.get().CLIENT_BLOODLUST_EFFECT_OVERLAY)
                        .setDefaultValue(false)
                        .setTooltip(text(
                                """
                                        When enabled, if player has the §cBloodlust Effect§r,
                                        an overlay will be displayed on the whole screen.
                                        The strength of the overlay depends on the player's health.
                                        
                                        §8§oDisabled by default for safety purposes."""
                        ))
                        .setSaveConsumer(value -> TOTVWConfig.get().CLIENT_BLOODLUST_EFFECT_OVERLAY = value)
                        .build()
        );



        ConfigCategory server = builder.getOrCreateCategory(Component.literal("Server"));
        server.addEntry(
                entryBuilder.startBooleanToggle(
                                Component.literal("Wolf Armor Damage Distribution"),
                                TOTVWConfig.get().SERVER_WOLF_DMG_DISTRIBUTION)
                        .setDefaultValue(true)
                        .setTooltip(text(
                                """
                                        When enabled, allow redistributing damage
                                        that weren't absorbed by the armor
                                        to both the armor and wolf."""
                        ))
                        .setSaveConsumer(value -> TOTVWConfig.get().SERVER_WOLF_DMG_DISTRIBUTION = value)
                        .build()
        );
        server.addEntry(
                entryBuilder.startBooleanToggle(
                                Component.literal("Use Notifiers"),
                                TOTVWConfig.get().CLIENT_ENABLE_NOTIFIERS)
                        .setDefaultValue(true)
                        .setTooltip(text(
                                "When enabled, notifications will\n"
                                + "be sent to chat or overlay"
                        ))
                        .setSaveConsumer(value -> TOTVWConfig.get().CLIENT_ENABLE_NOTIFIERS = value)
                        .build()
        );

        var benedictionSettings = entryBuilder.startSubCategory(Component.literal("Benediction of the Verdant Mountains"));
        benedictionSettings.add(
                entryBuilder.startIntField(
                                Component.literal("Max Chunk Scan"),
                                TOTVWConfig.get().SERVER_WOLF_PLAYER_SCAN_DISTANCE)
                        .setDefaultValue(16)
                        .setMax(128)
                        .setTooltip(text(
                                """
                                        Max chunk scan distance to check for
                                        wolf or player for enchantment
                                        effects to trigger.
                                        
                                        §8§oHigher values may affect performance!"""
                        ))
                        .setSaveConsumer(value -> TOTVWConfig.get().SERVER_WOLF_PLAYER_SCAN_DISTANCE = value)
                        .build()
        );
        benedictionSettings.add(
                entryBuilder.startIntField(
                                Component.literal("Low Health Threshold"),
                                TOTVWConfig.get().SERVER_BENEDICTION_HEALTH_THRESHOLD)
                        .setDefaultValue(30)
                        .setMax(90)
                        .setMin(10)
                        .setTooltip(text(
                                "Grant §bBlessing of the Verdant Wind§r when\n"
                                + "wolf/owner health threshold (in %) is met"
                        ))
                        .setSaveConsumer(value -> TOTVWConfig.get().SERVER_BENEDICTION_HEALTH_THRESHOLD = value)
                        .build()
        );
        benedictionSettings.add(
                entryBuilder.startBooleanToggle(
                                Component.literal("Share Benediction Stack"),
                                TOTVWConfig.get().SERVER_WOLF_SHARES_BENEDICTION_STACK)
                        .setDefaultValue(true)
                        .setTooltip(text(
                                "When enabled, if wolf has more than\n"
                                + "1 Benediction Stack and their owner"
                                + "enters dying state within " + TOTVWConfig.get().SERVER_WOLF_PLAYER_SCAN_DISTANCE + " chunks,\n"
                                + "wolf will consume §b1§r stack to revive owner."
                        ))
                        .setSaveConsumer(value -> TOTVWConfig.get().SERVER_WOLF_SHARES_BENEDICTION_STACK = value)
                        .build()
        );
        benedictionSettings.add(
                entryBuilder.startBooleanToggle(
                                Component.literal("Always Trigger Blessing"),
                                TOTVWConfig.get().SERVER_ALWAYS_TRIGGER_BLESSING)
                        .setDefaultValue(false)
                        .setTooltip(text(
                                "When enabled, when all of the\n"
                                +"following conditions are met:\n"
                                + "  > §bShare Benediction§r is enabled,\n"
                                + "  > wolf has more than 1 Benediction stack\n"
                                + "  > owner's health goes below §b" + TOTVWConfig.get().SERVER_BENEDICTION_HEALTH_THRESHOLD + " percent§r,\n"
                                + "wolf will grant §bBlessing of the Verdant Wind§r\n"
                                + "to owner regardless if wolf are\n"
                                + "able to revive them."
                        ))
                        .setSaveConsumer(value -> TOTVWConfig.get().SERVER_ALWAYS_TRIGGER_BLESSING = value)
                        .build()
        );
        benedictionSettings.add(
                entryBuilder.startBooleanToggle(
                                Component.literal("Teleport After Revival"),
                                TOTVWConfig.get().SERVER_TELEPORT_AFTER_SAVE)
                        .setDefaultValue(true)
                        .setTooltip(text(
                                """
                                        When enabled, teleport to the nearest wolf
                                        or owner after revival through Blessing.
                                        
                                        Only works if both are in the same dimension."""
                        ))
                        .setSaveConsumer(value -> TOTVWConfig.get().SERVER_TELEPORT_AFTER_SAVE = value)
                        .build()
        );
        if (TalesOfTheVerdantWind.IN_DEVELOPMENT) {
            benedictionSettings.add(
                    entryBuilder.startIntField(
                                    Component.literal("Max Benediction Stack"),
                                    TOTVWConfig.get().SERVER_MAX_WOLF_BENEDICTION_STACK)
                            .setDefaultValue(3)
                            .setMax(5)
                            .setMin(1)
                            .setSaveConsumer(value -> TOTVWConfig.get().SERVER_MAX_WOLF_BENEDICTION_STACK = value)
                            .build()
            );
        }
        server.addEntry(benedictionSettings.build());

        var enchantmentSkillSettings = entryBuilder.startSubCategory(Component.literal("Enchantment Skills"));
        enchantmentSkillSettings.add(
                entryBuilder.startBooleanToggle(
                                Component.literal("Attachment Cooldowns"),
                                TOTVWConfig.get().SERVER_SKILL_COOLDOWNS)
                        .setDefaultValue(true)
                        .setTooltip(text(
                                "Allow skill and item cooldowns\n"
                                        + "If disabled, active cooldowns will reset."
                        ))
                        .setSaveConsumer(value -> TOTVWConfig.get().SERVER_SKILL_COOLDOWNS = value)
                        .build()
        );
        enchantmentSkillSettings.add(
                entryBuilder.startBooleanToggle(
                                Component.literal("Other Cooldowns"),
                                TOTVWConfig.get().SERVER_OTHER_COOLDOWNS)
                        .setDefaultValue(true)
                        .setTooltip(text(
                                "Allow other time-based cooldowns.\n"
                                        + "If disabled, active cooldowns will reset."
                        ))
                        .setSaveConsumer(value -> TOTVWConfig.get().SERVER_OTHER_COOLDOWNS = value)
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
        debug.addEntry(
                entryBuilder.startBooleanToggle(
                                Component.literal("MIXIN UPDATE LOGS"),
                                TOTVWConfig.get().MIXIN_UPDATE_LOGS)
                        .setDefaultValue(false)
                        .setSaveConsumer(value -> TOTVWConfig.get().MIXIN_UPDATE_LOGS = value)
                        .build()
        );

        var debugBlockUpdates = entryBuilder.startSubCategory(Component.literal("Block Updates"));
        debugBlockUpdates.add(entryBuilder.startBooleanToggle(
                                Component.literal("Lodestone Wind Core"),
                                TOTVWConfig.get().BLOCK_UPDATE_WIND_CORE_LOGS)
                        .setDefaultValue(false)
                        .setSaveConsumer(value -> TOTVWConfig.get().BLOCK_UPDATE_WIND_CORE_LOGS = value)
                        .build()
        );

        return builder.build();
    }
    private static Component text(String... text) {
        String values = Arrays.toString(text).substring(1, Arrays.toString(text).length() - 1);
        return Component.literal(values);
    }
}