package cliffordha.totvw.data;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DataStorageUtil {
    public static Path rootFolder(MinecraftServer server, String root) throws IOException {
        Path dir = server.getWorldPath(LevelResource.ROOT)
                .resolve("totvw").resolve(root);
        Files.createDirectories(dir);
        return dir;
    }
}
