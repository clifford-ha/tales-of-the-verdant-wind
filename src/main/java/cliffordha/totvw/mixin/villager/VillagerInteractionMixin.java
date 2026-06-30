package cliffordha.totvw.mixin.villager;

import cliffordha.totvw.registry.ModAttachments;
import cliffordha.totvw.registry.ModColors;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static cliffordha.totvw.entity.skill.ConfigTools.notifyFromPlayer;

@Mixin(Villager.class)
public class VillagerInteractionMixin {

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void totvw$interact(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Villager villager = (Villager) (Object) this;
        String name = villager.getName().getString();
        ItemStack itemStack = player.getItemInHand(hand);

        boolean isMeVerdantType = villager.getAttachedOrElse(ModAttachments.Villager.IS_VERDANT_TYPE, false);

        if (itemStack.is(Items.STICK)) {
            if (isMeVerdantType) {
                notifyFromPlayer(player, ModColors.DEFAULT, name + " is a Verdant Type");
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }
}
