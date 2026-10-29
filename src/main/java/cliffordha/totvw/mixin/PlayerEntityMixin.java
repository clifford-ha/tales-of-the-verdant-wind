package cliffordha.totvw.mixin;

import cliffordha.totvw.registry.*;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerEntityMixin {

    @Inject(method = "isSwimming", at = @At("RETURN"), cancellable = true)
    private void canSwim(CallbackInfoReturnable<Boolean> cir) {
        Player player = (Player) (Object) this;
        if (player.hasEffect(VWEffects.PARALYZE) && !player.getAbilities().instabuild && !player.isSpectator()) cir.setReturnValue(false);
    }
}
