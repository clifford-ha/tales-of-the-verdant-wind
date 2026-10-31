package cliffordha.totvw.entity.skill;

import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.registry.VWSounds;
import cliffordha.totvw.registry.attachments.PlayerPrefs;
import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;

import static cliffordha.totvw.util.ExtUtil.playStaticSound;
import static cliffordha.totvw.util.ExtUtil.sendToChat;

public class SkillManager {
    public static void startCooldown(LivingEntity entity, SkillDefinition skill, int duration) {
        if (!VWConfig.get().SERVER_SKILL_COOLDOWNS) return;
        if (entity instanceof Wolf wolf) {
            wolf.setAttached(skill.cooldown(), duration);
            wolf.setAttached(skill.notifier(), 1);
        } else if (entity instanceof Player player) {
            player.setAttached(skill.cooldown(), duration);
            player.setAttached(skill.notifier(), 1);
        }
    }

    public static boolean isOnCooldown(LivingEntity entity, SkillDefinition skill) {
        return entity.getAttachedOrElse(skill.cooldown(), 0) > 0;
    }

    public static void depleteCooldown(LivingEntity entity, AttachmentType<Integer> skillCD) {
        int current = entity.getAttachedOrElse(skillCD, 0);
        if (current <= 0) return;
        entity.setAttached(skillCD, current - 1);
    }

    public static void playNotification(LivingEntity entity) {
        if (entity instanceof Player player) {
            if (cannotPlaySound(player)) return;
            playStaticSound(player, VWSounds.NOTIFY, SoundSource.PLAYERS, false);

        } else if (entity instanceof Wolf wolf && wolf.getOwner() instanceof Player player) {
            if (cannotPlaySound(player)) return;
            playStaticSound(player, VWSounds.NOTIFY, SoundSource.PLAYERS, false);
        }
    }
    private static boolean cannotPlaySound(Player player) {
        return !player.getAttachedOrElse(PlayerPrefs.ENABLE_NOTIFIERS, VWConfig.get().SYNCED_ENABLE_NOTIFIERS);
    }

    public static void processCDNotify(LivingEntity entity, SkillDefinition skill) {
        int cd = entity.getAttachedOrElse(skill.cooldown(), 0);
        int notifyFlag = entity.getAttachedOrElse(skill.notifier(), 0);

        if (notifyFlag == 1 && cd == 0) {
            if (entity instanceof Wolf wolf) {
                sendToChat(wolf, false, wolf.getPlainTextName() + ": " + skill.skillName() + " is ready!");
            } else if (entity instanceof Player player) {
                sendToChat(player, false, skill.skillName() + " is ready!");
            }
            playStaticSound(entity, VWSounds.NOTIFY, SoundSource.PLAYERS, false);
            entity.removeAttached(skill.notifier());
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