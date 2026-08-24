package cliffordha.totvw;

import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.loot.VWLootTables;
import cliffordha.totvw.registry.*;
import cliffordha.totvw.world.*;

import net.fabricmc.api.ModInitializer;
import terrablender.api.TerraBlenderApi;

public class TalesOfTheVerdantWind implements ModInitializer, TerraBlenderApi {
	public TalesOfTheVerdantWind() {}

	public static boolean IN_DEVELOPMENT = true;

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

		VWLootTables.registerModifiers();
		VWCommands.register();

		VWConfig.load();
		VWConfig.save();
	}

	@Override
	public void onTerraBlenderInitialized() {
		VWBiomes.registerBiomes();

		TOTVW.sendClassRegisterLog("[Dependency] TerraBlender");
	}
}