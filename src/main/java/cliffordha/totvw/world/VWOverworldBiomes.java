package cliffordha.totvw.world;

import cliffordha.totvw.worldgen.VWPlacedFeatures;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.attribute.*;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;


public class VWOverworldBiomes {
        public static Biome verdantMountains(HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
            MobSpawnSettings.Builder spawner = new MobSpawnSettings.Builder();
            BiomeGenerationSettings.Builder biome = new BiomeGenerationSettings.Builder(placedFeatureGetter, carverGetter);

            spawner.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityTypes.WOLF, 1, 2));
            spawner.addSpawn(MobCategory.CREATURE, 1, new MobSpawnSettings.SpawnerData(EntityTypes.SNIFFER, 1, 1));
            spawner.addSpawn(MobCategory.CREATURE, 4, new MobSpawnSettings.SpawnerData(EntityTypes.CHICKEN, 2, 4));
            spawner.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityTypes.GOAT, 4, 4));

            spawner.addSpawn(MobCategory.MONSTER, 10, new MobSpawnSettings.SpawnerData(EntityTypes.ZOMBIE, 2, 2));
            spawner.addSpawn(MobCategory.MONSTER, 30, new MobSpawnSettings.SpawnerData(EntityTypes.SKELETON, 2, 2));

            addVerdantDefaults(biome);
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
                    .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, 0x48d9c1)
                    .setAttribute(EnvironmentAttributes.WATER_FOG_END_DISTANCE, 128f)
                    .setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(SoundEvents.MUSIC_BIOME_JAGGED_PEAKS))
                    .build();
        }

    public static Biome verdantForest(HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        MobSpawnSettings.Builder spawner = new MobSpawnSettings.Builder();
        BiomeGenerationSettings.Builder biome = new BiomeGenerationSettings.Builder(placedFeatureGetter, carverGetter);

        spawner.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityTypes.WOLF, 3, 4));
        spawner.addSpawn(MobCategory.CREATURE, 1, new MobSpawnSettings.SpawnerData(EntityTypes.SNIFFER, 1, 1));
        spawner.addSpawn(MobCategory.CREATURE, 8, new MobSpawnSettings.SpawnerData(EntityTypes.CHICKEN, 2, 4));
        spawner.addSpawn(MobCategory.CREATURE, 6, new MobSpawnSettings.SpawnerData(EntityTypes.GOAT, 2, 2));

        spawner.addSpawn(MobCategory.MONSTER, 30, new MobSpawnSettings.SpawnerData(EntityTypes.ZOMBIE, 2, 2));
        spawner.addSpawn(MobCategory.MONSTER, 10, new MobSpawnSettings.SpawnerData(EntityTypes.SKELETON, 2, 2));

        addVerdantDefaults(biome);
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
                .setAttribute(EnvironmentAttributes.FOG_COLOR, 0x90e1d5)
                .setAttribute(EnvironmentAttributes.SKY_FOG_END_DISTANCE, 64f)
                .setAttribute(EnvironmentAttributes.SKY_COLOR, 0x90e1d5)
                .setAttribute(EnvironmentAttributes.SUNRISE_SUNSET_COLOR, 0xb6e1af)
                .setAttribute(EnvironmentAttributes.CLOUD_COLOR, 0xaefff3)
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, 0x48d9c1)
                .setAttribute(EnvironmentAttributes.WATER_FOG_END_DISTANCE, 32f)
                .setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(SoundEvents.MUSIC_BIOME_FOREST))
                .build();
    }


    private static void addVerdantDefaults(BiomeGenerationSettings.Builder biome) {
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
