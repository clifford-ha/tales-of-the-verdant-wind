package cliffordha.totvw.item.pages;

import cliffordha.totvw.entity.status.PlayerStats;
import cliffordha.totvw.entity.skill.SkillCD;
import cliffordha.totvw.entity.wolf.WolfEnchantmentRecord;
import cliffordha.totvw.entity.status.WolfStats;
import cliffordha.totvw.registry.VWEnchantments;
import cliffordha.totvw.registry.VWItems;
import cliffordha.totvw.registry.attachments.*;
import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import cliffordha.totvw.util.ExtUtil;
import cliffordha.totvw.worldgen.dimension.VWDimensions;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static cliffordha.totvw.item.pages.FTextColor.*;
import static cliffordha.totvw.item.pages.FTextStyle.*;
import static cliffordha.totvw.util.TextUtil.*;
import static cliffordha.totvw.util.TextUtil.nextLine;

public class EntityStats {
    private static final String SKILL_BLESSING = "Blessing of the Verdant Wind";
    private static final String SKILL_READY = cText(GREEN, "READY");
    private static final String SKILL_READY_WITHERED = cText(DARK_PURPLE, "READY");
    private static boolean shiftKeyDown() {
        return Minecraft.getInstance().hasShiftDown();
    }
    public static String witherwolfStats(Wolf wolf, WolfStats s) {
        if (wolf == null || s == null) return ScatteredPageItem.invalidEntity;
        var e = WolfEnchantmentRecord.of(wolf);

        int paralyzeCD = wolf.getAttachedOrElse(WolfAttachment.CD_BLOODLUST_SKILL_PARALYZE, 0);
        int paralyzeCDLimit = ExtUtil.cooldownFromBase(wolf.level(), SkillCD.BASE_PARALYZE);

        int ignoreHighDamageCD = wolf.getAttachedOrElse(WolfAttachment.CD_IGNORE_HIGH_DAMAGE, 0);

        int flashFreezeStack = wolf.getAttachedOrElse(WolfAttachment.FLASH_FREEZE_STACK, 0);
        List<String> tetheredEntities = wolf.getAttachedOrElse(WolfAttachment.TETHER_DATA, TetherData.EMPTY).tetheredEntities();

        boolean hasRunestone = Runestone.hasRunestone(wolf);

        List<String> enchants = new ArrayList<>();
        ArmorData armorS2C = wolf.getAttachedOrElse(WolfAttachment.ARMOR_DATA, ArmorData.NONE);
        ItemStack armor = wolf.isWearingBodyArmor() ? wolf.getBodyArmorItem() : null;
        double addedATK = armorS2C.getAddedATK();

        if (armor != null) {
            for (Object2IntMap.Entry<Holder<Enchantment>> entry : wolf.getBodyArmorItem().getEnchantments().entrySet()) {
                enchants.add(
                        Enchantment.getFullname(entry.getKey(), entry.getIntValue()).getString()
                );
            }
        }


        return addPage(
                "The wolf looks at you, confused on what you're trying to do... Confused as it may be, it remains loyal to the duty they are called upon even when their end draws near."
                        + nextParagraph

                        + fText(BOLD, "## ???") + nextLine
                        + "Remaining Time: " + cText(DARK_PURPLE, "" + WolfFragmentData.getLifespan(wolf)) + nextLine
                        + "Remaining Hits: " + cText(DARK_PURPLE, "" + WolfFragmentData.getHits(wolf)) + nextLine
                        + nextParagraph

                        + fText(BOLD, "## STATS") + nextLine
                        + "Attack Damage: " + armorS2C.baseATK() + (addedATK > 0 ? " + " + cText(DARK_PURPLE, "" + addedATK) : "") + nextLine
                        + "Attack Cycle: " + s.attackCycle() + nextLine

                        + tText(e.hasBenediction() || e.hasMight() || e.hasBloodLust(), nextParagraph
                        + fText(BOLD, "## SKILLS & EFFECTS"), true)

                        + tText(e.hasBenediction(), ignoreHighDamageCD > 0,
                        "Ignore High Damage CD: " + cooldownText(ignoreHighDamageCD, 15),
                        "Ignore High Damage: " + SKILL_READY_WITHERED,
                        true)
                        + tText(e.hasMight(), "Rupture: " + req("Might Level 3+"), true)
                        + tText(e.hasBloodLust(), paralyzeCD > 0,
                        "Paralyze CD: " + cooldownText(paralyzeCD, paralyzeCDLimit),
                        "Paralyze: " + SKILL_READY_WITHERED,
                        true)

                        + tText(e.hasArcticWinds() || s.trySavePoints() > 0 || hasRunestone, nextParagraph
                        + fText(BOLD, "## OTHER STATS"), true)

                        + tText(e.hasArcticWinds(), "Flash Freeze Stacks: " + flashFreezeStack, true)

                        + tText(hasRunestone, "Runestone Type: " + s.runestoneType().getName().replace(" Runestone", ""), true)
                        + tText(Runestone.hasTether(wolf), "Tethered Entities: " + (!tetheredEntities.isEmpty() ? getTetherEntities(tetheredEntities) : "None"), true)
                        + nextParagraph

                        + tText(!getActiveEffects(wolf).isEmpty(), "Active Effects: " + nextLine + iterateDecoratedList(getActiveEffects(wolf)) + nextParagraph, true)
                        + tText(!enchants.isEmpty(), "Active Enchantments: " + nextLine + iterateDecoratedList(enchants))

        );
    }
    public static String wolfStats(Wolf wolf, WolfStats s) {
        if (wolf == null || s == null) return ScatteredPageItem.invalidEntity;
        var e = WolfEnchantmentRecord.of(wolf);

        int blessingCD = wolf.getAttachedOrElse(WolfAttachment.CD_BLESSING_OF_THE_VERDANT_WIND, 0);
        int blessingCDLimit = ExtUtil.cooldownFromBase(wolf.level(), SkillCD.BASE_BLESSING);

        int paralyzeCD = wolf.getAttachedOrElse(WolfAttachment.CD_BLOODLUST_SKILL_PARALYZE, 0);
        int paralyzeCDLimit = ExtUtil.cooldownFromBase(wolf.level(), SkillCD.BASE_PARALYZE);

        int ruptureCD = wolf.getAttachedOrElse(WolfAttachment.CD_MIGHT_SKILL_RUPTURE, 0);
        int ruptureCDLimit = ExtUtil.cooldownFromBase(wolf.level(), SkillCD.BASE_RUPTURE);

        int witherwolfSummonCD = wolf.getAttachedOrElse(WolfAttachment.CD_WOLF_FRAGMENT_SUMMON, 0);
        int witherwolfCDLimit = SkillCD.BASE_FRAGMENTED_WOLF_SUMMON;

        int ignoreHighDamageCD = wolf.getAttachedOrElse(WolfAttachment.CD_IGNORE_HIGH_DAMAGE, 0);

        int flashFreezeStack = wolf.getAttachedOrElse(WolfAttachment.FLASH_FREEZE_STACK, 0);
        int arcticAffliction = wolf.getAttachedOrElse(VWAttachment.ARCTIC_AFFLICTION, 0);
        int frostBound = wolf.getAttachedOrElse(VWAttachment.FROST_BOUND_TIMER, 0);

        List<String> tetheredEntities = wolf.getAttachedOrElse(WolfAttachment.TETHER_DATA, TetherData.EMPTY).tetheredEntities();
        int trustedPlayers = s.trustedPlayers().size();
        int aggressors = s.aggressors().size();

        boolean respawnDimensionValid = !s.respawnDimension().equals(String.valueOf(VWDimensions.NONE.identifier()));
        boolean hasReturnPoint = !s.returnPoint().equals("None");

        boolean hasRunestone = Runestone.hasRunestone(wolf);

        List<String> enchants = new ArrayList<>();
        ArmorData armorS2C = wolf.getAttachedOrElse(WolfAttachment.ARMOR_DATA, ArmorData.NONE);
        ItemStack armor = wolf.isWearingBodyArmor() ? wolf.getBodyArmorItem() : null;
        double addedATK = armorS2C.getAddedATK();

        String durability;
        if (armor != null) {
            for (Object2IntMap.Entry<Holder<Enchantment>> entry : wolf.getBodyArmorItem().getEnchantments().entrySet()) {
                enchants.add(
                        Enchantment.getFullname(entry.getKey(), entry.getIntValue()).getString()
                );
            }
        }
        if (!armorS2C.isNull()) {
            durability = armorS2C.getName() + ": " + percentText(armorS2C.maxDurability() - armorS2C.durability(), armorS2C.maxDurability(), true);
        } else {
            durability = "Armor: N/A";
        }


        return addPage(
                fText(BOLD, "## MAIN STATS") + nextLine
                        + "Health: " + percentText(wolf.getHealth(), wolf.getMaxHealth(), true) + nextLine
                        + "Attack Damage: " + armorS2C.baseATK() + (addedATK > 0 ? " + " + cText(AQUA, "" + addedATK) : "") + nextLine
                        + durability + nextLine
                        + tText(shiftKeyDown(),
                        "UUID: " + s.UUID() + nextLine
                                + "Soul ID: " + s.soulID() + nextLine
                                + "Family ID: " + s.familyID() + nextLine
                                + "Owner: " + s.owner() + nextLine
                                + "Owner UUID: " + s.ownerUUID(),
                        true)

                        + nextParagraph

                        + fText(BOLD, "## ADDITIONAL STATS") + nextLine
                        + "Is Verdant: " + s.isVerdant() + nextLine
                        + "Benediction Stack: " + s.benedictionStack() + nextLine
                        + "Attack Cycle: " + s.attackCycle() + nextLine
                        + tText(hasReturnPoint, "Return Point: " + s.returnPoint(), true)
                        + tText(hasReturnPoint, "Respawn Dimension: " + tText(respawnDimensionValid, s.respawnDimension(), cText(RED, "Not set!")), true)

                        + tText(e.hasBenediction() || e.hasMight() || e.hasBloodLust(), nextParagraph
                        + fText(BOLD, "## SKILLS & EFFECTS"), true)

                        + tText(e.hasBenediction(), blessingCD > 0,
                        SKILL_BLESSING + " CD: " + cooldownText(blessingCD, blessingCDLimit),
                        SKILL_BLESSING + ": " + SKILL_READY,
                        true)
                        + tText(e.hasBenediction(), ignoreHighDamageCD > 0,
                        "Ignore High Damage CD: " + cooldownText(ignoreHighDamageCD, 15),
                        "Ignore High Damage: " + SKILL_READY,
                        true)
                        + tText(e.hasMight(), ruptureCD > 0,
                        "Rupture CD: " + cooldownText(ruptureCD, ruptureCDLimit),
                        "Rupture: " + (e.getMight() >= 3 ? SKILL_READY : req("Might Level 3+")),
                        true)
                        + tText(e.hasBloodLust(), paralyzeCD > 0,
                        "Paralyze CD: " + cooldownText(paralyzeCD, paralyzeCDLimit),
                        "Paralyze: " + SKILL_READY,
                        true)
                        + tText(e.hasWithering(), witherwolfSummonCD > 0,
                        "Witherwolf Summon CD: " + cooldownText(witherwolfSummonCD, witherwolfCDLimit),
                        "Witherwolf [Summon]: " + SKILL_READY,
                        true)

                        + tText(e.hasArcticWinds() || s.trySavePoints() > 0 || hasRunestone, nextParagraph
                        + fText(BOLD, "## OTHER STATS"), true)

                        + tText(e.hasArcticWinds(), "Flash Freeze Stacks: " + flashFreezeStack, true)
                        + tText(s.hasArcticAffliction(), "Arctic Affliction Stacks: " + cText(RED, "" + arcticAffliction), true)
                        + tText(s.hasArcticAffliction(), "Frost Bound: " + cText(RED, "" + frostBound), true)

                        + tText(s.trySavePoints() > 0, "Try-Save Points: " + s.trySavePoints(), true)
                        + tText(hasRunestone, "Runestone Type: " + s.runestoneType().getName().replace(" Runestone", ""), true)
                        + tText(Runestone.hasTether(wolf), "Tethered Entities: " + (!tetheredEntities.isEmpty() ? getTetherEntities(tetheredEntities) : "None"), true)
                        + nextParagraph

                        + tText(!getActiveEffects(wolf).isEmpty(), "Active Effects: " + nextLine + iterateDecoratedList(getActiveEffects(wolf)) + nextParagraph, true)
                        + tText(trustedPlayers > 0, "Trusted Players: " + nextLine + iterateDecoratedList(getPairs(s.trustedPlayers())) + nextParagraph, true)
                        + tText(aggressors > 0, "Aggressors: " + nextLine + iterateDecoratedList(getPairs(s.aggressors())) + nextParagraph, true)
                        + tText(!enchants.isEmpty(), "Active Enchantments: " + nextLine + iterateDecoratedList(enchants))

        );
    }
    private static String req(int value) {
        return cText(GRAY, "Required Level: " + value + "+");
    }
    private static String req(String value) {
        return cText(GRAY, "Requires " + value);
    }
    private static List<String> getActiveEffects(Wolf wolf) {
        Collection<MobEffectInstance> effects = wolf.getAttachedOrElse(VWAttachment.MOB_EFFECTS, List.of());
        List<String> constructed = new ArrayList<>();

        if (!effects.isEmpty()) {
            for (MobEffectInstance effect : effects) {
                int getAMP = effect.getAmplifier() + 1;
                String amp = getAMP > 0 ? " " + getAMP : "";
                String duration = effect.getDuration() > 20 ? "" + (effect.getDuration() / 20) : "0";
                duration = effect.getDuration() <= -1 ? "Infinite" : duration;
                String constructor = effect.getEffect().value().getDisplayName().getString() + amp + " [" + duration + "]";

                constructed.add(constructor);
            }
        }
        return constructed;
    }
    public static String playerStats(Player player) {
        var stat = PlayerStats.valueOf(player);
        int acquiredGenesis = stat.genesisRunestoneAcquisitionCount();

        int blessingCD = player.getAttachedOrElse(PlayerAttachment.CD_BLESSING_OF_THE_VERDANT_WIND, 0);
        int blessingCDLimit = ExtUtil.cooldownFromBase(player.level(), SkillCD.BASE_BLESSING);
        int havocCD = player.getAttachedOrElse(PlayerAttachment.CD_HAVOC, 0);
        List<Wolf> wolves = player.level().getEntitiesOfClass(
                Wolf.class,
                ExtUtil.scanArea(player, 16),
                test -> test.getOwner() == player && Runestone.hasHavoc(test)
        );
        boolean havocReady = !wolves.isEmpty();
        int verdantBloom = player.getAttachedOrElse(VWAttachment.VERDANT_BLOOM_STACK, 0);

        boolean hasAtrocity = stat.wolfAtrocityCount() + stat.villagerAtrocityCount() > 0;

        List<String> receivedHandbooks = new ArrayList<>();
        if (stat.receivedEnchantmentsHandbook()) receivedHandbooks.add("Enchantments");
        if (stat.receivedItemsHandbook()) receivedHandbooks.add("Items");
        if (stat.receivedFeaturesHandbook()) receivedHandbooks.add("Features");
        if (stat.receivedEffectsHandbook()) receivedHandbooks.add("Effects");

        ArmorData data = PlayerAttachment.getArmorData(player);

        return addPage(
                fText(BOLD, "## MAIN STATS") + nextLine
                        + "Health: " + percentText(player.getHealth(), player.getMaxHealth(), true) + nextLine
                        + "Hunger: " + percentText(player.getFoodData().getFoodLevel(), 20, true) + nextLine
                        + "Attack Damage: " + data.totalATK() + nextLine
                        + tText(shiftKeyDown(),
                        "UUID: " + stat.UUID(), true)
                        + tText(stat.wolfSouls() > 0, "Wolf Souls: " + stat.wolfSouls() + nextLine)
                        + nextParagraph

                        + fText(BOLD, "## EQUIPMENTS") + nextLine
                        + iterateDecoratedList(showAllArmors(player), false)
                        + nextParagraph

                        + fText(BOLD, "## ADDITIONAL STATS") + nextLine
                        + tText(hasAtrocity, "Atrocity Counter: "
                                + tText(stat.wolfAtrocityCount() > 0, "Wolf [" + stat.wolfAtrocityCount() + "] ")
                                + tText(stat.villagerAtrocityCount() > 0, "Villager [" + stat.villagerAtrocityCount() + "]"),
                        true)

                        + tText(acquiredGenesis > 0, "Genesis Runestone Acquisition Count: ", true)
                        + tText(verdantBloom > 0, "Verdant Bloom Stack: " + verdantBloom, true)
                        + tText(havocReady, "Havoc Type: " + stat.havocData().type().getName(), true)
                        + tText(havocReady, "Havoc Usage Count: " + stat.havocData().usageCount(), true)

                        + tText(VWEnchantments.getBenediction(player) || havocReady, nextParagraph

                        + fText(BOLD, "## SKILLS & EFFECTS"), true)

                        + tText(VWEnchantments.getBenediction(player), blessingCD > 0,
                        SKILL_BLESSING + " CD: " + cooldownText(blessingCD, blessingCDLimit),
                        SKILL_BLESSING + ": " + SKILL_READY,
                        true)
                        + tText(havocCD > 0 || havocReady, havocCD > 0,
                        "Havoc CD: " + cooldownText(havocCD, 75),
                        "Havoc: " + SKILL_READY,
                        true)

                        + tText(!stat.trustedPlayers().isEmpty() || !receivedHandbooks.isEmpty(), nextParagraph)

                        + tText(!stat.trustedPlayers().isEmpty() || !receivedHandbooks.isEmpty(),
                        fText(BOLD, "## OTHER STATS"), true)

                        + tText(!stat.trustedPlayers().isEmpty(), "Trusted Players: " + nextLine + iterateDecoratedList(getPairs(stat.trustedPlayers())) + nextParagraph)
                        + tText(!receivedHandbooks.isEmpty(), "Received handbooks: " + iterateList(receivedHandbooks, false))
        );
    }

    private static List<String> getPairs(List<NameHolder> list) {
        List<String> constructed = new ArrayList<>();
        if (list.isEmpty()) return constructed;

        if (shiftKeyDown()) {
            for (NameHolder data : list) {
                constructed.add(String.valueOf(data.uuid()));
            }
        } else {
            for (NameHolder data : list) {
                constructed.add(data.name());
            }
        }
        return constructed;
    }
    private static List<String> showAllArmors(Player player) {
        List<ArmorData> armors = PlayerAttachment.getArmorList(player);
        if (armors.isEmpty()) return List.of("N/A");
        List<String> constructed = new ArrayList<>();

        for (ArmorData data : armors) {
            String name = data.getName();
            constructed.add(name + " " + percentText(data.maxDurability() - data.durability(), data.maxDurability(), true));
        }

        return constructed;
    }
    private static String showValueFor(Player player, EquipmentSlot slot) {
        List<ArmorData> armors = player.getAttachedOrElse(PlayerAttachment.EQUIPMENTS, new ArrayList<>());
        if (armors.isEmpty()) return "N/A";

        for (ArmorData data : armors) {
            if (data.slot() == slot && !data.isSame(VWItems.EMPTY)) {
                String name = data.getName();
                return name + percentText(data.maxDurability() - data.durability(), data.maxDurability(), true) + nextLine;
            }
        }
        return "N/A";
    }
    private static String getArmorValue(boolean a, int value, int max) {
        if (!a || max <= 0) return "N/A" + nextLine;
        return percentText(value, max, true) + nextLine;
    }
    private static String getTetherEntities(List<String> list) {
        if (list.isEmpty()) return "None";
        if (list.size() > 1) {
            return parseName(list.getFirst()) + " and " + parseName(list.getLast());
        } else {
            return parseName(list.getFirst());
        }
    }
    private static String parseName(String name) {
        Identifier parse = Identifier.tryParse(name);
        if (parse != null) {
            return BuiltInRegistries.ENTITY_TYPE.getValue(parse).getDescription().getString();
        } else {
            return name;
        }
    }
}
