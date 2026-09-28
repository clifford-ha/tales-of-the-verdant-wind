package cliffordha.totvw.tag;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.VWBlocks;
import cliffordha.totvw.registry.VWItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

import static cliffordha.totvw.tag.VWTagHelpers.item;

public class VWItemTags extends FabricTagsProvider.ItemTagsProvider {
    public VWItemTags(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider wrapperLookup) {
        getOrCreateRawBuilder(VERDANT_ITEMS)
                .add(item(VWItems.VERIXIUM_CHUNK))
                .add(item(VWItems.CONDENSED_VERIXIUM))
                .add(item(VWItems.VERIXIUM_SHARD))
                .add(item(VWItems.VERIXIUM_POWDER))
                .add(item(VWItems.VERIXIUM_INGOT))
                .add(item(VWItems.VERIXIUM_PAPER))
                .add(item(VWItems.VERIXIUM_FLUID_BUCKET))
                .add(item(VWItems.VERIXIUM_ARMOR_UPGRADE_TEMPLATE))

                .addTag(VERIXIUM_ARMOR.location())
                .addTag(VERIXIUM_WEAPON.location())
                .addTag(VERIXIUM_TOOL.location());

        getOrCreateRawBuilder(VERIXIUM_ARMOR)
                .add(item(VWItems.VERIXIUM_WOLF_ARMOR))
                .add(item(VWItems.VERIXIUM_HELMET))
                .add(item(VWItems.VERIXIUM_CHESTPLATE))
                .add(item(VWItems.VERIXIUM_LEGGINGS))
                .add(item(VWItems.VERIXIUM_BOOTS));

        getOrCreateRawBuilder(VERIXIUM_WEAPON)
                .add(item(VWItems.VERIXIUM_SPEAR))
                .add(item(VWItems.VERIXIUM_SWORD))
                .add(item(VWItems.VERIXIUM_AXE));

        getOrCreateRawBuilder(VERIXIUM_TOOL)
                .add(item(VWItems.VERIXIUM_PICKAXE))
                .add(item(VWItems.VERIXIUM_HOE))
                .add(item(VWItems.VERIXIUM_SHOVEL));

        getOrCreateRawBuilder(REPAIRS_VERIXIUM)
                .add(item(VWItems.VERIXIUM_POWDER));

        getOrCreateRawBuilder(WOLF_ARMOR_ENCHANTABLE)
                .add(item(Items.WOLF_ARMOR))
                .add(item(VWItems.VERIXIUM_WOLF_ARMOR));

        getOrCreateRawBuilder(VERDANT_SPRUCE_LOGS)
                .add(item(VWBlocks.VERDANT_SPRUCE_LOG.asItem()))
                .add(item(VWBlocks.VERDANT_SPRUCE_WOOD.asItem()))
                .add(item(VWBlocks.STRIPPED_VERDANT_SPRUCE_LOG.asItem()))
                .add(item(VWBlocks.STRIPPED_VERDANT_SPRUCE_WOOD.asItem()));

        getOrCreateRawBuilder(BENEDICTION_ENCHANTMENT_USE_QUALIFIED_TOOLS)
                .addTag(ItemTags.SWORDS.location())
                .addTag(ItemTags.AXES.location())
                .addTag(ItemTags.PICKAXES.location())
                .addTag(ItemTags.SHOVELS.location())
                .addTag(ItemTags.HOES.location());

        getOrCreateRawBuilder(BENEDICTION_ENCHANTMENT_USE_QUALIFIED_ITEMS)
                .add(item(Items.GLOWSTONE_DUST))
                .add(item(VWItems.VERIXIUM_POWDER));

        getOrCreateRawBuilder(SCATTERED_PAGES)
                .add(item(Items.PAPER))
                .add(item(VWItems.VERIXIUM_PAPER));

        getOrCreateRawBuilder(LODESTONE_WIND_CORE_ENERGY_SOURCES)
                .add(item(VWItems.VERIXIUM_POWDER))
                .add(item(VWBlocks.VERIXIUM_POWDER_BLOCK.asItem()))
                .add(item(Items.WIND_CHARGE));

        getOrCreateRawBuilder(RUNESTONE_PLATES)
                .add(item(VWItems.GENESIS_RUNESTONE_PLATE))
                .add(item(VWItems.TETHER_RUNESTONE_PLATE))
                .add(item(VWItems.HAVOC_RUNESTONE_PLATE))
                .add(item(VWItems.SOUL_RUNESTONE_PLATE))
                .add(item(VWItems.EFFLORESCENCE_RUNESTONE_PLATE))
        ;



        getOrCreateRawBuilder(ItemTags.LOGS_THAT_BURN)
                .addTag(VERDANT_SPRUCE_LOGS.location());

        getOrCreateRawBuilder(ItemTags.PLANKS)
                .add(item(VWBlocks.VERDANT_SPRUCE_PLANKS.asItem()));

        getOrCreateRawBuilder(ItemTags.ARMOR_ENCHANTABLE)
                .addTag(WOLF_ARMOR_ENCHANTABLE.location());

        getOrCreateRawBuilder(ItemTags.HEAD_ARMOR)
                .add(item(VWItems.VERIXIUM_HELMET));

        getOrCreateRawBuilder(ItemTags.CHEST_ARMOR)
                .add(item(VWItems.VERIXIUM_CHESTPLATE))
                .addTag(WOLF_ARMOR_ENCHANTABLE.location());

        getOrCreateRawBuilder(ItemTags.LEG_ARMOR)
                .add(item(VWItems.VERIXIUM_LEGGINGS));

        getOrCreateRawBuilder(ItemTags.FOOT_ARMOR)
                .add(item(VWItems.VERIXIUM_BOOTS));

        getOrCreateRawBuilder(ItemTags.SPEARS)
                .add(item(VWItems.VERIXIUM_SPEAR));

        getOrCreateRawBuilder(ItemTags.SWORDS)
                .add(item(VWItems.VERIXIUM_SWORD));

        getOrCreateRawBuilder(ItemTags.AXES)
                .add(item(VWItems.VERIXIUM_AXE));

        getOrCreateRawBuilder(ItemTags.PICKAXES)
                .add(item(VWItems.VERIXIUM_PICKAXE));

        getOrCreateRawBuilder(ItemTags.HOES)
                .add(item(VWItems.VERIXIUM_HOE));

        getOrCreateRawBuilder(ItemTags.SHOVELS)
                .add(item(VWItems.VERIXIUM_SHOVEL));

        getOrCreateRawBuilder(ItemTags.TRIMMABLE_ARMOR)
                .addTag(VERIXIUM_ARMOR.location());

        getOrCreateRawBuilder(ItemTags.REPAIRS_DIAMOND_ARMOR)
                .add(item(VWItems.VERIXIUM_POWDER));

        getOrCreateRawBuilder(ItemTags.LUNGE_ENCHANTABLE)
                .add(item(VWItems.VERIXIUM_SPEAR));

        getOrCreateRawBuilder(ItemTags.WOODEN_DOORS)
                .add(item(VWBlocks.VERDANT_SPRUCE_DOOR.asItem()));
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
    public static final TagKey<Item> RUNESTONE_PLATES = create("runestone_plates");

    private static TagKey<Item> create(String name) {
        return TagKey.create(Registries.ITEM, TOTVW.registerID(name)); }
}