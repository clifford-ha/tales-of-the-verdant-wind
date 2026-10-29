package cliffordha.totvw;

import cliffordha.totvw.client.VWTooltips;
import cliffordha.totvw.client.screen.ConfigScreen;
import cliffordha.totvw.item.pages.ScatteredPageItem;
import cliffordha.totvw.client.VWKeymap;
import cliffordha.totvw.networking.ClientboundPackets;
import cliffordha.totvw.particle.*;
import cliffordha.totvw.registry.*;
import cliffordha.totvw.client.VWModelLayerProvider;
import cliffordha.totvw.client.EffectOverlay;
import cliffordha.totvw.util.ColorizeText;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.particle.FireflyParticle;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.ShelfRenderer;
import net.minecraft.client.renderer.blockentity.StandingSignRenderer;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;

import java.awt.*;
import java.util.List;

import static cliffordha.totvw.registry.VWColors.shadeColor;

public class TOTVWClient implements ClientModInitializer {
    public void onInitializeClient() {
        FluidRenderingRegistry.register(
                VWFluids.VERIXIUM_FLUID,
                VWFluids.FLOWING_VERIXIUM_FLUID,
                new FluidModel.Unbaked(
                        new Material(Identifier.withDefaultNamespace("block/water_still")),
                        new Material(Identifier.withDefaultNamespace("block/water_flow")),
                        new Material(Identifier.withDefaultNamespace("block/water_overlay")),
                        BlockTintSources.constant(VWColors.setColor(0x13e1a8))
                )
        );
        BlockColorRegistry.register(
                List.of((_) -> getRainbowColor()),
                VWBlocks.IRIDESCENT_GLASS,
                VWBlocks.IRIDESCENT_GLASS_PANE
        );
        ColorizeText.register(VWColors.VERDANT_WIND, List.of(
                getEnchantmentKey("benediction_of_the_verdant_mountains"),

                "item.minecraft.tipped_arrow.effect.sacred_verdant_potion",
                "item.minecraft.potion.effect.sacred_verdant_potion",
                "item.minecraft.splash_potion.effect.sacred_verdant_potion",
                "item.minecraft.lingering_potion.effect.sacred_verdant_potion",

                getEffectKey("blessing_of_the_verdant_wind"),
                getEffectKey("wind_veil")
                )
        );
        ColorizeText.register(VWColors.ARCTIC_WINDS, getEnchantmentKey("wolf_effect_arctic_winds"));
        ColorizeText.register(shadeColor(VWColors.MIGHT, 0.1f), getEnchantmentKey("wolf_effect_might"));
        ColorizeText.register(shadeColor(VWColors.BLOODLUST, 0.1f), getEnchantmentKey("wolf_effect_bloodlust"));
        ColorizeText.register(VWColors.GNAWING, getEnchantmentKey("wolf_effect_gnawing"));
        ColorizeText.register(VWColors.WITHERING, getEnchantmentKey("wolf_effect_withering"));
        ColorizeText.register(VWColors.POISON, getEnchantmentKey("wolf_effect_poisoning"));
        ColorizeText.register(VWColors.POISON, getEnchantmentKey("wolf_effect_oozing"));
        ColorizeText.register(VWColors.IGNITION, getEnchantmentKey("wolf_effect_ignition"));

        ColorizeText.register(VWColors.BLOODLUST, getEffectKey("bloodlust"));
        ColorizeText.register(VWColors.HAVOC_PARTICLE, getEffectKey("havoc"));

        ParticleProviderRegistry.getInstance().register(VWParticles.BENEDICTION_TRIGGER, BenedictionTriggerParticle.BenedictionParticleProvider::new);
        ParticleProviderRegistry.getInstance().register(VWParticles.VERDANT_BIOMES_AMBIENCE, FireflyParticle.FireflyProvider::new);
        ParticleProviderRegistry.getInstance().register(VWParticles.VERIXIUM_POWDER_RAIN, VerixiumPowderRainParticle.VerixiumPowderRainParticleProvider::new);
        ParticleProviderRegistry.getInstance().register(VWParticles.MIGHT_PARALYZE, MightParalyzeParticle.MightParalyzeParticleProvider::new);
        ParticleProviderRegistry.getInstance().register(VWParticles.EXT_SNOWFLAKE, ExtendedSnowflakeParticle.Provider::new);

        EntityRenderers.register(VWEntities.VERDANT_SPRUCE_BOAT, context -> new BoatRenderer(context, VWModelLayerProvider.VERDANT_SPRUCE_BOAT));
        EntityRenderers.register(VWEntities.VERDANT_SPRUCE_CHEST_BOAT, context -> new BoatRenderer(context, VWModelLayerProvider.VERDANT_SPRUCE_CHEST_BOAT));

        ModelLayerRegistry.registerModelLayer(VWModelLayerProvider.VERDANT_SPRUCE_BOAT, BoatModel::createBoatModel);
        ModelLayerRegistry.registerModelLayer(VWModelLayerProvider.VERDANT_SPRUCE_CHEST_BOAT, BoatModel::createChestBoatModel);

        LivingEntityRenderLayerRegistrationCallback.EVENT.register(
                (entityType, renderer, helper, context) -> {
                    helper.register(
                           new ArcticOverlayLayer<>(renderer)
                   );
                });


        BlockEntityRenderers.register(VWBlockEntityTypes.SIGN, StandingSignRenderer::new);
        BlockEntityRenderers.register(VWBlockEntityTypes.HANGING_SIGN, HangingSignRenderer::new);
        BlockEntityRenderers.register(VWBlockEntityTypes.SHELF, ShelfRenderer::new);
        EffectOverlay.register();
        VWTooltips.register();

        VWKeymap.register();

        ClientboundPackets.registerPackets();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Player player = client.player;
            if (player == null) return;

            if (VWKeymap.isWolfConfigKeyDown()) {
                ConfigScreen.create();
            } else if (VWKeymap.isStatusScreenKeyDown()) {
                if (client.crosshairPickEntity == null) {
                    ScatteredPageItem.openStatScreen(client.player);
                } else {
                    if (client.crosshairPickEntity instanceof Wolf wolf && wolf.getOwner() == client.player) {
                        ScatteredPageItem.showLiveWolfStats(wolf);
                        return;
                    }
                    ScatteredPageItem.openStatScreen(client.player);
                }
            }
        });
    }

    private static int getRainbowColor() {
        float hue = (System.currentTimeMillis() % 4000) / 1000.0f;
        return Color.HSBtoRGB(hue, 0.75f, 1.0f);
    }
    private static class ArcticOverlayLayer<S extends LivingEntityRenderState, M extends EntityModel<? super S>>
            extends RenderLayer<S, M> {
        private static final Identifier TEXTURE = TOTVW.registerID("textures/blocks/ice.png");

        public ArcticOverlayLayer(LivingEntityRenderer<?, ?, ?> renderer) {
            super((RenderLayerParent<S, M>) renderer);
        }

        @Override
        public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, S state, float yRot, float xRot) {

        }
    }
    private static String getEnchantmentKey(String key) {
        return "enchantment." + TOTVW.MOD_ID + "." + key;
    }
    private static String getEffectKey(String key) {
        return "effect." + TOTVW.MOD_ID + "." + key;
    }
}
