package cliffordha.totvw.block;

import cliffordha.totvw.block.entity.VWShelfBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.ShelfBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class VWShelfBlock extends ShelfBlock {
    public VWShelfBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new VWShelfBlockEntity(worldPosition, blockState);
    }
}
