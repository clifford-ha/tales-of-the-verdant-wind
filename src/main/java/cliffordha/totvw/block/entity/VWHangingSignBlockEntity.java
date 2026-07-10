package cliffordha.totvw.block.entity;

import cliffordha.totvw.registry.VWBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class VWHangingSignBlockEntity extends HangingSignBlockEntity {
    public VWHangingSignBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(worldPosition, blockState);
    }

    @Override
    public BlockEntityType<?> getType() {
        return VWBlockEntityTypes.HANGING_SIGN;
    }
}
