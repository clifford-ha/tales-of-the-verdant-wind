package cliffordha.totvw.mixin.misc;

import cliffordha.totvw.util.VWColorizeTextMixin;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.TippedArrowItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TippedArrowItem.class)
public class ArrowNameColorMixin {

    @Inject(method = "getName", at = @At("RETURN"), cancellable = true)
    private void colorizeEffectName(CallbackInfoReturnable<Component> cir) {

        Component description = cir.getReturnValue();
        if (!(description.getContents() instanceof TranslatableContents tc)) return;

        Integer color = VWColorizeTextMixin.getColor(tc.getKey());
        if (color == null) return;

        MutableComponent result = cir.getReturnValue().plainCopy().setStyle(Style.EMPTY.withColor(color));
        cir.setReturnValue(result);
    }
}
