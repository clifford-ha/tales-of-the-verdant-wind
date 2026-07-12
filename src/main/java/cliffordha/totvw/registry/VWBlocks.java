package cliffordha.totvw.registry;

import cliffordha.totvw.block.custom.VerixiumPowderBlock;
import cliffordha.totvw.registry.blocks.VWBlocksVerdant;
import cliffordha.totvw.TOTVW;

import net.minecraft.util.ColorRGBA;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

import static cliffordha.totvw.TOTVW.sendLog;

public class VWBlocks {
    public final static Block VERIXIUM_DEEPSLATE_ORE = TOTVW.registerBlock("verixium_deepslate_ore",
            properties -> new Block( properties
                    .sound(SoundType.DEEPSLATE)
                    .requiresCorrectToolForDrops()
                    .lightLevel(_ -> 9)
                    .strength(3.5F, 60F)),
            BlockBehaviour.Properties.of(),
            true
    );
    public final static Block VERIXIUM_STONE_ORE = TOTVW.registerBlock("verixium_stone_ore",
            properties -> new Block( properties
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()
                    .lightLevel(_ -> 9)
                    .strength(3.0F, 30F)),
            BlockBehaviour.Properties.of(),
            true
    );
    public final static Block VERIXIUM_POWDER_BLOCK = TOTVW.registerBlock("verixium_powder_block",
            properties -> new VerixiumPowderBlock(new ColorRGBA(VWColors.VERDANT_WIND), properties
                    .sound(SoundType.SAND)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .lightLevel(_ -> 15)
                    .pushReaction(PushReaction.DESTROY)
                    .strength(1.5F, 90F)),
            BlockBehaviour.Properties.of(),
            true
    );
    public static final Block VERIXIUM_FLUID = TOTVW.registerBlock("verixium_fluid",
            (props) -> new LiquidBlock(VWFluids.VERIXIUM_FLUID, props),
            BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)
                    .mapColor(DyeColor.CYAN)
                    .lightLevel(_ -> 14)
                    .noLootTable()
                    .liquid(),
            false
    );
    public final static Block IRIDESCENT_GLASS = TOTVW.registerBlock("iridescent_glass",
            properties -> new TransparentBlock(properties
                    .lightLevel(_ -> 9)
            ),
            BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS),
            true
    );
    public final static Block IRIDESCENT_GLASS_PANE = TOTVW.registerBlock("iridescent_glass_pane",
            properties -> new StainedGlassPaneBlock(DyeColor.CYAN, properties
                    .lightLevel(_ -> 9)
            ),
            BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS_PANE),
            true
    );


    // separate blocks of the same family when >= 10 items
    // my forgetful ahh will remember better that way
    public static void register() {
        VWBlocksVerdant.registerVerdantBlocks();
        sendLog("Blocks");
    }
}