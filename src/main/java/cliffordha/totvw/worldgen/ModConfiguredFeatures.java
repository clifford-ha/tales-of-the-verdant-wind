package cliffordha.totvw.worldgen;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.ModBlocks;

import cliffordha.totvw.registry.blocks.VerdantBlocks;
import cliffordha.totvw.tag.ModBlockTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.SpruceFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.GiantTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;


import java.util.List;

public class ModConfiguredFeatures {
    // ORES
    public static final ResourceKey<ConfiguredFeature<?, ?>> VERIXIUM_ORE_LARGE_CONFIGURED_KEY = ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verixium_ore_large"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> VERIXIUM_ORE_SMALL_CONFIGURED_KEY = ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verixium_ore_small"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> VERIXIUM_ORE_BURIED_CONFIGURED_KEY = ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verixium_ore_buried"));

    // SURFACE STRUCTURES
    public static final ResourceKey<ConfiguredFeature<?, ?>> VERDANT_PILLARS_CONFIGURED_KEY = ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verdant_pillars"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> VERIXIUM_FLUID_POND_CONFIGURED_KEY = ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verixium_fluid_pond"));

    // UNDERGROUND STRUCTURES
    public static final ResourceKey<ConfiguredFeature<?, ?>> VERDANT_HOLLOWS_CONFIGURED_KEY = ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verdant_hollows"));

    // TREES
    public static final ResourceKey<ConfiguredFeature<?, ?>> VERDANT_SPRUCE_BUSH_TREE_CONFIGURED_KEY = ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verdant_spruce_bush_tree"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> VERDANT_SPRUCE_TREE_CONFIGURED_KEY = ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verdant_spruce_tree"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> ANCIENT_VERDANT_SPRUCE_TREE_CONFIGURED_KEY = ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "ancient_verdant_spruce_tree"));

    // VEGETATION
    public static final ResourceKey<ConfiguredFeature<?, ?>> VERDANT_GRASS_PATCH_CONFIGURED_KEY = ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verdant_grass_patch"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> VERDANT_RIVER_SEAGRASS_CONFIGURED_KEY = ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verdant_river_seagrass"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> VERDANT_FERN_PATCH_CONFIGURED_KEY = ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verdant_fern_patch"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> VERDANT_TORCHFLOWER_PATCH_CONFIGURED_KEY = ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verdant_torchflower_patch"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> VERDANT_MOSS_VEGETATION_CONFIGURED_KEY = ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verdant_moss_vegetation"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> VERDANT_MOSS_PATCH_CONFIGURED_KEY = ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verdant_moss_patch"));


    public static void configure(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);

        RuleTest stoneReplaceableRule = new BlockMatchTest(Blocks.STONE);
        RuleTest deepslateReplaceableRule = new BlockMatchTest(Blocks.DEEPSLATE);

        List<OreConfiguration.TargetBlockState> verixiumOreConfig =
                List.of(
                        OreConfiguration.target(deepslateReplaceableRule, ModBlocks.VERIXIUM_DEEPSLATE_ORE.defaultBlockState()),
                        OreConfiguration.target(stoneReplaceableRule, ModBlocks.VERIXIUM_STONE_ORE.defaultBlockState())
                );

        context.register(VERIXIUM_ORE_LARGE_CONFIGURED_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(verixiumOreConfig, 8, 0.4f)));
        context.register(VERIXIUM_ORE_SMALL_CONFIGURED_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(verixiumOreConfig, 4, 0.0f)));
        context.register(VERIXIUM_ORE_BURIED_CONFIGURED_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(verixiumOreConfig, 12, 0.65f)));

        context.register(VERDANT_HOLLOWS_CONFIGURED_KEY, new ConfiguredFeature<>(Feature.GEODE,
                new GeodeConfiguration(
                        new GeodeBlockSettings(
                                BlockStateProvider.simple(Blocks.AIR), //INNER FILLER
                                BlockStateProvider.simple(Blocks.STONE), //INNER MAIN
                                BlockStateProvider.simple(ModBlocks.VERIXIUM_STONE_ORE), //INNER DECOR
                                BlockStateProvider.simple(Blocks.CALCITE), //MID LAYER
                                BlockStateProvider.simple(VerdantBlocks.DEEPSLATE), //OUTER
                                List.of(
                                        Blocks.AIR.defaultBlockState(),
                                        Blocks.AIR.defaultBlockState(),
                                        Blocks.AIR.defaultBlockState(),
                                        Blocks.AIR.defaultBlockState()
                                ),
                                BlockTags.FEATURES_CANNOT_REPLACE,
                                BlockTags.GEODE_INVALID_BLOCKS
                        ),
                        new GeodeLayerSettings(
                                1.7,
                                2.2,
                                3.2,
                                4.2D
                        ),
                        new GeodeCrackSettings(
                                0.0D,
                                1.2D,
                                2
                        ),
                        0.35D,
                        0.083D,
                        true,
                        UniformInt.of(4, 6),
                        UniformInt.of(3, 4),
                        UniformInt.of(1, 2),
                        -8,
                        8,
                        0.01,
                        1
                )
        ));

        context.register(VERDANT_PILLARS_CONFIGURED_KEY, new ConfiguredFeature<>(Feature.SPIKE,
                new SpikeConfiguration(
                        VerdantBlocks.VERDANT_MOSS_BLOCK.defaultBlockState(),
                        BlockPredicate.matchesBlocks(Blocks.GRASS_BLOCK),
                        BlockPredicate.matchesBlocks(List.of(
                                Blocks.AIR,
                                Blocks.SHORT_GRASS,
                                Blocks.FERN,
                                Blocks.SNOW
                        ))
                )));
        context.register(VERDANT_GRASS_PATCH_CONFIGURED_KEY, new ConfiguredFeature<>(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.SHORT_GRASS.defaultBlockState()))));
        context.register(VERDANT_FERN_PATCH_CONFIGURED_KEY, new ConfiguredFeature<>(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.FERN.defaultBlockState()))));
        context.register(VERDANT_TORCHFLOWER_PATCH_CONFIGURED_KEY, new ConfiguredFeature<>(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.TORCHFLOWER.defaultBlockState()))));
        context.register(VERDANT_MOSS_VEGETATION_CONFIGURED_KEY, new ConfiguredFeature<>(Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(new WeightedStateProvider(
                        WeightedList.<BlockState>builder()
                                .add(Blocks.FERN.defaultBlockState(), 7)
                                .add(VerdantBlocks.VERDANT_SPRUCE_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true), 6)
                                .add(Blocks.BUSH.defaultBlockState(), 3)
                                .add(Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(SweetBerryBushBlock.AGE, 3), 1)
                                .build()
                ))));
        context.register(VERDANT_MOSS_PATCH_CONFIGURED_KEY, new ConfiguredFeature<>(Feature.VEGETATION_PATCH,
                new VegetationPatchConfiguration(
                        ModBlockTags.VERDANT_MOSS_REPLACEABLE,
                        BlockStateProvider.simple(VerdantBlocks.VERDANT_MOSS_BLOCK.defaultBlockState()),
                        placedFeatures.getOrThrow(ModPlacedFeatures.VERDANT_MOSS_VEGETATION_KEY),
                        CaveSurface.FLOOR,
                        UniformInt.of(1, 7),
                        0.33F,
                        7,
                        0.33F,
                        UniformInt.of(1, 3),
                        0.33F
                )));
        context.register(VERDANT_RIVER_SEAGRASS_CONFIGURED_KEY, new ConfiguredFeature<>(Feature.SEAGRASS,
                new ProbabilityFeatureConfiguration(0.5f)));

        context.register(VERIXIUM_FLUID_POND_CONFIGURED_KEY, new ConfiguredFeature<>(Feature.LAKE,
                new LakeFeature.Configuration(
                        BlockStateProvider.simple(ModBlocks.VERIXIUM_FLUID),
                        BlockStateProvider.simple(Blocks.DEEPSLATE)

        )));
        context.register(VERDANT_SPRUCE_BUSH_TREE_CONFIGURED_KEY, new ConfiguredFeature<>(Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(VerdantBlocks.VERDANT_SPRUCE_LOG),
                        new StraightTrunkPlacer(2, 0, 0),
                        BlockStateProvider.simple(VerdantBlocks.VERDANT_SPRUCE_LEAVES.defaultBlockState()),
                        new SpruceFoliagePlacer(
                                ConstantInt.of(3),
                                ConstantInt.of(0),
                                ConstantInt.of(2)
                        ),
                        new TwoLayersFeatureSize(0, 0, 0)
                ).build()
        ));

        context.register(VERDANT_SPRUCE_TREE_CONFIGURED_KEY, new ConfiguredFeature<>(Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(VerdantBlocks.VERDANT_SPRUCE_LOG),
                        new StraightTrunkPlacer(6, 3, 3),
                        BlockStateProvider.simple(VerdantBlocks.VERDANT_SPRUCE_LEAVES),
                        new SpruceFoliagePlacer(
                                UniformInt.of(4, 7),
                                UniformInt.of(0, 1),
                                UniformInt.of(0, 3)
                        ),
                        new TwoLayersFeatureSize(0, 0, 0)
                ).build()
        ));

        context.register(ANCIENT_VERDANT_SPRUCE_TREE_CONFIGURED_KEY, new ConfiguredFeature<>(Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(VerdantBlocks.VERDANT_SPRUCE_LOG),
                        new GiantTrunkPlacer(9, 5, 3),
                        BlockStateProvider.simple(VerdantBlocks.VERDANT_SPRUCE_LEAVES),
                        new SpruceFoliagePlacer(
                                UniformInt.of(9, 11),
                                ConstantInt.of(0),
                                UniformInt.of(1, 9)
                        ),
                        new TwoLayersFeatureSize(1, 1, 2)
                ).build()
        ));

    }
}
