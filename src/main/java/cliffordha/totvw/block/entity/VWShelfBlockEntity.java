package cliffordha.totvw.block.entity;

import cliffordha.totvw.registry.VWBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class VWShelfBlockEntity extends ShelfBlockEntity {
    public VWShelfBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(worldPosition, blockState);
    }

    @Override
    public BlockEntityType<?> getType() {
        return VWBlockEntityTypes.SHELF;
    }
}
