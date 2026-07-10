package cliffordha.totvw.registry;

import cliffordha.totvw.registry.blocks.VWBlocksVerdant;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.minecraft.world.level.block.Block;

public class VWBlockProperties {
    public static void register() {
        flammable(VWBlocksVerdant.VERDANT_MOSS_BLOCK,   40, 60);
        flammable(VWBlocksVerdant.VERDANT_SPRUCE_LEAVES,   20, 40);
        flammable(VWBlocksVerdant.VERDANT_SPRUCE_SAPLING,   40, 60);
        flammableSet(20, 60,
                VWBlocksVerdant.VERDANT_SPRUCE_PLANKS,
                VWBlocksVerdant.VERDANT_SPRUCE_LOG,
                VWBlocksVerdant.STRIPPED_VERDANT_SPRUCE_LOG,
                VWBlocksVerdant.VERDANT_SPRUCE_WOOD,
                VWBlocksVerdant.STRIPPED_VERDANT_SPRUCE_WOOD,
                VWBlocksVerdant.VERDANT_SPRUCE_STAIRS,
                VWBlocksVerdant.VERDANT_SPRUCE_SLAB,
                VWBlocksVerdant.VERDANT_SPRUCE_FENCE,
                VWBlocksVerdant.VERDANT_SPRUCE_FENCE_GATE,
                VWBlocksVerdant.VERDANT_SPRUCE_DOOR,
                VWBlocksVerdant.VERDANT_SPRUCE_TRAPDOOR,
                VWBlocksVerdant.VERDANT_SPRUCE_SIGN,
                VWBlocksVerdant.VERDANT_SPRUCE_WALL_SIGN,
                VWBlocksVerdant.VERDANT_SPRUCE_HANGING_SIGN,
                VWBlocksVerdant.VERDANT_SPRUCE_WALL_HANGING_SIGN
        );

        strippable(VWBlocksVerdant.VERDANT_SPRUCE_LOG, VWBlocksVerdant.STRIPPED_VERDANT_SPRUCE_LOG);
        strippable(VWBlocksVerdant.VERDANT_SPRUCE_WOOD, VWBlocksVerdant.STRIPPED_VERDANT_SPRUCE_WOOD);
    }


    private static void flammable(Block block, int burnChance, int spreadChance){
        FlammableBlockRegistry.getDefaultInstance().add(block, burnChance, spreadChance);
    }

    private static void flammableSet(int burnChance, int spreadChance, Block... blocks){
        for(Block block : blocks) {
            FlammableBlockRegistry.getDefaultInstance().add(block, burnChance, spreadChance);
        }
    }

    private static void strippable(Block baseBlock, Block strippedBlock){
        StrippableBlockRegistry.register(baseBlock, strippedBlock);
    }
}