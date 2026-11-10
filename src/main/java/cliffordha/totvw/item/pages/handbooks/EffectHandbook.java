package cliffordha.totvw.item.pages.handbooks;

import static cliffordha.totvw.item.pages.FTextColor.*;
import static cliffordha.totvw.item.pages.FTextStyle.*;
import static cliffordha.totvw.util.TextUtil.*;

public class EffectHandbook {
    public static final String descBenediction = "Benediction of the Verdant Mountains";
    public static final String descAttackDMG = "Attack";

    public static final String titleBlessing = cText(DARK_AQUA, "Blessing of the Verdant Wind");
    public static final String titleWindVeil = cText(DARK_AQUA,  "Wind Veil");
    public static final String titleAmplifiedMight = cText(AQUA, "Amplified Might");
    public static final String titleBloodlust = cText(RED, "Bloodlust");
    public static final String titleParalyze = cText(RED, "Paralyze");
    public static final String titleHavoc = cText(RED, "Havoc");

    public static String blessingOfTheVerdantWind() {
        return
                "This can be granted by the " + descBenediction + " enchantment when a wolf or player triggers certain conditions or when near an active Lodestone Wind Core. Additionally, this effect is removed when the entity is a monster or enemy type."
                + nextParagraph
                + "While active: " + nextLine
                + "• Increase base " + descAttackDMG + " by 2." + nextLine
                + "• Increase Max Health by 20% for every AMP up to a max of 50%." + nextLine
                + "• Reduce burn time by 20% for every AMP.";
    }
    public static String windVeil() {
        return
                "An effect that completely nullifies any damage that the entity receives. If the damage comes from another mob and when the entity is a player or a wolf, and have the " + descBenediction + " enchantment, there is a 60% chance to knock back the mob."
                + nextParagraph

                + "Additionally, the effects of Havoc Runestone can be still be triggered.";
    }
    public static String amplifiedMight() {
        return
                "While active: " + nextLine
                + "• Increase Armor Toughness, Knockback Resistance, Max Absorption, and Jump Strength based on AMP." + nextLine
                + "• Reduce Fall Damage by 20% for every AMP.";
    }
    public static String bloodlust() {
        return
                "An effect that grants stronger " + descAttackDMG + " at the cost of persistent " + cText(RED, "self-damage") + " based on the entity's current HP every 3 seconds. For every AMP, the damage amount can be increased up to 30% (max) + 1 of current HP. Additionally, this effect is converted into Weakness if the following are met: entity's current HP has less than or equal to 4 points (2 hearts) or the self-damage is equal or more than the entity's current health."
                + nextParagraph

                + "Said self-damage can be reduced or negated through the following:" + nextLine
                + "• If the entity is a player or a wolf, and has the " + descBenediction + " enchantment, the damage has a 60% chance to be ignored." + nextLine
                + "• If the entity is a wolf, and has the " + descBenediction + " and Might enchantments (at eny level), the damage will be completely ignored. Moreover, if wol has a target and has the Havoc Runestone, the self-damage is transferred to the target instead." + nextLine
                + "• If the entity has the Protection enchantment that is equal to or more than 4 enchantment level, the self-inflicted damage is set to a constant half a heart or 1 point."
                + nextParagraph

                + "While active: " + nextLine
                + "• Increase Base " + descAttackDMG + " by 2 (in any AMP) and Total " + descAttackDMG + " by 20% for every AMP up to a max of 120%." + nextLine
                + "• Increase base Movement Speed by 15% for every AMP up to a max of 45%." + nextLine
                + "• Reduce armor points by 30% (10% if the affected is a wolf) for every AMP.";
    }
    public static String paralyze() {
        return
                "An affect that disables the movement and attack capabilities of the entity. This effect can be obtained by the Bloodlust enchantment when a wolf successfully attacks and paralyzes a target or is given by the Havoc Runestone. Will not work to a player that is not in Survival mode.";
    }
    public static String havoc() {
        return
                "An effect only acquirable when a wolf with a Havoc Runestone is present with you in the same dimension or in the Nether. While active: after you get a damage, the effect will undergo transmutation depending on how the damage was applied to you."
                + nextParagraph

                + "Read the " + fText(UNDERLINED, "Havoc Runestone Plate") + " entry in the Items Handbook for more information.";
    }
}
