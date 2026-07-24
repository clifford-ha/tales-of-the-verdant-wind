package cliffordha.totvw.mixin;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.config.TOTVWConfig;
import cliffordha.totvw.entity.VWTrustInteractionData;
import cliffordha.totvw.registry.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.painting.Painting;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static cliffordha.totvw.entity.skill.VWSkillProcessor.sendToChat;
import static cliffordha.totvw.util.VWGlobalUtil.*;

@Mixin(Player.class)
public abstract class PlayerEntityMixin {

    @Unique
    private static void sendInfo(Player player, String message) {
        if (!(player instanceof ServerPlayer server)) return;
        server.sendSystemMessage(Component.literal(message).withColor(VWColors.DEFAULT), true);
    }

    // under testing
    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    private void onDeath(DamageSource source, CallbackInfo ci) {
        Player player = (Player) (Object) this;
        sendToChat(player, false, "You died test");
        if (player.level() instanceof ServerLevel level) {
            List<Wolf> wolves = level.getEntities(EntityType.WOLF, player.getBoundingBox().inflate(128), wolf ->
                    wolf.getOwner() != null && wolf.getOwner().is(player) && wolf.getAttachedOrElse(VWAttachments.Wolf.WOLF_BENEDICTION, 0) > 1);
            Wolf wolf = wolves.getFirst();

            sendToChat(player, false, "You died test 0");

            int benediction = wolf.getAttachedOrElse(VWAttachments.Wolf.WOLF_BENEDICTION, 0);
            player.setHealth(1000f);
            player.removeAllEffects();

            rewriteEffect(player, MobEffects.RESISTANCE, sec(3), 255);
            rewriteEffect(player, VWEffects.BLESSING_OF_THE_VERDANT_WIND, sec(10), 2);
            rewriteEffect(player, MobEffects.ABSORPTION, sec(10), 2);
            wolf.setAttached(VWAttachments.Wolf.WOLF_BENEDICTION, benediction - 1);

            if (wolf.position().distanceTo(player.position()) > 10) {
                if (wolf.isInLava()) return;
                player.teleportTo(wolf.getX(), wolf.getY(), wolf.getZ());
            } else {
                if (player.isInLava()) return;
                wolf.tryToTeleportToOwner();
            }

            level.broadcastEntityEvent(player, (byte) 35);

            String name = wolf.getName().getString();
            int STACK_AFTER = wolf.getAttachedOrElse(VWAttachments.Wolf.WOLF_BENEDICTION, 0);
            TOTVW.sendInfo(name + " your companion shared their Benediction stack! Remaining stacks: " + STACK_AFTER);
            ci.cancel();
        }
    }

    @Inject(method = "interactOn", at = @At("HEAD"), cancellable = true)
    private void onInteract(Entity entity, InteractionHand hand, Vec3 location, CallbackInfoReturnable<InteractionResult> cir) {
        Player player = (Player) (Object) this;
        ItemStack itemStack = player.getItemInHand(hand);

        boolean inanimate = entity instanceof ArmorStand || entity instanceof Painting;
        boolean untrustable = entity instanceof Enemy && !(entity instanceof ZombieVillager);
        boolean isTamedWolf = entity instanceof Wolf wolf && wolf.getOwner() != null && wolf.getOwner() == player;

        boolean cannotTrade = player.getAttachedOrElse(VWAttachments.Player.PLAYER_VILLAGER_ATROCITY_COUNT, 0) > 15;

        if (cannotTrade && entity instanceof Villager || entity instanceof WanderingTrader) {
            int atrocityCount = player.getAttachedOrElse(VWAttachments.Player.PLAYER_VILLAGER_ATROCITY_COUNT, 0);
            sendInfo(player, "Your atrocity count (" + atrocityCount + ") is too high!");
            return;
        }

        if (itemStack.is(VWItems.VERIXIUM_POWDER)) {
            if (inanimate) {
                cir.setReturnValue(InteractionResult.SUCCESS);
                return;
            }
            if (untrustable) {
                sendToChat(player, VWColors.BLOODLUST_EFFECT_MUTED, true, "Can't trust this entity");
                return;
            }
            if (isTamedWolf) return;

            String targetName = entity.getName().getString();
            String thisMob = player.getStringUUID();
            String interactedWith = entity.getStringUUID();


            VWTrustInteractionData data = new VWTrustInteractionData(thisMob, interactedWith);
            VWTrustInteractionData empty = new VWTrustInteractionData("", "");

            boolean hasTrust = entity.getAttachedOrElse(VWAttachments.ENTITY_TRUSTED_MOB_DATA, empty).equals(data);

            if (!hasTrust) {
                entity.setAttached(VWAttachments.ENTITY_TRUSTED_MOB_DATA, data);
                entity.setAttached(VWAttachments.ENTITY_TRUST_POINTS, 2);
                sendToChat(player,true, "Trusted " + targetName);
                cir.setReturnValue(InteractionResult.SUCCESS);
                if (player.isCreative()) return;
                itemStack.shrink(1);
            } else if (hasTrust && player.isShiftKeyDown()) {
                entity.removeAttached(VWAttachments.ENTITY_TRUSTED_MOB_DATA);
                entity.removeAttached(VWAttachments.ENTITY_TRUST_POINTS);
                sendToChat(player,true, "Removed trust for " + targetName);
                cir.setReturnValue(InteractionResult.SUCCESS);
            } else {
                sendToChat(player,true, "Already trusted " + targetName);
                cir.setReturnValue(InteractionResult.PASS);
            }
        }
        if (!player.getAttachedOrElse(VWAttachments.Player.PLAYER_IS_DEV_MODE, false)) return;
        String target = entity.getName().getString();
        if (entity instanceof Villager villager) {
            if (itemStack.is(VWItems.VERIXIUM_PAPER)) {
                String t = villager.getAttachedOrElse(VWAttachments.Villager.VILLAGER_IS_VERDANT_TYPE, false) ? " is a " : " is not a ";
                sendInfo(player, target + t + "verdant type");
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        } else if (entity instanceof Wolf wolf) {
            if (itemStack.is(VWItems.VERIXIUM_PAPER)) {
                String t = wolf.getAttachedOrElse(VWAttachments.Wolf.WOLF_IS_VERDANT_TYPE, false) ? " is a " : " is not a ";
                sendInfo(player, target + t + "verdant type");
                cir.setReturnValue(InteractionResult.SUCCESS);
            } else if (itemStack.is(Items.HONEY_BOTTLE)) {
                boolean unTame = player.isCrouching()
                        && wolf.isTame()
                        && wolf.getOwner() == player;

                if (unTame) {
                    wolf.setTame(false, true);
                    wolf.setOwner(null);
                    sendInfo(player, target + " has been un-tamed");
                }
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (TOTVWConfig.get().OTHER_ATTACHMENT_CD) {
            Player player = (Player) (Object) this;
            int atrocityCount = player.getAttachedOrElse(VWAttachments.Player.PLAYER_VILLAGER_ATROCITY_COUNT, 0);
            if (player.level().getGameTime() % (20 * 30) == 0 && atrocityCount > 0) {
                player.setAttached(VWAttachments.Player.PLAYER_VILLAGER_ATROCITY_COUNT, atrocityCount - 1);
            }
        }
    }

    @Inject(method = "actuallyHurt", at = @At("HEAD"), cancellable = true)
    private void evaluateDMGSource(ServerLevel level, DamageSource source, float dmg, CallbackInfo ci) {
        Player player = (Player) (Object) this;

        if (source.is(DamageTypes.SONIC_BOOM) || source.is(DamageTypes.DRAGON_BREATH) || source.is(DamageTypes.WITHER_SKULL)) {
            if (playerEnchantmentLVL(player, VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS) > 0) {
                player.hurt(source, dmg * 0.5f);
                VWParticleEffects.triggerBenedictionParticles(player, 4);
                ci.cancel();
            }
        }
    }
}
