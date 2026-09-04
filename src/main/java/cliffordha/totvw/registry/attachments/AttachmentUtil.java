package cliffordha.totvw.registry.attachments;

import cliffordha.totvw.TOTVW;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;

import java.util.List;

public class AttachmentUtil {
    public static AttachmentType<List<CompoundTag>> registerCompoundList(String name) {
        return AttachmentRegistry.create(
                Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name),
                builder -> builder
                        .persistent(CompoundTag.CODEC.listOf())
                        .copyOnDeath()
                        .initializer(List::of)
        );
    }
    public static AttachmentType<BlockPos> registerBlockPos(String name) {
        return AttachmentRegistry.create(
                Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name),
                blockPosBuilder -> blockPosBuilder
                        .persistent(BlockPos.CODEC)
                        .syncWith(BlockPos.STREAM_CODEC, AttachmentSyncPredicate.all())
                        .copyOnDeath()
                        .initializer( () -> BlockPos.ZERO)
        );
    }
    public static AttachmentType<String> registerString(String name) {
        return AttachmentRegistry.create(
                Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name),
                stringBuilder -> stringBuilder
                        .persistent(Codec.STRING)
                        .syncWith(ByteBufCodecs.STRING_UTF8, AttachmentSyncPredicate.all())
                        .copyOnDeath()
                        .initializer(() -> "")
        );
    }
    public static AttachmentType<Integer> registerInt(String name) {
        return AttachmentRegistry.create(
                Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name),
                builder -> builder
                        .persistent(Codec.INT)
                        .syncWith(ByteBufCodecs.INT, AttachmentSyncPredicate.all())
                        .copyOnDeath()
                        .initializer(() -> 0)
        );
    }
    public static AttachmentType<Integer> registerInt(String prefix, String name) {
        return AttachmentRegistry.create(
                Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, prefix + "_" + name),
                builder -> builder
                        .persistent(Codec.INT)
                        .syncWith(ByteBufCodecs.INT, AttachmentSyncPredicate.all())
                        .copyOnDeath()
                        .initializer(() -> 0)
        );
    }
    public static AttachmentType<Boolean> registerBool(String name) {
        return AttachmentRegistry.create(
                Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name),
                builder -> builder
                        .persistent(Codec.BOOL)
                        .syncWith(ByteBufCodecs.BOOL, AttachmentSyncPredicate.all())
                        .copyOnDeath()
                        .initializer(() -> false)
        );
    }
    public static AttachmentType<Boolean> registerBool(String prefix, String name) {
        return AttachmentRegistry.create(
                Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, prefix + "_" + name),
                builder -> builder
                        .persistent(Codec.BOOL)
                        .syncWith(ByteBufCodecs.BOOL, AttachmentSyncPredicate.all())
                        .copyOnDeath()
                        .initializer(() -> false)
        );
    }
    public static AttachmentType<Float> registerFloat(String name) {
        return AttachmentRegistry.create(
                Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name),
                builder -> builder
                        .persistent(Codec.FLOAT)
                        .syncWith(ByteBufCodecs.FLOAT, AttachmentSyncPredicate.all())
                        .copyOnDeath()
                        .initializer(() -> 0.0f)
        );
    }
}
