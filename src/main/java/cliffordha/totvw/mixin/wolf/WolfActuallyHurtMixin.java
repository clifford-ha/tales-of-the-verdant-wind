package cliffordha.totvw.mixin.wolf;

import cliffordha.totvw.config.TOTVWConfig;
import cliffordha.totvw.registry.ModColors;
import cliffordha.totvw.registry.ModEnchantments;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Crackiness;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static cliffordha.totvw.entity.TConstants.wolfEnchantmentLVL;
import static cliffordha.totvw.entity.skill.ConfigTools.notifyFromWolf;

@Mixin(Wolf.class)
public abstract class WolfActuallyHurtMixin extends LivingEntity {

    protected WolfActuallyHurtMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "actuallyHurt", at = @At("HEAD"), cancellable = true)
    private void totvw$actuallyHurt(ServerLevel level, DamageSource source, float damage, CallbackInfo ci) {
        Wolf wolf = (Wolf) (Object) this;

        int ACTIVE_MIGHT = wolfEnchantmentLVL(wolf, ModEnchantments.WOLF_EFFECT_MIGHT);
        int ACTIVE_BENEDICTION = wolfEnchantmentLVL(wolf, ModEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS);
        int ACTIVE_ENHANCEMENT_KIT = wolfEnchantmentLVL(wolf, ModEnchantments.WOLF_ARMOR_ENHANCEMENT_KIT);

        if (TOTVWConfig.get().wolfArmorDamageDistribution) {
            if (!wolf.canArmorAbsorb(source)) return;

            ItemStack armor = wolf.getBodyArmorItem();
            int damageBefore = armor.getDamageValue();
            int maxDamage = armor.getMaxDamage();

            float computedDMG;
            if (ACTIVE_BENEDICTION > 0) {
                computedDMG = damage * 0.6f;
            } else if (ACTIVE_ENHANCEMENT_KIT > 0) {
                computedDMG = damage * 0.7f;
            } else if (ACTIVE_MIGHT > 0) {
                computedDMG = damage * 0.8f;
            } else {
                computedDMG = damage;
            }

            float finalArmorDMG = computedDMG * 0.75f;
            float finalWolfDMG = computedDMG * 0.25f;

            armor.hurtAndBreak(Mth.ceil(finalArmorDMG), wolf, EquipmentSlot.BODY);
            if (Crackiness.WOLF_ARMOR.byDamage(damageBefore, maxDamage) != Crackiness.WOLF_ARMOR.byDamage(wolf.getBodyArmorItem())) {
                wolf.playSound(SoundEvents.WOLF_ARMOR_CRACK);
                level.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, Items.ARMADILLO_SCUTE), wolf.getX(), wolf.getY() + 1.0, wolf.getZ(), 20, 0.2, 0.1, 0.2, 0.1);
            }

            super.actuallyHurt(level, source, finalWolfDMG);

            if (TOTVWConfig.get().sendLog) { notifyFromWolf(wolf, ModColors.DEFAULT_MUTED, "TrueDMG: " + damage + " §e| FinalWolfDMG: " + finalWolfDMG + " §d| FinalArmorDMG: " + finalArmorDMG); }
            ci.cancel();
        }
    }
}