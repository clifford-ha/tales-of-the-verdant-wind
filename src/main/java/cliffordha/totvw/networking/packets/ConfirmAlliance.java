package cliffordha.totvw.networking.packets;

import cliffordha.totvw.TOTVW;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.UUID;

public record ConfirmAlliance(
        UUID uuid
) implements CustomPacketPayload {
    public static final Type<ConfirmAlliance> TYPE =
            new Type<>(TOTVW.registerID("confirm_alliance"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ConfirmAlliance> STREAM_CODEC =
            StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC, ConfirmAlliance::uuid,
                    ConfirmAlliance::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
