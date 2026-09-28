package cliffordha.totvw.entity.player;

import cliffordha.totvw.item.events.VWItemBlessings;
import cliffordha.totvw.registry.VWEnchantments;
import cliffordha.totvw.registry.VWItems;
import cliffordha.totvw.registry.VWSounds;
import cliffordha.totvw.registry.attachments.VWAttachments;
import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
import cliffordha.totvw.tag.VWItemTags;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import static cliffordha.totvw.util.VWUtil.sendToChat;

public class VWPlayerInteractions {
    public static void onInteractEvents() {
        UseEntityCallback.EVENT.register( (player, level, _, entity, _) -> onEntityInteractEvent(player, entity));
        UseItemCallback.EVENT.register((player, level, _) -> onUseItemEvent(player));
    }

    private static InteractionResult onEntityInteractEvent(Player player, Entity entity) {
        if (player.level().isClientSide()) return InteractionResult.PASS;
        if (!(entity instanceof LivingEntity)) return InteractionResult.PASS;

        ItemStack stack = player.getMainHandItem();
        boolean hasPaper = stack.is(Items.PAPER) || stack.is(VWItems.VERIXIUM_PAPER);

        if (hasPaper && entity instanceof Player otherPlayer) {
            if (player == otherPlayer) return InteractionResult.PASS;

            AttachmentType<List<Pair<String, UUID>>> T_LIST = PlayerAttachment.TRUSTED_PLAYERS;
            List<Pair<String, UUID>> oldPlayerData = player.getAttachedOrElse(T_LIST, List.of());
            List<Pair<String, UUID>> newPlayerData = new ArrayList<>(oldPlayerData);

            Pair<String, UUID> mobData = PlayerAttachment.getNameAndUUID(otherPlayer);

            boolean hasPriorData = oldPlayerData.stream().anyMatch(data -> data.getB() == mobData.getB());
            if (oldPlayerData.size() >= 4 && !hasPriorData) {
                sendToChat(player, true, "You can only list 4 trusted players at a time.");
                return InteractionResult.FAIL;
            }
            if (hasPriorData) {
                var index = oldPlayerData.stream().findFirst().filter(data -> data.getB() == mobData.getB()).map(oldPlayerData::indexOf).orElse(null);
                if (index == null) return InteractionResult.PASS;

                Pair<String, UUID> data = oldPlayerData.get(index);

                if (!data.getA().equals(mobData.getA()) && data.getB() == mobData.getB()) {
                    Pair<String, UUID> update = new Pair<>(mobData.getA(), mobData.getB());

                    newPlayerData.set(newPlayerData.indexOf(data), update);
                    player.setAttached(T_LIST, List.copyOf(newPlayerData));
                    sendToChat(player, true, "Updated name for " + data.getA() + " to " + update.getA() + ".");
                    return InteractionResult.SUCCESS;

                } else {
                    sendToChat(player, true, "You already trust " + mobData.getA());
                    return InteractionResult.FAIL;
                }
            } else {
                newPlayerData.add(mobData);
                player.setAttached(PlayerAttachment.TRUSTED_PLAYERS, List.copyOf(newPlayerData));

                sendToChat(player, false, otherPlayer.getPlainTextName() + " is now a trusted player!");
                player.playSound(VWSounds.NOTIFY);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    private static InteractionResult onUseItemEvent(Player player) {
        if (player.level().isClientSide()) return InteractionResult.PASS;

        ItemStack mainHand = player.getMainHandItem();
        boolean BENEDICTION_SUPPORTED_ITEMS = (mainHand.tags().anyMatch(Predicate.isEqual(VWItemTags.BENEDICTION_ENCHANTMENT_USE_QUALIFIED_TOOLS))
                || mainHand.tags().anyMatch(Predicate.isEqual(VWItemTags.BENEDICTION_ENCHANTMENT_USE_QUALIFIED_ITEMS)));

        boolean HAS_BENEDICTION  = VWEnchantments.getBenediction(player);

        if (BENEDICTION_SUPPORTED_ITEMS && HAS_BENEDICTION && player.isCrouching()) {
            boolean applied = VWItemBlessings.tryApply(player);
            if (applied) return InteractionResult.SUCCESS_SERVER;
        }
        return InteractionResult.PASS;
    }
}
