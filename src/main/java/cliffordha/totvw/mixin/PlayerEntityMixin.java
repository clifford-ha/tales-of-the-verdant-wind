package cliffordha.totvw.mixin;

import cliffordha.totvw.entity.VWInteractionData;
import cliffordha.totvw.registry.VWAttachments;
import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.VWItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.painting.Painting;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

import static cliffordha.totvw.entity.skill.VWSkillProcessor.notifyFromPlayer;

@Mixin(Player.class)
public class PlayerEntityMixin {

    @Inject(method = "interactOn", at = @At("HEAD"), cancellable = true)
    private void trustInteract(Entity entity, InteractionHand hand, Vec3 location, CallbackInfoReturnable<InteractionResult> cir) {
        Player player = (Player) (Object) this;
        ItemStack itemStack = player.getItemInHand(hand);
        String dash = "-";
        String truster = player.getName().getString() + player.getStringUUID();
        String trustee = dash + entity.getName().getString() + entity.getStringUUID();
        String name = entity.getName().getString();

        boolean inanimate = entity instanceof ArmorStand || entity instanceof Painting;
        boolean untrustable = entity instanceof Enemy && !(entity instanceof ZombieVillager);
        boolean isTamedWolf = entity instanceof Wolf wolf && wolf.getOwner() != null && wolf.getStringUUID().equals(wolf.getOwner().getStringUUID());

        if (itemStack.is(VWItems.VERIXIUM_POWDER)) {
            if (inanimate) { cir.setReturnValue(InteractionResult.SUCCESS); }
            if (inanimate) return;
            if (untrustable) { notifyFromPlayer(player, VWColors.BLOODLUST_EFFECT_MUTED, true, "Can't trust this entity");}
            if (untrustable) return;
            if (isTamedWolf) return;

            if (!Objects.equals(entity.getAttached(VWAttachments.ENTITY_INTERACTION_DATA), new VWInteractionData(truster, trustee))) {
                entity.setAttached(VWAttachments.ENTITY_INTERACTION_DATA, new VWInteractionData(truster, trustee));
                entity.setAttached(VWAttachments.ENTITY_TRUST_POINTS, + 2);
                notifyFromPlayer(player, VWColors.VERDANT_WIND, true, "Trusted: " + name);
                cir.setReturnValue(InteractionResult.SUCCESS);
                if (player.isInvulnerable() || player.isCreative() || player.isSpectator()) return;
                itemStack.shrink(1);
            } else if (Objects.equals(entity.getAttached(VWAttachments.ENTITY_INTERACTION_DATA), new VWInteractionData(truster, trustee)) && player.isShiftKeyDown()) {
                entity.removeAttached(VWAttachments.ENTITY_INTERACTION_DATA);
                entity.removeAttached(VWAttachments.ENTITY_TRUST_POINTS);
                notifyFromPlayer(player, VWColors.VERDANT_WIND, true, "Removed trust for " + name);
                cir.setReturnValue(InteractionResult.SUCCESS);
            } else {
                notifyFromPlayer(player, VWColors.DEFAULT_MUTED, true, "Already trusted: " + name);
                cir.setReturnValue(InteractionResult.PASS);
            }
        }
    }
}
