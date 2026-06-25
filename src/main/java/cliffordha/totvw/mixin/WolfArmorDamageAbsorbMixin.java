package cliffordha.totvw.mixin;

import cliffordha.totvw.tag.ModItemTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.wolf.Wolf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Wolf.class)
public class WolfArmorDamageAbsorbMixin {
    @Inject(method = "canArmorAbsorb", at = @At("HEAD"), cancellable = true)
    private void totvw$absorbDMG(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        Wolf wolf = (Wolf) (Object) this;
        cir.setReturnValue(wolf.getBodyArmorItem().is(ModItemTags.WOLF_ARMOR_ENCHANTABLE) && !source.is(DamageTypeTags.BYPASSES_WOLF_ARMOR));
    }
}
