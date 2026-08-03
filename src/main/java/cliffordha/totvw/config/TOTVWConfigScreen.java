package cliffordha.totvw.config;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.TalesOfTheVerdantWind;
import cliffordha.totvw.block.custom.LodestoneWindCoreBlock;
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

        if (TOTVW.IN_DEVELOPMENT) {
            client.addEntry(
                    entryBuilder.startBooleanToggle(
                                    Component.literal("Use New Language Set"),
                                    TOTVWConfig.get().CLIENT_TRANSLATE_LANGUAGE)
                            .setDefaultValue(false)
                            .setTooltip(text(
                                    """
                                            When circumstances are met, certain item
                                            tooltips will show untranslated version of the text"""
                            ))
                            .setSaveConsumer(value -> TOTVWConfig.get().CLIENT_TRANSLATE_LANGUAGE = value)
                            .build()
            );
        }
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
                                        When enabled, if a player has the §cBloodlust Effect§r,
                                        an overlay will be displayed on the whole screen.
                                        The strength of the overlay depends on the player's health.
                                        
                                        §8§oDisabled by default for safety purposes."""
                        ))
                        .setSaveConsumer(value -> TOTVWConfig.get().CLIENT_BLOODLUST_EFFECT_OVERLAY = value)
                        .build()
        );
        client.addEntry(
                entryBuilder.startBooleanToggle(
                                Component.literal("Show Atrocity Counter"),
                                TOTVWConfig.get().CLIENT_SHOW_ATROCITY_COUNTER)
                        .setDefaultValue(false)
                        .setTooltip(text(
                                """
                                        When enabled, if a player hits a wolf or villager,
                                        it will display a counter on the screen."""
                        ))
                        .setSaveConsumer(value -> TOTVWConfig.get().CLIENT_SHOW_ATROCITY_COUNTER = value)
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
                                        effects to trigger. Note that
                                        this will still be limited by
                                        your current game render distance.
                                        
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
                                Component.literal("Tell Owner Who Attacked Wolf"),
                                TOTVWConfig.get().SERVER_TELL_OWNER_WHO_HURT_WOLF)
                        .setDefaultValue(true)
                        .setTooltip(text(
                                """
                                        When enabled, if wolf consume a
                                        §bBenediction Stack§r, tell owner who last
                                        attacked wolf."""
                        ))
                        .setSaveConsumer(value -> TOTVWConfig.get().SERVER_TELL_OWNER_WHO_HURT_WOLF = value)
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
        if (TOTVW.IN_DEVELOPMENT) {
            benedictionSettings.add(
                    entryBuilder.startIntField(
                                    Component.literal("Max Benediction Stack"),
                                    TOTVWConfig.get().SERVER_MAX_WOLF_BENEDICTION_STACK)
                            .setDefaultValue(3)
                            .setMax(10)
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

        // DEBUG
        ConfigCategory debug = builder.getOrCreateCategory(Component.literal("Debug"));

        //
        var logEnchantmentCD = entryBuilder.startSubCategory(Component.literal("Show Enchantment Cooldowns"));
        logEnchantmentCD.add(
                entryBuilder.startBooleanToggle(
                                Component.literal("Wolf CD"),
                                TOTVWConfig.get().LOG_ENCHANTMENT_SHOW_WOLF_CD)
                        .setDefaultValue(false)
                        .setSaveConsumer(value -> TOTVWConfig.get().LOG_ENCHANTMENT_SHOW_WOLF_CD = value)
                        .build()
        );
        logEnchantmentCD.add(
                entryBuilder.startBooleanToggle(
                                Component.literal("Player CD"),
                                TOTVWConfig.get().LOG_ENCHANTMENT_SHOW_PLAYER_CD)
                        .setDefaultValue(false)
                        .setSaveConsumer(value -> TOTVWConfig.get().LOG_ENCHANTMENT_SHOW_PLAYER_CD = value)
                        .build()
        );
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
        debug.addEntry(logEnchantmentCD.build());
        //

        //
        var debugBlockUpdates = entryBuilder.startSubCategory(Component.literal("Block Updates"));
        //

        //
        var debugLodestoneWindCore = entryBuilder.startSubCategory(Component.literal("[Block Updates] Lodestone Wind Core"));
        debugLodestoneWindCore.add(
                entryBuilder.startBooleanToggle(
                        Component.literal(LodestoneWindCoreBlock.LOG_ENERGY_UPDATES),
                                TOTVWConfig.get().LOG_WINDCORE_ENERGY_CHANGES)
                        .setDefaultValue(false)
                        .setTooltip(text(
                                "On/Off updates, Auto-Recharge"
                        ))
                        .setSaveConsumer(value -> TOTVWConfig.get().LOG_WINDCORE_ENERGY_CHANGES = value)
                        .build()
        );
        debugLodestoneWindCore.add(
                entryBuilder.startBooleanToggle(
                                Component.literal(LodestoneWindCoreBlock.LOG_RECORD),
                                TOTVWConfig.get().LOG_WINDCORE_RECORD)
                        .setDefaultValue(false)
                        .setTooltip(text(
                                "Counters, computed values, etc."
                        ))
                        .setSaveConsumer(value -> TOTVWConfig.get().LOG_WINDCORE_RECORD = value)
                        .build()
        );
        debugLodestoneWindCore.add(
                entryBuilder.startBooleanToggle(
                                Component.literal(LodestoneWindCoreBlock.LOG_ENTITY_EVENT),
                                TOTVWConfig.get().LOG_WINDCORE_ENTITY_EVENT)
                        .setDefaultValue(false)
                        .setSaveConsumer(value -> TOTVWConfig.get().LOG_WINDCORE_ENTITY_EVENT = value)
                        .build()
        );
        debugLodestoneWindCore.add(
                entryBuilder.startBooleanToggle(
                                Component.literal(LodestoneWindCoreBlock.LOG_ENTITY_CONVERSION),
                                TOTVWConfig.get().LOG_WINDCORE_ENTITY_CONVERSION)
                        .setDefaultValue(false)
                        .setTooltip(text(
                                """
                                        Wolf and Villager conversion events
                                        (Verdant Status)"""
                        ))
                        .setSaveConsumer(value -> TOTVWConfig.get().LOG_WINDCORE_ENTITY_CONVERSION = value)
                        .build()
        );
        debug.addEntry(debugLodestoneWindCore.build());
        //

        return builder.build();
    }
    private static Component text(String... text) {
        String values = Arrays.toString(text).substring(1, Arrays.toString(text).length() - 1);
        return Component.literal(values);
    }
}