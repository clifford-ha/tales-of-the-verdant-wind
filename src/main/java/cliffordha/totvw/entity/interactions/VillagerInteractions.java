package cliffordha.totvw.entity.interactions;

import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import static cliffordha.totvw.util.ExtUtil.sendToChat;

public class VillagerInteractions {
    public static InteractionResult handleInteraction(ServerLevel level, Player player, LivingEntity entity, ItemStack stack) {
        int atrocityCount = PlayerAttachment.getVillagerAtrocityCount(player);
        if (atrocityCount > 20) {
            return tryVillagerInteract(player, entity);
        }

        return InteractionResult.PASS;
    }
    private static InteractionResult tryVillagerInteract(Player player, LivingEntity entity) {
        int atrocityCount = player.getAttachedOrElse(PlayerAttachment.VILLAGER_ATROCITY_COUNT, 0);
        if (atrocityCount > 20 && entity instanceof Villager || entity instanceof WanderingTrader) {
            sendToChat(player, true, "Your atrocity count (" + atrocityCount + ") is too high!");
            return InteractionResult.FAIL;
        }
        return InteractionResult.SUCCESS;
    }
}
