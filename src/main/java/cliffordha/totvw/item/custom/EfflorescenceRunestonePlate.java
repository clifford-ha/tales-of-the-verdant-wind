package cliffordha.totvw.item.custom;

import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.attachments.VWAttachments;
import cliffordha.totvw.util.VWUtil;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

import static cliffordha.totvw.util.VWUtil.playSound;
import static cliffordha.totvw.util.VWUtil.sendToChat;

public class EfflorescenceRunestonePlate extends Item {
    public EfflorescenceRunestonePlate(Properties properties) {
        super(properties);
    }
    private static final Component EFFLORESCENCE = Component.literal("§lEfflorescence Runestone Plate§r").withColor(VWColors.RUNESTONE_EFFLORESCENCE);
    private static boolean ACTIVE = false;

    @Override
    public Component getName(ItemStack itemStack) {
        return ACTIVE ? EFFLORESCENCE : super.getName(itemStack);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (player.getCooldowns().isOnCooldown(new ItemStack(this))) return InteractionResult.FAIL;
        ItemStack stack = getBonemeal(player);

        if (stack == null || stack.count() < 5) {
            sendToChat(player, true, "You need at least 5 bonemeal to use this!");
            player.getCooldowns().addCooldown(new ItemStack(this), 10);
            return InteractionResult.FAIL;
        }

        if (stack.count() >= 5) {
            int currentStacks = player.getAttachedOrElse(VWAttachments.VERDANT_BLOOM_STACK, 0);
            player.setAttached(VWAttachments.VERDANT_BLOOM_STACK, currentStacks + 50);
            stack.consume(5, player);
            player.getCooldowns().addCooldown(new ItemStack(this), VWUtil.TimeUtil.duration(1, 30));

            if (level.isClientSide()) {
                playSound(player, SoundEvents.BONE_MEAL_USE, SoundSource.PLAYERS, true);
                sendToChat(player, false, "You gained 50 Verdant Bloom stacks!");
            }

            return InteractionResult.SUCCESS;
        }

        return super.use(level, player, hand);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
        if (owner instanceof Player player) {
            getPlayer = player;
            ACTIVE = player.getAttachedOrElse(VWAttachments.VERDANT_BLOOM_STACK, 0) > 0;
        }
        super.inventoryTick(itemStack, level, owner, slot);
    }

    private static Player getPlayer;

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        if (getPlayer == null) return;
        int amount = getPlayer.getAttachedOrElse(VWAttachments.VERDANT_BLOOM_STACK, 0);
        if (amount > 0) {
            builder.accept(Component.literal("Verdant Bloom Stacks: " + amount).withColor(VWColors.RUNESTONE_EFFLORESCENCE));
        }

        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
    }

    public static @Nullable ItemStack getBonemeal(Player player) {
        return player.getInventory().contains(new ItemStack(Items.BONE_MEAL)) ?
                player.getInventory().getItem(player.getInventory().findSlotMatchingItem(new ItemStack(Items.BONE_MEAL))) : null;
    }
}
