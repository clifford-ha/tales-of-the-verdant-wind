package cliffordha.totvw.entity.skill;

import cliffordha.totvw.config.TOTVWConfig;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;

import static cliffordha.totvw.entity.skill.ConfigTools.*;

public final class SkillUtil {
    private SkillUtil() {}

    public static void startCooldown(Wolf wolf, WolfSkillDefinition skill, int duration) {
        if (!TOTVWConfig.get().attachmentSkillCD) return;
        wolf.setAttached(skill.cooldown(), duration);
        wolf.setAttached(skill.notifier(), 1);
    }
    public static void startCooldown(Player player, PlayerSkillDefinition skill, int duration) {
        if (!TOTVWConfig.get().attachmentSkillCD) return;
        player.setAttached(skill.cooldown(), duration);
        player.setAttached(skill.notifier(), 1);
    }


    public static void notifyReset(Wolf wolf, WolfSkillDefinition skill) {
        int cooldown = wolf.getAttachedOrElse(skill.cooldown(), 0);
        int notify = wolf.getAttachedOrElse(skill.notifier(), 0);

        if (cooldown <= 0 && notify == 1) {
            wolf.setAttached(skill.notifier(), 0);
            if (!TOTVWConfig.get().useNotifiers) return;
            notifyFromWolf(wolf, skill.notifierColor(), true, name(wolf) + skill.skillName() + " is ready!");
        }
    }
    public static void notifyReset(Player player, PlayerSkillDefinition skill) {
        int cooldown = player.getAttachedOrElse(skill.cooldown(), 0);
        int notify = player.getAttachedOrElse(skill.notifier(), 0);

        if (cooldown <= 0 && notify == 1) {
            player.setAttached(skill.notifier(), 0);
            if (!TOTVWConfig.get().useNotifiers) return;
            notifyFromPlayer(player, skill.notifierColor(), true, name(player) + skill.skillName() + " is ready!");
        }
    }


    public static int evaluateDifficulty(ServerLevel level, int peaceful, int easy, int normal, int hard) {
        return switch (level.getDifficulty()) {
            case PEACEFUL -> peaceful;
            case EASY -> easy;
            case NORMAL -> normal;
            default -> hard;
        };
    }


    private static String name(LivingEntity entity) {
        if (entity instanceof Player player) {
            return player.getName().getString() + ": ";
        } else if (entity instanceof Wolf wolf) {
            return wolf.getName().getString() + ": ";
        } else { throw new IllegalArgumentException("Entity must be a player or wolf"); }
    }
}