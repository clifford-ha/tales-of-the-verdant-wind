package cliffordha.totvw.client;

import cliffordha.totvw.config.TOTVWConfig;
import cliffordha.totvw.registry.ModColors;
import cliffordha.totvw.registry.ModEnchantments;
import cliffordha.totvw.registry.ModItems;
import cliffordha.totvw.tag.ModItemTags;
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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
public final class ModTooltipsClient {
    public static void register() {
        ItemTooltipCallback.EVENT.register(ModTooltipsClient::onTooltip);
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
        if (stack.tags().noneMatch(Predicate.isEqual(ModItemTags.BENEDICTION_ENCHANTMENT_USE_QUALIFIED_TOOLS))) return;
        if (getBenedictionLevel(mc) <= 0) return;

        addEffectHeader(out, ModColors.VERDANT_WIND, "effect.tales-of-the-verdant-wind.blessing_of_the_verdant_wind");

        String desc = resolveToolEffect(stack);
        if (desc != null) addMutedItalic(out, desc);
    }

    private static String resolveToolEffect(ItemStack stack) {
        if (stack.tags().anyMatch(t -> t.location().getPath().equals("swords")))   return "+Strength";
        if (stack.tags().anyMatch(t -> t.location().getPath().equals("axes")))     return "+Strength II";
        if (stack.tags().anyMatch(t -> t.location().getPath().equals("pickaxes"))) return "+Haste";
        if (stack.tags().anyMatch(t -> t.location().getPath().equals("hoes")))     return "+Absorption";
        if (stack.tags().anyMatch(t -> t.location().getPath().equals("shovels")))  return "+Speed";
        return null;
    }


    private static void handleBenedictionItemLines(ItemStack stack, Minecraft mc, List<Component> out) {
        if (stack.tags().noneMatch(Predicate.isEqual(ModItemTags.BENEDICTION_ENCHANTMENT_USE_QUALIFIED_ITEMS))) return;
        if (getBenedictionLevel(mc) <= 0) return;

        addEffectHeader(out, ModColors.VERDANT_WIND, "effect.tales-of-the-verdant-wind.blessing_of_the_verdant_wind");

        if (stack.is(ModItems.VERIXIUM_POWDER)) {
            addMutedItalic(out, "+Slow Falling");
        } else {
            addMutedItalic(out, "+Night Vision");
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


        if (!TOTVWConfig.get().useNewLanguageSet) {
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
        
        if (stack.is(ModItems.VERIXIUM_CHUNK)) {
            addLoreShiftGated(out, LORE_verixiumChunk);

        } else if (stack.is(ModItems.CONDENSED_VERIXIUM)) {
            addLoreShiftGated(out, LORE_condensedVerixium0, LORE_condensedVerixium1);

        } else if (stack.is(ModItems.VERIXIUM_SHARD)) {
            addLoreShiftGated(out, LORE_verixiumShard);

        } else if (stack.is(ModItems.VERIXIUM_POWDER)) {
            addLoreShiftGated(out, LORE_verixiumPowder);

        } else if (stack.is(ModItems.VERIXIUM_INGOT)) {
            addLoreShiftGated(out, LORE_verixiumIngot);
            
        } else if (stack.is(ModItems.VERIXIUM_PAPER)
                || stack.is(ModItems.VERIXIUM_ARMOR_UPGRADE_TEMPLATE)) {
            addMutedItalic(out, Component.translatable(itemVerixium).getString());
            
        } else if (stack.is(ModItems.VERIXIUM_HELMET)
                || stack.is(ModItems.VERIXIUM_LEGGINGS)
                || stack.is(ModItems.VERIXIUM_BOOTS)) {
            addMutedItalic(out, Component.translatable(itemVerixiumArmor).getString());

        } else if (stack.is(ModItems.VERIXIUM_CHESTPLATE)) {
            addMutedItalic(out, Component.translatable(itemVerixiumArmor).getString());
            addEffectHeader(out, ModColors.VERDANT_WIND, "effect.tales-of-the-verdant-wind.blessing_of_the_verdant_wind");
            addMutedItalicIfBenediction(out, mc, "Grants an effect when certain item(s) are used");
            
        } else if (stack.is(ModItems.VERIXIUM_WOLF_ARMOR)) {
            addMutedItalic(out, Component.translatable(itemVerixiumArmor).getString());
            addEffectHeader(out, ModColors.VERDANT_WIND, "effect.tales-of-the-verdant-wind.blessing_of_the_verdant_wind");
            addWolfArmorBenedictionLore(out, mc);
        }
    }

    private static void addWolfArmorBenedictionLore(List<Component> out, Minecraft mc) {
        int benediction = getBenedictionLevel(mc);
        String l_verixiumWolfArmor = "\"The breezy mountains once favored the call of a certain howl...\"";
        String l_verixiumWolfArmorE = "\"The breezy mountains once favored the call of a certain howl. Although subtle, he too does his part to protect the verdant mountains... a place he calls home.\"";

        String loreKey = (benediction == 0)
                ? l_verixiumWolfArmor
                : l_verixiumWolfArmorE;
        addLoreShiftTitle(out, "Armor Engraving:", loreKey);
    }


    private static int getBenedictionLevel(Minecraft mc) {
        if (mc.player == null || mc.level == null) return 0;
        ItemStack chest = mc.player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.isEmpty()) return 0;
        Registry<Enchantment> reg = mc.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        return chest.getEnchantments().getLevel(reg.getOrThrow(ModEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS));
    }


    private static void addEffectHeader(List<Component> out, int color, String translationKey) {
        out.add(Component.empty());
        out.add(Component.translatable(translationKey).withColor(color));
    }


    private static void addMutedItalic(List<Component> out, String text) {
        out.add(Component.literal(text)
                .withColor(ModColors.VERDANT_WIND_MUTED)
                .withStyle(ChatFormatting.ITALIC));
    }


    private static void addMutedItalicIfBenediction(List<Component> out, Minecraft mc, String text) {
        if (getBenedictionLevel(mc) <= 0) return;
        addMutedItalic(out, text);
    }


    private static void addLoreShiftGated(List<Component> out, String... keys) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.hasShiftDown()) {
            for (String key : keys) {
                List<FormattedText> wrapped = mc.font.getSplitter()
                        .splitLines(Component.translatable(key), 200, Style.EMPTY);
                for (FormattedText line : wrapped) {
                    out.add(Component.literal(line.getString())
                            .withColor(ModColors.VERDANT_WIND_MUTED)
                            .withStyle(ChatFormatting.ITALIC));
                }
            }
        } else {
            out.add(Component.literal("Hold §nShift§r key to see more details").withStyle(ChatFormatting.DARK_GRAY));
        }
    }
    private static void addLoreShiftTitle(List<Component> out, String title, String... keys) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.hasShiftDown()) {
            out.add(Component.translatable(title).withColor(ModColors.VERDANT_WIND));
            for (String key : keys) {
                List<FormattedText> wrapped = mc.font.getSplitter()
                        .splitLines(Component.translatable(key), 200, Style.EMPTY);
                for (FormattedText line : wrapped) {
                    out.add(Component.literal(line.getString())
                            .withColor(ModColors.VERDANT_WIND_MUTED)
                            .withStyle(ChatFormatting.ITALIC));
                }
            }
        } else {
            out.add(Component.literal("Hold §nShift§r key to see more details").withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}