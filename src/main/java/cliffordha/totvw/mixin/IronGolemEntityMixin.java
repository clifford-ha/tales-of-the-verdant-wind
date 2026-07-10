package cliffordha.totvw.mixin;

import cliffordha.totvw.registry.VWAttachments;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.animal.wolf.Wolf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IronGolem.class)
public class IronGolemEntityMixin {

    @Inject(method = "canAttack", at = @At("HEAD"), cancellable = true)
    private void onTick(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if (target instanceof Wolf wolf && wolf.getAttachedOrElse(VWAttachments.Wolf.WOLF_TRY_SAVE_POINTS, 0) > 0) {
            cir.setReturnValue(false);
        }
    }
}
