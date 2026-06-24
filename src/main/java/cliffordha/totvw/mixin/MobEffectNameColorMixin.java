package cliffordha.totvw.mixin;

import cliffordha.totvw.util.ModTextColors;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.effect.MobEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEffect.class)
public class MobEffectNameColorMixin {

    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void totvw$colorizeEffectName(CallbackInfoReturnable<Component> cir) {

        Component description = cir.getReturnValue();
        if (!(description.getContents() instanceof TranslatableContents tc)) return;

        Integer color = ModTextColors.getColor(tc.getKey());
        if (color == null) return;

        MutableComponent result = cir.getReturnValue().plainCopy().setStyle(Style.EMPTY.withColor(color));
        cir.setReturnValue(result);
    }
}
