package cliffordha.totvw.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record VWTrustInteractionData(String player, String trustee) {
    public static final Codec<VWTrustInteractionData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("thisMob").forGetter(VWTrustInteractionData::player),
                    Codec.STRING.fieldOf("interactedWith").forGetter(VWTrustInteractionData::trustee)
            ).apply(instance, VWTrustInteractionData::new)
    );

    public static VWTrustInteractionData create() {
        return new VWTrustInteractionData("", "");
    }
}