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

	public static boolean IN_DEVELOPMENT = false;

	@Override
	public void onInitialize() {
		TOTVW.sendStat(TOTVW.MOD_NAME_LONG + " ( or TOTVW for short) started initializing...");
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

		LootTableEvents.MODIFY.register(VWLootTableModifier::modifyLootTables);

		VWCommands.register();
	}

	@Override
	public void onTerraBlenderInitialized() {
		VWBiomes.registerBiomes();

		TOTVW.sendClassRegisterLog("[Dependency] TerraBlender");
	}
}