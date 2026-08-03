package cliffordha.totvw.tag;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagEntry;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public class VWTagHelpers {
    public static TagEntry addBlock(Block block) {
        return TagEntry.element(BuiltInRegistries.BLOCK.getKey(block));
    }
    public static TagEntry addItem(Item item) {
        return TagEntry.element(BuiltInRegistries.ITEM.getKey(item));
    }

    public static TagEntry biome(ResourceKey<Biome> biome) {
        return TagEntry.element(biome.identifier());
    }
    public static TagEntry type(ResourceKey<DamageType> type) {
        return TagEntry.element(type.identifier());
    }
    public static TagEntry enchantment(ResourceKey<Enchantment> type) {
        return TagEntry.element(type.identifier());
    }
}
