package cliffordha.totvw.block;

import cliffordha.totvw.block.entity.VWHangingSignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class VWWallHangingSignBlock extends WallHangingSignBlock {
    public VWWallHangingSignBlock(WoodType type, Properties properties) {
        super(type, properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VWHangingSignBlockEntity(pos, state);
    }
}
