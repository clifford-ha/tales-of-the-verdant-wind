package cliffordha.totvw.networking.packets;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.attachments.TetherData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

// client -> server
public record TetherDataPayload(int entityId, TetherData tetherData) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TetherDataPayload> TYPE =
            new CustomPacketPayload.Type<>(TOTVW.registerID("tether_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TetherDataPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, TetherDataPayload::entityId,
                    ByteBufCodecs.fromCodec(TetherData.CODEC), TetherDataPayload::tetherData,
                    TetherDataPayload::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}