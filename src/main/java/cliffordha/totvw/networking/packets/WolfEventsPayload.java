package cliffordha.totvw.networking.packets;

import cliffordha.totvw.TOTVW;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.ArrayList;
import java.util.List;

/**
 * Payload for the WolfEvents packet.
 * <p> NOTES: The second index must be listed below
 * <p> RULES: The first index is the wolf's entity id then set the second for ACTIONS defined here:
 * <p> 0 = test, for debugging only, 1 = reserved for other purposes
 */
public record WolfEventsPayload(
        List<Integer> integerFlags

) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<WolfEventsPayload> TYPE =
            new CustomPacketPayload.Type<>(TOTVW.registerID("wolf_events"));

    public static final StreamCodec<RegistryFriendlyByteBuf, WolfEventsPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.fromCodec(Codec.INT.listOf()), WolfEventsPayload::integerFlags,
                    WolfEventsPayload::new
            );

    public static WolfEventsPayload EMPTY = new WolfEventsPayload(List.of());

    public static WolfEventsPayload setOf(int amount) {
        if (amount == 0) return EMPTY;
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            list.add(i);
        }
        return new WolfEventsPayload(list);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final int DROP_RUNESTONE_FROM_SHEARED = 2;
    public static final int CHANGE_BENEDICTION_RETURN_METHOD = 3;
    public static final int REMOVE_RETURN_POINT = 4;
}
