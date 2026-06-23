package cliffordha.totvw.tag;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.ModBlocks;
import cliffordha.totvw.registry.blocks.VerdantBlocks;
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

public class ModBlockTags extends FabricTagsProvider.BlockTagsProvider {
    public ModBlockTags(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider wrapperLookup) {
        valueLookupBuilder(VERDANT_BLOCKS)
                .add(ModBlocks.VERIXIUM_STONE_ORE)
                .add(ModBlocks.VERIXIUM_DEEPSLATE_ORE)
                .add(ModBlocks.VERIXIUM_POWDER_BLOCK)
                .add(VerdantBlocks.VERDANT_MOSS_BLOCK)
                .add(VerdantBlocks.VERDANT_SPRUCE_LEAVES)
                .add(VerdantBlocks.VERDANT_SPRUCE_SAPLING)
                .add(VerdantBlocks.VERDANT_SPRUCE_PLANKS)
                .add(VerdantBlocks.VERDANT_SPRUCE_LOG)
                .add(VerdantBlocks.VERDANT_SPRUCE_WOOD)
                .add(VerdantBlocks.STRIPPED_VERDANT_SPRUCE_LOG)
                .add(VerdantBlocks.STRIPPED_VERDANT_SPRUCE_WOOD)
                .add(VerdantBlocks.VERDANT_SPRUCE_SLAB)
                .add(VerdantBlocks.VERDANT_SPRUCE_STAIRS)
                .add(VerdantBlocks.VERDANT_SPRUCE_FENCE)
                .add(VerdantBlocks.VERDANT_SPRUCE_FENCE_GATE)
                .add(VerdantBlocks.VERDANT_SPRUCE_SIGN)
                .add(VerdantBlocks.VERDANT_SPRUCE_WALL_SIGN)
                .add(VerdantBlocks.VERDANT_SPRUCE_HANGING_SIGN)
                .add(VerdantBlocks.VERDANT_SPRUCE_WALL_HANGING_SIGN)
                .add(VerdantBlocks.VERDANT_SPRUCE_SHELF)
                .add(VerdantBlocks.VERDANT_SPRUCE_BUTTON)
                .add(VerdantBlocks.VERDANT_SPRUCE_PRESSURE_PLATE)
                .add(VerdantBlocks.VERDANT_SPRUCE_DOOR);

        valueLookupBuilder(ModBlockTags.VERDANT_MOSS_REPLACEABLE)
                .add(Blocks.GRASS_BLOCK)
                .add(Blocks.DIRT)
                .add(Blocks.DEEPSLATE)
                .add(Blocks.COARSE_DIRT)
                .add(Blocks.STONE);


        valueLookupBuilder(BlockTags.LEAVES)
                .add(VerdantBlocks.VERDANT_SPRUCE_LEAVES);

        valueLookupBuilder(BlockTags.DIRT)
                .add(VerdantBlocks.VERDANT_MOSS_BLOCK);

        valueLookupBuilder(BlockTags.SAPLINGS)
                .add(VerdantBlocks.VERDANT_SPRUCE_SAPLING);

        valueLookupBuilder(BlockTags.FLOWER_POTS)
                .add(VerdantBlocks.POTTED_VERDANT_SPRUCE_SAPLING);


        valueLookupBuilder(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ModBlocks.VERIXIUM_POWDER_BLOCK);

        valueLookupBuilder(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.VERIXIUM_STONE_ORE)
                .add(ModBlocks.VERIXIUM_DEEPSLATE_ORE)
                .add(ModBlocks.IRIDESCENT_GLASS)
                .add(ModBlocks.IRIDESCENT_GLASS_PANE);

        valueLookupBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.VERIXIUM_STONE_ORE)
                .add(ModBlocks.VERIXIUM_DEEPSLATE_ORE)
                .add(ModBlocks.IRIDESCENT_GLASS)
                .add(ModBlocks.IRIDESCENT_GLASS_PANE);

        valueLookupBuilder(BlockTags.MINEABLE_WITH_AXE)
                .add(VerdantBlocks.VERDANT_SPRUCE_PLANKS)
                .add(VerdantBlocks.VERDANT_SPRUCE_LOG)
                .add(VerdantBlocks.VERDANT_SPRUCE_WOOD)
                .add(VerdantBlocks.STRIPPED_VERDANT_SPRUCE_LOG)
                .add(VerdantBlocks.STRIPPED_VERDANT_SPRUCE_WOOD)
                .add(VerdantBlocks.VERDANT_SPRUCE_SLAB)
                .add(VerdantBlocks.VERDANT_SPRUCE_STAIRS)
                .add(VerdantBlocks.VERDANT_SPRUCE_SIGN)
                .add(VerdantBlocks.VERDANT_SPRUCE_WALL_SIGN)
                .add(VerdantBlocks.VERDANT_SPRUCE_HANGING_SIGN)
                .add(VerdantBlocks.VERDANT_SPRUCE_WALL_HANGING_SIGN)
                .add(VerdantBlocks.VERDANT_SPRUCE_FENCE)
                .add(VerdantBlocks.VERDANT_SPRUCE_FENCE_GATE)
                .add(VerdantBlocks.VERDANT_SPRUCE_BUTTON)
                .add(VerdantBlocks.VERDANT_SPRUCE_PRESSURE_PLATE)
                .add(VerdantBlocks.VERDANT_SPRUCE_DOOR);

        valueLookupBuilder(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(ModBlocks.VERIXIUM_POWDER_BLOCK);

        valueLookupBuilder(BlockTags.EDIBLE_FOR_SHEEP)
                .add(VerdantBlocks.VERDANT_MOSS_BLOCK);

        valueLookupBuilder(BlockTags.DAMPENS_VIBRATIONS)
                .add(VerdantBlocks.VERDANT_MOSS_BLOCK);




        valueLookupBuilder(BlockTags.WOODEN_BUTTONS)
                .add(VerdantBlocks.VERDANT_SPRUCE_BUTTON);

        valueLookupBuilder(BlockTags.WOODEN_DOORS)
                .add(VerdantBlocks.VERDANT_SPRUCE_DOOR);

        valueLookupBuilder(BlockTags.WOODEN_FENCES)
                .add(VerdantBlocks.VERDANT_SPRUCE_FENCE);

        valueLookupBuilder(BlockTags.WOODEN_STAIRS)
                .add(VerdantBlocks.VERDANT_SPRUCE_STAIRS);

        valueLookupBuilder(BlockTags.WOODEN_SLABS)
                .add(VerdantBlocks.VERDANT_SPRUCE_SLAB);

        valueLookupBuilder(BlockTags.WOODEN_PRESSURE_PLATES)
                .add(VerdantBlocks.VERDANT_SPRUCE_PRESSURE_PLATE);

        valueLookupBuilder(BlockTags.WOODEN_DOORS)
                .add(VerdantBlocks.VERDANT_SPRUCE_DOOR);

        valueLookupBuilder(BlockTags.STANDING_SIGNS)
                .add(VerdantBlocks.VERDANT_SPRUCE_SIGN);

        valueLookupBuilder(BlockTags.WALL_SIGNS)
                .add(VerdantBlocks.VERDANT_SPRUCE_WALL_SIGN);

        valueLookupBuilder(BlockTags.CEILING_HANGING_SIGNS)
                .add(VerdantBlocks.VERDANT_SPRUCE_HANGING_SIGN);

        valueLookupBuilder(BlockTags.WALL_HANGING_SIGNS)
                .add(VerdantBlocks.VERDANT_SPRUCE_WALL_HANGING_SIGN);

        valueLookupBuilder(BlockTags.WOODEN_SHELVES)
                .add(VerdantBlocks.VERDANT_SPRUCE_SHELF);


        valueLookupBuilder(ModBlockTags.VERDANT_SPRUCE_LOGS)
                .add(VerdantBlocks.VERDANT_SPRUCE_LOG)
                .add(VerdantBlocks.VERDANT_SPRUCE_WOOD)
                .add(VerdantBlocks.STRIPPED_VERDANT_SPRUCE_LOG)
                .add(VerdantBlocks.STRIPPED_VERDANT_SPRUCE_WOOD);

        valueLookupBuilder(BlockTags.LOGS_THAT_BURN)
                .addTag(ModBlockTags.VERDANT_SPRUCE_LOGS);
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