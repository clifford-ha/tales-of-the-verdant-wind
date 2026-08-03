package cliffordha.totvw.world;

import cliffordha.totvw.tag.VWBiomeTags;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.data.worldgen.placement.CavePlacements;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.world.level.levelgen.GenerationStep;

public class VWBiomeModifications {
    public static void register() {
        BiomeModifications.addFeature(
                BiomeSelectors.tag(VWBiomeTags.IS_VERDANT_BIOMES),
                GenerationStep.Decoration.LOCAL_MODIFICATIONS,
                CavePlacements.CLASSIC_VINES
        );
        BiomeModifications.addFeature(
                BiomeSelectors.tag(VWBiomeTags.IS_VERDANT_BIOMES),
                GenerationStep.Decoration.LOCAL_MODIFICATIONS,
                VegetationPlacements.PATCH_SUGAR_CANE
        );
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(VWBiomes.VERDANT_FOREST),
                GenerationStep.Decoration.LOCAL_MODIFICATIONS,
                VegetationPlacements.PATCH_FIREFLY_BUSH_NEAR_WATER
        );
    }
}
