package cliffordha.totvw;

import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.loot.VWLootTables;
import cliffordha.totvw.registry.*;
import cliffordha.totvw.client.ClientPrefsPayload;
import cliffordha.totvw.registry.attachments.VWAttachments;
import cliffordha.totvw.registry.attachments.PlayerPrefs;
import cliffordha.totvw.world.*;
import cliffordha.totvw.world.tree.VWRootPlacerTypes;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
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

		PayloadTypeRegistry.serverboundPlay().register(ClientPrefsPayload.TYPE, ClientPrefsPayload.STREAM_CODEC);

		ServerPlayNetworking.registerGlobalReceiver(ClientPrefsPayload.TYPE, (payload, context) -> {
			ServerPlayer player = context.player();
			player.level().getServer().execute(() -> {
				player.setAttached(PlayerPrefs.ENABLE_NOTIFIERS, payload.enableNotifiers());
				player.setAttached(PlayerPrefs.SHOW_ATROCITY_COUNTER, payload.showAtrocityCounter());

				player.setAttached(PlayerPrefs.BENEDICTION_HEALTH_THRESHOLD, payload.benedictionLowHealthThreshold());
				player.setAttached(PlayerPrefs.BENEDICTION_SHARE_STACK, payload.benedictionShareStack());
				player.setAttached(PlayerPrefs.BENEDICTION_ALWAYS_TRIGGER_BLESSING, payload.benedictionAlwaysTriggerBlessing());
				player.setAttached(PlayerPrefs.BENEDICTION_TELEPORT_AFTER_SAVE, payload.benedictionTeleportAfterSave());
				player.setAttached(PlayerPrefs.BENEDICTION_WOLF_TP_METHOD, payload.benedictionWolfTPMethod());
				player.setAttached(PlayerPrefs.BENEDICTION_PLAYER_TP_METHOD, payload.benedictionPlayerTPMethod());
				player.setAttached(PlayerPrefs.BENEDICTION_WOLF_TP_ALL, payload.benedictionWolfTPAll());
			});
		});
	}

	@Override
	public void onTerraBlenderInitialized() {
		VWBiomes.registerBiomes();

		TOTVW.sendClassRegisterLog("[Dependency] TerraBlender");
	}
}