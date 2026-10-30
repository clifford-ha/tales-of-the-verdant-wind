package cliffordha.totvw.entity;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.data.WolfStorage;
import cliffordha.totvw.entity.skills.VerdantWindBlessing;
import cliffordha.totvw.item.custom.SoulRunestonePlate;
import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.VWEffects;
import cliffordha.totvw.registry.attachments.AttachmentUtil;
import cliffordha.totvw.registry.attachments.PlayerPrefs;
import cliffordha.totvw.registry.attachments.ReturnPointData;
import cliffordha.totvw.registry.attachments.VWAttachments;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import cliffordha.totvw.util.ExtUtil;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static cliffordha.totvw.util.ExtUtil.*;

public class WolfBenedictionHelper {
    public static boolean reviveWolf(Wolf wolf, ServerLevel level, DamageSource source) {
        AttachmentType<Integer> BENEDICTION_STACK = WolfAttachment.BENEDICTION;
        int STACK_BEFORE = wolf.getAttachedOrElse(BENEDICTION_STACK, 0);
        if (STACK_BEFORE < 1) return false;

        VerdantWindBlessing.applyBenedictionEffects(wolf, true);

        wolf.dropLeash();
        wolf.unRide();
        wolf.setOrderedToSit(false);

        level.broadcastEntityEvent(wolf, (byte) 35);

        // main
        wolf.setAttached(BENEDICTION_STACK, STACK_BEFORE - 1);

        String name = wolf.getPlainTextName();
        int STACK_AFTER = wolf.getAttachedOrElse(BENEDICTION_STACK, 0);
        if (STACK_AFTER == 0) {
            sendToChat(wolf, VWColors.BLOODLUST_MUTED, name + " used up all Benediction stacks");
        } else {
            sendToChat(wolf, VWColors.VERDANT_WIND_MUTED,STACK_AFTER + " Benediction stack remaining for " + name);
        }

        if (wolf.getOwner() != null && wolf.getOwner() instanceof Player player) {
            Entity attacker = source.getEntity();
            if (attacker != null) {
                sendToChat(player, VWColors.BLOODLUST_MUTED, false, attacker.getPlainTextName() + " tried to kill " + name + ".");
            } else {
                String report = "Someone tried to kill " + name + ".";
                if (source.is(DamageTypes.GENERIC_KILL)) report = name + " was killed through a command.";
                sendToChat(player, VWColors.BLOODLUST_MUTED, false, report);
            }
        }

        if (wolf.isTame()) {
            LivingEntity owner = wolf.getOwner();

            if (owner == null) {
                ReturnPointData data = wolf.getAttachedOrElse(WolfAttachment.RETURN_POINT, ReturnPointData.DEFAULT);
                if (data.recordAfter()) {
                    teleportToReturnPointOrOwner(wolf, null);
                } else {
                    saveWolfFile(level.getServer(), wolf);
                }
            } else {
                boolean canTP = wolf.canTeleport(wolf.level(), owner.level())
                        && wolf.level().dimensionType().equals(owner.level().dimensionType())
                        && owner.getAttachedOrElse(PlayerPrefs.BENEDICTION_TELEPORT_AFTER_SAVE, VWConfig.get().SYNCED_TELEPORT_AFTER_SAVE);
                if (canTP) {
                    if (wolf.distanceTo(owner) < 14) return true;
                    tryTeleport(wolf, owner);
                } else {
                    teleportToReturnPointOrOwner(wolf, owner);
                }
            }
        }
        return true;
    }
    public static void saveWolfFile(MinecraftServer server, Wolf wolf) {
        if (WolfStorage.saveWolfNbt(server, wolf)) {
            wolf.discard();
        }
    }
    public static void loadWolfFilesIfPresent(Player player, ServerLevel level) {
        if (loadWolfFiles(player, level)) {
            deleteLoadedWolfFiles(player, level);
        }
    }
    public static boolean loadWolf(ServerLevel level, Player player, CompoundTag wolfTag, boolean fromRecords) {
        if (wolfTag.isEmpty()) return false;
        ListTag setPosition = SoulRunestonePlate.getPosition(player);
        wolfTag.put("Pos", setPosition);

        TagValueInput input = (TagValueInput) TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), wolfTag);
        Optional<Entity> wolfEntity = EntityType.create(input, level, new EntitySpawnRequest(EntitySpawnReason.LOAD, false));

        if (wolfEntity.isPresent()) {
            Entity wolfE = wolfEntity.get();
            if (level.getEntity(wolfE.getUUID()) != null) {
                wolfE.setUUID(UUID.randomUUID());
            }
            level.addFreshEntity(wolfE);
            Wolf wolf = (Wolf) wolfE;
            if (!fromRecords) {
                if (wolf.getHealth() < 2f) wolf.setHealth(4f);
            } else {
                wolf.heal(100f);
            }
            wolf.removeAllEffects();
            wolf.teleportToAroundBlockPos(player.blockPosition());
            addHiddenEffect(wolf, VWEffects.WIND_VEIL, 3, 0);
            UUID sharedID = VWAttachments.validateSharedID(wolf, player);

            if (fromRecords) {
                WolfStorage.deleteWolfNBTFile(level.getServer(), sharedID, wolf.getUUID());
            }
        }
        return true;
    }
    public static void loadWolves(ServerLevel level, Player player, List<CompoundTag> wolfTag) {
        List<CompoundTag> catcher = new ArrayList<>();
        for (CompoundTag tag : wolfTag) {
            if (!loadWolf(level, player, tag, false)) {
                catcher.add(tag);
            }
        }
        if (catcher.isEmpty()) return;
        int pass = 0;
        for (CompoundTag tag : catcher) {
            if (loadWolf(level, player, tag, false)) pass++;
        }
        if (pass != catcher.size()) {
            ExtUtil.sendToChat(player, VWColors.VERDANT_WIND_MUTED, "Failed to load " + (catcher.size() - pass) + " wolves");
        }
    }
    public static boolean loadWolfFiles(Player player, ServerLevel level) {
        UUID sharedID = player.getAttachedOrElse(VWAttachments.WOLF_PLAYER_SHARED_ID, AttachmentUtil.EMPTY_UUID);
        if (sharedID.equals(AttachmentUtil.EMPTY_UUID)) return false;

        try {
            Path path = WolfStorage.benedictionDirectory(level.getServer(), sharedID);
            if (!Files.exists(path)) return false;
            try(DirectoryStream<Path> files = Files.newDirectoryStream(path, "*.nbt")) {

                int success = 0;
                for (Path file : files) {
                    CompoundTag tag = NbtIo.readCompressed(file, NbtAccounter.unlimitedHeap());
                    if (loadWolf(level, player, tag, true)) success++;
                }
                if (success > 0) {
                    String message = success == 1 ? "1 recorded wolf" : success + " recorded wolves";
                    ExtUtil.sendToChat(player, VWColors.VERDANT_WIND_MUTED, "Loaded " + message);
                    return true;
                } else {
                    if (TOTVW.IN_DEVELOPMENT) {
                        ExtUtil.sendToChat(player, VWColors.VERDANT_WIND_MUTED, "No recorded wolves found");
                    }
                    return false;
                }
            } catch (IOException e) {
                return false;
            }

        } catch (IOException e) {
            return false;
        }
    }
    public static void deleteLoadedWolfFiles(Player player, ServerLevel level) {
        UUID sharedID = player.getAttachedOrElse(VWAttachments.WOLF_PLAYER_SHARED_ID, AttachmentUtil.EMPTY_UUID);
        if (sharedID.equals(AttachmentUtil.EMPTY_UUID)) return;
        try {
            Path path = WolfStorage.benedictionDirectory(level.getServer(), sharedID);
            if (!Files.exists(path)) return;
            List<Path> files = Files.list(path).toList();
            if (files.isEmpty()) return;

            for (Path file : files) {
                CompoundTag tag = NbtIo.readCompressed(file, NbtAccounter.unlimitedHeap());
                if (tag.isEmpty()) return;
                int[] empty = new int[4];
                UUID wolfID = UUIDUtil.uuidFromIntArray(tag.getIntArray("UUID").orElse(empty));
                if (wolfID.equals(UUIDUtil.uuidFromIntArray(empty))) return;

                WolfStorage.deleteWolfNBTFile(level.getServer(), sharedID, wolfID);
            }

        } catch (IOException _) {
        }
    }


    private static void teleportToReturnPointOrOwner(Wolf wolf, @Nullable LivingEntity owner) {
        if (owner != null && !isSameDimension(wolf, owner)) {
            ServerLevel level = (ServerLevel) owner.level();
            List<Wolf> wolves = wolf.level().getEntitiesOfClass(
                    Wolf.class,
                    scanArea(wolf, 48),
                    other -> other.getOwner() == owner);
            boolean tpAll = owner.getAttachedOrElse(PlayerPrefs.BENEDICTION_WOLF_TP_ALL, VWConfig.get().SYNCED_WOLF_TP_ALL);
            int tpMethod = owner.getAttachedOrElse(PlayerPrefs.BENEDICTION_WOLF_TP_METHOD, VWConfig.get().SYNCED_WOLF_TP_METHOD);

            if (!wolves.isEmpty() && tpAll) {
                for (Wolf other : wolves) {
                    if (tpMethod == 0) {
                        other.teleport(teleporter(level, owner));
                    } else {
                        addEffect(owner, VWEffects.WIND_VEIL, 3, 0);
                        owner.teleport(teleporter(level, wolf));
                        other.teleport(teleporter(level, wolf));
                    }
                    addEffect(other, VWEffects.WIND_VEIL, 3, 0);
                }
            }

            if (tpMethod == 0) wolf.teleport(teleporter(level, owner));
            addEffect(wolf, VWEffects.WIND_VEIL, 3, 0);
            return;
        }
        ReturnPointData data = wolf.getAttachedOrElse(WolfAttachment.RETURN_POINT, ReturnPointData.DEFAULT);
        if (data.isValid() && ReturnPointData.returnToTargetOrDefault(wolf, data, false)) {
            addEffect(wolf, VWEffects.WIND_VEIL, 3, 0);
            wolf.teleportToAroundBlockPos(data.pos());
        }

    }
    private static TeleportTransition teleporter(ServerLevel level, LivingEntity entity) {
        return new TeleportTransition(
                level,
                new Vec3(entity.getX(), entity.getY(), entity.getZ()),
                Vec3.ZERO,
                level.getRespawnData().yaw(),
                level.getRespawnData().pitch(),
                TeleportTransition.PLACE_PORTAL_TICKET
        );
    }
    private static void tryTeleport(Wolf wolf, LivingEntity owner) {
        BlockPos playerPos = owner.blockPosition();
        BlockPos wolfPos = wolf.blockPosition();
        if (owner.getAttachedOrElse(PlayerPrefs.BENEDICTION_WOLF_TP_METHOD, VWConfig.get().SYNCED_WOLF_TP_METHOD) < 1) {
            wolf.teleportToAroundBlockPos(playerPos);
        } else {
            owner.teleportTo(wolfPos.getX(), wolfPos.getY(), wolfPos.getZ());
        }
    }
}
