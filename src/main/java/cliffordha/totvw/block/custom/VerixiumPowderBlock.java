package cliffordha.totvw.block.custom;

import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.VWParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SandBlock;
import net.minecraft.world.level.block.state.BlockState;

public class VerixiumPowderBlock extends SandBlock {
    public VerixiumPowderBlock(ColorRGBA dustColor, Properties properties) {
        super(dustColor, properties);
    }
    public final int dustColor = VWColors.VERDANT_WIND;

    @Override
    public int getDustColor(BlockState blockState, BlockGetter level, BlockPos pos) {
        return this.dustColor;
    }

    @Override
    protected int getLightDampening(BlockState state) {
        return 0;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (level.isRaining() || level.isThundering()) {
            if (level.getGameTime() % 10 * ( 1+ level.getRandom().nextFloat()) == 0) {
                for (int i = 0; i < (4 * (1 + level.getRandom().nextFloat())); i++) {
                    level.addParticle(VWParticles.VERIXIUM_POWDER_RAIN_PARTICLE, pos.getX() + level.getRandom().nextFloat(), pos.getY() + 1, pos.getZ() + level.getRandom().nextFloat(), 0.0D, 0.0D, 0.0D);
                }
                if (level.getRandom().nextFloat() == 0.33f) {
                    level.playSound(null, pos, SoundEvents.SAND_IDLE, SoundSource.BLOCKS);
                }
            }
        }
        super.animateTick(state, level, pos, random);
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) {
            double value = 0;
            for (int i = 0; i < 24; i++) {
                level.addParticle(VWParticles.BENEDICTION_TRIGGER_PARTICLE, pos.getX() + level.getRandom().nextFloat(), pos.getY(), pos.getZ() + level.getRandom().nextFloat(), value, value, value);
            }
        }
        super.destroy(level, pos, state);
    }
}
