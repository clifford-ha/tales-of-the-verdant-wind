package cliffordha.totvw.loot;

import cliffordha.totvw.registry.ModEnchantments;
import cliffordha.totvw.registry.ModItems;
import net.fabricmc.fabric.api.loot.v3.FabricLootTableBuilder;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class ModLootTableModifier {
    public static void modifyLootTables(ResourceKey<LootTable> key, FabricLootTableBuilder builder, LootTableSource source, HolderLookup.Provider provider) {

        if(BuiltInLootTables.ANCIENT_CITY.equals(key)) {
            LootPool.Builder poolBuilder = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .when(LootItemRandomChanceCondition.randomChance(0.07f))
                    .add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).apply(new SetEnchantmentsFunction.Builder()
                            .withEnchantment(provider.getOrThrow(ModEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS), ConstantValue.exactly(1))))
                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)).build());
            builder.pool(poolBuilder.build());
        }
        if(BuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_RARE.equals(key)) {
            LootPool.Builder poolBuilder = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .when(LootItemRandomChanceCondition.randomChance(0.33f))
                    .add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).apply(new SetEnchantmentsFunction.Builder()
                            .withEnchantment(provider.getOrThrow(ModEnchantments.WOLF_EFFECT_WITHERING), ConstantValue.exactly(5))))
                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)).build());
            builder.pool(poolBuilder.build());
        }
        if(BuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_RARE.equals(key)) {
            LootPool.Builder poolBuilder = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .when(LootItemRandomChanceCondition.randomChance(0.33f))
                    .add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).apply(new SetEnchantmentsFunction.Builder()
                            .withEnchantment(provider.getOrThrow(ModEnchantments.WOLF_EFFECT_POISONING), ConstantValue.exactly(5))))
                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)).build());
            builder.pool(poolBuilder.build());
        }
        if(BuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_RARE.equals(key)) {
            LootPool.Builder poolBuilder = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .when(LootItemRandomChanceCondition.randomChance(0.33f))
                    .add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).apply(new SetEnchantmentsFunction.Builder()
                            .withEnchantment(provider.getOrThrow(ModEnchantments.WOLF_EFFECT_MIGHT), ConstantValue.exactly(5))))
                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)).build());
            builder.pool(poolBuilder.build());
        }
        if(BuiltInLootTables.BURIED_TREASURE.equals(key)) {
            LootPool.Builder poolBuilder = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .when(LootItemRandomChanceCondition.randomChance(0.14f))
                    .add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).apply(new SetEnchantmentsFunction.Builder()
                            .withEnchantment(provider.getOrThrow(ModEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS), ConstantValue.exactly(1))))
                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)).build());
            builder.pool(poolBuilder.build());
        }
        if(BuiltInLootTables.BABY_VILLAGER_GIFT.equals(key)) {
            LootPool.Builder poolBuilder = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .when(LootItemRandomChanceCondition.randomChance(0.80f))
                    .add(LootItem.lootTableItem(ModItems.VERIXIUM_ARMOR_UPGRADE_TEMPLATE).apply(new SetEnchantmentsFunction.Builder()))
                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)).build());
            builder.pool(poolBuilder.build());
        }
    }
}
