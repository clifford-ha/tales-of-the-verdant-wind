package cliffordha.totvw;

import cliffordha.totvw.client.VWTooltips;
import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.particle.MightParalyzeParticle;
import cliffordha.totvw.particle.VerixiumPowderRainParticle;
import cliffordha.totvw.registry.*;
import cliffordha.totvw.client.VWModelLayerProvider;
import cliffordha.totvw.particle.BenedictionTriggerParticle;
import cliffordha.totvw.particle.VerdantBiomesEnvironmentAmbiance;
import cliffordha.totvw.client.ClientPrefsPayload;
import cliffordha.totvw.util.VWEffectOverlays;
import cliffordha.totvw.util.VWColorizeTextMixin;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.ShelfRenderer;
import net.minecraft.client.renderer.blockentity.StandingSignRenderer;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;

import java.util.List;

import static cliffordha.totvw.registry.VWColors.setColor;

public class TOTVWClient implements ClientModInitializer {
    public void onInitializeClient() {
        FluidRenderingRegistry.register(
                VWFluids.VERIXIUM_FLUID,
                VWFluids.FLOWING_VERIXIUM_FLUID,
                new FluidModel.Unbaked(
                        new Material(Identifier.withDefaultNamespace("block/water_still")),
                        new Material(Identifier.withDefaultNamespace("block/water_flow")),
                        new Material(Identifier.withDefaultNamespace("block/water_overlay")),
                        BlockTintSources.constant(setColor(0x13e1a8))
                )
        );
        BlockColorRegistry.register(
                List.of((_) -> getRainbowColor()),
                VWBlocks.IRIDESCENT_GLASS,
                VWBlocks.IRIDESCENT_GLASS_PANE
        );
        VWColorizeTextMixin.register(
                "enchantment.tales-of-the-verdant-wind.benediction_of_the_verdant_mountains",
                VWColors.VERDANT_WIND
        );
        VWColorizeTextMixin.register(
                "effect.tales-of-the-verdant-wind.blessing_of_the_verdant_wind",
                VWColors.VERDANT_WIND
        );
        VWColorizeTextMixin.register(
                "item.minecraft.tipped_arrow.effect.sacred_verdant_potion",
                VWColors.VERDANT_WIND_MUTED
        );
        VWColorizeTextMixin.register(
                "effect.tales-of-the-verdant-wind.bloodlust",
                VWColors.BLOODLUST_EFFECT
        );
        ParticleProviderRegistry.getInstance().register(VWParticles.BENEDICTION_TRIGGER_PARTICLE, BenedictionTriggerParticle.BenedictionParticleProvider::new);
        ParticleProviderRegistry.getInstance().register(VWParticles.VERDANT_BIOMES_ENVIRONMENT_AMBIANCE, VerdantBiomesEnvironmentAmbiance.VerdantBiomesEnvironmentAmbianceProvider::new);
        ParticleProviderRegistry.getInstance().register(VWParticles.VERIXIUM_POWDER_RAIN_PARTICLE, VerixiumPowderRainParticle.VerixiumPowderRainParticleProvider::new);
        ParticleProviderRegistry.getInstance().register(VWParticles.MIGHT_PARALYZE_PARTICLE, MightParalyzeParticle.MightParalyzeParticleProvider::new);

        EntityRenderers.register(VWEntities.VERDANT_SPRUCE_BOAT, context -> new BoatRenderer(context, VWModelLayerProvider.VERDANT_SPRUCE_BOAT));
        EntityRenderers.register(VWEntities.VERDANT_SPRUCE_CHEST_BOAT, context -> new BoatRenderer(context, VWModelLayerProvider.VERDANT_SPRUCE_CHEST_BOAT));
        ModelLayerRegistry.registerModelLayer(VWModelLayerProvider.VERDANT_SPRUCE_BOAT, BoatModel::createBoatModel);
        ModelLayerRegistry.registerModelLayer(VWModelLayerProvider.VERDANT_SPRUCE_CHEST_BOAT, BoatModel::createChestBoatModel);


        BlockEntityRenderers.register(VWBlockEntityTypes.SIGN, StandingSignRenderer::new);
        BlockEntityRenderers.register(VWBlockEntityTypes.HANGING_SIGN, HangingSignRenderer::new);
        BlockEntityRenderers.register(VWBlockEntityTypes.SHELF, ShelfRenderer::new);
        VWEffectOverlays.register();
        VWTooltips.register();
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) ->
                sender.sendPacket(new ClientPrefsPayload(
                        VWConfig.get().CLIENT_SHOW_ATROCITY_COUNTER,
                        VWConfig.get().CLIENT_ENABLE_NOTIFIERS,

                        VWConfig.get().SERVER_BENEDICTION_HEALTH_THRESHOLD,
                        VWConfig.get().SERVER_WOLF_SHARES_BENEDICTION_STACK,
                        VWConfig.get().SERVER_ALWAYS_TRIGGER_BLESSING,
                        VWConfig.get().SERVER_TELEPORT_AFTER_SAVE,
                        VWConfig.get().SERVER_WOLF_TP_METHOD,
                        VWConfig.get().SERVER_PLAYER_TP_METHOD,
                        VWConfig.get().SERVER_WOLF_TP_ALL
                ))
        );
    }
    private static int getRainbowColor() {
        float hue = (System.currentTimeMillis() % 4000) / 1000.0f;
        return java.awt.Color.HSBtoRGB(hue, 0.75f, 1.0f);
    }
}
