package cliffordha.totvw.mixin;

import cliffordha.totvw.registry.*;
import cliffordha.totvw.tag.ModItemTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static cliffordha.totvw.entity.TConstants.*;
import static cliffordha.totvw.entity.skill.ConfigTools.notifyFromWolf;
import static cliffordha.totvw.entity.skill.ConfigTools.playSound;

@Mixin(Wolf.class)
public class WolfInteractMixin {
    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void totvw$reviveWolf(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Wolf wolf = (Wolf) (Object) this;
        String name = wolf.getName().getString();
        ItemStack playerHand = player.getItemInHand(hand);

        int ACTIVE_BENEDICTION = wolf.getAttachedOrElse(ModAttachments.Wolf.WOLF_BENEDICTION, 0);
        int ACTIVE_BENEDICTION_ENCHANTMENT = wolfEnchantmentLVL(wolf, ModEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS);
        ItemStack itemStack = player.getItemInHand(hand);

        if (itemStack.is(Items.TOTEM_OF_UNDYING) && wolf.isWearingBodyArmor()) {
            if (ACTIVE_BENEDICTION_ENCHANTMENT == 0) return;
            if (ACTIVE_BENEDICTION >= 3) { notifyFromWolf(wolf, ModColors.DEFAULT_MUTED, true, "Max Benediction stack reached!"); }
            if (ACTIVE_BENEDICTION >= 3) return;

            wolf.setAttached(ModAttachments.Wolf.WOLF_BENEDICTION, ACTIVE_BENEDICTION + 1);
            itemStack.shrink(1);
            playSound(wolf, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.AMBIENT);

            addParticle(wolf.level(), wolf.blockPosition(), ModParticles.BENEDICTION_TRIGGER_PARTICLE, 0);
            notifyFromWolf(wolf, ModColors.VERDANT_WIND, true, name + " Benediction stack: " + wolf.getAttachedOrElse(ModAttachments.Wolf.WOLF_BENEDICTION, 0));
            cir.setReturnValue(InteractionResult.SUCCESS);

        } else if (itemStack.is(ModItemTags.WOLF_ARMOR_ENCHANTABLE) && !wolf.isTame() && !wolf.isWearingBodyArmor()) {
            if (wolf.level() instanceof ServerLevel serverLevel) {
                wolf.equipItemIfPossible(serverLevel, playerHand);
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }
}
