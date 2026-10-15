package cliffordha.totvw.worldgen;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.VWBlocks;
import cliffordha.totvw.tag.VWBlockTags;
import cliffordha.totvw.world.tree.DefaultRootPlacer;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
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
import net.minecraft.world.level.levelgen.feature.*;
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

public class VWConfiguredFeatures {
    // ORES
    public static final ResourceKey<Feature> VERIXIUM_ORE_LARGE_CONFIGURED_KEY = create("verixium_ore_large");
    public static final ResourceKey<Feature> VERIXIUM_ORE_SMALL_CONFIGURED_KEY = create("verixium_ore_small");
    public static final ResourceKey<Feature> VERIXIUM_ORE_BURIED_CONFIGURED_KEY = create("verixium_ore_buried");

    // SURFACE STRUCTURES
    public static final ResourceKey<Feature> VERDANT_PILLARS_CONFIGURED_KEY = create("verdant_pillars");
    public static final ResourceKey<Feature> VERIXIUM_FLUID_POND_CONFIGURED_KEY = create("verixium_fluid_pond");

    // UNDERGROUND STRUCTURES
    public static final ResourceKey<Feature> VERDANT_HOLLOWS_CONFIGURED_KEY = create("verdant_hollows");

    // TREES
    public static final ResourceKey<Feature> VERDANT_SPRUCE_BUSH_TREE_CONFIGURED_KEY = create("verdant_spruce_bush_tree");
    public static final ResourceKey<Feature> VERDANT_SPRUCE_TREE_CONFIGURED_KEY = create("verdant_spruce_tree");
    public static final ResourceKey<Feature> ANCIENT_VERDANT_SPRUCE_TREE_CONFIGURED_KEY = create("ancient_verdant_spruce_tree");

    // VEGETATION
    public static final ResourceKey<Feature> VERDANT_GRASS_PATCH_CONFIGURED_KEY = create("verdant_grass_patch");
    public static final ResourceKey<Feature> VERDANT_RIVER_SEAGRASS_CONFIGURED_KEY = create("verdant_river_seagrass");
    public static final ResourceKey<Feature> VERDANT_FERN_PATCH_CONFIGURED_KEY = create("verdant_fern_patch");
    public static final ResourceKey<Feature> VERDANT_TORCHFLOWER_PATCH_CONFIGURED_KEY = create("verdant_torchflower_patch");
    public static final ResourceKey<Feature> VERDANT_MOSS_VEGETATION_CONFIGURED_KEY = create("verdant_moss_vegetation");
    public static final ResourceKey<Feature> VERDANT_MOSS_PATCH_CONFIGURED_KEY = create("verdant_moss_patch");

    public static final ResourceKey<Feature> VERDANT_FARMLANDS_PATCH = create("verdant_farmlands_patch");
    public static final ResourceKey<Feature> VERDANT_FARMLANDS_DISK = create("verdant_farmlands_disk");

    // helper
    private static ResourceKey<Feature> create(String name) {
        return ResourceKey.create(Registries.FEATURE, TOTVW.registerID(name));
    }


    public static void configure(BootstrapContext<Feature> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);

        RuleTest stoneReplaceableRule = new BlockMatchTest(Blocks.STONE);
        RuleTest deepslateReplaceableRule = new BlockMatchTest(Blocks.DEEPSLATE);

        List<BlockReplacement> oreVerixiumTargetList = List.of(BlockReplacement.replace(stoneReplaceableRule, Blocks.DIAMOND_ORE.defaultBlockState()), BlockReplacement.replace(deepslateReplaceableRule, Blocks.DEEPSLATE_DIAMOND_ORE.defaultBlockState()));

        register(context, VERIXIUM_ORE_LARGE_CONFIGURED_KEY, new OreFeature(oreVerixiumTargetList, 8, 0.4f));
        register(context, VERIXIUM_ORE_SMALL_CONFIGURED_KEY, new OreFeature(oreVerixiumTargetList, 4, 0.0f));
        register(context, VERIXIUM_ORE_BURIED_CONFIGURED_KEY, new OreFeature(oreVerixiumTargetList, 12, 0.65f));

        register(context, VERDANT_HOLLOWS_CONFIGURED_KEY,
                new GeodeFeature(
                        new GeodeBlockSettings(
                                BlockStateProvider.holderOf(Blocks.AIR), //INNER FILLER
                                BlockStateProvider.holderOf(Blocks.STONE), //INNER MAIN
                                BlockStateProvider.holderOf(VWBlocks.VERIXIUM_STONE_ORE), //INNER DECOR
                                BlockStateProvider.holderOf(Blocks.CALCITE), //MID LAYER
                                BlockStateProvider.holderOf(Blocks.DEEPSLATE), //OUTER
                                List.of(
                                        Blocks.AIR.defaultBlockState(),
                                        Blocks.AIR.defaultBlockState(),
                                        Blocks.AIR.defaultBlockState(),
                                        Blocks.AIR.defaultBlockState()
                                ),
                                context.lookup(Registries.BLOCK).getOrThrow(BlockTags.FEATURES_CANNOT_REPLACE),
                                context.lookup(Registries.BLOCK).getOrThrow(BlockTags.GEODE_INVALID_BLOCKS)
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
        );

        register(context, VERDANT_PILLARS_CONFIGURED_KEY,
                new SpikeFeature(
                        VWBlocks.VERDANT_MOSS_BLOCK.defaultBlockState(),
                        BlockPredicate.matchesBlocks(Blocks.GRASS_BLOCK),
                        BlockPredicate.matchesBlocks(
                                Blocks.AIR,
                                Blocks.SHORT_GRASS,
                                Blocks.FERN,
                                Blocks.SNOW
                        )
                ));
        register(context, VERDANT_GRASS_PATCH_CONFIGURED_KEY, new SimpleBlockFeature(BlockStateProvider.holderOf(Blocks.SHORT_GRASS.defaultBlockState())));
        register(context, VERDANT_FERN_PATCH_CONFIGURED_KEY, new SimpleBlockFeature(BlockStateProvider.holderOf(Blocks.FERN.defaultBlockState())));
        register(context, VERDANT_TORCHFLOWER_PATCH_CONFIGURED_KEY, new SimpleBlockFeature(BlockStateProvider.holderOf(Blocks.TORCHFLOWER.defaultBlockState())));
        register(context, VERDANT_MOSS_VEGETATION_CONFIGURED_KEY, new SimpleBlockFeature(
                new WeightedStateProvider(
                        WeightedList.<BlockState>builder()
                                .add(Blocks.FERN.defaultBlockState(), 7)
                                .add(VWBlocks.VERDANT_SPRUCE_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true), 6)
                                .add(Blocks.BUSH.defaultBlockState(), 3)
                                .add(Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(SweetBerryBushBlock.AGE, 3), 1)
                                .build())
                )
        );

        HolderSet<Block> mossReplaceable = context.lookup(Registries.BLOCK).getOrThrow(VWBlockTags.VERDANT_MOSS_REPLACEABLE);
        register(context, VERDANT_MOSS_PATCH_CONFIGURED_KEY,
                new VegetationPatchFeature(
                        mossReplaceable,
                        BlockStateProvider.holderOf(VWBlocks.VERDANT_MOSS_BLOCK),
                        placedFeatures.getOrThrow(VWPlacedFeatures.VERDANT_MOSS_VEGETATION_KEY),
                        CaveSurface.FLOOR,
                        UniformInt.of(1, 7),
                        0.33F,
                        7,
                        0.33F,
                        UniformInt.of(1, 3),
                        0.33F
                )
        );

        context.register(VERIXIUM_FLUID_POND_CONFIGURED_KEY, new LakeFeature(
                BlockStateProvider.holderOf(VWBlocks.VERIXIUM_FLUID),
                BlockStateProvider.holderOf(Blocks.DEEPSLATE),
                BlockPredicate.alwaysTrue(),
                BlockPredicate.matchesTag(VWBlockTags.VERDANT_MOSS_REPLACEABLE),
                BlockPredicate.matchesTag(VWBlockTags.VERDANT_MOSS_REPLACEABLE)
        ));

        register(context, VERDANT_SPRUCE_BUSH_TREE_CONFIGURED_KEY, new TreeFeature(
                BlockStateProvider.holderOf(VWBlocks.VERDANT_SPRUCE_LOG),
                new StraightTrunkPlacer(2, 0, 0),

                BlockStateProvider.holderOf(VWBlocks.VERDANT_SPRUCE_LEAVES.defaultBlockState()),
                new SpruceFoliagePlacer(
                        UniformInt.of(3, 4),
                        ConstantInt.of(0),
                        UniformInt.of(2, 3)
                ),
                DefaultRootPlacer.addDefault(),
                new TwoLayersFeatureSize(0,0,0),
                List.of(),
                false,
                BlockStateProvider.holderOf(VWBlocks.VERDANT_SPRUCE_LOG)

        ));

        register(context, VERDANT_SPRUCE_TREE_CONFIGURED_KEY, new TreeFeature(
                BlockStateProvider.holderOf(VWBlocks.VERDANT_SPRUCE_LOG),
                new StraightTrunkPlacer(7, 3, 3),
                BlockStateProvider.holderOf(VWBlocks.VERDANT_SPRUCE_LEAVES.defaultBlockState()),
                new SpruceFoliagePlacer(
                        UniformInt.of(4, 7),
                        UniformInt.of(0, 1),
                        UniformInt.of(2, 3)
                ),
                DefaultRootPlacer.addDefault(),
                new TwoLayersFeatureSize(1,1,2),
                List.of(),
                false,
                BlockStateProvider.holderOf(VWBlocks.VERDANT_SPRUCE_LOG)
        ));

        register(context, ANCIENT_VERDANT_SPRUCE_TREE_CONFIGURED_KEY, new TreeFeature(
                BlockStateProvider.holderOf(VWBlocks.VERDANT_SPRUCE_LOG),
                new GiantTrunkPlacer(9, 5, 3),
                BlockStateProvider.holderOf(VWBlocks.VERDANT_SPRUCE_LEAVES.defaultBlockState()),
                new SpruceFoliagePlacer(
                        UniformInt.of(9, 11),
                        ConstantInt.of(0),
                        UniformInt.of(2, 9)
                ),
                DefaultRootPlacer.addDefault(),
                new TwoLayersFeatureSize(1,1,2),
                List.of(),
                false,
                BlockStateProvider.holderOf(VWBlocks.VERDANT_SPRUCE_LOG)
        ));

        register(context, VERDANT_RIVER_SEAGRASS_CONFIGURED_KEY, new SimpleBlockFeature(
                BlockStateProvider.holderOf(Blocks.SEAGRASS.defaultBlockState())
        ));

        register(context, VERDANT_FARMLANDS_DISK, new DiskFeature(
                BlockStateProvider.holderOf(VWBlocks.FARMLAND_PLACER.defaultBlockState()),
                BlockPredicate.matchesTag(VWBlockTags.VERDANT_MOSS_REPLACEABLE),
                UniformInt.of(3, 7),
                2
        ));

        register(context, VERDANT_FARMLANDS_PATCH, new VegetationPatchFeature(
                mossReplaceable,
                BlockStateProvider.holderOf(VWBlocks.FARMLAND_PLACER.defaultBlockState()),
                placedFeatures.getOrThrow(VWPlacedFeatures.VERDANT_MOSS_VEGETATION_KEY),
                CaveSurface.FLOOR,
                UniformInt.of(1, 2),
                0F,
                2,
                0.3F,
                UniformInt.of(1, 3),
                0.33F
        ));
    }

    private static <FC extends Feature> void register(BootstrapContext<Feature> context, ResourceKey<Feature> key, FC configuration) {
        context.register(key, configuration);
    }
}