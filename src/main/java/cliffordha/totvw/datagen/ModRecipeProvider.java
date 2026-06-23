package cliffordha.totvw.datagen;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import cliffordha.totvw.registry.blocks.VerdantBlocks;
import cliffordha.totvw.registry.ModItems;
import cliffordha.totvw.tag.ModItemTags;
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

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
        return new RecipeProvider(registryLookup, exporter) {
            @Override
            public void buildRecipes() {
                HolderLookup.RegistryLookup<Item> itemLookup = registries.lookupOrThrow(Registries.ITEM);

                shaped(RecipeCategory.MISC, ModItems.VERIXIUM_PAPER, 1)
                        .pattern("X")
                        .pattern("P")
                        .define('X', ModItems.VERIXIUM_POWDER)
                        .define('P', Items.PAPER)
                        .group("verixium_materials")
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                shaped(RecipeCategory.MISC, Items.FIREWORK_ROCKET, 8)
                        .pattern("XP")
                        .define('X', ModItems.VERIXIUM_PAPER)
                        .define('P', Items.GUNPOWDER)
                        .group("verixium_materials")
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                shaped(RecipeCategory.MISC, ModItems.CONDENSED_VERIXIUM, 1)
                        .pattern("XX")
                        .pattern("XX")
                        .define('X', ModItems.VERIXIUM_CHUNK)
                        .group("verixium_raw_materials")
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                stonecutting(Ingredient.of(ModItems.CONDENSED_VERIXIUM), RecipeCategory.MISC, ModItems.VERIXIUM_SHARD, 1)
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .group("verixium_raw_materials")
                        .save(output);

                oreBlasting(
                        List.of(ModItems.VERIXIUM_SHARD),
                        RecipeCategory.MISC,
                        CookingBookCategory.MISC,
                        ModItems.VERIXIUM_POWDER,
                        1000.0F,
                        600,
                        "verixium_raw_materials"
                );
                shaped(RecipeCategory.COMBAT, ModItems.VERIXIUM_SPEAR, 1)
                        .pattern("  L")
                        .pattern(" X ")
                        .pattern("X  ")
                        .define('X', Items.STICK)
                        .define('L', ModItems.VERIXIUM_INGOT)
                        .group("verixium_weapons")
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                shaped(RecipeCategory.COMBAT, ModItems.VERIXIUM_SWORD, 1)
                        .pattern("L")
                        .pattern("X")
                        .pattern("X")
                        .define('X', Items.STICK)
                        .define('L', ModItems.VERIXIUM_INGOT)
                        .group("verixium_weapons")
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                shaped(RecipeCategory.COMBAT, ModItems.VERIXIUM_AXE, 1)
                        .pattern("LL")
                        .pattern("LX")
                        .pattern(" X")
                        .define('X', Items.STICK)
                        .define('L', ModItems.VERIXIUM_INGOT)
                        .group("verixium_weapons")
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                shaped(RecipeCategory.TOOLS, ModItems.VERIXIUM_PICKAXE, 1)
                        .pattern("LLL")
                        .pattern(" X ")
                        .pattern(" X ")
                        .define('X', Items.STICK)
                        .define('L', ModItems.VERIXIUM_INGOT)
                        .group("verixium_tools")
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                shaped(RecipeCategory.TOOLS, ModItems.VERIXIUM_HOE, 1)
                        .pattern("LL")
                        .pattern(" X")
                        .pattern(" X")
                        .define('X', Items.STICK)
                        .define('L', ModItems.VERIXIUM_INGOT)
                        .group("verixium_tools")
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                shaped(RecipeCategory.TOOLS, ModItems.VERIXIUM_SHOVEL, 1)
                        .pattern("L")
                        .pattern("X")
                        .pattern("X")
                        .define('X', Items.STICK)
                        .define('L', ModItems.VERIXIUM_INGOT)
                        .group("verixium_tools")
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                shaped(RecipeCategory.COMBAT, ModItems.VERIXIUM_HELMET, 1)
                        .pattern("XXX")
                        .pattern("X X")
                        .define('X', ModItems.VERIXIUM_INGOT)
                        .group("verixium_armors")
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                shaped(RecipeCategory.COMBAT, ModItems.VERIXIUM_CHESTPLATE, 1)
                        .pattern("X X")
                        .pattern("XXX")
                        .pattern("XXX")
                        .define('X', ModItems.VERIXIUM_INGOT)
                        .group("verixium_armors")
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                shaped(RecipeCategory.COMBAT, ModItems.VERIXIUM_LEGGINGS, 1)
                        .pattern("XXX")
                        .pattern("X X")
                        .pattern("X X")
                        .define('X', ModItems.VERIXIUM_INGOT)
                        .group("verixium_armors")
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                shaped(RecipeCategory.COMBAT, ModItems.VERIXIUM_BOOTS, 1)
                        .pattern("X X")
                        .pattern("X X")
                        .define('X', ModItems.VERIXIUM_INGOT)
                        .group("verixium_armors")
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                shaped(RecipeCategory.MISC, ModItems.VERIXIUM_INGOT, 1)
                        .pattern("XXX")
                        .pattern("XDX")
                        .pattern("XXX")
                        .define('D', Items.DIAMOND)
                        .define('X', ModItems.VERIXIUM_INGOT)
                        .group("verixium_raw_materials")
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                shaped(RecipeCategory.TOOLS, ModItems.VERIXIUM_FLUID_BUCKET, 1)
                        .pattern(" X ")
                        .pattern("IWI")
                        .pattern(" I ")
                        .define('X', ModItems.VERIXIUM_POWDER)
                        .define('W', Items.WATER_BUCKET)
                        .define('I', Items.IRON_INGOT)
                        .group("verixium_materials")
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                shaped(RecipeCategory.COMBAT, ModItems.VERIXIUM_ARMOR_UPGRADE_TEMPLATE, 1)
                        .pattern("XIX")
                        .pattern("IWI")
                        .pattern("XIX")
                        .define('X', ModItems.VERIXIUM_POWDER)
                        .define('W', Items.WIND_CHARGE)
                        .define('I', Items.DEEPSLATE)
                        .group("verixium_armors")
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);


                stairBuilder(VerdantBlocks.VERDANT_SPRUCE_STAIRS, Ingredient.of(VerdantBlocks.VERDANT_SPRUCE_PLANKS))
                        .unlockedBy(getHasName(VerdantBlocks.VERDANT_SPRUCE_PLANKS), has(VerdantBlocks.VERDANT_SPRUCE_PLANKS))
                        .save(output);

                slab(RecipeCategory.BUILDING_BLOCKS, VerdantBlocks.VERDANT_SPRUCE_SLAB, VerdantBlocks.VERDANT_SPRUCE_PLANKS);

                buttonBuilder(VerdantBlocks.VERDANT_SPRUCE_BUTTON, Ingredient.of(VerdantBlocks.VERDANT_SPRUCE_PLANKS))
                        .unlockedBy(getHasName(VerdantBlocks.VERDANT_SPRUCE_PLANKS), has(VerdantBlocks.VERDANT_SPRUCE_PLANKS))
                        .save(output);

                pressurePlate(VerdantBlocks.VERDANT_SPRUCE_PRESSURE_PLATE, VerdantBlocks.VERDANT_SPRUCE_PLANKS);

                fenceBuilder(VerdantBlocks.VERDANT_SPRUCE_FENCE, Ingredient.of(VerdantBlocks.VERDANT_SPRUCE_PLANKS))
                        .unlockedBy(getHasName(VerdantBlocks.VERDANT_SPRUCE_PLANKS), has(VerdantBlocks.VERDANT_SPRUCE_PLANKS))
                        .save(output);

                fenceGateBuilder(VerdantBlocks.VERDANT_SPRUCE_FENCE_GATE, Ingredient.of(VerdantBlocks.VERDANT_SPRUCE_PLANKS))
                        .unlockedBy(getHasName(VerdantBlocks.VERDANT_SPRUCE_PLANKS), has(VerdantBlocks.VERDANT_SPRUCE_PLANKS))
                        .save(output);

                trapdoorBuilder(VerdantBlocks.VERDANT_SPRUCE_TRAPDOOR, Ingredient.of(VerdantBlocks.VERDANT_SPRUCE_PLANKS))
                        .unlockedBy(getHasName(VerdantBlocks.VERDANT_SPRUCE_PLANKS), has(VerdantBlocks.VERDANT_SPRUCE_PLANKS))
                        .save(output);

                doorBuilder(VerdantBlocks.VERDANT_SPRUCE_DOOR, Ingredient.of(VerdantBlocks.VERDANT_SPRUCE_PLANKS))
                        .unlockedBy(getHasName(VerdantBlocks.VERDANT_SPRUCE_PLANKS), has(VerdantBlocks.VERDANT_SPRUCE_PLANKS))
                        .save(output);

                woodFromLogs(VerdantBlocks.VERDANT_SPRUCE_WOOD, VerdantBlocks.VERDANT_SPRUCE_LOG);
                woodFromLogs(VerdantBlocks.STRIPPED_VERDANT_SPRUCE_WOOD, VerdantBlocks.STRIPPED_VERDANT_SPRUCE_LOG);
                planksFromLogs(VerdantBlocks.VERDANT_SPRUCE_PLANKS, ModItemTags.VERDANT_SPRUCE_LOGS, 4);
                woodenBoat(ModItems.VERDANT_SPRUCE_BOAT, VerdantBlocks.VERDANT_SPRUCE_PLANKS);
                chestBoat(ModItems.VERDANT_SPRUCE_CHEST_BOAT, ModItems.VERDANT_SPRUCE_BOAT);

                signBuilder(ModItems.VERDANT_SPRUCE_SIGN, Ingredient.of(VerdantBlocks.VERDANT_SPRUCE_PLANKS));
                hangingSign(ModItems.VERDANT_SPRUCE_HANGING_SIGN, VerdantBlocks.VERDANT_SPRUCE_PLANKS);
                shelf(VerdantBlocks.VERDANT_SPRUCE_SHELF, VerdantBlocks.STRIPPED_VERDANT_SPRUCE_LOG);
            }
        };
    }

    @Override
    public String getName() {
        return "ModRecipeProvider";
    }
}