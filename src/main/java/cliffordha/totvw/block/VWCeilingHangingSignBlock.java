package cliffordha.totvw.block;

import cliffordha.totvw.block.entity.VWHangingSignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class VWCeilingHangingSignBlock extends CeilingHangingSignBlock {
    public VWCeilingHangingSignBlock(WoodType type, Properties properties) {
        super(type, properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new VWHangingSignBlockEntity(worldPosition, blockState);
    }
}
