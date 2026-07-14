package cliffordha.totvw.loot;

import cliffordha.totvw.registry.VWEnchantments;
import cliffordha.totvw.registry.VWItems;
import net.fabricmc.fabric.api.loot.v3.FabricLootTableBuilder;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class VWLootTableModifier {
    public static void modifyLootTables(ResourceKey<LootTable> key, FabricLootTableBuilder builder, LootTableSource source, HolderLookup.Provider provider) {

        if(BuiltInLootTables.ANCIENT_CITY.equals(key)) {
            LootPool.Builder poolBuilder = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .when(LootItemRandomChanceCondition.randomChance(0.07f))
                    .add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).apply(new SetEnchantmentsFunction.Builder()
                            .withEnchantment(provider.getOrThrow(VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS), ConstantValue.exactly(1))))
                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)).build());
            builder.pool(poolBuilder.build());
        }
        if(BuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_RARE.equals(key)) {
            LootPool.Builder poolBuilder = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .when(LootItemRandomChanceCondition.randomChance(0.1f))
                    .add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).apply(new SetEnchantmentsFunction.Builder()
                            .withEnchantment(provider.getOrThrow(VWEnchantments.WOLF_EFFECT_WITHERING), UniformGenerator.between(1, 3))))
                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)).build());
            builder.pool(poolBuilder.build());
        }
        if(BuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_RARE.equals(key)) {
            LootPool.Builder poolBuilder = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .when(LootItemRandomChanceCondition.randomChance(0.1f))
                    .add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).apply(new SetEnchantmentsFunction.Builder()
                            .withEnchantment(provider.getOrThrow(VWEnchantments.WOLF_EFFECT_POISONING), UniformGenerator.between(3, 5))))
                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)).build());
            builder.pool(poolBuilder.build());
        }
        if(BuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_RARE.equals(key)) {
            LootPool.Builder poolBuilder = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .when(LootItemRandomChanceCondition.randomChance(0.1f))
                    .add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).apply(new SetEnchantmentsFunction.Builder()
                            .withEnchantment(provider.getOrThrow(VWEnchantments.WOLF_EFFECT_MIGHT), UniformGenerator.between(3, 5))))
                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)).build());
            builder.pool(poolBuilder.build());
        }
        if(BuiltInLootTables.BURIED_TREASURE.equals(key)) {
            LootPool.Builder poolBuilder = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .when(LootItemRandomChanceCondition.randomChance(0.03f))
                    .add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).apply(new SetEnchantmentsFunction.Builder()
                            .withEnchantment(provider.getOrThrow(VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS), ConstantValue.exactly(1))))
                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)).build());
            builder.pool(poolBuilder.build());
        }
        if(BuiltInLootTables.BABY_VILLAGER_GIFT.equals(key)) {
            LootPool.Builder poolBuilder = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .when(LootItemRandomChanceCondition.randomChance(0.07f))
                    .add(LootItem.lootTableItem(VWItems.VERIXIUM_ARMOR_UPGRADE_TEMPLATE).apply(new SetEnchantmentsFunction.Builder()))
                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)).build());
            builder.pool(poolBuilder.build());
        }
        /*
        if (VWLootTables.VERDANT_CAMP_VALUABLES.equals(key)) {
            LootPool.Builder first = createPool(ConstantValue.exactly(1), 0.10f);
            add(provider, first, VWEnchantments.WOLF_EFFECT_WITHERING, 3);
            add(provider, first, VWEnchantments.WOLF_EFFECT_POISONING, 5);
            add(provider, first, VWEnchantments.WOLF_EFFECT_OOZING, 1);
            add(provider, first, VWEnchantments.WOLF_EFFECT_IGNITION, 3);
            builder.pool(first.build());

            LootPool.Builder second = createPool(ConstantValue.exactly(1), 0.07f);
            add(provider, second, VWEnchantments.WOLF_EFFECT_MIGHT, 5);
            add(provider, second, VWEnchantments.WOLF_EFFECT_LIFTING, 3);
            add(provider, second, VWEnchantments.WOLF_EFFECT_BLOODLUST, 3);
            add(provider, second, VWEnchantments.WOLF_EFFECT_GNAWING, 2);
            builder.pool(second.build());

            LootPool.Builder third = createPool(ConstantValue.exactly(1), 0.05f);
            add(provider, third, Enchantments.FORTUNE, 3);
            add(provider, third, Enchantments.MENDING, 1);
            add(provider, third, Enchantments.LOOTING, 3);
            add(provider, third, Enchantments.SILK_TOUCH, 1);
            builder.pool(third.build());

            LootPool.Builder fourth = createPool(ConstantValue.exactly(1), 0.15f);
            add(provider, fourth, VWEnchantments.WOLF_ARMOR_ENHANCEMENT_KIT, 1);
            add(provider, fourth, Enchantments.SHARPNESS, 5);
            add(provider, fourth, Enchantments.FIRE_ASPECT, 3);
            add(provider, fourth, Enchantments.KNOCKBACK, 2);
            add(provider, fourth, Enchantments.FLAME, 1);
            builder.pool(fourth.build());

            LootPool.Builder fifth = createPool(ConstantValue.exactly(1), 0.05f);
            add(provider, fifth, VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS, 1);
            builder.pool(fifth.build());
        }*/
    }

    private static LootPool.Builder createPool(NumberProvider number, float chance) {
        return LootPool.lootPool().setRolls(number).when(LootItemRandomChanceCondition.randomChance(chance));
    }
    private static void add(HolderLookup.Provider provider, LootPool.Builder pool, ResourceKey<Enchantment> enchantment, int level) {
        pool.add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).apply(new SetEnchantmentsFunction.Builder()
                        .withEnchantment(provider.getOrThrow(enchantment), ConstantValue.exactly(level))))
                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))).build();
    }
}
