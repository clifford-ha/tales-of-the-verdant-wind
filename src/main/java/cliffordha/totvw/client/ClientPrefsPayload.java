package cliffordha.totvw.client;

import cliffordha.totvw.TOTVW;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ClientPrefsPayload(
        boolean showAtrocityCounter,
        boolean enableNotifiers,

        int benedictionLowHealthThreshold,
        boolean benedictionShareStack,
        boolean benedictionAlwaysTriggerBlessing,
        boolean benedictionTeleportAfterSave,
        int benedictionWolfTPMethod,
        int benedictionPlayerTPMethod,
        boolean benedictionWolfTPAll
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientPrefsPayload> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "client_prefs"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientPrefsPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, ClientPrefsPayload::showAtrocityCounter,
                    ByteBufCodecs.BOOL, ClientPrefsPayload::enableNotifiers,

                    ByteBufCodecs.INT, ClientPrefsPayload::benedictionLowHealthThreshold,
                    ByteBufCodecs.BOOL, ClientPrefsPayload::benedictionShareStack,
                    ByteBufCodecs.BOOL, ClientPrefsPayload::benedictionAlwaysTriggerBlessing,
                    ByteBufCodecs.BOOL, ClientPrefsPayload::benedictionTeleportAfterSave,
                    ByteBufCodecs.INT, ClientPrefsPayload::benedictionWolfTPMethod,
                    ByteBufCodecs.INT, ClientPrefsPayload::benedictionPlayerTPMethod,
                    ByteBufCodecs.BOOL, ClientPrefsPayload::benedictionWolfTPAll,
                    ClientPrefsPayload::new
            );
    @Override public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}