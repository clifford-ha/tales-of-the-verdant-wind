package cliffordha.totvw.registry.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.entity.LivingEntity;

import java.util.UUID;

public record NameHolder(String name, UUID uuid) {
    public static final NameHolder EMPTY = new NameHolder("", AttachmentUtil.EMPTY_UUID);
    public static final Codec<NameHolder> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.STRING.fieldOf("name").forGetter(NameHolder::name),
                    UUIDUtil.CODEC.fieldOf("uuid").forGetter(NameHolder::uuid)
            ).apply(instance, NameHolder::new)
    );

    public boolean isValid() {
        return (this != EMPTY) || (this.name() != null && this.uuid() != null);
    }

    public boolean sameUUID(NameHolder other) {
        return this.uuid().equals(other.uuid());
    }
    public boolean sameName(NameHolder other) {
        return this.name().equals(other.name());
    }
    public static NameHolder of(LivingEntity entity) {
        return new NameHolder(entity.getPlainTextName(), entity.getUUID());
    }
}
