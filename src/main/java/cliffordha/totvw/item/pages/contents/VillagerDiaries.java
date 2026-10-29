package cliffordha.totvw.item.pages.contents;

import cliffordha.totvw.item.pages.ScatteredPageItem;
import net.minecraft.world.entity.player.Player;

import static cliffordha.totvw.util.TextUtil.*;

public class VillagerDiaries {
    private static Player player;
    public static void resolvePlayer(Player resolver) {
        player = resolver;
    }

    public static String[] get(int id) {
        if (player == null) return ScatteredPageItem.invalidPlayer;
        return ScatteredPageItem.invalidRef;
    }

    private static String[] get3000() {
        return addPage(
                ""
        );
    }
    private static String[] get3001() {
        return addPage(
                ""
        );
    }
    private static String[] get3002() {
        return addPage(
                ""
        );
    }
}
