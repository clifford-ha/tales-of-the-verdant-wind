package cliffordha.totvw.client;

import cliffordha.totvw.config.TOTVWConfig;
import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.VWEnchantments;
import cliffordha.totvw.registry.VWItems;
import cliffordha.totvw.tag.VWItemTags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
public final class VWItemBlessingTooltip {
    public static void register() {
        ItemTooltipCallback.EVENT.register(VWItemBlessingTooltip::onTooltip);
    }
    private static void onTooltip(ItemStack stack, Item.TooltipContext context, TooltipFlag flag, List<Component> lines) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        List<Component> injected = new ArrayList<>();

        handleBenedictionToolLines(stack, mc, injected);
        handleBenedictionItemLines(stack, mc, injected);
        handleLoreLines(stack, injected);

        if (!injected.isEmpty()) {
            int insertAt = Math.min(1, lines.size());
            lines.addAll(insertAt, injected);
        }
    }

    private static void handleBenedictionToolLines(ItemStack stack, Minecraft mc, List<Component> out) {
        if (stack.tags().noneMatch(Predicate.isEqual(VWItemTags.BENEDICTION_ENCHANTMENT_USE_QUALIFIED_TOOLS))) return;
        if (getBenedictionLevel(mc) <= 0) return;

        String desc = resolveToolEffect(stack);
        if (desc != null) addVWItem(mc, out, desc);
    }

    private static String resolveToolEffect(ItemStack stack) {
        if (stack.tags().anyMatch(t -> t.location().getPath().equals("swords")))   return "VW Strength";
        if (stack.tags().anyMatch(t -> t.location().getPath().equals("axes")))     return "VW Strength II";
        if (stack.tags().anyMatch(t -> t.location().getPath().equals("pickaxes"))) return "VW Haste";
        if (stack.tags().anyMatch(t -> t.location().getPath().equals("hoes")))     return "VW Absorption";
        if (stack.tags().anyMatch(t -> t.location().getPath().equals("shovels")))  return "VW Speed";
        return null;
    }


    private static void handleBenedictionItemLines(ItemStack stack, Minecraft mc, List<Component> out) {
        if (stack.tags().noneMatch(Predicate.isEqual(VWItemTags.BENEDICTION_ENCHANTMENT_USE_QUALIFIED_ITEMS))) return;
        if (getBenedictionLevel(mc) <= 0) return;

        if (stack.is(VWItems.VERIXIUM_POWDER)) {
            addVWItem(mc, out, "VW Slow Falling");
        } else {
            addVWItem(mc, out, "VW Night Vision");
        }
    }
    private static void handleLoreLines(ItemStack stack, List<Component> out) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        
        // this translation block looks awful ngl
        String unset = "Not yet set";

        String itemVerixiumArmor = "A light yet durable armor";
        String itemVerixium = "A subtle hint of wind emanates from this item...";
        String LORE_verixiumChunk;
        String LORE_condensedVerixium0;
        String LORE_condensedVerixium1;
        String LORE_verixiumShard;
        String LORE_verixiumPowder;
        String LORE_verixiumIngot;


        if (!TOTVWConfig.get().CLIENT_TRANSLATE_LANGUAGE) {
            LORE_verixiumChunk = "\"For the land that they call 'home' and for the people they protect.\"";
            LORE_condensedVerixium0 = "\"For the land that they call 'home' and for the people they protect.";
            LORE_condensedVerixium1 = "O God of the Verdant Winds...\"";
            LORE_verixiumShard = "\"Bless us, so we may reap a bountiful harvest.\"";
            LORE_verixiumPowder = "\"Guide us, so we may never be swept by the floods of fiery lies.\"";
            LORE_verixiumIngot = "\"Protect us, so we may defend your land and people from the perils of the creatures from beyond.\"";
        } else {
            LORE_verixiumChunk = unset;
            LORE_condensedVerixium0 = unset;
            LORE_condensedVerixium1 = unset;
            LORE_verixiumShard = unset;
            LORE_verixiumPowder = unset;
            LORE_verixiumIngot = unset;
        }
        
        if (stack.is(VWItems.VERIXIUM_CHUNK)) {
            addLoreShiftGated(out, 1, LORE_verixiumChunk);

        } else if (stack.is(VWItems.CONDENSED_VERIXIUM)) {
            addLoreShiftGated(out, 1, LORE_condensedVerixium0, LORE_condensedVerixium1);

        } else if (stack.is(VWItems.VERIXIUM_SHARD)) {
            addLoreShiftGated(out, 1,  LORE_verixiumShard);

        } else if (stack.is(VWItems.VERIXIUM_POWDER)) {
            addLoreShiftGated(out, 1, LORE_verixiumPowder);

        } else if (stack.is(VWItems.VERIXIUM_INGOT)) {
            addLoreShiftGated(out, 1, LORE_verixiumIngot);
            
        } else if (stack.is(VWItems.VERIXIUM_PAPER)
                || stack.is(VWItems.VERIXIUM_ARMOR_UPGRADE_TEMPLATE)) {
            addMutedItalic(mc, out, itemVerixium);
            
        } else if (stack.is(VWItems.VERIXIUM_HELMET)
                || stack.is(VWItems.VERIXIUM_LEGGINGS)
                || stack.is(VWItems.VERIXIUM_BOOTS)) {
            addMutedItalic(mc, out, itemVerixiumArmor);

        } else if (stack.is(ItemTags.CHEST_ARMOR)) {
            if (stack.is(VWItems.VERIXIUM_CHESTPLATE)) {
                addMutedItalic(mc, out, itemVerixiumArmor);
            }
            if (getBenedictionLevel(mc) <= 0) return;
            addLoreShiftGated(out, 1001, "Grants effect when an item with blessing mark [VW] is used");

        } else if (stack.is(VWItems.VERIXIUM_WOLF_ARMOR)) {
            addMutedItalic(mc, out, itemVerixiumArmor);
            addWolfArmorBenedictionLore(mc, out);
        }
    }

    private static void addWolfArmorBenedictionLore(Minecraft mc, List<Component> out) {
        String LORE_verixiumWolfArmor = "\"The breezy mountains once favored the call of a certain howl...\"";
        String LORE_verixiumWolfArmorE = "\"The breezy mountains once favored the call of a certain howl. Although subtle, he too does his part to protect the verdant mountains... a place he calls home.\"";

        String loreKey = (getBenedictionLevel(mc) == 0) ? LORE_verixiumWolfArmor : LORE_verixiumWolfArmorE;
        addLoreShiftGated(out, 1, "Armor Engraving:", loreKey);
    }

    private static int getBenedictionLevel(Minecraft mc) {
        if (mc.player == null || mc.level == null) return 0;
        ItemStack chest = mc.player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.isEmpty()) return 0;
        Registry<Enchantment> reg = mc.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        return chest.getEnchantments().getLevel(reg.getOrThrow(VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS));
    }

    private static void addVWItem(Minecraft mc, List<Component> out, String... text) {
        if (getBenedictionLevel(mc) <= 0) return;
        if (mc.hasShiftDown()) {
            out.add(Component.literal(Arrays.toString(text)).withColor(VWColors.VERDANT_WIND));
        } else {
            out.add(Component.literal("[VW] Active"));
        }
    }

    private static void addMutedItalic(Minecraft mc, List<Component> out, String text) {
        if (!mc.hasShiftDown()) {
            out.add(Component.literal(text)
                    .withColor(VWColors.GRAY)
                    .withStyle(ChatFormatting.ITALIC));
        }
    }

    /** 0 = for none | 1 = for 'expand' | 1001 = reserved for chestplate **/
    private static void addLoreShiftGated(List<Component> out, int type, String... keys) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.hasShiftDown()) {
            if (type == 1001) {out.add(Component.literal("Verdant Wind's Blessing").withColor(VWColors.VERDANT_WIND));}
            for (String key : keys) {
                List<FormattedText> wrapped = mc.font.getSplitter()
                        .splitLines(Component.translatable(key), 200, Style.EMPTY);
                for (FormattedText line : wrapped) {
                    out.add(Component.literal(line.getString())
                            .withColor(VWColors.VERDANT_WIND_MUTED)
                            .withStyle(ChatFormatting.ITALIC));
                }
            }
            if (type == 1001) {out.add(Component.literal(""));}
        } else {
            if (type == 0) return;
            if (type == 1) {
                out.add(Component.literal("Read lore...").withColor(VWColors.GRAY));
            } else if (type == 1001) {
                out.add(Component.literal("[VW] Blessing Active"));
            } else {
                out.add(Component.literal("Invalid INT value").withColor(VWColors.BLOODLUST_EFFECT));
            }
        }
    }
}