package cliffordha.totvw.item.pages.handbooks;

import static cliffordha.totvw.item.pages.FTextColor.*;
import static cliffordha.totvw.item.pages.FTextStyle.*;
import static cliffordha.totvw.util.TextUtil.*;

public class FeatureHandbook {
    public static final String titleVerdantTypeWolves = cText(AQUA, "Verdant-type Wolves");
    public static final String titleVerdantTypeVillagers = cText(AQUA, "Verdant-type Villagers");
    public static final String titleBenedictionReturnPosition = cText(AQUA, "Benediction Return Point");
    public static final String titleRevivalByProxy = cText(AQUA, "Revival by Proxy");
    public static final String titleTrustSystem = cText(GOLD, "Alliance [Trust System]");

    public static String revivalByProxy() {
        return
                "Allows a player to be revived by their wolf companion when they get incapacitated and have no Totem of Undying in their mainhand or offhand slot (enabled by default in the settings). This can be triggered in the following ways:"
                + nextParagraph

                + "• Player gets incapacitated and their wolf companion has more than one Benediction Stack and is in a loaded chunk."
                + nextParagraph

                + "• If the above trigger fails for any reason, if the player has the Soul Runestone Plate and has wolf souls that meet the criteria mentioned above, the player will be revived by their wolf companion. Check the Soul Runestone Plate entry in the Items Handbook for additional information.";
    }
    public static String verdantTypeWolves() {
        return
                "This type (and variant) of wolves are found in the new Verdant Biomes."
                + nextParagraph

                + "• These wolves don't attack their own even when a player who tamed them attacks one of their kind, as well as the new Verdant-type Villagers."
                + nextParagraph

                + "• While an untamed verdant wolf is near a villager, if there is a monster that targets said villager, these verdant wolves will attack the assaulting monster after a 3 second deliberation which will amount the Try-Save-Status mechanic."
                + nextParagraph

                + fText(BOLD, "Try-Save-Status:") + nextLine
                + "When a wolf successfully targets and hits a monster that previously targeted a villager, it will gain 1 Try-Save point and the maximum amount of points gained this way is 12.";
    }
    public static String verdantTypeVillagers() {
        return
                "A new type of villager that are found in the new Verdant Biomes."
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
    public static String benedictionSpawnpointInfo() {
        return
                "Holding any bed then interacting it with your wolf companion will set a Benediction Return Point for them. When they get knocked down and said return point is present and valid, wolf will attempt to teleport to the return point. If said position is invalid, they will be recorded instead."
                + nextParagraph

                + "However, if you are not present when your wolf gets knocked down, override this and record them instead. The recorded wolves will respawn when you load back in."
                ;
    }
    public static String trustSystemInfo() {
        return
                "Reintroduced from earlier versions of the mod. To utilize this feature, you can use a Paper or Verixium Paper towards a fellow player by right clicking them. A request will be sent to them and you will get notified once the they accept the request. This Trust System provides damage protection for both parties and is also used for Wolf-Player interaction."
                + nextParagraph

                + "For wolves: Wolf will ignore damage from players that you trust provided that they also listed you as trustee. Your list of trusted players is auto synced to wolf every 3 seconds."
                + nextParagraph

                + "For players: Trusted players that also list you as trustee won't be affected by your attacks. This also applies to them."
                + nextParagraph

                + "Unsynced Status: Removing a trusted player gets automatically removed from the other player's list if available. In cases where it fails to auto-sync, the data becomes unsynced and the one without data must manually request again to fix the problem. While in this state, the damage protection will not work.";
    }


    public static String FEATURES_HANDBOOK_CONTENTS() {
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
