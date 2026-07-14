package cliffordha.totvw.loot;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public class VWLootTables {
    public static final ResourceKey<LootTable> VERDANT_CAMP_VALUABLES = ResourceKey.create(
            Registries.LOOT_TABLE,
            Identifier.fromNamespaceAndPath("tales-of-the-verdant-wind", "chests/verdant_camp_valuables")
    );
}
