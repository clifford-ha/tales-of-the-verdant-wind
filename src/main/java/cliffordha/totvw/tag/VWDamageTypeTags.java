package cliffordha.totvw.tag;

import cliffordha.totvw.datagen.VWDamageTypes;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagEntry;
import net.minecraft.world.damagesource.DamageType;

import java.util.concurrent.CompletableFuture;

public class VWDamageTypeTags extends FabricTagsProvider<DamageType> {
    public VWDamageTypeTags(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, Registries.DAMAGE_TYPE, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {

        getOrCreateRawBuilder(DamageTypeTags.NO_KNOCKBACK)
                .add(TagEntry.element(VWDamageTypes.BLOODLUST.identifier()))
                .add(TagEntry.element(VWDamageTypes.BLEEDING.identifier()))
                .build();

        getOrCreateRawBuilder(DamageTypeTags.BYPASSES_ARMOR)
                .add(TagEntry.element(VWDamageTypes.BLOODLUST.identifier()))
                .add(TagEntry.element(VWDamageTypes.BLEEDING.identifier()))
                .add(TagEntry.element(VWDamageTypes.LODESTONE_WIND_CORE_PULSE.identifier()))
                .add(TagEntry.element(VWDamageTypes.SCORCHING_HEAT.identifier()))
                .build();

        getOrCreateRawBuilder(DamageTypeTags.BYPASSES_WOLF_ARMOR)
                .add(TagEntry.element(VWDamageTypes.BLOODLUST.identifier()))
                .add(TagEntry.element(VWDamageTypes.BLEEDING.identifier()))
                .build();

        getOrCreateRawBuilder(DamageTypeTags.ALWAYS_HURTS_ENDER_DRAGONS)
                .add(TagEntry.element(VWDamageTypes.LODESTONE_WIND_CORE_PULSE.identifier()))
                .add(TagEntry.element(VWDamageTypes.SCORCHING_HEAT.identifier()))
                .build();

        getOrCreateRawBuilder(DamageTypeTags.BYPASSES_INVULNERABILITY)
                .add(TagEntry.element(VWDamageTypes.LODESTONE_WIND_CORE_PULSE.identifier()))
                .build();

        getOrCreateRawBuilder(DamageTypeTags.BYPASSES_RESISTANCE)
                .add(TagEntry.element(VWDamageTypes.LODESTONE_WIND_CORE_PULSE.identifier()))
                .add(TagEntry.element(VWDamageTypes.SCORCHING_HEAT.identifier()))
                .add(TagEntry.element(VWDamageTypes.BLOODLUST.identifier()))
                .build();

        getOrCreateRawBuilder(DamageTypeTags.BYPASSES_SHIELD)
                .add(TagEntry.element(VWDamageTypes.LODESTONE_WIND_CORE_PULSE.identifier()))
                .add(TagEntry.element(VWDamageTypes.SCORCHING_HEAT.identifier()))
                .build();

        getOrCreateRawBuilder(DamageTypeTags.BYPASSES_EFFECTS)
                .add(TagEntry.element(VWDamageTypes.LODESTONE_WIND_CORE_PULSE.identifier()))
                .add(TagEntry.element(VWDamageTypes.BLEEDING.identifier()))
                .add(TagEntry.element(VWDamageTypes.SCORCHING_HEAT.identifier()))
                .build();
    }
}
