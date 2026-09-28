package cliffordha.totvw.world;

import cliffordha.totvw.worldgen.VWPlacedFeatures;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ARGB;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.attribute.*;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class VWOverworldBiomes {
        public static Biome verdantMountains(HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<WorldCarver> carverGetter) {
            MobSpawnSettings.Builder spawner = new MobSpawnSettings.Builder();
            BiomeGenerationSettings.Builder biome = new BiomeGenerationSettings.Builder(placedFeatureGetter, carverGetter);

            spawner.addSpawn(EntityTypes.WOLF, 10, UniformInt.of(1, 2));
            spawner.addSpawn(EntityTypes.SNIFFER, 1, UniformInt.of(0, 1));
            spawner.addSpawn(EntityTypes.CHICKEN, 4, UniformInt.of(2, 4));
            spawner.addSpawn(EntityTypes.GOAT, 10, UniformInt.of(3, 4));

            spawner.addSpawn(EntityTypes.ZOMBIE, 10, UniformInt.of(2, 2));
            spawner.addSpawn(EntityTypes.SKELETON, 30, UniformInt.of(2, 2));

            spawner.addSpawn(EntityTypes.TROPICAL_FISH, 1, UniformInt.of(0, 1));

            spawner.build();

            addVerdantDefaults(biome);
            biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VWPlacedFeatures.VERDANT_FARMLANDS_PATCH_KEY);
            biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VWPlacedFeatures.VERDANT_PILLARS_KEY);
            biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VWPlacedFeatures.VERDANT_SPRUCE_TREE_LOWER_KEY);


            return new Biome.BiomeBuilder()
                    .hasPrecipitation(true)
                    .temperature(0.45F)
                    .downfall(0.85F)

                    .specialEffects((new BiomeSpecialEffects.Builder()
                            .waterColor(0x48bcd9)
                            .grassColorOverride(0x286B60) //0x0f6b5a
                            .foliageColorOverride(0x0b9c78)
                            .dryFoliageColorOverride(0x0b9c78)
                            ).build())
                    .mobSpawnSettings(spawner.build()).generationSettings(biome.build())
                    .setAttribute(EnvironmentAttributes.INCREASED_FIRE_BURNOUT, true)
                    .setAttribute(EnvironmentAttributes.FOG_END_DISTANCE, 1024f)
                    .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, ARGB.vector3fFromRGB24(0x48d9c1))
                    .setAttribute(EnvironmentAttributes.WATER_FOG_END_DISTANCE, 128f)
                    .setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(SoundEvents.MUSIC_BIOME_JAGGED_PEAKS))
                    .build();
        }

    public static Biome verdantForest(HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<WorldCarver> carverGetter) {
        MobSpawnSettings.Builder spawner = new MobSpawnSettings.Builder();
        BiomeGenerationSettings.Builder biome = new BiomeGenerationSettings.Builder(placedFeatureGetter, carverGetter);

        spawner.addSpawn(EntityTypes.WOLF, 10, UniformInt.of(3, 4));
        spawner.addSpawn(EntityTypes.SNIFFER, 1, UniformInt.of(0, 1));
        spawner.addSpawn(EntityTypes.CHICKEN, 8, UniformInt.of(2, 4));
        spawner.addSpawn(EntityTypes.GOAT, 6, UniformInt.of(0, 2));

        spawner.addSpawn(EntityTypes.ZOMBIE, 30, UniformInt.of(2, 2));
        spawner.addSpawn(EntityTypes.SKELETON, 10, UniformInt.of(2, 2));

        spawner.addSpawn(EntityTypes.TROPICAL_FISH, 1, UniformInt.of(0, 1));
        spawner.addSpawn(EntityTypes.NAUTILUS, 1, UniformInt.of(0, 1));

        spawner.build();

        addVerdantDefaults(biome);
        biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VWPlacedFeatures.VERDANT_FARMLANDS_DISK_KEY);
        biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VWPlacedFeatures.ANCIENT_VERDANT_SPRUCE_TREE_KEY);
        biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VWPlacedFeatures.VERDANT_SPRUCE_BUSH_TREE_KEY);

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.35F)
                .downfall(0.85F)

                .specialEffects((new BiomeSpecialEffects.Builder()
                        .waterColor(0x7cc0d9)
                        .grassColorOverride(0x0f6b5a)
                        .foliageColorOverride(0x0b9c78)
                        .dryFoliageColorOverride(0x0b9c78)
                ).build())
                .mobSpawnSettings(spawner.build()).generationSettings(biome.build())
                .setAttribute(EnvironmentAttributes.FOG_END_DISTANCE, 128f)
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.vector3fFromRGB24(0x90e1d5))
                .setAttribute(EnvironmentAttributes.SKY_FOG_END_DISTANCE, 64f)
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.vector3fFromRGB24(0x90e1d5))
                .setAttribute(EnvironmentAttributes.SUNRISE_SUNSET_COLOR, ARGB.vector4fFromARGB32(0xb6e1af))
                .setAttribute(EnvironmentAttributes.CLOUD_COLOR, ARGB.vector4fFromARGB32(0xaefff3))
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, ARGB.vector3fFromRGB24(0x48d9c1))
                .setAttribute(EnvironmentAttributes.WATER_FOG_END_DISTANCE, 32f)
                .setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(SoundEvents.MUSIC_BIOME_FOREST))
                .build();
    }


    public static void addVerdantDefaults(BiomeGenerationSettings.Builder biome) {
        biome.addCarver(Carvers.CAVE);
        biome.addCarver(Carvers.CAVE_EXTRA_UNDERGROUND);
        biome.addCarver(Carvers.CANYON);
        biome.addFeature(GenerationStep.Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND);
        biome.addFeature(GenerationStep.Decoration.LAKES, MiscOverworldPlacements.SPRING_WATER);
        BiomeDefaultFeatures.addDefaultCrystalFormations(biome);
        BiomeDefaultFeatures.addDefaultMonsterRoom(biome);
        BiomeDefaultFeatures.addDefaultUndergroundVariety(biome);
        BiomeDefaultFeatures.addDefaultOres(biome);
        BiomeDefaultFeatures.addDefaultSoftDisks(biome);
        biome.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, VWPlacedFeatures.VERDANT_HOLLOWS_KEY);
        biome.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, VWPlacedFeatures.UNDERGROUND_VERIXIUM_FLUID_POND_KEY);
        biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, VWPlacedFeatures.VERIXIUM_ORE_LARGE_KEY);
        biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, VWPlacedFeatures.VERIXIUM_ORE_SMALL_KEY);
        biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, VWPlacedFeatures.VERIXIUM_ORE_BURIED_KEY);

        biome.addFeature(GenerationStep.Decoration.LAKES, VWPlacedFeatures.VERIXIUM_FLUID_POND_KEY);
        BiomeDefaultFeatures.addDefaultMushrooms(biome);
        BiomeDefaultFeatures.addJungleVines(biome);

        biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VWPlacedFeatures.VERDANT_GRASS_PATCH_KEY);
        biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VWPlacedFeatures.VERDANT_FERN_PATCH_KEY);
        biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VWPlacedFeatures.VERDANT_TORCHFLOWER_PATCH_KEY);
        biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VWPlacedFeatures.VERDANT_MOSS_PATCH_KEY);
        biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VWPlacedFeatures.VERDANT_MOSS_PATCH_HIGH_KEY);
        biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VWPlacedFeatures.VERDANT_RIVER_SEAGRASS_KEY);
    }
}
