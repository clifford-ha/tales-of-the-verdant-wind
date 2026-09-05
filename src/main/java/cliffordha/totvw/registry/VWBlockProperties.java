package cliffordha.totvw.registry;

import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.minecraft.world.level.block.Block;

public class VWBlockProperties {
    public static void register() {
        flammable(VWBlocks.VERDANT_MOSS_BLOCK,   40, 60);
        flammable(VWBlocks.VERDANT_SPRUCE_LEAVES,   20, 40);
        flammable(VWBlocks.VERDANT_SPRUCE_SAPLING,   40, 60);
        flammableSet(20, 60,
                VWBlocks.VERDANT_SPRUCE_PLANKS,
                VWBlocks.VERDANT_SPRUCE_LOG,
                VWBlocks.STRIPPED_VERDANT_SPRUCE_LOG,
                VWBlocks.VERDANT_SPRUCE_WOOD,
                VWBlocks.STRIPPED_VERDANT_SPRUCE_WOOD,
                VWBlocks.VERDANT_SPRUCE_STAIRS,
                VWBlocks.VERDANT_SPRUCE_SLAB,
                VWBlocks.VERDANT_SPRUCE_FENCE,
                VWBlocks.VERDANT_SPRUCE_FENCE_GATE,
                VWBlocks.VERDANT_SPRUCE_DOOR,
                VWBlocks.VERDANT_SPRUCE_TRAPDOOR,
                VWBlocks.VERDANT_SPRUCE_SIGN,
                VWBlocks.VERDANT_SPRUCE_WALL_SIGN,
                VWBlocks.VERDANT_SPRUCE_HANGING_SIGN,
                VWBlocks.VERDANT_SPRUCE_WALL_HANGING_SIGN
        );

        strippable(VWBlocks.VERDANT_SPRUCE_LOG, VWBlocks.STRIPPED_VERDANT_SPRUCE_LOG);
        strippable(VWBlocks.VERDANT_SPRUCE_WOOD, VWBlocks.STRIPPED_VERDANT_SPRUCE_WOOD);
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