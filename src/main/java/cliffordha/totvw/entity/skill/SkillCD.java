package cliffordha.totvw.entity.skill;

import cliffordha.totvw.registry.attachments.HavocType;
import net.minecraft.world.entity.player.Player;

public class SkillCD {
    public static final int BASE_BLESSING = 15 * 60;
    public static final int BASE_PARALYZE = 18 * 60;
    public static final int BASE_RUPTURE = 45;
    public static final int BASE_FRAGMENTED_WOLF_SUMMON = 20;

    public static final int HAVOC_ANNIHILATION = 90;
    public static final int HAVOC_EXPULSION = (60 * 2) + 30;
    public static final int HAVOC_VOID = (60 * 7);
    public static int getHavoc(Player player) {
        int cooldown;
        if (HavocType.isAnnihilation(player)) {
            cooldown = HAVOC_ANNIHILATION;

        } else if (HavocType.isExpulsion(player)) {
            cooldown = HAVOC_EXPULSION;

        } else if (HavocType.isVoid(player)) {
            cooldown = HAVOC_VOID;

        } else {
            cooldown = 0;
        }
        return cooldown;
    }
}
