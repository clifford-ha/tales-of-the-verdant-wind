package cliffordha.totvw.entity.skill;

import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.registry.VWSounds;
import cliffordha.totvw.registry.attachments.PlayerPrefs;
import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import cliffordha.totvw.util.ExtUtil;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;

import static cliffordha.totvw.util.ExtUtil.sendToChat;

public class SkillManager {
    public static void startCooldown(LivingEntity entity, SkillDefinition skill, int duration) {
        if (!VWConfig.get().SERVER_SKILL_COOLDOWNS) return;
        if (!entity.is(EntityTypes.WOLF) || !entity.is(EntityTypes.PLAYER)) return;
        entity.setAttached(skill.cooldown(), duration);
        entity.setAttached(skill.notifier(), 1);
    }

    public static boolean isOnCooldown(LivingEntity entity, SkillDefinition skill) {
        return entity.getAttachedOrElse(skill.cooldown(), 0) > 0;
    }

    public static void notifyReset(LivingEntity entity, SkillDefinition skill) {
        if (!entity.is(EntityTypes.WOLF) || !entity.is(EntityTypes.PLAYER)) return;
        int cooldown = entity.getAttachedOrElse(skill.cooldown(), 0);
        int notify = entity.getAttachedOrElse(skill.notifier(), 0);
        if (cooldown <= 0 && notify == 1) {
            entity.setAttached(skill.notifier(), 0);
            sendToChat(entity, skill.notifierColor(), true, name(entity) + skill.skillName() + " is ready!");
        }
    }

    private static String name(LivingEntity entity) {
        if (entity instanceof Player player) {
            return player.getName().getString() + ": ";
        } else if (entity instanceof Wolf wolf) {
            return wolf.getName().getString() + ": ";
        }
        return "Invalid Entity";
    }

    public static void depleteCooldown(LivingEntity entity, AttachmentType<Integer> skillCD) {
        int current = entity.getAttachedOrElse(skillCD, 0);
        if (current <= 0) return;
        entity.setAttached(skillCD, current - 1);
    }

    public static void playNotification(LivingEntity entity) {
        if (entity instanceof Wolf wolf && wolf.getOwner() instanceof Player player) {
            if (cannotPlaySound(player)) return;
            player.level().playSound(null, player.blockPosition(), VWSounds.NOTIFY, SoundSource.PLAYERS);
        } else if (entity instanceof Player player) {
            if (cannotPlaySound(player)) return;
            player.level().playSound(null, player.blockPosition(), VWSounds.NOTIFY, SoundSource.PLAYERS);
        }
    }
    private static boolean cannotPlaySound(Player player) {
        return !player.getAttachedOrElse(PlayerPrefs.ENABLE_NOTIFIERS, VWConfig.get().SYNCED_ENABLE_NOTIFIERS);
    }

    public static void processCDNotify(LivingEntity entity, AttachmentType<Integer> cooldown, AttachmentType<Integer> notify, int color, String... msg) {
        int cd = entity.getAttachedOrElse(cooldown, 0);
        int notifyFlag = entity.getAttachedOrElse(notify, 0);

        if (notifyFlag == 1 && cd == 0) {
            if (entity instanceof Wolf wolf) {
                sendToChat(wolf, color, msg);
            } else if (entity instanceof Player player) {
                sendToChat(player, color, msg);
            }
            ExtUtil.playSound(entity, VWSounds.NOTIFY, SoundSource.PLAYERS, false);
            entity.setAttached(notify, 0);
        }
    }

    public static void setPlayerConfiguration(Player player) {
        int CD_VERDANT_BLESSING = player.getAttachedOrElse(PlayerAttachment.CD_BLESSING_OF_THE_VERDANT_WIND, 0);
        int CD_HAVOC_AMP = player.getAttachedOrElse(PlayerAttachment.CD_HAVOC, 0);

        if (CD_VERDANT_BLESSING > 0) player.removeAttached(PlayerAttachment.CD_BLESSING_OF_THE_VERDANT_WIND);
        if (CD_HAVOC_AMP > 0) player.removeAttached(PlayerAttachment.CD_HAVOC);
    }

    public static void setPlayerOtherConfiguration(Player player) {
        int COUNTER_VILLAGER_ATROCITY = player.getAttachedOrElse(PlayerAttachment.VILLAGER_ATROCITY_COUNT, 0);
        int COUNTER_WOLF_ATROCITY = player.getAttachedOrElse(PlayerAttachment.WOLF_ATROCITY_COUNT, 0);

        if (COUNTER_VILLAGER_ATROCITY > 0) player.removeAttached(PlayerAttachment.CD_BLESSING_OF_THE_VERDANT_WIND);
        if (COUNTER_WOLF_ATROCITY > 0) player.removeAttached(PlayerAttachment.CD_BLESSING_OF_THE_VERDANT_WIND);
    }

    public static void setWolfConfiguration(Wolf wolf) {
        int CD_VERDANT_BLESSING = wolf.getAttachedOrElse(WolfAttachment.CD_BLESSING_OF_THE_VERDANT_WIND, 0);
        int CD_BLOODLUST_SKILL_PARALYZE = wolf.getAttachedOrElse(WolfAttachment.CD_BLOODLUST_SKILL_PARALYZE, 0);
        int CD_MIGHT_RUPTURE = wolf.getAttachedOrElse(WolfAttachment.CD_MIGHT_SKILL_RUPTURE, 0);
        int CD_IGNORE_DMG = wolf.getAttachedOrElse(WolfAttachment.CD_IGNORE_HIGH_DAMAGE, 0);

        if (CD_VERDANT_BLESSING > 0) wolf.removeAttached(WolfAttachment.CD_BLESSING_OF_THE_VERDANT_WIND);
        if (CD_BLOODLUST_SKILL_PARALYZE > 0) wolf.removeAttached(WolfAttachment.CD_BLOODLUST_SKILL_PARALYZE);
        if (CD_MIGHT_RUPTURE > 0) wolf.removeAttached(WolfAttachment.CD_MIGHT_SKILL_RUPTURE);
        if (CD_IGNORE_DMG > 0) wolf.removeAttached(WolfAttachment.CD_IGNORE_HIGH_DAMAGE);
    }
}