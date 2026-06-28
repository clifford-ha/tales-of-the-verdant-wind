package cliffordha.totvw.registry.creativetab;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.ModBlocks;
import cliffordha.totvw.registry.blocks.VerdantBlocks;
import cliffordha.totvw.registry.ModItems;
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
            .icon(() -> new ItemStack(ModItems.VERIXIUM_INGOT))
            .title(Component.translatable("creativeTab.verdantItems"))
            .displayItems((params, output) -> {
                output.accept(ModBlocks.VERIXIUM_STONE_ORE);
                output.accept(ModBlocks.VERIXIUM_DEEPSLATE_ORE);
                output.accept(ModBlocks.VERIXIUM_POWDER_BLOCK);
                output.accept(VerdantBlocks.VERDANT_MOSS_BLOCK);
                output.accept(VerdantBlocks.VERDANT_SPRUCE_LEAVES);
                output.accept(VerdantBlocks.VERDANT_SPRUCE_SAPLING);

                output.accept(VerdantBlocks.VERDANT_SPRUCE_LOG);
                output.accept(VerdantBlocks.VERDANT_SPRUCE_WOOD);
                output.accept(VerdantBlocks.STRIPPED_VERDANT_SPRUCE_LOG);
                output.accept(VerdantBlocks.STRIPPED_VERDANT_SPRUCE_WOOD);
                output.accept(VerdantBlocks.VERDANT_SPRUCE_PLANKS);
                output.accept(VerdantBlocks.VERDANT_SPRUCE_SHELF);
                output.accept(VerdantBlocks.VERDANT_STORAGE_BOX);
                output.accept(VerdantBlocks.VERDANT_SPRUCE_TRAPDOOR);
                output.accept(VerdantBlocks.VERDANT_SPRUCE_DOOR);
                output.accept(VerdantBlocks.VERDANT_SPRUCE_SLAB);
                output.accept(VerdantBlocks.VERDANT_SPRUCE_STAIRS);
                output.accept(VerdantBlocks.VERDANT_SPRUCE_FENCE);
                output.accept(VerdantBlocks.VERDANT_SPRUCE_FENCE_GATE);
                output.accept(ModItems.VERDANT_SPRUCE_SIGN);
                output.accept(ModItems.VERDANT_SPRUCE_HANGING_SIGN);
                output.accept(VerdantBlocks.VERDANT_SPRUCE_BUTTON);
                output.accept(VerdantBlocks.VERDANT_SPRUCE_PRESSURE_PLATE);
                output.accept(ModItems.VERDANT_SPRUCE_BOAT);
                output.accept(ModItems.VERDANT_SPRUCE_CHEST_BOAT);

                output.accept(ModBlocks.IRIDESCENT_GLASS);
                output.accept(ModBlocks.IRIDESCENT_GLASS_PANE);

                output.accept(ModItems.VERIXIUM_CHUNK);
                output.accept(ModItems.CONDENSED_VERIXIUM);
                output.accept(ModItems.VERIXIUM_SHARD);
                output.accept(ModItems.VERIXIUM_POWDER);
                output.accept(ModItems.VERIXIUM_INGOT);
                output.accept(ModItems.VERIXIUM_FLUID_BUCKET);
                output.accept(ModItems.VERIXIUM_PAPER);
                output.accept(ModItems.VERIXIUM_ARMOR_UPGRADE_TEMPLATE);

                output.accept(ModItems.VERIXIUM_HELMET);
                output.accept(ModItems.VERIXIUM_CHESTPLATE);
                output.accept(ModItems.VERIXIUM_LEGGINGS);
                output.accept(ModItems.VERIXIUM_BOOTS);

                output.accept(ModItems.VERIXIUM_WOLF_ARMOR);
                output.accept(ModItems.VERIXIUM_HORSE_ARMOR);

                output.accept(ModItems.VERIXIUM_SPEAR);
                output.accept(ModItems.VERIXIUM_SWORD);
                output.accept(ModItems.VERIXIUM_AXE);
                output.accept(ModItems.VERIXIUM_PICKAXE);
                output.accept(ModItems.VERIXIUM_HOE);
                output.accept(ModItems.VERIXIUM_SHOVEL);
            })
            .build();
}