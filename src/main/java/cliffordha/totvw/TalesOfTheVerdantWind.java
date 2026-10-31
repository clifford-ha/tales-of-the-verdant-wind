package cliffordha.totvw;

import cliffordha.totvw.entity.GlobalServerTickEvents;
import cliffordha.totvw.networking.ServerboundPackets;
import cliffordha.totvw.networking.VWNetworking;
import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.loot.VWLootTables;
import cliffordha.totvw.registry.*;
import cliffordha.totvw.registry.attachments.VWAttachment;
import cliffordha.totvw.world.*;
import cliffordha.totvw.world.tree.VWRootPlacerTypes;

import net.fabricmc.api.ModInitializer;
import terrablender.api.TerraBlenderApi;

public class TalesOfTheVerdantWind implements ModInitializer, TerraBlenderApi {
	public TalesOfTheVerdantWind() {}

	public static final boolean IN_DEVELOPMENT = true;
	@Override
	public void onInitialize() {
		TOTVW.sendStat(TOTVW.MOD_NAME_LONG + " (or TOTVW for short) started initializing...");
		VWItems.register();
		VWBlocks.register();

		VWBlockProperties.register();
		VWFluids.register();

		VWAttachment.register();
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

		GlobalServerTickEvents.register();


		VWCommands.register();
		VWLootTables.registerModifiers();

		VWConfig.loadAndVerify();

		VWNetworking.register();
		ServerboundPackets.register();
	}

	@Override
	public void onTerraBlenderInitialized() {
		VWBiomes.registerBiomes();

		TOTVW.sendClassRegisterLog("[Dependency] TerraBlender");
	}
}