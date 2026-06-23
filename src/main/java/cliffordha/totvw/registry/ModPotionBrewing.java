package cliffordha.totvw.registry;

import net.fabricmc.fabric.api.registry.FabricPotionBrewingBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;

public class ModPotionBrewing {
    public static void registerBrewingRecipes() {
        FabricPotionBrewingBuilder.BUILD.register(builder -> {
            builder.registerPotionRecipe(Potions.POISON, Ingredient.of(Items.WITHER_ROSE), ModPotions.BALEFUL_STRENGTH_POTION);
            builder.registerPotionRecipe(Potions.STRENGTH, Ingredient.of(ModItems.VERIXIUM_POWDER), ModPotions.MIGHT_AMPLIFIER_POTION);
            builder.registerPotionRecipe(Potions.WIND_CHARGED, Ingredient.of(ModItems.VERIXIUM_POWDER), ModPotions.SACRED_VERDANT_POTION);
        });
    }
}
