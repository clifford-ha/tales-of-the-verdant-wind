package cliffordha.totvw.tag;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.VWBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class VWBlockTags extends FabricTagsProvider.BlockTagsProvider {
    public VWBlockTags(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider wrapperLookup) {
        valueLookupBuilder(VERDANT_BLOCKS)
                .add(VWBlocks.VERIXIUM_STONE_ORE)
                .add(VWBlocks.VERIXIUM_DEEPSLATE_ORE)
                .add(VWBlocks.VERIXIUM_POWDER_BLOCK)
                .add(VWBlocks.VERDANT_MOSS_BLOCK)
                .add(VWBlocks.VERDANT_SPRUCE_LEAVES)
                .add(VWBlocks.VERDANT_SPRUCE_SAPLING)
                .add(VWBlocks.VERDANT_SPRUCE_PLANKS)
                .add(VWBlocks.VERDANT_SPRUCE_LOG)
                .add(VWBlocks.VERDANT_SPRUCE_WOOD)
                .add(VWBlocks.STRIPPED_VERDANT_SPRUCE_LOG)
                .add(VWBlocks.STRIPPED_VERDANT_SPRUCE_WOOD)
                .add(VWBlocks.VERDANT_SPRUCE_SLAB)
                .add(VWBlocks.VERDANT_SPRUCE_STAIRS)
                .add(VWBlocks.VERDANT_SPRUCE_FENCE)
                .add(VWBlocks.VERDANT_SPRUCE_FENCE_GATE)
                .add(VWBlocks.VERDANT_SPRUCE_SIGN)
                .add(VWBlocks.VERDANT_SPRUCE_WALL_SIGN)
                .add(VWBlocks.VERDANT_SPRUCE_HANGING_SIGN)
                .add(VWBlocks.VERDANT_SPRUCE_WALL_HANGING_SIGN)
                .add(VWBlocks.VERDANT_SPRUCE_SHELF)
                .add(VWBlocks.VERDANT_SPRUCE_BUTTON)
                .add(VWBlocks.VERDANT_SPRUCE_PRESSURE_PLATE)
                .add(VWBlocks.VERDANT_SPRUCE_DOOR);

        valueLookupBuilder(VWBlockTags.VERDANT_MOSS_REPLACEABLE)
                .add(Blocks.GRASS_BLOCK)
                .add(Blocks.DIRT)
                .add(Blocks.DEEPSLATE)
                .add(Blocks.COARSE_DIRT)
                .add(Blocks.STONE);


        valueLookupBuilder(BlockTags.LEAVES)
                .add(VWBlocks.VERDANT_SPRUCE_LEAVES);

        valueLookupBuilder(BlockTags.DIRT)
                .add(VWBlocks.VERDANT_MOSS_BLOCK);

        valueLookupBuilder(BlockTags.SAPLINGS)
                .add(VWBlocks.VERDANT_SPRUCE_SAPLING);

        valueLookupBuilder(BlockTags.FLOWER_POTS)
                .add(VWBlocks.POTTED_VERDANT_SPRUCE_SAPLING);


        valueLookupBuilder(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(VWBlocks.VERIXIUM_POWDER_BLOCK);

        valueLookupBuilder(BlockTags.NEEDS_IRON_TOOL)
                .add(VWBlocks.VERIXIUM_STONE_ORE)
                .add(VWBlocks.VERIXIUM_DEEPSLATE_ORE)
                .add(VWBlocks.LODESTONE_WIND_CORE)
                .add(VWBlocks.IRIDESCENT_GLASS)
                .add(VWBlocks.IRIDESCENT_GLASS_PANE);

        valueLookupBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(VWBlocks.VERIXIUM_STONE_ORE)
                .add(VWBlocks.VERIXIUM_DEEPSLATE_ORE)
                .add(VWBlocks.LODESTONE_WIND_CORE)
                .add(VWBlocks.IRIDESCENT_GLASS)
                .add(VWBlocks.IRIDESCENT_GLASS_PANE);

        valueLookupBuilder(BlockTags.MINEABLE_WITH_AXE)
                .add(VWBlocks.VERDANT_SPRUCE_PLANKS)
                .add(VWBlocks.VERDANT_SPRUCE_LOG)
                .add(VWBlocks.VERDANT_SPRUCE_WOOD)
                .add(VWBlocks.STRIPPED_VERDANT_SPRUCE_LOG)
                .add(VWBlocks.STRIPPED_VERDANT_SPRUCE_WOOD)
                .add(VWBlocks.VERDANT_SPRUCE_SLAB)
                .add(VWBlocks.VERDANT_SPRUCE_STAIRS)
                .add(VWBlocks.VERDANT_SPRUCE_SIGN)
                .add(VWBlocks.VERDANT_SPRUCE_WALL_SIGN)
                .add(VWBlocks.VERDANT_SPRUCE_HANGING_SIGN)
                .add(VWBlocks.VERDANT_SPRUCE_WALL_HANGING_SIGN)
                .add(VWBlocks.VERDANT_SPRUCE_FENCE)
                .add(VWBlocks.VERDANT_SPRUCE_FENCE_GATE)
                .add(VWBlocks.VERDANT_SPRUCE_BUTTON)
                .add(VWBlocks.VERDANT_SPRUCE_PRESSURE_PLATE)
                .add(VWBlocks.VERDANT_SPRUCE_DOOR);

        valueLookupBuilder(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(VWBlocks.VERIXIUM_POWDER_BLOCK);

        valueLookupBuilder(BlockTags.EDIBLE_FOR_SHEEP)
                .add(VWBlocks.VERDANT_MOSS_BLOCK);

        valueLookupBuilder(BlockTags.DAMPENS_VIBRATIONS)
                .add(VWBlocks.VERDANT_MOSS_BLOCK);




        valueLookupBuilder(BlockTags.WOODEN_BUTTONS)
                .add(VWBlocks.VERDANT_SPRUCE_BUTTON);

        valueLookupBuilder(BlockTags.WOODEN_DOORS)
                .add(VWBlocks.VERDANT_SPRUCE_DOOR);

        valueLookupBuilder(BlockTags.WOODEN_FENCES)
                .add(VWBlocks.VERDANT_SPRUCE_FENCE);

        valueLookupBuilder(BlockTags.WOODEN_STAIRS)
                .add(VWBlocks.VERDANT_SPRUCE_STAIRS);

        valueLookupBuilder(BlockTags.WOODEN_SLABS)
                .add(VWBlocks.VERDANT_SPRUCE_SLAB);

        valueLookupBuilder(BlockTags.WOODEN_PRESSURE_PLATES)
                .add(VWBlocks.VERDANT_SPRUCE_PRESSURE_PLATE);

        valueLookupBuilder(BlockTags.WOODEN_DOORS)
                .add(VWBlocks.VERDANT_SPRUCE_DOOR);

        valueLookupBuilder(BlockTags.STANDING_SIGNS)
                .add(VWBlocks.VERDANT_SPRUCE_SIGN);

        valueLookupBuilder(BlockTags.WALL_SIGNS)
                .add(VWBlocks.VERDANT_SPRUCE_WALL_SIGN);

        valueLookupBuilder(BlockTags.CEILING_HANGING_SIGNS)
                .add(VWBlocks.VERDANT_SPRUCE_HANGING_SIGN);

        valueLookupBuilder(BlockTags.WALL_HANGING_SIGNS)
                .add(VWBlocks.VERDANT_SPRUCE_WALL_HANGING_SIGN);

        valueLookupBuilder(BlockTags.WOODEN_SHELVES)
                .add(VWBlocks.VERDANT_SPRUCE_SHELF);


        valueLookupBuilder(VWBlockTags.VERDANT_SPRUCE_LOGS)
                .add(VWBlocks.VERDANT_SPRUCE_LOG)
                .add(VWBlocks.VERDANT_SPRUCE_WOOD)
                .add(VWBlocks.STRIPPED_VERDANT_SPRUCE_LOG)
                .add(VWBlocks.STRIPPED_VERDANT_SPRUCE_WOOD);

        valueLookupBuilder(BlockTags.LOGS_THAT_BURN)
                .addTag(VWBlockTags.VERDANT_SPRUCE_LOGS);
    }

    public static final TagKey<Block> VERDANT_BLOCKS = create("verdant_blocks");
    public static final TagKey<Block> VERDANT_SPRUCE_LOGS = create("verdant_spruce_logs");
    public static final TagKey<Block> VERDANT_MOSS_REPLACEABLE = create("verdant_moss_replaceable");

    private static TagKey<Block> create(String name) {
        return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name)); }
}