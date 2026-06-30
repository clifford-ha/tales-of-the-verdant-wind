package cliffordha.totvw.mixin.wolf;

import cliffordha.totvw.entity.player.InteractionData;
import cliffordha.totvw.registry.ModAttachments;
import cliffordha.totvw.registry.ModEnchantments;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static cliffordha.totvw.entity.TConstants.wolfEnchantmentLVL;

@Mixin(Wolf.class)
public class WolfAttackableTargetsMixin {

    @Inject(method = "wantsToAttack", at = @At("HEAD"), cancellable = true)
    private void totvw$wantsToAttack(LivingEntity target, LivingEntity owner, CallbackInfoReturnable<Boolean> cir) {
        Wolf wolf = (Wolf) (Object) this;
        if (target instanceof Creeper) {
            if (wolf.getHealth() < wolf.getMaxHealth() * 0.5f) return;
            if (!wolf.isWearingBodyArmor()) return;
            int encProtection = wolfEnchantmentLVL(wolf, Enchantments.PROTECTION);
            int encBlastProtection = wolfEnchantmentLVL(wolf, Enchantments.BLAST_PROTECTION);
            int encGnawing = wolfEnchantmentLVL(wolf, ModEnchantments.WOLF_EFFECT_GNAWING);
            int encMight = wolfEnchantmentLVL(wolf, ModEnchantments.WOLF_EFFECT_MIGHT);
            int encBenediction = wolfEnchantmentLVL(wolf, ModEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS);
            if (!(encGnawing > 0 && (encProtection >= 3 || encBlastProtection >= 3 || encMight > 2 || encBenediction > 0))) return;
            cir.setReturnValue(true);
        }
        if (wolf.isTame() && wolf.getOwner() != null) {
            LivingEntity player = wolf.getOwner();
            String dash = "-";
            String truster = player.getName().getString() + player.getStringUUID();
            String trustee = dash + target.getName().getString() + target.getStringUUID();
            InteractionData data = target.getAttached(ModAttachments.INTERACTION_DATA);
            if (data == null) return;
            if (data.player().equals(truster) && data.trustee().equals(trustee)) {
                wolf.stopBeingAngry();
                cir.setReturnValue(false);
            }
        }
    }
}
