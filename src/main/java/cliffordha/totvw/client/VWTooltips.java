package cliffordha.totvw.client;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.VWBlocks;
import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.VWEnchantments;
import cliffordha.totvw.util.VWUtil;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class VWTooltips {
    public static void register() {
        ItemTooltipCallback.EVENT.register(VWTooltips::getItem);

        TOTVW.sendClassRegisterLog("Tooltips");
    }

    private static void getItem(ItemStack itemStack, Item.TooltipContext tooltipContext, TooltipFlag tooltipFlag, List<Component> text) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        Player player = mc.player;
        if (player == null) return;

        int ACTIVE_BENEDICTION = VWUtil.entityEnchantmentLVL(player, EquipmentSlot.CHEST, VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS);
        if (itemStack.is(VWBlocks.LODESTONE_WIND_CORE.asItem()) && ACTIVE_BENEDICTION > 0) {
            addText(text,"The core seem to respond with your armor...");
        }
    }

    private static void addText(List<Component> out, String... keys) {
        Minecraft mc = Minecraft.getInstance();

        for (String key : keys) {
            List<FormattedText> wrapped = mc.font.getSplitter()
                    .splitLines(Component.translatable(key), 150, Style.EMPTY);
            for (FormattedText line : wrapped) {
                out.add(Component.literal(line.getString())
                        .withColor(VWColors.VERDANT_WIND_MUTED)
                        .withStyle(ChatFormatting.ITALIC));
            }
        }
    }
}
