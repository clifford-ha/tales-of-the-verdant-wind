package cliffordha.totvw.networking.packets;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.attachments.TetherData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record OpenTetherManagerPayload(
        int entityId,
        TetherData tetherData

) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OpenTetherManagerPayload> TYPE =
            new CustomPacketPayload.Type<>(TOTVW.registerID("tether_manager"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenTetherManagerPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, OpenTetherManagerPayload::entityId,
                    ByteBufCodecs.fromCodec(TetherData.CODEC), OpenTetherManagerPayload::tetherData,
                    OpenTetherManagerPayload::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}