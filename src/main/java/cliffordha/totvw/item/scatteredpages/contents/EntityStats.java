package cliffordha.totvw.item.scatteredpages.contents;

import cliffordha.totvw.entity.player.PlayerStats;
import cliffordha.totvw.entity.skill.SkillCooldowns;
import cliffordha.totvw.entity.wolf.WolfEnchants;
import cliffordha.totvw.entity.wolf.WolfStats;
import cliffordha.totvw.item.scatteredpages.ScatteredPageItem;
import cliffordha.totvw.registry.VWEnchantments;
import cliffordha.totvw.registry.VWItems;
import cliffordha.totvw.registry.attachments.Runestone;
import cliffordha.totvw.registry.attachments.VWAttachments;
import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import cliffordha.totvw.util.BasicArmorData;
import cliffordha.totvw.util.VWTextUtil;
import cliffordha.totvw.util.VWUtil;
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
import net.minecraft.world.item.enchantment.Enchantment;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static cliffordha.totvw.item.scatteredpages.ScatteredPageTextColor.*;
import static cliffordha.totvw.item.scatteredpages.ScatteredPageTextStyle.*;
import static cliffordha.totvw.util.VWTextUtil.*;
import static cliffordha.totvw.util.VWTextUtil.nextLine;

public class EntityStats {
    private static final String SKILL_BLESSING = "Blessing of the Verdant Wind";
    private static final String SKILL_READY = cText(GREEN, "READY");
    private static Minecraft mc = Minecraft.getInstance();

    public static String[] wolfStats(Wolf wolf, WolfStats s) {
        if (wolf == null || s == null) return ScatteredPageItem.invalidEntity;
        var e = WolfEnchants.of(wolf);

        int blessingCD = wolf.getAttachedOrElse(WolfAttachment.CD_BLESSING_OF_THE_VERDANT_WIND, 0);
        int blessingCDLimit = VWUtil.cooldownFromBase(wolf.level(), SkillCooldowns.BASE_BLESSING);

        int paralyzeCD = wolf.getAttachedOrElse(WolfAttachment.CD_BLOODLUST_SKILL_PARALYZE, 0);
        int paralyzeCDLimit = VWUtil.cooldownFromBase(wolf.level(), SkillCooldowns.BASE_PARALYZE);

        int ruptureCD = wolf.getAttachedOrElse(WolfAttachment.CD_MIGHT_SKILL_RUPTURE, 0);
        int ruptureCDLimit = VWUtil.cooldownFromBase(wolf.level(), SkillCooldowns.BASE_RUPTURE);

        int ignoreHighDamageCD = wolf.getAttachedOrElse(WolfAttachment.CD_IGNORE_HIGH_DAMAGE, 0);

        int flashFreezeStack = wolf.getAttachedOrElse(WolfAttachment.FLASH_FREEZE_STACK, 0);
        int arcticAffliction = wolf.getAttachedOrElse(VWAttachments.ARCTIC_AFFLICTION, 0);
        int frostBound = wolf.getAttachedOrElse(VWAttachments.FROST_BOUND_TIMER, 0);

        List<String> tetheredEntities = wolf.getAttachedOrElse(WolfAttachment.TETHERED_ENTITY_TYPES, List.of());
        int trustedPlayers = s.trustedPlayers().size();
        int aggressors = s.aggressors().size();

        boolean respawnDimensionValid = !s.respawnDimension().equals(String.valueOf(VWDimensions.NONE.identifier()));
        boolean hasReturnPoint = !s.returnPoint().equals("None");

        boolean hasRunestone = Runestone.hasRunestone(wolf);

        List<String> enchants = new ArrayList<>();
        BasicArmorData armor = wolf.getAttachedOrElse(WolfAttachment.ARMOR_DATA, BasicArmorData.NONE);

        String durability;
        if (!armor.isNull()) {
            durability = armor.getName() + ": " + iText(armor.getMaxDurability() - armor.getDurability(), armor.getMaxDurability(), true) + " / " + armor.getMaxDurability();
            for (Object2IntMap.Entry<Holder<Enchantment>> entry : armor.getItemStack().getEnchantments().entrySet()) {
                enchants.add(
                        Enchantment.getFullname(entry.getKey(), entry.getIntValue()).getString()
                );
            }
        } else {
            durability = "Armor: N/A";
        }


        return addPage(
                fText(BOLD, "## MAIN STATS") + nextLine
                        + "Health: " + iText(wolf.getHealth(), wolf.getMaxHealth(), true) + " / " + wolf.getMaxHealth() + nextLine
                        + durability + nextLine
                        + tText(mc != null && mc.hasShiftDown(),
                        "UUID: " + s.UUID() + nextLine
                                + "Soul ID: " + s.soulID() + nextLine
                                + "Family ID: " + s.familyID() + nextLine
                                + "Owner: " + s.owner() + nextLine
                                + "Owner UUID: " + s.ownerUUID() + nextLine
                                + "Shared UUID: " + s.sharedUUID(),
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
                        SKILL_BLESSING + " CD: " + iText(blessingCD, blessingCDLimit),
                        SKILL_BLESSING + ": " + SKILL_READY,
                        true)
                        + tText(e.hasBenediction(), ignoreHighDamageCD > 0,
                        "Ignore High Damage CD: " + iText(ignoreHighDamageCD, 15),
                        "Ignore High Damage: " + SKILL_READY,
                        true)
                        + tText(e.hasMight(), ruptureCD > 0,
                        "Rupture CD: " + iText(ruptureCD, ruptureCDLimit),
                        "Rupture: " + SKILL_READY,
                        true)
                        + tText(e.hasBloodLust(), paralyzeCD > 0,
                        "Paralyze CD: " + iText(paralyzeCD, paralyzeCDLimit),
                        "Paralyze: " + SKILL_READY,
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
                        + tText(trustedPlayers > 0, "Trusted Players: " + nextLine + getPairs(s.trustedPlayers()) + nextParagraph, true)
                        + tText(aggressors > 0, "Aggressors: " + nextLine + getPairs(s.aggressors()) + nextParagraph, true)
                        + tText(!enchants.isEmpty(), "Active Enchantments: " + nextLine + iterateDecoratedList(enchants))

        );
    }
    private static List<String> getActiveEffects(Wolf wolf) {
        Collection<MobEffectInstance> effects = wolf.getAttachedOrElse(VWAttachments.MOB_EFFECTS, List.of());
        List<String> constructed = new ArrayList<>();

        if (!effects.isEmpty()) {
            for (MobEffectInstance effect : effects) {
                String amp = effect.getAmplifier() > 0 ? " " + effect.getAmplifier() : "";
                int duration = effect.getDuration() > 20 ? effect.getDuration() / 20 : 0;
                String constructor = effect.getEffect().value().getDisplayName().getString() + amp + " [" + duration + "]";

                constructed.add(constructor);
            }
        }
        return constructed;
    }
    public static String[] playerStats(Player player) {
        var stat = PlayerStats.valueOf(player);

        int acquiredGenesis = stat.genesisRunestoneAcquisitionCount();

        int blessingCD = player.getAttachedOrElse(PlayerAttachment.CD_BLESSING_OF_THE_VERDANT_WIND, 0);
        int blessingCDLimit = VWUtil.cooldownFromBase(player.level(), SkillCooldowns.BASE_BLESSING);
        int havocCD = player.getAttachedOrElse(PlayerAttachment.CD_HAVOC, 0);
        List<Wolf> wolves = player.level().getEntitiesOfClass(
                Wolf.class,
                VWUtil.scanArea(player, 16),
                test -> test.getOwner() == player && Runestone.hasHavoc(test)
        );
        boolean havocReady = !wolves.isEmpty();
        int verdantBloom = player.getAttachedOrElse(VWAttachments.VERDANT_BLOOM_STACK, 0);

        boolean hasAtrocity = stat.wolfAtrocityCount() + stat.villagerAtrocityCount() > 0;

        List<String> receivedHandbooks = new ArrayList<>();
        if (stat.receivedEnchantmentsHandbook()) receivedHandbooks.add("Enchantments");
        if (stat.receivedItemsHandbook()) receivedHandbooks.add("Items");
        if (stat.receivedFeaturesHandbook()) receivedHandbooks.add("Features");
        if (stat.receivedEffectsHandbook()) receivedHandbooks.add("Effects");

        return addPage(
                fText(BOLD, "## MAIN STATS") + nextLine
                        + "Health: " + iText(player.getHealth(), player.getMaxHealth(), true) + " / " + player.getMaxHealth() + nextLine
                        + "Hunger: " + iText(player.getFoodData().getFoodLevel(), 20, true) + nextLine
                        + tText(mc != null && mc.hasShiftDown(),
                        "UUID: " + stat.UUID() + nextLine
                                + "Shared UUID: " + stat.sharedUUID(), true)
                        + tText(stat.wolfSouls() > 0, "Wolf Souls: " + stat.wolfSouls() + nextLine)
                        + nextParagraph

                        + fText(BOLD, "## EQUIPMENTS") + nextLine
                        + iterateDecoratedList(showAllArmors(player))
                        + nextParagraph

                        + fText(BOLD, "## ADDITIONAL STATS") + nextLine
                        + tText(hasAtrocity, "Atrocity Counter: "
                                + tText(stat.wolfAtrocityCount() > 0, "Wolf [" + stat.wolfAtrocityCount() + "] ")
                                + tText(stat.villagerAtrocityCount() > 0, "Villager [" + stat.villagerAtrocityCount() + "]"),
                        true)

                        + tText(acquiredGenesis > 0, "Genesis Runestone Acquisition Count: ", true)
                        + tText(verdantBloom > 0, "Verdant Bloom Stack: " + verdantBloom, true)
                        + tText(havocReady, "Havoc Type: " + stat.havocType().getName(), true)
                        + tText(havocReady, "Havoc Usage Count: " + stat.havocUsageCount(), true)

                        + tText(VWEnchantments.getBenediction(player) || havocReady, nextParagraph
                        + fText(BOLD, "## SKILLS & EFFECTS"), true)

                        + tText(VWEnchantments.getBenediction(player), blessingCD > 0,
                        SKILL_BLESSING + " CD: " + iText(blessingCD, blessingCDLimit),
                        SKILL_BLESSING + ": " + SKILL_READY,
                        true)
                        + tText(havocReady, havocCD > 0,
                        "Havoc CD: " + iText(havocCD, 75),
                        "Havoc: " + SKILL_READY,
                        true)

                        + tText(!stat.trustedPlayers().isEmpty() || !receivedHandbooks.isEmpty(),
                        fText(BOLD, "## OTHER STATS"), true)

                        + tText(!stat.trustedPlayers().isEmpty(), "Trusted Players: " + nextLine + getPairs(stat.trustedPlayers()) + nextParagraph)
                        + tText(!receivedHandbooks.isEmpty(), "Received handbooks: " + VWTextUtil.iterateList(receivedHandbooks, false))
        );
    }
    private static String getPairs(List<Pair<String, UUID>> list) {
        return list.stream().sorted().map(pair -> pair.getA() + " " + pair.getB()).reduce((a, b) -> a + nextLine + b).orElse("");
    }
    private static List<String> showAllArmors(Player player) {
        List<BasicArmorData> armors = player.getAttachedOrElse(PlayerAttachment.EQUIPMENTS, new ArrayList<>());
        if (armors.isEmpty()) return List.of("N/A");
        List<String> constructed = new ArrayList<>();

        for (BasicArmorData data : armors) {
            String name = data.getName();
            constructed.add(name + ": " + iText(data.getMaxDurability() - data.getDurability(), data.getMaxDurability(), true) + " / " + data.getMaxDurability());
        }

        return constructed;
    }
    private static String showValueFor(Player player, EquipmentSlot slot) {
        List<BasicArmorData> armors = player.getAttachedOrElse(PlayerAttachment.EQUIPMENTS, new ArrayList<>());
        if (armors.isEmpty()) return "N/A";

        for (BasicArmorData data : armors) {
            if (data.getSlot() == slot && !data.isSame(VWItems.EMPTY)) {
                String name = data.getName() + getArmorValue(true, data.getDurability(), data.getMaxDurability());
                return name + iText(data.getMaxDurability() - data.getDurability(), data.getMaxDurability(), true) + nextLine;
            }
        }
        return "N/A";
    }
    private static String getArmorValue(boolean a, int value, int max) {
        if (!a || max <= 0) return "N/A" + nextLine;
        return iText(value, max, true) + nextLine;
    }
    private static String getTetherEntities(List<String> list) {
        String add = list.size() > 1 ? " and " : "";
        StringBuilder types = new StringBuilder();
        for (String s : list) {
            Identifier parse = Identifier.tryParse(s);
            if (parse != null) {
                String name = BuiltInRegistries.ENTITY_TYPE.getValue(parse).getDescription().getString();
                types.append(name).append(add);
            } else {
                types.append(s).append(add);
            }
        }
        return types.toString();
    }
}
