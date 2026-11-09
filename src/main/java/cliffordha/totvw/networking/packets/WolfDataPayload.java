package cliffordha.totvw.networking.packets;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.attachments.ArmorData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record WolfDataPayload(
        int wolfId,
        ArmorData armorData
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<WolfDataPayload> TYPE =
            new CustomPacketPayload.Type<>(TOTVW.registerID("wolf_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, WolfDataPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, WolfDataPayload::wolfId,
                    ByteBufCodecs.fromCodec(ArmorData.CODEC), WolfDataPayload::armorData,
                    WolfDataPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
