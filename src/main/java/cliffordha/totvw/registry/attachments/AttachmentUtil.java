package cliffordha.totvw.registry.attachments;

import cliffordha.totvw.TOTVW;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.*;

public class AttachmentUtil {
    public static final UUID EMPTY_UUID = new UUID(0L, 0L);

    public static AttachmentType<HavocData> registerHavocData(String name) {
        return AttachmentRegistry.create(
                TOTVW.registerID(name),
                builder -> builder.persistent(HavocData.CODEC)
                        .syncWith(ByteBufCodecs.fromCodec(HavocData.CODEC), AttachmentSyncPredicate.all())
                        .initializer(() -> HavocData.NONE)
        );
    }
    public static AttachmentType<Runestone> registerRunestone(String name) {
        return AttachmentRegistry.create(
                TOTVW.registerID(name),
                builder -> builder.persistent(Runestone.CODEC)
                        .syncWith(ByteBufCodecs.fromCodec(Runestone.CODEC), AttachmentSyncPredicate.all())
                        .initializer(() -> Runestone.EMPTY)
        );
    }
    public static AttachmentType<List<NameHolder>> registerTrustData(String name) {
        return registerTrustData(name, false);
    }
    public static AttachmentType<List<NameHolder>> registerTrustData(String name, boolean copyOnDeath) {
        return AttachmentRegistry.create(
                TOTVW.registerID(name),
                builder -> {
                    builder.persistent(NameHolder.CODEC.listOf())
                            .syncWith(ByteBufCodecs.fromCodec(NameHolder.CODEC.listOf()), AttachmentSyncPredicate.all())
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
    public static AttachmentType<List<String>> registerStringList(String name) {
        return AttachmentRegistry.create(
                TOTVW.registerID(name),
                builder -> builder.persistent(Codec.STRING.listOf())
                        .syncWith(ByteBufCodecs.fromCodec(Codec.STRING.listOf()), AttachmentSyncPredicate.all())
                        .initializer(ArrayList::new)
        );
    }
    public static AttachmentType<UUID> registerUUID(String name) {
        return registerUUID(name, false);
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
    public static AttachmentType<ReturnPointData> registerReturnPointData(String name) {
        return AttachmentRegistry.create(
                TOTVW.registerID(name),
                builder -> builder.persistent(ReturnPointData.CODEC)
                        .syncWith(ByteBufCodecs.fromCodec(ReturnPointData.CODEC), AttachmentSyncPredicate.all())
                        .initializer(() -> ReturnPointData.DEFAULT)
        );
    }
    public static AttachmentType<BlockPos> registerBlockPos(String name) {
        return registerBlockPos(name, false);
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
                builder -> builder
                        .initializer(ArrayList::new)
        );
    }
    public static AttachmentType<List<ArmorData>> registerArmorList(String name) {
        return AttachmentRegistry.create(
                TOTVW.registerID(name),
                builder -> builder.persistent(ArmorData.CODEC.listOf())
                        .syncWith(ByteBufCodecs.fromCodec(ArmorData.CODEC.listOf()), AttachmentSyncPredicate.all())
                        .initializer(ArrayList::new)
        );
    }
    public static AttachmentType<ArmorData> registerArmorData(String name) {
        return AttachmentRegistry.create(
                TOTVW.registerID(name),
                builder -> {
                    builder.persistent(ArmorData.CODEC)
                            .syncWith(ByteBufCodecs.fromCodec(ArmorData.CODEC), AttachmentSyncPredicate.all())
                            .initializer(() -> ArmorData.NONE);
                }
        );
    }
    public static AttachmentType<TetherData> registerTetherData(String name) {
        return AttachmentRegistry.create(
                TOTVW.registerID(name),
                builder -> builder.persistent(TetherData.CODEC)
                        .syncWith(ByteBufCodecs.fromCodec(TetherData.CODEC), AttachmentSyncPredicate.all())
                        .initializer(() -> TetherData.EMPTY)
        );
    }
    public static AttachmentType<WolfFragmentData> registerWitherwolf(String name) {
        return AttachmentRegistry.create(
                TOTVW.registerID(name),
                builder -> builder.persistent(WolfFragmentData.CODEC)
                        .syncWith(ByteBufCodecs.fromCodec(WolfFragmentData.CODEC), AttachmentSyncPredicate.all())
                        .initializer(() -> WolfFragmentData.EMPTY)
        );
    }
    public static AttachmentType<Integer> registerUnsyncedInt(String name) {
        return AttachmentRegistry.create(
                TOTVW.registerID(name),
                builder -> builder.initializer(() -> 0)
        );
    }
    public static AttachmentType<Integer> registerInt(String name) {
        return registerInt(name, false);
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
    public static AttachmentType<Boolean> registerBool(String name) {
        return registerBool(name, false);
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
}
