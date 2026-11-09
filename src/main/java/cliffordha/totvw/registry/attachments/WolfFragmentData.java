package cliffordha.totvw.registry.attachments;

import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.animal.wolf.Wolf;

public record WolfFragmentData(
        int lifespan,
        int extension,
        int hits
) {
    public static final Codec<WolfFragmentData> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.INT.fieldOf("lifespan").forGetter(WolfFragmentData::lifespan),
                    Codec.INT.fieldOf("extension").forGetter(WolfFragmentData::extension),
                    Codec.INT.fieldOf("hits").forGetter(WolfFragmentData::hits)
            ).apply(instance, WolfFragmentData::new)
    );
    public static final WolfFragmentData EMPTY = new WolfFragmentData(0, 0, 0);

    public static void initialize(Wolf wolf) {
        wolf.setAttached(WolfAttachment.FRAGMENT_DATA, new WolfFragmentData(60, 0, 12));
    }

    public static void decrementLifespan(Wolf wolf) {
        WolfFragmentData data = wolf.getAttachedOrElse(WolfAttachment.FRAGMENT_DATA, EMPTY);
        wolf.setAttached(WolfAttachment.FRAGMENT_DATA, new WolfFragmentData(data.lifespan() - 1, data.extension(), data.hits()));
    }
    public static int getLifespan(Wolf wolf) {
        return get(wolf).lifespan();
    }
    public static void addLifespan(Wolf wolf, int value) {
        WolfFragmentData data = get(wolf);
        wolf.setAttached(WolfAttachment.FRAGMENT_DATA, new WolfFragmentData(data.lifespan() + value, data.extension(), data.hits()));
    }
    public static int getExtension(Wolf wolf) {
        return get(wolf).extension();
    }
    public static void addExtension(Wolf wolf, int extension) {
        WolfFragmentData data = get(wolf);
        wolf.setAttached(WolfAttachment.FRAGMENT_DATA, new WolfFragmentData(data.lifespan(), data.extension() + extension, data.hits()));
    }
    public static int getHits(Wolf wolf) {
        return get(wolf).hits();
    }
    public static void extendHits(Wolf wolf) {
        WolfFragmentData data = get(wolf);
        wolf.setAttached(WolfAttachment.FRAGMENT_DATA, new WolfFragmentData(data.lifespan(), data.extension(), data.hits() + 3));
    }
    public static void decrementHit(Wolf wolf) {
        WolfFragmentData data = get(wolf);
        wolf.setAttached(WolfAttachment.FRAGMENT_DATA, new WolfFragmentData(data.lifespan(), data.extension(), data.hits() - 1));
    }
    public static WolfFragmentData get(Wolf wolf) {
        return wolf.getAttachedOrElse(WolfAttachment.FRAGMENT_DATA, EMPTY);
    }
}
