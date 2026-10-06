package cliffordha.totvw;

import cliffordha.totvw.networking.ServerboundPackets;
import cliffordha.totvw.networking.VWNetworking;
import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.loot.VWLootTables;
import cliffordha.totvw.registry.*;
import cliffordha.totvw.registry.attachments.VWAttachments;
import cliffordha.totvw.world.*;
import cliffordha.totvw.world.tree.VWRootPlacerTypes;

import net.fabricmc.api.ModInitializer;
import terrablender.api.TerraBlenderApi;

public class TalesOfTheVerdantWind implements ModInitializer, TerraBlenderApi {
	public TalesOfTheVerdantWind() {}

	public static final boolean IN_DEVELOPMENT = false;
	@Override
	public void onInitialize() {
		TOTVW.sendStat(TOTVW.MOD_NAME_LONG + " (or TOTVW for short) started initializing...");
		VWItems.register();
		VWBlocks.register();

		VWBlockProperties.register();
		VWFluids.register();

		VWAttachments.register();
		VWEntities.register();
		VWBlockEntityTypes.register();
		VWEnchantments.register();

		VWEffects.register();
		VWPotions.register();
		VWPotionBrewing.register();
		VWParticles.register();
		VWSounds.register();

		VWRootPlacerTypes.register();
		VWBiomeModifications.register();


		VWCommands.register();
		VWLootTables.registerModifiers();

		VWConfig.load();
		VWConfig.save();

		VWNetworking.register();
		ServerboundPackets.register();
	}

	@Override
	public void onTerraBlenderInitialized() {
		VWBiomes.registerBiomes();

		TOTVW.sendClassRegisterLog("[Dependency] TerraBlender");
	}
}