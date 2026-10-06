package cliffordha.totvw.item.custom;

import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.VWEffects;
import cliffordha.totvw.registry.attachments.Runestone;
import cliffordha.totvw.util.VWUtil;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static cliffordha.totvw.util.VWUtil.sendToChat;

public class TetherRunestonePlate extends Item {
    public TetherRunestonePlate(Properties properties) {
        super(properties);
    }

    private static final Component TETHER = Component.literal("§lTether Runestone Plate§r").withColor(VWColors.RUNESTONE_TETHER);
    private static boolean ACTIVE = false;

    @Override
    public Component getName(ItemStack itemStack) {
        return ACTIVE ? TETHER : super.getName(itemStack);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
        List<Wolf> wolves = level.getEntitiesOfClass(
                Wolf.class,
                owner.getBoundingBox().inflate(16),
                wolf -> wolf.getOwner() == owner && Runestone.hasTether(wolf)
        ).stream().limit(1).toList();
        ACTIVE = owner instanceof Player && !wolves.isEmpty();
        super.inventoryTick(itemStack, level, owner, slot);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (player.getCooldowns().isOnCooldown(new ItemStack(this))) return InteractionResult.FAIL;
        if (level instanceof ServerLevel serverLevel && player.isCrouching()) {
            List<Wolf> recall = new ArrayList<>(serverLevel.getEntities(EntityType.WOLF,
                    t -> t.getOwner() == player
            ));

            List<Wolf> multiLevelRecall = new ArrayList<>();
            for (ServerLevel multiLevel : serverLevel.getServer().getAllLevels()) {
                multiLevelRecall.addAll(multiLevel.getEntities(
                        EntityType.WOLF,
                        t -> t.getOwner() == player && t.level().dimensionType() != player.level().dimensionType()
                ));
            }
            if (recall.isEmpty() && multiLevelRecall.isEmpty()) {
                sendToChat(player, true, "No tamed wolves to recall!");
                return InteractionResult.FAIL;
            } else {
                int t = 0;
                if (!multiLevelRecall.isEmpty()) {
                    for (Wolf wolf : multiLevelRecall) {
                        if (wolf.canTeleport(wolf.level(), player.level())) {
                            wolf.unRide();
                            wolf.setOrderedToSit(false);
                            wolf.dropLeash();
                            VWUtil.addEffect(wolf, VWEffects.WIND_VEIL, 60, 0);

                            wolf.teleport(
                                    new TeleportTransition(
                                            (ServerLevel) player.level(),
                                            new Vec3(player.getX(), player.getY(), player.getZ()),
                                            Vec3.ZERO,
                                            player.level().getRespawnData().yaw(),
                                            player.level().getRespawnData().pitch(),
                                            TeleportTransition.PLACE_PORTAL_TICKET
                                    )
                            );
                            t++;
                        }
                    }
                    sendToChat(player, true, "Recalled " + t + " tamed wolves from other dimensions.");
                }
                if (!recall.isEmpty()) {
                    for (Wolf wolf : recall) {
                        wolf.unRide();
                        wolf.setOrderedToSit(false);
                        wolf.dropLeash();

                        wolf.teleportTo(player.getX(), player.getY(), player.getZ());
                        VWUtil.addEffect(wolf, VWEffects.WIND_VEIL, 60, 0);
                    }
                    sendToChat(player, true, "Recalled " + recall.size() + " nearby tamed wolves.");
                }
                player.getCooldowns().addCooldown(new ItemStack(this), 100);
                return InteractionResult.SUCCESS_SERVER;
            }
        }
        return super.use(level, player, hand);
    }
}
