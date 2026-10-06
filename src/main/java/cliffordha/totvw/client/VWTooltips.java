package cliffordha.totvw.client;

import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.registry.*;
import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
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
import static cliffordha.totvw.registry.VWEnchantments.entityEnchantmentLVL;
import static cliffordha.totvw.util.VWUtil.TextUtil.*;

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

        String itemVerixiumArmor = "A light yet durable armor";
        String itemVerixium = "A subtle hint of wind emanates from this item...";;
        
        String LORE_soulRunestonePlate = "My only regret is that I won't be able to be by your side as you grow up... and, do so forgive me for entrusting you with a heavy burden.";
        String LORE_soulRunestoneFragment1 = "\"There will always be those who try to defy the absolute limit of this world. Though sacrifices were uncomfortably high, the ingenuity always take precedence to overcome the obstacles that stand in the way of the living.\"";
        String LORE_soulRunestoneFragment2 = "\"But what good does constantly defying the very principles of nature have?\"";
        String LORE_soulRunestoneFragment3 = "\"Prior to the Wind's ascent, the people of ∎∎∎∎ tried to preserve the memories from the stream of time. Only then did they realize that a soul can be ∎∎∎∎∎ and ∎∎∎∎∎∎∎∎.\"";
        String LORE_soulRunestoneFragment4 = "\"Perhaps... my authority will quench their hatred. Onward, I shall entrust the duties of taking care of our people to each and everyone of you till the day 'I' return.\"";

        String LORE_genesisRunestonePlate = "A fragment of unknown origin...";
        String LORE_efflorescenceRunestonePlate = "You feel as though you are revitalized by the subtle powered contained by this item...";
        String LORE_tetherRunestonePlate = "You can feel a strange connection to the world itself... Perhaps you can use it for something great?";
        String LORE_havocRunestonePlate = "It appears this item is fluctuating on its own...";

        String LORE_verixiumChunk = "\"For the land that they call 'home' and for the people they protect.\"";
        String LORE_condensedVerixium0 = "\"For the land that they call 'home' and for the people they protect.";
        String LORE_condensedVerixium1 = "O God of the Verdant Winds...\"";
        String LORE_verixiumShard = "\"Bless us, so we may reap a bountiful harvest.\"";
        String LORE_verixiumPowder = "\"Guide us, so we may never be swept by the floods of fiery lies.\"";
        String LORE_verixiumIngot = "\"Protect us, so we may defend your land and people from the perils of the creatures from beyond.\"";

        if (stack.is(VWItemTags.RUNESTONE_PLATES)) {
            if (stack.is(VWItems.GENESIS_RUNESTONE_PLATE)) {
                addEmpty(out);
                addExpandingText(out, LORE_genesisRunestonePlate);

            } else if (stack.is(VWItems.EFFLORESCENCE_RUNESTONE_PLATE)) {
                addEmpty(out);
                addExpandingText(out, LORE_efflorescenceRunestonePlate);

            } else if (stack.is(VWItems.SOUL_RUNESTONE_PLATE)) {
                int ACTIVE_SOULS = player.getAttachedOrElse(PlayerAttachment.WOLF_SOULS, List.of()).size();
                if (ACTIVE_SOULS > 0) {
                    int randomInt10 = player.getAttachedOrElse(PlayerAttachment.RANDOM_INT_10, 0);

                    String wolf = ACTIVE_SOULS < 2 ? "The " + cText(AQUA, "wolf") + " is " : "The " + cText(AQUA, ACTIVE_SOULS + "") + " wolves are ";
                    addText(out, wolf + soulRunestoneIdle(randomInt10, ACTIVE_SOULS));
                }
                addEmpty(out);
                addExpandingText(out, "Fragmented Memory:", LORE_soulRunestonePlate);

            } else if (stack.is(VWItems.TETHER_RUNESTONE_PLATE)) {
                addEmpty(out);
                addExpandingText(out, LORE_tetherRunestonePlate);

            } else if (stack.is(VWItems.HAVOC_RUNESTONE_PLATE)) {
                addEmpty(out);
                addExpandingText(out, LORE_havocRunestonePlate);

            }
        }

        if (stack.is(VWItems.SOUL_RUNESTONE_FRAGMENT_1)) addExpandingText(out, LORE_soulRunestoneFragment1);
        if (stack.is(VWItems.SOUL_RUNESTONE_FRAGMENT_2)) addExpandingText(out, LORE_soulRunestoneFragment2);
        if (stack.is(VWItems.SOUL_RUNESTONE_FRAGMENT_3)) addExpandingText(out, LORE_soulRunestoneFragment3);
        if (stack.is(VWItems.SOUL_RUNESTONE_FRAGMENT_4)) addExpandingText(out, LORE_soulRunestoneFragment4);

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
    private static String soulRunestoneIdle(int randomInt10, int souls) {
        String sleeping = souls > 1 ? "resting together..." : "resting...";
        List<String> actions = List.of(
                sleeping,
                "waiting for your call...",
                "snoozing...",
                "idling around...",
                "lazing...",
                "taking a nap...",
                "chilling...",
                "dozing off...",
                "in deep peaceful slumber...",
                "in standby..."
        );
        return actions.get(Math.clamp(randomInt10 - 1, 0, actions.size() - 1));
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
                        .withColor(VWColors.GRAY));
            }
        }
    }
    private static void addEmpty(List<Component> out) {
        out.add(Component.literal(""));
    }
    private static void addExpandingText(List<Component> out, int color, String... keys) {
        if (mc.hasShiftDown()) {
            for (String key : keys) {
                List<FormattedText> wrapped = mc.font.getSplitter()
                        .splitLines(Component.translatable(key), 150, Style.EMPTY);
                for (FormattedText line : wrapped) {
                    out.add(Component.literal(line.getString()).withColor(color));
                }
            }
        } else {
            out.add(Component.literal("Read lore...").withColor(VWColors.GRAY));
        }
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
    private static void addETForSurvival(List<Component> out, String... keys) {
        Player player = mc.player;
        if (player == null) return;
        if (player.isCreative() && !VWConfig.get().CLIENT_ALLOW_LORE_SPOILERS) return;
        addExpandingText(out, keys);
    }
}