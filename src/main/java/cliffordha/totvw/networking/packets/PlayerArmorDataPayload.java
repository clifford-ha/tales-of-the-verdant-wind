package cliffordha.totvw.networking.packets;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.attachments.ArmorData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;

public record PlayerArmorDataPayload(
        List<ArmorData> armorData

) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PlayerArmorDataPayload> TYPE =
            new CustomPacketPayload.Type<>(TOTVW.registerID("player_armor_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerArmorDataPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.fromCodec(ArmorData.CODEC.listOf()), PlayerArmorDataPayload::armorData,
                    PlayerArmorDataPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
