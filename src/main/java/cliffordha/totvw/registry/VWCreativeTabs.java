package cliffordha.totvw.registry;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.VWItems.Pages;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class VWCreativeTabs {
    public static final ResourceKey<CreativeModeTab> TOTVW_ITEMS_TAB_KEY = ResourceKey.create(
            BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "items_tab"));

    public static final ResourceKey<CreativeModeTab> TOTVW_SCATTERED_PAGES_TAB_KEY = ResourceKey.create(
            BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "scattered_pages_tab"));

    public static final CreativeModeTab TOTVW_ITEMS_TAB = FabricCreativeModeTab.builder()
            .icon(() -> new ItemStack(VWItems.VERIXIUM_CHUNK))
            .title(Component.literal(TOTVW.MOD_NAME_LONG).withColor(VWColors.VERDANT_WIND))
            .displayItems((params, output) -> {
                output.accept(VWBlocks.VERIXIUM_STONE_ORE);
                output.accept(VWBlocks.VERIXIUM_DEEPSLATE_ORE);
                output.accept(VWBlocks.VERIXIUM_POWDER_BLOCK);
                output.accept(VWBlocks.VERDANT_MOSS_BLOCK);
                output.accept(VWBlocks.VERDANT_SPRUCE_LEAVES);
                output.accept(VWBlocks.VERDANT_SPRUCE_SAPLING);

                output.accept(VWBlocks.VERDANT_SPRUCE_LOG);
                output.accept(VWBlocks.VERDANT_SPRUCE_WOOD);
                output.accept(VWBlocks.STRIPPED_VERDANT_SPRUCE_LOG);
                output.accept(VWBlocks.STRIPPED_VERDANT_SPRUCE_WOOD);
                output.accept(VWBlocks.VERDANT_SPRUCE_PLANKS);
                output.accept(VWBlocks.VERDANT_SPRUCE_SHELF);
                output.accept(VWBlocks.VERDANT_SPRUCE_STORAGE_BOX);
                output.accept(VWBlocks.LODESTONE_WIND_CORE);
                output.accept(VWBlocks.VERDANT_SPRUCE_TRAPDOOR);
                output.accept(VWBlocks.VERDANT_SPRUCE_DOOR);
                output.accept(VWBlocks.VERDANT_SPRUCE_SLAB);
                output.accept(VWBlocks.VERDANT_SPRUCE_STAIRS);
                output.accept(VWBlocks.VERDANT_SPRUCE_FENCE);
                output.accept(VWBlocks.VERDANT_SPRUCE_FENCE_GATE);
                output.accept(VWItems.VERDANT_SPRUCE_SIGN);
                output.accept(VWItems.VERDANT_SPRUCE_HANGING_SIGN);
                output.accept(VWBlocks.VERDANT_SPRUCE_BUTTON);
                output.accept(VWBlocks.VERDANT_SPRUCE_PRESSURE_PLATE);
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
                output.accept(VWItems.SOUL_RUNESTONE_PLATE);

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

                output.accept(VWItems.SOUL_RUNESTONE_FRAGMENT_1);
                output.accept(VWItems.SOUL_RUNESTONE_FRAGMENT_2);
                output.accept(VWItems.SOUL_RUNESTONE_FRAGMENT_3);
                output.accept(VWItems.SOUL_RUNESTONE_FRAGMENT_4);
            }).build();

    public static final CreativeModeTab TOTVW_SCATTERED_PAGES_TAB = FabricCreativeModeTab.builder()
            .icon(() -> new ItemStack(Pages.OLD_SCATTERED_PAGE))
            .title(Component.literal("Scattered Pages"))
            .displayItems((params, output) -> {
                if (TOTVW.IN_DEVELOPMENT) {
                    output.accept(Pages.SCATTERED_PAGE);
                    output.accept(Pages.OLD_SCATTERED_PAGE);

                    output.accept(Pages.PLAYER_STATS);
                    output.accept(Pages.SP_ID_TEST);
                    output.accept(Pages.SP_ID_1000);
                }
                output.accept(Pages.ENCHANTMENTS_HANDBOOK);
                output.accept(Pages.EFFECTS_HANDBOOK);
                output.accept(Pages.ITEMS_HANDBOOK);
                output.accept(Pages.FEATURES_HANDBOOK);

                output.accept(Pages.SP_ID_1001);
                output.accept(Pages.SP_ID_1002);
                output.accept(Pages.SP_ID_1003);
                output.accept(Pages.SP_ID_1004);
                output.accept(Pages.SP_ID_1005);
                output.accept(Pages.SP_ID_1006);

                output.accept(Pages.LODESTONE_WIND_CORE_MANUAL);

            }).build();


    public static void register() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, VWCreativeTabs.TOTVW_ITEMS_TAB_KEY, VWCreativeTabs.TOTVW_ITEMS_TAB);
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, VWCreativeTabs.TOTVW_SCATTERED_PAGES_TAB_KEY, VWCreativeTabs.TOTVW_SCATTERED_PAGES_TAB);
    }
}
