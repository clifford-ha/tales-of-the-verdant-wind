package cliffordha.totvw.networking;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.entity.WolfBenedictionHelper;
import cliffordha.totvw.networking.packets.*;
import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.attachments.PlayerPrefs;
import cliffordha.totvw.registry.attachments.ReturnPointData;
import cliffordha.totvw.registry.attachments.Runestone;
import cliffordha.totvw.registry.attachments.TetherData;
import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
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

import static cliffordha.totvw.util.ExtUtil.sendToChat;

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

            if (TOTVW.IN_DEVELOPMENT) {
                sendToChat(player, VWColors.DEFAULT, false, "TOTVW mod version is a development build.");
                sendToChat(player, VWColors.DEFAULT, false, "Bugs and potential crashes are to be expected");
                player.setAttached(PlayerAttachment.IS_DEV_MODE, true);

            }

            if (WolfBenedictionHelper.loadWolfFiles(player, level)) {
                WolfBenedictionHelper.deleteLoadedWolfFiles(player, level);
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(WolfEventsPayload.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            player.level().getServer().execute(() -> {
                if (!(player.level().getEntity(payload.integerFlags().getFirst()) instanceof Wolf wolf)) return;
                if (wolf.getOwner() != player || player.distanceTo(wolf) > 12) return;

                ReturnPointData data = wolf.getAttachedOrElse(WolfAttachment.RETURN_POINT, ReturnPointData.DEFAULT);

                int type = payload.integerFlags().get(1);
                switch (type) {
                    case WolfEventsPayload.DROP_RUNESTONE_FROM_SHEARED -> {
                        Runestone.dropRunestone(wolf, player);
                    }
                    case WolfEventsPayload.CHANGE_BENEDICTION_RETURN_METHOD -> {
                        wolf.setAttached(WolfAttachment.RETURN_POINT, ReturnPointData.changeReturnMethod(data, !data.recordAfter()));
                    }
                    case WolfEventsPayload.REMOVE_RETURN_POINT -> {
                        wolf.setAttached(WolfAttachment.RETURN_POINT, ReturnPointData.removeReturnPoint(data));
                    }
                    default -> {}
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(TetherDataPayload.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            player.level().getServer().execute(() -> {
                if (!(player.level().getEntity(payload.entityId()) instanceof Wolf wolf)) return;
                if (wolf.getOwner() != player || player.distanceTo(wolf) > 8 || !Runestone.hasTether(wolf)) return;

                TetherData data = wolf.getAttachedOrElse(WolfAttachment.TETHER_DATA, TetherData.EMPTY);
                List<String> newBlacklist = new ArrayList<>();
                for (String type : payload.tetherData().blacklistedEntities()) {
                    Identifier id = Identifier.tryParse(type);
                    if (id == null || !BuiltInRegistries.ENTITY_TYPE.containsKey(id)) return;
                    newBlacklist.add(id.toString());
                }
                List<String> tethered = new ArrayList<>(data.tetheredEntities());
                tethered.removeIf(newBlacklist::contains);
                wolf.setAttached(WolfAttachment.TETHER_DATA, new TetherData(
                        List.copyOf(tethered), List.copyOf(newBlacklist),
                        payload.tetherData().autoAttackTethered(),
                        payload.tetherData().tetherChunkTicks()));
            });
        });
    }
}
