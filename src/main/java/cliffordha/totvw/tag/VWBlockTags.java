package cliffordha.totvw.tag;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.VWBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

import static cliffordha.totvw.tag.VWTagHelpers.*;

public class VWBlockTags extends FabricTagsProvider.BlockTagsProvider {
    public VWBlockTags(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider wrapperLookup) {
        getOrCreateRawBuilder(VERDANT_BLOCKS)
                .add(block(VWBlocks.VERIXIUM_STONE_ORE))
                .add(block(VWBlocks.VERIXIUM_DEEPSLATE_ORE))
                .add(block(VWBlocks.VERIXIUM_POWDER_BLOCK))
                .add(block(VWBlocks.VERDANT_MOSS_BLOCK))
                .add(block(VWBlocks.VERDANT_SPRUCE_LEAVES))
                .add(block(VWBlocks.VERDANT_SPRUCE_SAPLING))
                .add(block(VWBlocks.VERDANT_SPRUCE_PLANKS))
                .add(block(VWBlocks.VERDANT_SPRUCE_LOG))
                .add(block(VWBlocks.VERDANT_SPRUCE_WOOD))
                .add(block(VWBlocks.STRIPPED_VERDANT_SPRUCE_LOG))
                .add(block(VWBlocks.STRIPPED_VERDANT_SPRUCE_WOOD))
                .add(block(VWBlocks.VERDANT_SPRUCE_SLAB))
                .add(block(VWBlocks.VERDANT_SPRUCE_STAIRS))
                .add(block(VWBlocks.VERDANT_SPRUCE_FENCE))
                .add(block(VWBlocks.VERDANT_SPRUCE_FENCE_GATE))
                .add(block(VWBlocks.VERDANT_SPRUCE_SIGN))
                .add(block(VWBlocks.VERDANT_SPRUCE_WALL_SIGN))
                .add(block(VWBlocks.VERDANT_SPRUCE_HANGING_SIGN))
                .add(block(VWBlocks.VERDANT_SPRUCE_WALL_HANGING_SIGN))
                .add(block(VWBlocks.VERDANT_SPRUCE_SHELF))
                .add(block(VWBlocks.VERDANT_SPRUCE_BUTTON))
                .add(block(VWBlocks.VERDANT_SPRUCE_PRESSURE_PLATE))
                .add(block(VWBlocks.VERDANT_SPRUCE_DOOR));

        getOrCreateRawBuilder(VERDANT_MOSS_REPLACEABLE)
                .addTag(BlockTags.DIRT.location())
                .addOptionalTag(BlockTags.STONE_ORE_REPLACEABLES.location())
                .addOptionalTag(BlockTags.DEEPSLATE_ORE_REPLACEABLES.location())
                .addOptionalTag(BlockTags.SAND.location())
                .add(block(Blocks.GRAVEL))
                .add(block(Blocks.GRASS_BLOCK));


        getOrCreateRawBuilder(BlockTags.LEAVES)
                .add(block(VWBlocks.VERDANT_SPRUCE_LEAVES));

        getOrCreateRawBuilder(BlockTags.PREVENTS_NEARBY_LEAF_DECAY)
                .add(block(VWBlocks.VERDANT_MOSS_BLOCK))
                .add(block(VWBlocks.VERDANT_SPRUCE_LOG))
                .add(block(VWBlocks.VERDANT_SPRUCE_WOOD));

        getOrCreateRawBuilder(BlockTags.DIRT)
                .add(block(VWBlocks.VERDANT_MOSS_BLOCK));

        getOrCreateRawBuilder(BlockTags.FLOWERS)
                .add(block(VWBlocks.VERDANT_SPRUCE_SAPLING));

        getOrCreateRawBuilder(BlockTags.FLOWER_POTS)
                .add(block(VWBlocks.POTTED_VERDANT_SPRUCE_SAPLING));


        getOrCreateRawBuilder(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(block(VWBlocks.VERIXIUM_POWDER_BLOCK));

        getOrCreateRawBuilder(BlockTags.NEEDS_IRON_TOOL)
                .add(block(VWBlocks.VERIXIUM_STONE_ORE))
                .add(block(VWBlocks.VERIXIUM_DEEPSLATE_ORE))
                .add(block(VWBlocks.LODESTONE_WIND_CORE))
                .add(block(VWBlocks.IRIDESCENT_GLASS))
                .add(block(VWBlocks.IRIDESCENT_GLASS_PANE));

        getOrCreateRawBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(block(VWBlocks.VERIXIUM_STONE_ORE))
                .add(block(VWBlocks.VERIXIUM_DEEPSLATE_ORE))
                .add(block(VWBlocks.LODESTONE_WIND_CORE))
                .add(block(VWBlocks.IRIDESCENT_GLASS))
                .add(block(VWBlocks.IRIDESCENT_GLASS_PANE));

        getOrCreateRawBuilder(BlockTags.MINEABLE_WITH_AXE)
                .add(block(VWBlocks.VERDANT_SPRUCE_PLANKS))
                .add(block(VWBlocks.VERDANT_SPRUCE_LOG))
                .add(block(VWBlocks.VERDANT_SPRUCE_WOOD))
                .add(block(VWBlocks.STRIPPED_VERDANT_SPRUCE_LOG))
                .add(block(VWBlocks.STRIPPED_VERDANT_SPRUCE_WOOD))
                .add(block(VWBlocks.VERDANT_SPRUCE_SLAB))
                .add(block(VWBlocks.VERDANT_SPRUCE_STAIRS))
                .add(block(VWBlocks.VERDANT_SPRUCE_SIGN))
                .add(block(VWBlocks.VERDANT_SPRUCE_WALL_SIGN))
                .add(block(VWBlocks.VERDANT_SPRUCE_HANGING_SIGN))
                .add(block(VWBlocks.VERDANT_SPRUCE_WALL_HANGING_SIGN))
                .add(block(VWBlocks.VERDANT_SPRUCE_FENCE))
                .add(block(VWBlocks.VERDANT_SPRUCE_FENCE_GATE))
                .add(block(VWBlocks.VERDANT_SPRUCE_BUTTON))
                .add(block(VWBlocks.VERDANT_SPRUCE_PRESSURE_PLATE))
                .add(block(VWBlocks.VERDANT_SPRUCE_DOOR));

        getOrCreateRawBuilder(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(block(VWBlocks.VERIXIUM_POWDER_BLOCK));

        getOrCreateRawBuilder(BlockTags.EDIBLE_FOR_SHEEP)
                .add(block(VWBlocks.VERDANT_MOSS_BLOCK));

        getOrCreateRawBuilder(BlockTags.DAMPENS_VIBRATIONS)
                .add(block(VWBlocks.VERDANT_MOSS_BLOCK));




        getOrCreateRawBuilder(BlockTags.WOODEN_BUTTONS)
                .add(block(VWBlocks.VERDANT_SPRUCE_BUTTON));

        getOrCreateRawBuilder(BlockTags.WOODEN_DOORS)
                .add(block(VWBlocks.VERDANT_SPRUCE_DOOR));

        getOrCreateRawBuilder(BlockTags.WOODEN_FENCES)
                .add(block(VWBlocks.VERDANT_SPRUCE_FENCE));

        getOrCreateRawBuilder(BlockTags.WOODEN_STAIRS)
                .add(block(VWBlocks.VERDANT_SPRUCE_STAIRS));

        getOrCreateRawBuilder(BlockTags.WOODEN_SLABS)
                .add(block(VWBlocks.VERDANT_SPRUCE_SLAB));

        getOrCreateRawBuilder(BlockTags.WOODEN_PRESSURE_PLATES)
                .add(block(VWBlocks.VERDANT_SPRUCE_PRESSURE_PLATE));

        getOrCreateRawBuilder(BlockTags.WOODEN_DOORS)
                .add(block(VWBlocks.VERDANT_SPRUCE_DOOR));

        getOrCreateRawBuilder(BlockTags.STANDING_SIGNS)
                .add(block(VWBlocks.VERDANT_SPRUCE_SIGN));

        getOrCreateRawBuilder(BlockTags.WALL_SIGNS)
                .add(block(VWBlocks.VERDANT_SPRUCE_WALL_SIGN));

        getOrCreateRawBuilder(BlockTags.CEILING_HANGING_SIGNS)
                .add(block(VWBlocks.VERDANT_SPRUCE_HANGING_SIGN));

        getOrCreateRawBuilder(BlockTags.WALL_HANGING_SIGNS)
                .add(block(VWBlocks.VERDANT_SPRUCE_WALL_HANGING_SIGN));

        getOrCreateRawBuilder(BlockTags.WOODEN_SHELVES)
                .add(block(VWBlocks.VERDANT_SPRUCE_SHELF));


        getOrCreateRawBuilder(VWBlockTags.VERDANT_SPRUCE_LOGS)
                .add(block(VWBlocks.VERDANT_SPRUCE_LOG))
                .add(block(VWBlocks.VERDANT_SPRUCE_WOOD))
                .add(block(VWBlocks.STRIPPED_VERDANT_SPRUCE_LOG))
                .add(block(VWBlocks.STRIPPED_VERDANT_SPRUCE_WOOD));

        getOrCreateRawBuilder(BlockTags.OVERWORLD_NATURAL_LOGS)
                .addTag(VWBlockTags.VERDANT_SPRUCE_LOGS.location());
    }

    public static final TagKey<Block> VERDANT_BLOCKS = create("verdant_blocks");
    public static final TagKey<Block> VERDANT_SPRUCE_LOGS = create("verdant_spruce_logs");
    public static final TagKey<Block> VERDANT_MOSS_REPLACEABLE = create("verdant_moss_replaceable");

    private static TagKey<Block> create(String name) {
        return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name));
    }

    private static Identifier copyFrom(String path) {
        return Identifier.withDefaultNamespace(path);
    }
}