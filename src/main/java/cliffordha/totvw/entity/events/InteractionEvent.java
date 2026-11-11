package cliffordha.totvw.entity.events;

import cliffordha.totvw.entity.interactions.VillagerInteractions;
import cliffordha.totvw.entity.interactions.WolfInteractions;
import cliffordha.totvw.item.events.VWItemBlessings;
import cliffordha.totvw.networking.VWNetworking;
import cliffordha.totvw.networking.packets.ConfirmAlliance;
import cliffordha.totvw.registry.VWEnchantments;
import cliffordha.totvw.registry.VWItems;
import cliffordha.totvw.registry.attachments.NameHolder;
import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
import cliffordha.totvw.tag.VWItemTags;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static cliffordha.totvw.util.ExtUtil.scanArea;
import static cliffordha.totvw.util.ExtUtil.sendToChat;

public class InteractionEvent {
    public static InteractionResult onEntityInteractEvent(Player player, Entity entity, InteractionHand hand) {
        if (!(player.level() instanceof ServerLevel level)) return InteractionResult.PASS;
        if (!(entity instanceof LivingEntity livingEntity)) return InteractionResult.PASS;

        ItemStack stack = player.getItemInHand(hand);

        switch (livingEntity) {
            case Player otherPlayer -> {
                return processTrust(player, otherPlayer, stack, hand);
            }
            case Wolf wolf -> {
                return WolfInteractions.handleInteraction(level, player, wolf, stack);
            }
            case WanderingTrader wanderingTrader -> {
                return VillagerInteractions.handleInteraction(level, player, wanderingTrader, stack);
            }
            case Villager villager -> {
                return VillagerInteractions.handleInteraction(level, player, villager, stack);
            }
            default -> {
                return InteractionResult.PASS;
            }
        }
    }
    public static InteractionResult onUseItemEvent(Player player) {
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

    public static InteractionResult processTrust(Player player, Player other, ItemStack stack, InteractionHand hand) {
        if (other.getAttachedOrElse(PlayerAttachment.BLOCK_ALLIANCE_REQUEST, false)) return InteractionResult.PASS;
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        if (player == other) return InteractionResult.PASS;
        if (!stack.is(VWItems.VERIXIUM_PAPER) && !stack.is(Items.PAPER)) return InteractionResult.PASS;

        AttachmentType<List<NameHolder>> T_LIST = PlayerAttachment.TRUSTED_PLAYERS;
        List<NameHolder> oldPlayerData = player.getAttachedOrElse(T_LIST, List.of());
        NameHolder mobData = NameHolder.of(other);

        int index = -1;
        for (int i = 0; i < oldPlayerData.size(); i++) {
            if (oldPlayerData.get(i).sameUUID(mobData)) {
                index = i;
                break;
            }
        }

        if (index < 0) {
            if (player.getAttachedOrElse(PlayerAttachment.CD_ALLIANCE_REQUEST, 0) > 0) {
                sendToChat(player, true, "Alliance request on cooldown");
                return InteractionResult.FAIL;
            }
            if (oldPlayerData.size() >= 8) {
                sendToChat(player, true, "You can only add 8 trusted players at a time");
                return InteractionResult.FAIL;
            }

            List<Monster> monsters = player.level().getEntitiesOfClass(Monster.class, scanArea(player, 12));
            String invalid = "Cannot request alliance; " + other.getPlainTextName();

            if (!other.onGround()) {
                sendToChat(player, true, invalid + " is not on stable ground");
                return InteractionResult.FAIL;
            }
            if (other.isSleeping() || other.isSpectator() || other.isPassenger() || other.isInLava()) {
                sendToChat(player, true, invalid + " cannot interact with their current state");
                return InteractionResult.FAIL;
            }

            if (!monsters.isEmpty() && !(player.isCreative() && other.isCreative())) {
                sendToChat(player, true, "Request was aborted; there are monsters nearby");
                return InteractionResult.FAIL;
            }

            if (other instanceof ServerPlayer serverOther) {
                VWNetworking.sendToPlayer(serverOther, new ConfirmAlliance(player.getUUID()));
                player.setAttached(PlayerAttachment.CD_ALLIANCE_REQUEST, 30);
                sendToChat(player, false, "Request sent");
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        NameHolder data = oldPlayerData.get(index);
        if (!data.sameName(mobData)) {
            List<NameHolder> newPlayerData = new ArrayList<>(oldPlayerData);
            NameHolder update = new NameHolder(mobData.name(), mobData.uuid());
            newPlayerData.set(index, update);
            player.setAttached(T_LIST, List.copyOf(newPlayerData));
            sendToChat(player, true, "Updated name for " + data.name() + " to " + update.name());
        } else {
            sendToChat(player, true, "You already trust " + data.name());
        }
        return InteractionResult.SUCCESS;
    }
    private static void setCooldown(Player player, ItemStack stack) {
        player.getCooldowns().addCooldown(stack, 20);
    }
}
