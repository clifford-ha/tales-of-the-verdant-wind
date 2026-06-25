package cliffordha.totvw.datagen;

import java.util.concurrent.CompletableFuture;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.ModBlocks;
import cliffordha.totvw.registry.blocks.VerdantBlocks;
import cliffordha.totvw.registry.ModEnchantments;
import cliffordha.totvw.registry.ModEntities;
import cliffordha.totvw.registry.ModItems;

import cliffordha.totvw.registry.ModSounds;
import net.minecraft.core.HolderLookup;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

public class ModEngLangProvider extends FabricLanguageProvider {
    public ModEngLangProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider holderLookup, TranslationBuilder text) {
        text.add(ModBlocks.VERIXIUM_DEEPSLATE_ORE, "Verixium Deepslate Ore");
        text.add(ModBlocks.VERIXIUM_STONE_ORE, "Verixium Stone Ore");
        text.add(ModBlocks.VERIXIUM_POWDER_BLOCK, "Verixium Powder Block");
        text.add(VerdantBlocks.VERDANT_MOSS_BLOCK, "Verdant Moss Block");
        text.add(VerdantBlocks.VERDANT_SPRUCE_LEAVES, "Verdant Spruce Leaves");
        text.add(VerdantBlocks.VERDANT_SPRUCE_SAPLING, "Verdant Spruce Sapling");
        text.add(VerdantBlocks.POTTED_VERDANT_SPRUCE_SAPLING, "Potted Verdant Spruce Sapling");

        text.add(ModBlocks.IRIDESCENT_GLASS, "Iridescent Glass");
        text.add(ModBlocks.IRIDESCENT_GLASS_PANE, "Iridescent Glass Pane");

        text.add(VerdantBlocks.VERDANT_SPRUCE_LOG, "Verdant Spruce Log");
        text.add(VerdantBlocks.VERDANT_SPRUCE_WOOD, "Verdant Spruce Wood");
        text.add(VerdantBlocks.STRIPPED_VERDANT_SPRUCE_LOG, "Stripped Verdant Spruce Log");
        text.add(VerdantBlocks.STRIPPED_VERDANT_SPRUCE_WOOD, "Stripped Verdant Spruce Wood");
        text.add(VerdantBlocks.VERDANT_SPRUCE_PLANKS, "Verdant Spruce Planks");
        text.add(VerdantBlocks.VERDANT_SPRUCE_SLAB, "Verdant Spruce Slab");
        text.add(VerdantBlocks.VERDANT_SPRUCE_STAIRS, "Verdant Spruce Stairs");
        text.add(VerdantBlocks.VERDANT_SPRUCE_FENCE, "Verdant Spruce Fence");
        text.add(VerdantBlocks.VERDANT_SPRUCE_FENCE_GATE, "Verdant Spruce Fence Gate");
        text.add(VerdantBlocks.VERDANT_SPRUCE_BUTTON, "Verdant Spruce Button");
        text.add(VerdantBlocks.VERDANT_SPRUCE_PRESSURE_PLATE, "Verdant Spruce Pressure Plate");
        text.add(VerdantBlocks.VERDANT_SPRUCE_DOOR, "Verdant Spruce Door");
        text.add(VerdantBlocks.VERDANT_SPRUCE_TRAPDOOR, "Verdant Spruce Trapdoor");
        text.add(ModItems.VERDANT_SPRUCE_SIGN, "Verdant Spruce Sign");
        text.add(ModItems.VERDANT_SPRUCE_HANGING_SIGN, "Verdant Spruce Hanging Sign");
        text.add(VerdantBlocks.VERDANT_SPRUCE_SHELF, "Verdant Spruce Shelf");

        text.add("container.blast_synthesizing_furnace", "Blast Synthesizing Furnace");

        text.add(ModItems.VERDANT_SPRUCE_BOAT, "Verdant Spruce Boat");
        text.add(ModItems.VERDANT_SPRUCE_CHEST_BOAT, "Verdant Spruce Chest Boat");

        text.add(ModEntities.VERDANT_SPRUCE_BOAT, "Verdant Spruce Boat");
        text.add(ModEntities.VERDANT_SPRUCE_CHEST_BOAT, "Verdant Spruce Chest Boat");


        text.add(ModItems.VERIXIUM_FLUID_BUCKET, "Verixium Fluid Bucket");
        text.add("verixium_fluid", "Verixium Fluid");
        text.add("flowing_verixium_fluid", "Flowing Verixium Fluid");


        text.add(ModItems.VERIXIUM_CHUNK, "Verixium Chunk");
        text.add(ModItems.CONDENSED_VERIXIUM, "Condensed Verixium");
        text.add(ModItems.VERIXIUM_SHARD, "Verixium Shard");
        text.add(ModItems.VERIXIUM_POWDER, "Verixium Powder");
        text.add(ModItems.VERIXIUM_INGOT, "Verixium Ingot");

        text.add(ModItems.VERIXIUM_PAPER, "Verixium Paper");


        text.add(ModItems.VERIXIUM_HELMET, "Verixium Helmet");
        text.add(ModItems.VERIXIUM_CHESTPLATE, "Verixium Chestplate");
        text.add(ModItems.VERIXIUM_LEGGINGS, "Verixium Leggings");
        text.add(ModItems.VERIXIUM_BOOTS, "Verixium Boots");

        text.add(ModItems.VERIXIUM_WOLF_ARMOR, "Verixium Wolf Armor");
        text.add(ModItems.VERIXIUM_ARMOR_UPGRADE_TEMPLATE, "Verixium Armor Upgrade Template");

        text.add(ModItems.VERIXIUM_HORSE_ARMOR, "Verixium Horse Armor");


        text.addEnchantment(ModEnchantments.WOLF_EFFECT_IGNITION, "Wolf ATK Effect: §vIgnition");
        text.addEnchantment(ModEnchantments.WOLF_EFFECT_POISONING, "Wolf ATK Effect: §cPoison");
        text.addEnchantment(ModEnchantments.WOLF_EFFECT_WITHERING, "Wolf ATK Effect: §cWithering");
        text.addEnchantment(ModEnchantments.WOLF_EFFECT_LIFTING, "Wolf ATK Effect: Lifting");
        text.addEnchantment(ModEnchantments.WOLF_EFFECT_BLOODLUST, "Wolf ATK Effect: §cBloodlust");
        text.addEnchantment(ModEnchantments.WOLF_EFFECT_MIGHT, "Wolf ATK Effect: §dMight");
        text.addEnchantment(ModEnchantments.WOLF_EFFECT_OOZING, "Wolf ATK Effect: §aOozing");
        text.addEnchantment(ModEnchantments.WOLF_EFFECT_GNAWING, "Wolf ATK Effect: §dGnawing");
        text.addEnchantment(ModEnchantments.WOLF_ARMOR_ENHANCEMENT_KIT, "Wolf Armor Enhancement Kit");
        text.addEnchantment(ModEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS,"Benediction of the Verdant Mountains");


        text.add("effect.tales-of-the-verdant-wind.blessing_of_the_verdant_wind", "Blessing of the Verdant Wind");
        text.add("effect.tales-of-the-verdant-wind.amplified_might", "Amplified Might");
        text.add("effect.tales-of-the-verdant-wind.bloodlust", "Bloodlust");
        text.add("effect.tales-of-the-verdant-wind.paralyze", "Paralyzed");

        text.add("effect.tales-of-the-verdant-wind.bloodlust.description", "Gives massive attack buff in exchange for constant damage while the effect is active");
        
        text.add("item.minecraft.potion.effect.sacred_verdant_potion", "Sacred Verdant Potion");
        text.add("item.minecraft.splash_potion.effect.sacred_verdant_potion", "Sacred Verdant Splash Potion");
        text.add("item.minecraft.lingering_potion.effect.sacred_verdant_potion", "Sacred Verdant Lingering Potion");

        text.add("item.minecraft.potion.effect.might_amplifier_potion", "Amplified Might Potion");
        text.add("item.minecraft.splash_potion.effect.might_amplifier_potion", "Amplified Might Splash Potion");
        text.add("item.minecraft.lingering_potion.effect.might_amplifier_potion", "Amplified Might Lingering Potion");

        text.add("item.minecraft.potion.effect.baleful_strength_potion", "Baleful Strength Potion");
        text.add("item.minecraft.splash_potion.effect.baleful_strength_potion", "Baleful Strength Splash Potion");
        text.add("item.minecraft.lingering_potion.effect.baleful_strength_potion", "Baleful Strength Lingering Potion");

        text.add("item.minecraft.tipped_arrow.effect.sacred_verdant_potion", "Arrow infused with Verdant Wind");
        text.add("item.minecraft.tipped_arrow.effect.might_amplifier_potion", "Arrow of Amplified Might");
        text.add("item.minecraft.tipped_arrow.effect.baleful_strength_potion", "Arrow of Baleful Strength");


        text.add("death.attack.bloodlust", "%1$s died from the agonizing effects of §cBloodlust§r");
        text.add("death.attack.bloodlust.player", "%1$s died from the agonizing effects of §cBloodlust§r while fighting %1$s");


        text.add(ModItems.VERIXIUM_SPEAR, "Verixium Spear");
        text.add(ModItems.VERIXIUM_SWORD, "Verixium Sword");
        text.add(ModItems.VERIXIUM_AXE, "Verixium Axe");
        text.add(ModItems.VERIXIUM_PICKAXE, "Verixium Pickaxe");
        text.add(ModItems.VERIXIUM_SHOVEL, "Verixium Shovel");
        text.add(ModItems.VERIXIUM_HOE, "Verixium Hoe");


        text.add("biome.tales-of-the-verdant-wind.verdant_mountains", "Verdant Mountains");
        text.add("biome.tales-of-the-verdant-wind.verdant_forest", "Verdant Forest");


        text.add(ModSounds.WOLF_HOWL_A, "Distant wolf howls");
        text.add(ModSounds.WOLF_HOWL_B1, "Distant wolf howls");
        text.add(ModSounds.WOLF_HOWL_B2, "Distant wolf howls");
        text.add(ModSounds.WOLF_HOWL_B3, "Distant wolf howls");

        text.add(ModSounds.NOTIFY, "Notification popped");
        text.add(ModSounds.WOLF_SKILL_PARALYZE, "%1$s got paralyzed");



        text.add(TOTVW.MOD_ID, "Tales of the Verdant Wind");
        text.add("creativeTab.verdantItems", "Verdant Items");
    }
}