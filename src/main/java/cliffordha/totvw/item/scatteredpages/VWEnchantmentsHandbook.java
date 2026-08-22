package cliffordha.totvw.item.scatteredpages;

import cliffordha.totvw.config.VWConfig;

import static cliffordha.totvw.item.scatteredpages.ScatteredPageTextColor.*;
import static cliffordha.totvw.item.scatteredpages.ScatteredPageTextStyle.*;
import static cliffordha.totvw.util.VWUtil.TextUtil.*;

public class VWEnchantmentsHandbook {
    private static final String dot = ".";
    private static final String descAttackDMG = "Attack Damage";
    private static final String descBenediction = "Benediction of the Verdant Mountains";
    private static final String descBleedingDMG = cText(RED, "Bleeding damage");

    private static final String titleBenediction = cText(DARK_AQUA, bold("Benediction of the Verdant Mountains"));
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

    private static String benedictionOfTheVerdantMountainsInfo() {
        return titleBenediction.toUpperCase() + nextLine
                + "This is the core enchantment that will provide access to features that are related to the Verdant Mountains, as well as the key to unlocking the full potential of other enchantments. This enchantment also gives " + cText(DARK_AQUA, "+3 ") + descAttackDMG + dot
                + nextParagraph

                + bold("Core Features:") + nextLine
                + "• If your wolf companion has the enchantment, you may want to give them a Totem of Undying and it will be automatically converted into a Benediction Stack. They will automatically use it to revive themselves when they get knocked down. The stack will also remain even if the armor is removed or destroyed during combat."
                + nextParagraph
                + "• Your companion can also share their stack through " + fText(UNDERLINED, "Revival by Proxy") + cText(DARK_GRAY, " (Enabled by default)") + dot
                + nextParagraph
                + "• When your health drops below " + cText(AQUA, VWConfig.get().SERVER_BENEDICTION_HEALTH_THRESHOLD + "") + ", it will grant " + cText(AQUA, "Blessing of the Verdant Wind (30 sec) and temporary immunity for 10 seconds") + ". This buff also applies to your companion when you have the enchantment and their health drops below said threshold. The duration of cooldown depends on your current game difficulty. This buff is considered a skill."
                + nextParagraph
                + "• Passive: If wolf gets hurt and the damage exceeds 50% of their max health, it will be entirely ignored (except if the damage comes from /kill command). This passive has a cooldown of 15 seconds."
                + nextParagraph

                + bold("Combat Features:") + nextLine
                + "• When holding a tool or weapon with a [VW] mark, hold *Crouch + Right Click to activate the buff. The buff provided depends on what tool you are holding. The duration and amplifier are also increased when within the Verdant Biomes!";
    }
    private static String wolfArmorEnhancementKitInfo() {
        return titleWolfArmorEnhancementKit.toUpperCase() + nextLine
                + "This enchantment slightly boosts your companion's base attributes: Max Health, Water Movement Speed, and Knockback Resistance.";
    }
    private static String mightEnchantmentInfo() {
        return titleMight.toUpperCase() + nextLine
                + "On attack: grant Absorption and Amplified Might effect for a short period of time, scalable by enchantment level. The enchantment also boosts " + descAttackDMG + " and lessens Fall Damage. Additionally, if wolf has a baby wolf nearby, base effects will be shared to them."
                + nextParagraph

                + italic("At level 3 or higher:") + nextLine
                + "• Forcibly remove the Strength effect from the target if present." + nextLine
                + "• When wolf is on fire, automatically extinguish self. Additionally, if wolf has a baby wolf nearby that is on fire, the effect will be extended to them as well." + nextLine
                + "• " + cText(AQUA, "Rupture") + " skill is unlocked."
                + nextParagraph

                + bold("Rupture") + nextLine
                + "A skill that deals an additional instance of " + descBleedingDMG + " when the target's health is below 60% of their max health — damage scales based on distance to owner. If the owner is in a different dimension or is not online, damage will scale to 180% of wolf's max health. The cooldown duration depends on the current game difficulty and is further reduced if your companion has the Benediction of the Verdant Mountains enchantment."
                + nextParagraph

                + italic("At level 5 or higher:") + nextLine
                + "• Deal another instance of " + descBleedingDMG + " equal to 10% of wolf's max health.";
    }
    private static String bloodlustEnchantmentInfo() {
        return titleBloodlust.toUpperCase() + nextLine
                + "On attack: grant the Bloodlust effect to wolf while the target gets Weakness effect for a short period of time and the amplifier of the effect is based on the enchantment level. The enchantment also boosts the following: " + descAttackDMG + ", Attack Knockback, and Knockback Resistance."
                + nextParagraph

                + bold("Paralyze") + nextLine
                + "A skill that will paralyze the target for a period of time if it is a player or if their health is more than 20 points. The cooldown duration depends on the current game difficulty."
                + nextParagraph

                + italic("At level 3 or higher:") + nextLine
                + "• On attack, target will get Slowness and remove the following: Speed & Regeneration effects if present.";
    }
    private static String oozingEnchantmentInfo() {
        return titleOozing.toUpperCase() + nextLine
                + "On attack, inflict stackable Oozing effect to target for one minute and said duration will be multiplied if wolf also has the "
                + cText(RED, "Bloodlust (1.5x)") + " or " + cText(AQUA, "Might (1.25x)") + " enchantment.";
    }
    private static String ignitionEnchantmentInfo() {
        return titleIgnition.toUpperCase() + nextLine
                + "Wolf will now ignore any damages that inflict " + cText(AQUA, "freezing") + " (vanilla). On attack, ignite target for 3 seconds (multiplied by enchantment level). If target is immune to fire, deal " + cText(GOLD, "Scorching Heat") + " instead. Damage and duration of fire increases when wolf is within Nether or hot biomes. Additionally, wolf will automatically extinguish itself when on fire, and if wolf has a baby wolf nearby that is on fire, extend the effect to them as well, including the passive."
                + nextParagraph

                + italic("At level 3 or higher:") + nextLine
                + "• Forcibly remove the Fire Resistance effect from the target if present." + nextLine
                + "• There is a small chance to break the Powdered Snow block when wolf is inside of it."

                + nextParagraph
                + "• Passive: When wolf is within the Nether biomes and also has " + descBenediction + " and Fire Protection equal to or more than 3 enchantment level, grant a strong Fire Resistance effect. This passive is extended when their owner is also present and is nearby.";
    }
    private static String liftingEnchantmentInfo() {
        return titleLifting.toUpperCase() + nextLine
                + "On attack, lift enemy from ground by inflicting Levitation effect for a short period of time. If said effect is already present, knock back the target instead.";
    }
    private static String gnawingEnchantmentInfo() {
        return titleGnawing.toUpperCase() + nextLine
                + "On attack, heal wolf by 15% of its max health. If wolf has a baby wolf nearby, healing will be shared to them as well."
                + nextParagraph

                + "• Passive: When wolf's owner is present and wearing armor enchanted with any of the following:" + nextLine
                + " > Blast Protection" + nextLine
                + " > Fire Protection" + nextLine
                + " > Projectile Protection or Protection"
                + nextParagraph

                + "...the owner will be healed based on that enchantment's level, capped at 20% of their max health. If multiple qualifying enchantments are present, the first one in that order takes priority. Additionally, if the owner's armor have the " + descBenediction + " enchantment, the heal amount receives an additional 20% boost."
                + nextParagraph

                + italic("At level 2 or higher:") + nextLine
                + "• Wolf's on-attack heal increases to 30% of its max health and the owner's heal cap is raised to 40%.";
    }
    private static String poisoningEnchantmentInfo() {
        return titlePoisoning.toUpperCase() + nextLine
                + "On attack, inflict stackable Poison effect to target for a period of time. The duration of the effect is based on the enchantment level."
                + nextParagraph

                + italic("At level 3 or higher:") + nextLine
                + "• Forcibly remove the Regeneration effect from the target if present.";
    }
    private static String witheringEnchantmentInfo() {
        return titleWithering.toUpperCase() + nextLine
                + "On attack, inflict Wither effect to target for a short period of time. The duration and amplifier of the effect is based on the enchantment level. If target is invulnerable to the effect, deal " + descBleedingDMG + " instead based on 50% of target's current health."
                + nextParagraph

                + italic("At level 3 or higher:") + nextLine
                + "• Forcibly remove the Regeneration effect from the target if present.";
    }
    private static String mendingEnchantmentInfo() {
        return titleMending.toUpperCase() + nextLine
                + "On attack, if wolf's armor has been damaged, restore durability based on wolf's current total " + descAttackDMG + ". If wolf also has the " + descBenediction + " enchantment, the restoration efficiency will further improve.";
    }

    public static final String ENCHANTMENTS_HANDBOOK_TITLE = addTitle("Enchantments Handbook");
    public static String[] ENCHANTMENTS_HANDBOOK_CONTENTS() {
        return addPage(
                fText(BOLD, "INTRODUCTION") + nextLine
                        + "TOTVW: Wolf Additions introduces several custom enchantments to help enhance your wolf companion's on-attack combat abilities. Depending on the enchantments available in your companion's armor, it can trigger different effects and skills depending on the circumstances."
                        + addSeparator
                        + benedictionOfTheVerdantMountainsInfo()
                        + addSeparator
                        + wolfArmorEnhancementKitInfo()
                        + addSeparator
                        + mightEnchantmentInfo()
                        + addSeparator
                        + bloodlustEnchantmentInfo()
                        + addSeparator
                        + oozingEnchantmentInfo()
                        + addSeparator
                        + ignitionEnchantmentInfo()
                        + addSeparator
                        + liftingEnchantmentInfo()
                        + addSeparator
                        + gnawingEnchantmentInfo()
                        + addSeparator
                        + poisoningEnchantmentInfo()
                        + addSeparator
                        + witheringEnchantmentInfo()
                        + addSeparator
                        + mendingEnchantmentInfo()
                        + addSeparator
                        + bold("OTHER EFFECTS") + nextLine
                        + "• When wolf has a baby wolf nearby and has the following enchantments: Protection, Blast Protection, or Fire Protection, the baby will be granted a strong effect based on the enchantment type."
                        + addSeparator
                        + cText(DARK_AQUA, "#".repeat(64)) + nextLine
                        + cText(DARK_AQUA, "#".repeat(64))
                        + addSeparator
                        + bold("AUTHOR'S NOTE") + nextLine
                        + "Some settings of the mod can be configured. You can edit these by manually accessing the config file \"tales-of-the-verdant-wind.toml\" or through other means like Mod Menu."
        );
    }
}
