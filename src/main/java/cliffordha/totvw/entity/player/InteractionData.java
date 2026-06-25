package cliffordha.totvw.entity.player;

import cliffordha.totvw.TOTVW;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record InteractionData(String player, String trustee) {
    public static final Codec<InteractionData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("player").forGetter(InteractionData::player),
                    Codec.STRING.fieldOf("trustee").forGetter(InteractionData::trustee)
            ).apply(instance, InteractionData::new)
    );

    public static InteractionData create() {
        return new InteractionData("defaultPlayer", "defaultTrustee");
    }
}