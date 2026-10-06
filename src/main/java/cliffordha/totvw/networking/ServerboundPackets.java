package cliffordha.totvw.networking;

import cliffordha.totvw.networking.packets.ClientPrefsPayload;
import cliffordha.totvw.networking.packets.TetherBlacklistPayload;
import cliffordha.totvw.registry.attachments.PlayerPrefs;
import cliffordha.totvw.registry.attachments.Runestone;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
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

        ServerPlayNetworking.registerGlobalReceiver(TetherBlacklistPayload.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            player.level().getServer().execute(() -> {
                if (!(player.level().getEntity(payload.entityId()) instanceof Wolf wolf)) return;
                if (wolf.getOwner() != player || player.distanceTo(wolf) > 8 || !Runestone.hasTether(wolf)) return;

                Identifier id = Identifier.tryParse(payload.entityTypeId());
                if (id == null || !BuiltInRegistries.ENTITY_TYPE.containsKey(id)) return;

                List<String> list = new ArrayList<>(WolfAttachment.getTetherBlacklist(wolf));
                String key = id.toString();
                if (payload.blacklisted()) {
                    if (!list.contains(key) && list.size() < 64) list.add(key);
                } else {
                    list.remove(key);
                }
                wolf.setAttached(WolfAttachment.TETHER_ENTITY_BLACKLIST, list);
            });
        });
    }
}
