package cliffordha.totvw.networking;

import cliffordha.totvw.entity.WolfBenedictionHelper;
import cliffordha.totvw.networking.packets.*;
import cliffordha.totvw.registry.attachments.PlayerPrefs;
import cliffordha.totvw.registry.attachments.Runestone;
import cliffordha.totvw.registry.attachments.TetherData;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.wolf.Wolf;

import java.util.ArrayList;
import java.util.List;

public class ServerboundPackets {
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(ClientPrefsPayload.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            player.level().getServer().execute(() -> {
                player.setAttached(PlayerPrefs.ENABLE_NOTIFIERS, payload.enableNotifiers());
                player.setAttached(PlayerPrefs.SHOW_ATROCITY_COUNTER, payload.showAtrocityCounter());

                player.setAttached(PlayerPrefs.BENEDICTION_HEALTH_THRESHOLD, payload.benedictionLowHealthThreshold());
                player.setAttached(PlayerPrefs.BENEDICTION_SHARE_STACK, payload.benedictionShareStack());
                player.setAttached(PlayerPrefs.BENEDICTION_ALWAYS_TRIGGER_BLESSING, payload.benedictionAlwaysTriggerBlessing());
                player.setAttached(PlayerPrefs.BENEDICTION_TELEPORT_AFTER_SAVE, payload.benedictionTeleportAfterSave());
                player.setAttached(PlayerPrefs.BENEDICTION_WOLF_TP_METHOD, payload.benedictionWolfTPMethod());
                player.setAttached(PlayerPrefs.BENEDICTION_PLAYER_TP_METHOD, payload.benedictionPlayerTPMethod());
                player.setAttached(PlayerPrefs.BENEDICTION_WOLF_TP_ALL, payload.benedictionWolfTPAll());
            });
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            ServerLevel level = player.level();

            if (WolfBenedictionHelper.loadWolfFiles(player, level)) {
                WolfBenedictionHelper.deleteLoadedWolfFiles(player, level);
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(TetherDataPayload.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            player.level().getServer().execute(() -> {
                if (!(player.level().getEntity(payload.entityId()) instanceof Wolf wolf)) return;
                if (wolf.getOwner() != player || player.distanceTo(wolf) > 8 || !Runestone.hasTether(wolf)) return;

                TetherData data = wolf.getAttachedOrElse(WolfAttachment.TETHER_DATA, TetherData.EMPTY);
                List<String> tethered = new ArrayList<>(data.tetheredEntities());
                List<String> blacklisted = payload.tetherData().blacklistedEntities();
                List<String> newBlacklist = new ArrayList<>();
                if (payload.tetherData().blacklistedEntities().isEmpty()) return;

                for (String type : blacklisted) {
                    Identifier id = Identifier.tryParse(type);
                    if (id == null || !BuiltInRegistries.ENTITY_TYPE.containsKey(id)) return;

                    newBlacklist.add(id.toString());
                }
                tethered.removeIf(blacklisted::contains);
                wolf.setAttached(WolfAttachment.TETHER_DATA, TetherData.updateEntities(data, List.copyOf(tethered), List.copyOf(newBlacklist)));
            });
        });
    }
}
