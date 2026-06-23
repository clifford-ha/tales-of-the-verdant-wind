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

        strippable(VerdantBlocks.VERDANT_SPRUCE_LOG, VerdantBlocks.STRIPPED_VERDANT_SPRUCE_LOG);
        strippable(VerdantBlocks.VERDANT_SPRUCE_WOOD, VerdantBlocks.STRIPPED_VERDANT_SPRUCE_WOOD);
    }


    private static void flammable(Block block, int burnChance, int spreadChance){
        FlammableBlockRegistry.getDefaultInstance().add(block, burnChance, spreadChance);
    }

    private static void strippable(Block baseBlock, Block strippedBlock){
        StrippableBlockRegistry.register(baseBlock, strippedBlock);
    }
}