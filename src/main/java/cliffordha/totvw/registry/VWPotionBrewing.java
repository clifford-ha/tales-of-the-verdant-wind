package cliffordha.totvw.registry;

import net.fabricmc.fabric.api.registry.FabricPotionBrewingBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;

public class VWPotionBrewing {
    public static void register() {
        FabricPotionBrewingBuilder.BUILD.register(builder -> {
            builder.registerPotionRecipe(Potions.POISON, Ingredient.of(Items.WITHER_ROSE), VWPotions.BALEFUL_STRENGTH_POTION);
            builder.registerPotionRecipe(Potions.STRENGTH, Ingredient.of(VWItems.VERIXIUM_POWDER), VWPotions.MIGHT_AMPLIFIER_POTION);
            builder.registerPotionRecipe(Potions.WIND_CHARGED, Ingredient.of(VWItems.VERIXIUM_POWDER), VWPotions.SACRED_VERDANT_POTION);
        });
    }
}
