package cliffordha.totvw.tag;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.VWBlocks;
import cliffordha.totvw.registry.blocks.VWBlocksVerdant;
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
                .add(VWBlocksVerdant.VERDANT_MOSS_BLOCK)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_LEAVES)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_SAPLING)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_PLANKS)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_LOG)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_WOOD)
                .add(VWBlocksVerdant.STRIPPED_VERDANT_SPRUCE_LOG)
                .add(VWBlocksVerdant.STRIPPED_VERDANT_SPRUCE_WOOD)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_SLAB)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_STAIRS)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_FENCE)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_FENCE_GATE)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_SIGN)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_WALL_SIGN)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_HANGING_SIGN)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_WALL_HANGING_SIGN)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_SHELF)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_BUTTON)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_PRESSURE_PLATE)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_DOOR);

        valueLookupBuilder(VWBlockTags.VERDANT_MOSS_REPLACEABLE)
                .add(Blocks.GRASS_BLOCK)
                .add(Blocks.DIRT)
                .add(Blocks.DEEPSLATE)
                .add(Blocks.COARSE_DIRT)
                .add(Blocks.STONE);


        valueLookupBuilder(BlockTags.LEAVES)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_LEAVES);

        valueLookupBuilder(BlockTags.DIRT)
                .add(VWBlocksVerdant.VERDANT_MOSS_BLOCK);

        valueLookupBuilder(BlockTags.SAPLINGS)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_SAPLING);

        valueLookupBuilder(BlockTags.FLOWER_POTS)
                .add(VWBlocksVerdant.POTTED_VERDANT_SPRUCE_SAPLING);


        valueLookupBuilder(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(VWBlocks.VERIXIUM_POWDER_BLOCK);

        valueLookupBuilder(BlockTags.NEEDS_IRON_TOOL)
                .add(VWBlocks.VERIXIUM_STONE_ORE)
                .add(VWBlocks.VERIXIUM_DEEPSLATE_ORE)
                .add(VWBlocks.IRIDESCENT_GLASS)
                .add(VWBlocks.IRIDESCENT_GLASS_PANE);

        valueLookupBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(VWBlocks.VERIXIUM_STONE_ORE)
                .add(VWBlocks.VERIXIUM_DEEPSLATE_ORE)
                .add(VWBlocks.IRIDESCENT_GLASS)
                .add(VWBlocks.IRIDESCENT_GLASS_PANE);

        valueLookupBuilder(BlockTags.MINEABLE_WITH_AXE)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_PLANKS)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_LOG)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_WOOD)
                .add(VWBlocksVerdant.STRIPPED_VERDANT_SPRUCE_LOG)
                .add(VWBlocksVerdant.STRIPPED_VERDANT_SPRUCE_WOOD)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_SLAB)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_STAIRS)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_SIGN)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_WALL_SIGN)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_HANGING_SIGN)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_WALL_HANGING_SIGN)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_FENCE)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_FENCE_GATE)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_BUTTON)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_PRESSURE_PLATE)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_DOOR);

        valueLookupBuilder(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(VWBlocks.VERIXIUM_POWDER_BLOCK);

        valueLookupBuilder(BlockTags.EDIBLE_FOR_SHEEP)
                .add(VWBlocksVerdant.VERDANT_MOSS_BLOCK);

        valueLookupBuilder(BlockTags.DAMPENS_VIBRATIONS)
                .add(VWBlocksVerdant.VERDANT_MOSS_BLOCK);




        valueLookupBuilder(BlockTags.WOODEN_BUTTONS)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_BUTTON);

        valueLookupBuilder(BlockTags.WOODEN_DOORS)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_DOOR);

        valueLookupBuilder(BlockTags.WOODEN_FENCES)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_FENCE);

        valueLookupBuilder(BlockTags.WOODEN_STAIRS)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_STAIRS);

        valueLookupBuilder(BlockTags.WOODEN_SLABS)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_SLAB);

        valueLookupBuilder(BlockTags.WOODEN_PRESSURE_PLATES)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_PRESSURE_PLATE);

        valueLookupBuilder(BlockTags.WOODEN_DOORS)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_DOOR);

        valueLookupBuilder(BlockTags.STANDING_SIGNS)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_SIGN);

        valueLookupBuilder(BlockTags.WALL_SIGNS)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_WALL_SIGN);

        valueLookupBuilder(BlockTags.CEILING_HANGING_SIGNS)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_HANGING_SIGN);

        valueLookupBuilder(BlockTags.WALL_HANGING_SIGNS)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_WALL_HANGING_SIGN);

        valueLookupBuilder(BlockTags.WOODEN_SHELVES)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_SHELF);


        valueLookupBuilder(VWBlockTags.VERDANT_SPRUCE_LOGS)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_LOG)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_WOOD)
                .add(VWBlocksVerdant.STRIPPED_VERDANT_SPRUCE_LOG)
                .add(VWBlocksVerdant.STRIPPED_VERDANT_SPRUCE_WOOD);

        valueLookupBuilder(BlockTags.LOGS_THAT_BURN)
                .addTag(VWBlockTags.VERDANT_SPRUCE_LOGS);
    }
    public static final TagKey<Block> VERDANT_BLOCKS = TagKey.create(Registries.BLOCK,
            Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verdant_blocks")
    );
    public static final TagKey<Block> VERDANT_SPRUCE_LOGS = TagKey.create(Registries.BLOCK,
            Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verdant_spruce_logs")
    );
    public static final TagKey<Block> VERDANT_MOSS_REPLACEABLE = TagKey.create(Registries.BLOCK,
            Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verdant_moss_replaceable")
    );
}