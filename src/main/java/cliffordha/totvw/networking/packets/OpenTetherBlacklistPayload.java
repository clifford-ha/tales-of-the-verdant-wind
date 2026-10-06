package cliffordha.totvw.networking.packets;

import cliffordha.totvw.TOTVW;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

// server -> client
public record OpenTetherBlacklistPayload(int entityId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OpenTetherBlacklistPayload> TYPE =
            new CustomPacketPayload.Type<>(TOTVW.registerID("open_tether_blacklist"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenTetherBlacklistPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, OpenTetherBlacklistPayload::entityId,
                    OpenTetherBlacklistPayload::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}