package cliffordha.totvw.networking;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.networking.packets.*;
import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
import cliffordha.totvw.registry.attachments.ArmorData;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class VWNetworking {
    public static void registerClientboundPackets(PayloadTypeRegistry<RegistryFriendlyByteBuf> registry) {
        registry.register(OpenTetherManagerPayload.TYPE, OpenTetherManagerPayload.STREAM_CODEC);
        registry.register(MobEffectInstancePayload.TYPE, MobEffectInstancePayload.STREAM_CODEC);
        registry.register(WolfDataPayload.TYPE, WolfDataPayload.STREAM_CODEC);
        registry.register(PlayerDataPayload.TYPE, PlayerDataPayload.STREAM_CODEC);

    }
    public static void registerServerboundPackets(PayloadTypeRegistry<RegistryFriendlyByteBuf> registry) {
        registry.register(ClientPrefsPayload.TYPE, ClientPrefsPayload.STREAM_CODEC);
        registry.register(TetherDataPayload.TYPE, TetherDataPayload.STREAM_CODEC);
        registry.register(WolfEventsPayload.TYPE, WolfEventsPayload.STREAM_CODEC);

    }

    public static void sendPacket2S(CustomPacketPayload payload) {
        if (ClientPlayNetworking.canSend(payload.type())) {
            ClientPlayNetworking.send(payload);
        }
    }
    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        if (ServerPlayNetworking.canSend(player, payload.type())) {
            ServerPlayNetworking.send(player, payload);
        }
    }
    public static void syncPlayerArmor(Player player) {
        if (player instanceof ServerPlayer sp) {
            sp.setAttached(PlayerAttachment.ARMOR_DATA, ArmorData.of(sp));
            sendToPlayer(sp, new PlayerDataPayload(ArmorData.of(sp), PlayerAttachment.getArmorList(player)));
        }
    }

    public static void syncWolfArmor(Wolf wolf) {
        if (!(wolf.getOwner() instanceof ServerPlayer owner)) return;
        ItemStack armor = wolf.getBodyArmorItem();
        AttributeMap map = wolf.getAttributes();
        ArmorData data = armor.isEmpty() ? ArmorData.NONE : new ArmorData(
                armor.getItemName(), ArmorData.getID(armor),
                armor.getMaxDamage(), armor.getDamageValue(),
                EquipmentSlot.BODY,
                map.getBaseValue(Attributes.ATTACK_DAMAGE),
                map.getValue(Attributes.ATTACK_DAMAGE));

        sendToPlayer(owner, new WolfDataPayload(wolf.getId(), data));
    }
    public static void syncWolfEffects(Wolf wolf) {
        if (!(wolf.getOwner() instanceof ServerPlayer owner)) return;
        sendToPlayer(owner, new MobEffectInstancePayload(wolf.getId(), List.copyOf(wolf.getActiveEffects())));
    }
    public static void syncPlayerEffects(Player player) {
        if (!(player instanceof ServerPlayer sp)) return;
        sendToPlayer(sp, new MobEffectInstancePayload(sp.getId(), List.copyOf(sp.getActiveEffects())));
    }

    public static void register() {
        registerClientboundPackets(PayloadTypeRegistry.clientboundPlay());
        registerServerboundPackets(PayloadTypeRegistry.serverboundPlay());

        TOTVW.sendClassRegisterLog("Networking");
    }
}
