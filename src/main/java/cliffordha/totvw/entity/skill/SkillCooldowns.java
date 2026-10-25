package cliffordha.totvw.entity.skill;

import cliffordha.totvw.registry.attachments.HavocType;
import net.minecraft.world.entity.player.Player;

public class SkillCooldowns {
    public static final int BASE_BLESSING = 15 * 60;
    public static final int BASE_PARALYZE = 18 * 60;
    public static final int BASE_RUPTURE = 21;
    public static int getHavoc(Player player) {
        int cooldown;
        if (HavocType.isAnnihilation(player)) {
            cooldown = 90;
        } else if (HavocType.isExpulsion(player)) {
            cooldown = (60 * 2) + 30;
        } else if (HavocType.isVoid(player)) {
            cooldown = (60 * 7);
        } else {
            cooldown = 30;
        }
        return cooldown;
    }
}
