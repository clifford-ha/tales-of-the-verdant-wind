package cliffordha.totvw.entity.player;

import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.attachments.PlayerPrefs;
import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import static cliffordha.totvw.util.VWUtil.sendToChat;
import static cliffordha.totvw.util.VWUtil.setDifficultyBasedValue;

public class PlayerAtrocityCounter {
    public static void processAtrocity(Player player, LivingEntity victim, boolean death) {
        Level level = player.level();

        float multiplier = setDifficultyBasedValue(level, 0.5f, 0.75f, 1.0f, 2.0f);
        int maybeAddMore = level.getRandom().nextIntBetweenInclusive(0, 3);
        int deduction = Mth.ceil(3 * multiplier) + maybeAddMore;
        int finalDeduction = death ? deduction * 4 : deduction;

        ServerPlayer serverPlayer = (ServerPlayer) player;

        if (victim instanceof Wolf wolf) {
            AttachmentType<Integer> WOLF_COUNTER = PlayerAttachment.WOLF_ATROCITY_COUNT;
            boolean maybeForgive = wolf.getOwner() != null && wolf.getOwner().is(player) && level.getRandom().nextBoolean();
            if (maybeForgive) return;

            int current = player.getAttachedOrElse(WOLF_COUNTER, 0);
            player.setAttached(WOLF_COUNTER, current + finalDeduction);

            showAtrocityCounter(serverPlayer, wolf, player.getAttachedOrElse(WOLF_COUNTER, 0));
        } else if (victim instanceof Villager || victim instanceof WanderingTrader) {
            AttachmentType<Integer> VILLAGER_COUNTER = PlayerAttachment.VILLAGER_ATROCITY_COUNT;

            int current = player.getAttachedOrElse(VILLAGER_COUNTER, 0);
            player.setAttached(VILLAGER_COUNTER, current + finalDeduction);

            showAtrocityCounter(serverPlayer, victim, player.getAttachedOrElse(VILLAGER_COUNTER, 0));
        }
    }
    private static void showAtrocityCounter(Player player, LivingEntity victim, int count) {
        if (!player.getAttachedOrElse(PlayerPrefs.SHOW_ATROCITY_COUNTER, VWConfig.get().CLIENT_SHOW_ATROCITY_COUNTER)) return;
        if (victim instanceof Wolf) {
            sendToChat(player, VWColors.BLOODLUST_EFFECT_MUTED, true, "Wolf atrocity count: " + count);
        } else if (victim instanceof Villager || victim instanceof WanderingTrader) {
            sendToChat(player, VWColors.BLOODLUST_EFFECT_MUTED, true, "Villager atrocity count: " + count);
        }
    }
}
