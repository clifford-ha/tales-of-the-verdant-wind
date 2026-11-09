package cliffordha.totvw.networking.packets;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.attachments.ArmorData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;

public record PlayerDataPayload(
        ArmorData armorData,
        List<ArmorData> equipments

) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PlayerDataPayload> TYPE =
            new CustomPacketPayload.Type<>(TOTVW.registerID("player_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerDataPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.fromCodec(ArmorData.CODEC), PlayerDataPayload::armorData,
                    ByteBufCodecs.fromCodec(ArmorData.CODEC.listOf()), PlayerDataPayload::equipments,
                    PlayerDataPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
