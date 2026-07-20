package cliffordha.totvw.datagen;

import cliffordha.totvw.block.custom.LodestoneWindCore;
import cliffordha.totvw.block.custom.VWStorageBlock;
import cliffordha.totvw.registry.VWItems;
import cliffordha.totvw.registry.blocks.VWBlocksVerdant;
import cliffordha.totvw.registry.VWBlocks;
import cliffordha.totvw.registry.items.VWItemsScatteredPage;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.*;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class VWModelProvider extends FabricModelProvider {
    public VWModelProvider(FabricPackOutput output) {
        super(output);
    }

    private static TextureMapping storageBoxTextureMapping(Block block, String topSuffix) {
        return new TextureMapping()
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_side"))
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(block, "_bottom"))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, topSuffix));
    }

    private static TextureMapping lodestoneWindCoreMapping(Block block, String front) {
        return new TextureMapping()
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(block))
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(block))
                .put(TextureSlot.FRONT, TextureMapping.getBlockTexture(block, "_front" + front));
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators block) {
        var verdantSpruceFamily = block.family(VWBlocksVerdant.VERDANT_SPRUCE_PLANKS);

        block.createTrivialCube(VWBlocks.VERIXIUM_STONE_ORE);
        block.createTrivialCube(VWBlocks.VERIXIUM_DEEPSLATE_ORE);
        block.createColoredBlockWithRandomRotations(TexturedModel.CUBE, VWBlocksVerdant.VERDANT_MOSS_BLOCK);
        block.createColoredBlockWithRandomRotations(TexturedModel.CUBE, VWBlocks.VERIXIUM_POWDER_BLOCK);

        block.createGlassBlocks(VWBlocks.IRIDESCENT_GLASS, VWBlocks.IRIDESCENT_GLASS_PANE);

        block.createTrivialBlock(VWBlocksVerdant.VERDANT_SPRUCE_LEAVES, TexturedModel.LEAVES);
        block.createPlantWithDefaultItem(VWBlocksVerdant.VERDANT_SPRUCE_SAPLING, VWBlocksVerdant.POTTED_VERDANT_SPRUCE_SAPLING, BlockModelGenerators.PlantType.TINTED);

        verdantSpruceFamily.generateFor(VWBlocksVerdant.VERDANT_SPRUCE_FAMILY);
        block.createHangingSign(VWBlocksVerdant.STRIPPED_VERDANT_SPRUCE_WOOD, VWBlocksVerdant.VERDANT_SPRUCE_HANGING_SIGN, VWBlocksVerdant.VERDANT_SPRUCE_WALL_HANGING_SIGN);
        block.createShelf(VWBlocksVerdant.VERDANT_SPRUCE_SHELF, VWBlocksVerdant.STRIPPED_VERDANT_SPRUCE_LOG);
        block.woodProvider(VWBlocksVerdant.VERDANT_SPRUCE_LOG).log(VWBlocksVerdant.VERDANT_SPRUCE_LOG).wood(VWBlocksVerdant.VERDANT_SPRUCE_WOOD);
        block.woodProvider(VWBlocksVerdant.STRIPPED_VERDANT_SPRUCE_LOG).log(VWBlocksVerdant.STRIPPED_VERDANT_SPRUCE_LOG).wood(VWBlocksVerdant.STRIPPED_VERDANT_SPRUCE_WOOD);

        MultiVariant OPEN = BlockModelGenerators.plainVariant(
                ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(VWBlocksVerdant.VERDANT_SPRUCE_STORAGE_BOX, "_open",
                        storageBoxTextureMapping(VWBlocksVerdant.VERDANT_SPRUCE_STORAGE_BOX, "_top_open"), block.modelOutput)
        );
        MultiVariant CLOSED = BlockModelGenerators.plainVariant(
                ModelTemplates.CUBE_BOTTOM_TOP.create(VWBlocksVerdant.VERDANT_SPRUCE_STORAGE_BOX,
                        storageBoxTextureMapping(VWBlocksVerdant.VERDANT_SPRUCE_STORAGE_BOX, "_top"), block.modelOutput)
        );

        MultiVariant LODESTONE_WIND_CORE_ACTIVE = BlockModelGenerators.plainVariant(
                ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM.createWithSuffix(VWBlocks.LODESTONE_WIND_CORE, "_active",
                        lodestoneWindCoreMapping(VWBlocks.LODESTONE_WIND_CORE, "_active"), block.modelOutput)
        );
        MultiVariant LODESTONE_WIND_CORE = BlockModelGenerators.plainVariant(
                ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM.create(VWBlocks.LODESTONE_WIND_CORE,
                        lodestoneWindCoreMapping(VWBlocks.LODESTONE_WIND_CORE, ""), block.modelOutput)
        );

        block.blockStateOutput.accept(MultiVariantGenerator.dispatch(VWBlocks.LODESTONE_WIND_CORE)
                .with(BlockModelGenerators.createBooleanModelDispatch(LodestoneWindCore.ACTIVE, LODESTONE_WIND_CORE_ACTIVE, LODESTONE_WIND_CORE))
                .with(PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING)
                        .select(Direction.NORTH, BlockModelGenerators.NOP)
                        .select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180)
                        .select(Direction.WEST,  BlockModelGenerators.Y_ROT_270)
                        .select(Direction.EAST,  BlockModelGenerators.Y_ROT_90)
                ));

        block.blockStateOutput.accept(MultiVariantGenerator.dispatch(VWBlocksVerdant.VERDANT_SPRUCE_STORAGE_BOX)
                .with(BlockModelGenerators.createBooleanModelDispatch(VWStorageBlock.OPEN, OPEN, CLOSED))
                .with(PropertyDispatch.modify(BlockStateProperties.FACING)
                        .select(Direction.DOWN,  BlockModelGenerators.X_ROT_180)
                        .select(Direction.UP,    BlockModelGenerators.NOP)
                        .select(Direction.NORTH, BlockModelGenerators.X_ROT_90)
                        .select(Direction.SOUTH, BlockModelGenerators.X_ROT_90.then(BlockModelGenerators.Y_ROT_180))
                        .select(Direction.WEST,  BlockModelGenerators.X_ROT_90.then(BlockModelGenerators.Y_ROT_270))
                        .select(Direction.EAST,  BlockModelGenerators.X_ROT_90.then(BlockModelGenerators.Y_ROT_90))
                ));
    }

    private static void add(ItemModelGenerators item, ModelTemplate template, Item... v) {
        for (Item items : v) {
            item.generateFlatItem(items, template);
        }
    }
    private static void addCopy(ItemModelGenerators item, ModelTemplate template, Item donor, Item... v) {
        for (Item items : v) {
            item.generateFlatItem(items, donor, template);
        }
    }

    @Override
    public void generateItemModels(ItemModelGenerators item) {
        add(item, ModelTemplates.FLAT_ITEM,
                VWItems.VERIXIUM_CHUNK,
                VWItems.CONDENSED_VERIXIUM,
                VWItems.VERIXIUM_SHARD,
                VWItems.VERIXIUM_POWDER,
                VWItems.VERIXIUM_INGOT,
                VWItems.VERIXIUM_PAPER,
                VWItems.VERIXIUM_ARMOR_UPGRADE_TEMPLATE,

                VWItems.VERIXIUM_HELMET,
                VWItems.VERIXIUM_CHESTPLATE,
                VWItems.VERIXIUM_LEGGINGS,
                VWItems.VERIXIUM_BOOTS,

                VWItems.VERIXIUM_WOLF_ARMOR,
                VWItems.VERIXIUM_HORSE_ARMOR,

                VWItems.VERIXIUM_FLUID_BUCKET,
                VWItems.VERDANT_SPRUCE_BOAT,
                VWItems.VERDANT_SPRUCE_CHEST_BOAT
        );

        add(item, ModelTemplates.FLAT_HANDHELD_ITEM,
                VWItems.VERIXIUM_SWORD,
                VWItems.VERIXIUM_AXE,
                VWItems.VERIXIUM_PICKAXE,
                VWItems.VERIXIUM_SHOVEL,
                VWItems.VERIXIUM_HOE
        );
        item.generateSpear(VWItems.VERIXIUM_SPEAR);

        item.generateFlatItem(VWItemsScatteredPage.SCATTERED_PAGE, ModelTemplates.FLAT_ITEM);
        addCopy(item, ModelTemplates.FLAT_ITEM, VWItemsScatteredPage.SCATTERED_PAGE,
                VWItemsScatteredPage.SP_ID_1000,

                VWItemsScatteredPage.SP_ID_1001,
                VWItemsScatteredPage.SP_ID_1002,
                VWItemsScatteredPage.SP_ID_1003,
                VWItemsScatteredPage.SP_ID_1004,

                VWItemsScatteredPage.SP_ID_1005,

                VWItemsScatteredPage.SP_ID_1006,

                VWItemsScatteredPage.SP_ID_1007,
                VWItemsScatteredPage.SP_ID_1008,
                VWItemsScatteredPage.SP_ID_1009,

                VWItemsScatteredPage.SP_ID_TEST,
                VWItemsScatteredPage.PLAYER_STATS
        );
    }

    @Override
    public String getName() {
        return "VWModelProvider";
    }
}