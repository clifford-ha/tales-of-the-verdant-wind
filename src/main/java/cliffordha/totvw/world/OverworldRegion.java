package cliffordha.totvw.world;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.ParameterUtils;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.api.VanillaParameterOverlayBuilder;

import java.util.function.Consumer;

public class OverworldRegion extends Region {
    public OverworldRegion(Identifier name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        VanillaParameterOverlayBuilder builder = new VanillaParameterOverlayBuilder();

        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.span(
                        ParameterUtils.Temperature.COOL,
                        ParameterUtils.Temperature.NEUTRAL
                ))
                .humidity(ParameterUtils.Humidity.span(
                        ParameterUtils.Humidity.HUMID,
                        ParameterUtils.Humidity.WET))
                .continentalness(
                        ParameterUtils.Continentalness.INLAND
                )
                .erosion(
                        ParameterUtils.Erosion.EROSION_0,
                        ParameterUtils.Erosion.EROSION_1,
                        ParameterUtils.Erosion.EROSION_2
                )
                .depth(ParameterUtils.Depth.SURFACE, ParameterUtils.Depth.FLOOR)
                .weirdness(
                        ParameterUtils.Weirdness.LOW_SLICE_VARIANT_ASCENDING,
                        ParameterUtils.Weirdness.PEAK_NORMAL,
                        ParameterUtils.Weirdness.HIGH_SLICE_NORMAL_DESCENDING)

                .build().forEach(parameterPoint -> builder.add(parameterPoint, ModBiomes.VERDANT_MOUNTAINS));

        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.span(
                        ParameterUtils.Temperature.COOL,
                        ParameterUtils.Temperature.NEUTRAL
                ))
                .humidity(ParameterUtils.Humidity.span(
                        ParameterUtils.Humidity.HUMID,
                        ParameterUtils.Humidity.WET))
                .continentalness(
                        ParameterUtils.Continentalness.INLAND
                )
                .erosion(
                        ParameterUtils.Erosion.EROSION_0,
                        ParameterUtils.Erosion.EROSION_6
                )
                .depth(ParameterUtils.Depth.SURFACE, ParameterUtils.Depth.FLOOR)
                .weirdness(
                        ParameterUtils.Weirdness.MID_SLICE_NORMAL_DESCENDING,
                        ParameterUtils.Weirdness.HIGH_SLICE_NORMAL_DESCENDING
                )

                .build().forEach(parameterPoint -> builder.add(parameterPoint, ModBiomes.VERDANT_FOREST));

        builder.build().forEach(mapper);
    }
}
