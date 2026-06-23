package cliffordha.totvw.world;

import cliffordha.totvw.tag.ModBiomeTags;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.world.level.levelgen.GenerationStep;

public class ModBiomeModifications {
    public static void addBiomeModifications() {
        BiomeModifications.addFeature(
                BiomeSelectors.tag(ModBiomeTags.IS_VERDANT_BIOMES),
                GenerationStep.Decoration.LOCAL_MODIFICATIONS,
                MiscOverworldPlacements.DISK_CLAY
        );
        BiomeModifications.addFeature(
                BiomeSelectors.tag(ModBiomeTags.IS_VERDANT_BIOMES),
                GenerationStep.Decoration.LOCAL_MODIFICATIONS,
                MiscOverworldPlacements.DISK_GRAVEL
        );
        BiomeModifications.addFeature(
                BiomeSelectors.tag(ModBiomeTags.IS_VERDANT_BIOMES),
                GenerationStep.Decoration.LOCAL_MODIFICATIONS,
                MiscOverworldPlacements.DISK_SAND
        );
    }
}
