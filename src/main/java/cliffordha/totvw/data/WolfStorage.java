package cliffordha.totvw.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.storage.TagValueOutput;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class WolfStorage {
    public static Path benedictionDirectory(MinecraftServer server, UUID sharedID) throws IOException {
        Path dir = DataStorageUtil.rootFolder(server, "wolves").resolve("benediction-records").resolve(sharedID.toString());
        Files.createDirectories(dir);
        return dir;
    }
    public static Path wolfFolder(MinecraftServer server, UUID ownID) throws IOException {
        Path dir = DataStorageUtil.rootFolder(server, "wolves").resolve("data").resolve(String.valueOf(ownID));
        Files.createDirectories(dir);
        return dir;
    }

    public static Optional<CompoundTag> loadWolfNBT(MinecraftServer server, UUID sharedID, UUID wolfID) {
        try {
            Path path = benedictionDirectory(server, sharedID).resolve(wolfID + ".nbt");
            if (!Files.exists(path)) return Optional.empty();
            return Optional.of(NbtIo.readCompressed(path, NbtAccounter.unlimitedHeap()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    public static Optional<CompoundTag> loadNBT(MinecraftServer server, Wolf wolf, String fileName) {
        try {
            Path path = wolfFolder(server, wolf.getUUID()).resolve(fileName + ".nbt");
            if (!Files.exists(path)) return Optional.empty();
            return Optional.of(NbtIo.readCompressed(path, NbtAccounter.unlimitedHeap()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    public static boolean saveWolfNbt(MinecraftServer server, Wolf wolf) {
        UUID sharedID = wolf.getOwnerReference() != null ? wolf.getOwnerReference().getUUID() : null;
        if (sharedID == null) return false;

        wolf.unRide();
        wolf.dropLeash();

        wolf.extinguishFire();
        wolf.removeAllEffects();
        wolf.setOrderedToSit(false);

        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, wolf.registryAccess());
        boolean saved = wolf.save(output);
        CompoundTag data = output.buildResult();
        if (!saved || data.isEmpty()) return false;
        data.remove("Pos");

        try {
            Path path = benedictionDirectory(server, sharedID).resolve(wolf.getStringUUID() + ".nbt");
            NbtIo.writeCompressed(data, path);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public static boolean saveNBTFile(MinecraftServer server, Wolf wolf, CompoundTag data, String fileName) {
        try {
            Path path = wolfFolder(server, wolf.getUUID()).resolve(fileName+ ".nbt");
            NbtIo.writeCompressed(data, path);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean deleteWolfNBTFile(MinecraftServer server, UUID sharedID, UUID soulID) {
        try {
            return Files.deleteIfExists(benedictionDirectory(server, sharedID).resolve(soulID + ".nbt"));
        } catch (IOException e) {
            return false;
        }
    }
    public static boolean deleteNBTFile(MinecraftServer server, Wolf wolf, String fileName) {
        try {
            return Files.deleteIfExists(wolfFolder(server, wolf.getUUID()).resolve(fileName + ".nbt"));
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean deleteFolder(MinecraftServer server, UUID uuid) {
        try {
            Path dir = benedictionDirectory(server, uuid);
            try (Stream<Path> walk = Files.walk(dir)) {
                walk.sorted(Comparator.reverseOrder()).forEach(p -> {
                    try { Files.delete(p); } catch (IOException ignored) {}
                });
            }
            return !Files.exists(dir);
        } catch (IOException e) {
            return false;
        }
    }
}