package cliffordha.totvw.mixin.wolf;

import cliffordha.totvw.registry.ModAttachments;
import cliffordha.totvw.registry.ModColors;
import cliffordha.totvw.registry.ModEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.wolf.Wolf;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static cliffordha.totvw.entity.TConstants.*;
import static cliffordha.totvw.entity.skill.ConfigTools.notifyFromWolf;

@Mixin(Wolf.class)
public abstract class WolfBenedictionMixin {
    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    private void totvw$reviveWolf(DamageSource source, CallbackInfo ci) {
        Wolf wolf = (Wolf) (Object) this;
        int ACTIVE_BENEDICTION = wolf.getAttachedOrElse(ModAttachments.Wolf.WOLF_BENEDICTION, 0);

        if (ACTIVE_BENEDICTION == 0) return;
        wolf.setHealth(40.0f);
        wolf.removeAllEffects();

        rewriteEffect(wolf, MobEffects.RESISTANCE, sec(3), 255);
        rewriteEffect(wolf, ModEffects.BLESSING_OF_THE_VERDANT_WIND, sec(10), 2);
        rewriteEffect(wolf, MobEffects.ABSORPTION, sec(10), 2);
        rewriteEffect(wolf, MobEffects.STRENGTH, sec(10), 2);

        wolf.setAttached(ModAttachments.Wolf.WOLF_BENEDICTION, ACTIVE_BENEDICTION - 1);

        if (ACTIVE_BENEDICTION - 1 == 0) {
            notifyFromWolf(wolf, ModColors.BLOODLUST_EFFECT_MUTED,ACTIVE_BENEDICTION - 1 + " Benediction stack remaining for " + wolf.getName().getString());
            wolf.removeAttached(ModAttachments.Wolf.WOLF_BENEDICTION);
        } else {
            notifyFromWolf(wolf, ModColors.VERDANT_WIND_MUTED,ACTIVE_BENEDICTION - 1 + " Benediction stack remaining for " + wolf.getName().getString());
        }

        if (wolf.level() instanceof ServerLevel) {
            wolf.level().broadcastEntityEvent(wolf, (byte) 35);
        }
        ci.cancel();
    }
}