package cliffordha.totvw.enchantment;

import cliffordha.totvw.datagen.VWDamageTypes;
import cliffordha.totvw.registry.VWParticles;
import cliffordha.totvw.util.TextPopup;
import cliffordha.totvw.entity.wolf.WolfEnchantmentRecord;
import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.VWEnchantments;
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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;

import java.util.List;

import static cliffordha.totvw.util.ExtUtil.*;
import static cliffordha.totvw.util.ExtUtil.random;

public class ArcticWindsEffect {
    public static void run(Wolf wolf, ServerLevel level, int enchantmentLevel) {
        applyEnvironmentEffects(wolf, level);
        if (flashFreezeStack(wolf) < 100) {
            int generate = isInBiome(wolf, BiomeTags.SPAWNS_COLD_VARIANT_FARM_ANIMALS) ? 2 : 1;
            int add = level.getRandom().nextFloat() < enchantmentLevel * 0.10f ? 1 : 0;
            setFlashFreeze(wolf, generate + add);
        }
        List<Wolf> nearby = level.getEntitiesOfClass(
                Wolf.class,
                scanArea(wolf, 12),
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
    public static void applyEffectsOnHit(Wolf wolf, LivingEntity victim, ServerLevel level, int enchantmentLevel, boolean exclusive) {
        RandomSource random = level.getRandom();
        int flashFreeze = flashFreezeStack(wolf);
        int trigger = inColdEnv(victim) ? 1 : 2;
        int applyAffliction = inColdEnv(victim) ? 2 : 1;

        if (flashFreeze > 0 && afflictionStack(victim) < 24) {
            setAffliction(victim, applyAffliction + Math.min(enchantmentLevel, 3));
            setFlashFreeze(wolf, -1);
        }

        int scorching = victim.getAttachedOrElse(VWAttachment.SCORCHING_HEAT, 0);
        if (scorching >= 3) {
            applyThermalBurn(level, wolf, victim, scorching * trigger);

        } else {
            boolean applyFrostBound = flashFreeze > 0 && random.nextFloat() < enchantmentLevel * 0.21f;
            if (victim.isInWaterOrRain()) applyFrostBound = random.nextFloat() < 0.8f;

            if (applyFrostBound) {
                TextPopup.showBold(victim, "Frostbound!", VWColors.ARCTIC_WINDS);
                setFrostBound(victim, 1);
                if (exclusive) setFlashFreeze(wolf, -trigger);
                setAffliction(victim, -trigger);
                playSound(victim, SoundEvents.PLAYER_HURT_FREEZE, SoundSource.HOSTILE, false);
            }
        }

        int affliction = afflictionStack(victim);
        if (affliction > 0) {
            if (isDimension(victim, VWDimensions.NETHER)) {
                if (random.nextFloat() < affliction * 0.06 && affliction >= 9) {
                    float dmg = Math.max(affliction + trigger, affliction * 0.6f);
                    applyThermalBurn(level, wolf, victim, getDMGValue(victim, dmg));
                }
            } else {
                if (random.nextFloat() < affliction * 0.025) {
                    int afflictionReduction = inColdEnv(victim) ? -5 : -7;

                    if (victim.isOnFire() || victim.isInLava()) {
                        applyThermalBurn(level, wolf, victim, Math.max(affliction + trigger, 4));

                    } else {
                        float damage = getDMGValue(victim, Math.clamp(random.nextInt(affliction), 2, 6) + trigger);
                        float size = 1.3f + random.nextFloat();
                        victim.hurtServer(level, VWDamageTypes.winterShock(level), damage);
                        TextPopup.showBold(wolf, victim, "Wintershock! " + Mth.floor(damage), VWColors.ARCTIC_WINDS, size);
                        setAffliction(victim, afflictionReduction);
                        sendParticles(VWParticles.EXT_SNOWFLAKE, level, victim.blockPosition(), 2, 1.5);
                    }
                }
            }
        }
        setFlashFreeze(wolf, -trigger);
    }
    public static void applyThermalBurn(ServerLevel level, Wolf wolf, LivingEntity entity, float initial) {
        float damage =  getDMGValue(entity, initial);
        entity.hurtServer(level, VWDamageTypes.thermalBurn(level), damage);
        setAffliction(entity, -2);
        int scorched = entity.getAttachedOrElse(VWAttachment.SCORCHING_HEAT, 0);
        if (scorched >= 3) {
            entity.setAttached(VWAttachment.SCORCHING_HEAT, scorched - 3);
        }

        if (damage < 10) {
            TextPopup.show(wolf, entity, "Thermal Burn! " + Mth.floor(damage), VWColors.IGNITION);
        } else {
            TextPopup.showBold(wolf, entity, "Thermal Burn! " + Mth.floor(damage), VWColors.IGNITION, 1.3f + random.nextFloat());
        }

        sendParticles(ParticleTypes.LAVA, level, entity.blockPosition(), 9, 0.5);
        playSound(entity, SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL, false);

    }
    private static float getDMGValue(LivingEntity entity, float initial) {
        int affliction = afflictionStack(entity);
        if (affliction > 16 || frostBoundStack(entity) > 0 && random.nextBoolean()) {
            return initial * 2.3f;
        }
        return initial;
    }

    public static void removeResidualStacks(Wolf wolf) {
        if (!isInBiome(wolf, BiomeTags.SPAWNS_COLD_VARIANT_FARM_ANIMALS)) {
            setFlashFreeze(wolf, -1);
        }
    }
    private static void applyEnvironmentEffects(Wolf wolf, ServerLevel level) {
        int extra = VWEnchantments.getArcticWinds(wolf);
        List<Wolf> wolves = level.getEntitiesOfClass(Wolf.class,
                scanArea(wolf, 8),
                test -> VWEnchantments.getArcticWinds(test) > 0);
        sendParticles(ParticleTypes.SNOWFLAKE, level, wolf.blockPosition(), Math.clamp(25 - (wolves.size() * 2L), 4, 24) + extra, 8.0 + extra, -5.0f);
    }

    public static boolean negateFireDamageForOwner(Player owner, ServerLevel level, DamageSource source, float dmg) {
        boolean isHurtFromFire = owner.isOnFire() && !owner.isInvulnerableTo(level, source);

        if (isHurtFromFire) {
            List<Wolf> wolves = level.getEntitiesOfClass(Wolf.class,
                    scanArea(owner, 8),
                    wolf -> VWEnchantments.getArcticWinds(wolf) >= 3
                            && flashFreezeStack(wolf) > 10
                            && wolf.getOwner() == owner);

            if (!wolves.isEmpty()) {
                Wolf wolf = wolves.getFirst();
                if (level.getRandom().nextFloat() < 0.7f) {
                    setFlashFreeze(wolf, - Math.clamp(Mth.ceil(dmg), 1, 3));
                    playSound(owner, SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, false);
                }
                owner.extinguishFire();
                return true;
            }
        }
        return false;
    }
    public static boolean negateFreezingOrFireDMG(Wolf wolf, DamageSource source) {
        var enchants = WolfEnchantmentRecord.of(wolf);
        if (source.is(DamageTypeTags.IS_FREEZING)) {
            return enchants.hasArcticWinds() || enchants.getIgnition() >= 3;

        } else {
            if (wolf.isOnFire() && !wolf.isInLava()) {
                return enchants.getArcticWinds() >= 3 || enchants.hasIgnition();

            } else if (wolf.isInLava()) {
                if (flashFreezeStack(wolf) > 0) {
                    if (random.nextFloat() < 0.7f) setFlashFreeze(wolf, -1);
                    wolf.extinguishFire();
                    playSound(wolf, SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, false);
                    return true;

                } else {
                    return enchants.getIgnition() >= 3 && random.nextFloat() < 0.8f;
                }
            }
        }
        return false;
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
    public static int frostBoundStack(Entity entity) {
        if (!(entity instanceof LivingEntity)) return 0;
        return entity.getAttachedOrElse(VWAttachment.FROST_BOUND_TIMER, 0);
    }
}
