package cliffordha.totvw.datagen;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import cliffordha.totvw.registry.VWBlocks;
import cliffordha.totvw.registry.VWItems;
import cliffordha.totvw.tag.VWItemTags;
import cliffordha.totvw.util.VWUtil;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.*;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import static net.minecraft.data.recipes.SingleItemRecipeBuilder.stonecutting;

public class VWRecipeProvider extends FabricRecipeProvider {
    public VWRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, BootstrapContext<Recipe<?>> recipes, BootstrapContext<Advancement> advancements) {
        return new RecipeProvider(recipes, advancements) {

            private static String getRecipeOutputName(final ItemLike product, final ItemLike material) {
                String itemName = getItemName(product);
                return "stonecutting_" + itemName + "_from_" + getItemName(material);
            }

            private void stonecutter(RecipeCategory category, ItemLike result, ItemLike base, int count) {
                SingleItemRecipeBuilder dyeRecipe = SingleItemRecipeBuilder.stonecutting(Ingredient.of(base), category, result, count).unlockedBy(getHasName(base), this.has(base));
                dyeRecipe.save(this.output, getRecipeOutputName(result, base));
            }

            private void dyeFromIridescentGlass(ItemLike... items) {
                for (ItemLike item : items) {
                    stonecutter(RecipeCategory.MISC, item, VWBlocks.IRIDESCENT_GLASS, 4);
                    stonecutter(RecipeCategory.MISC, item, VWBlocks.IRIDESCENT_GLASS_PANE, 2);
                }
            }

            @Override
            public void buildRecipes() {
                shaped(RecipeCategory.MISC, VWBlocks.VERIXIUM_POWDER_BLOCK, 1)
                        .pattern("XXX")
                        .pattern("XXX")
                        .pattern("XXX")
                        .define('X', VWItems.VERIXIUM_POWDER)
                        .group("verixium_materials")
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                shaped(RecipeCategory.MISC, VWItems.VERIXIUM_POWDER, 1)
                        .pattern("X")
                        .define('X', VWBlocks.VERIXIUM_POWDER_BLOCK)
                        .group("verixium_materials")
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);


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
                        650.0F,
                        20 * 90,
                        "verixium_raw_materials"
                );
                oreBlasting(
                        List.of(VWItems.VERIXIUM_FLUID_BUCKET),
                        RecipeCategory.MISC,
                        CookingBookCategory.MISC,
                        VWItems.VERIXIUM_CHUNK,
                        900.0F,
                        VWUtil.TimeUtil.duration(3, 45),
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
                        .unlockedBy(getHasName(VWItems.VERIXIUM_CHUNK), has(VWItems.VERIXIUM_CHUNK))
                        .save(output);

                shaped(RecipeCategory.COMBAT, VWItems.VERIXIUM_CHESTPLATE, 1)
                        .pattern("X X")
                        .pattern("XXX")
                        .pattern("XXX")
                        .define('X', VWItems.VERIXIUM_INGOT)
                        .group("verixium_armors")
                        .unlockedBy(getHasName(VWItems.VERIXIUM_CHUNK), has(VWItems.VERIXIUM_CHUNK))
                        .save(output);

                shaped(RecipeCategory.COMBAT, VWItems.VERIXIUM_LEGGINGS, 1)
                        .pattern("XXX")
                        .pattern("X X")
                        .pattern("X X")
                        .define('X', VWItems.VERIXIUM_INGOT)
                        .group("verixium_armors")
                        .unlockedBy(getHasName(VWItems.VERIXIUM_CHUNK), has(VWItems.VERIXIUM_CHUNK))
                        .save(output);

                shaped(RecipeCategory.COMBAT, VWItems.VERIXIUM_BOOTS, 1)
                        .pattern("X X")
                        .pattern("X X")
                        .define('X', VWItems.VERIXIUM_INGOT)
                        .group("verixium_armors")
                        .unlockedBy(getHasName(VWItems.VERIXIUM_CHUNK), has(VWItems.VERIXIUM_CHUNK))
                        .save(output);

                shaped(RecipeCategory.MISC, VWItems.VERIXIUM_INGOT, 1)
                        .pattern("XXX")
                        .pattern("XDX")
                        .pattern("XXX")
                        .define('D', Items.DIAMOND)
                        .define('X', VWItems.VERIXIUM_INGOT)
                        .group("verixium_raw_materials")
                        .unlockedBy(getHasName(Items.DIAMOND), has(Items.DIAMOND))
                        .save(output);

                shaped(RecipeCategory.TOOLS, VWItems.VERIXIUM_FLUID_BUCKET, 1)
                        .pattern(" X ")
                        .pattern("IWI")
                        .pattern(" I ")
                        .define('X', VWItems.VERIXIUM_POWDER)
                        .define('W', Items.WATER_BUCKET)
                        .define('I', Items.IRON_INGOT)
                        .group("verixium_materials")
                        .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                        .save(output);

                shaped(RecipeCategory.COMBAT, VWItems.VERIXIUM_ARMOR_UPGRADE_TEMPLATE, 2)
                        .pattern("XIX")
                        .pattern("WTW")
                        .pattern("XIX")
                        .define('T', VWItems.VERIXIUM_ARMOR_UPGRADE_TEMPLATE)
                        .define('X', VWItems.VERIXIUM_POWDER)
                        .define('W', Items.WIND_CHARGE)
                        .define('I', Items.DEEPSLATE)
                        .group("verixium_armors")
                        .unlockedBy(getHasName(VWItems.VERIXIUM_POWDER), has(VWItems.VERIXIUM_POWDER))
                        .save(output, "verixium_armor_upgrade_template_duplicate");

                shaped(RecipeCategory.COMBAT, VWItems.VERIXIUM_ARMOR_UPGRADE_TEMPLATE, 1)
                        .pattern("XWX")
                        .pattern("WTW")
                        .pattern("XWX")
                        .define('W', Blocks.DEEPSLATE)
                        .define('X', VWBlocks.VERIXIUM_POWDER_BLOCK)
                        .define('T', Items.WIND_CHARGE)
                        .group("verixium_armors")
                        .unlockedBy(getHasName(VWItems.VERIXIUM_POWDER), has(VWItems.VERIXIUM_POWDER))
                        .save(output);

                shaped(RecipeCategory.MISC, VWItems.SOUL_RUNESTONE_PLATE, 1)
                        .pattern("AB")
                        .pattern("CD")
                        .define('A', VWItems.SOUL_RUNESTONE_FRAGMENT_1)
                        .define('B', VWItems.SOUL_RUNESTONE_FRAGMENT_2)
                        .define('C', VWItems.SOUL_RUNESTONE_FRAGMENT_3)
                        .define('D', VWItems.SOUL_RUNESTONE_FRAGMENT_4)
                        .group("soul_runestone_materials")
                        .unlockedBy(getHasName(VWItems.SOUL_RUNESTONE_FRAGMENT_1), has(VWItems.SOUL_RUNESTONE_FRAGMENT_3))
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
                shelf(VWBlocks.VERDANT_SPRUCE_SHELF, VWBlocks.STRIPPED_VERDANT_SPRUCE_LOG);

                shaped(RecipeCategory.BUILDING_BLOCKS, VWItems.VERDANT_SPRUCE_SIGN, 3)
                        .pattern("XXX")
                        .pattern("XXX")
                        .pattern(" P ")
                        .define('X', VWBlocks.VERDANT_SPRUCE_PLANKS)
                        .define('P', Items.STICK)
                        .unlockedBy(getHasName(VWBlocks.VERDANT_SPRUCE_PLANKS), has(VWBlocks.VERDANT_SPRUCE_PLANKS))
                        .save(output);

                shaped(RecipeCategory.BUILDING_BLOCKS, VWItems.VERDANT_SPRUCE_HANGING_SIGN, 6)
                        .pattern("P P")
                        .pattern("XXX")
                        .pattern("XXX")
                        .define('X', VWBlocks.STRIPPED_VERDANT_SPRUCE_LOG)
                        .define('P', Blocks.IRON_CHAIN)
                        .unlockedBy(getHasName(VWBlocks.STRIPPED_VERDANT_SPRUCE_LOG), has(VWBlocks.STRIPPED_VERDANT_SPRUCE_LOG))
                        .save(output);

                shaped(RecipeCategory.REDSTONE, VWBlocks.VERDANT_SPRUCE_STORAGE_BOX, 1)
                        .pattern("XPX")
                        .pattern("XCX")
                        .pattern("XPX")
                        .define('X', VWBlocks.VERDANT_SPRUCE_LOG)
                        .define('P', VWBlocks.VERDANT_SPRUCE_SLAB)
                        .define('C', Items.CHEST)
                        .unlockedBy(getHasName(VWBlocks.VERDANT_SPRUCE_LOG), has(VWBlocks.VERDANT_SPRUCE_LOG))
                        .save(output);




                // Request by DustyWoofi
                shaped(RecipeCategory.MISC, VWBlocks.IRIDESCENT_GLASS_PANE, 16)
                        .pattern("XXX")
                        .pattern("XXX")
                        .define('X', VWBlocks.IRIDESCENT_GLASS)
                        .unlockedBy(getHasName(Blocks.GLASS), has(Blocks.GLASS))
                        .save(output);

                dyeFromIridescentGlass(
                        Items.DYE.white(),
                        Items.DYE.gray(),
                        Items.DYE.brown(),
                        Items.DYE.orange(),
                        Items.DYE.lime(),
                        Items.DYE.cyan(),
                        Items.DYE.blue(),
                        Items.DYE.magenta(),
                        Items.DYE.lightGray(),
                        Items.DYE.black(),
                        Items.DYE.red(),
                        Items.DYE.yellow(),
                        Items.DYE.green(),
                        Items.DYE.lightBlue(),
                        Items.DYE.purple(),
                        Items.DYE.pink()
                );
            }
        };
    }

    @Override
    public String getName() {
        return "VWRecipeProvider";
    }
}