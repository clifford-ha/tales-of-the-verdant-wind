package cliffordha.totvw.datagen;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.client.VWKeymap;
import cliffordha.totvw.registry.*;
import cliffordha.totvw.registry.VWBlocks;
import cliffordha.totvw.registry.VWItems.Pages;

import net.minecraft.core.HolderLookup;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static cliffordha.totvw.item.pages.ScatteredPageTitle.*;

public class VWEngLangProvider extends FabricLanguageProvider {
    public VWEngLangProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider holderLookup, TranslationBuilder text) {
        text.add(VWBlocks.VERIXIUM_DEEPSLATE_ORE, "Verixium Deepslate Ore");
        text.add(VWBlocks.VERIXIUM_STONE_ORE, "Verixium Stone Ore");
        text.add(VWBlocks.VERIXIUM_POWDER_BLOCK, "Verixium Powder Block");
        text.add(VWBlocks.VERDANT_MOSS_BLOCK, "Verdant Moss Block");
        text.add(VWBlocks.VERDANT_SPRUCE_LEAVES, "Verdant Spruce Leaves");
        text.add(VWBlocks.VERDANT_SPRUCE_SAPLING, "Verdant Spruce Sapling");
        text.add(VWBlocks.POTTED_VERDANT_SPRUCE_SAPLING, "Potted Verdant Spruce Sapling");

        text.add(VWBlocks.IRIDESCENT_GLASS, "Iridescent Glass");
        text.add(VWBlocks.IRIDESCENT_GLASS_PANE, "Iridescent Glass Pane");

        text.add(VWBlocks.VERDANT_SPRUCE_LOG, "Verdant Spruce Log");
        text.add(VWBlocks.VERDANT_SPRUCE_WOOD, "Verdant Spruce Wood");
        text.add(VWBlocks.STRIPPED_VERDANT_SPRUCE_LOG, "Stripped Verdant Spruce Log");
        text.add(VWBlocks.STRIPPED_VERDANT_SPRUCE_WOOD, "Stripped Verdant Spruce Wood");
        text.add(VWBlocks.VERDANT_SPRUCE_PLANKS, "Verdant Spruce Planks");
        text.add(VWBlocks.VERDANT_SPRUCE_SLAB, "Verdant Spruce Slab");
        text.add(VWBlocks.VERDANT_SPRUCE_STAIRS, "Verdant Spruce Stairs");
        text.add(VWBlocks.VERDANT_SPRUCE_FENCE, "Verdant Spruce Fence");
        text.add(VWBlocks.VERDANT_SPRUCE_FENCE_GATE, "Verdant Spruce Fence Gate");
        text.add(VWBlocks.VERDANT_SPRUCE_BUTTON, "Verdant Spruce Button");
        text.add(VWBlocks.VERDANT_SPRUCE_PRESSURE_PLATE, "Verdant Spruce Pressure Plate");
        text.add(VWBlocks.VERDANT_SPRUCE_DOOR, "Verdant Spruce Door");
        text.add(VWBlocks.VERDANT_SPRUCE_TRAPDOOR, "Verdant Spruce Trapdoor");
        text.add(VWItems.VERDANT_SPRUCE_SIGN, "Verdant Spruce Sign");
        text.add(VWItems.VERDANT_SPRUCE_HANGING_SIGN, "Verdant Spruce Hanging Sign");
        text.add(VWBlocks.VERDANT_SPRUCE_SHELF, "Verdant Spruce Shelf");
        text.add(VWBlocks.VERDANT_SPRUCE_STORAGE_BOX, "Verdant Spruce Storage Box");

        text.add(VWBlocks.VERIXIUM_FLUID, "Verixium Fluid");

        text.add("container.tales-of-the-verdant-wind.storage_box", "Storage Box");
        text.add(VWBlocks.LODESTONE_WIND_CORE, "Lodestone Wind Core");

        text.add(VWItems.VERDANT_SPRUCE_BOAT, "Verdant Spruce Boat");
        text.add(VWItems.VERDANT_SPRUCE_CHEST_BOAT, "Verdant Spruce Chest Boat");

        text.add(VWEntities.VERDANT_SPRUCE_BOAT, "Verdant Spruce Boat");
        text.add(VWEntities.VERDANT_SPRUCE_CHEST_BOAT, "Verdant Spruce Chest Boat");


        text.add(VWItems.VERIXIUM_FLUID_BUCKET, "Verixium Fluid Bucket");
        text.add("verixium_fluid", "Verixium Fluid");
        text.add("flowing_verixium_fluid", "Flowing Verixium Fluid");


        text.add(VWItems.VERIXIUM_CHUNK, "Verixium Chunk");
        text.add(VWItems.CONDENSED_VERIXIUM, "Condensed Verixium");
        text.add(VWItems.VERIXIUM_SHARD, "Verixium Shard");
        text.add(VWItems.VERIXIUM_POWDER, "Verixium Powder");
        text.add(VWItems.VERIXIUM_INGOT, "Verixium Ingot");

        text.add(VWItems.VERIXIUM_PAPER, "Verixium Paper");


        text.add(VWItems.VERIXIUM_HELMET, "Verixium Helmet");
        text.add(VWItems.VERIXIUM_CHESTPLATE, "Verixium Chestplate");
        text.add(VWItems.VERIXIUM_LEGGINGS, "Verixium Leggings");
        text.add(VWItems.VERIXIUM_BOOTS, "Verixium Boots");

        text.add(VWItems.VERIXIUM_WOLF_ARMOR, "Verixium Wolf Armor");
        text.add(VWItems.VERIXIUM_ARMOR_UPGRADE_TEMPLATE, "Verixium Armor Upgrade Template");

        text.add(VWItems.VERIXIUM_HORSE_ARMOR, "Verixium Horse Armor");

        text.add(VWItems.SOUL_RUNESTONE_PLATE, "Soul Runestone Plate");
        text.add(VWItems.SOUL_RUNESTONE_FRAGMENT_1, "Soul Runestone Fragment (TL)");
        text.add(VWItems.SOUL_RUNESTONE_FRAGMENT_2, "Soul Runestone Fragment (TR)");
        text.add(VWItems.SOUL_RUNESTONE_FRAGMENT_3, "Soul Runestone Fragment (BL)");
        text.add(VWItems.SOUL_RUNESTONE_FRAGMENT_4, "Soul Runestone Fragment (BR)");
        text.add(VWItems.TETHER_RUNESTONE_PLATE, "Tether Runestone Plate");
        text.add(VWItems.GENESIS_RUNESTONE_PLATE, "Genesis Runestone Plate");
        text.add(VWItems.HAVOC_RUNESTONE_PLATE, "Havoc Runestone Plate");
        text.add(VWItems.EFFLORESCENCE_RUNESTONE_PLATE, "Efflorescence Runestone Plate");

        // MISC BLOCKS
        text.add(VWBlocks.FARMLAND_PLACER, "Farmland Placer");


        String SCATTERED_PAGE = "Scattered Page";
        text.add(Pages.SCATTERED_PAGE, SCATTERED_PAGE);

        String OLD_SCATTERED_PAGE = "Old Scattered Page";
        text.add(Pages.OLD_SCATTERED_PAGE, OLD_SCATTERED_PAGE);

        text.add(Pages.ENCHANTMENTS_HANDBOOK, "Enchantments Handbook");
        text.add(Pages.EFFECTS_HANDBOOK, "Effects Handbook");
        text.add(Pages.ITEMS_HANDBOOK, "Items Handbook");
        text.add(Pages.FEATURES_HANDBOOK, "Features Handbook");

        text.add(Pages.PLAYER_STATS, "Player Stats");
        text.add(Pages.SP_ID_TEST, "Test Page");
        text.add(Pages.SP_ID_1000, SP_1000.getTitle());

        text.add(Pages.SP_ID_1001, SP_1001.getTitle());
        text.add(Pages.SP_ID_1002, SP_1002.getTitle());
        text.add(Pages.SP_ID_1003, SP_1003.getTitle());
        text.add(Pages.SP_ID_1004, SP_1004.getTitle());

        text.add(Pages.SP_ID_1005, SP_1005.getTitle());

        text.add(Pages.SP_ID_1006, SP_1006.getTitle());

        text.add(Pages.SP_ID_3000, SP_3000.getTitle());
        text.add(Pages.SP_ID_3001, SP_3001.getTitle());
        text.add(Pages.SP_ID_3002, SP_3002.getTitle());

        text.add(Pages.LODESTONE_WIND_CORE_MANUAL, LODESTONE_WIND_CORE_MANUAL.getTitle());



        text.addEnchantment(VWEnchantments.WOLF_EFFECT_IGNITION, "Wolf Effect: Ignition");
        text.addEnchantment(VWEnchantments.WOLF_EFFECT_POISONING, "Wolf Effect: Poison");
        text.addEnchantment(VWEnchantments.WOLF_EFFECT_WITHERING, "Wolf Effect: Withering");
        text.addEnchantment(VWEnchantments.WOLF_EFFECT_LIFTING, "Wolf Effect: Lifting");
        text.addEnchantment(VWEnchantments.WOLF_EFFECT_BLOODLUST, "Wolf Effect: Bloodlust");
        text.addEnchantment(VWEnchantments.WOLF_EFFECT_MIGHT, "Wolf Effect: Might");
        text.addEnchantment(VWEnchantments.WOLF_EFFECT_OOZING, "Wolf Effect: Oozing");
        text.addEnchantment(VWEnchantments.WOLF_EFFECT_GNAWING, "Wolf Effect: Gnawing");
        text.addEnchantment(VWEnchantments.WOLF_EFFECT_ARCTIC_WINDS, "Wolf Effect: Arctic Winds");
        text.addEnchantment(VWEnchantments.WOLF_ARMOR_ENHANCEMENT_KIT, "Wolf Armor Enhancement Kit");
        text.addEnchantment(VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS,"Benediction of the Verdant Mountains");


        text.add(effectKey("blessing_of_the_verdant_wind"), "Blessing of the Verdant Wind");
        text.add(effectKey("amplified_might"), "Amplified Might");
        text.add(effectKey("bloodlust"), "Bloodlust");
        text.add(effectKey("paralyze"), "Paralyzed");
        text.add(effectKey("wind_veil"), "Wind Veil");
        text.add(effectKey("havoc"), "Havoc");

        text.add("effect.tales-of-the-verdant-wind.bloodlust.description", "Gives massive attack buff in exchange for constant damage while the effect is active");

        text.add(potionKey("sacred_verdant_potion"), "Sacred Verdant Potion");
        text.add(potionSplashKey("sacred_verdant_potion"), "Sacred Verdant Splash Potion");
        text.add(potionLingeringKey("sacred_verdant_potion"), "Sacred Verdant Lingering Potion");
        text.add(arrowKey("sacred_verdant_potion"), "Arrow infused with Verdant Wind");

        HashMap<String, String> potionTranslations = new HashMap<>();
        potionTranslations.put("might_amplifier_potion", "Amplified Might");
        potionTranslations.put("baleful_strength_potion", "Baleful Strength");

        potionTranslations(text, potionTranslations);

        HashMap<String, List<String>> deathTranslations = new HashMap<>();
        deathTranslations.put("bloodlust", List.of(
                "%1$s died from the agonizing effects of §cBloodlust§r",
                "%1$s died from the agonizing effects of §cBloodlust§r while fighting %2$s"
        ));
        deathTranslations.put("scorching_heat", List.of(
                "%1$s died from scorching heat",
                "%1$s died from scorching heat while fighting %2$s"
        ));
        deathTranslations.put("bleeding", List.of(
                "%1$s bled to death",
                "%1$s bled to death while fighting %2$s"
        ));
        deathTranslations.put("wind_core_pulse", List.of(
                "%1$s got incapacitated by the Wind Core's pulse",
                "%1$s got incapacitated by the Wind Core's pulse while fighting %2$s"
        ));
        deathTranslations.put("havoc", List.of(
                "%1$s got their life drained by the effects of Havoc",
                "%1$s got their life drained by the effects of Havoc while fighting %2$s"
        ));
        deathTranslations.put("tether_proxy", List.of(
                "%1$s got killed via proxy",
                "%1$s got killed via proxy while fighting %2$s"
        ));
        deathTranslations.put("thermal_burn", List.of(
                "%1$s got thermally burnt",
                "%1$s got thermally burnt while fighting %2$s"
        ));
        deathTranslations.put("winterbreeze_shock", List.of(
                "%1$s chilled to death from the effects of Winterbreeze Shock",
                "%1$s chilled to death from the effects of Winterbreeze Shock while fighting %2$s"
        ));

        deathTranslationsBuilder(text, deathTranslations);


        text.add(VWItems.VERIXIUM_SPEAR, "Verixium Spear");
        text.add(VWItems.VERIXIUM_SWORD, "Verixium Sword");
        text.add(VWItems.VERIXIUM_AXE, "Verixium Axe");
        text.add(VWItems.VERIXIUM_PICKAXE, "Verixium Pickaxe");
        text.add(VWItems.VERIXIUM_SHOVEL, "Verixium Shovel");
        text.add(VWItems.VERIXIUM_HOE, "Verixium Hoe");


        text.add(biomeKey("verdant_mountains"), "Verdant Mountains");
        text.add(biomeKey("verdant_forest"), "Verdant Forest");


        // SOUNDS
        text.add(VWSounds.WOLF_HOWL_A, "Distant wolf howls");
        text.add(VWSounds.WOLF_HOWL_B1, "Distant wolf howls");
        text.add(VWSounds.WOLF_HOWL_B2, "Distant wolf howls");
        text.add(VWSounds.WOLF_HOWL_B3, "Distant wolf howls");

        text.add(VWSounds.NOTIFY, "Notification popped");
        text.add(VWSounds.WOLF_SKILL_PARALYZE, "%1$s got paralyzed");

        text.add(VWSounds.LODESTONE_WIND_CORE_AMBIENT, "Wind Core whooshes");



        // KEYMAPPINGS
        text.add(VWKeymap.TOTVW_CONFIG_SCREEN, "TOTVW Config Screen");
        text.add(VWKeymap.TOTVW_STAT_SCREEN, "TOTVW Stats Screen");

        text.add(TOTVW.MOD_ID, "Tales of the Verdant Wind");
        text.add(VWItems.EMPTY, "");

        //DEV
        if (TOTVW.IN_DEVELOPMENT) {
            text.add(VWItems.DevItems.ATTACHMENTS_REMOVER, "Attachments Remover");
        }
    }

    private static String biomeKey(String name) {
        return "biome." + TOTVW.MOD_ID + "." + name;
    }
    private static String effectKey(String name) {
        return "effect." + TOTVW.MOD_ID + "." + name;
    }
    private static String deathKey(String name) {
        return "death.attack." + name;
    }
    private static String deathPlayerKey(String name) {
        return "death.attack." + name + ".player";
    }
    private static void deathTranslationsBuilder(TranslationBuilder builder, HashMap<String, List<String>> deaths) {
        for (String death : deaths.keySet()) {
            builder.add(deathKey(death), deaths.get(death).getFirst());
            builder.add(deathPlayerKey(death), deaths.get(death).get(1));
        }
    }

    private static String potionKey(String name) {
        return "item.minecraft.potion.effect." + name;
    }
    private static String potionSplashKey(String name) {
        return "item.minecraft.splash_potion.effect." + name;
    }
    private static String potionLingeringKey(String name) {
        return "item.minecraft.lingering_potion.effect." + name;
    }
    private static String arrowKey(String name) {
        return "item.minecraft.tipped_arrow.effect." + name;
    }
    private static void potionTranslations(TranslationBuilder builder, HashMap<String, String> potions) {
        for (String potion : potions.keySet()) {
            builder.add(potionKey(potion), potions.get(potion) + " Potion");
            builder.add(potionSplashKey(potion), potions.get(potion) + " Splash Potion");
            builder.add(potionLingeringKey(potion), potions.get(potion) + " Lingering Potion");
            builder.add(arrowKey(potion), "Arrow of " + potions.get(potion));
        }
    }
}