package cliffordha.totvw.block.entity;

import cliffordha.totvw.registry.VWBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class VWSignBlockEntity extends SignBlockEntity {
    public VWSignBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(VWBlockEntityTypes.SIGN, worldPosition, blockState);
    }

    @Override
    public BlockEntityType<?> getType() {
        return VWBlockEntityTypes.SIGN;
    }

    @Override
    public boolean isValidBlockState(BlockState blockState) {
        return this.getType().isValid(blockState);
    }
}
