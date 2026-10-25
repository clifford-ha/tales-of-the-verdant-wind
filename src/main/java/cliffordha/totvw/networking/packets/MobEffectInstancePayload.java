package cliffordha.totvw.networking.packets;

import cliffordha.totvw.TOTVW;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.List;

public record MobEffectInstancePayload(List<MobEffectInstance> instance) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MobEffectInstancePayload> TYPE =
            new CustomPacketPayload.Type<>(TOTVW.registerID("mob_effect_instance"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MobEffectInstancePayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.fromCodec(MobEffectInstance.CODEC.listOf()), MobEffectInstancePayload::instance,
                    MobEffectInstancePayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
