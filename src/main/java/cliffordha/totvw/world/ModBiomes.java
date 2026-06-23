package cliffordha.totvw.world;

import cliffordha.totvw.TOTVW;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import terrablender.api.Regions;

public class ModBiomes {
    public static final ResourceKey<Biome> VERDANT_MOUNTAINS = registerBiomeKey("verdant_mountains");
    public static final ResourceKey<Biome> VERDANT_FOREST = registerBiomeKey("verdant_forest");

    public static void registerBiomes() {
        Regions.register(new OverworldRegion(Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "totvw_overworld"), 20));
    }

    public static void bootstrap(BootstrapContext<Biome> context) {
        var carvers = context.lookup(Registries.CONFIGURED_CARVER);
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);

        register(context, VERDANT_MOUNTAINS, ModOverworldBiomes.verdantMountains(placedFeatures, carvers));
        register(context, VERDANT_FOREST, ModOverworldBiomes.verdantForest(placedFeatures, carvers));
    }

    private static void register(BootstrapContext<Biome> context, ResourceKey<Biome> key, Biome biome) {
        context.register(key, biome);
    }

    private static ResourceKey<Biome> registerBiomeKey(String name) {
        return ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name));
    }
}