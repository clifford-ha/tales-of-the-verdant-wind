package cliffordha.totvw.networking.packets;

import cliffordha.totvw.TOTVW;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.UUID;

public record ProcessAlliancePayload(
        UUID uuid,
        boolean remove
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ProcessAlliancePayload> TYPE =
            new CustomPacketPayload.Type<>(TOTVW.registerID("process_alliance"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ProcessAlliancePayload> STREAM_CODEC =
            StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC, ProcessAlliancePayload::uuid,
                    ByteBufCodecs.BOOL, ProcessAlliancePayload::remove,
                    ProcessAlliancePayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
