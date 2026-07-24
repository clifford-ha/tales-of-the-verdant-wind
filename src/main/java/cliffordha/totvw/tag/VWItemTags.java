package cliffordha.totvw.tag;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.VWBlocks;
import cliffordha.totvw.registry.VWItems;
import cliffordha.totvw.registry.blocks.VWBlocksVerdant;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class VWItemTags extends FabricTagsProvider.ItemTagsProvider {
    public VWItemTags(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider wrapperLookup) {
        valueLookupBuilder(VERDANT_ITEMS)
                .add(VWItems.VERIXIUM_CHUNK)
                .add(VWItems.CONDENSED_VERIXIUM)
                .add(VWItems.VERIXIUM_SHARD)
                .add(VWItems.VERIXIUM_POWDER)
                .add(VWItems.VERIXIUM_INGOT)
                .add(VWItems.VERIXIUM_PAPER)
                .add(VWItems.VERIXIUM_FLUID_BUCKET)
                .add(VWItems.VERIXIUM_ARMOR_UPGRADE_TEMPLATE)

                .addTag(VERIXIUM_ARMOR)
                .addTag(VERIXIUM_WEAPON)
                .addTag(VERIXIUM_TOOL);

        valueLookupBuilder(VERIXIUM_ARMOR)
                .add(VWItems.VERIXIUM_WOLF_ARMOR)
                .add(VWItems.VERIXIUM_HELMET)
                .add(VWItems.VERIXIUM_CHESTPLATE)
                .add(VWItems.VERIXIUM_LEGGINGS)
                .add(VWItems.VERIXIUM_BOOTS);

        valueLookupBuilder(VERIXIUM_WEAPON)
                .add(VWItems.VERIXIUM_SPEAR)
                .add(VWItems.VERIXIUM_SWORD)
                .add(VWItems.VERIXIUM_AXE);

        valueLookupBuilder(VERIXIUM_TOOL)
                .add(VWItems.VERIXIUM_PICKAXE)
                .add(VWItems.VERIXIUM_HOE)
                .add(VWItems.VERIXIUM_SHOVEL);

        valueLookupBuilder(REPAIRS_VERIXIUM)
                .add(VWItems.VERIXIUM_POWDER);

        valueLookupBuilder(WOLF_ARMOR_ENCHANTABLE)
                .add(Items.WOLF_ARMOR)
                .add(VWItems.VERIXIUM_WOLF_ARMOR);

        valueLookupBuilder(VWItemTags.VERDANT_SPRUCE_LOGS)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_LOG.asItem())
                .add(VWBlocksVerdant.VERDANT_SPRUCE_WOOD.asItem())
                .add(VWBlocksVerdant.STRIPPED_VERDANT_SPRUCE_LOG.asItem())
                .add(VWBlocksVerdant.STRIPPED_VERDANT_SPRUCE_WOOD.asItem());

        valueLookupBuilder(ItemTags.LOGS_THAT_BURN)
                .addTag(VWItemTags.VERDANT_SPRUCE_LOGS);

        valueLookupBuilder(ItemTags.PLANKS)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_PLANKS.asItem());

        valueLookupBuilder(VWItemTags.BENEDICTION_ENCHANTMENT_USE_QUALIFIED_TOOLS)
                .addTag(ItemTags.SWORDS)
                .addTag(ItemTags.AXES)
                .addTag(ItemTags.PICKAXES)
                .addTag(ItemTags.SHOVELS)
                .addTag(ItemTags.HOES);

        valueLookupBuilder(VWItemTags.BENEDICTION_ENCHANTMENT_USE_QUALIFIED_ITEMS)
                .add(Items.GLOWSTONE_DUST)
                .add(VWItems.VERIXIUM_POWDER);

        valueLookupBuilder(VWItemTags.SCATTERED_PAGES)
                .add(Items.PAPER)
                .add(VWItems.VERIXIUM_PAPER);

        valueLookupBuilder(VWItemTags.LODESTONE_WIND_CORE_ENERGY_SOURCES)
                .add(VWItems.VERIXIUM_POWDER)
                .add(VWBlocks.VERIXIUM_POWDER_BLOCK.asItem())
                .add(Items.WIND_CHARGE);



        valueLookupBuilder(ItemTags.ARMOR_ENCHANTABLE)
                .addTag(VWItemTags.WOLF_ARMOR_ENCHANTABLE);

        valueLookupBuilder(ItemTags.HEAD_ARMOR)
                .add(VWItems.VERIXIUM_HELMET);

        valueLookupBuilder(ItemTags.CHEST_ARMOR)
                .add(VWItems.VERIXIUM_CHESTPLATE)
                .addTag(WOLF_ARMOR_ENCHANTABLE);

        valueLookupBuilder(ItemTags.LEG_ARMOR)
                .add(VWItems.VERIXIUM_LEGGINGS);

        valueLookupBuilder(ItemTags.FOOT_ARMOR)
                .add(VWItems.VERIXIUM_BOOTS);

        valueLookupBuilder(ItemTags.SPEARS)
                .add(VWItems.VERIXIUM_SPEAR);

        valueLookupBuilder(ItemTags.SWORDS)
                .add(VWItems.VERIXIUM_SWORD);

        valueLookupBuilder(ItemTags.AXES)
                .add(VWItems.VERIXIUM_AXE);

        valueLookupBuilder(ItemTags.PICKAXES)
                .add(VWItems.VERIXIUM_PICKAXE);

        valueLookupBuilder(ItemTags.HOES)
                .add(VWItems.VERIXIUM_HOE);

        valueLookupBuilder(ItemTags.SHOVELS)
                .add(VWItems.VERIXIUM_SHOVEL);

        valueLookupBuilder(ItemTags.TRIMMABLE_ARMOR)
                .addTag(VERIXIUM_ARMOR);

        valueLookupBuilder(ItemTags.REPAIRS_DIAMOND_ARMOR)
                .add(VWItems.VERIXIUM_POWDER);

        valueLookupBuilder(ItemTags.LUNGE_ENCHANTABLE)
                .add(VWItems.VERIXIUM_SPEAR);

        valueLookupBuilder(ItemTags.DOORS)
                .add(VWBlocksVerdant.VERDANT_SPRUCE_DOOR.asItem());
    }

    public static final TagKey<Item> VERDANT_ITEMS = create("verdant_items");
    public static final TagKey<Item> VERDANT_SPRUCE_LOGS = create("verdant_spruce_logs");

    public static final TagKey<Item> REPAIRS_VERIXIUM = create("repairs_verixium");
    public static final TagKey<Item> VERIXIUM_ARMOR = create("verixium_armor");
    public static final TagKey<Item> VERIXIUM_WEAPON = create("verixium_weapon");
    public static final TagKey<Item> VERIXIUM_TOOL = create("verixium_tool");

    public static final TagKey<Item> WOLF_ARMOR_ENCHANTABLE = create("wolf_armor_enchantable");

    public static final TagKey<Item> BENEDICTION_ENCHANTMENT_USE_QUALIFIED_ITEMS = create("benediction_enchantment_use_qualified_items");
    public static final TagKey<Item> BENEDICTION_ENCHANTMENT_USE_QUALIFIED_TOOLS = create("benediction_enchantment_use_qualified_tools");
    public static final TagKey<Item> LODESTONE_WIND_CORE_ENERGY_SOURCES = create("lodestone_wind_core_energy_sources");
    public static final TagKey<Item> SCATTERED_PAGES = create("scattered_pages");

    private static TagKey<Item> create(String name) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name)); }
}