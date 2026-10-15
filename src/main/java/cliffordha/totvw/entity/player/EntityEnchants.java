package cliffordha.totvw.entity.player;

import cliffordha.totvw.registry.VWEnchantments;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantments;

import static cliffordha.totvw.registry.VWEnchantments.entityEnchantmentLVL;

public record EntityEnchants(
        boolean benediction,

        //from vanilla
        int protection,
        int fireProtection,
        int projectileProtection,
        int blastProtection
) {
    public static EntityEnchants of(LivingEntity entity) {
        return new EntityEnchants(
                VWEnchantments.getBenediction(entity),

                entityEnchantmentLVL(entity, Enchantments.PROTECTION),
                entityEnchantmentLVL(entity, Enchantments.FIRE_PROTECTION),
                entityEnchantmentLVL(entity, Enchantments.PROJECTILE_PROTECTION),
                entityEnchantmentLVL(entity, Enchantments.BLAST_PROTECTION)
        );
    }
}
