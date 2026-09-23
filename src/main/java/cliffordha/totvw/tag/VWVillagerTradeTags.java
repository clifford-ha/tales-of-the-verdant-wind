package cliffordha.totvw.tag;

import cliffordha.totvw.datagen.villager.VWVillagerTrades;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.VillagerTradeTags;
import net.minecraft.world.item.trading.VillagerTrade;

import java.util.concurrent.CompletableFuture;

import static cliffordha.totvw.tag.VWTagHelpers.trade;

public class VWVillagerTradeTags extends FabricTagsProvider<VillagerTrade> {
    public VWVillagerTradeTags(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, Registries.VILLAGER_TRADE, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        getOrCreateRawBuilder(VillagerTradeTags.WEAPONSMITH_LEVEL_2)
                .add(trade(VWVillagerTrades.WEAPONSMITH_2_RUNESTONE_FRAGMENT_3))
                .add(trade(VWVillagerTrades.WEAPONSMITH_2_WOLF_ATK_ENCHANTMENTS));


        getOrCreateRawBuilder(VillagerTradeTags.WEAPONSMITH_LEVEL_3)
                .add(trade(VWVillagerTrades.WEAPONSMITH_3_LODESTONE_WIND_CORE));


        getOrCreateRawBuilder(VillagerTradeTags.LIBRARIAN_LEVEL_2)
                .add(trade(VWVillagerTrades.LIBRARIAN_2_WOLF_ATK_ENCHANTMENTS))
                .add(trade(VWVillagerTrades.LIBRARIAN_2_VERIXIUM_PAPER));


        getOrCreateRawBuilder(VillagerTradeTags.CLERIC_LEVEL_2)
                .add(trade(VWVillagerTrades.CLERIC_2_VERIXIUM_POWDER))
                .add(trade(VWVillagerTrades.CLERIC_2_EMERALD));


        getOrCreateRawBuilder(VillagerTradeTags.ARMORER_LEVEL_1)
                .add(trade(VWVillagerTrades.ARMORER_1_WOLF_ENHANCEMENT_KIT));
        getOrCreateRawBuilder(VillagerTradeTags.ARMORER_LEVEL_4)
                .add(trade(VWVillagerTrades.ARMORER_4_VERIXIUM_WOLF_ARMOR));
        getOrCreateRawBuilder(VillagerTradeTags.ARMORER_LEVEL_5)
                .add(trade(VWVillagerTrades.ARMORER_5_VERIXIUM_ARMOR_UPGRADE_TEMPLATE));


        getOrCreateRawBuilder(VillagerTradeTags.WANDERING_TRADER_COMMON)
                .add(trade(VWVillagerTrades.WANDERING_VERDANT_SPRUCE_TREE_SAPLING));
        getOrCreateRawBuilder(VillagerTradeTags.WANDERING_TRADER_UNCOMMON)
                .add(trade(VWVillagerTrades.WANDERING_FAR_AWAY_ENCHANTMENTS));
    }
}
