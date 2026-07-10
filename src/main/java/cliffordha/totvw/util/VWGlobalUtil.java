package cliffordha.totvw.util;

import cliffordha.totvw.config.TOTVWConfig;
import cliffordha.totvw.entity.player.VWPlayerBehaviors;
import cliffordha.totvw.entity.skill.SkillUtil;
import cliffordha.totvw.entity.wolf.VWWolfBehaviors;
import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.tag.VWBiomeTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import static cliffordha.totvw.entity.skill.VWSkillProcessor.notifyFromPlayer;
import static cliffordha.totvw.entity.skill.VWSkillProcessor.notifyFromWolf;

public final class VWGlobalUtil {
    public static int sec(int sec) {return sec * 20;}
    public static int min(int min) {return min * sec(60);}

    public static float triggerHeal(LivingEntity granter, LivingEntity grantee) {
        float triggerHeal;
        if (granter.level().getBiome(granter.blockPosition()).is(VWBiomeTags.IS_VERDANT_BIOMES)) {
            triggerHeal = Math.round((granter.getHealth() * 0.5f) + (grantee.getMaxHealth() * 0.5f));
        } else {
            triggerHeal = Math.round((granter.getHealth() * 0.5f) + (grantee.getMaxHealth() * 0.3f));}
        return triggerHeal;
    }
    public static void verdantBlessingAfterEffects(ServerLevel level, LivingEntity entity) {
        if (!TOTVWConfig.get().attachmentSkillCD) return;
        int cooldown = setDifficultyBasedValue(level, min(3), min(9), min(15), min(21));
        if (isHalfHealth(entity)) {
            addHiddenEffect(entity, MobEffects.WEAKNESS, min(1), 0);
        } else {
            addHiddenEffect(entity, MobEffects.WEAKNESS, min(1), 1);
        }
        if (entity instanceof Wolf wolf) {
            SkillUtil.startCooldown(wolf, VWWolfBehaviors.VERDANT_BLESSING, cooldown);
            notifyFromWolf(wolf, VWColors.VERDANT_WIND_MUTED, "Cooldown: " + cooldown / min(1) + " minutes");
        } else if (entity instanceof Player player) {
            SkillUtil.startCooldown(player, VWPlayerBehaviors.VERDANT_BLESSING, cooldown);
            notifyFromPlayer(player, VWColors.VERDANT_WIND_MUTED, "Cooldown: " + cooldown / min(1) + " minutes");
        }
    }


    public static int setDifficultyBasedValue(ServerLevel level, int peacefulCD, int easyCD, int normalCD, int hardCD) {
        int finalCD;
        switch (level.getDifficulty()) {
            case PEACEFUL -> finalCD = peacefulCD;
            case EASY -> finalCD = easyCD;
            case NORMAL -> finalCD = normalCD;
            default -> finalCD = hardCD;
        }
        return finalCD;
    }

    public static float setDifficultyBasedValue(ServerLevel level, float peacefulCD, float easyCD, float normalCD, float hardCD) {
        float finalCD;
        switch (level.getDifficulty()) {
            case PEACEFUL -> finalCD = peacefulCD;
            case EASY -> finalCD = easyCD;
            case NORMAL -> finalCD = normalCD;
            default -> finalCD = hardCD;
        }
        return finalCD;
    }

    public static void playSound(LivingEntity entity, SoundEvent sound, SoundSource source) {
        if (!(entity.level() instanceof ServerLevel level)) return;
        var posX = entity.getX();
        var posY = entity.getY();
        var posZ = entity.getZ();
        var random = level.getRandom().nextFloat();
        level.playSound(null, posX, posY, posZ, sound, source, 0.5f + random, 0.5f + random);
    }

    public static boolean isHalfHealth(LivingEntity entity) {
        return entity.getHealth() >= entity.getMaxHealth() * 0.5f;
    }

    public static void addParticle(Level level, BlockPos pos, ParticleOptions particle, int frequency) {
        for (int i = 0; i < (4 * (frequency + 1)); i++) {
            level.addParticle(particle, pos.getX(), pos.getY(), pos.getZ(), 0.0D, 0.0D, 0.0D);
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