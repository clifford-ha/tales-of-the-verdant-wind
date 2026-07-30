package cliffordha.totvw.datagen;

import cliffordha.totvw.TOTVW;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;

public class VWDamageTypes {
    public static final ResourceKey<DamageType> BLOODLUST = createDMGType("bloodlust");
    public static final ResourceKey<DamageType> BLEEDING = createDMGType("bleeding");
    public static final ResourceKey<DamageType> LODESTONE_WIND_CORE_PULSE = createDMGType("wind_core_pulse");
    public static final ResourceKey<DamageType> SCORCHING_HEAT = createDMGType("scorching_heat");

    public static void bootstrap(BootstrapContext<DamageType> context) {
        context.register(BLOODLUST, new DamageType("bloodlust", 0.0f, DamageEffects.HURT));
        context.register(BLEEDING, new DamageType("bleeding", 0.2f, DamageEffects.HURT));
        context.register(LODESTONE_WIND_CORE_PULSE, new DamageType("wind_core_pulse", 0.3f, DamageEffects.HURT));
        context.register(SCORCHING_HEAT, new DamageType("scorching_heat", 0.4f, DamageEffects.HURT));
    }

    private static ResourceKey<DamageType> createDMGType(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name));
    }

    public static DamageSource create(Level level, ResourceKey<DamageType> key) {
        return new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(key));
    }
}