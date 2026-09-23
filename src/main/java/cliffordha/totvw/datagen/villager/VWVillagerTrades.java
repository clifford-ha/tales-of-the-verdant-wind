package cliffordha.totvw.datagen.villager;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.VWBlocks;
import cliffordha.totvw.registry.VWEnchantments;
import cliffordha.totvw.registry.VWItems;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.TradeCost;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.item.trading.VillagerTrades;

import java.util.List;
import java.util.Optional;

public class VWVillagerTrades {
    public static final ResourceKey<VillagerTrade> WEAPONSMITH_2_WOLF_ATK_ENCHANTMENTS = createKey("weaponsmith/2/wolf_atk_enchantments");
    public static final ResourceKey<VillagerTrade> WEAPONSMITH_2_RUNESTONE_FRAGMENT_3 = createKey("weaponsmith/2/runestone_fragment_3");
    public static final ResourceKey<VillagerTrade> WEAPONSMITH_3_LODESTONE_WIND_CORE = createKey("weaponsmith/3/lodestone_wind_core");

    public static final ResourceKey<VillagerTrade> LIBRARIAN_2_VERIXIUM_PAPER = createKey("librarian/2/verixium_paper");
    public static final ResourceKey<VillagerTrade> LIBRARIAN_2_WOLF_ATK_ENCHANTMENTS = createKey("librarian/2/wolf_atk_enchantments");

    public static final ResourceKey<VillagerTrade> CLERIC_2_VERIXIUM_POWDER = createKey("cleric/2/verixium_powder");
    public static final ResourceKey<VillagerTrade> CLERIC_2_EMERALD = createKey("cleric/2/emerald");

    public static final ResourceKey<VillagerTrade> ARMORER_1_WOLF_ENHANCEMENT_KIT = createKey("armorer/1/wolf_enhancement_kit");
    public static final ResourceKey<VillagerTrade> ARMORER_4_VERIXIUM_WOLF_ARMOR = createKey("armorer/5/verixium_wolf_armor");
    public static final ResourceKey<VillagerTrade> ARMORER_5_VERIXIUM_ARMOR_UPGRADE_TEMPLATE = createKey("armorer/5/verixium_armor_upgrade_template");

    public static final ResourceKey<VillagerTrade> WANDERING_VERDANT_SPRUCE_TREE_SAPLING = createKey("wandering_trader/verdant_spruce_tree_sapling");
    public static final ResourceKey<VillagerTrade> WANDERING_FAR_AWAY_ENCHANTMENTS = createKey("wandering_trader/far_away_enchantments");


    public static void bootstrap(BootstrapContext<VillagerTrade> context) {
        var items = context.lookup(Registries.ITEM);
        var enchantments = context.lookup(Registries.ENCHANTMENT);

        context.register(WEAPONSMITH_2_RUNESTONE_FRAGMENT_3, new VillagerTrade(
                new TradeCost(Items.EMERALD, 48),
                Optional.of(new TradeCost(VWItems.VERIXIUM_SHARD, 7)),
                new ItemStackTemplate(VWItems.SOUL_RUNESTONE_FRAGMENT_3),
                1, 30, 0.00f,
                Optional.empty(), List.of()));
        context.register(WEAPONSMITH_2_WOLF_ATK_ENCHANTMENTS, new VillagerTrade(
                new TradeCost(Items.EMERALD, 24),
                Optional.of(new TradeCost(Items.FIRE_CHARGE, 16)),
                new ItemStackTemplate(Items.ENCHANTED_BOOK),
                3, 50, 0.05f,
                Optional.empty(),
                VillagerTrades.enchantedBook(items,
                        HolderSet.direct(
                                enchantments.getOrThrow(VWEnchantments.WOLF_EFFECT_IGNITION)
                        ))
        ));
        context.register(WEAPONSMITH_3_LODESTONE_WIND_CORE, new VillagerTrade(
                new TradeCost(Items.EMERALD, 50),
                Optional.of(new TradeCost(VWBlocks.VERIXIUM_POWDER_BLOCK, 3)),
                new ItemStackTemplate(VWBlocks.LODESTONE_WIND_CORE.asItem()),
                1, 200, 0.00f,
                Optional.empty(), List.of()));


        context.register(LIBRARIAN_2_WOLF_ATK_ENCHANTMENTS, new VillagerTrade(
                new TradeCost(Items.EMERALD, 24),
                Optional.of(new TradeCost(VWItems.VERIXIUM_PAPER, 16)),
                new ItemStackTemplate(Items.ENCHANTED_BOOK),
                6, 20, 0.05f,
                Optional.empty(),
                VillagerTrades.enchantedBook(items,
                        HolderSet.direct(
                                enchantments.getOrThrow(VWEnchantments.WOLF_EFFECT_LIFTING),
                                enchantments.getOrThrow(VWEnchantments.WOLF_EFFECT_MIGHT),
                                enchantments.getOrThrow(VWEnchantments.WOLF_EFFECT_OOZING)
                        ))
        ));
        context.register(LIBRARIAN_2_VERIXIUM_PAPER, new VillagerTrade(
                new TradeCost(Items.EMERALD, 1),
                new ItemStackTemplate(VWItems.VERIXIUM_PAPER, 2),
                24, 5, 0.05f,
                Optional.empty(), List.of()));


        context.register(CLERIC_2_VERIXIUM_POWDER, new VillagerTrade(
                new TradeCost(VWItems.VERIXIUM_FLUID_BUCKET, 1),
                new ItemStackTemplate(VWItems.VERIXIUM_POWDER, 3),
                256, 20, 0f,
                Optional.empty(), List.of()));
        context.register(CLERIC_2_EMERALD, new VillagerTrade(
                new TradeCost(VWItems.VERIXIUM_POWDER, 4),
                new ItemStackTemplate(Items.EMERALD),
                256, 20, 0.05f,
                Optional.empty(), List.of()));


        context.register(ARMORER_1_WOLF_ENHANCEMENT_KIT, new VillagerTrade(
                new TradeCost(Items.EMERALD, 16),
                Optional.of(new TradeCost(Items.IRON_INGOT, 10)),
                new ItemStackTemplate(Items.ENCHANTED_BOOK),
                2, 15, 0.05f,
                Optional.empty(),
                VillagerTrades.enchantedBook(items,
                        HolderSet.direct(
                                enchantments.getOrThrow(VWEnchantments.WOLF_ARMOR_ENHANCEMENT_KIT)
                        ))
        ));
        context.register(ARMORER_4_VERIXIUM_WOLF_ARMOR, new VillagerTrade(
                new TradeCost(Items.EMERALD, 30),
                new ItemStackTemplate(VWItems.VERIXIUM_WOLF_ARMOR),
                12, 20, 0.05f,
                Optional.empty(), List.of()));
        context.register(ARMORER_5_VERIXIUM_ARMOR_UPGRADE_TEMPLATE, new VillagerTrade(
                new TradeCost(Items.EMERALD, 48),
                Optional.of(new TradeCost(Items.WIND_CHARGE, 12)),
                new ItemStackTemplate(VWItems.VERIXIUM_ARMOR_UPGRADE_TEMPLATE),
                12, 30, 0.05f,
                Optional.empty(), List.of()));


        context.register(WANDERING_VERDANT_SPRUCE_TREE_SAPLING, new VillagerTrade(
                new TradeCost(Items.DIRT, 4),
                new ItemStackTemplate(VWBlocks.VERDANT_SPRUCE_SAPLING.asItem()),
                12, 20, 0.05f,
                Optional.empty(), List.of()));
        context.register(WANDERING_FAR_AWAY_ENCHANTMENTS, new VillagerTrade(
                new TradeCost(Items.EMERALD, 10),
                Optional.of(new TradeCost(VWItems.VERIXIUM_CHUNK, 2)),
                new ItemStackTemplate(Items.ENCHANTED_BOOK),
                2, 50, 0.05f,
                Optional.empty(),
                VillagerTrades.enchantedBook(items,
                        HolderSet.direct(
                                enchantments.getOrThrow(VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS),
                                enchantments.getOrThrow(VWEnchantments.WOLF_EFFECT_OOZING)
                        ))
        ));
    }


    private static ResourceKey<VillagerTrade> createKey(String name) {
        return ResourceKey.create(Registries.VILLAGER_TRADE, TOTVW.registerID(name));
    }
}
