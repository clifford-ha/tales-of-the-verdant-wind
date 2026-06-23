package cliffordha.totvw.entity.skill;

import cliffordha.totvw.config.TOTVWConfig;
import cliffordha.totvw.registry.ModAttachments;
import cliffordha.totvw.registry.ModColors;
import cliffordha.totvw.registry.ModSounds;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.animal.wolf.Wolf;

import java.util.Arrays;

import static cliffordha.totvw.entity.TConstants.*;

public class ConfigTools {

    public static void depleteCooldown(LivingEntity entity, AttachmentType<Integer> skillCD) {
        int current = entity.getAttachedOrElse(skillCD, 0);
        if (current <= 0) return;
        entity.setAttached(skillCD, current - sec(1));
    }

    public static void processCDNotify(LivingEntity entity, AttachmentType<Integer> cooldown, AttachmentType<Integer> notify, int color, String... msg) {
        if (!TOTVWConfig.get().useNotifiers) return;
        int cd = entity.getAttachedOrElse(cooldown, 0);
        int notifyFlag = entity.getAttachedOrElse(notify, 0);

        if (notifyFlag == 1 && cd == 0) {
            if (entity instanceof Wolf wolf) {
                notifyFromWolf(wolf, color, msg);
            } else if (entity instanceof Player player) {
                notifyFromPlayer(player, color, msg);
            }
            entity.setAttached(notify, 0);
            entity.playSound(ModSounds.NOTIFY, 1.0f, 1.0f);
        }
    }

    public static void setPlayerConfiguration(Player player, int config) {
        String name = player.getName().getString();
        int CD_VERDANT_BLESSING = player.getAttachedOrElse(ModAttachments.Player.CD_BLESSING_OF_THE_VERDANT_WIND, 0);
        if (config == 0) {
            if (CD_VERDANT_BLESSING > 0) {
                notifyFromPlayer(player, ModColors.DEFAULT_MUTED, name + ": VerdantBlessingCD: " + CD_VERDANT_BLESSING / 20 + " sec");
            }
        } else if (config == 1) {
            if (CD_VERDANT_BLESSING > 0) {
                player.setAttached(ModAttachments.Player.CD_BLESSING_OF_THE_VERDANT_WIND, 0);
            }
        } else throw new IllegalArgumentException("Invalid config value");
    }


    /** 0 = sendLog, 1 = resetCD **/
    public static void setWolfConfiguration(Wolf wolf, int config) {
        String name = wolf.getName().getString();
        int CD_VERDANT_BLESSING = wolf.getAttachedOrElse(ModAttachments.Wolf.CD_BLESSING_OF_THE_VERDANT_WIND, 0);
        int CD_BLOODLUST_SKILL_PARALYZE = wolf.getAttachedOrElse(ModAttachments.Wolf.CD_BLOODLUST_SKILL_PARALYZE, 0);
        int CD_MIGHT_RUPTURE = wolf.getAttachedOrElse(ModAttachments.Wolf.CD_MIGHT_SKILL_RUPTURE, 0);

        if (config == 0) {
            if (CD_VERDANT_BLESSING > 0) {
                notifyFromWolf(wolf, ModColors.DEFAULT_MUTED, name + ": VerdantBlessingCD: " + CD_VERDANT_BLESSING / sec(1) + " sec");
            }
            if (CD_BLOODLUST_SKILL_PARALYZE > 0) {
                notifyFromWolf(wolf, ModColors.DEFAULT_MUTED, name + ": ParalyzeCD: " + CD_BLOODLUST_SKILL_PARALYZE / sec(1) + " sec");
            }
            if (CD_MIGHT_RUPTURE > 0) {
                notifyFromWolf(wolf, ModColors.DEFAULT_MUTED, name + ": RuptureCD: " + CD_MIGHT_RUPTURE / sec(1) + " sec");
            }
        } else if (config == 1) {
            if (CD_VERDANT_BLESSING > 0) {
                wolf.setAttached(ModAttachments.Wolf.CD_BLESSING_OF_THE_VERDANT_WIND, 0);
            }
            if (CD_BLOODLUST_SKILL_PARALYZE > 0) {
                wolf.setAttached(ModAttachments.Wolf.CD_BLOODLUST_SKILL_PARALYZE, 0);
            }
            if (CD_MIGHT_RUPTURE > 0) {
                wolf.setAttached(ModAttachments.Wolf.CD_MIGHT_SKILL_RUPTURE, 0);
            }
        } else throw new IllegalArgumentException("Invalid config value");
    }


    public static void playSound(LivingEntity entity, SoundEvent sound, SoundSource source) {
        if (!(entity.level() instanceof ServerLevel level)) return;
        var posX = entity.getX();
        var posY = entity.getY();
        var posZ = entity.getZ();
        var random = level.getRandom().nextFloat();
        level.playSound(null, posX, posY, posZ, sound, source, 0.7f + random, 0.7f + random);
    }




    public static void notifyFromPlayer(Player player, int color, String... msg) {
        if (!TOTVWConfig.get().useNotifiers) return;
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(Component.literal(Arrays.toString(msg)).withColor(color));
        }
    }
    public static void notifyFromWolf(Wolf wolf, int color, String... msg) {
        if (!TOTVWConfig.get().useNotifiers) return;
        if (wolf.getOwner() instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(Component.literal(Arrays.toString(msg)).withColor(color));
        }
    }


    public static void notifyFromPlayer(Player player, int color, boolean overlay, String... msg) {
        if (!TOTVWConfig.get().useNotifiers) return;
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(Component.literal(Arrays.toString(msg)).withColor(color), overlay);
        }
    }
    public static void notifyFromWolf(Wolf wolf, int color, boolean overlay, String... msg) {
        if (!TOTVWConfig.get().useNotifiers) return;
        if (wolf.getOwner() instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(Component.literal(Arrays.toString(msg)).withColor(color), overlay);
        }
    }
}
