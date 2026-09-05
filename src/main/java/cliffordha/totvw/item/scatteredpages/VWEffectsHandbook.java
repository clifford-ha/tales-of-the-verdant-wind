package cliffordha.totvw.item.scatteredpages;

import static cliffordha.totvw.item.scatteredpages.ScatteredPageTextColor.*;
import static cliffordha.totvw.item.scatteredpages.ScatteredPageTextStyle.*;
import static cliffordha.totvw.util.VWUtil.TextUtil.*;

public class VWEffectsHandbook {
    private static final String dot = ".";

    private static final String descBenediction = "Benediction of the Verdant Mountains";
    private static final String descAttackDMG = "Attack Damage";

    private static final String titleBlessing = cText(DARK_AQUA, bold("Blessing of the Verdant Wind"));
    private static final String titleAmplifiedMight = cText(AQUA, bold("Amplified Might"));
    private static final String titleBloodlust = cText(RED, bold("Bloodlust"));
    private static final String titleParalyze = cText(RED, bold("Paralyze"));

    private static String italic(String t) {
        return fText(ITALIC, t);
    }
    private static String bold(String t) {
        return fText(BOLD, t);
    }

    private static String blessingOfTheVerdantWind() {
        return titleBlessing.toUpperCase() + nextLine
                + "This can be granted by the " + descBenediction + " enchantment when a wolf or player triggers certain conditions or when near an active Lodestone Wind Core. Additionally, this effect is removed when the entity is a monster or enemy type."
                + nextParagraph
                + "While active: " + nextLine
                + "• Increase base " + descAttackDMG + " by 2." + nextLine
                + "• Increase Max Health by 20% for every AMP up to a max of 50%." + nextLine
                + "• Reduce burn time by 20% for every AMP.";
    }
    private static String amplifiedMight() {
        return titleAmplifiedMight.toUpperCase() + nextLine
                + "While active: " + nextLine
                + "• Increase Armor Toughness, Knockback Resistance, Max Absorption, and Jump Strength based on AMP." + nextLine
                + "• Reduce Fall Damage by 20% for every AMP.";
    }
    private static String bloodlust() {
        return titleBloodlust.toUpperCase() + nextLine
                + "An effect that grants stronger " + descAttackDMG + " at the cost of persistent " + cText(RED, "self-damage") + " based on the entity's current HP within every 3 seconds. For every AMP, the damage amount can be increased up to 30% (max) + 1 of current HP. Additionally, this effect is converted into Weakness if the following are met: entity's current HP has less than or equal to 4 points (2 hearts) or the self-damage is equal or more than the entity's current health."
                + nextParagraph

                + "Said self-damage can be reduced or negated through the following:" + nextLine
                + "• If the entity is a player or a wolf, and has the " + descBenediction + " enchantment, the damage has a 60% chance to be ignored." + nextLine
                + "• If the entity is a wolf, and has the " + descBenediction + " and Might enchantments (at eny level), the damage will be completely ignored." + nextLine
                + "• If the entity has the Protection enchantment that is equal to or more than 4 enchantment level, the self-inflicted damage is set to a constant half a heart or 1 point."
                + nextParagraph

                + "While active: " + nextLine
                + "• Increase base " + descAttackDMG + " by 2 (in any AMP) and total " + descAttackDMG + " by 20% for every AMP up to a max of 120%." + nextLine
                + "• Increase base Movement Speed by 15% for every AMP up to a max of 45%." + nextLine
                + "• Reduce armor points by 30% (10% if the affected is a wolf) for every AMP.";
    }
    private static String paralyze() {
        return titleParalyze.toUpperCase() + nextLine
                + "An affect that disables the movement and attack capabilities of the entity. This effect can only be produced by the Bloodlust enchantment when a wolf successfully attacks and paralyzes a target. Will not work to a player that is not in Survival mode.";
    }
    public static final String EFFECTS_HANDBOOK_TITLE = addTitle("Effects Handbook");
    public static String[] EFFECTS_HANDBOOK_CONTENTS() {
        return addPage(fText(BOLD, "INTRODUCTION") + nextLine
                + "TOTVW: Wolf Additions introduces several custom effects that are mostly utilized by the Wolf ATK Effects enchantment."
                + nextParagraph
                + " For easier explanation:" + nextLine
                + fText(BOLD, "Entity") + ": The entity (or you) that is affected by the effect." + nextLine
                + fText(BOLD, "AMP") + ": The effect's amplifier level."
                + addSeparator
                + blessingOfTheVerdantWind()
                + addSeparator
                + amplifiedMight()
                + addSeparator
                + bloodlust()
                + addSeparator
                + paralyze()
                + addSeparator
                + cText(DARK_AQUA, "#".repeat(64)) + nextLine
                + cText(DARK_AQUA, "#".repeat(64))
        );
    }
}
