package cliffordha.totvw.registry.creativetab;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.VWBlocks;
import cliffordha.totvw.registry.VWItems;
import cliffordha.totvw.registry.blocks.VWBlocksVerdant;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class VerdantItemsTab extends Item {
    public VerdantItemsTab(Properties properties) {
        super(properties);
    }

    public static final ResourceKey<CreativeModeTab> VERDANT_ITEMS_TAB_KEY = ResourceKey.create(
            BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verdant_items_tab"));

    public static final CreativeModeTab VERDANT_ITEMS_TAB = FabricCreativeModeTab.builder()
            .icon(() -> new ItemStack(VWItems.VERIXIUM_INGOT))
            .title(Component.translatable("creativeTab.verdantItems"))
            .displayItems((params, output) -> {
                output.accept(VWBlocks.VERIXIUM_STONE_ORE);
                output.accept(VWBlocks.VERIXIUM_DEEPSLATE_ORE);
                output.accept(VWBlocks.VERIXIUM_POWDER_BLOCK);
                output.accept(VWBlocksVerdant.VERDANT_MOSS_BLOCK);
                output.accept(VWBlocksVerdant.VERDANT_SPRUCE_LEAVES);
                output.accept(VWBlocksVerdant.VERDANT_SPRUCE_SAPLING);

                output.accept(VWBlocksVerdant.VERDANT_SPRUCE_LOG);
                output.accept(VWBlocksVerdant.VERDANT_SPRUCE_WOOD);
                output.accept(VWBlocksVerdant.STRIPPED_VERDANT_SPRUCE_LOG);
                output.accept(VWBlocksVerdant.STRIPPED_VERDANT_SPRUCE_WOOD);
                output.accept(VWBlocksVerdant.VERDANT_SPRUCE_PLANKS);
                output.accept(VWBlocksVerdant.VERDANT_SPRUCE_SHELF);
                output.accept(VWBlocksVerdant.VERDANT_SPRUCE_STORAGE_BOX);
                output.accept(VWBlocksVerdant.VERDANT_SPRUCE_TRAPDOOR);
                output.accept(VWBlocksVerdant.VERDANT_SPRUCE_DOOR);
                output.accept(VWBlocksVerdant.VERDANT_SPRUCE_SLAB);
                output.accept(VWBlocksVerdant.VERDANT_SPRUCE_STAIRS);
                output.accept(VWBlocksVerdant.VERDANT_SPRUCE_FENCE);
                output.accept(VWBlocksVerdant.VERDANT_SPRUCE_FENCE_GATE);
                output.accept(VWItems.VERDANT_SPRUCE_SIGN);
                output.accept(VWItems.VERDANT_SPRUCE_HANGING_SIGN);
                output.accept(VWBlocksVerdant.VERDANT_SPRUCE_BUTTON);
                output.accept(VWBlocksVerdant.VERDANT_SPRUCE_PRESSURE_PLATE);
                output.accept(VWItems.VERDANT_SPRUCE_BOAT);
                output.accept(VWItems.VERDANT_SPRUCE_CHEST_BOAT);

                output.accept(VWBlocks.IRIDESCENT_GLASS);
                output.accept(VWBlocks.IRIDESCENT_GLASS_PANE);

                output.accept(VWItems.VERIXIUM_CHUNK);
                output.accept(VWItems.CONDENSED_VERIXIUM);
                output.accept(VWItems.VERIXIUM_SHARD);
                output.accept(VWItems.VERIXIUM_POWDER);
                output.accept(VWItems.VERIXIUM_INGOT);
                output.accept(VWItems.VERIXIUM_FLUID_BUCKET);
                output.accept(VWItems.VERIXIUM_PAPER);
                output.accept(VWItems.VERIXIUM_ARMOR_UPGRADE_TEMPLATE);

                output.accept(VWItems.VERIXIUM_HELMET);
                output.accept(VWItems.VERIXIUM_CHESTPLATE);
                output.accept(VWItems.VERIXIUM_LEGGINGS);
                output.accept(VWItems.VERIXIUM_BOOTS);

                output.accept(VWItems.VERIXIUM_WOLF_ARMOR);
                output.accept(VWItems.VERIXIUM_HORSE_ARMOR);

                output.accept(VWItems.VERIXIUM_SPEAR);
                output.accept(VWItems.VERIXIUM_SWORD);
                output.accept(VWItems.VERIXIUM_AXE);
                output.accept(VWItems.VERIXIUM_PICKAXE);
                output.accept(VWItems.VERIXIUM_HOE);
                output.accept(VWItems.VERIXIUM_SHOVEL);
            })
            .build();
}