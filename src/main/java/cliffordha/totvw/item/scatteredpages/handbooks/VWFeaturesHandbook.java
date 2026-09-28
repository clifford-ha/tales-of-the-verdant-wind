package cliffordha.totvw.item.scatteredpages.handbooks;

import static cliffordha.totvw.item.scatteredpages.ScatteredPageTextColor.*;
import static cliffordha.totvw.item.scatteredpages.ScatteredPageTextStyle.*;
import static cliffordha.totvw.util.VWUtil.TextUtil.*;

public class VWFeaturesHandbook {
    private static final String descAttackDMG = "Attack Damage";
    private static final String descBenediction = "Benediction of the Verdant Mountains";
    private static final String descBleedingDMG = cText(RED, "Bleeding damage");

    private static final String titleVerdantTypeWolves = cText(AQUA, bold("Verdant-type Wolves"));
    private static final String titleVerdantTypeVillagers = cText(AQUA, bold("Verdant-type Villagers"));
    private static final String titleBenedictionReturnPosition = cText(AQUA, bold("Benediction Return Position"));
    private static final String titleRevivalByProxy = cText(AQUA, bold("Revival by Proxy"));
    private static final String titleTrustSystem = cText(GOLD, bold("Trust System"));

    private static final String titleWolfArmorEnhancementKit = cText(YELLOW, bold("Wolf Armor Enhancement Kit"));
    private static final String titleMight = cText(AQUA, bold("Might"));
    private static final String titleBloodlust = cText(RED, bold("Bloodlust"));
    private static final String titleOozing = cText(GREEN, bold("Oozing"));
    private static final String titleIgnition = cText(GOLD, bold("Ignition"));
    private static final String titleLifting = cText(DARK_PURPLE, bold("Lifting"));
    private static final String titleGnawing = cText(LIGHT_PURPLE, bold("Gnawing"));
    private static final String titlePoisoning = cText(DARK_GREEN, bold("Poisoning"));
    private static final String titleWithering = cText(DARK_RED, bold("Withering"));
    private static final String titleMending = cText(LIGHT_PURPLE, bold("Mending"));

    private static String italic(String t) {
        return fText(ITALIC, t);
    }
    private static String bold(String t) {
        return fText(BOLD, t);
    }

    private static String revivalByProxy() {
        return titleRevivalByProxy.toUpperCase() + nextLine
                + "A mechanic allows a player to be revived by their wolf companion when they get incapacitated and have no Totem of Undying in their mainhand or offhand slot (enabled by default in the settings). This can be triggered in the following ways:"
                + nextParagraph

                + "• Primary Trigger: Player gets incapacitated and their wolf companion is within the scan radius with more than one Benediction Stack. The scan radius is based on Max Chunk Scan in the settings (server)."
                + nextParagraph

                + "• If the Primary Trigger fails for any reason, if the player has the Soul Runestone Plate and has a wolf souls that meet the criteria mentioned above, the player will be revived by their wolf companion. Check the Soul Runestone Plate entry in the Items Handbook for additional information.";
    }
    private static String verdantTypeWolves() {
        return titleVerdantTypeWolves.toUpperCase() + nextLine
                + "This type (and variant) of wolves are found in the new Verdant Biomes."
                + nextParagraph

                + "• These wolves don't attack their own even when a player who tamed them attacks one of their kind, as well as the new Verdant-type Villagers."
                + nextParagraph

                + "• While an untamed verdant wolf is near a villager, if there is a monster that targets said villager, these verdant wolves will attack the assaulting monster after a 3 second deliberation which will trigger the Try-Save-Status mechanic."
                + nextParagraph

                + fText(BOLD, "Try-Save-Status:") + nextLine
                + "When a wolf successfully targets and hits a monster that previously targeted a villager, it will gain 1 Try-Save point and the maximum amount of points gained this way is 12.";
    }
    private static String verdantTypeVillagers() {
        return titleVerdantTypeVillagers.toUpperCase() + nextLine
                + "A new type of villager that are found in the new Verdant Biomes."
                + nextParagraph

                + "• If a verdant villager has a profession, offer discount to player when they have no or have less than 20 points of Villager Atrocity (Counter). These discounts are randomized, ranged from 15% to 50%, every 24 minutes or a full minecraft day. The max range for discount will be set to 80% when the verdant villager is within its own biome."
                + nextParagraph

                + "• If this type of villager is a cleric (profession), otherwise known as verdant-cleric, they will gain the ability to cast healing to nearby villagers of any type or variant and iron golems. "

                + fText(BOLD, "For other villagers: ")
                + "heal once every 30 seconds when others' health drops below 90%. "

                +fText(BOLD, "For iron golems: ")
                + "heal once every 90 seconds when their health drops below 75%."
                + nextParagraph

                + "Additionally, the verdant-cleric will be able to heal verdant wolves that have Try-Save point(s) once every 60 seconds when their health drops below 90% until they have 0 Try-Save point(s) left."
                + nextParagraph

                + "A verdant-cleric's healing cooldown may be reduced when an active Lodestone Wind Core is nearby."
                ;
    }
    private static String benedictionSpawnpointInfo() {
        return titleBenedictionReturnPosition.toUpperCase() + nextLine
                + "When a wolf has the " + descBenediction + " enchantment, the player will be able to set a return point for the wolf. When the wolf gets knocked down and said return point is present and valid, and Teleport After Save is enabled, wolf will atempt to teleport to thet return point if it is valid.";
    }
    private static String trustSystemInfo() {
        return titleTrustSystem.toUpperCase() + nextLine
                + "Reintroduced from earlier versions of the mod. To utilize this feature, a player can use a Paper or Verixium Paper towards a fellow player by right clicking them. A text prompt should notify you upon succeeding. These Trust System is currently being used for Wolf-Player interaction."
                + nextParagraph

                + "When a player has a tamed wolf, they can use the command to perform various actions like syncing the data from player to wolf: " + bold("[  /totvw trustData wolf sync  ]")
                + nextParagraph

                + "The player can also remove all or specific trusted players from their data. You need to query"
                ;
    }


    public static String[] FEATURES_HANDBOOK_CONTENTS() {
        return addPage(
                fText(BOLD, "INTRODUCTION") + nextLine
                        + "A list of new features and mechanics that aren't introduced in other handbooks."
                        + addSeparator
                        + revivalByProxy()
                        + addSeparator
                        + verdantTypeWolves()
                        + addSeparator
                        + verdantTypeVillagers()
                        + addSeparator
                        + benedictionSpawnpointInfo()
                + addSeparator

        );
    }
}
