package cliffordha.totvw.item.scatteredpages.contents;

import cliffordha.totvw.entity.player.PlayerStats;
import cliffordha.totvw.entity.wolf.WolfStats;
import cliffordha.totvw.item.scatteredpages.ScatteredPageItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import oshi.util.tuples.Pair;

import java.util.List;
import java.util.UUID;

import static cliffordha.totvw.item.scatteredpages.ScatteredPageTextColor.*;
import static cliffordha.totvw.item.scatteredpages.ScatteredPageTextStyle.*;
import static cliffordha.totvw.util.VWUtil.TextUtil.*;

public class MiscBookSet {
    public static Player player;
    public static void resolvePlayer(Player resolver) {
        player = resolver;
    }
    public static String[] get(int id) {
        if (player == null) return ScatteredPageItem.invalidPlayer;
        switch (id) {
            case -2 -> {
                return playerStats();
            }
            case 1000 -> {
                return multiTestPage();
            }
            case 333 -> {
                return getLodestoneWindCoreManual();
            }
        }
        return ScatteredPageItem.invalidRef;
    }
    public static String[] wolfStats(WolfStats stats) {
        return addPage(
                "UUID: " + stats.UUID() + nextLine
                + "Soul ID: " + stats.soulID() + nextLine
                + "Family ID: " + stats.familyID() + nextLine
                + "Owner: " + stats.owner() + nextLine
                + "Owner UUID: " + stats.ownerUUID() + nextLine
                + "Shared UUID: " + stats.sharedUUID() + nextParagraph

                + "Is Verdant: " + stats.isVerdant() + nextLine
                + "Benediction Stack: " + stats.benedictionStack() + nextLine
                + "Attack Cycle: " + stats.attackCycle() + nextLine
                + "Try Save Points: " + stats.trySavePoints() + nextLine
                + "Return Point: " + stats.returnPoint() + nextLine
                + "Runestone Type: " + stats.runestoneType() + nextParagraph

                + "Trusted Players: " + nextLine + getPairs(stats.trustedPlayers()) + nextParagraph
                + "Aggressors: " + nextLine + getPairs(stats.aggressors()) + nextParagraph

        );
    }
    private static String[] playerStats() {
        var stat = PlayerStats.valueOf(player);

        return addPage(
                "UUID: " + stat.UUID() + nextLine
                + "Wolf Souls: " + stat.wolfSouls() + nextLine
                + "SHARED UUID: " + stat.sharedUUID()
                + nextParagraph

                + "Atrocity Count [Wolf]: " + stat.wolfAtrocityCount() + nextLine
                + "Atrocity Cound [Villager]: " + stat.villagerAtrocityCount()
                + nextParagraph

                + "Havoc Type: " + stat.havocType() + nextLine
                + "Havoc Usage Count: " + stat.havocUsageCount() + nextLine

                + "TRUSTED PLAYER DATA: " + nextLine + getPairs(stat.trustedPlayers())
                + nextParagraph

                + "Has received handbook for: " + nextLine
                + "Enchantments: " + stat.hasReceivedEnchantmentsHandbook() + nextLine
                + "Items: " + stat.hasReceivedItemsHandbook() + nextLine
                + "Features: " + stat.hasReceivedFeaturesHandbook() + nextLine
                + "Effects: " + stat.hasReceivedEffectsHandbook()
        );
    }
    private static String getPairs(List<Pair<String, UUID>> list) {
        return list.stream().map(pair -> pair.getA() + " " + pair.getB()).reduce((a, b) -> a + nextLine + b).orElse("");
    }
    private static String[] multiTestPage() {
        boolean HAS_ARMOR = !player.getItemBySlot(EquipmentSlot.CHEST).isEmpty();
        return addPage(
                "This is a page that has a lot of contents and is intended to be used for testing purposes (obviously). In this " + fText(UNDERLINED, "scattered page") + ", you can see a lot of text formatting options, colors, etc. used for testing this very UI you are looking at."
                        + nextParagraph
                        + "This is its second paragraph. I bet it could do more than that. What if, let's say, you have an armor while reading this text? Look here: " + tText(HAS_ARMOR, cText(RED, "COLOR"), cText(AQUA, "COLOR"))
                        + nextParagraph
                        + "Did you try it? If you have an armor, you should see a red color on the " + fText(UNDERLINED, "COLOR") + " text otherwise, you should see aqua color."
                        + nextParagraph
                        + "But, what if I wanna underline a text? Look here: " + fText(UNDERLINED, "UNDERLINED")
                        + " Do you wanna see a couple more?"
                        + nextParagraph
                        + "Look below: "
                        + nextLine
                        + fText(ITALIC, "ITALIC")
                        + nextLine
                        + fText(BOLD, "BOLD")
                        + nextLine
                        + fText(STRIKETHROUGH, "STRIKETHROUGH")
                        + nextParagraph
                        + "There is a lot to test to achieve a desirable output and that is because each scattered page (item) has different contents which means it also has different text formatting."
                        + nextParagraph
                        + "This is the end of this page. Hope you enjoyed it!"
        );
    }


    private static String[] getLodestoneWindCoreManual() {
        boolean IN_LOWLIGHT = player.level().getMaxLocalRawBrightness(player.blockPosition(), 0) < 9;
        return addPage(fText(BOLD, fText(ITALIC, "An Experimental Research on Wind-charged Monster Deterring Field to Combat the Catastrophic Effects of the Creatures from Beyond"))
                + nextParagraph

                + fText(BOLD, "APPROVAL") + nextLine
                + "\"By virtue granted by our god, 'we', the people who reside among the Verdant Forest, grant the qualified Scholars access to resources nurtured by our land for an indefinite period of time. Purpose of access extends only to the following agreed upon use: studying, developing, and manufacturing solutions that may bring end to disasters caused by the creatures from beyond.\""
                + nextParagraph

                + "Grantor(s): Signed by " + bText("the people's chief.") + nextLine
                + "Grantee(s): Scholars from " + bText("the Nation of Erudites")

                + addSeparator
                + fText(BOLD, "ABSTRACT") + nextLine
                + "By modifying the Lodestone's attracting energy properties, we essentially create a wind field that can be used to deter any living things nearby. This core will serve as a getProtection field for the Scholars and hunters to minimize the risk of injury as well as mortality rate when such individuals are within places where safety is a concern."
                + nextParagraph
                + "Project's deterring performance showed promising results as it deterred the qualified subjects across different environment, including different variables, with 97% success rate. This has surpassed its prototype's deterring performance by at least 40%. However, its energy efficiency has dropped down to 70% unlike its protype with a staggering 96% at normal conditions. Project has been marked for further testing."
                + addSeparator
                + fText(BOLD, "METHOD") + nextLine
                + "To operate the Lodestone Wind Core (proposed name), an energy source is required before it can be activated by a special Verixium-based paper. Usable energy types are as follows: " + fText(STRIKETHROUGH, "Block of Redstone") + ", Verixium Powder Block, Verixium Powder, " + fText(STRIKETHROUGH, "Water Fluid") + ", Wind Charge. " + cText(GRAY, "Request for additional information for other energy sources is pending...")
                + nextParagraph
                + "Energy consumption may vary depending on the environment. That being said, the core will operate at stronger frequencies when enough energy is readily available for use."
                + nextParagraph
                + "Normal Parameters: Wind energy < 60000, stable, qualified subjects are scanned at optimal distance." + nextLine
                + "High Parameters: Wind energy > 60000, unstable, close contact with the core may cause nausea and fatigue however, qualified subjects are scanned at longer distances."
                + nextParagraph

                + "Once the core is activated, it will immediately start harnessing the surrounding Wind Energy and simultaneously convert it into two separate fields:"
                + nextParagraph

                + fText(BOLD, "PROTECTION FIELD") + nextLine
                + "At intervals, the core will grant healing and random effects to nearby wolves (hunters) and scholars. Code 024 shows that when the core enters the " + bText("unstable") + " state, said grantee also receives stronger effects than usual. No consistent records yet."
                + nextParagraph

                + fText(BOLD, "DETERRING FIELD") + nextLine
                + "At intervals, the core will deter marked subjects until they get incapacitated. The deterring field will also siphon the subject's internal pressure which will create a pressure difference (PD) point. When such point exceed the standard 100PD point threshold, the subject may suffer from their own implosion. The rate at which PD accumulates depend on the surrounding environment and the subjects mass."

                + addSeparator
                + fText(BOLD, "USE NOTICE") + nextLine
                + "• High Priority: If any related incident arises, be it from using this material or its content(s), please notify and report to the Head of Scholars or authorities from the " + bText("Nolayan") + " people immediately." + nextLine
                + "• High Priority: DO NOT STAND VERY CLOSELY TO THE CORE WHEN TESTING FOR HIGH ENERGY EFFICIENCY!" + nextLine
                + "• Recalibrate the qualified variables every day to prevent unnecessary checks and save energy." + nextLine
                + "• Report energy use every 30 minutes." + nextLine
                + "• Do not let the core run indefinitely in scorching environments."

                + addSeparator
                + tText(IN_LOWLIGHT,fText(BOLD, "INCIDENT REPORT") + nextLine
                + "• " + cText(DARK_GRAY, "023: ") + "Scholars who had altercations with the locals seem to suffer fatigue when near the core. Due to the nature of incident, speculations are dismissed and the investigation is made unavailable to other scholars." + nextLine
                + "• " + cText(DARK_GRAY, "024: ") + "The core occasionally enters state where Wind Energy readings are abnormally high despite having shown no negative effects to wolves and scholars.", "")
        );
    }
}
