package cliffordha.totvw.item.events;

import cliffordha.totvw.client.screen.ModTestScreen;
import cliffordha.totvw.registry.ModColors;
import cliffordha.totvw.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import static cliffordha.totvw.entity.skill.ConfigTools.notifyFromPlayer;

public class ReadScatteredPages {
    private static boolean isItem(Player player, Item item) {
        ItemStack mainHandItem = player.getItemBySlot(EquipmentSlot.MAINHAND);
        return mainHandItem.is(item);
    }
    public static boolean tryApply(Level level, Player player) {
        boolean page1 = isItem(player, ModItems.VERIXIUM_PAPER);
        boolean page2 = isItem(player, Items.PAPER);

        if (page1) {
            setPage(player, 1);
        } else if (page2) {
            setPage(player, 2);
        } else {
            return false;
        }
        return true;
    }

    private static void setPage(Player player, int page) {

        switch (page) {
            case 1 -> {
                Minecraft.getInstance().setScreen(
                        new ModTestScreen(Component.literal("TEST PAGE 1"), 1)
                );
            }
            case 2 -> {
                Minecraft.getInstance().setScreen(
                        new ModTestScreen(Component.literal("TEST PAGE 2"), 2)
                );
            }
            default -> {
            }
        }
        sendLog(player, page);
    }

    private static void sendLog(Player player, int page) {
        notifyFromPlayer(player, ModColors.DEFAULT_MUTED, "DEBUG: Page " + page + " opened");
    }
}
