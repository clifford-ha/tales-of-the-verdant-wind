package cliffordha.totvw.tag;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagEntry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class VWTagHelpers {
    public static TagEntry addEntry(Block block) {
        return TagEntry.element(BuiltInRegistries.BLOCK.getKey(block));
    }
    public static TagEntry addEntry(Item item) {
        return TagEntry.element(BuiltInRegistries.ITEM.getKey(item));
    }
}
