package cliffordha.totvw.networking;

import cliffordha.totvw.client.screen.TetherBlacklistScreen;
import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.networking.packets.ClientPrefsPayload;
import cliffordha.totvw.networking.packets.OpenTetherBlacklistPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientboundPackets {
    public static void registerPackets() {
        ClientPlayNetworking.registerGlobalReceiver(OpenTetherBlacklistPayload.TYPE, (payload, context) ->
                context.client().setScreenAndShow(new TetherBlacklistScreen(payload.entityId())));

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) ->
                sender.sendPacket(new ClientPrefsPayload(
                        VWConfig.get().CLIENT_SHOW_ATROCITY_COUNTER,
                        VWConfig.get().CLIENT_ENABLE_NOTIFIERS,

                        VWConfig.get().SERVER_BENEDICTION_HEALTH_THRESHOLD,
                        VWConfig.get().SERVER_WOLF_SHARES_BENEDICTION_STACK,
                        VWConfig.get().SERVER_ALWAYS_TRIGGER_BLESSING,
                        VWConfig.get().SERVER_TELEPORT_AFTER_SAVE,
                        VWConfig.get().SERVER_WOLF_TP_METHOD,
                        VWConfig.get().SERVER_PLAYER_TP_METHOD,
                        VWConfig.get().SERVER_WOLF_TP_ALL
                ))
        );
    }
}
