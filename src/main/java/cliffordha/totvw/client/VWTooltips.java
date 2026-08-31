package cliffordha.totvw.client;

import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.registry.*;
import cliffordha.totvw.tag.VWItemTags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static cliffordha.totvw.item.scatteredpages.ScatteredPageTextColor.*;
import static cliffordha.totvw.item.scatteredpages.ScatteredPageTextStyle.*;
import static cliffordha.totvw.util.VWUtil.TextUtil.*;
import static cliffordha.totvw.util.VWUtil.entityEnchantmentLVL;

@Environment(EnvType.CLIENT)
public class VWTooltips {
    public static void register() {
        ItemTooltipCallback.EVENT.register(VWTooltips::onTooltip);
    }
    private static final Minecraft mc = Minecraft.getInstance();
    private static void onTooltip(ItemStack stack, Item.TooltipContext context, TooltipFlag flag, List<Component> lines) {
        if (mc.level == null) return;

        List<Component> injected = new ArrayList<>();

        handleBenedictionToolLines(stack, injected);
        handleBenedictionItemLines(stack, injected);
        handleLoreLines(stack, injected);

        if (!injected.isEmpty()) {
            int insertAt = Math.min(1, lines.size());
            lines.addAll(insertAt, injected);
        }
    }

    private static void handleBenedictionToolLines(ItemStack stack, List<Component> out) {
        if (stack.tags().noneMatch(Predicate.isEqual(VWItemTags.BENEDICTION_ENCHANTMENT_USE_QUALIFIED_TOOLS))) return;
        if (!hasBenediction()) return;

        String desc = resolveToolEffect(stack);
        if (desc != null) addVWItem(out, desc);
    }

    private static String resolveToolEffect(ItemStack stack) {
        if (stack.tags().anyMatch(t -> t.location().getPath().equals("swords")))   return "VW Strength";
        if (stack.tags().anyMatch(t -> t.location().getPath().equals("axes")))     return "VW Strength II";
        if (stack.tags().anyMatch(t -> t.location().getPath().equals("pickaxes"))) return "VW Haste";
        if (stack.tags().anyMatch(t -> t.location().getPath().equals("hoes")))     return "VW Absorption";
        if (stack.tags().anyMatch(t -> t.location().getPath().equals("shovels")))  return "VW Speed";
        return null;
    }


    private static void handleBenedictionItemLines(ItemStack stack, List<Component> out) {
        if (stack.tags().noneMatch(Predicate.isEqual(VWItemTags.BENEDICTION_ENCHANTMENT_USE_QUALIFIED_ITEMS))) return;
        if (!hasBenediction()) return;

        if (stack.is(VWItems.VERIXIUM_POWDER)) {
            addVWItem(out, "VW Slow Falling");
        } else {
            addVWItem(out, "VW Night Vision");
        }
    }
    private static void handleLoreLines(ItemStack stack, List<Component> out) {
        if (mc.level == null) return;
        Player player = mc.player;
        if (player == null) return;

        boolean ACTIVE_BENEDICTION = entityEnchantmentLVL(player, EquipmentSlot.CHEST, VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS) > 0;

        // this translation block looks awful ngl
        String unset = "Not yet set";

        String itemVerixiumArmor = "A light yet durable armor";
        String itemVerixium = "A subtle hint of wind emanates from this item...";

        String LORE_soulRunestoneFragment1;
        String LORE_verixiumChunk;
        String LORE_condensedVerixium0;
        String LORE_condensedVerixium1;
        String LORE_verixiumShard;
        String LORE_verixiumPowder;
        String LORE_verixiumIngot;


        if (!VWConfig.get().CLIENT_TRANSLATE_LANGUAGE) {
            LORE_soulRunestoneFragment1 = "There will always be those who try to defy the absolute limit of this world. Though sacrifices were uncomfortably high, the ingenuity always take precedence to overcome the obstacles that stand in the way of the living.";
            LORE_verixiumChunk = "\"For the land that they call 'home' and for the people they protect.\"";
            LORE_condensedVerixium0 = "\"For the land that they call 'home' and for the people they protect.";
            LORE_condensedVerixium1 = "O God of the Verdant Winds...\"";
            LORE_verixiumShard = "\"Bless us, so we may reap a bountiful harvest.\"";
            LORE_verixiumPowder = "\"Guide us, so we may never be swept by the floods of fiery lies.\"";
            LORE_verixiumIngot = "\"Protect us, so we may defend your land and people from the perils of the creatures from beyond.\"";
        } else {
            LORE_soulRunestoneFragment1 = unset;
            LORE_verixiumChunk = unset;
            LORE_condensedVerixium0 = unset;
            LORE_condensedVerixium1 = unset;
            LORE_verixiumShard = unset;
            LORE_verixiumPowder = unset;
            LORE_verixiumIngot = unset;
        }

        if (stack.is(VWItems.SOUL_RUNESTONE_PLATE)) {
            int ACTIVE_SOULS = player.getAttachedOrElse(VWAttachments.Player.PLAYER_WOLF_SOULS_COUNTER, 0);
            if (ACTIVE_SOULS < 1) return;
            String wolf = ACTIVE_SOULS < 2 ? "A " + fText(BOLD, cText(AQUA, "wolf")) + " is" : "The " + fText(BOLD, cText(AQUA, ACTIVE_SOULS + "")) + " wolves are";
            addText(out, wolf + " sleeping...");
        }
        if (stack.is(VWItems.SOUL_RUNESTONE_FRAGMENT_1)) addExpandingText(out, LORE_soulRunestoneFragment1);

        if (stack.is(VWItems.VERIXIUM_CHUNK)) {
            addExpandingText(out, LORE_verixiumChunk);

        } else if (stack.is(VWItems.CONDENSED_VERIXIUM)) {
            addExpandingText(out, LORE_condensedVerixium0, LORE_condensedVerixium1);

        } else if (stack.is(VWItems.VERIXIUM_SHARD)) {
            addExpandingText(out, LORE_verixiumShard);

        } else if (stack.is(VWItems.VERIXIUM_POWDER)) {
            addExpandingText(out, LORE_verixiumPowder);

        } else if (stack.is(VWItems.VERIXIUM_INGOT)) {
            addExpandingText(out, LORE_verixiumIngot);

        } else if (stack.is(VWItems.VERIXIUM_PAPER) || stack.is(VWItems.VERIXIUM_ARMOR_UPGRADE_TEMPLATE)) {
            addMutedItalic(out, itemVerixium);

        } else if (stack.is(VWItems.VERIXIUM_HELMET) || stack.is(VWItems.VERIXIUM_LEGGINGS)
                || stack.is(VWItems.VERIXIUM_BOOTS) || stack.is(VWItems.VERIXIUM_CHESTPLATE)) {
            addMutedItalic(out, itemVerixiumArmor);
        } else if (stack.is(VWItems.VERIXIUM_WOLF_ARMOR)) {
            addMutedItalic(out, itemVerixiumArmor);
            addWolfArmorBenedictionLore(out);
        }

        if (ACTIVE_BENEDICTION) {
            if (stack.is(ItemTags.CHEST_ARMOR)) {
                if (stack.is(ItemTags.REPAIRS_WOLF_ARMOR) || stack.is(VWItemTags.WOLF_ARMOR_ENCHANTABLE)) return;
                addTextChestplate(out, "Grants effect when an item with blessing mark [VW] is used");
            }
            if (stack.is(VWBlocks.LODESTONE_WIND_CORE.asItem())) addText(out, "The core seem to respond with your armor...");
        }
    }

    private static void addWolfArmorBenedictionLore(List<Component> out) {
        String loreKey;
        if (!hasBenediction()) {
            loreKey = "The breezy mountains once favored the call of a certain howl...";
        } else {
            loreKey = "The breezy mountains once favored the call of a certain howl. Although subtle, he too does his part to protect the verdant mountains... a place he calls home.";
        }
        addExpandingText(out,"Armor Engraving:", loreKey);
    }

    private static boolean hasBenediction() {
        if (mc.player == null || mc.level == null) return false;
        return entityEnchantmentLVL(mc.player, EquipmentSlot.CHEST, VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS) > 0;
    }

    private static void addVWItem(List<Component> out, String... text) {
        if (mc.hasShiftDown()) {
            out.add(Component.literal(Arrays.toString(text)).withColor(VWColors.VERDANT_WIND));
        } else {
            out.add(Component.literal("[VW] Active"));
        }
    }

    private static void addMutedItalic(List<Component> out, String text) {
        if (!mc.hasShiftDown()) {
            out.add(Component.literal(text)
                    .withColor(VWColors.GRAY)
                    .withStyle(ChatFormatting.ITALIC));
        }
    }

    private static void addTextChestplate(List<Component> out, String... keys) {
        if (mc.hasShiftDown()) {
            out.add(Component.literal("Verdant Wind's Blessing").withColor(VWColors.VERDANT_WIND));
            for (String key : keys) {
                List<FormattedText> wrapped = mc.font.getSplitter()
                        .splitLines(Component.translatable(key), 200, Style.EMPTY);
                for (FormattedText line : wrapped) {
                    out.add(Component.literal(line.getString())
                            .withColor(VWColors.VERDANT_WIND_MUTED)
                            .withStyle(ChatFormatting.ITALIC));
                }
            }
            out.add(Component.literal(""));
        } else {
            out.add(Component.literal("[VW] Blessing Active"));
        }
    }
    private static void addText(List<Component> out, String... keys) {
        for (String key : keys) {
            List<FormattedText> wrapped = mc.font.getSplitter()
                    .splitLines(Component.translatable(key), 150, Style.EMPTY);
            for (FormattedText line : wrapped) {
                out.add(Component.literal(line.getString())
                        .withColor(VWColors.GRAY)
                        .withStyle(ChatFormatting.ITALIC));
            }
        }
    }
    private static void addEmpty(List<Component> out) {
        out.add(Component.literal(""));
    }
    private static void addExpandingText(List<Component> out, String... keys) {
        if (mc.hasShiftDown()) {
            for (String key : keys) {
                List<FormattedText> wrapped = mc.font.getSplitter()
                        .splitLines(Component.translatable(key), 150, Style.EMPTY);
                for (FormattedText line : wrapped) {
                    out.add(Component.literal(line.getString()).withColor(VWColors.VERDANT_WIND_MUTED));
                }
            }
        } else {
            out.add(Component.literal("Read lore...").withColor(VWColors.GRAY));
        }
    }
}