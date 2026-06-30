package cliffordha.totvw.mixin.misc;

import cliffordha.totvw.util.ModTextColors;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class EnchantmentNameColorMixin {

    @Inject(method = "getFullname", at = @At("RETURN"), cancellable = true)
    private static void totvw$colorizeEnchantmentName(Holder<Enchantment> enchantment, int level, CallbackInfoReturnable<Component> cir) {

        Component description = enchantment.value().description();
        if (!(description.getContents() instanceof TranslatableContents tc)) return;

        Integer color = ModTextColors.getColor(tc.getKey());
        if (color == null) return;

        MutableComponent result = cir.getReturnValue().plainCopy().setStyle(Style.EMPTY.withColor(color));
        cir.setReturnValue(result);
    }
}