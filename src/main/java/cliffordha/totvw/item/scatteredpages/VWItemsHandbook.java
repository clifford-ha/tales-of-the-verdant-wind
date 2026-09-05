package cliffordha.totvw.item.scatteredpages;

import static cliffordha.totvw.item.scatteredpages.ScatteredPageTextColor.*;
import static cliffordha.totvw.item.scatteredpages.ScatteredPageTextStyle.*;
import static cliffordha.totvw.util.VWUtil.TextUtil.*;

public class VWItemsHandbook {
    private static final String dot = ".";
    private static final String descBenediction = "Benediction of the Verdant Mountains";
    private static final String descWindCoreEnergySources = "Verixium Powder, Verixium Powder Block, and Wind Charge.";

    private static final String titleSoulRunestonePlate = cText(DARK_AQUA, bold("Soul Runestone Plate"));
    private static final String titleLodestoneWindCore = cText(YELLOW, bold("Lodestone Wind Core"));

    private static String italic(String t) {
        return fText(ITALIC, t);
    }
    private static String bold(String t) {
        return fText(BOLD, t);
    }

    private static String soulRunestonePlateInfo() {
        return titleSoulRunestonePlate.toUpperCase() + nextLine
                + "An item that allows you to store your wolf companions's soul within your own, allowing you to travel with ease knowing that you can summon them anytime later. You can store up to 5 wolf souls at most. However, if you have the " + descBenediction + " enchantment, the limit will be capped to 12 instead."
                + nextParagraph
                + "Moreover, when you reach the 3 souls threshold, adding more will result in a penalty that will inflict damage upon you. This has a 60% chance of getting triggered and each soul past the threshold will multiply the damage."
                + nextParagraph

                + "This item also allows you to trigger Revival by Proxy provided that you meet the prerequisites. If the threshold is met, each additional soul contained will increase the chances of this item to break and fragment itself.";
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

    public static final String ITEMS_HANDBOOK_TITLE = addTitle("Items Handbook");
    public static String[] ITEMS_HANDBOOK_CONTENTS() {
        return addPage(fText(BOLD, "INTRODUCTION") + nextLine
                + "TOTVW: Wolf Additions adds new items to support the new mechanics introduced in the mod, giving you further contents to experience."
                + addSeparator
                + soulRunestonePlateInfo()
                + addSeparator
                + lodestoneWindCoreInfo()
        );
    }
}
