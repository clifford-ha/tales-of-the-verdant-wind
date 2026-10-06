package cliffordha.totvw.networking.packets;

import cliffordha.totvw.TOTVW;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

// client -> server
public record TetherBlacklistPayload(int entityId, String entityTypeId, boolean blacklisted) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TetherBlacklistPayload> TYPE =
            new CustomPacketPayload.Type<>(TOTVW.registerID("tether_blacklist"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TetherBlacklistPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, TetherBlacklistPayload::entityId,
                    ByteBufCodecs.STRING_UTF8, TetherBlacklistPayload::entityTypeId,
                    ByteBufCodecs.BOOL, TetherBlacklistPayload::blacklisted,
                    TetherBlacklistPayload::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}