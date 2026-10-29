package cliffordha.totvw.networking;

import cliffordha.totvw.client.screen.TetherScreen;
import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.networking.packets.*;
import cliffordha.totvw.registry.attachments.VWAttachments;
import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.world.entity.animal.wolf.Wolf;

import java.util.ArrayList;

public class ClientboundPackets {
    public static void registerPackets() {
        ClientPlayNetworking.registerGlobalReceiver(OpenTetherBlacklistPayload.TYPE, (payload, context) ->
                context.client().setScreenAndShow(new TetherScreen(payload.entityId(), payload.tetherData())));

        ClientPlayNetworking.registerGlobalReceiver(MobEffectInstancePayload.TYPE, (payload, context) -> {
            if (context.client().level != null && context.client().level.getEntity(payload.entityId()) instanceof Wolf wolf) {
                wolf.setAttached(VWAttachments.MOB_EFFECTS, new ArrayList<>(payload.instance()));
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(WolfDataPayload.TYPE, (payload, context) -> {
            if (context.client().level != null && context.client().level.getEntity(payload.wolfId()) instanceof Wolf wolf) {
                if (payload.armorData().isEmpty()) wolf.removeAttached(WolfAttachment.ARMOR_DATA);
                else wolf.setAttached(WolfAttachment.ARMOR_DATA, payload.armorData());
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(PlayerArmorDataPayload.TYPE, (payload, context) -> {
            context.player().setAttached(PlayerAttachment.EQUIPMENTS, new ArrayList<>(payload.armorData()));
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) ->
                sender.sendPacket(new ClientPrefsPayload(
                        VWConfig.get().SYNCED_SHOW_ATROCITY_COUNTER,
                        VWConfig.get().SYNCED_ENABLE_NOTIFIERS,

                        VWConfig.get().SYNCED_BENEDICTION_HEALTH_THRESHOLD,
                        VWConfig.get().SYNCED_WOLF_SHARES_BENEDICTION_STACK,
                        VWConfig.get().SYNCED_ALWAYS_TRIGGER_BLESSING,
                        VWConfig.get().SYNCED_TELEPORT_AFTER_SAVE,
                        VWConfig.get().SYNCED_WOLF_TP_METHOD,
                        VWConfig.get().SYNCED_PLAYER_TP_METHOD,
                        VWConfig.get().SYNCED_WOLF_TP_ALL
                ))
        );
    }
}
