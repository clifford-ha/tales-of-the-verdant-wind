package cliffordha.totvw.block.custom;

import cliffordha.totvw.registry.VWParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class VerixiumOreBlock extends Block {
    public VerixiumOreBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) {
            double value = 0;
            for (int i = 0; i < 16; i++) {
                level.addParticle(VWParticles.BENEDICTION_TRIGGER_PARTICLE, pos.getX() + level.getRandom().nextFloat(), pos.getY(), pos.getZ() + level.getRandom().nextFloat(), value, value, value);
            }
        }
        super.destroy(level, pos, state);
    }
}
