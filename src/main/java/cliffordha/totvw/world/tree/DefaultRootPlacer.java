package cliffordha.totvw.world.tree;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.rootplacers.AboveRootPlacement;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacer;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.Optional;
import java.util.function.BiConsumer;

public class DefaultRootPlacer extends RootPlacer {
    public DefaultRootPlacer(IntProvider trunkOffsetY, Holder<BlockStateProvider> rootProvider, Optional<AboveRootPlacement> aboveRootPlacement) {
        super(trunkOffsetY, rootProvider, aboveRootPlacement);
    }
    public static final MapCodec<RootPlacer> CODEC = RecordCodecBuilder.mapCodec( instance ->
            rootPlacerParts(instance).apply(instance, DefaultRootPlacer::new));

    @Override
    protected RootPlacerType<?> type() {
        return VWRootPlacerTypes.DEFAULT;
    }

    @Override
    public boolean placeRoots(WorldGenLevel level, BiConsumer<BlockPos, BlockState> rootSetter, RandomSource random, BlockPos origin, BlockPos trunkOrigin, TreeFeature tree) {
        return true;
    }

    public static Optional<RootPlacer> addDefault() {
        return Optional.of(
                new DefaultRootPlacer(
                        ConstantInt.of(0),
                        BlockStateProvider.holderOf(Blocks.DIRT),
                        Optional.empty()
                )
        );
    }
}
