package cliffordha.totvw.entity;

import cliffordha.totvw.networking.VWNetworking;
import cliffordha.totvw.networking.packets.MobEffectInstancePayload;

import cliffordha.totvw.networking.packets.PlayerArmorDataPayload;
import cliffordha.totvw.networking.packets.WolfDataPayload;
import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
import cliffordha.totvw.util.BasicArmorData;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class VWGlobalTickEvents {
    public static void register() {
        onServerTick();
    }

    private static void onServerTick() {
        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            for (var serverLevel : server.getAllLevels()) {
                for (Entity mob : serverLevel.getAllEntities()) {
                    if (!(mob instanceof LivingEntity entity)) return;
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

        if (entity instanceof Player player && player.getArmorValue() > 0) {
            VWNetworking.sendPacket2C(new PlayerArmorDataPayload(PlayerAttachment.getArmorList(player)));
        }

        if (entity instanceof Wolf wolf && wolf.isTame()) {
            if (!wolf.getActiveEffects().isEmpty()) {
                VWNetworking.sendPacket2C(new MobEffectInstancePayload(wolf.getActiveEffects().stream().toList()));
            }
            ItemStack armor = wolf.getBodyArmorItem();
            if (!armor.isEmpty()) {
                ClientPlayNetworking.send(new WolfDataPayload(new BasicArmorData(
                        armor.getItemName(),
                        BasicArmorData.getID(armor),
                        armor.getMaxDamage(),
                        armor.getDamageValue(),
                        EquipmentSlot.BODY
                )));
            }
        }
    }
}
