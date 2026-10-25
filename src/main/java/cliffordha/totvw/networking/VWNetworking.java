package cliffordha.totvw.networking;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.networking.packets.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public class VWNetworking {
    public static void registerClientboundPackets(PayloadTypeRegistry<RegistryFriendlyByteBuf> registry) {
        registry.register(OpenTetherBlacklistPayload.TYPE, OpenTetherBlacklistPayload.STREAM_CODEC);

    }
    public static void registerServerboundPackets(PayloadTypeRegistry<RegistryFriendlyByteBuf> registry) {
        registry.register(ClientPrefsPayload.TYPE, ClientPrefsPayload.STREAM_CODEC);
        registry.register(TetherBlacklistPayload.TYPE, TetherBlacklistPayload.STREAM_CODEC);
        registry.register(MobEffectInstancePayload.TYPE, MobEffectInstancePayload.STREAM_CODEC);
        registry.register(WolfDataPayload.TYPE, WolfDataPayload.STREAM_CODEC);
        registry.register(PlayerArmorDataPayload.TYPE, PlayerArmorDataPayload.STREAM_CODEC);

    }

    public static void sendPacket2C(CustomPacketPayload payload) {
        if (ClientPlayNetworking.canSend(payload.type())) {
            ClientPlayNetworking.send(payload);
        }
    }
    public static void sendPacket2S(LivingEntity entity, CustomPacketPayload payload) {
        if (!(entity instanceof ServerPlayer player)) return;
        if (ServerPlayNetworking.canSend(player, payload.type())) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    public static void register() {
        registerClientboundPackets(PayloadTypeRegistry.clientboundPlay());
        registerServerboundPackets(PayloadTypeRegistry.serverboundPlay());

        TOTVW.sendClassRegisterLog("Networking");
    }
}
