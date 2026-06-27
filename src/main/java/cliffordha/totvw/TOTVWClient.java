package cliffordha.totvw;

import cliffordha.totvw.client.ModTooltipsClient;
import cliffordha.totvw.particle.MightParalyzeParticle;
import cliffordha.totvw.particle.VerixiumPowderRainParticle;
import cliffordha.totvw.registry.*;
import cliffordha.totvw.client.ModModelLayerProvider;
import cliffordha.totvw.particle.BenedictionTriggerParticle;
import cliffordha.totvw.particle.VerdantBiomesEnvironmentAmbiance;
import cliffordha.totvw.util.ModEffectOverlays;
import cliffordha.totvw.util.ModTextColors;

import net.fabricmc.api.ClientModInitializer;
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
import net.minecraft.world.level.block.TrapDoorBlock;

import java.util.List;

public class TOTVWClient implements ClientModInitializer {
    public void onInitializeClient() {
        FluidRenderingRegistry.register(
                ModFluids.VERIXIUM_FLUID,
                ModFluids.FLOWING_VERIXIUM_FLUID,
                new FluidModel.Unbaked(
                        new Material(Identifier.withDefaultNamespace("block/water_still")),
                        new Material(Identifier.withDefaultNamespace("block/water_flow")),
                        new Material(Identifier.withDefaultNamespace("block/water_overlay")),
                        BlockTintSources.constant(setColor(0x13e1a8))
                )
        );
        BlockColorRegistry.register(
                List.of((_) -> getRainbowColor()),
                ModBlocks.IRIDESCENT_GLASS,
                ModBlocks.IRIDESCENT_GLASS_PANE
        );
        ModTextColors.register(
                "enchantment.tales-of-the-verdant-wind.benediction_of_the_verdant_mountains",
                ModColors.VERDANT_WIND
        );
        ModTextColors.register(
                "effect.tales-of-the-verdant-wind.blessing_of_the_verdant_wind",
                ModColors.VERDANT_WIND
        );
        ModTextColors.register(
                "item.minecraft.tipped_arrow.effect.sacred_verdant_potion",
                ModColors.VERDANT_WIND
        );
        ModTextColors.register(
                "effect.tales-of-the-verdant-wind.bloodlust",
                ModColors.BLOODLUST_EFFECT
        );
        ParticleProviderRegistry.getInstance().register(ModParticles.BENEDICTION_TRIGGER_PARTICLE, BenedictionTriggerParticle.BenedictionParticleProvider::new);
        ParticleProviderRegistry.getInstance().register(ModParticles.VERDANT_BIOMES_ENVIRONMENT_AMBIANCE, VerdantBiomesEnvironmentAmbiance.VerdantBiomesEnvironmentAmbianceProvider::new);
        ParticleProviderRegistry.getInstance().register(ModParticles.VERIXIUM_POWDER_RAIN_PARTICLE, VerixiumPowderRainParticle.VerixiumPowderRainParticleProvider::new);
        ParticleProviderRegistry.getInstance().register(ModParticles.MIGHT_PARALYZE_PARTICLE, MightParalyzeParticle.MightParalyzeParticleProvider::new);

        EntityRenderers.register(ModEntities.VERDANT_SPRUCE_BOAT, context -> new BoatRenderer(context, ModModelLayerProvider.VERDANT_SPRUCE_BOAT));
        EntityRenderers.register(ModEntities.VERDANT_SPRUCE_CHEST_BOAT, context -> new BoatRenderer(context, ModModelLayerProvider.VERDANT_SPRUCE_CHEST_BOAT));
        ModelLayerRegistry.registerModelLayer(ModModelLayerProvider.VERDANT_SPRUCE_BOAT, BoatModel::createBoatModel);
        ModelLayerRegistry.registerModelLayer(ModModelLayerProvider.VERDANT_SPRUCE_CHEST_BOAT, BoatModel::createChestBoatModel);

        BlockEntityRenderers.register(ModBlockEntityTypes.SIGN, StandingSignRenderer::new);
        BlockEntityRenderers.register(ModBlockEntityTypes.HANGING_SIGN, HangingSignRenderer::new);
        BlockEntityRenderers.register(ModBlockEntityTypes.SHELF, ShelfRenderer::new);
        ModEffectOverlays.register();
        ModTooltipsClient.register();
    }
    private static int getRainbowColor() {
        float hue = (System.currentTimeMillis() % 4000) / 1000.0f;
        return java.awt.Color.HSBtoRGB(hue, 0.25f, 1.0f);
    }
    private static int setColor(int color) {
        return (0xff << 24) | color;
    }
}
