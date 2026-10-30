package cliffordha.totvw.registry.attachments;

import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;

import java.util.List;

public record TetherData(List<String> tetheredEntities, List<String> blacklistedEntities, boolean autoAttackTethered, boolean tetherChunkTicks) {
    public static final TetherData EMPTY = new TetherData(List.of(), List.of(), false, false);
    public static final Codec<TetherData> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.STRING.listOf().fieldOf("tetheredEntities").forGetter(TetherData::tetheredEntities),
                    Codec.STRING.listOf().fieldOf("blacklistEntities").forGetter(TetherData::blacklistedEntities),
                    Codec.BOOL.fieldOf("autoAttackTethered").forGetter(TetherData::autoAttackTethered),
                    Codec.BOOL.fieldOf("tetherChunkTicks").forGetter(TetherData::tetherChunkTicks)
            ).apply(instance, TetherData::new)
    );

    public static boolean isTethered(LivingEntity entity) {
        TetherData data = entity.getAttachedOrElse(WolfAttachment.TETHER_DATA, EMPTY);
        if (data.tetheredEntities().isEmpty()) return false;
        for (String e : data.tetheredEntities()) {
            Identifier identifier = Identifier.tryParse(e);
            BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
            if (identifier != null && identifier.equals(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()))) {
                return true;
            }
        }
        return false;
    }
    public static TetherData updateEntities(TetherData data, List<String> entities, List<String> blacklist) {
        return new TetherData(entities, blacklist, data.autoAttackTethered(), data.tetherChunkTicks());
    }
    public static TetherData updateEntities(TetherData data, List<String> entities) {
        return new TetherData(entities, data.blacklistedEntities(), data.autoAttackTethered(), data.tetherChunkTicks());
    }
    public static TetherData updateBlacklisted(TetherData data, List<String> blacklist) {
        return new TetherData(data.tetheredEntities(), blacklist, data.autoAttackTethered(), data.tetherChunkTicks());
    }
    public static TetherData updateAutoAttack(TetherData data, boolean value) {
        return new TetherData(data.tetheredEntities(), data.blacklistedEntities(), value, data.tetherChunkTicks());
    }
    public static TetherData setLoadChunk(TetherData data, boolean value) {
        return new TetherData(data.tetheredEntities(), data.blacklistedEntities(), data.autoAttackTethered(), value);
    }
    public static TetherData of(Wolf wolf) {
        return wolf.getAttachedOrCreate(WolfAttachment.TETHER_DATA, () -> EMPTY);
    }
}
