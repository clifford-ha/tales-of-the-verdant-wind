package cliffordha.totvw.world.tree;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.worldgen.ModConfiguredFeatures;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public class ModTreeGrowers {
    public static final TreeGrower VERDANT = new TreeGrower(TOTVW.MOD_ID + ":verdant",
            Optional.of(ModConfiguredFeatures.ANCIENT_VERDANT_SPRUCE_TREE_CONFIGURED_KEY), Optional.of(ModConfiguredFeatures.VERDANT_SPRUCE_TREE_CONFIGURED_KEY), Optional.empty());
}