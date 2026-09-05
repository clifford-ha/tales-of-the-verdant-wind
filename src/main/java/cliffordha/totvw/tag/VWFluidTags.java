package cliffordha.totvw.tag;

import cliffordha.totvw.TOTVW;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class VWFluidTags extends FabricTagsProvider<Fluid> {
    public VWFluidTags(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, Registries.FLUID, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider registries) {
        getOrCreateRawBuilder(VWFluidTags.VERIXIUM_FLUID)
                .add(TagEntry.element(Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verixium_fluid")))
                .add(TagEntry.element(Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "flowing_verixium_fluid")));


        getOrCreateRawBuilder(FluidTags.WATER)
                .addTag(VWFluidTags.VERIXIUM_FLUID.location());

        getOrCreateRawBuilder(FluidTags.BUBBLE_COLUMN_CAN_OCCUPY)
                .addTag(VWFluidTags.VERIXIUM_FLUID.location());

        getOrCreateRawBuilder(FluidTags.SUPPORTS_LILY_PAD)
                .addTag(VWFluidTags.VERIXIUM_FLUID.location());

        getOrCreateRawBuilder(FluidTags.SUPPORTS_SUGAR_CANE_ADJACENTLY)
                .addTag(VWFluidTags.VERIXIUM_FLUID.location());
    }

    public static final TagKey<Fluid> VERIXIUM_FLUID = TagKey.create(Registries.FLUID, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verixium_fluid"));
}