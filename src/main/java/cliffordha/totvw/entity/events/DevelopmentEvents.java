package cliffordha.totvw.entity.events;

import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityTypes;

import static cliffordha.totvw.util.ExtUtil.sendToChat;

public class DevelopmentEvents {
    public static void developmentTick() {
        ServerTickEvents.END_SERVER_TICK.register((MinecraftServer server) -> {
            for (var serverLevel : server.getAllLevels()) {
                serverLevel.getEntities(EntityTypes.PLAYER, _ -> true).forEach(player -> {
                    if (!player.entityTags().contains(player.getStringUUID() + "-reminderStamp")) {
                        sendToChat(player, VWColors.VERDANT_WIND, false, "TOTVW mod version is a development build.");
                        player.entityTags().add(player.getStringUUID() + "-reminderStamp");
                    }
                    if (!player.getAttachedOrElse(PlayerAttachment.IS_DEV_MODE, false)) {
                        player.setAttached(PlayerAttachment.IS_DEV_MODE, true);
                    }
                });
            }
        });
    }

    public static void register() {
        developmentTick();
    }
}
