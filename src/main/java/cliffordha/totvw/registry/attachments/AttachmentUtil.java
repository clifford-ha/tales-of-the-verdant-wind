package cliffordha.totvw.registry.attachments;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.VWItems;
import cliffordha.totvw.util.BasicArmorData;
import cliffordha.totvw.worldgen.dimension.VWDimensions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import oshi.util.tuples.Pair;

import java.util.*;

public class AttachmentUtil {
    public static final UUID EMPTY_UUID = new UUID(0L, 0L);

    private static final Codec<Pair<String, UUID>> ENTITY_DATA_CODEC = RecordCodecBuilder.create(
            pair -> pair.group(
                    Codec.STRING.fieldOf("name").forGetter(Pair::getA),
                    UUIDUtil.CODEC.fieldOf("uuid").forGetter(Pair::getB)
            ).apply(pair, Pair::new)
    );
    private static final StreamCodec<FriendlyByteBuf, Pair<String, UUID>> ENTITY_DATA_CODEC_STREAM = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, Pair::getA,
            UUIDUtil.STREAM_CODEC, Pair::getB,
            Pair::new
    );
    public static final Codec<Pair<String, Boolean>> STRING_BOOLEAN_CODEC = RecordCodecBuilder.create(
            pair -> pair.group(
                    Codec.STRING.fieldOf("id").forGetter(Pair::getA),
                    Codec.BOOL.fieldOf("value").forGetter(Pair::getB)
            ).apply(pair, Pair::new)
    );
    public static final StreamCodec<FriendlyByteBuf, Pair<String, Boolean>> STRING_BOOLEAN_CODEC_STREAM = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, Pair::getA,
            ByteBufCodecs.BOOL, Pair::getB,
            Pair::new
    );
    public static final Codec<Pair<ItemStack, EquipmentSlot>> ITEM_STACK = RecordCodecBuilder.create(
            pair -> pair.group(
                    ItemStack.CODEC.fieldOf("stack").forGetter(Pair::getA),
                    EquipmentSlot.CODEC.fieldOf("slot").forGetter(Pair::getB)
            ).apply(pair, Pair::new)
    );

    public static AttachmentType<HavocType> registerHavocType(String name, boolean copyOnDeath) {
        return AttachmentRegistry.create(
                TOTVW.registerID(name),
                builder -> {
                    builder.persistent(HavocType.CODEC)
                            .syncWith(ByteBufCodecs.fromCodec(HavocType.CODEC), AttachmentSyncPredicate.all())
                            .initializer(() -> HavocType.NONE);
                    if (copyOnDeath) builder.copyOnDeath();
                }
        );
    }
    public static AttachmentType<Runestone> registerRunestone(String name, boolean copyOnDeath) {
        return AttachmentRegistry.create(
                TOTVW.registerID(name),
                builder -> {
                    builder.persistent(Runestone.CODEC)
                            .syncWith(ByteBufCodecs.fromCodec(Runestone.CODEC), AttachmentSyncPredicate.all())
                            .initializer(() -> Runestone.EMPTY);
                    if (copyOnDeath) builder.copyOnDeath();
                }
        );
    }
    public static AttachmentType<List<Pair<String, UUID>>> registerListPair(String name, boolean copyOnDeath) {
        return AttachmentRegistry.create(
                TOTVW.registerID(name),
                builder -> {
                    builder.persistent(ENTITY_DATA_CODEC.listOf())
                            .syncWith(ENTITY_DATA_CODEC_STREAM.apply(ByteBufCodecs.list()), AttachmentSyncPredicate.all())
                            .initializer(ArrayList::new);
                    if (copyOnDeath) builder.copyOnDeath();
                }
        );
    }
    public static AttachmentType<List<Pair<String, Boolean>>> registerListPairEntities(String name, boolean copyOnDeath) {
        return AttachmentRegistry.create(
                TOTVW.registerID(name),
                builder -> {
                    builder.persistent(STRING_BOOLEAN_CODEC.listOf())
                            .syncWith(STRING_BOOLEAN_CODEC_STREAM.apply(ByteBufCodecs.list()), AttachmentSyncPredicate.all())
                            .initializer(ArrayList::new);
                    if (copyOnDeath) builder.copyOnDeath();
                }
        );
    }
    public static AttachmentType<List<CompoundTag>> registerCompoundList(String name, boolean copyOnDeath) {
        return AttachmentRegistry.create(
                TOTVW.registerID(name),
                builder -> {
                    builder.persistent(CompoundTag.CODEC.listOf())
                            .syncWith(ByteBufCodecs.fromCodec(CompoundTag.CODEC.listOf()), AttachmentSyncPredicate.all())
                            .initializer(ArrayList::new);
                    if (copyOnDeath) builder.copyOnDeath();
                }
        );
    }
    public static AttachmentType<List<String>> registerStringList(String name, boolean copyOnDeath) {
        return AttachmentRegistry.create(
                TOTVW.registerID(name),
                builder -> {
                    builder.persistent(Codec.STRING.listOf())
                            .syncWith(ByteBufCodecs.fromCodec(Codec.STRING.listOf()), AttachmentSyncPredicate.all())
                            .initializer(ArrayList::new);
                    if (copyOnDeath) builder.copyOnDeath();
                }
        );
    }
    public static AttachmentType<List<UUID>> registerUUIDList(String name, boolean copyOnDeath) {
        return AttachmentRegistry.create(
                TOTVW.registerID(name),
                builder -> {
                    builder.persistent(UUIDUtil.CODEC.listOf())
                            .syncWith(ByteBufCodecs.fromCodec(UUIDUtil.CODEC.listOf()), AttachmentSyncPredicate.all())
                            .initializer(ArrayList::new);
                    if (copyOnDeath) builder.copyOnDeath();
                }
        );
    }
    public static AttachmentType<UUID> registerUUID(String name, boolean copyOnDeath) {
        return AttachmentRegistry.create(
                TOTVW.registerID(name),
                builder -> {
                    builder.persistent(UUIDUtil.CODEC)
                            .syncWith(ByteBufCodecs.fromCodec(UUIDUtil.CODEC), AttachmentSyncPredicate.all())
                            .initializer(() -> EMPTY_UUID);
                    if (copyOnDeath) builder.copyOnDeath();
                }
        );
    }
    public static AttachmentType<ResourceKey<Level>> registerLevel(String name, boolean copyOnDeath) {
        return AttachmentRegistry.create(
                TOTVW.registerID(name),
                builder -> {
                    builder.persistent(Level.RESOURCE_KEY_CODEC)
                            .syncWith(ByteBufCodecs.fromCodec(Level.RESOURCE_KEY_CODEC), AttachmentSyncPredicate.all())
                            .initializer(() -> VWDimensions.NONE);
                    if (copyOnDeath) builder.copyOnDeath();
                }
        );
    }
    public static AttachmentType<BlockPos> registerBlockPos(String name, boolean copyOnDeath) {
        return AttachmentRegistry.create(
                TOTVW.registerID(name),
                builder -> {
                    builder.persistent(BlockPos.CODEC)
                            .syncWith(BlockPos.STREAM_CODEC, AttachmentSyncPredicate.all())
                            .initializer(() -> BlockPos.ZERO);
                    if (copyOnDeath) builder.copyOnDeath();
                }
        );
    }
    public static <E> Codec<Collection<E>> collectionOf(Codec<E> elementCodec) {
        return elementCodec.listOf().xmap(
                ArrayList::new,
                ArrayList::new
        );
    }
    public static final Codec<Collection<MobEffectInstance>> EFFECT_COLLECTION_CODEC = collectionOf(MobEffectInstance.CODEC);
    public static AttachmentType<Collection<MobEffectInstance>> registerEffectsCollection(String name) {
        return AttachmentRegistry.create(
                TOTVW.registerID(name),
                builder -> builder.persistent(EFFECT_COLLECTION_CODEC)
                        .syncWith(ByteBufCodecs.fromCodec(EFFECT_COLLECTION_CODEC), AttachmentSyncPredicate.all())
                        .initializer(ArrayList::new)
        );
    }
    public static AttachmentType<List<BasicArmorData>> registerArmorList(String name) {
        return AttachmentRegistry.create(
                TOTVW.registerID(name),
                builder -> builder.persistent(BasicArmorData.CODEC.listOf())
                        .syncWith(ByteBufCodecs.fromCodec(BasicArmorData.CODEC.listOf()), AttachmentSyncPredicate.all())
                        .initializer(ArrayList::new)
        );
    }
    public static AttachmentType<BasicArmorData> registerArmorData(String name) {
        return AttachmentRegistry.create(
                TOTVW.registerID(name),
                builder -> {
                    builder.persistent(BasicArmorData.CODEC)
                            .syncWith(ByteBufCodecs.fromCodec(BasicArmorData.CODEC), AttachmentSyncPredicate.all())
                            .initializer(() -> BasicArmorData.NONE);
                }
        );
    }
    public static AttachmentType<Integer> registerInt(String name, boolean copyOnDeath) {
        return AttachmentRegistry.create(
                TOTVW.registerID(name),
                builder -> {
                    builder.persistent(Codec.INT)
                            .syncWith(ByteBufCodecs.INT, AttachmentSyncPredicate.all())
                            .initializer(() -> 0);
                    if (copyOnDeath) builder.copyOnDeath();
                }
        );
    }
    public static AttachmentType<Boolean> registerBool(String name, boolean copyOnDeath) {
        return AttachmentRegistry.create(
                TOTVW.registerID(name),
                builder -> {
                    builder.persistent(Codec.BOOL)
                            .syncWith(ByteBufCodecs.BOOL, AttachmentSyncPredicate.all())
                            .initializer(() -> false);
                    if (copyOnDeath) builder.copyOnDeath();
                }
        );
    }
    public static AttachmentType<List<Boolean>> registerBoolList(String name, boolean copyOnDeath) {
        return AttachmentRegistry.create(
                TOTVW.registerID(name),
                builder -> {
                    builder.persistent(Codec.BOOL.listOf())
                            .syncWith(ByteBufCodecs.fromCodec(Codec.BOOL.listOf()), AttachmentSyncPredicate.all())
                            .initializer(ArrayList::new);
                    if (copyOnDeath) builder.copyOnDeath();
                }
        );
    }
    public static AttachmentType<Float> registerFloat(String name, boolean copyOnDeath) {
        return AttachmentRegistry.create(
                TOTVW.registerID(name),
                builder -> {
                    builder.persistent(Codec.FLOAT)
                            .syncWith(ByteBufCodecs.FLOAT, AttachmentSyncPredicate.all())
                            .initializer(() -> 0.0f);
                    if (copyOnDeath) builder.copyOnDeath();
                }
        );
    }

    public static void setAttachment(LivingEntity entity, AttachmentType<Integer> type, int amount) {
        int current = entity.getAttachedOrElse(type, 0);
        entity.setAttached(type, current + (amount));

        if (entity.getAttachedOrElse(type, 0) <= 0) {
            entity.removeAttached(type);
        }
    }
    public static void setAttachment(LivingEntity entity, AttachmentType<Integer> type, int amount, int limit) {
        int current = entity.getAttachedOrElse(type, 0);
        entity.setAttached(type, Math.min(current + (amount), limit));

        if (entity.getAttachedOrElse(type, 0) <= 0) {
            entity.removeAttached(type);
        }
    }
}
