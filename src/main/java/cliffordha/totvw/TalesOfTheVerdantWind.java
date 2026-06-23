package cliffordha.totvw;

import cliffordha.totvw.config.TOTVWConfig;
import cliffordha.totvw.datagen.ModAdvancements;
import cliffordha.totvw.registry.ModBlocks;
import cliffordha.totvw.registry.ModEffects;
import cliffordha.totvw.registry.ModEnchantments;
import cliffordha.totvw.registry.ModEntities;
import cliffordha.totvw.registry.ModFluids;
import cliffordha.totvw.registry.ModItems;
import cliffordha.totvw.loot.ModLootTableModifier;
import cliffordha.totvw.registry.ModParticles;
import cliffordha.totvw.registry.ModPotions;
import cliffordha.totvw.registry.*;
import cliffordha.totvw.registry.ModSounds;
import cliffordha.totvw.world.ModBiomeModifications;

import cliffordha.totvw.world.ModBiomes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import terrablender.api.TerraBlenderApi;

import static cliffordha.totvw.TOTVW.MOD_NAME;

public class TalesOfTheVerdantWind implements ModInitializer, TerraBlenderApi {
	public TalesOfTheVerdantWind(){}

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();

		ModBlockProperties.register();
		ModFluids.registerModFluids();

		ModEntities.registerModEntities();
		ModBlockEntityTypes.register();
		ModEnchantments.registerModEnchantments();

		ModEffects.registerModEffects();
		ModPotions.registerModPotions();
		ModPotionBrewing.registerBrewingRecipes();
		ModParticles.registerModParticles();
		ModSounds.registerModSounds();
		ModBiomeModifications.addBiomeModifications();
		ModAttachments.registerModAttachments();

		TOTVWConfig.load();

		// Debugging
		//ModCommands.registerModCommands();
		LootTableEvents.MODIFY.register(ModLootTableModifier::modifyLootTables);


		TOTVW.LOGGER.info(MOD_NAME + " has been initialized!");
	}
	@Override
	public void onTerraBlenderInitialized() {
		ModBiomes.registerBiomes();

		TOTVW.LOGGER.info(MOD_NAME + " | [TerraBlender] - Biomes registered!");
	}
}