package cliffordha.totvw.world.tree;

import cliffordha.totvw.TOTVW;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacer;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacerType;

public class VWRootPlacerTypes {
    public static final RootPlacerType<RootPlacer> DEFAULT = Registry.register(
            BuiltInRegistries.ROOT_PLACER_TYPE, TOTVW.registerID("default_root_placer"),
            new RootPlacerType<>(DefaultRootPlacer.CODEC)
    );

    public static void register() {
    }
}
