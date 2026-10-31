package cliffordha.totvw.entity;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.data.WolfStorage;
import cliffordha.totvw.entity.skills.RevivalByProxy;
import cliffordha.totvw.entity.skills.VerdantWindBlessing;
import cliffordha.totvw.item.custom.SoulRunestonePlate;
import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.VWEffects;
import cliffordha.totvw.registry.attachments.PlayerPrefs;
import cliffordha.totvw.registry.attachments.ReturnPointData;
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
        int STACK = wolf.getAttachedOrElse(BENEDICTION_STACK, 0);
        if (STACK < 1) return false;

        VerdantWindBlessing.applyBenedictionEffects(wolf, true);

        wolf.dropLeash();
        wolf.unRide();
        wolf.setOrderedToSit(false);

        level.broadcastEntityEvent(wolf, (byte) 35);

        wolf.setAttached(BENEDICTION_STACK, STACK - 1);

        String name = wolf.getPlainTextName();
        STACK = wolf.getAttachedOrElse(BENEDICTION_STACK, 0);
        if (STACK == 0) {
            sendToChat(wolf, VWColors.BLOODLUST_MUTED, name + " used up all Benediction stacks");
        } else {
            sendToChat(wolf, VWColors.VERDANT_WIND_MUTED, STACK + " Benediction stack remaining for " + name);
        }
        if (!wolf.isTame()) return true;

        LivingEntity owner = wolf.getOwner();

        if (owner == null) {
            ReturnPointData data = wolf.getAttachedOrElse(WolfAttachment.RETURN_POINT, ReturnPointData.DEFAULT);
            if (data.recordAfter()) {
                saveWolfFiles(level.getServer(), List.of(wolf), null);
            } else {
                teleportToReturnPointOrOwner(level, wolf, null);
            }
            return true;
        }

        Entity attacker = source.getEntity();
        if (attacker != null) {
            sendToChat(owner, VWColors.BLOODLUST_MUTED, false, attacker.getPlainTextName() + " tried to kill " + name + ".");
        } else {
            String report = "Someone tried to kill " + name + ".";
            if (source.is(DamageTypes.GENERIC_KILL)) report = name + " was killed through a command.";
            sendToChat(owner, VWColors.BLOODLUST_MUTED, false, report);
        }

        // owner is close so revive in place
        if (wolf.distanceTo(owner) <= 8) return true;
        boolean canTP = owner.getAttachedOrElse(PlayerPrefs.BENEDICTION_TELEPORT_AFTER_SAVE, VWConfig.get().SYNCED_TELEPORT_AFTER_SAVE);
        if (canTP) {
            teleportToReturnPointOrOwner(level, wolf, owner);
            return true;
        }

        //record to file
        List<Wolf> targets = new ArrayList<>();
        targets.add(wolf);
        if (owner.getAttachedOrElse(PlayerPrefs.BENEDICTION_WOLF_TP_ALL, VWConfig.get().SYNCED_WOLF_TP_ALL)) {
            targets.addAll(level.getEntitiesOfClass(Wolf.class, scanArea(wolf, 32),
                    other -> other != wolf && other.isTame() && other.getOwner() == owner));
        }
        saveWolfFiles(level.getServer(), targets, owner);
        return true;
    }
    private static void teleportToReturnPointOrOwner(ServerLevel level, Wolf wolf, @Nullable LivingEntity owner) {
        if (owner != null) {
            List<Wolf> wolves1 = owner.level().getEntitiesOfClass(Wolf.class, scanArea(wolf, 48), other -> other.getOwner() == owner);
            List<Wolf> wolves = level.getEntitiesOfClass(Wolf.class, scanArea(wolf, 48), other -> other.getOwner() == owner);

            if (isSameDimension(wolf, owner) && wolf.canTeleport(wolf.level(), owner.level())) {
                tryTeleport(wolf, owner);
                performTeleportAll(level, wolf, wolves, owner, true);
                performTeleportAll(level, wolf, wolves1, owner, true);


            } else {
                if (owner.getAttachedOrElse(PlayerPrefs.BENEDICTION_WOLF_TP_METHOD, VWConfig.get().SYNCED_WOLF_TP_METHOD) < 1) {
                    wolf.teleport(teleporter(level, owner));

                } else {
                    owner.teleport(teleporter(level, wolf));
                    addEffect(owner, VWEffects.WIND_VEIL, 3, 0);
                }
                RevivalByProxy.untetherWolf(wolf);
                performTeleportAll(level, wolf, wolves, owner, false);
                performTeleportAll(level, wolf, wolves1, owner, false);
                addEffect(wolf, VWEffects.WIND_VEIL, 3, 0);
            }
            return;
        }
        ReturnPointData data = wolf.getAttachedOrElse(WolfAttachment.RETURN_POINT, ReturnPointData.DEFAULT);
        if (data.isValid() && ReturnPointData.returnToTargetOrDefault(wolf, data, false)) {
            addEffect(wolf, VWEffects.WIND_VEIL, 3, 0);
            wolf.teleportToAroundBlockPos(data.pos());
        } else {
            saveWolfFile(level.getServer(), wolf);
        }
    }
    private static void performTeleportAll(ServerLevel level, Wolf main, List<Wolf> wolves, LivingEntity owner, boolean sameDimension) {
        if (!owner.getAttachedOrElse(PlayerPrefs.BENEDICTION_WOLF_TP_ALL, VWConfig.get().SYNCED_WOLF_TP_ALL)) return;
        if (wolves.isEmpty()) return;

        for (Wolf other : wolves) {
            if (other == main) continue;
            if (sameDimension) {
                tryTeleport(other, owner);

            } else {
                if (owner.getAttachedOrElse(PlayerPrefs.BENEDICTION_WOLF_TP_METHOD, VWConfig.get().SYNCED_WOLF_TP_METHOD) < 1) {
                    other.teleport(teleporter(level, owner));

                } else {
                    owner.teleport(teleporter(level, main));
                    other.teleport(teleporter(level, owner));
                }

                addEffect(other, VWEffects.WIND_VEIL, 3, 0);
            }
            RevivalByProxy.untetherWolf(other);
        }
    }
    private static TeleportTransition teleporter(ServerLevel level, LivingEntity entity) {
        ServerLevel newLevel = entity.level() instanceof ServerLevel serverLevel ? serverLevel : level;
        BlockPos pos = entity.blockPosition();
        if (!RevivalByProxy.validLevel(entity)) {
            pos = newLevel.getRespawnData().pos();
        }
        return new TeleportTransition(
                newLevel,
                new Vec3(pos.getX(), pos.getY(), pos.getZ()),
                Vec3.ZERO,
                newLevel.getRespawnData().yaw(),
                newLevel.getRespawnData().pitch(),
                TeleportTransition.PLACE_PORTAL_TICKET
        );
    }
    private static void tryTeleport(Wolf wolf, LivingEntity owner) {
        BlockPos playerPos = owner.blockPosition();
        BlockPos wolfPos = wolf.blockPosition();
        RevivalByProxy.untetherWolf(wolf);
        if (owner.getAttachedOrElse(PlayerPrefs.BENEDICTION_WOLF_TP_METHOD, VWConfig.get().SYNCED_WOLF_TP_METHOD) < 1) {
            wolf.teleportToAroundBlockPos(playerPos);
            addEffect(wolf, VWEffects.WIND_VEIL, 3, 0);
        } else {
            owner.teleportTo(wolfPos.getX(), wolfPos.getY(), wolfPos.getZ());
            addEffect(owner, VWEffects.WIND_VEIL, 3, 0);
        }
    }


    private static void saveWolfFiles(MinecraftServer server, List<Wolf> wolves, @Nullable LivingEntity owner) {
        for (Wolf w : wolves) {
            saveWolfFile(server, w);
        }
    }
    public static void saveWolfFile(MinecraftServer server, Wolf wolf) {
        if (WolfStorage.saveWolfNbt(server, wolf)) {
            wolf.remove(Entity.RemovalReason.UNLOADED_TO_CHUNK);
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

            if (fromRecords) {
                WolfStorage.deleteWolfNBTFile(level.getServer(), player.getUUID(), wolf.getUUID());
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
        try {
            Path path = WolfStorage.benedictionDirectory(level.getServer(), player.getUUID());
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
        try {
            Path path = WolfStorage.benedictionDirectory(level.getServer(), player.getUUID());
            if (!Files.exists(path)) return;
            List<Path> files = Files.list(path).toList();
            if (files.isEmpty()) return;

            for (Path file : files) {
                CompoundTag tag = NbtIo.readCompressed(file, NbtAccounter.unlimitedHeap());
                if (tag.isEmpty()) return;
                int[] empty = new int[4];
                UUID wolfID = UUIDUtil.uuidFromIntArray(tag.getIntArray("UUID").orElse(empty));
                if (wolfID.equals(UUIDUtil.uuidFromIntArray(empty))) return;

                WolfStorage.deleteWolfNBTFile(level.getServer(), player.getUUID(), wolfID);
            }

        } catch (IOException _) {
        }
    }
}
