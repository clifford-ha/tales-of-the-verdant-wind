package cliffordha.totvw.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class PlayerStorage {
    private static Path getPlayerFolder(MinecraftServer server, UUID uuid) throws IOException {
        Path dir = DataStorageUtil.rootFolder(server, "players").resolve(String.valueOf(uuid));
        Files.createDirectories(dir);
        return dir;
    }

    public static Optional<CompoundTag> loadNbt(MinecraftServer server, UUID playerUUID, String fileName) {
        try {
            Path path = getPlayerFolder(server, playerUUID).resolve(fileName + ".nbt");
            if (!Files.exists(path)) return Optional.empty();
            return Optional.of(NbtIo.readCompressed(path, NbtAccounter.unlimitedHeap()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    public static boolean saveNbt(MinecraftServer server, Player player, CompoundTag data) {
        try {
            Path path = getPlayerFolder(server, player.getUUID()).resolve(player.getStringUUID() + ".nbt");
            NbtIo.writeCompressed(data, path);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean deleteNBTFile(MinecraftServer server, UUID sharedID, UUID uuid) {
        try {
            return Files.deleteIfExists(getPlayerFolder(server, sharedID).resolve(uuid + ".nbt"));
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean deleteFolder(MinecraftServer server, UUID uuid) {
        try {
            Path dir = getPlayerFolder(server, uuid);
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