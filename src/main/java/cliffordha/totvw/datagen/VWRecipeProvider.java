package cliffordha.totvw.datagen;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import cliffordha.totvw.registry.VWItems;
import cliffordha.totvw.registry.blocks.VWBlocksVerdant;
import cliffordha.totvw.tag.VWItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;

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
                        1000.0F,
                        600,
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


                stairBuilder(VWBlocksVerdant.VERDANT_SPRUCE_STAIRS, Ingredient.of(VWBlocksVerdant.VERDANT_SPRUCE_PLANKS))
                        .unlockedBy(getHasName(VWBlocksVerdant.VERDANT_SPRUCE_PLANKS), has(VWBlocksVerdant.VERDANT_SPRUCE_PLANKS))
                        .save(output);

                slab(RecipeCategory.BUILDING_BLOCKS, VWBlocksVerdant.VERDANT_SPRUCE_SLAB, VWBlocksVerdant.VERDANT_SPRUCE_PLANKS);

                buttonBuilder(VWBlocksVerdant.VERDANT_SPRUCE_BUTTON, Ingredient.of(VWBlocksVerdant.VERDANT_SPRUCE_PLANKS))
                        .unlockedBy(getHasName(VWBlocksVerdant.VERDANT_SPRUCE_PLANKS), has(VWBlocksVerdant.VERDANT_SPRUCE_PLANKS))
                        .save(output);

                pressurePlate(VWBlocksVerdant.VERDANT_SPRUCE_PRESSURE_PLATE, VWBlocksVerdant.VERDANT_SPRUCE_PLANKS);

                fenceBuilder(VWBlocksVerdant.VERDANT_SPRUCE_FENCE, Ingredient.of(VWBlocksVerdant.VERDANT_SPRUCE_PLANKS))
                        .unlockedBy(getHasName(VWBlocksVerdant.VERDANT_SPRUCE_PLANKS), has(VWBlocksVerdant.VERDANT_SPRUCE_PLANKS))
                        .save(output);

                fenceGateBuilder(VWBlocksVerdant.VERDANT_SPRUCE_FENCE_GATE, Ingredient.of(VWBlocksVerdant.VERDANT_SPRUCE_PLANKS))
                        .unlockedBy(getHasName(VWBlocksVerdant.VERDANT_SPRUCE_PLANKS), has(VWBlocksVerdant.VERDANT_SPRUCE_PLANKS))
                        .save(output);

                trapdoorBuilder(VWBlocksVerdant.VERDANT_SPRUCE_TRAPDOOR, Ingredient.of(VWBlocksVerdant.VERDANT_SPRUCE_PLANKS))
                        .unlockedBy(getHasName(VWBlocksVerdant.VERDANT_SPRUCE_PLANKS), has(VWBlocksVerdant.VERDANT_SPRUCE_PLANKS))
                        .save(output);

                doorBuilder(VWBlocksVerdant.VERDANT_SPRUCE_DOOR, Ingredient.of(VWBlocksVerdant.VERDANT_SPRUCE_PLANKS))
                        .unlockedBy(getHasName(VWBlocksVerdant.VERDANT_SPRUCE_PLANKS), has(VWBlocksVerdant.VERDANT_SPRUCE_PLANKS))
                        .save(output);

                woodFromLogs(VWBlocksVerdant.VERDANT_SPRUCE_WOOD, VWBlocksVerdant.VERDANT_SPRUCE_LOG);
                woodFromLogs(VWBlocksVerdant.STRIPPED_VERDANT_SPRUCE_WOOD, VWBlocksVerdant.STRIPPED_VERDANT_SPRUCE_LOG);
                planksFromLogs(VWBlocksVerdant.VERDANT_SPRUCE_PLANKS, VWItemTags.VERDANT_SPRUCE_LOGS, 4);
                woodenBoat(VWItems.VERDANT_SPRUCE_BOAT, VWBlocksVerdant.VERDANT_SPRUCE_PLANKS);
                chestBoat(VWItems.VERDANT_SPRUCE_CHEST_BOAT, VWItems.VERDANT_SPRUCE_BOAT);

                signBuilder(VWItems.VERDANT_SPRUCE_SIGN, Ingredient.of(VWBlocksVerdant.VERDANT_SPRUCE_PLANKS));
                hangingSign(VWItems.VERDANT_SPRUCE_HANGING_SIGN, VWBlocksVerdant.VERDANT_SPRUCE_PLANKS);
                shelf(VWBlocksVerdant.VERDANT_SPRUCE_SHELF, VWBlocksVerdant.STRIPPED_VERDANT_SPRUCE_LOG);

                shaped(RecipeCategory.REDSTONE, VWBlocksVerdant.VERDANT_SPRUCE_STORAGE_BOX, 1)
                        .pattern("XPX")
                        .pattern("XCX")
                        .pattern("XPX")
                        .define('X', VWBlocksVerdant.VERDANT_SPRUCE_LOG)
                        .define('P', VWBlocksVerdant.VERDANT_SPRUCE_SLAB)
                        .define('C', Items.CHEST)
                        .unlockedBy(getHasName(VWBlocksVerdant.VERDANT_SPRUCE_PLANKS), has(VWBlocksVerdant.VERDANT_SPRUCE_PLANKS))
                        .save(output);
            }
        };
    }

    @Override
    public String getName() {
        return "ModRecipeProvider";
    }
}