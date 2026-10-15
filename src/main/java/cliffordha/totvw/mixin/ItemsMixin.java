package cliffordha.totvw.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Items.class)
public class ItemsMixin {
    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item$Properties;wolfArmor(Lnet/minecraft/world/item/equipment/ArmorMaterial;)Lnet/minecraft/world/item/Item$Properties;"))
    private static Item.Properties modifyWolfArmor(Item.Properties original) {
        return original.enchantable(10);
    }
}
