package cliffordha.totvw.mixin;

import cliffordha.totvw.registry.ModAttachments;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TamableAnimal.class)
public class TamableAnimalOnDeathMixin {
    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    private void totvw$onDeath(CallbackInfo ci) {
        if ((Object) this instanceof Wolf wolf) {
            boolean ACTIVE_BENEDICTION = wolf.hasAttached(ModAttachments.Wolf.WOLF_BENEDICTION);

            if (ACTIVE_BENEDICTION && wolf.isAlive()) {
                ci.cancel();
            }
        }
    }
}
