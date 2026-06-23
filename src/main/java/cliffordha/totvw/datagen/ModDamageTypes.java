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

public class ModDamageTypes {
    public static final ResourceKey<DamageType> BLOODLUST = ResourceKey.create(Registries.DAMAGE_TYPE,
            Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "bloodlust"));

    public static final ResourceKey<DamageType> BLEEDING = ResourceKey.create(Registries.DAMAGE_TYPE,
            Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "bleeding"));

    public static void bootstrap(BootstrapContext<DamageType> context) {
        context.register(BLOODLUST, new DamageType("bloodlust", 0.0f, DamageEffects.HURT));
        context.register(BLEEDING, new DamageType("bleeding", 0.0f, DamageEffects.HURT));
    }

    public static DamageSource create(Level level, ResourceKey<DamageType> key) {
        return new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(key));
    }
}