package cliffordha.totvw.tag;


import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;

import java.util.concurrent.CompletableFuture;

import static cliffordha.totvw.tag.VWTagHelpers.type;
import static cliffordha.totvw.datagen.VWDamageTypes.*;

public class VWDamageTypeTags extends FabricTagsProvider<DamageType> {
    public VWDamageTypeTags(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, Registries.DAMAGE_TYPE, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {

        getOrCreateRawBuilder(DamageTypeTags.NO_KNOCKBACK)
                .add(type(BLOODLUST))
                .add(type(BLEEDING))
                .build();

        getOrCreateRawBuilder(DamageTypeTags.BYPASSES_ARMOR)
                .add(type(BLOODLUST))
                .add(type(BLEEDING))
                .add(type(LODESTONE_WIND_CORE_PULSE))
                .add(type(SCORCHING_HEAT))
                .build();

        getOrCreateRawBuilder(DamageTypeTags.BYPASSES_WOLF_ARMOR)
                .add(type(BLOODLUST))
                .add(type(BLEEDING))
                .build();

        getOrCreateRawBuilder(DamageTypeTags.ALWAYS_HURTS_ENDER_DRAGONS)
                .add(type(LODESTONE_WIND_CORE_PULSE))
                .add(type(SCORCHING_HEAT))
                .build();

        getOrCreateRawBuilder(DamageTypeTags.BYPASSES_INVULNERABILITY)
                .add(type(LODESTONE_WIND_CORE_PULSE))
                .build();

        getOrCreateRawBuilder(DamageTypeTags.BYPASSES_RESISTANCE)
                .add(type(LODESTONE_WIND_CORE_PULSE))
                .add(type(SCORCHING_HEAT))
                .add(type(BLOODLUST))
                .build();

        getOrCreateRawBuilder(DamageTypeTags.BYPASSES_SHIELD)
                .add(type(LODESTONE_WIND_CORE_PULSE))
                .add(type(SCORCHING_HEAT))
                .build();

        getOrCreateRawBuilder(DamageTypeTags.BYPASSES_EFFECTS)
                .add(type(LODESTONE_WIND_CORE_PULSE))
                .add(type(BLEEDING))
                .add(type(SCORCHING_HEAT))
                .build();
    }
}
