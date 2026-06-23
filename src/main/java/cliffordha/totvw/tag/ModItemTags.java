package cliffordha.totvw.tag;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.blocks.VerdantBlocks;
import cliffordha.totvw.registry.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class ModItemTags extends FabricTagsProvider.ItemTagsProvider {
    public ModItemTags(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider wrapperLookup) {
        valueLookupBuilder(VERDANT_ITEMS)
                .add(ModItems.VERIXIUM_CHUNK)
                .add(ModItems.CONDENSED_VERIXIUM)
                .add(ModItems.VERIXIUM_SHARD)
                .add(ModItems.VERIXIUM_POWDER)
                .add(ModItems.VERIXIUM_INGOT)
                .add(ModItems.VERIXIUM_PAPER)
                .add(ModItems.VERIXIUM_FLUID_BUCKET)
                .add(ModItems.VERIXIUM_ARMOR_UPGRADE_TEMPLATE)

                .addTag(VERIXIUM_ARMOR)
                .addTag(VERIXIUM_WEAPON)
                .addTag(VERIXIUM_TOOL);

        valueLookupBuilder(VERIXIUM_ARMOR)
                .add(ModItems.VERIXIUM_WOLF_ARMOR)
                .add(ModItems.VERIXIUM_HELMET)
                .add(ModItems.VERIXIUM_CHESTPLATE)
                .add(ModItems.VERIXIUM_LEGGINGS)
                .add(ModItems.VERIXIUM_BOOTS);

        valueLookupBuilder(VERIXIUM_WEAPON)
                .add(ModItems.VERIXIUM_SPEAR)
                .add(ModItems.VERIXIUM_SWORD)
                .add(ModItems.VERIXIUM_AXE);

        valueLookupBuilder(VERIXIUM_TOOL)
                .add(ModItems.VERIXIUM_PICKAXE)
                .add(ModItems.VERIXIUM_HOE)
                .add(ModItems.VERIXIUM_SHOVEL);

        valueLookupBuilder(REPAIRS_VERIXIUM)
                .add(ModItems.VERIXIUM_POWDER);

        valueLookupBuilder(WOLF_ARMOR_ENCHANTABLE)
                .add(Items.WOLF_ARMOR)
                .add(ModItems.VERIXIUM_WOLF_ARMOR);

        valueLookupBuilder(ModItemTags.VERDANT_SPRUCE_LOGS)
                .add(VerdantBlocks.VERDANT_SPRUCE_LOG.asItem())
                .add(VerdantBlocks.VERDANT_SPRUCE_WOOD.asItem())
                .add(VerdantBlocks.STRIPPED_VERDANT_SPRUCE_LOG.asItem())
                .add(VerdantBlocks.STRIPPED_VERDANT_SPRUCE_WOOD.asItem());

        valueLookupBuilder(ItemTags.LOGS_THAT_BURN)
                .addTag(ModItemTags.VERDANT_SPRUCE_LOGS);

        valueLookupBuilder(ItemTags.PLANKS)
                .add(VerdantBlocks.VERDANT_SPRUCE_PLANKS.asItem());

        valueLookupBuilder(ModItemTags.BENEDICTION_ENCHANTMENT_USE_QUALIFIED_TOOLS)
                .addTag(ItemTags.SWORDS)
                .addTag(ItemTags.AXES)
                .addTag(ItemTags.PICKAXES)
                .addTag(ItemTags.SHOVELS)
                .addTag(ItemTags.HOES);

        valueLookupBuilder(ModItemTags.BENEDICTION_ENCHANTMENT_USE_QUALIFIED_ITEMS)
                .add(Items.GLOWSTONE_DUST)
                .add(ModItems.VERIXIUM_POWDER);



        valueLookupBuilder(ItemTags.ARMOR_ENCHANTABLE)
                .addTag(ModItemTags.WOLF_ARMOR_ENCHANTABLE);

        valueLookupBuilder(ItemTags.HEAD_ARMOR)
                .add(ModItems.VERIXIUM_HELMET);

        valueLookupBuilder(ItemTags.CHEST_ARMOR)
                .add(ModItems.VERIXIUM_CHESTPLATE)
                .addTag(WOLF_ARMOR_ENCHANTABLE);

        valueLookupBuilder(ItemTags.LEG_ARMOR)
                .add(ModItems.VERIXIUM_LEGGINGS);

        valueLookupBuilder(ItemTags.FOOT_ARMOR)
                .add(ModItems.VERIXIUM_BOOTS);

        valueLookupBuilder(ItemTags.SPEARS)
                .add(ModItems.VERIXIUM_SPEAR);

        valueLookupBuilder(ItemTags.SWORDS)
                .add(ModItems.VERIXIUM_SWORD);

        valueLookupBuilder(ItemTags.AXES)
                .add(ModItems.VERIXIUM_AXE);

        valueLookupBuilder(ItemTags.PICKAXES)
                .add(ModItems.VERIXIUM_PICKAXE);

        valueLookupBuilder(ItemTags.HOES)
                .add(ModItems.VERIXIUM_HOE);

        valueLookupBuilder(ItemTags.SHOVELS)
                .add(ModItems.VERIXIUM_SHOVEL);

        valueLookupBuilder(ItemTags.TRIMMABLE_ARMOR)
                .addTag(VERIXIUM_ARMOR);

        valueLookupBuilder(ItemTags.REPAIRS_DIAMOND_ARMOR)
                .add(ModItems.VERIXIUM_POWDER);
    }

    public static final TagKey<Item> VERDANT_ITEMS = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verdant_items")
    );
    public static final TagKey<Item> VERDANT_SPRUCE_LOGS = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verdant_spruce_logs")
    );
    public static final TagKey<Item> REPAIRS_VERIXIUM = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "repairs_verixium")
    );
    public static final TagKey<Item> VERIXIUM_ARMOR = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verixium_armor")
    );
    public static final TagKey<Item> VERIXIUM_WEAPON = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verixium_weapon")
    );
    public static final TagKey<Item> VERIXIUM_TOOL = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verixium_tool")
    );
    public static final TagKey<Item> WOLF_ARMOR_ENCHANTABLE = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "wolf_armor_enchantable")
    );

    public static final TagKey<Item> BENEDICTION_ENCHANTMENT_USE_QUALIFIED_ITEMS = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "benediction_enchantment_use_qualified_items")
    );
    public static final TagKey<Item> BENEDICTION_ENCHANTMENT_USE_QUALIFIED_TOOLS = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "benediction_enchantment_use_qualified_tools")
    );
}