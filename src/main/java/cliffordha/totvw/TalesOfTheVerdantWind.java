package cliffordha.totvw;

import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.loot.VWLootTables;
import cliffordha.totvw.registry.*;
import cliffordha.totvw.client.ClientPrefsPayload;
import cliffordha.totvw.registry.attachments.VWAttachments;
import cliffordha.totvw.registry.attachments.VWPlayerPrefs;
import cliffordha.totvw.world.*;

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

		VWCommands.register();
		VWLootTables.registerModifiers();

		VWConfig.load();
		VWConfig.save();

		PayloadTypeRegistry.serverboundPlay().register(ClientPrefsPayload.TYPE, ClientPrefsPayload.STREAM_CODEC);

		ServerPlayNetworking.registerGlobalReceiver(ClientPrefsPayload.TYPE, (payload, context) -> {
			ServerPlayer player = context.player();
			player.level().getServer().execute(() -> {
				player.setAttached(VWPlayerPrefs.ENABLE_NOTIFIERS, payload.enableNotifiers());
				player.setAttached(VWPlayerPrefs.SHOW_ATROCITY_COUNTER, payload.showAtrocityCounter());

				player.setAttached(VWPlayerPrefs.BENEDICTION_HEALTH_THRESHOLD, payload.benedictionLowHealthThreshold());
				player.setAttached(VWPlayerPrefs.BENEDICTION_SHARE_STACK, payload.benedictionShareStack());
				player.setAttached(VWPlayerPrefs.BENEDICTION_ALWAYS_TRIGGER_BLESSING, payload.benedictionAlwaysTriggerBlessing());
				player.setAttached(VWPlayerPrefs.BENEDICTION_TELEPORT_AFTER_SAVE, payload.benedictionTeleportAfterSave());
				player.setAttached(VWPlayerPrefs.BENEDICTION_WOLF_TP_METHOD, payload.benedictionWolfTPMethod());
				player.setAttached(VWPlayerPrefs.BENEDICTION_PLAYER_TP_METHOD, payload.benedictionPlayerTPMethod());
				player.setAttached(VWPlayerPrefs.BENEDICTION_WOLF_TP_ALL, payload.benedictionWolfTPAll());
			});
		});
	}

	@Override
	public void onTerraBlenderInitialized() {
		VWBiomes.registerBiomes();

		TOTVW.sendClassRegisterLog("[Dependency] TerraBlender");
	}
}