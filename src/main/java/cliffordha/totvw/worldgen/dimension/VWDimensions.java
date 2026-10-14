package cliffordha.totvw.worldgen.dimension;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.world.VWBiomes;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TimelineTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.attribute.*;
import net.minecraft.world.clock.WorldClocks;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.MoonPhase;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

import java.util.List;
import java.util.Optional;

public class VWDimensions {
    public static final ResourceKey<Level> OVERWORLD = ResourceKey.create(Registries.DIMENSION, Identifier.withDefaultNamespace("overworld"));
    public static final ResourceKey<Level> NETHER = ResourceKey.create(Registries.DIMENSION, Identifier.withDefaultNamespace("the_nether"));
    public static final ResourceKey<Level> END = ResourceKey.create(Registries.DIMENSION, Identifier.withDefaultNamespace("the_end"));

    public static final ResourceKey<LevelStem> NOLAYAN_KEY = ResourceKey.create(Registries.LEVEL_STEM, TOTVW.registerID( "nolayan"));
    public static final ResourceKey<Level> NOLAYAN_LEVEL_KEY = ResourceKey.create(Registries.DIMENSION, TOTVW.registerID( "nolayan"));
    public static final ResourceKey<DimensionType> NOLAYAN_DIMENSION_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, TOTVW.registerID("nolayan_dimension_type"));

    public static void bootstrapType(BootstrapContext<DimensionType> context) {
        var timelines = context.lookup(Registries.TIMELINE);
        var clocks = context.lookup(Registries.WORLD_CLOCK);
        var blocks = context.lookup(Registries.BLOCK);

        var mob = context.lookup(Registries.ENTITY_TYPE);

        context.register(NOLAYAN_DIMENSION_TYPE, new DimensionType(
                false, // hasFixedTime
                true, // hasSkylight
                false, // hasCeiling
                false, // hasEnderDragonFight
                1.0, // coordinateScale
                -64, // minY
                384, // height
                384, // logicalHeight
                blocks.getOrThrow(BlockTags.INFINIBURN_OVERWORLD), // infiniburn
                1.0f, // ambientLight
                new DimensionType.MonsterSettings(UniformInt.of(1, 9), 9),
                DimensionType.Skybox.NONE,
                CardinalLighting.Type.DEFAULT,
                EnvironmentAttributeMap.builder()
                        .set(EnvironmentAttributes.FOG_COLOR, ARGB.vector3fFromRGB24(0xBEFDFF))
                        .set(EnvironmentAttributes.SKY_COLOR, ARGB.vector3fFromRGB24(OverworldBiomes.calculateSkyColor(0.35f)))
                        .set(EnvironmentAttributes.CLOUD_COLOR, ARGB.vector4fFromARGB32(0xBEFDFF))
                        .set(EnvironmentAttributes.SUNRISE_SUNSET_COLOR, ARGB.vector4fFromARGB32( 0x17484D))
                        .set(EnvironmentAttributes.SNOW_GOLEM_MELTS, false)
                        .set(EnvironmentAttributes.CLOUD_HEIGHT, 256F)
                        .set(EnvironmentAttributes.BACKGROUND_MUSIC, BackgroundMusic.OVERWORLD)
                        .set(EnvironmentAttributes.BED_RULE, BedRule.CAN_SLEEP_WHEN_DARK)
                        .set(EnvironmentAttributes.RESPAWN_ANCHOR_WORKS, false)
                        .set(EnvironmentAttributes.NETHER_PORTAL_SPAWNS_PIGLINS, true)
                        .set(EnvironmentAttributes.AMBIENT_SOUNDS, AmbientSounds.LEGACY_CAVE_SETTINGS)
                        .set(EnvironmentAttributes.MOON_PHASE, MoonPhase.FULL_MOON)
                        .set(EnvironmentAttributes.FOG_START_DISTANCE, 0F)
                        .set(EnvironmentAttributes.CREAKING_ACTIVE, false)
                        .set(EnvironmentAttributes.CREATURE_WORLD_GEN_SPAWN_PROBABILITY, 0.2f)
                        .set(EnvironmentAttributes.INCREASED_FIRE_BURNOUT, true)
                        .build(),
                timelines.getOrThrow(TimelineTags.IN_OVERWORLD),
                Optional.of(clocks.getOrThrow(WorldClocks.OVERWORLD))));
    }

    public static void bootstrapStem(BootstrapContext<LevelStem> context) {
        HolderGetter<Biome> biomeRegistry = context.lookup(Registries.BIOME);
        HolderGetter<DimensionType> dimTypes = context.lookup(Registries.DIMENSION_TYPE);
        HolderGetter<NoiseGeneratorSettings> noiseGenSettings = context.lookup(Registries.NOISE_SETTINGS);

        NoiseBasedChunkGenerator verdantForest = new NoiseBasedChunkGenerator(
                new FixedBiomeSource(biomeRegistry.getOrThrow(VWBiomes.VERDANT_MOUNTAINS)),
                noiseGenSettings.getOrThrow(NoiseGeneratorSettings.AMPLIFIED));

        NoiseBasedChunkGenerator noiseBasedChunkGenerator = new NoiseBasedChunkGenerator(
                MultiNoiseBiomeSource.createFromList(
                        new Climate.ParameterList<>(List.of(
                                Pair.of(Climate.parameters(0.4F, 0.1F, 0.1F, 0.7F, 0.0F, 0.43F, 0.0F), biomeRegistry.getOrThrow(VWBiomes.VERDANT_FOREST)),
                                Pair.of(Climate.parameters(0.35F, 0.0F, 0.1F, 0.2F, 0.0F, 1.3F, 0.0F), biomeRegistry.getOrThrow(VWBiomes.VERDANT_MOUNTAINS))
                        ))),
                noiseGenSettings.getOrThrow(NoiseGeneratorSettings.OVERWORLD));

        LevelStem verdantMountainsStem = new LevelStem(dimTypes.getOrThrow(VWDimensions.NOLAYAN_DIMENSION_TYPE), noiseBasedChunkGenerator);

        context.register(NOLAYAN_KEY, verdantMountainsStem);
    }
}
