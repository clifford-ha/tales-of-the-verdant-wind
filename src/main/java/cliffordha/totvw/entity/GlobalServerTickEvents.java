package cliffordha.totvw.entity;

import cliffordha.totvw.networking.VWNetworking;

import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;

public class GlobalServerTickEvents {
    public static void register() {
        onServerTick();
    }
    private static void onServerTick() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (var serverLevel : server.getAllLevels()) {
                for (Entity mob : serverLevel.getAllEntities()) {
                    if (!(mob instanceof LivingEntity entity)) continue;
                    instantTick(serverLevel, entity);
                    if (serverLevel.getGameTime() % 20 == 0) {
                        mainTickThread(serverLevel, entity);
                    }
                }
            }
        });
    }
    private static void instantTick(ServerLevel level, LivingEntity entity) {

    }
    private static void mainTickThread(ServerLevel level, LivingEntity entity) {
        if (!level.getServer().isReady()) return;

        if (entity instanceof Player player) {
            VWNetworking.syncPlayerArmor(player);
        }
        if (entity instanceof Wolf wolf && wolf.isTame()) {
            if (WolfAttachment.tetherChunkTicks(wolf)) {
                level.getChunkSource().addTicketAndLoadWithRadius(
                        TicketType.FORCED,
                        wolf.chunkPosition(),
                        1
                );
            }
            VWNetworking.syncWolfEffects(wolf);
            VWNetworking.syncWolfArmor(wolf);
        }
    }
}
