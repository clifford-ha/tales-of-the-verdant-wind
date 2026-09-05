package cliffordha.totvw.loot;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.VWBlocks;
import cliffordha.totvw.registry.VWEnchantments;
import cliffordha.totvw.registry.VWItems;
import net.fabricmc.fabric.api.loot.v3.FabricLootTableBuilder;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class VWLootTables {
    private static final ConstantValue ONE_ROLL = ConstantValue.exactly(1);

    public static final ResourceKey<LootTable> VERDANT_CAMP_VALUABLES = createFromPath("chests/verdant_camp_valuables");
    public static final ResourceKey<LootTable> VERIXIUM_PILLAR = createFromPath("chests/verixium_pillar");
    public static final ResourceKey<LootTable> VERDANT_VILLAGE_WEAPONSMITH = createFromPath("chests/village/verdant/weaponsmith");

    private static void modifyLootTables(ResourceKey<LootTable> key, FabricLootTableBuilder builder, LootTableSource source, HolderLookup.Provider provider) {
        if (BuiltInLootTables.ANCIENT_CITY.equals(key) || BuiltInLootTables.BURIED_TREASURE.equals(key)) {
            LootPool.Builder benedictionEnchantment = addEnchantedBookChance(provider, VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS, 1, 0.07f);
            LootPool.Builder page1005 = addItemChance(VWItems.Pages.SP_ID_1005,1, 0.07f);

            builder.pool(benedictionEnchantment.build()).pool(page1005.build());
        }
        if (BuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_RARE.equals(key)) {
            LootPool.Builder witheringEnch = addEnchantedBookChance(provider, VWEnchantments.WOLF_EFFECT_WITHERING, 1, 3, 0.1f);
            LootPool.Builder poisoningEnch = addEnchantedBookChance(provider, VWEnchantments.WOLF_EFFECT_POISONING, 3, 5, 0.1f);
            LootPool.Builder mightEnch = addEnchantedBookChance(provider, VWEnchantments.WOLF_EFFECT_MIGHT, 3, 5, 0.1f);

            builder.pool(witheringEnch.build()).pool(poisoningEnch.build()).pool(mightEnch.build());
        }
        if (BuiltInLootTables.BABY_VILLAGER_GIFT.equals(key)) {
            LootPool.Builder verixiumWolfArmor = LootPool.lootPool()
                    .setRolls(ONE_ROLL)
                    .when(LootItemRandomChanceCondition.randomChance(0.12f))
                    .add(LootItem.lootTableItem(VWItems.VERIXIUM_ARMOR_UPGRADE_TEMPLATE).apply(new SetEnchantmentsFunction.Builder()))
                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)).build());
            builder.pool(verixiumWolfArmor.build());
        }
        if (BuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_UNIQUE.equals(key)) {
            LootPool.Builder lodestoneWindCore = addBlockChance(VWBlocks.LODESTONE_WIND_CORE, 1, 0.12f);
            builder.pool(lodestoneWindCore.build());
        }
        if (BuiltInLootTables.VILLAGE_ARMORER.equals(key)) {
            LootPool.Builder page1003 = addItemChance(VWItems.Pages.SP_ID_1003,1, 0.33f);
            LootPool.Builder page1004 = addItemChance(VWItems.Pages.SP_ID_1004,1, 0.33f);

            builder.pool(page1003.build()).pool(page1004.build());
        }
        if (BuiltInLootTables.PILLAGER_OUTPOST.equals(key)) {
            LootPool.Builder page1001 = addItemChance(VWItems.Pages.SP_ID_1001,1, 0.33f);
            LootPool.Builder page1002 = addItemChance(VWItems.Pages.SP_ID_1002,1, 0.33f);

            builder.pool(page1001.build()).pool(page1002.build());
        }

        if (VERIXIUM_PILLAR.equals(key)) {
            LootPool.Builder soulRunestoneFragment1 = addItemChance(VWItems.SOUL_RUNESTONE_FRAGMENT_1,1, 0.33f);
            LootPool.Builder soulRunestoneFragment3 = addItemChance(VWItems.SOUL_RUNESTONE_FRAGMENT_3,1, 0.33f);

            builder.pool(soulRunestoneFragment1.build()).pool(soulRunestoneFragment3.build());
        }
        if (VERDANT_VILLAGE_WEAPONSMITH.equals(key)) {
            LootPool.Builder soulRunestoneFragment2 = addItemChance(VWItems.SOUL_RUNESTONE_FRAGMENT_2,1, 0.33f);
            LootPool.Builder soulRunestoneFragment4 = addItemChance(VWItems.SOUL_RUNESTONE_FRAGMENT_4,1, 0.33f);

            builder.pool(soulRunestoneFragment2.build()).pool(soulRunestoneFragment4.build());
        }
        if (VERDANT_CAMP_VALUABLES.equals(key)) {
            LootPool.Builder page1006 = addItemChance(VWItems.Pages.SP_ID_1006,1, 0.07f);

            builder.pool(page1006.build());
        }
    }

    private static LootPool.Builder addItemChance(Item item, int count, float chance) {
        return LootPool.lootPool()
                .setRolls(ONE_ROLL)
                .when(LootItemRandomChanceCondition.randomChance(chance))
                .add(LootItem.lootTableItem(item).apply(SetItemCountFunction.setCount(ConstantValue.exactly(count))).build());
    }
    private static LootPool.Builder addBlockChance(Block block, int count, float chance) {
        return LootPool.lootPool()
                .setRolls(ONE_ROLL)
                .when(LootItemRandomChanceCondition.randomChance(chance))
                .add(LootItem.lootTableItem(block)
                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(count))).build());
    }
    private static LootPool.Builder addEnchantedBookChance(HolderLookup.Provider provider, ResourceKey<Enchantment> ench, int lvl, float chance) {
        return LootPool.lootPool()
                .setRolls(ONE_ROLL)
                .when(LootItemRandomChanceCondition.randomChance(chance))
                .add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).apply(new SetEnchantmentsFunction.Builder()
                        .withEnchantment(provider.getOrThrow(ench), ConstantValue.exactly(lvl)))
                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))).build());
    }
    private static LootPool.Builder addEnchantedBookChance(HolderLookup.Provider provider, ResourceKey<Enchantment> ench, int lvlA, int lvlB, float chance) {
        return LootPool.lootPool()
                .setRolls(ONE_ROLL)
                .when(LootItemRandomChanceCondition.randomChance(chance))
                .add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).apply(new SetEnchantmentsFunction.Builder()
                        .withEnchantment(provider.getOrThrow(ench), UniformGenerator.between(lvlA, lvlB)))
                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))).build());
    }

    public static void registerModifiers() {
        LootTableEvents.MODIFY.register(VWLootTables::modifyLootTables);
    }
    private static ResourceKey<LootTable> createFromPath(String path) {
        return ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, path));
    }
    private static ResourceKey<LootTable> createFromDefault(String path) {
        return ResourceKey.create(Registries.LOOT_TABLE, Identifier.withDefaultNamespace(path));
    }
}
