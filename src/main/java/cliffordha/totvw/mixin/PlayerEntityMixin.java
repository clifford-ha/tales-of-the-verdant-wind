package cliffordha.totvw.mixin;

import cliffordha.totvw.config.TOTVWConfig;
import cliffordha.totvw.registry.*;
import com.mojang.datafixers.util.Either;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.biome.v1.NetherBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.attribute.BedRule;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static cliffordha.totvw.util.VWUtil.sendToChat;
import static cliffordha.totvw.util.VWUtil.*;

@Mixin(Player.class)
public abstract class PlayerEntityMixin {

    @Inject(method = "interactOn", at = @At("HEAD"), cancellable = true)
    private void onInteract(Entity entity, InteractionHand hand, Vec3 location, CallbackInfoReturnable<InteractionResult> cir) {
        Player player = (Player) (Object) this;
        ItemStack itemStack = player.getItemInHand(hand);

        int atrocityCount = player.getAttachedOrElse(VWAttachments.Player.PLAYER_VILLAGER_ATROCITY_COUNT, 0);

        if (atrocityCount > 20 && entity instanceof Villager || entity instanceof WanderingTrader) {
            sendToChat(player, true, "Your atrocity count (" + atrocityCount + ") is too high!");
            return;
        }
        String target = entity.getName().getString();
        if (entity instanceof Villager villager) {
            if (itemStack.is(VWItems.VERIXIUM_PAPER)) {
                String sentence = villager.getAttachedOrElse(VWAttachments.Villager.VILLAGER_IS_VERDANT_TYPE, false) ? " is a " : " is not a ";
                sendToChat(player, true, target + sentence + "verdant type");
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        } else if (entity instanceof Wolf wolf) {
            AttachmentType<Integer> BENEDICTION_STACK = VWAttachments.Wolf.WOLF_BENEDICTION;

            if (itemStack.is(VWItems.VERIXIUM_PAPER)) {
                if (player.isCrouching()) {
                    sendToChat(player, true, target + " has " + wolf.getAttachedOrElse(BENEDICTION_STACK, 0) + " remaining Benediction stack(s)");
                    cir.setReturnValue(InteractionResult.SUCCESS);
                } else {
                    String sentence = wolf.getAttachedOrElse(VWAttachments.Wolf.WOLF_IS_VERDANT_TYPE, false) ? " is a " : " is not a ";
                    sendToChat(player, true, target + sentence + "verdant type");
                    cir.setReturnValue(InteractionResult.SUCCESS);
                }
            } else if (itemStack.is(Items.PAPER)) {
                if (player.isCrouching()) {
                    BlockPos pos = wolf.getAttachedOrElse(VWAttachments.Wolf.WOLF_RESPAWN_POINT, wolf.blockPosition());
                    sendToChat(player, true, target + "'s respawn point is at " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
                } else {
                    if (wolf.isBaby()) {
                        sendToChat(player, false, target + " has " + wolf.getAttachedOrElse(VWAttachments.Wolf.WOLF_PARENTS_ID, ""));
                    } else {
                        sendToChat(player, false, target + " ID: " + wolf.getStringUUID());
                    }
                }
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (TOTVWConfig.get().SERVER_OTHER_COOLDOWNS) {
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

    @Unique
    private static final Logger SEND = LoggerFactory.getLogger("TOTVW/PlayerEntityMixin");
    @Unique
    private static void sendToServer(String message) {
        if (TOTVWConfig.get().MIXIN_UPDATE_LOGS) {
            SEND.info(message);
        }
    }
}
