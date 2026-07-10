package cliffordha.totvw;

import cliffordha.totvw.config.TOTVWConfig;
import cliffordha.totvw.registry.VWBlocks;
import cliffordha.totvw.registry.VWEffects;
import cliffordha.totvw.registry.VWEnchantments;
import cliffordha.totvw.registry.VWEntities;
import cliffordha.totvw.registry.VWFluids;
import cliffordha.totvw.registry.VWItems;
import cliffordha.totvw.loot.VWLootTableModifier;
import cliffordha.totvw.registry.VWParticles;
import cliffordha.totvw.registry.VWPotions;
import cliffordha.totvw.registry.*;
import cliffordha.totvw.registry.VWSounds;
import cliffordha.totvw.world.VWBiomeModifications;

import cliffordha.totvw.world.VWBiomes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import terrablender.api.TerraBlenderApi;

import static cliffordha.totvw.TOTVW.MOD_NAME;

public class TalesOfTheVerdantWind implements ModInitializer, TerraBlenderApi {
	public TalesOfTheVerdantWind() {
	}

	@Override
	public void onInitialize() {
		VWItems.registerModItems();
		VWBlocks.registerModBlocks();

		VWBlockProperties.register();
		VWFluids.registerModFluids();

		VWEntities.registerModEntities();
		VWBlockEntityTypes.register();
		VWEnchantments.registerModEnchantments();

		VWEffects.registerModEffects();
		VWPotions.registerModPotions();
		VWPotionBrewing.registerBrewingRecipes();
		VWParticles.registerModParticles();
		VWSounds.registerModSounds();
		VWBiomeModifications.addBiomeModifications();
		VWAttachments.registerModAttachments();

		TOTVWConfig.load();
		TOTVWConfig.save();

		// Debugging
		//VWCommands.registerModCommands();
		LootTableEvents.MODIFY.register(VWLootTableModifier::modifyLootTables);


		TOTVW.LOGGER.info(MOD_NAME + " has been initialized!");
	}

	@Override
	public void onTerraBlenderInitialized() {
		VWBiomes.registerBiomes();

		TOTVW.LOGGER.info(MOD_NAME + " | [TerraBlender] - Biomes registered!");
	}
}