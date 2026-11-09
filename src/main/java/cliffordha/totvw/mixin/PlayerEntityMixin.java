package cliffordha.totvw.mixin;

import cliffordha.totvw.entity.skills.RunestoneEffects;
import cliffordha.totvw.registry.*;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerEntityMixin {

    @Inject(method = "actuallyHurt", at = @At("TAIL"))
    private void onHurt(ServerLevel level, DamageSource source, float dmg, CallbackInfo ci) {
        Player player = (Player) (Object) this;
        Entity attacker = source.getEntity();

        if (player.hasEffect(VWEffects.HAVOC)) {
            RunestoneEffects.getHavocForPlayer(player, attacker, dmg);
        }
    }

    @Inject(method = "interactOn", at = @At("HEAD"), cancellable = true)
    private void onInteract(Entity entity, InteractionHand hand, Vec3 location, CallbackInfoReturnable<InteractionResult> cir) {
        if (entity instanceof Wolf wolf && wolf.hasAttached(WolfAttachment.FRAGMENT_DATA)) cir.setReturnValue(InteractionResult.PASS);
    }

    @Inject(method = "isSwimming", at = @At("RETURN"), cancellable = true)
    private void canSwim(CallbackInfoReturnable<Boolean> cir) {
        Player player = (Player) (Object) this;
        if (player.hasEffect(VWEffects.PARALYZE) && !player.getAbilities().instabuild && !player.isSpectator()) cir.setReturnValue(false);
    }
}
