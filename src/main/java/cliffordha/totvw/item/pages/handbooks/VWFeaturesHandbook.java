package cliffordha.totvw.item.pages.handbooks;

import static cliffordha.totvw.item.pages.FTextColor.*;
import static cliffordha.totvw.item.pages.FTextStyle.*;
import static cliffordha.totvw.util.TextUtil.*;

public class VWFeaturesHandbook {
    private static final String titleVerdantTypeWolves = cText(AQUA, bold("Verdant-type Wolves"));
    private static final String titleVerdantTypeVillagers = cText(AQUA, bold("Verdant-type Villagers"));
    private static final String titleBenedictionReturnPosition = cText(AQUA, bold("Benediction Return Point"));
    private static final String titleRevivalByProxy = cText(AQUA, bold("Revival by Proxy"));
    private static final String titleTrustSystem = cText(GOLD, bold("Trust System"));

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

                + "• While an untamed verdant wolf is near a villager, if there is a monster that targets said villager, these verdant wolves will attack the assaulting monster after a 3 second deliberation which will amount the Try-Save-Status mechanic."
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
                + "Holding any bed then interacting it with your wolf companion will set a Benediction Return Point for them. When they get knocked down and said return point is present and valid, wolf will attempt to teleport to the return point. If said position is invalid, they will be recorded instead."
                + nextParagraph

                + "However, if you are not present when your wolf gets knocked down, override this and record them instead. The recorded wolves will respawn when you load back in."
                ;
    }
    private static String trustSystemInfo() {
        return titleTrustSystem.toUpperCase() + nextLine
                + "Reintroduced from earlier versions of the mod. To utilize this feature, a player can use a Paper or Verixium Paper towards a fellow player by right clicking them. A text prompt should notify you upon succeeding. This Trust System is currently being used for Wolf-Player interaction."
                + nextParagraph

                + "For wolves: Wolf will ignore damage from players that you trust provided that they also listed you as trustee. Your list of trusted players is auto synced to wolf every 3 seconds."
                + nextParagraph

                + "For players: Trusted players that also list you as trustee won't be affected by your attacks. This also applies to them.";
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
                        + trustSystemInfo()
                + addSeparator

        );
    }
}
