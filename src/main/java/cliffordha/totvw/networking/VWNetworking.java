package cliffordha.totvw.networking;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.networking.packets.ClientPrefsPayload;
import cliffordha.totvw.networking.packets.OpenTetherBlacklistPayload;
import cliffordha.totvw.networking.packets.TetherBlacklistPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class VWNetworking {
    public static void registerClientboundPackets(PayloadTypeRegistry<RegistryFriendlyByteBuf> registry) {
        registry.register(OpenTetherBlacklistPayload.TYPE, OpenTetherBlacklistPayload.STREAM_CODEC);

    }
    public static void registerServerboundPackets(PayloadTypeRegistry<RegistryFriendlyByteBuf> registry) {
        registry.register(ClientPrefsPayload.TYPE, ClientPrefsPayload.STREAM_CODEC);
        registry.register(TetherBlacklistPayload.TYPE, TetherBlacklistPayload.STREAM_CODEC);

    }

    public static void register() {
        registerClientboundPackets(PayloadTypeRegistry.clientboundPlay());
        registerServerboundPackets(PayloadTypeRegistry.serverboundPlay());

        TOTVW.sendClassRegisterLog("Networking");
    }
}
