package cliffordha.totvw.datagen;

import cliffordha.totvw.registry.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class ModDyeSynthesizerRecipeProvider extends ModRecipeProvider {
    public ModDyeSynthesizerRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    //@Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
        return new RecipeProvider(registryLookup, exporter) {
            //@Override
            public void buildRecipes() {
                HolderLookup.RegistryLookup<Item> itemLookup = registries.lookupOrThrow(Registries.ITEM);

                // Request by DustyWoofi
                shaped(RecipeCategory.MISC, ModBlocks.IRIDESCENT_GLASS_PANE, 16)
                        .pattern("XXX")
                        .pattern("XXX")
                        .define('X', ModBlocks.IRIDESCENT_GLASS)
                        .unlockedBy(getHasName(Items.CRAFTING_TABLE), has(Items.CRAFTING_TABLE))
                        .save(output);

                stonecutterResultFromBase(RecipeCategory.MISC, Items.WHITE_DYE, ModBlocks.IRIDESCENT_GLASS, 4);
                stonecutterResultFromBase(RecipeCategory.MISC, Items.GRAY_DYE, ModBlocks.IRIDESCENT_GLASS, 4);
                stonecutterResultFromBase(RecipeCategory.MISC, Items.BROWN_DYE, ModBlocks.IRIDESCENT_GLASS, 4);
                stonecutterResultFromBase(RecipeCategory.MISC, Items.ORANGE_DYE, ModBlocks.IRIDESCENT_GLASS, 4);
                stonecutterResultFromBase(RecipeCategory.MISC, Items.LIME_DYE, ModBlocks.IRIDESCENT_GLASS, 4);
                stonecutterResultFromBase(RecipeCategory.MISC, Items.CYAN_DYE, ModBlocks.IRIDESCENT_GLASS, 4);
                stonecutterResultFromBase(RecipeCategory.MISC, Items.BLUE_DYE, ModBlocks.IRIDESCENT_GLASS, 4);
                stonecutterResultFromBase(RecipeCategory.MISC, Items.MAGENTA_DYE, ModBlocks.IRIDESCENT_GLASS, 4);
                stonecutterResultFromBase(RecipeCategory.MISC, Items.LIGHT_GRAY_DYE, ModBlocks.IRIDESCENT_GLASS, 4);
                stonecutterResultFromBase(RecipeCategory.MISC, Items.BLACK_DYE, ModBlocks.IRIDESCENT_GLASS, 4);
                stonecutterResultFromBase(RecipeCategory.MISC, Items.RED_DYE, ModBlocks.IRIDESCENT_GLASS, 4);
                stonecutterResultFromBase(RecipeCategory.MISC, Items.YELLOW_DYE, ModBlocks.IRIDESCENT_GLASS, 4);
                stonecutterResultFromBase(RecipeCategory.MISC, Items.GREEN_DYE, ModBlocks.IRIDESCENT_GLASS, 4);
                stonecutterResultFromBase(RecipeCategory.MISC, Items.LIGHT_BLUE_DYE, ModBlocks.IRIDESCENT_GLASS, 4);
                stonecutterResultFromBase(RecipeCategory.MISC, Items.PURPLE_DYE, ModBlocks.IRIDESCENT_GLASS, 4);
                stonecutterResultFromBase(RecipeCategory.MISC, Items.PINK_DYE, ModBlocks.IRIDESCENT_GLASS, 4);

                stonecutterResultFromBase(RecipeCategory.MISC, Items.WHITE_DYE, ModBlocks.IRIDESCENT_GLASS_PANE, 2);
                stonecutterResultFromBase(RecipeCategory.MISC, Items.GRAY_DYE, ModBlocks.IRIDESCENT_GLASS_PANE, 2);
                stonecutterResultFromBase(RecipeCategory.MISC, Items.BROWN_DYE, ModBlocks.IRIDESCENT_GLASS_PANE, 2);
                stonecutterResultFromBase(RecipeCategory.MISC, Items.ORANGE_DYE, ModBlocks.IRIDESCENT_GLASS_PANE, 2);
                stonecutterResultFromBase(RecipeCategory.MISC, Items.LIME_DYE, ModBlocks.IRIDESCENT_GLASS_PANE, 2);
                stonecutterResultFromBase(RecipeCategory.MISC, Items.CYAN_DYE, ModBlocks.IRIDESCENT_GLASS_PANE, 2);
                stonecutterResultFromBase(RecipeCategory.MISC, Items.BLUE_DYE, ModBlocks.IRIDESCENT_GLASS_PANE, 2);
                stonecutterResultFromBase(RecipeCategory.MISC, Items.MAGENTA_DYE, ModBlocks.IRIDESCENT_GLASS_PANE, 2);
                stonecutterResultFromBase(RecipeCategory.MISC, Items.LIGHT_GRAY_DYE, ModBlocks.IRIDESCENT_GLASS_PANE, 2);
                stonecutterResultFromBase(RecipeCategory.MISC, Items.BLACK_DYE, ModBlocks.IRIDESCENT_GLASS_PANE, 2);
                stonecutterResultFromBase(RecipeCategory.MISC, Items.RED_DYE, ModBlocks.IRIDESCENT_GLASS_PANE, 2);
                stonecutterResultFromBase(RecipeCategory.MISC, Items.YELLOW_DYE, ModBlocks.IRIDESCENT_GLASS_PANE, 2);
                stonecutterResultFromBase(RecipeCategory.MISC, Items.GREEN_DYE, ModBlocks.IRIDESCENT_GLASS_PANE, 2);
                stonecutterResultFromBase(RecipeCategory.MISC, Items.LIGHT_BLUE_DYE, ModBlocks.IRIDESCENT_GLASS_PANE, 2);
                stonecutterResultFromBase(RecipeCategory.MISC, Items.PURPLE_DYE, ModBlocks.IRIDESCENT_GLASS_PANE, 2);
                stonecutterResultFromBase(RecipeCategory.MISC, Items.PINK_DYE, ModBlocks.IRIDESCENT_GLASS_PANE, 2);
            }
        };
    }

    @Override
    public String getName() {
        return "ModDyeSynthesizerRecipeProvider";
    }
}