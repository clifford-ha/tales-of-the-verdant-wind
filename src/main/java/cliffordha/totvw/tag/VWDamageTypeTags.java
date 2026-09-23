package cliffordha.totvw.tag;

import cliffordha.totvw.TOTVW;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;

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
                .add(type(BLEEDING));

        getOrCreateRawBuilder(DamageTypeTags.BYPASSES_ARMOR)
                .add(type(BLOODLUST))
                .add(type(BLEEDING))
                .add(type(HAVOC))
                .add(type(LODESTONE_WIND_CORE_PULSE))
                .add(type(SCORCHING_HEAT));

        getOrCreateRawBuilder(DamageTypeTags.BYPASSES_WOLF_ARMOR)
                .add(type(BLOODLUST))
                .add(type(BLEEDING));

        getOrCreateRawBuilder(DamageTypeTags.ALWAYS_HURTS_ENDER_DRAGONS)
                .add(type(LODESTONE_WIND_CORE_PULSE))
                .add(type(SCORCHING_HEAT));

        getOrCreateRawBuilder(DamageTypeTags.BYPASSES_INVULNERABILITY)
                .add(type(HAVOC))
                .add(type(LODESTONE_WIND_CORE_PULSE));

        getOrCreateRawBuilder(DamageTypeTags.BYPASSES_RESISTANCE)
                .add(type(HAVOC))
                .add(type(BLEEDING))
                .add(type(LODESTONE_WIND_CORE_PULSE))
                .add(type(SCORCHING_HEAT))
                .add(type(BLOODLUST));

        getOrCreateRawBuilder(DamageTypeTags.BYPASSES_SHIELD)
                .add(type(HAVOC))
                .add(type(LODESTONE_WIND_CORE_PULSE))
                .add(type(SCORCHING_HEAT));

        getOrCreateRawBuilder(DamageTypeTags.BYPASSES_EFFECTS)
                .add(type(LODESTONE_WIND_CORE_PULSE))
                .add(type(BLEEDING))
                .add(type(HAVOC))
                .add(type(SCORCHING_HEAT));

        getOrCreateRawBuilder(BENEDICTION_CAN_REDUCE_HIGH_DAMAGE)
                .add(type(DamageTypes.SONIC_BOOM))
                .add(type(DamageTypes.WITHER_SKULL))
                .add(type(DamageTypes.DRAGON_BREATH));
    }

    public static final TagKey<DamageType> BENEDICTION_CAN_REDUCE_HIGH_DAMAGE = create("benediction_can_reduce_high_damage");
    private static TagKey<DamageType> create(String name) {
        return TagKey.create(Registries.DAMAGE_TYPE, TOTVW.registerID(name)); }
}
