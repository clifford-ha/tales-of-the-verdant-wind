package cliffordha.totvw.registry.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;

public record HavocData(
        HavocType type,
        int usageCount
) {
    public static final Codec<HavocData> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    HavocType.CODEC.fieldOf("type").forGetter(HavocData::type),
                    Codec.INT.fieldOf("usage_count").forGetter(HavocData::usageCount)
            ).apply(instance, HavocData::new)
    );

    public static final HavocData NONE = new HavocData(HavocType.NONE, 0);

    public static void decrementUsageCount(LivingEntity entity) {
        HavocData data = getData(entity);
        if (data.usageCount() <= 0) {
            entity.removeAttached(VWAttachment.HAVOC_DATA);
            return;
        }
        entity.setAttached(VWAttachment.HAVOC_DATA, data.decrementUsageCount());
    }
    public static int getUsageCount(LivingEntity entity) {
        return getData(entity).usageCount();
    }
    public HavocData addUsageCount(int count) {
        return new HavocData(type(), usageCount() + (count));
    }
    public HavocData decrementUsageCount() {
        return new HavocData(type(), usageCount() - 1);
    }
    public static HavocType getType(LivingEntity entity) {
        return getData(entity).type();
    }
    public static HavocData getData(LivingEntity entity) {
        return entity.getAttachedOrElse(VWAttachment.HAVOC_DATA, NONE);
    }
}
