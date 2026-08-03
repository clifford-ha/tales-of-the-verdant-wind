package cliffordha.totvw.entity.skill;

import cliffordha.totvw.config.TOTVWConfig;
import cliffordha.totvw.registry.VWAttachments;
import cliffordha.totvw.registry.VWSounds;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.animal.wolf.Wolf;

import static cliffordha.totvw.util.VWUtil.sendToChat;

public class VWSkillProcessor {
    public static void depleteCooldown(LivingEntity entity, AttachmentType<Integer> skillCD) {
        int current = entity.getAttachedOrElse(skillCD, 0);
        if (current <= 0) return;
        entity.setAttached(skillCD, current - 1);
    }

    public static void playNotification(LivingEntity entity) {
        if (!TOTVWConfig.get().CLIENT_ENABLE_NOTIFIERS) return;
        if (!(entity.level() instanceof ServerLevel)) return;
        if (entity instanceof Wolf wolf && wolf.getOwner() != null && wolf.getOwner() instanceof ServerPlayer player) {
            player.level().playLocalSound(player, VWSounds.NOTIFY, SoundSource.PLAYERS, 1.0f, 1.0f);
        } else if (entity instanceof ServerPlayer player) {
            player.level().playLocalSound(player, VWSounds.NOTIFY, SoundSource.PLAYERS, 1.0f, 1.0f);
        }
    }

    public static void processCDNotify(LivingEntity entity, AttachmentType<Integer> cooldown, AttachmentType<Integer> notify, int color, String... msg) {
        if (!TOTVWConfig.get().CLIENT_ENABLE_NOTIFIERS) return;
        int cd = entity.getAttachedOrElse(cooldown, 0);
        int notifyFlag = entity.getAttachedOrElse(notify, 0);

        if (notifyFlag == 1 && cd == 0) {
            if (entity instanceof Wolf wolf) {
                sendToChat(wolf, color, msg);
            } else if (entity instanceof Player player) {
                sendToChat(player, color, msg);
            }
            playNotification(entity);
            entity.setAttached(notify, 0);
        }
    }

    public static void setPlayerConfiguration(Player player, int config) {
        String name = player.getName().getString();
        int CD_VERDANT_BLESSING = player.getAttachedOrElse(VWAttachments.Player.PLAYER_CD_BLESSING_OF_THE_VERDANT_WIND, 0);

        if (config == 0) {
            if (CD_VERDANT_BLESSING > 0) showLog(player, name + " | VerdantBlessingCD", CD_VERDANT_BLESSING);
        } else if (config == 1) {
            if (CD_VERDANT_BLESSING > 0) player.setAttached(VWAttachments.Player.PLAYER_CD_BLESSING_OF_THE_VERDANT_WIND, 0);
        }
    }


    /** 0 = sendLog, 1 = resetCD **/
    public static void setWolfConfiguration(Wolf wolf, int config) {
        String name = wolf.getName().getString();
        int CD_VERDANT_BLESSING = wolf.getAttachedOrElse(VWAttachments.Wolf.WOLF_CD_BLESSING_OF_THE_VERDANT_WIND, 0);
        int CD_BLOODLUST_SKILL_PARALYZE = wolf.getAttachedOrElse(VWAttachments.Wolf.WOLF_CD_BLOODLUST_SKILL_PARALYZE, 0);
        int CD_MIGHT_RUPTURE = wolf.getAttachedOrElse(VWAttachments.Wolf.WOLF_CD_MIGHT_SKILL_RUPTURE, 0);
        int CD_IGNORE_DMG = wolf.getAttachedOrElse(VWAttachments.Wolf.WOLF_CD_IGNORE_HIGH_DAMAGE, 0);

        if (config == 0) {
            if (CD_VERDANT_BLESSING > 0) showLog(wolf, name + " | VerdantBlessingCD", CD_VERDANT_BLESSING);
            if (CD_BLOODLUST_SKILL_PARALYZE > 0) showLog(wolf, name + " | ParalyzeCD", CD_BLOODLUST_SKILL_PARALYZE);
            if (CD_MIGHT_RUPTURE > 0) showLog(wolf, name + " | MightCD", CD_MIGHT_RUPTURE);
            if (CD_IGNORE_DMG > 0) showLog(wolf, name + " | IgnoreDMG", CD_IGNORE_DMG);
        } else if (config == 1) {
            if (CD_VERDANT_BLESSING > 0) wolf.setAttached(VWAttachments.Wolf.WOLF_CD_BLESSING_OF_THE_VERDANT_WIND, 0);
            if (CD_BLOODLUST_SKILL_PARALYZE > 0) wolf.setAttached(VWAttachments.Wolf.WOLF_CD_BLOODLUST_SKILL_PARALYZE, 0);
            if (CD_MIGHT_RUPTURE > 0) wolf.setAttached(VWAttachments.Wolf.WOLF_CD_MIGHT_SKILL_RUPTURE, 0);
            if (CD_IGNORE_DMG > 0) wolf.setAttached(VWAttachments.Wolf.WOLF_CD_IGNORE_HIGH_DAMAGE, 0);
        }
    }
    private static void showLog(LivingEntity entity, String value, int attachment) {
        if (entity instanceof Wolf wolf) {
            sendToChat(wolf, false, value + " | " + attachment + " sec");
        } else if (entity instanceof Player player) {
            sendToChat(player, false, value + " | " + attachment + " sec");
        }
    }


}
