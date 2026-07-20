package cliffordha.totvw;

import cliffordha.totvw.config.TOTVWConfig;
import cliffordha.totvw.registry.*;
import cliffordha.totvw.loot.VWLootTableModifier;
import cliffordha.totvw.world.*;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import terrablender.api.TerraBlenderApi;

public class TalesOfTheVerdantWind implements ModInitializer, TerraBlenderApi {
	public TalesOfTheVerdantWind() {}

	@Override
	public void onInitialize() {
		VWItems.register();
		VWBlocks.register();

		VWBlockProperties.register();
		VWFluids.register();

		VWEntities.register();
		VWBlockEntityTypes.register();
		VWEnchantments.register();

		VWEffects.register();
		VWPotions.register();
		VWPotionBrewing.register();
		VWParticles.register();
		VWSounds.register();
		VWBiomeModifications.register();
		VWAttachments.register();

		TOTVWConfig.load();
		TOTVWConfig.save();

		// Debugging
		//VWCommands.registerModCommands();
		LootTableEvents.MODIFY.register(VWLootTableModifier::modifyLootTables);


		TOTVW.sendInfo("Mod has been initialized!");
	}

	@Override
	public void onTerraBlenderInitialized() {
		VWBiomes.registerBiomes();

		TOTVW.sendClassRegisterLog("[Dependency] TerraBlender");
	}
}