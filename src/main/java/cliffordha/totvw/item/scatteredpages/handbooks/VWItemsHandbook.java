package cliffordha.totvw.item.scatteredpages.handbooks;

import static cliffordha.totvw.item.scatteredpages.ScatteredPageTextColor.*;
import static cliffordha.totvw.item.scatteredpages.ScatteredPageTextStyle.*;
import static cliffordha.totvw.util.VWUtil.TextUtil.*;

public class VWItemsHandbook {
    private static final String descBenediction = "Benediction of the Verdant Mountains";
    private static final String descWindCoreEnergySources = "Verixium Powder, Verixium Powder Block, and Wind Charge.";

    private static final String titleVerixiumResources = cText(AQUA, bold("Verixium Resources"));
    private static final String titleLodestoneWindCore = cText(YELLOW, bold("Lodestone Wind Core"));
    private static final String titleScatteredPages = cText(GRAY, bold("Scattered Pages"));
    private static final String titleIridescentGlass = cText(LIGHT_PURPLE, bold("Iridescent Glass"));
    private static final String titleVerdantSpruceStorageBox = cText(GREEN, bold("Verdant Spruce Storage Box"));
    private static final String titleVerixiumArmorUpgradeTemplate = cText(YELLOW, bold("Verixium Armor Upgrade Template"));
    private static final String titleSoulRunestonePlate = cText(DARK_AQUA, bold("Soul Runestone Plate"));
    private static final String titleTetherRunestonePlate = cText(BLUE, bold("Tether Runestone Plate"));
    private static final String titleHavocRunestonePlate = cText(RED, bold("Havoc Runestone Plate"));

    private static String italic(String t) {
        return fText(ITALIC, t);
    }
    private static String bold(String t) {
        return fText(BOLD, t);
    }

    private static String verixiumResourcesInfo() {
        return titleVerixiumResources.toUpperCase() + nextLine
                + "A collection of items that are used to craft Verixium-based items. Starting with the Verixium Chunks that are found exclusively in the Verdant Biomes, you can use it to make a Condensed Verixium which can be cut into shards by using a stonecutter. Upon obtaining a Verixium Shard, you can then smelt it and turn it into Verixium Powder. The last step needs a diamond and by covering it up with the powder, the product, Verixium Ingot, can be obtained."
                + nextParagraph

                + "Additional Info:" + nextLine
                + "Smelting the shards into its powder form gives you a substantial amount of experience points. The Verixium Powder can be used to obtain the Verixium Fluid Bucket, and when you have the " + descBenediction + " enchantment, you may be able to use it and obtain a certain effect.";
    }
    private static String lodestoneWindCoreInfo() {
        return titleLodestoneWindCore.toUpperCase() + nextLine
                + "A block that can be obtained inside the Ancient Pillars that are found in Verdant Villages. Its core function is to buff nearby players and wolves while debuffing enemies and damaging them at intervals. The core can be activated with a Verixium Paper and while active, use these to add Wind Energy: " + descWindCoreEnergySources
                + nextParagraph

                + fText(BOLD, "Functions:") + nextLine
                + "• While the core has remaining energy, it will continuously consume it to run and the amount of energy consumed depends on what biome the block is in." + nextLine
                + "• At random, the core will \"pulse\" and give random positive and negative effects to nearby mobs." + nextLine
                + "• When the core has more than 2000 energy, it will convert nearby wolves and villagers (random single target selection) with a 3% chance." + nextLine
                + "• Pressure Status: If a monster is nearby, the core will continuously remove their internal pressure, giving them a Pressure Difference Point. The monster's chance of imploding will start to rise after their Pressure Difference Point reaches more than 100. When the monster implodes, the monster suffers a single instance of DMG equal to 30% of its total max health (current health if in Hard game difficulty).";
    }
    private static String iridescentGlassInfo() {
        return titleIridescentGlass.toUpperCase() + nextLine
                + "A variant of glass that can be obtained by placing a Glass Block/Pane next to a Verixium Fluid. This glass can be used to craft any dye colors when placed inside a stonecutter, shredding it to the desired color.";
    }
    private static String verdantSpruceStorageBoxInfo() {
        return titleVerdantSpruceStorageBox.toUpperCase() + nextLine
                + "A variant of the vanilla Barrel that can store up to 54 items. It can be crafted using a chest, verdant spruce log, and verdant spruce slab.";
    }
    private static String verixiumArmorUpgradeTemplateInfo() {
        return titleVerixiumArmorUpgradeTemplate.toUpperCase() + nextLine
                + "A template that can be used to transform Diamond Armors into Verixium-type. It can be obtained through the following: as reward in the Trial Chambers, in Ancient City chests, or when a villager with armorer profession gives it to you as gift.";
    }
    private static String soulRunestonePlateInfo() {
        return titleSoulRunestonePlate.toUpperCase() + nextLine
                + "An item that allows you to store your wolf companions's soul within your own, allowing you to travel with ease knowing that you can summon them anytime later. You can store up to 5 wolf souls at most. However, if you have the " + descBenediction + " enchantment, the limit will be capped to 12 instead."
                + nextParagraph

                + "Moreover, when you reach the 3 souls threshold, adding more will result in a penalty that will inflict damage upon you. This has a 60% chance of getting triggered and each soul past the threshold will multiply the damage."
                + nextParagraph

                + "This item also allows you to trigger Revival by Proxy provided that you meet the prerequisites. If the threshold is met, each additional soul contained will increase the chances of this item to break and fragment itself.";
    }
    private static String tetherRunestonePlateInfo() {
        return titleTetherRunestonePlate.toUpperCase() + nextLine
                + "An item that can be used to extend your wolf companion's attack capabilities. To use, simply hold this item and right click your companion. After that, wolf will unlock LINK status and will be able to record entity types."
                + nextParagraph

                + "LINK STATUS:" + nextLine
                + "While active, when wolf successfully deals damage, the runestone will record that victim's entity type (Link Record). A max of 2 can be recorded at any given time. This record is updated every time wolf tries to trigger Link, removing a recorded entity type that is not present in a 12 block radius every 3 attack cycle. Should wolf attack a new entity (that is not recorded) while Link Record already has 2, randomly replace either of the two with the new entity type. Additionally, if a player carries the wolf's soul using a Soul Runestone Plate, the Link Record is removed."
                + nextParagraph

                + "When the runestone contains an entity record and wolf successfully deals damage to any victim, the Link effect will be triggered and all nearby entities that match the recorded type will be damaged. The damage applied through Link will still apply the effects that comes from certain Wolf ATK Effect enchantments. Additionally, if wolf has no armor and Link effect is triggered, the damage applied will be based on its own attribute (total attack damage).";
    }
    private static String havocRunestonePlateInfo() {
        return titleHavocRunestonePlate.toUpperCase() + nextLine
                + "An item that can grant effects to the player via proxy (wolf). To use, simply hold this item and use it to interact with your wolf companion. This will unlock the HAVOC status and will give its owner this effect at intervals. If the owner has already used this Runestone's buff, wolf will grant the effect after the cooldown is removed. Each buff provides a Usage Points that the player can trigger when successfully hitting a target. The usage point is reset to 0 when the Havoc effect gets removed and vice versa."
                + nextParagraph

                + "HAVOC STATUS:" + nextLine
                + "When active and player gets the effect, and gets hit by a mob, the effect will undergo a change to certain Havoc Types depending the circumstance." + nextLine
                + "• Void: Player gets damaged and the damage exceeds 60% of their current health, and they have no Totem of Undying in their offhand or mainhand inventory slot." + nextLine
                + "• Expulsion: Player gets attacked by a mob that carries a shield, any armor, or totem of undying in their respective inventory slots, where at least 3 are occupied." + nextLine
                + "• Annihilation: Activated when the conditions for Void and Expulsion type are not met."
                + nextParagraph

                + "Havoc Status Buffs:" + nextLine
                + "• Annihilation: Player gets 6 usage points and transform the effect into Annihilation type which lasts for 60 seconds. When player attacks: the target suffers Weakness II effect which lasts 6 seconds and each attack extends the duration for another 6 seconds. If the target has the Resistance or Fire Resistance effect and has a duration longer than 3 seconds, its duration will be set to 3 seconds. However, if the duration has less than or equal to 3 seconds, the effects gets removed directly. Upon activation, this buff wil be on cooldown for 90 seconds."
                + nextParagraph

                + "• Expulsion: Player gets 2 usage points and transform the effect into Expulsion type which lasts for 30 seconds. When player attacks: target is knocked back and do: If the target is a player, search for a certain item in their equipment slot in sequential order: Shield, any chestplate, headwear, leggings or boots, any enchantable weapon, and Totem of Undying. If such item exists, it will be destroyed and if multiple qualifying items are present, the first item gets destroyed. However, if the target is not a player, paralyze target for 2 seconds. Upon activation, this buff wil be on cooldown for 3 minutes."
                + nextParagraph

                + "• Void: Player gets 2 usage points and transform the effect into Void type which lasts for 12 seconds. When player attacks: the target will be sent to the void and player will suffer Poison and Slowness effect that will stack for each trigger. Upon activation, this buff wil be on cooldown for 5 minutes and 30 seconds."
                + nextParagraph

                + "The buffs can be granted if the player has no existing cooldown of the Havoc Effect and only one buff can be active at any given time. Additionally, if the player has the " + descBenediction + " enchantment, grant 1 extra usage point and extra 15 seconds duration.";
    }
    private static String scatteredPagesInfo() {
        return titleScatteredPages.toUpperCase() + nextLine
                + "An item with readable contents that is scattered across the world. Explore and obtain and you might find discover something about how this all came to be :3";
    }


    public static String[] ITEMS_HANDBOOK_CONTENTS() {
        return addPage(fText(BOLD, "INTRODUCTION") + nextLine
                + "TOTVW: Wolf Additions adds new items to support the new mechanics introduced in the mod, giving you further contents to experience."
                + addSeparator
                + verixiumResourcesInfo()
                + addSeparator
                + lodestoneWindCoreInfo()
                + addSeparator
                + iridescentGlassInfo()
                + addSeparator
                + verdantSpruceStorageBoxInfo()
                + addSeparator
                + verixiumArmorUpgradeTemplateInfo()
                + addSeparator
                + soulRunestonePlateInfo()
                + addSeparator
                + tetherRunestonePlateInfo()
                + addSeparator
                + havocRunestonePlateInfo()
                + addSeparator
                + scatteredPagesInfo()
        );
    }
}
