package cliffordha.totvw.datagen;

import cliffordha.totvw.block.VWStorageBlock;
import cliffordha.totvw.registry.VWItems;
import cliffordha.totvw.registry.blocks.VWBlocksVerdant;
import cliffordha.totvw.registry.VWBlocks;
import cliffordha.totvw.registry.items.VWItemsScatteredPage;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.Direction;
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

    @Override
    public void generateItemModels(ItemModelGenerators item) {
        item.generateFlatItem(VWItems.VERIXIUM_CHUNK, ModelTemplates.FLAT_ITEM);
        item.generateFlatItem(VWItems.CONDENSED_VERIXIUM, ModelTemplates.FLAT_ITEM);
        item.generateFlatItem(VWItems.VERIXIUM_SHARD, ModelTemplates.FLAT_ITEM);
        item.generateFlatItem(VWItems.VERIXIUM_POWDER, ModelTemplates.FLAT_ITEM);
        item.generateFlatItem(VWItems.VERIXIUM_INGOT, ModelTemplates.FLAT_ITEM);
        item.generateFlatItem(VWItems.VERIXIUM_PAPER, ModelTemplates.FLAT_ITEM);
        item.generateFlatItem(VWItems.VERIXIUM_ARMOR_UPGRADE_TEMPLATE, ModelTemplates.FLAT_ITEM);

        item.generateFlatItem(VWItems.VERIXIUM_HELMET, ModelTemplates.FLAT_ITEM);
        item.generateFlatItem(VWItems.VERIXIUM_CHESTPLATE, ModelTemplates.FLAT_ITEM);
        item.generateFlatItem(VWItems.VERIXIUM_LEGGINGS, ModelTemplates.FLAT_ITEM);
        item.generateFlatItem(VWItems.VERIXIUM_BOOTS, ModelTemplates.FLAT_ITEM);

        item.generateFlatItem(VWItems.VERIXIUM_WOLF_ARMOR, ModelTemplates.FLAT_ITEM);
        item.generateFlatItem(VWItems.VERIXIUM_HORSE_ARMOR, ModelTemplates.FLAT_HANDHELD_ITEM);

        item.generateFlatItem(VWItems.VERIXIUM_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
        item.generateFlatItem(VWItems.VERIXIUM_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        item.generateFlatItem(VWItems.VERIXIUM_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        item.generateFlatItem(VWItems.VERIXIUM_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
        item.generateFlatItem(VWItems.VERIXIUM_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);
        item.generateSpear(VWItems.VERIXIUM_SPEAR);

        item.generateFlatItem(VWItems.VERIXIUM_FLUID_BUCKET, ModelTemplates.FLAT_ITEM);

        item.generateFlatItem(VWItems.VERDANT_SPRUCE_BOAT, ModelTemplates.FLAT_ITEM);
        item.generateFlatItem(VWItems.VERDANT_SPRUCE_CHEST_BOAT, ModelTemplates.FLAT_ITEM);


        item.generateFlatItem(VWItemsScatteredPage.SP_ID_1000, VWItems.VERIXIUM_PAPER, ModelTemplates.FLAT_ITEM);
        item.generateFlatItem(VWItemsScatteredPage.SP_ID_1001, VWItems.VERIXIUM_PAPER, ModelTemplates.FLAT_ITEM);
        item.generateFlatItem(VWItemsScatteredPage.SP_ID_1002, VWItems.VERIXIUM_PAPER, ModelTemplates.FLAT_ITEM);
    }

    @Override
    public String getName() {
        return "ModModelProvider";
    }
}