package cliffordha.totvw.block.custom;

import cliffordha.totvw.registry.ModColors;
import cliffordha.totvw.registry.ModParticleEffects;
import cliffordha.totvw.registry.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SandBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;

public class VerixiumPowderBlock extends SandBlock {
    public VerixiumPowderBlock(ColorRGBA dustColor, Properties properties) {
        super(dustColor, properties);
    }
    public final int dustColor = ModColors.VERDANT_WIND;

    @Override
    public int getDustColor(BlockState blockState, BlockGetter level, BlockPos pos) {
        return this.dustColor;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    @Override
    protected int getLightDampening(BlockState state) {
        return 0;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (level.getGameTime() % 10 * ( 1+ level.getRandom().nextFloat()) == 0) {
            if (level.isRaining() || level.isThundering()) {
                for (int i = 0; i < (4 * (1 + level.getRandom().nextFloat())); i++) {
                    level.addParticle(ModParticles.VERIXIUM_POWDER_RAIN_PARTICLE, pos.getX() + level.getRandom().nextFloat(), pos.getY() + 1, pos.getZ() + level.getRandom().nextFloat(), 0.0D, 0.0D, 0.0D);
                }
                if (level.getRandom().nextFloat() == 0.33f) {
                    level.playSound(null, pos, SoundEvents.SAND_IDLE, SoundSource.BLOCKS);
                }
            }
        }
        super.animateTick(state, level, pos, random);
    }
}
