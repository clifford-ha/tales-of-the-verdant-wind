package cliffordha.totvw.config;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.block.custom.LodestoneWindCoreBlock;
import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.client.ClientPrefsPayload;
import me.shedaniel.clothconfig2.api.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Arrays;

public class VWConfigScreen {
    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal(TOTVW.MOD_NAME).withColor(VWColors.VERDANT_WIND))
                .transparentBackground()
                .setSavingRunnable(() -> {
                    VWConfig.save();
                    if (ClientPlayNetworking.canSend(ClientPrefsPayload.TYPE)) {
                        ClientPlayNetworking.send(new ClientPrefsPayload(
                                VWConfig.get().CLIENT_SHOW_ATROCITY_COUNTER,
                                VWConfig.get().CLIENT_ENABLE_NOTIFIERS,

                                VWConfig.get().SERVER_BENEDICTION_HEALTH_THRESHOLD,
                                VWConfig.get().SERVER_WOLF_SHARES_BENEDICTION_STACK,
                                VWConfig.get().SERVER_ALWAYS_TRIGGER_BLESSING,
                                VWConfig.get().SERVER_TELEPORT_AFTER_SAVE,
                                VWConfig.get().SERVER_WOLF_TP_METHOD,
                                VWConfig.get().SERVER_PLAYER_TP_METHOD,
                                VWConfig.get().SERVER_WOLF_TP_ALL
                        ));
                    }
                });

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory client = builder.getOrCreateCategory(Component.literal("Client"));

        if (TOTVW.IN_DEVELOPMENT) {
            client.addEntry(
                    entryBuilder.startBooleanToggle(
                                    Component.literal("Use New Language Set"),
                                    VWConfig.get().CLIENT_TRANSLATE_LANGUAGE)
                            .setDefaultValue(false)
                            .setTooltip(text(
                                    """
                                            When circumstances are met, certain item
                                            tooltips will show untranslated version of the text"""
                            ))
                            .setSaveConsumer(value -> VWConfig.get().CLIENT_TRANSLATE_LANGUAGE = value)
                            .build()
            );
        }
        client.addEntry(
                entryBuilder.startBooleanToggle(
                                Component.literal("Use Notifiers"),
                                VWConfig.get().CLIENT_ENABLE_NOTIFIERS)
                        .setDefaultValue(true)
                        .setTooltip(text(
                                "When enabled, notifications will\n"
                                        + "be sent to chat or overlay"
                        ))
                        .setSaveConsumer(value -> VWConfig.get().CLIENT_ENABLE_NOTIFIERS = value)
                        .build()
        );
        client.addEntry(
                entryBuilder.startBooleanToggle(
                                Component.literal("Use New Sounds"),
                                VWConfig.get().CLIENT_MOD_SOUNDS)
                        .setDefaultValue(true)
                        .setTooltip(text(
                                """
                                        When enabled, custom sounds will be used
                                        for various events. Option still in
                                        development and may be removed in the future."""
                        ))
                        .setSaveConsumer(value -> VWConfig.get().CLIENT_MOD_SOUNDS = value)
                        .build()
        );

        var effectOverlaySettings = entryBuilder.startSubCategory(Component.literal("Effect Screen Overlays"));
        effectOverlaySettings.add(
                entryBuilder.startBooleanToggle(
                                Component.literal("Allow Effect Overlays"),
                                VWConfig.get().CLIENT_ALLOW_EFFECT_OVERLAYS)
                        .setDefaultValue(true)
                        .setTooltip(text("When enabled, certain mob effects will affect the screen."))
                        .setSaveConsumer(value -> VWConfig.get().CLIENT_ALLOW_EFFECT_OVERLAYS = value)
                        .build()
        );
        effectOverlaySettings.add(
                entryBuilder.startBooleanToggle(
                        Component.literal("Bloodlust Effect Screen Overlay"),
                                VWConfig.get().CLIENT_BLOODLUST_EFFECT_OVERLAY)
                        .setDefaultValue(false)
                        .setTooltip(text("When enabled, if a player has the §cBloodlust Effect§r,\n" +
                                "an overlay will be displayed on the whole screen.\n" +
                                "The strength of the overlay depends on the player's health.\n\n" +
                                "§8§oDisabled by default for safety purposes.\n" +
                                "Will also be disabled when Allow Effect Overlays is disabled."
                        ))
                        .setSaveConsumer(value -> VWConfig.get().CLIENT_BLOODLUST_EFFECT_OVERLAY = value)
                        .build()
        );
        client.addEntry(effectOverlaySettings.build());

        client.addEntry(
                entryBuilder.startBooleanToggle(
                                Component.literal("Show Atrocity Counter"),
                                VWConfig.get().CLIENT_SHOW_ATROCITY_COUNTER)
                        .setDefaultValue(false)
                        .setTooltip(text(
                                """
                                        When enabled, if a player hits a wolf or villager,
                                        it will display a counter on the screen.
                                        
                                        You can also know this by using the command:
                                        /totvw get_atrocity_count"""
                        ))
                        .setSaveConsumer(value -> VWConfig.get().CLIENT_SHOW_ATROCITY_COUNTER = value)
                        .build()
        );
        client.addEntry(
                entryBuilder.startBooleanToggle(
                                Component.literal("Allow Lore Spoilers"),
                                VWConfig.get().CLIENT_ALLOW_LORE_SPOILERS)
                        .setDefaultValue(false)
                        .setTooltip(text(
                                """
                                        When enabled, show texts on pages that
                                        contain lore regardless if the player
                                        is not in survival mode."""
                        ))
                        .setSaveConsumer(value -> VWConfig.get().CLIENT_ALLOW_LORE_SPOILERS = value)
                        .build()
        );



        ConfigCategory server = builder.getOrCreateCategory(Component.literal("Server"));
        server.addEntry(
                entryBuilder.startBooleanToggle(
                                Component.literal("Wolf Armor Damage Distribution"),
                                VWConfig.get().SERVER_WOLF_DMG_DISTRIBUTION)
                        .setDefaultValue(true)
                        .setTooltip(text(
                                """
                                        When enabled, allow redistributing damage
                                        that weren't absorbed by the armor
                                        to both the armor and wolf."""
                        ))
                        .setSaveConsumer(value -> VWConfig.get().SERVER_WOLF_DMG_DISTRIBUTION = value)
                        .build()
        );

        var benedictionSettings = entryBuilder.startSubCategory(Component.literal("Benediction of the Verdant Mountains"));
        benedictionSettings.add(
                entryBuilder.startIntField(
                                Component.literal("Max Chunk Scan"),
                                VWConfig.get().SERVER_WOLF_PLAYER_SCAN_DISTANCE)
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
                        .setSaveConsumer(value -> VWConfig.get().SERVER_WOLF_PLAYER_SCAN_DISTANCE = value)
                        .build()
        );
        benedictionSettings.add(
                entryBuilder.startIntField(
                                Component.literal("Low Health Threshold"),
                                VWConfig.get().SERVER_BENEDICTION_HEALTH_THRESHOLD)
                        .setDefaultValue(30)
                        .setMax(50)
                        .setMin(10)
                        .setTooltip(text(
                                "Grant §bBlessing of the Verdant Wind§r when\n"
                                + "wolf/owner health threshold (in %) is met"
                        ))
                        .setSaveConsumer(value -> VWConfig.get().SERVER_BENEDICTION_HEALTH_THRESHOLD = value)
                        .build()
        );
        benedictionSettings.add(
                entryBuilder.startBooleanToggle(
                                Component.literal("Share Benediction Stack"),
                                VWConfig.get().SERVER_WOLF_SHARES_BENEDICTION_STACK)
                        .setDefaultValue(true)
                        .setTooltip(text(
                                "When enabled, if wolf has more than\n"
                                + "1 Benediction Stack and their owner\n"
                                + "enters dying state within " + VWConfig.get().SERVER_WOLF_PLAYER_SCAN_DISTANCE + " chunks,\n"
                                + "wolf will consume §b1§r stack to revive owner."
                        ))
                        .setSaveConsumer(value -> VWConfig.get().SERVER_WOLF_SHARES_BENEDICTION_STACK = value)
                        .build()
        );
        benedictionSettings.add(
                entryBuilder.startBooleanToggle(
                                Component.literal("Always Trigger Blessing"),
                                VWConfig.get().SERVER_ALWAYS_TRIGGER_BLESSING)
                        .setDefaultValue(false)
                        .setTooltip(text(
                                "When enabled, when all of the\n"
                                +"following conditions are met:\n"
                                + "  > §bShare Benediction§r is enabled,\n"
                                + "  > wolf has more than 1 Benediction stack\n"
                                + "  > owner's health goes below §b" + VWConfig.get().SERVER_BENEDICTION_HEALTH_THRESHOLD + " percent§r,\n"
                                + "wolf will grant §bBlessing of the Verdant Wind§r\n"
                                + "to owner regardless if wolf are\n"
                                + "able to revive them."
                        ))
                        .setSaveConsumer(value -> VWConfig.get().SERVER_ALWAYS_TRIGGER_BLESSING = value)
                        .build()
        );
        var benedictionTPSettings = entryBuilder.startSubCategory(Component.literal("Benediction TP Settings"));
        benedictionTPSettings.add(entryBuilder.startBooleanToggle(
                Component.literal("Teleport After Revival"), VWConfig.get().SERVER_TELEPORT_AFTER_SAVE)
                .setDefaultValue(true)
                .setTooltip(text("""
                        When enabled, teleport to the nearest wolf
                        or owner after revival through Blessing.
                        For wolves: if player is inaccessible,
                        teleport to the saved spawn if valid.
                        
                        Only works if both are in the same dimension."""
                ))
                .setSaveConsumer(value -> VWConfig.get().SERVER_TELEPORT_AFTER_SAVE = value)
                .build()
        );
        benedictionTPSettings.add(entryBuilder.startBooleanToggle(
                        Component.literal("All Wolf Gets TP"), VWConfig.get().SERVER_WOLF_TP_ALL)
                .setDefaultValue(false)
                .setTooltip(text("""
                        When enabled, teleport ALL tamed
                        wolves to player's location if one
                        of the wolves is able to revive them.
                        This option only affects the Player
                        TP Method. Have fun with this :3
                        
                        Enable Teleport After Revival
                        to work."""
                ))
                .setSaveConsumer(value -> VWConfig.get().SERVER_WOLF_TP_ALL = value)
                .build()
        );
        benedictionSettings.add(entryBuilder.startIntField(
                        Component.literal("Wolf TP Method"), VWConfig.get().SERVER_WOLF_TP_METHOD)
                .setDefaultValue(0)
                .setMin(0)
                .setMax(1)
                .setTooltip(text("""
                        If 0, wolf will be teleport to player.
                        Otherwise, do reverse.
                        
                        Enable Teleport After Revival
                        to work."""
                ))
                .setSaveConsumer(value -> VWConfig.get().SERVER_WOLF_TP_METHOD = value)
                .build()
        );
        benedictionSettings.add(entryBuilder.startIntField(
                        Component.literal("Player TP Method"), VWConfig.get().SERVER_PLAYER_TP_METHOD)
                .setDefaultValue(0)
                .setMin(0)
                .setMax(1)
                .setTooltip(text("""
                        If 0, player will will be teleported
                        to wolf. Otherwise, do reverse.
                        
                        Enable Teleport After Revival
                        to work."""
                ))
                .setSaveConsumer(value -> VWConfig.get().SERVER_PLAYER_TP_METHOD = value)
                .build()
        );
        benedictionSettings.add(benedictionTPSettings.build());
        server.addEntry(benedictionSettings.build());

        var enchantmentSkillSettings = entryBuilder.startSubCategory(Component.literal("Enchantment Skills"));
        enchantmentSkillSettings.add(
                entryBuilder.startBooleanToggle(
                                Component.literal("Attachment Cooldowns"),
                                VWConfig.get().SERVER_SKILL_COOLDOWNS)
                        .setDefaultValue(true)
                        .setTooltip(text(
                                "Allow skill cooldowns\n"
                                        + "If disabled, active cooldowns will reset."
                        ))
                        .setSaveConsumer(value -> VWConfig.get().SERVER_SKILL_COOLDOWNS = value)
                        .build()
        );
        enchantmentSkillSettings.add(
                entryBuilder.startBooleanToggle(
                                Component.literal("Item Cooldowns"),
                                VWConfig.get().SERVER_ITEM_COOLDOWNS)
                        .setDefaultValue(true)
                        .setTooltip(text(
                                "Allow item-specific cooldowns."
                        ))
                        .setSaveConsumer(value -> VWConfig.get().SERVER_ITEM_COOLDOWNS = value)
                        .build()
        );
        enchantmentSkillSettings.add(
                entryBuilder.startBooleanToggle(
                                Component.literal("Other Cooldowns"),
                                VWConfig.get().SERVER_OTHER_COOLDOWNS)
                        .setDefaultValue(true)
                        .setTooltip(text(
                                "Allow other time-based cooldowns.\n"
                                        + "If disabled, active cooldowns will reset."
                        ))
                        .setSaveConsumer(value -> VWConfig.get().SERVER_OTHER_COOLDOWNS = value)
                        .build()
        );
        server.addEntry(enchantmentSkillSettings.build());

        // DEBUG
        ConfigCategory debug = builder.getOrCreateCategory(Component.literal("Debug"));

        debug.addEntry(
                entryBuilder.startBooleanToggle(
                        Component.literal("HOVER HERE"),
                        VWConfig.get().DUMMY)
                        .setDefaultValue(false)
                        .setTooltip(text("Certain settings inside the DEBUG is\n"
                                + "only visible in server log terminal."))
                        .build()
        );

        //
        var logEnchantmentCD = entryBuilder.startSubCategory(Component.literal("Show Enchantment Cooldowns"));
        logEnchantmentCD.add(
                entryBuilder.startBooleanToggle(
                                Component.literal("Wolf CD"),
                                VWConfig.get().LOG_ENCHANTMENT_SHOW_WOLF_CD)
                        .setDefaultValue(false)
                        .setSaveConsumer(value -> VWConfig.get().LOG_ENCHANTMENT_SHOW_WOLF_CD = value)
                        .build()
        );
        logEnchantmentCD.add(
                entryBuilder.startBooleanToggle(
                                Component.literal("Player CD"),
                                VWConfig.get().LOG_ENCHANTMENT_SHOW_PLAYER_CD)
                        .setDefaultValue(false)
                        .setSaveConsumer(value -> VWConfig.get().LOG_ENCHANTMENT_SHOW_PLAYER_CD = value)
                        .build()
        );
        debug.addEntry(
                entryBuilder.startBooleanToggle(
                                Component.literal("SEND LOGS"),
                                VWConfig.get().DEBUG_PRINT_LOGS)
                        .setDefaultValue(false)
                        .setSaveConsumer(value -> VWConfig.get().DEBUG_PRINT_LOGS = value)
                        .build()
        );
        debug.addEntry(
                entryBuilder.startBooleanToggle(
                                Component.literal("MIXIN UPDATE LOGS"),
                                VWConfig.get().MIXIN_UPDATE_LOGS)
                        .setDefaultValue(false)
                        .setSaveConsumer(value -> VWConfig.get().MIXIN_UPDATE_LOGS = value)
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
                                VWConfig.get().LOG_WINDCORE_ENERGY_CHANGES)
                        .setDefaultValue(false)
                        .setTooltip(text(
                                "On/Off updates, Auto-Recharge"
                        ))
                        .setSaveConsumer(value -> VWConfig.get().LOG_WINDCORE_ENERGY_CHANGES = value)
                        .build()
        );
        debugLodestoneWindCore.add(
                entryBuilder.startBooleanToggle(
                                Component.literal(LodestoneWindCoreBlock.LOG_RECORD),
                                VWConfig.get().LOG_WINDCORE_RECORD)
                        .setDefaultValue(false)
                        .setTooltip(text(
                                "Counters, computed values, etc."
                        ))
                        .setSaveConsumer(value -> VWConfig.get().LOG_WINDCORE_RECORD = value)
                        .build()
        );
        debugLodestoneWindCore.add(
                entryBuilder.startBooleanToggle(
                                Component.literal(LodestoneWindCoreBlock.LOG_ENTITY_EVENT),
                                VWConfig.get().LOG_WINDCORE_ENTITY_EVENT)
                        .setDefaultValue(false)
                        .setSaveConsumer(value -> VWConfig.get().LOG_WINDCORE_ENTITY_EVENT = value)
                        .build()
        );
        debugLodestoneWindCore.add(
                entryBuilder.startBooleanToggle(
                                Component.literal(LodestoneWindCoreBlock.LOG_ENTITY_CONVERSION),
                                VWConfig.get().LOG_WINDCORE_ENTITY_CONVERSION)
                        .setDefaultValue(false)
                        .setTooltip(text(
                                """
                                        Wolf and Villager conversion events
                                        (Verdant Status)"""
                        ))
                        .setSaveConsumer(value -> VWConfig.get().LOG_WINDCORE_ENTITY_CONVERSION = value)
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