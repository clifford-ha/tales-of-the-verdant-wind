package cliffordha.totvw.datagen;

import cliffordha.totvw.block.custom.LodestoneWindCoreBlock;
import cliffordha.totvw.block.custom.StorageBlock;
import cliffordha.totvw.registry.VWItems;
import cliffordha.totvw.registry.VWItems.Pages;
import cliffordha.totvw.registry.VWBlocks;
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

    private static TextureMapping lodestoneWindCoreMapping(Block block, String active) {
        return new TextureMapping()
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_side"))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, "_top"))
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(block, "_side"))
                .put(TextureSlot.FRONT, TextureMapping.getBlockTexture(block, active));
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators block) {
        var verdantSpruceFamily = block.family(VWBlocks.VERDANT_SPRUCE_PLANKS);

        block.createTrivialCube(VWBlocks.VERIXIUM_STONE_ORE);
        block.createTrivialCube(VWBlocks.VERIXIUM_DEEPSLATE_ORE);
        block.createColoredBlockWithRandomRotations(TexturedModel.CUBE, VWBlocks.VERDANT_MOSS_BLOCK);
        block.createColoredBlockWithRandomRotations(TexturedModel.CUBE, VWBlocks.VERIXIUM_POWDER_BLOCK);

        block.createGlassBlocks(VWBlocks.IRIDESCENT_GLASS, VWBlocks.IRIDESCENT_GLASS_PANE);

        block.createTrivialBlock(VWBlocks.VERDANT_SPRUCE_LEAVES, TexturedModel.LEAVES);
        block.createPlantWithDefaultItem(VWBlocks.VERDANT_SPRUCE_SAPLING, VWBlocks.POTTED_VERDANT_SPRUCE_SAPLING, BlockModelGenerators.PlantType.TINTED);

        verdantSpruceFamily.generateFor(VWBlocks.VERDANT_SPRUCE_FAMILY);
        block.createHangingSign(VWBlocks.STRIPPED_VERDANT_SPRUCE_WOOD, VWBlocks.VERDANT_SPRUCE_HANGING_SIGN, VWBlocks.VERDANT_SPRUCE_WALL_HANGING_SIGN);
        block.createShelf(VWBlocks.VERDANT_SPRUCE_SHELF, VWBlocks.STRIPPED_VERDANT_SPRUCE_LOG);
        block.woodProvider(VWBlocks.VERDANT_SPRUCE_LOG).log(VWBlocks.VERDANT_SPRUCE_LOG).wood(VWBlocks.VERDANT_SPRUCE_WOOD);
        block.woodProvider(VWBlocks.STRIPPED_VERDANT_SPRUCE_LOG).log(VWBlocks.STRIPPED_VERDANT_SPRUCE_LOG).wood(VWBlocks.STRIPPED_VERDANT_SPRUCE_WOOD);

        MultiVariant OPEN = BlockModelGenerators.plainVariant(
                ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(VWBlocks.VERDANT_SPRUCE_STORAGE_BOX, "_open",
                        storageBoxTextureMapping(VWBlocks.VERDANT_SPRUCE_STORAGE_BOX, "_top_open"), block.modelOutput)
        );
        MultiVariant CLOSED = BlockModelGenerators.plainVariant(
                ModelTemplates.CUBE_BOTTOM_TOP.create(VWBlocks.VERDANT_SPRUCE_STORAGE_BOX,
                        storageBoxTextureMapping(VWBlocks.VERDANT_SPRUCE_STORAGE_BOX, "_top"), block.modelOutput)
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
                .with(BlockModelGenerators.createBooleanModelDispatch(LodestoneWindCoreBlock.ACTIVE, LODESTONE_WIND_CORE_ACTIVE, LODESTONE_WIND_CORE))
                .with(PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING)
                        .select(Direction.NORTH, BlockModelGenerators.NOP)
                        .select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180)
                        .select(Direction.WEST,  BlockModelGenerators.Y_ROT_270)
                        .select(Direction.EAST,  BlockModelGenerators.Y_ROT_90)
                ));

        block.blockStateOutput.accept(MultiVariantGenerator.dispatch(VWBlocks.VERDANT_SPRUCE_STORAGE_BOX)
                .with(BlockModelGenerators.createBooleanModelDispatch(StorageBlock.OPEN, OPEN, CLOSED))
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
                VWItems.VERDANT_SPRUCE_CHEST_BOAT,

                VWItems.SOUL_RUNESTONE_PLATE,
                VWItems.SOUL_RUNESTONE_FRAGMENT_1,
                VWItems.SOUL_RUNESTONE_FRAGMENT_2,
                VWItems.SOUL_RUNESTONE_FRAGMENT_3,
                VWItems.SOUL_RUNESTONE_FRAGMENT_4
        );

        add(item, ModelTemplates.FLAT_HANDHELD_ITEM,
                VWItems.VERIXIUM_SWORD,
                VWItems.VERIXIUM_AXE,
                VWItems.VERIXIUM_PICKAXE,
                VWItems.VERIXIUM_SHOVEL,
                VWItems.VERIXIUM_HOE
        );
        item.generateSpear(VWItems.VERIXIUM_SPEAR);

        item.generateFlatItem(Pages.SCATTERED_PAGE, ModelTemplates.FLAT_ITEM);
        item.generateFlatItem(Pages.SCATTERED_PAGE_VARIANT_1, ModelTemplates.FLAT_ITEM);
        item.generateFlatItem(Pages.SCATTERED_PAGE_VARIANT_2, ModelTemplates.FLAT_ITEM);
        item.generateFlatItem(Pages.SCATTERED_PAGE_VARIANT_3, ModelTemplates.FLAT_ITEM);

        item.generateFlatItem(Pages.OLD_SCATTERED_PAGE, ModelTemplates.FLAT_ITEM);
        item.generateFlatItem(Pages.OLD_SCATTERED_PAGE_VARIANT_1, ModelTemplates.FLAT_ITEM);
        item.generateFlatItem(Pages.OLD_SCATTERED_PAGE_VARIANT_2, ModelTemplates.FLAT_ITEM);
        item.generateFlatItem(Pages.OLD_SCATTERED_PAGE_VARIANT_3, ModelTemplates.FLAT_ITEM);

        item.generateFlatItem(Pages.ENCHANTMENTS_HANDBOOK, ModelTemplates.FLAT_ITEM);
        addCopy(item, ModelTemplates.FLAT_ITEM, Pages.ENCHANTMENTS_HANDBOOK,
                Pages.EFFECTS_HANDBOOK,
                Pages.ITEMS_HANDBOOK
        );

        addCopy(item, ModelTemplates.FLAT_ITEM, Pages.SCATTERED_PAGE,
                Pages.PLAYER_STATS,
                Pages.LODESTONE_WIND_CORE_MANUAL,

                Pages.SP_ID_1001,
                Pages.SP_ID_1002,
                Pages.SP_ID_1003,
                Pages.SP_ID_1004,

                Pages.SP_ID_1005

        );

        addCopy(item, ModelTemplates.FLAT_ITEM, Pages.OLD_SCATTERED_PAGE,
                Pages.SP_ID_1000,

                Pages.SP_ID_1006,

                Pages.SP_ID_1007,
                Pages.SP_ID_1008,
                Pages.SP_ID_1009,

                Pages.SP_ID_TEST
        );
    }

    @Override
    public String getName() {
        return "VWModelProvider";
    }
}