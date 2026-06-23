package cliffordha.totvw.block.custom;

import cliffordha.totvw.registry.ModParticles;
import cliffordha.totvw.world.ModBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.UntintedParticleLeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

public class VerdantSpruceLeavesBlock extends UntintedParticleLeavesBlock {
    public VerdantSpruceLeavesBlock(float leafParticleChance, ParticleOptions particle, Properties properties) {
        super(leafParticleChance, particle, properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        boolean inVerdantForest = level.getBiome(pos).is(ModBiomes.VERDANT_FOREST);
        boolean inVerdantMountains = level.getBiome(pos).is(ModBiomes.VERDANT_MOUNTAINS);

        boolean darkness = level.getMaxLocalRawBrightness(pos, 0) < 13;
        double randomChance = random.nextDouble();

        double glowX = (double)pos.getX() + random.nextDouble() * 13.0 - 5.0;
        double glowY = (double)pos.getY() + random.nextDouble() * 9.0;
        double glowZ = (double)pos.getZ() + random.nextDouble() * 13.0 - 5.0;

        if (darkness && randomChance <= 0.09 && inVerdantMountains) {
            level.addParticle(ModParticles.VERDANT_BIOMES_ENVIRONMENT_AMBIANCE, glowX, glowY, glowZ, 9D, 13D, 9D );
        } else if (darkness && randomChance <= 0.1 && inVerdantForest) {
            level.addParticle(ModParticles.VERDANT_BIOMES_ENVIRONMENT_AMBIANCE, glowX, glowY, glowZ, 13D, 15D, 13D );
        }
        super.animateTick(state, level, pos, random);
    }
}
