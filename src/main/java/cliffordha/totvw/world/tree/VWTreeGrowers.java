package cliffordha.totvw.world.tree;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.worldgen.VWConfiguredFeatures;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public class VWTreeGrowers {
    public static final TreeGrower VERDANT = new TreeGrower(TOTVW.MOD_ID + ":verdant",
            WeightedList.of(VWConfiguredFeatures.ANCIENT_VERDANT_SPRUCE_TREE_CONFIGURED_KEY),
            WeightedList.of(VWConfiguredFeatures.VERDANT_SPRUCE_TREE_CONFIGURED_KEY),
            WeightedList.of(),
            VWConfiguredFeatures.VERDANT_SPRUCE_BUSH_TREE_CONFIGURED_KEY
    );
}