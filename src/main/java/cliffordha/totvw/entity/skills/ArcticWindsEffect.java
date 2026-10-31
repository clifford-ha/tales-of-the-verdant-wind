package cliffordha.totvw.entity.skills;

import cliffordha.totvw.datagen.VWDamageTypes;
import cliffordha.totvw.entity.wolf.WolfEnchantments;
import cliffordha.totvw.registry.VWEnchantments;
import cliffordha.totvw.registry.VWParticles;
import cliffordha.totvw.registry.attachments.VWAttachment;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import cliffordha.totvw.util.ExtUtil;
import cliffordha.totvw.worldgen.dimension.VWDimensions;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;

import java.util.List;

import static cliffordha.totvw.util.ExtUtil.*;

public class ArcticWindsEffect {
    public static void run(Wolf wolf, ServerLevel level, int enchantmentLevel) {
        applyEnvironmentEffects(wolf, level);
        if (flashFreezeStack(wolf) < 50) {
            int generate = isInBiome(wolf, BiomeTags.SPAWNS_COLD_VARIANT_FARM_ANIMALS) ? 2 : 1;
            int add = level.getRandom().nextFloat() < enchantmentLevel * 0.10f ? 1 : 0;
            setFlashFreeze(wolf, generate + add);
        }
        List<Wolf> nearby = level.getEntitiesOfClass(
                Wolf.class,
                ExtUtil.scanArea(wolf, 12),
                test -> test.getOwner() == wolf.getOwner()
        );
        if (afflictionStack(wolf) > 0 && VWEnchantments.getArcticWinds(wolf) > 2) {
            setFlashFreeze(wolf, afflictionStack(wolf));
            wolf.setAttached(VWAttachment.ARCTIC_AFFLICTION, 0);

            if (nearby.isEmpty()) return;
            for (Wolf afflicted : nearby) {
                getStacksFromAfflictedFriendlyWolves(wolf, afflicted);
            }
        }
    }
    private static void getStacksFromAfflictedFriendlyWolves(Wolf wolf, Wolf afflicted) {
        setFlashFreeze(wolf, afflictionStack(afflicted));
        afflicted.removeAttached(VWAttachment.ARCTIC_AFFLICTION);
    }
    public static void applyEffectsOnHit(Wolf wolf, LivingEntity victim, ServerLevel level, int enchantmentLevel, boolean enchantmentExclusive) {
        int flashFreeze = flashFreezeStack(wolf);
        int trigger = inColdEnv(victim) ? 2 : 3;
        trigger = enchantmentExclusive ? trigger : 2;
        int applyAffliction = inColdEnv(victim) ? 2 : 1;
        int add = level.getRandom().nextFloat() < enchantmentLevel * 0.10f ? 1 : 0;
        if (flashFreeze > 0 && afflictionStack(victim) < 24) {
            setAffliction(victim, applyAffliction + add);
            setFlashFreeze(wolf, -trigger);
        }
        boolean applyFrostBound = random.nextFloat() < enchantmentLevel * 0.15f && flashFreeze >= trigger && afflictionStack(victim) >= trigger;
        if (victim.isInWaterOrRain()) {
            applyFrostBound = random.nextFloat() < 0.8f;
        }
        if (applyFrostBound) {
            setFrostBound(victim, 1);
            setFlashFreeze(wolf, -trigger);
            setAffliction(victim, -trigger);
            if (enchantmentExclusive) {
                playSound(victim, SoundEvents.PLAYER_HURT_FREEZE, SoundSource.HOSTILE, false);
            }
        }

        // DEV
        //sendToChat(wolf.getOwner(), false, "Affliction Stack: " + afflictionStack(victim) + " | Frost Bound Stack: " + frostBoundStack(victim));
    }
    public static void removeResidualStacks(Wolf wolf) {
        if (!isInBiome(wolf, BiomeTags.SPAWNS_COLD_VARIANT_FARM_ANIMALS)) {
            setFlashFreeze(wolf, -1);
        }
    }

    private static void applyEnvironmentEffects(Wolf wolf, ServerLevel level) {
        List<Wolf> wolves = level.getEntitiesOfClass(Wolf.class,
                scanArea(wolf, 8),
                test -> VWEnchantments.getArcticWinds(test) > 0);
        sendParticles(VWParticles.EXT_SNOWFLAKE, level, wolf.blockPosition(), Math.clamp(25 - (wolves.size() * 2L), 4, 24), 8.0, -5.0f);
    }
    public static boolean negateFireDamageForOwner(Player owner, ServerLevel level, DamageSource source, float dmg) {
        if (!source.is(DamageTypeTags.IS_FIRE)) return false;
        boolean isHurtFromFire = owner.isOnFire() && !owner.isInvulnerableTo(level, source);

        if (isHurtFromFire) {
            List<Wolf> wolves = level.getEntitiesOfClass(Wolf.class,
                    scanArea(owner, 16),
                    wolf -> VWEnchantments.getArcticWinds(wolf) >= 3
                            && flashFreezeStack(wolf) > dmg
                            && wolf.getOwner() == owner);

            if (!wolves.isEmpty()) {
                Wolf wolf = wolves.getFirst();
                if (level.getRandom().nextFloat() < 0.15f) {
                    setFlashFreeze(wolf, -(int)dmg);
                    playSound(owner, SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, false);
                    owner.extinguishFire();
                }
                return true;
            }
        }
        return false;
    }
    public static boolean negateFreezingOrFireDMG(Wolf wolf, DamageSource source) {
        var enchants = WolfEnchantments.of(wolf);
        if (source.is(DamageTypeTags.IS_FREEZING)) {
            return enchants.hasArcticWinds() || enchants.getIgnition() >= 3;

        } else if (wolf.isInLava()) {
            if (flashFreezeStack(wolf) > 0) {
                if (random.nextFloat() < 0.8f) setFlashFreeze(wolf, -1);
                wolf.extinguishFire();
                playSound(wolf, SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, false);
                return true;

            } else {
                return enchants.getIgnition() > 0;
            }
        }
        return false;
    }

    public static void afflictionTick(ServerLevel level, LivingEntity entity) {
        int affliction = afflictionStack(entity);
        RandomSource random = level.getRandom();
        int trigger = inColdEnv(entity) ? 2 : 3;

        if (isDimension(entity, VWDimensions.NETHER)) {
            if (random.nextFloat() < affliction * 0.035 && affliction >= 9) {
                float dmg = Math.max(affliction + trigger, affliction * 0.6f);
                entity.hurtServer(level, VWDamageTypes.thermalBurn(level), getDMGValue(entity, dmg));
                setAffliction(entity, -9);
                playSound(entity, SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL, false);
                sendParticles(ParticleTypes.SMOKE, level, entity.blockPosition(), 3, 0.5f);
            }
        } else {
            if (random.nextFloat() < affliction * 0.025f) {
                setFrostBound(entity, 1);
                playSound(entity, SoundEvents.PLAYER_HURT_FREEZE, SoundSource.NEUTRAL, false);
            }
            if (affliction > trigger && random.nextFloat() < affliction * 0.025) {
                float dmg;
                int afflictionReduction = inColdEnv(entity) ? -5 : -7;
                DamageSource source;

                if (entity.isOnFire() || entity.isInLava()) {
                    entity.extinguishFire();
                    dmg = Math.max(affliction + trigger, 4);
                    source = VWDamageTypes.thermalBurn(level);
                } else {
                    dmg = Math.clamp(random.nextInt(affliction), 2, 6) + trigger;
                    source = VWDamageTypes.winterShock(level);
                }

                entity.hurtServer(level, source, getDMGValue(entity, dmg));
                setAffliction(entity, afflictionReduction);
            }
        }
    }
    private static float getDMGValue(LivingEntity entity, float initial) {
        int affliction = afflictionStack(entity);
        if (affliction > 16 || frostBoundStack(entity) > 0 && random.nextBoolean()) {
            return initial * 2.3f;
        }
        return initial;
    }

    public static boolean inColdEnv(LivingEntity entity) {
        return isInBiome(entity, BiomeTags.SPAWNS_COLD_VARIANT_FARM_ANIMALS) || isDimension(entity, VWDimensions.END);
    }
    public static void setFrostBound(LivingEntity entity, int amount) {
        setStack(entity, VWAttachment.FROST_BOUND_TIMER, amount, 3);
        entity.setAttached(VWAttachment.FROST_BOUND_POS, entity.blockPosition());
    }
    public static void setAffliction(LivingEntity entity, int amount) {
        setStack(entity, VWAttachment.ARCTIC_AFFLICTION, amount, 24);
    }
    public static void setFlashFreeze(Wolf wolf, int amount) {
        setStack(wolf, WolfAttachment.FLASH_FREEZE_STACK, amount, 50);
    }
    private static void setStack(LivingEntity entity, AttachmentType<Integer> type, int amount, int limit) {
        int current = entity.getAttachedOrElse(type, 0);
        entity.setAttached(type, Math.min((current + (amount)), limit));
        if (entity.getAttachedOrElse(type, 0) <= 0) {
            if (type == VWAttachment.FROST_BOUND_TIMER) {
                entity.removeAttached(VWAttachment.FROST_BOUND_POS);
            }
            entity.removeAttached(type);
        }
    }
    public static int flashFreezeStack(Wolf wolf) {
        return wolf.getAttachedOrElse(WolfAttachment.FLASH_FREEZE_STACK, 0);
    }
    public static int afflictionStack(LivingEntity entity) {
        return entity.getAttachedOrElse(VWAttachment.ARCTIC_AFFLICTION, 0);
    }
    public static int frostBoundStack(LivingEntity entity) {
        return entity.getAttachedOrElse(VWAttachment.FROST_BOUND_TIMER, 0);
    }
}
