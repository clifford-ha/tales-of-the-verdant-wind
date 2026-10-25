package cliffordha.totvw.networking.packets;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.util.BasicArmorData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;

public record WolfDataPayload(
        BasicArmorData armorData
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<WolfDataPayload> TYPE =
            new CustomPacketPayload.Type<>(TOTVW.registerID("wolf_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, WolfDataPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.fromCodec(BasicArmorData.CODEC), WolfDataPayload::armorData,
                    WolfDataPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
