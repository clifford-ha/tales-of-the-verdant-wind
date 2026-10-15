package cliffordha.totvw.entity.skill;

import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.registry.VWSounds;
import cliffordha.totvw.registry.attachments.PlayerPrefs;
import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import cliffordha.totvw.util.VWUtil;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;

import static cliffordha.totvw.util.VWUtil.sendToChat;

public class SkillManager {
    public static void startCooldown(Wolf wolf, WolfSkillDefinition skill, int duration) {
        if (!VWConfig.get().SERVER_SKILL_COOLDOWNS) return;
        wolf.setAttached(skill.cooldown(), duration);
        wolf.setAttached(skill.notifier(), 1);
    }
    public static void startCooldown(Player player, PlayerSkillDefinition skill, int duration) {
        if (!VWConfig.get().SERVER_SKILL_COOLDOWNS) return;
        player.setAttached(skill.cooldown(), duration);
        player.setAttached(skill.notifier(), 1);
    }

    public static boolean isOnCooldown(LivingEntity entity, WolfSkillDefinition skill) {
        return entity.getAttachedOrElse(skill.cooldown(), 0) > 0;
    }

    public static void notifyReset(Wolf wolf, WolfSkillDefinition skill) {
        int cooldown = wolf.getAttachedOrElse(skill.cooldown(), 0);
        int notify = wolf.getAttachedOrElse(skill.notifier(), 0);
        if (cooldown <= 0 && notify == 1) {
            wolf.setAttached(skill.notifier(), 0);
            sendToChat(wolf, skill.notifierColor(), true, name(wolf) + skill.skillName() + " is ready!");
        }
    }
    public static void notifyReset(Player player, PlayerSkillDefinition skill) {
        int cooldown = player.getAttachedOrElse(skill.cooldown(), 0);
        int notify = player.getAttachedOrElse(skill.notifier(), 0);
        if (cooldown <= 0 && notify == 1) {
            player.setAttached(skill.notifier(), 0);
            sendToChat(player, skill.notifierColor(), true, name(player) + skill.skillName() + " is ready!");
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
        return !player.getAttachedOrElse(PlayerPrefs.ENABLE_NOTIFIERS, VWConfig.get().CLIENT_ENABLE_NOTIFIERS);
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
            VWUtil.playSound(entity, VWSounds.NOTIFY, SoundSource.PLAYERS, true);
            entity.setAttached(notify, 0);
        }
    }

    public static void setPlayerConfiguration(Player player, int config) {
        int CD_VERDANT_BLESSING = player.getAttachedOrElse(PlayerAttachment.CD_BLESSING_OF_THE_VERDANT_WIND, 0);
        int CD_HAVOC_AMP = player.getAttachedOrElse(PlayerAttachment.CD_HAVOC, 0);

        if (config == 0) {
            if (CD_VERDANT_BLESSING > 0) showLog(player, "VerdantBlessingCD", CD_VERDANT_BLESSING);
            if (CD_HAVOC_AMP > 0) showLog(player, "HavocCD", CD_HAVOC_AMP);
        } else {
            if (CD_VERDANT_BLESSING > 0) player.setAttached(PlayerAttachment.CD_BLESSING_OF_THE_VERDANT_WIND, 0);
            if (CD_HAVOC_AMP > 0) player.setAttached(PlayerAttachment.CD_HAVOC, 0);
        }
    }

    public static void setPlayerOtherConfig(Player player) {
        int COUNTER_VILLAGER_ATROCITY = player.getAttachedOrElse(PlayerAttachment.VILLAGER_ATROCITY_COUNT, 0);
        int COUNTER_WOLF_ATROCITY = player.getAttachedOrElse(PlayerAttachment.WOLF_ATROCITY_COUNT, 0);

        if (COUNTER_VILLAGER_ATROCITY > 0) player.setAttached(PlayerAttachment.CD_BLESSING_OF_THE_VERDANT_WIND, 0);
        if (COUNTER_WOLF_ATROCITY > 0) player.setAttached(PlayerAttachment.CD_BLESSING_OF_THE_VERDANT_WIND, 0);
    }


    /** 0 = sendLog, 1 = resetCD **/
    public static void setWolfConfiguration(Wolf wolf, int config) {
        String name = wolf.getPlainTextName();
        int CD_VERDANT_BLESSING = wolf.getAttachedOrElse(WolfAttachment.CD_BLESSING_OF_THE_VERDANT_WIND, 0);
        int CD_BLOODLUST_SKILL_PARALYZE = wolf.getAttachedOrElse(WolfAttachment.CD_BLOODLUST_SKILL_PARALYZE, 0);
        int CD_MIGHT_RUPTURE = wolf.getAttachedOrElse(WolfAttachment.CD_MIGHT_SKILL_RUPTURE, 0);
        int CD_IGNORE_DMG = wolf.getAttachedOrElse(WolfAttachment.CD_IGNORE_HIGH_DAMAGE, 0);

        if (config == 0) {
            if (CD_VERDANT_BLESSING > 0) showLog(wolf, name + " | VerdantBlessingCD", CD_VERDANT_BLESSING);
            if (CD_BLOODLUST_SKILL_PARALYZE > 0) showLog(wolf, name + " | ParalyzeCD", CD_BLOODLUST_SKILL_PARALYZE);
            if (CD_MIGHT_RUPTURE > 0) showLog(wolf, name + " | MightCD", CD_MIGHT_RUPTURE);
            if (CD_IGNORE_DMG > 0) showLog(wolf, name + " | IgnoreHighDMG", CD_IGNORE_DMG);
        } else {
            if (CD_VERDANT_BLESSING > 0) wolf.setAttached(WolfAttachment.CD_BLESSING_OF_THE_VERDANT_WIND, 0);
            if (CD_BLOODLUST_SKILL_PARALYZE > 0) wolf.setAttached(WolfAttachment.CD_BLOODLUST_SKILL_PARALYZE, 0);
            if (CD_MIGHT_RUPTURE > 0) wolf.setAttached(WolfAttachment.CD_MIGHT_SKILL_RUPTURE, 0);
            if (CD_IGNORE_DMG > 0) wolf.setAttached(WolfAttachment.CD_IGNORE_HIGH_DAMAGE, 0);
        }
    }
    private static void showLog(LivingEntity entity, String value, int attachment) {
        if (entity instanceof Wolf wolf) {
            sendToChat(wolf, false, value + " | " + attachment + " sec");
        } else if (entity instanceof Player player) {
            sendToChat(player, false, value + " | " + attachment + " sec");
        }
    }
    private static void showLog(LivingEntity entity, String value, String attachment) {
        if (entity instanceof Wolf wolf) {
            sendToChat(wolf, false, value + " | " + attachment);
        } else if (entity instanceof Player player) {
            sendToChat(player, false, value + " | " + attachment);
        }
    }
}