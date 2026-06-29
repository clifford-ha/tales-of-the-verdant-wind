package cliffordha.totvw.registry;

import cliffordha.totvw.registry.blocks.VerdantBlocks;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.minecraft.world.level.block.Block;

public class ModBlockProperties {
    public static void register() {
        flammable(VerdantBlocks.VERDANT_MOSS_BLOCK,   40, 60);
        flammable(VerdantBlocks.VERDANT_SPRUCE_LEAVES,   20, 40);
        flammable(VerdantBlocks.VERDANT_SPRUCE_SAPLING,   40, 60);
        flammableSet(20, 60,
                VerdantBlocks.VERDANT_SPRUCE_PLANKS,
                VerdantBlocks.VERDANT_SPRUCE_LOG,
                VerdantBlocks.STRIPPED_VERDANT_SPRUCE_LOG,
                VerdantBlocks.VERDANT_SPRUCE_WOOD,
                VerdantBlocks.STRIPPED_VERDANT_SPRUCE_WOOD,
                VerdantBlocks.VERDANT_SPRUCE_STAIRS,
                VerdantBlocks.VERDANT_SPRUCE_SLAB,
                VerdantBlocks.VERDANT_SPRUCE_FENCE,
                VerdantBlocks.VERDANT_SPRUCE_FENCE_GATE,
                VerdantBlocks.VERDANT_SPRUCE_DOOR,
                VerdantBlocks.VERDANT_SPRUCE_TRAPDOOR,
                VerdantBlocks.VERDANT_SPRUCE_SIGN,
                VerdantBlocks.VERDANT_SPRUCE_WALL_SIGN,
                VerdantBlocks.VERDANT_SPRUCE_HANGING_SIGN,
                VerdantBlocks.VERDANT_SPRUCE_WALL_HANGING_SIGN
        );

        strippable(VerdantBlocks.VERDANT_SPRUCE_LOG, VerdantBlocks.STRIPPED_VERDANT_SPRUCE_LOG);
        strippable(VerdantBlocks.VERDANT_SPRUCE_WOOD, VerdantBlocks.STRIPPED_VERDANT_SPRUCE_WOOD);
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