package cliffordha.totvw.entity;

import cliffordha.totvw.tag.ModBiomeTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

public class TConstants {
    public static int sec(int sec) {return sec * 20;}
    public static int min(int min) {return min * sec(60);}

    public static float triggerHeal(LivingEntity granter, LivingEntity grantee) {
        float triggerHeal;
        if (granter.level().getBiome(granter.blockPosition()).is(ModBiomeTags.IS_VERDANT_BIOMES)) {
            triggerHeal = Math.round((granter.getHealth() * 0.5f) + (grantee.getMaxHealth() * 0.5f));
        } else {
            triggerHeal = Math.round((granter.getHealth() * 0.5f) + (grantee.getMaxHealth() * 0.3f));}
        return triggerHeal;
    }

    public static int verdantBlessingCD(ServerLevel level) {
        int finalCD;
        switch (level.getDifficulty()) {
            case PEACEFUL -> finalCD = min(6);
            case EASY -> finalCD = min(12);
            case NORMAL -> finalCD = min(18);
            default -> finalCD = min(24);
        }
        return finalCD;
    }

    public static boolean isHealthHalf(LivingEntity entity) {
        return entity.getHealth() >= entity.getMaxHealth() * 0.5f;
    }

    public static void addParticle(Level level, BlockPos pos, ParticleOptions particle, int frequency) {
        for (int i = 0; i < (4 * (frequency + 1)); i++) {
            level.addParticle(particle, pos.getX(), pos.getY(), pos.getZ(), 0.0D, 0.0D, 0.0D);
        }
    }
    public static void addRandomPosParticle(Level level, BlockPos pos, ParticleOptions particle, int frequency, double xyz) {
        for (int i = 0; i < (4 * (frequency + 1)); i++) {
            level.addParticle(particle, pos.getX() + level.getRandom().nextDouble(), pos.getY() + level.getRandom().nextDouble(), pos.getZ() + level.getRandom().nextDouble(), xyz, xyz, xyz);
        }
    }

    public static void rewriteEffect(LivingEntity entity, Holder<MobEffect> effect, int sec, int amp) {
        if (entity.hasEffect(effect)) { entity.removeEffect(effect); }
        entity.addEffect(new MobEffectInstance(effect, sec, amp, false, false)); }

    public static void addEffect(LivingEntity entity, Holder<MobEffect> effect, int sec, int amp) {
        entity.addEffect(new MobEffectInstance(effect, sec, amp)); }

    public static void addHiddenEffect(LivingEntity entity, Holder<MobEffect> effect, int sec, int amp) {
        entity.addEffect(new MobEffectInstance(effect, sec, amp, false, false)); }

    public static void removeEffect(LivingEntity entity, Holder<MobEffect> effect) {
        if (entity.hasEffect(effect)) {entity.removeEffect(effect);} }


    public static int wolfEnchantmentLVL(Wolf wolf, ResourceKey<Enchantment> enchantment) {
        if (!wolf.isWearingBodyArmor()) return 0;
        return wolf.getItemBySlot(EquipmentSlot.BODY).getEnchantments()
                .getLevel(wolf.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantment));}

    public static int playerEnchantmentLVL(Wolf wolf, ResourceKey<Enchantment> enchantment) {
        LivingEntity player = wolf.getOwner();
        if (player == null) return 0;
        return player.getItemBySlot(EquipmentSlot.CHEST).getEnchantments()
                .getLevel(player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantment));
    }
}