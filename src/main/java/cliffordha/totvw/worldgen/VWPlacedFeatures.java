package cliffordha.totvw.worldgen;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.tag.VWBlockTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.TrapezoidInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

public class VWPlacedFeatures {
    // ORES
    public static final ResourceKey<PlacedFeature> VERIXIUM_ORE_LARGE_KEY = create("verixium_ore_large");
    public static final ResourceKey<PlacedFeature> VERIXIUM_ORE_SMALL_KEY = create("verixium_ore_small");
    public static final ResourceKey<PlacedFeature> VERIXIUM_ORE_BURIED_KEY = create("verixium_ore_buried");

    // SURFACE STRUCTURES
    public static final ResourceKey<PlacedFeature> VERDANT_PILLARS_KEY = create("verdant_pillars");
    public static final ResourceKey<PlacedFeature> VERIXIUM_FLUID_POND_KEY = create("verixium_fluid_pond");
    public static final ResourceKey<PlacedFeature> UNDERGROUND_VERIXIUM_FLUID_POND_KEY = create("underground_verixium_fluid_pond");

    // UNDERGROUND STRUCTURES
    public static final ResourceKey<PlacedFeature> VERDANT_HOLLOWS_KEY = create("verdant_hollows");

    //TREES
    public static final ResourceKey<PlacedFeature> VERDANT_SPRUCE_BUSH_TREE_KEY = create("verdant_spruce_bush_tree");
    public static final ResourceKey<PlacedFeature> VERDANT_SPRUCE_TREE_LOWER_KEY = create("verdant_spruce_tree_lower");
    public static final ResourceKey<PlacedFeature> VERDANT_SPRUCE_TREE_HIGHER_KEY = create("verdant_spruce_tree_higher");
    public static final ResourceKey<PlacedFeature> ANCIENT_VERDANT_SPRUCE_TREE_KEY = create("ancient_verdant_spruce_tree");

    public static final ResourceKey<PlacedFeature> VILLAGE_VERDANT_SPRUCE_TREE_KEY = create("village_verdant_spruce_tree");

    // VEGETATION
    public static final ResourceKey<PlacedFeature> VERDANT_MOSS_VEGETATION_KEY = create("verdant_moss_vegetation");
    public static final ResourceKey<PlacedFeature> VERDANT_MOSS_PATCH_KEY = create("verdant_moss_patch");
    public static final ResourceKey<PlacedFeature> VERDANT_MOSS_PATCH_HIGH_KEY = create("verdant_moss_patch_high");
    public static final ResourceKey<PlacedFeature> VERDANT_GRASS_PATCH_KEY = create("verdant_grass_patch");
    public static final ResourceKey<PlacedFeature> VERDANT_RIVER_SEAGRASS_KEY = create("verdant_river_seagrass");
    public static final ResourceKey<PlacedFeature> VERDANT_FERN_PATCH_KEY = create("verdant_fern_patch");

    public static final ResourceKey<PlacedFeature> VERDANT_TORCHFLOWER_PATCH_KEY = create("verdant_torchflower_patch");
    public static final ResourceKey<PlacedFeature> VERDANT_WILD_SNIFFER_EGG_KEY = create("verdant_wild_sniffer_egg");


    //helper
    private static ResourceKey<PlacedFeature> create(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, fromNamespaceAndPath(TOTVW.MOD_ID, name));
    }


    public static void configure(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        context.register(VERIXIUM_ORE_LARGE_KEY, new PlacedFeature(configuredFeatures.getOrThrow(VWConfiguredFeatures.VERIXIUM_ORE_LARGE_CONFIGURED_KEY),
                List.of(
                        RarityFilter.onAverageOnceEvery(33),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(64)),
                        BiomeFilter.biome()
                )
        ));
        context.register(VERIXIUM_ORE_SMALL_KEY, new PlacedFeature(configuredFeatures.getOrThrow(VWConfiguredFeatures.VERIXIUM_ORE_SMALL_CONFIGURED_KEY),
                List.of(
                        CountPlacement.of(2),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(256)),
                        BiomeFilter.biome()
                )
        ));
        context.register(VERIXIUM_ORE_BURIED_KEY, new PlacedFeature(configuredFeatures.getOrThrow(VWConfiguredFeatures.VERIXIUM_ORE_BURIED_CONFIGURED_KEY),
                List.of(
                        CountPlacement.of(2),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(32)),
                        BiomeFilter.biome()
                )
        ));
        context.register(VERDANT_PILLARS_KEY, new PlacedFeature(configuredFeatures.getOrThrow(VWConfiguredFeatures.VERDANT_PILLARS_CONFIGURED_KEY),
                List.of(
                        RarityFilter.onAverageOnceEvery(4),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(Blocks.AIR)),
                        BiomeFilter.biome()
                ))
        );
        context.register(VERDANT_SPRUCE_BUSH_TREE_KEY, new PlacedFeature(configuredFeatures.getOrThrow(VWConfiguredFeatures.VERDANT_SPRUCE_BUSH_TREE_CONFIGURED_KEY),
                List.of(
                        InSquarePlacement.spread(),
                        CountPlacement.of(UniformInt.of(0, 2)),
                        SurfaceWaterDepthFilter.forMaxDepth(0),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID,
                        BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(Blocks.AIR)),
                        BiomeFilter.biome()
                ))
        );
        context.register(VILLAGE_VERDANT_SPRUCE_TREE_KEY, new PlacedFeature(configuredFeatures.getOrThrow(VWConfiguredFeatures.VERDANT_SPRUCE_TREE_CONFIGURED_KEY),
                List.of(
                        InSquarePlacement.spread(),
                        CountPlacement.of(UniformInt.of(0, 2)),
                        SurfaceWaterDepthFilter.forMaxDepth(0),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID,
                        BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(Blocks.SHORT_GRASS, Blocks.FERN))
                ))
        );
        context.register(VERDANT_SPRUCE_TREE_LOWER_KEY, new PlacedFeature(configuredFeatures.getOrThrow(VWConfiguredFeatures.VERDANT_SPRUCE_TREE_CONFIGURED_KEY),
                List.of(
                        CountPlacement.of(UniformInt.of(0, 9)),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(84)),
                        SurfaceWaterDepthFilter.forMaxDepth(0),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID,
                        BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(Blocks.AIR)),
                        BiomeFilter.biome()
                ))
        );
        context.register(VERDANT_SPRUCE_TREE_HIGHER_KEY, new PlacedFeature(configuredFeatures.getOrThrow(VWConfiguredFeatures.VERDANT_SPRUCE_TREE_CONFIGURED_KEY),
                List.of(
                        CountPlacement.of(UniformInt.of(0, 5)),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(90), VerticalAnchor.absolute(256)),
                        SurfaceWaterDepthFilter.forMaxDepth(0),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID,
                        BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(Blocks.AIR)),
                        BiomeFilter.biome()
                ))
        );
        context.register(ANCIENT_VERDANT_SPRUCE_TREE_KEY, new PlacedFeature(configuredFeatures.getOrThrow(VWConfiguredFeatures.ANCIENT_VERDANT_SPRUCE_TREE_CONFIGURED_KEY),
                List.of(
                        CountPlacement.of(UniformInt.of(4, 12)),
                        InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(0),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID,
                        BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(List.of(Blocks.AIR))),
                        BiomeFilter.biome()
                ))
        );
        context.register(VERDANT_HOLLOWS_KEY, new PlacedFeature(configuredFeatures.getOrThrow(VWConfiguredFeatures.VERDANT_HOLLOWS_CONFIGURED_KEY),
                List.of(
                        RarityFilter.onAverageOnceEvery(48),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(48)),
                        BlockPredicateFilter.forPredicate(BlockPredicate.anyOf(
                                BlockPredicate.matchesTag(BlockTags.DEEPSLATE_ORE_REPLACEABLES),
                                BlockPredicate.matchesTag(BlockTags.STONE_ORE_REPLACEABLES))),
                        BiomeFilter.biome()
                ))
        );
        context.register(VERDANT_MOSS_VEGETATION_KEY, new PlacedFeature(configuredFeatures.getOrThrow(VWConfiguredFeatures.VERDANT_MOSS_VEGETATION_CONFIGURED_KEY),
                List.of(
                        InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(0),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID,
                        CountPlacement.of(4),
                        BlockPredicateFilter.forPredicate(BlockPredicate.anyOf(BlockPredicate.matchesBlocks(Blocks.AIR), BlockPredicate.matchesTag(VWBlockTags.VERDANT_MOSS_REPLACEABLE)))
                ))
        );
        context.register(VERDANT_MOSS_PATCH_KEY, new PlacedFeature(configuredFeatures.getOrThrow(VWConfiguredFeatures.VERDANT_MOSS_PATCH_CONFIGURED_KEY),
                List.of(
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(74)),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        CountPlacement.of(UniformInt.of(1, 2)),
                        RandomOffsetPlacement.vertical(ConstantInt.of(1)),
                        BiomeFilter.biome()
                ))
        );
        context.register(VERDANT_MOSS_PATCH_HIGH_KEY, new PlacedFeature(configuredFeatures.getOrThrow(VWConfiguredFeatures.VERDANT_MOSS_PATCH_CONFIGURED_KEY),
                List.of(
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(84), VerticalAnchor.absolute(256)),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        CountPlacement.of(UniformInt.of(1, 4)),
                        RandomOffsetPlacement.vertical(ConstantInt.of(1)),
                        BiomeFilter.biome()
                ))
        );
        context.register(VERDANT_GRASS_PATCH_KEY, new PlacedFeature(configuredFeatures.getOrThrow(VWConfiguredFeatures.VERDANT_GRASS_PATCH_CONFIGURED_KEY),
                List.of(
                        NoiseThresholdCountPlacement.of(-0.8, 5, 10),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        CountPlacement.of(32),
                        RandomOffsetPlacement.of(
                                TrapezoidInt.of(-7, 7, 0),
                                TrapezoidInt.of(-3, 3, 0)
                        ),
                        BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(Blocks.AIR)),
                        BiomeFilter.biome()
                ))
        );
        context.register(VERDANT_FERN_PATCH_KEY, new PlacedFeature(configuredFeatures.getOrThrow(VWConfiguredFeatures.VERDANT_FERN_PATCH_CONFIGURED_KEY),
                List.of(
                        NoiseThresholdCountPlacement.of(-0.8, 5, 10),
                        InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(0),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        CountPlacement.of(8),
                        RandomOffsetPlacement.of(
                                TrapezoidInt.of(-7, 7, 0),
                                TrapezoidInt.of(-3, 3, 0)
                        ),
                        BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(Blocks.AIR)),
                        BiomeFilter.biome()
                ))
        );
        context.register(VERDANT_TORCHFLOWER_PATCH_KEY, new PlacedFeature(configuredFeatures.getOrThrow(VWConfiguredFeatures.VERDANT_TORCHFLOWER_PATCH_CONFIGURED_KEY),
                List.of(
                        NoiseThresholdCountPlacement.of(-0.8, 5, 10),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        CountPlacement.of(UniformInt.of(0, 1)),
                        RandomOffsetPlacement.of(
                                TrapezoidInt.of(-7, 7, 0),
                                TrapezoidInt.of(-3, 3, 0)
                        ),
                        BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(Blocks.AIR)),
                        BiomeFilter.biome()
                ))
        );
        context.register(VERDANT_RIVER_SEAGRASS_KEY, new PlacedFeature(configuredFeatures.getOrThrow(VWConfiguredFeatures.VERDANT_RIVER_SEAGRASS_CONFIGURED_KEY),
                List.of(
                        CountPlacement.of(48),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                        BiomeFilter.biome()
                ))
        );
        context.register(VERIXIUM_FLUID_POND_KEY, new PlacedFeature(configuredFeatures.getOrThrow(VWConfiguredFeatures.VERIXIUM_FLUID_POND_CONFIGURED_KEY),
                List.of(
                        RarityFilter.onAverageOnceEvery(200),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()
                ))
        );
        context.register(UNDERGROUND_VERIXIUM_FLUID_POND_KEY, new PlacedFeature(configuredFeatures.getOrThrow(VWConfiguredFeatures.VERIXIUM_FLUID_POND_CONFIGURED_KEY),
                List.of(
                        CountPlacement.of(UniformInt.of(0, 1)),
                        InSquarePlacement.spread(),
                        PlacementUtils.FULL_RANGE,
                        BiomeFilter.biome()
                ))
        );
        /*
        context.register(VERDANT_WILD_SNIFFER_EGG_KEY, new PlacedFeature(configuredFeatures.getOrThrow(VWConfiguredFeatures.VERDANT_SNIFFER_EGG_CONFIGURED_KEY),
                List.of(
                        RarityFilter.onAverageOnceEvery(23),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID,
                        BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(Blocks.SWEET_BERRY_BUSH)),
                        BiomeFilter.biome()
                ))
        );*/
    }

}
