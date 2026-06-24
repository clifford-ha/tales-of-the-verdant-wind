package cliffordha.totvw.mixin;

import cliffordha.totvw.config.TOTVWConfig;
import cliffordha.totvw.tag.ModItemTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Wolf.class)
public class WolfArmorDamageAbsorbMixin {
    @Inject(method = "canArmorAbsorb", at = @At("HEAD"), cancellable = true)
    private void totvw$absorbDMG(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        Wolf wolf = (Wolf) (Object) this;
        boolean dmg1 = source.is(DamageTypeTags.IS_PROJECTILE);
        boolean dmg2 = source.is(DamageTypeTags.IS_EXPLOSION);
        boolean dmg3 = source.is(DamageTypeTags.IS_MACE_SMASH);
        boolean dmg4 = source.is(DamageTypeTags.IS_LIGHTNING);

        cir.setReturnValue(wolf.getBodyArmorItem().is(ModItemTags.WOLF_ARMOR_ENCHANTABLE) && !source.is(DamageTypeTags.BYPASSES_WOLF_ARMOR));

        /*
        if (TOTVWConfig.get().wolfArmorDamageDistribution) {
            if (dmg1 || dmg2 || dmg3 || dmg4) {
                cir.setReturnValue(wolf.getBodyArmorItem().is(ModItemTags.WOLF_ARMOR_ENCHANTABLE));
            } else {
                cir.setReturnValue(false);
            }
        } else {
            cir.setReturnValue(wolf.getBodyArmorItem().is(ModItemTags.WOLF_ARMOR_ENCHANTABLE) && !source.is(DamageTypeTags.BYPASSES_WOLF_ARMOR));
        }*/
    }
}
