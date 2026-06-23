package cliffordha.totvw.registry;

import cliffordha.totvw.TOTVW;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import static cliffordha.totvw.TOTVW.sendLog;

public class ModParticles {
    public static final SimpleParticleType BENEDICTION_TRIGGER_PARTICLE =
            registerParticle("benediction_trigger_particle", FabricParticleTypes.simple());

    public static final SimpleParticleType VERDANT_BIOMES_ENVIRONMENT_AMBIANCE =
            registerParticle("verdant_biomes_environment_ambiance", FabricParticleTypes.simple());

    public static final SimpleParticleType VERIXIUM_POWDER_RAIN_PARTICLE =
            registerParticle("verixium_powder_rain_particle", FabricParticleTypes.simple());

    public static final SimpleParticleType MIGHT_PARALYZE_PARTICLE =
            registerParticle("might_paralyze_particle", FabricParticleTypes.simple());


    private static SimpleParticleType registerParticle(String name, SimpleParticleType particleType) {
        return Registry.register(BuiltInRegistries.PARTICLE_TYPE, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name), particleType);
    }

    public static void registerModParticles() {
        sendLog("Particles");
    }
}