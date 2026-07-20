package cliffordha.totvw.entity.skill;

import cliffordha.totvw.config.TOTVWConfig;
import cliffordha.totvw.registry.VWAttachments;
import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.VWSounds;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.animal.wolf.Wolf;

import java.util.Arrays;

import static cliffordha.totvw.util.VWGlobalUtil.*;

public class VWSkillProcessor {
    public static void depleteCooldown(LivingEntity entity, AttachmentType<Integer> skillCD) {
        int current = entity.getAttachedOrElse(skillCD, 0);
        if (current <= 0) return;
        entity.setAttached(skillCD, current - sec(1));
    }

    public static void playNotification(LivingEntity entity) {
        if (!TOTVWConfig.get().ENABLE_NOTIFIERS) return;
        if (!(entity.level() instanceof ServerLevel)) return;
        if (entity instanceof Wolf wolf && wolf.getOwner() != null && wolf.getOwner() instanceof ServerPlayer player) {
            player.level().playLocalSound(player, VWSounds.NOTIFY, SoundSource.PLAYERS, 1.0f, 1.0f);
        } else if (entity instanceof ServerPlayer player) {
            player.level().playLocalSound(player, VWSounds.NOTIFY, SoundSource.PLAYERS, 1.0f, 1.0f);
        }
    }

    public static void processCDNotify(LivingEntity entity, AttachmentType<Integer> cooldown, AttachmentType<Integer> notify, int color, String... msg) {
        if (!TOTVWConfig.get().ENABLE_NOTIFIERS) return;
        int cd = entity.getAttachedOrElse(cooldown, 0);
        int notifyFlag = entity.getAttachedOrElse(notify, 0);

        if (notifyFlag == 1 && cd == 0) {
            if (entity instanceof Wolf wolf) {
                notifyFromWolf(wolf, color, msg);
            } else if (entity instanceof Player player) {
                notifyFromPlayer(player, color, msg);
            }
            playNotification(entity);
            entity.setAttached(notify, 0);
        }
    }

    public static void setPlayerConfiguration(Player player, int config) {
        String name = player.getName().getString();
        int CD_VERDANT_BLESSING = player.getAttachedOrElse(VWAttachments.Player.PLAYER_CD_BLESSING_OF_THE_VERDANT_WIND, 0);

        if (config == 0) {
            if (CD_VERDANT_BLESSING > 0) {
                notifyFromPlayer(player, VWColors.DEFAULT_MUTED, name + " | VerdantBlessingCD: " + CD_VERDANT_BLESSING / 20 + " sec");
            }
        } else if (config == 1) {
            if (CD_VERDANT_BLESSING > 0) {
                player.setAttached(VWAttachments.Player.PLAYER_CD_BLESSING_OF_THE_VERDANT_WIND, 0);
            }
        }
    }


    /** 0 = sendLog, 1 = resetCD **/
    public static void setWolfConfiguration(Wolf wolf, int config) {
        String name = wolf.getName().getString();
        int CD_VERDANT_BLESSING = wolf.getAttachedOrElse(VWAttachments.Wolf.WOLF_CD_BLESSING_OF_THE_VERDANT_WIND, 0);
        int CD_BLOODLUST_SKILL_PARALYZE = wolf.getAttachedOrElse(VWAttachments.Wolf.WOLF_CD_BLOODLUST_SKILL_PARALYZE, 0);
        int CD_MIGHT_RUPTURE = wolf.getAttachedOrElse(VWAttachments.Wolf.WOLF_CD_MIGHT_SKILL_RUPTURE, 0);

        if (config == 0) {
            if (CD_VERDANT_BLESSING > 0) {
                notifyFromWolf(wolf, VWColors.DEFAULT_MUTED, name + " | VerdantBlessingCD: " + CD_VERDANT_BLESSING / sec(1) + " sec");
            }
            if (CD_BLOODLUST_SKILL_PARALYZE > 0) {
                notifyFromWolf(wolf, VWColors.DEFAULT_MUTED, name + " | ParalyzeCD: " + CD_BLOODLUST_SKILL_PARALYZE / sec(1) + " sec");
            }
            if (CD_MIGHT_RUPTURE > 0) {
                notifyFromWolf(wolf, VWColors.DEFAULT_MUTED, name + " | RuptureCD: " + CD_MIGHT_RUPTURE / sec(1) + " sec");
            }
        } else if (config == 1) {
            if (CD_VERDANT_BLESSING > 0) {
                wolf.setAttached(VWAttachments.Wolf.WOLF_CD_BLESSING_OF_THE_VERDANT_WIND, 0);
            }
            if (CD_BLOODLUST_SKILL_PARALYZE > 0) {
                wolf.setAttached(VWAttachments.Wolf.WOLF_CD_BLOODLUST_SKILL_PARALYZE, 0);
            }
            if (CD_MIGHT_RUPTURE > 0) {
                wolf.setAttached(VWAttachments.Wolf.WOLF_CD_MIGHT_SKILL_RUPTURE, 0);
            }
        }
    }


    public static void notifyFromPlayer(Player player, int color, String... msg) {
        if (!TOTVWConfig.get().ENABLE_NOTIFIERS) return;
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(Component.literal(Arrays.toString(msg)).withColor(color));
        }
    }
    public static void notifyFromWolf(Wolf wolf, int color, String... msg) {
        if (!TOTVWConfig.get().ENABLE_NOTIFIERS) return;
        if (wolf.getOwner() != null && wolf.getOwner() instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(Component.literal(Arrays.toString(msg)).withColor(color));
        }
    }


    public static void notifyFromPlayer(Player player, int color, boolean overlay, String... msg) {
        if (!TOTVWConfig.get().ENABLE_NOTIFIERS) return;
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(Component.literal(Arrays.toString(msg)).withColor(color), overlay);
        }
    }
    public static void notifyFromWolf(Wolf wolf, int color, boolean overlay, String... msg) {
        if (!TOTVWConfig.get().ENABLE_NOTIFIERS) return;
        if (wolf.getOwner() != null && wolf.getOwner() instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(Component.literal(Arrays.toString(msg)).withColor(color), overlay);
        }
    }
}
