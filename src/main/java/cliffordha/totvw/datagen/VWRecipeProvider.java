package cliffordha.totvw.datagen;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import cliffordha.totvw.registry.VWBlocks;
import cliffordha.totvw.registry.VWItems;
import cliffordha.totvw.tag.VWItemTags;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.InventoryChangeTrigger.TriggerInstance.Slots;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.world.level.ItemLike;

import static net.minecraft.data.recipes.SingleItemRecipeBuilder.stonecutting;

public class VWRecipeProvider extends FabricRecipeProvider {
    public VWRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
        return new RecipeProvider(registryLookup, exporter) {
            @Override
            public void buildRecipes() {
                HolderLookup.RegistryLookup<Item> itemLookup = registries.lookupOrThrow(Registries.ITEM);

                shaped(RecipeCategory.MISC, VWItems.VERIXIUM_PAPER, 1)
                        .pattern("X")
                        .pattern("P")
                        .define('X', VWItems.VERIXIUM_POWDER)
                        .define('P', Items.PAPER)
                        .group("verixium_materials")
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                shaped(RecipeCategory.MISC, Items.FIREWORK_ROCKET, 8)
                        .pattern("XP")
                        .define('X', VWItems.VERIXIUM_PAPER)
                        .define('P', Items.GUNPOWDER)
                        .group("verixium_materials")
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                shaped(RecipeCategory.MISC, VWItems.CONDENSED_VERIXIUM, 1)
                        .pattern("XX")
                        .pattern("XX")
                        .define('X', VWItems.VERIXIUM_CHUNK)
                        .group("verixium_raw_materials")
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                stonecutting(Ingredient.of(VWItems.CONDENSED_VERIXIUM), RecipeCategory.MISC, VWItems.VERIXIUM_SHARD, 1)
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .group("verixium_raw_materials")
                        .save(output);

                oreBlasting(
                        List.of(VWItems.VERIXIUM_SHARD),
                        RecipeCategory.MISC,
                        CookingBookCategory.MISC,
                        VWItems.VERIXIUM_POWDER,
                        750.0F,
                        20 * 90,
                        "verixium_raw_materials"
                );
                shaped(RecipeCategory.COMBAT, VWItems.VERIXIUM_SPEAR, 1)
                        .pattern("  L")
                        .pattern(" X ")
                        .pattern("X  ")
                        .define('X', Items.STICK)
                        .define('L', VWItems.VERIXIUM_INGOT)
                        .group("verixium_weapons")
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                shaped(RecipeCategory.COMBAT, VWItems.VERIXIUM_SWORD, 1)
                        .pattern("L")
                        .pattern("X")
                        .pattern("X")
                        .define('X', Items.STICK)
                        .define('L', VWItems.VERIXIUM_INGOT)
                        .group("verixium_weapons")
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                shaped(RecipeCategory.COMBAT, VWItems.VERIXIUM_AXE, 1)
                        .pattern("LL")
                        .pattern("LX")
                        .pattern(" X")
                        .define('X', Items.STICK)
                        .define('L', VWItems.VERIXIUM_INGOT)
                        .group("verixium_weapons")
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                shaped(RecipeCategory.TOOLS, VWItems.VERIXIUM_PICKAXE, 1)
                        .pattern("LLL")
                        .pattern(" X ")
                        .pattern(" X ")
                        .define('X', Items.STICK)
                        .define('L', VWItems.VERIXIUM_INGOT)
                        .group("verixium_tools")
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                shaped(RecipeCategory.TOOLS, VWItems.VERIXIUM_HOE, 1)
                        .pattern("LL")
                        .pattern(" X")
                        .pattern(" X")
                        .define('X', Items.STICK)
                        .define('L', VWItems.VERIXIUM_INGOT)
                        .group("verixium_tools")
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                shaped(RecipeCategory.TOOLS, VWItems.VERIXIUM_SHOVEL, 1)
                        .pattern("L")
                        .pattern("X")
                        .pattern("X")
                        .define('X', Items.STICK)
                        .define('L', VWItems.VERIXIUM_INGOT)
                        .group("verixium_tools")
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                shaped(RecipeCategory.COMBAT, VWItems.VERIXIUM_HELMET, 1)
                        .pattern("XXX")
                        .pattern("X X")
                        .define('X', VWItems.VERIXIUM_INGOT)
                        .group("verixium_armors")
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                shaped(RecipeCategory.COMBAT, VWItems.VERIXIUM_CHESTPLATE, 1)
                        .pattern("X X")
                        .pattern("XXX")
                        .pattern("XXX")
                        .define('X', VWItems.VERIXIUM_INGOT)
                        .group("verixium_armors")
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                shaped(RecipeCategory.COMBAT, VWItems.VERIXIUM_LEGGINGS, 1)
                        .pattern("XXX")
                        .pattern("X X")
                        .pattern("X X")
                        .define('X', VWItems.VERIXIUM_INGOT)
                        .group("verixium_armors")
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                shaped(RecipeCategory.COMBAT, VWItems.VERIXIUM_BOOTS, 1)
                        .pattern("X X")
                        .pattern("X X")
                        .define('X', VWItems.VERIXIUM_INGOT)
                        .group("verixium_armors")
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                shaped(RecipeCategory.MISC, VWItems.VERIXIUM_INGOT, 1)
                        .pattern("XXX")
                        .pattern("XDX")
                        .pattern("XXX")
                        .define('D', Items.DIAMOND)
                        .define('X', VWItems.VERIXIUM_INGOT)
                        .group("verixium_raw_materials")
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                shaped(RecipeCategory.TOOLS, VWItems.VERIXIUM_FLUID_BUCKET, 1)
                        .pattern(" X ")
                        .pattern("IWI")
                        .pattern(" I ")
                        .define('X', VWItems.VERIXIUM_POWDER)
                        .define('W', Items.WATER_BUCKET)
                        .define('I', Items.IRON_INGOT)
                        .group("verixium_materials")
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                shaped(RecipeCategory.COMBAT, VWItems.VERIXIUM_ARMOR_UPGRADE_TEMPLATE, 1)
                        .pattern("XIX")
                        .pattern("IWI")
                        .pattern("XIX")
                        .define('X', VWItems.VERIXIUM_POWDER)
                        .define('W', Items.WIND_CHARGE)
                        .define('I', Items.DEEPSLATE)
                        .group("verixium_armors")
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);


                stairBuilder(VWBlocks.VERDANT_SPRUCE_STAIRS, Ingredient.of(VWBlocks.VERDANT_SPRUCE_PLANKS))
                        .unlockedBy(getHasName(VWBlocks.VERDANT_SPRUCE_PLANKS), has(VWBlocks.VERDANT_SPRUCE_PLANKS))
                        .save(output);

                slab(RecipeCategory.BUILDING_BLOCKS, VWBlocks.VERDANT_SPRUCE_SLAB, VWBlocks.VERDANT_SPRUCE_PLANKS);

                buttonBuilder(VWBlocks.VERDANT_SPRUCE_BUTTON, Ingredient.of(VWBlocks.VERDANT_SPRUCE_PLANKS))
                        .unlockedBy(getHasName(VWBlocks.VERDANT_SPRUCE_PLANKS), has(VWBlocks.VERDANT_SPRUCE_PLANKS))
                        .save(output);

                pressurePlate(VWBlocks.VERDANT_SPRUCE_PRESSURE_PLATE, VWBlocks.VERDANT_SPRUCE_PLANKS);

                fenceBuilder(VWBlocks.VERDANT_SPRUCE_FENCE, Ingredient.of(VWBlocks.VERDANT_SPRUCE_PLANKS))
                        .unlockedBy(getHasName(VWBlocks.VERDANT_SPRUCE_PLANKS), has(VWBlocks.VERDANT_SPRUCE_PLANKS))
                        .save(output);

                fenceGateBuilder(VWBlocks.VERDANT_SPRUCE_FENCE_GATE, Ingredient.of(VWBlocks.VERDANT_SPRUCE_PLANKS))
                        .unlockedBy(getHasName(VWBlocks.VERDANT_SPRUCE_PLANKS), has(VWBlocks.VERDANT_SPRUCE_PLANKS))
                        .save(output);

                trapdoorBuilder(VWBlocks.VERDANT_SPRUCE_TRAPDOOR, Ingredient.of(VWBlocks.VERDANT_SPRUCE_PLANKS))
                        .unlockedBy(getHasName(VWBlocks.VERDANT_SPRUCE_PLANKS), has(VWBlocks.VERDANT_SPRUCE_PLANKS))
                        .save(output);

                doorBuilder(VWBlocks.VERDANT_SPRUCE_DOOR, Ingredient.of(VWBlocks.VERDANT_SPRUCE_PLANKS))
                        .unlockedBy(getHasName(VWBlocks.VERDANT_SPRUCE_PLANKS), has(VWBlocks.VERDANT_SPRUCE_PLANKS))
                        .save(output);

                woodFromLogs(VWBlocks.VERDANT_SPRUCE_WOOD, VWBlocks.VERDANT_SPRUCE_LOG);
                woodFromLogs(VWBlocks.STRIPPED_VERDANT_SPRUCE_WOOD, VWBlocks.STRIPPED_VERDANT_SPRUCE_LOG);
                planksFromLogs(VWBlocks.VERDANT_SPRUCE_PLANKS, VWItemTags.VERDANT_SPRUCE_LOGS, 4);
                woodenBoat(VWItems.VERDANT_SPRUCE_BOAT, VWBlocks.VERDANT_SPRUCE_PLANKS);
                chestBoat(VWItems.VERDANT_SPRUCE_CHEST_BOAT, VWItems.VERDANT_SPRUCE_BOAT);

                signBuilder(VWItems.VERDANT_SPRUCE_SIGN, Ingredient.of(VWBlocks.VERDANT_SPRUCE_PLANKS));
                hangingSign(VWItems.VERDANT_SPRUCE_HANGING_SIGN, VWBlocks.VERDANT_SPRUCE_PLANKS);
                shelf(VWBlocks.VERDANT_SPRUCE_SHELF, VWBlocks.STRIPPED_VERDANT_SPRUCE_LOG);

                shaped(RecipeCategory.REDSTONE, VWBlocks.VERDANT_SPRUCE_STORAGE_BOX, 1)
                        .pattern("XPX")
                        .pattern("XCX")
                        .pattern("XPX")
                        .define('X', VWBlocks.VERDANT_SPRUCE_LOG)
                        .define('P', VWBlocks.VERDANT_SPRUCE_SLAB)
                        .define('C', Items.CHEST)
                        .unlockedBy(getHasName(VWBlocks.VERDANT_SPRUCE_PLANKS), has(VWBlocks.VERDANT_SPRUCE_PLANKS))
                        .save(output);




                // Request by DustyWoofi
                shaped(RecipeCategory.MISC, VWBlocks.IRIDESCENT_GLASS_PANE, 16)
                        .pattern("XXX")
                        .pattern("XXX")
                        .define('X', VWBlocks.IRIDESCENT_GLASS)
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                dyeFromIridescentGlass(exporter,
                        Items.WHITE_DYE,
                        Items.GRAY_DYE,
                        Items.BROWN_DYE,
                        Items.ORANGE_DYE,
                        Items.LIME_DYE,
                        Items.CYAN_DYE,
                        Items.BLUE_DYE,
                        Items.MAGENTA_DYE,
                        Items.LIGHT_GRAY_DYE,
                        Items.BLACK_DYE,
                        Items.RED_DYE,
                        Items.YELLOW_DYE,
                        Items.GREEN_DYE,
                        Items.LIGHT_BLUE_DYE,
                        Items.PURPLE_DYE,
                        Items.PINK_DYE
                );
            }
        };
    }

    private static String getItemName(ItemLike item) {
        return BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
    }

    private static SingleItemRecipeBuilder getDye(ItemLike block, ItemLike output, int count) {
        String name = BuiltInRegistries.ITEM.getKey(output.asItem()).getPath();
        return stonecutting(Ingredient.of(block), RecipeCategory.MISC, output, count).unlockedBy(name, CriteriaTriggers.INVENTORY_CHANGED.createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), Slots.ANY, List.of(ItemPredicate.Builder.item().build()))));
    }

    private void dyeFromIridescentGlass(RecipeOutput exporter, Item... dye) {
        for (Item item : dye) {
            SingleItemRecipeBuilder recipe = getDye(VWBlocks.IRIDESCENT_GLASS, item, 4);
            String outputDye = "iridescent_glass_to_" + getItemName(item);
            recipe.save(exporter, "stonecutting_" + outputDye);
        }
        for (Item item : dye) {
            SingleItemRecipeBuilder recipe = getDye(VWBlocks.IRIDESCENT_GLASS_PANE, item, 2);
            String outputDye = "iridescent_glass_pane_to_" + getItemName(item);
            recipe.save(exporter, "stonecutting_" + outputDye);
        }
    }

    @Override
    public String getName() {
        return "VWRecipeProvider";
    }
}