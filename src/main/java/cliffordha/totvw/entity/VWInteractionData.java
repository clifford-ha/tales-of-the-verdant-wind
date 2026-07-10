package cliffordha.totvw.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record VWInteractionData(String player, String trustee) {
    public static final Codec<VWInteractionData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("player").forGetter(VWInteractionData::player),
                    Codec.STRING.fieldOf("trustee").forGetter(VWInteractionData::trustee)
            ).apply(instance, VWInteractionData::new)
    );

    public static VWInteractionData create() {
        return new VWInteractionData("defaultPlayer", "defaultTrustee");
    }
}