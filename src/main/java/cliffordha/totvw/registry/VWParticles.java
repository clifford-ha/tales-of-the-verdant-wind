package cliffordha.totvw.registry;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.util.ExtUtil;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

import static cliffordha.totvw.TOTVW.sendClassRegisterLog;

public class VWParticles {
    public static final SimpleParticleType BENEDICTION_TRIGGER =
            registerParticle("benediction_trigger", FabricParticleTypes.simple());

    public static final SimpleParticleType VERDANT_BIOMES_AMBIENCE =
            registerParticle("verdant_biomes_ambience", FabricParticleTypes.simple());

    public static final SimpleParticleType VERIXIUM_POWDER_RAIN =
            registerParticle("verixium_powder_rain", FabricParticleTypes.simple());

    public static final SimpleParticleType MIGHT_PARALYZE =
            registerParticle("might_paralyze", FabricParticleTypes.simple());

    public static final SimpleParticleType EXT_SNOWFLAKE =
            registerParticle("ext_snowflake", FabricParticleTypes.simple());


    private static SimpleParticleType registerParticle(String name, SimpleParticleType particleType) {
        return Registry.register(BuiltInRegistries.PARTICLE_TYPE, TOTVW.registerID(name), particleType);
    }
    public static void showBlessingParticle(LivingEntity entity, int frequency) {
        if (!(entity.level() instanceof ServerLevel level)) return;
        DustParticleOptions dust = new DustParticleOptions(VWColors.VERDANT_WIND, level.getRandom().nextFloat() + 0.5f);
        ExtUtil.sendParticles(dust, level, entity.blockPosition(), 4 * (frequency + 1), 1);
    }

    public static void register() {
        sendClassRegisterLog("Particles");
    }
}