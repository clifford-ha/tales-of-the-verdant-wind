package cliffordha.totvw.datagen;

import cliffordha.totvw.registry.blocks.VerdantBlocks;
import cliffordha.totvw.registry.ModBlocks;
import cliffordha.totvw.registry.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TexturedModel;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators block) {
        var verdantSpruceFamily = block.family(VerdantBlocks.VERDANT_SPRUCE_PLANKS);

        block.createTrivialCube(ModBlocks.VERIXIUM_STONE_ORE);
        block.createTrivialCube(ModBlocks.VERIXIUM_DEEPSLATE_ORE);
        block.createColoredBlockWithRandomRotations(TexturedModel.CUBE, ModBlocks.VERIXIUM_POWDER_BLOCK);

        block.createGlassBlocks(ModBlocks.IRIDESCENT_GLASS, ModBlocks.IRIDESCENT_GLASS_PANE);

        block.createColoredBlockWithRandomRotations(TexturedModel.CUBE, VerdantBlocks.VERDANT_MOSS_BLOCK);
        block.createTrivialBlock(VerdantBlocks.VERDANT_SPRUCE_LEAVES, TexturedModel.LEAVES);
        block.createPlantWithDefaultItem(VerdantBlocks.VERDANT_SPRUCE_SAPLING, VerdantBlocks.POTTED_VERDANT_SPRUCE_SAPLING, BlockModelGenerators.PlantType.TINTED);

        verdantSpruceFamily.generateFor(VerdantBlocks.VERDANT_SPRUCE_FAMILY);
        block.createHangingSign(VerdantBlocks.STRIPPED_VERDANT_SPRUCE_WOOD, VerdantBlocks.VERDANT_SPRUCE_HANGING_SIGN, VerdantBlocks.VERDANT_SPRUCE_WALL_HANGING_SIGN);
        block.createShelf(VerdantBlocks.VERDANT_SPRUCE_SHELF, VerdantBlocks.STRIPPED_VERDANT_SPRUCE_LOG);
        block.woodProvider(VerdantBlocks.VERDANT_SPRUCE_LOG).log(VerdantBlocks.VERDANT_SPRUCE_LOG).wood(VerdantBlocks.VERDANT_SPRUCE_WOOD);
        block.woodProvider(VerdantBlocks.STRIPPED_VERDANT_SPRUCE_LOG).log(VerdantBlocks.STRIPPED_VERDANT_SPRUCE_LOG).wood(VerdantBlocks.STRIPPED_VERDANT_SPRUCE_WOOD);
    }

    @Override
    public void generateItemModels(ItemModelGenerators item) {
        item.generateFlatItem(ModItems.VERIXIUM_CHUNK, ModelTemplates.FLAT_ITEM);
        item.generateFlatItem(ModItems.CONDENSED_VERIXIUM, ModelTemplates.FLAT_ITEM);
        item.generateFlatItem(ModItems.VERIXIUM_SHARD, ModelTemplates.FLAT_ITEM);
        item.generateFlatItem(ModItems.VERIXIUM_POWDER, ModelTemplates.FLAT_ITEM);
        item.generateFlatItem(ModItems.VERIXIUM_INGOT, ModelTemplates.FLAT_ITEM);
        item.generateFlatItem(ModItems.VERIXIUM_PAPER, ModelTemplates.FLAT_ITEM);
        item.generateFlatItem(ModItems.VERIXIUM_ARMOR_UPGRADE_TEMPLATE, ModelTemplates.FLAT_ITEM);

        item.generateFlatItem(ModItems.VERIXIUM_HELMET, ModelTemplates.FLAT_ITEM);
        item.generateFlatItem(ModItems.VERIXIUM_CHESTPLATE, ModelTemplates.FLAT_ITEM);
        item.generateFlatItem(ModItems.VERIXIUM_LEGGINGS, ModelTemplates.FLAT_ITEM);
        item.generateFlatItem(ModItems.VERIXIUM_BOOTS, ModelTemplates.FLAT_ITEM);

        item.generateFlatItem(ModItems.VERIXIUM_WOLF_ARMOR, ModelTemplates.FLAT_ITEM);
        item.generateFlatItem(ModItems.VERIXIUM_HORSE_ARMOR, ModelTemplates.FLAT_HANDHELD_ITEM);

        item.generateFlatItem(ModItems.VERIXIUM_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
        item.generateFlatItem(ModItems.VERIXIUM_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        item.generateFlatItem(ModItems.VERIXIUM_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        item.generateFlatItem(ModItems.VERIXIUM_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
        item.generateFlatItem(ModItems.VERIXIUM_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);

        item.generateFlatItem(ModItems.VERIXIUM_FLUID_BUCKET, ModelTemplates.FLAT_ITEM);

        item.generateFlatItem(ModItems.VERDANT_SPRUCE_BOAT, ModelTemplates.FLAT_ITEM);
        item.generateFlatItem(ModItems.VERDANT_SPRUCE_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
    }

    @Override
    public String getName() {
        return "ModModelProvider";
    }
}