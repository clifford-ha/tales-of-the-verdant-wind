package cliffordha.totvw.entity.wolf;

import cliffordha.totvw.datagen.VWDamageTypes;
import cliffordha.totvw.effect.HavocEffect;
import cliffordha.totvw.entity.MobEnchantments;
import cliffordha.totvw.entity.skill.SkillCooldowns;
import cliffordha.totvw.entity.skill.SkillManager;
import cliffordha.totvw.entity.skill.WolfSkills;
import cliffordha.totvw.entity.skills.ArcticWindsEffect;
import cliffordha.totvw.entity.skills.RunestoneEffects;
import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.VWEffects;
import cliffordha.totvw.registry.VWSounds;
import cliffordha.totvw.registry.attachments.Runestone;
import cliffordha.totvw.registry.attachments.VWAttachments;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import cliffordha.totvw.util.DamageZip;
import cliffordha.totvw.util.ExtUtil;

import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jspecify.annotations.Nullable;

import java.util.List;

import static cliffordha.totvw.util.TimeUtil.tickSeconds;
import static cliffordha.totvw.util.ExtUtil.*;
import static cliffordha.totvw.util.ExtUtil.removeEffect;

public class WolfAttackEffects {
    public static void onPostAttack(Wolf wolf, ServerLevel level, @Nullable LivingEntity linkVictim, boolean enchantmentExclusive, DamageSource override) {
        var victim = enchantmentExclusive ? VWWolfBehaviors.CURRENT_VICTIM.get() : linkVictim;
        if (victim == null) return;
        LivingEntity player = wolf.getOwner();
        RandomSource random = wolf.getRandom();

        float victimHealth = victim.getHealth();
        float victimMaxHealth = victim.getMaxHealth();

        var enchantment = WolfEnchantments.of(wolf);

        DamageSource DMG_SOURCE_BLEEDING = VWDamageTypes.bleeding(level);

        float BLEEDING_CONSOLIDATED_DMG = 0;
        float SCORCHING_CONSOLIDATED_DMG = 0;

        List<Wolf> babyWolves = level.getEntitiesOfClass(
                Wolf.class,
                scanArea(wolf, 12),
                test -> WolfAttachment.isFamilyRelated(wolf, test) && test.isBaby());

        if (enchantment.hasMending() && enchantmentExclusive) {
            float conversion = enchantment.hasBenediction() ? 0.25f : 0.1f;
            int dmg = wolf.getAttribute(Attributes.ATTACK_DAMAGE) != null ? Mth.ceil(wolf.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * conversion) + 2 : Mth.ceil(2 * conversion) + 2;
            ItemStack itemStack = wolf.getBodyArmorItem();
            if (itemStack.isDamaged()) {
                int toRepairFromXpAmount = EnchantmentHelper.modifyDurabilityToRepairFromXp(level, itemStack, dmg);
                int repair = Math.min(toRepairFromXpAmount, itemStack.getDamageValue());
                itemStack.setDamageValue(itemStack.getDamageValue() - repair);
                if (random.nextFloat() < 0.33f) {
                    sendParticles(ParticleTypes.GLOW, level, wolf.blockPosition(), 3, 0.5);
                    playSound(wolf, SoundEvents.WOLF_ARMOR_REPAIR, SoundSource.NEUTRAL, true);
                }
            }
        }

        boolean HAS_ANY_DEFENSE = enchantment.hasBlastProtection() || enchantment.hasFireProtection() || enchantment.hasProjectileProtection() && enchantment.hasProtection();
        if (enchantmentExclusive && HAS_ANY_DEFENSE) {
            //CASE 4
            buffForBabyWolves(wolf, babyWolves, enchantment, 4);
        }

        if (enchantment.hasArcticWinds()) {
            ArcticWindsEffect.applyEffectsOnHit(wolf, victim, level, enchantment.getArcticWinds(), enchantmentExclusive);
        }

        if (enchantment.hasIgnition()) {
            int ACTIVE_IGNITION = enchantment.getIgnition();
            int burnTime = ACTIVE_IGNITION * 3;
            boolean isWithinHotBiomes = isInBiome(wolf, BiomeTags.IS_NETHER) || level.getBiome(wolf.blockPosition()).value().getBaseTemperature() > 1.8f;
            int computedTime = isWithinHotBiomes ? (burnTime * 2) : burnTime;
            int bonus = Runestone.hasHavoc(wolf) ? 3 : 0;
            if (ACTIVE_IGNITION >= 3) {
                removeEffect(victim, MobEffects.FIRE_RESISTANCE);
            }

            if (victim.fireImmune()) {
                float finalDMG = isWithinHotBiomes ? (computedTime + bonus) * 3 : ACTIVE_IGNITION * 2;
                SCORCHING_CONSOLIDATED_DMG += finalDMG;
            } else {
                victim.igniteForSeconds(computedTime + bonus);
            }
        }

        if (enchantment.hasPoisoning()) {
            int ACTIVE_POISONING = enchantment.getPoisoning();
            if (ACTIVE_POISONING >= 3) {
                removeEffect(victim, MobEffects.REGENERATION);
            }
            addOrStackEffect(victim, MobEffects.POISON, (ACTIVE_POISONING * 2), Math.min(ACTIVE_POISONING - 1, 2), true);
        }

        if (enchantment.hasWithering()) {
            int ACTIVE_WITHERING = enchantment.getWithering();
            if (ACTIVE_WITHERING >= 3) {
                removeEffect(victim, MobEffects.REGENERATION);
            }
            rewriteEffect(victim, MobEffects.WITHER, ACTIVE_WITHERING * 2, ACTIVE_WITHERING);
            if (victim.isInvulnerableTo(level, level.damageSources().wither())) {
                BLEEDING_CONSOLIDATED_DMG += victimHealth * 0.5f;
            }
        }

        if (enchantment.hasLifting()) {
            int ACTIVE_LIFTING = enchantment.getLifting();
            if (victim.hasEffect(MobEffects.LEVITATION)) {
                double str = level.getRandom().nextDouble();
                victim.knockback(str, ACTIVE_LIFTING, str, level.damageSources().flyIntoWall(), ACTIVE_LIFTING);
            } else {
                addHiddenEffect(victim, MobEffects.LEVITATION, 10, ACTIVE_LIFTING * 3);
            }
        }

        if (enchantment.hasBloodLust() && enchantmentExclusive) {
            int ACTIVE_BLOODLUST = enchantment.getBloodlust();
            int paralyzeTime = 3 + (tickSeconds(ACTIVE_BLOODLUST * 3));

            addEffect(wolf, VWEffects.BLOODLUST, 6, ACTIVE_BLOODLUST - 1);

            if (wolf.hasEffect(VWEffects.BLOODLUST)) {
                addOrStackEffect(victim, MobEffects.SLOWNESS, 2, 0, true);
                if (ACTIVE_BLOODLUST >= 3) {
                    removeEffect(victim, MobEffects.SPEED);
                }
            }
            addOrStackEffect(victim, MobEffects.WEAKNESS, 2, 0, true);
            boolean checkVictim = victim.is(EntityTypes.PLAYER) || victim.getMaxHealth() > 20.0;
            boolean isNotParalyzed = !victim.hasEffect(VWEffects.PARALYZE) || victim.getAttachedOrElse(VWAttachments.FROST_BOUND_TIMER, 0) <= 0;
            if (checkVictim && !SkillManager.isOnCooldown(wolf, WolfSkills.PARALYZER) && isNotParalyzed) {
                addHiddenEffect(victim, VWEffects.PARALYZE, paralyzeTime, 0);

                SkillManager.startCooldown(wolf, WolfSkills.PARALYZER, cooldownFromBase(level, SkillCooldowns.BASE_PARALYZE));

                sendToChat(wolf, VWColors.MIGHT, victim.getPlainTextName() + " has been paralyzed for " + (paralyzeTime) + " seconds by " + wolfName(wolf) + "!");
                ExtUtil.playSound(victim, VWSounds.WOLF_SKILL_PARALYZE, SoundSource.HOSTILE, 0.1f, 0.55f + level.getRandom().nextFloat(), false);
            }
        }

        if (enchantment.hasOozing() && enchantmentExclusive) {
            int defaultTime;
            if (enchantment.getBloodlust() > 0) {
                defaultTime = (int) ((enchantment.getBloodlust() * 1.50) * 60);
            } else if (enchantment.getMight() > 0) {
                defaultTime = (int) ((enchantment.getMight() * 1.25) * 60);
            } else {
                defaultTime = 60;
            }
            addOrStackEffect(victim, MobEffects.OOZING, defaultTime, 1, false);
        }

        if (enchantment.hasMight()) {
            int ACTIVE_MIGHT = enchantment.getMight();
            boolean isHigh = random.nextFloat() < ACTIVE_MIGHT * 0.15f;
            int amp = victimHealth > wolf.getHealth() * 0.5f && isHigh ? 2 : 0;
            addEffect(wolf, MobEffects.ABSORPTION, Math.clamp(Mth.ceil(victimHealth * 0.10f), 2, 30), amp);
            if (ACTIVE_MIGHT >= 3) {
                removeEffect(victim, MobEffects.RESISTANCE);
                boolean checkHealth = victimHealth > 3 && victimHealth <= victimMaxHealth * 0.6f;
                if (checkHealth && !SkillManager.isOnCooldown(wolf, WolfSkills.RUPTURE)) {
                    float finalDMG;
                    if (player != null) {
                        if (wolf.distanceTo(player) < 4) {
                            finalDMG = wolf.getHealth() * 0.2f;
                        } else {
                            finalDMG = wolf.getHealth() * 0.35f;
                        }
                    } else {
                        finalDMG = wolf.getMaxHealth() * 1.8f;
                    }
                    float decreaseTime = enchantment.hasBenediction() ? 0.75f: 1.0f;
                    int finalCD = cooldownFromBase(level, SkillCooldowns.BASE_RUPTURE, decreaseTime);
                    BLEEDING_CONSOLIDATED_DMG += finalDMG;
                    sendParticles(ParticleTypes.EXPLOSION_EMITTER, level, victim.blockPosition().below(), 1, 0);
                    playSound(victim, SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, true);
                    SkillManager.startCooldown(wolf, WolfSkills.RUPTURE, finalCD);
                }
            }
            if (ACTIVE_MIGHT >= 4) {
                removeEffect(victim, MobEffects.ABSORPTION);
            }
            if (ACTIVE_MIGHT >= 5) {
                removeEffect(victim, MobEffects.STRENGTH);
                BLEEDING_CONSOLIDATED_DMG += wolf.getMaxHealth() * 0.10f;
            }
        }

        if (enchantment.hasGnawing() && enchantmentExclusive) {
            float heal = enchantment.getGnawing() == 1 ? wolf.getMaxHealth() * 0.15f : wolf.getMaxHealth() * 0.30f;
            wolf.heal(heal);

            // CASE 3
            buffForBabyWolves(wolf, babyWolves, enchantment, 3);

            if (player != null && player.getHealth() < player.getMaxHealth()) {
                MobEnchantments mobEnchantments = MobEnchantments.of(player);

                float healStrength = getHealStrength(mobEnchantments);
                float baseCap = enchantment.getGnawing() > 1 ? player.getMaxHealth() * 0.4f : player.getMaxHealth() * 0.2f;
                player.heal(Math.min(baseCap, healStrength));

            }
        }
        applyFinalDamage(level, wolf, new DamageZip(DMG_SOURCE_BLEEDING, BLEEDING_CONSOLIDATED_DMG, victim), enchantmentExclusive);
        applyFinalDamage(level, wolf, new DamageZip(VWDamageTypes.scorchingHeat(level), SCORCHING_CONSOLIDATED_DMG, victim), enchantmentExclusive);
    }

    public static void onTick(Wolf wolf, ServerLevel level) {
        boolean armor = !wolf.getBodyArmorItem().getEnchantments().isEmpty();
        if (armor) {
            armorTick(wolf, level);
        }
        Runestone type = wolf.getAttachedOrElse(RunestoneEffects.RUNESTONE_TYPE, Runestone.EMPTY);
        RunestoneEffects.processRunestones(wolf, level, type);
        if (type != Runestone.EMPTY) {
            ParticleOptions t;
            int n;
            if (type == Runestone.TETHER) {
                t = ParticleTypes.ENCHANT;
                n = 4;
            } else if (type == Runestone.HAVOC) {
                t = HavocEffect.HAVOC_PARTICLE;
                n = 6;
            } else if (type == Runestone.EFFLORESCENCE) {
                t = ParticleTypes.GLOW;
                n = 2;
            } else {
                t = null;
                n = 0;
            }
            if (t != null) {
                sendParticles(t, level, wolf.blockPosition(), n, 0.5);
            }
        }
    }
    private static void armorTick(Wolf wolf, ServerLevel level) {
        LivingEntity player = wolf.getOwner();

        var enchantment = WolfEnchantments.of(wolf);

        boolean ACTIVE_BENEDICTION = enchantment.hasBenediction();
        int ACTIVE_IGNITION = enchantment.getIgnition();
        int ACTIVE_MIGHT = enchantment.getMight();
        int ACTIVE_FIRE_PROTECTION = enchantment.getFireProtection();

        List<Wolf> babyWolves = level.getEntitiesOfClass(
                Wolf.class,
                scanArea(wolf, 12),
                test -> WolfAttachment.isFamilyRelated(wolf, test) && test.isBaby()
        );
        if (enchantment.hasArcticWinds()) {
            ArcticWindsEffect.run(wolf, level, enchantment.getArcticWinds());
        } else {
            ArcticWindsEffect.removeResidualStacks(wolf);
        }

        if (ACTIVE_BENEDICTION && ACTIVE_IGNITION > 0 && ACTIVE_FIRE_PROTECTION >= 3 && isInBiome(wolf, BiomeTags.IS_NETHER)) {
            addHiddenEffect(wolf, MobEffects.FIRE_RESISTANCE, 3, 8);
            if (player != null && isInBiome(player, BiomeTags.IS_NETHER) && wolf.distanceTo(player) < 24) addHiddenEffect(player, MobEffects.FIRE_RESISTANCE, 3, 8);

            // CASE 2
            buffForBabyWolves(wolf, babyWolves, enchantment, 2);
        }
        if (ACTIVE_MIGHT > 0 && wolf.getTarget() != null) {
            addEffect(wolf, VWEffects.AMPLIFIED_MIGHT, ACTIVE_MIGHT * 3, Math.min(ACTIVE_MIGHT - 1, 2));

            // CASE 1
            buffForBabyWolves(wolf, babyWolves, enchantment, 1);
        }
        if (ACTIVE_IGNITION > 0 || ACTIVE_MIGHT > 3) {
            if (wolf.isOnFire()) {
                wolf.extinguishFire();
            }
        }
    }
    public static void applyFinalDamage(ServerLevel level, Wolf wolf, LivingEntity target, DamageSource source, float amount, boolean isFromEnchantment) {
        if (target == null) return;
        if (!isFromEnchantment) {
            double damage = wolf.getAttributeValue(Attributes.ATTACK_DAMAGE);
            target.hurtServer(level, source, (float) damage);
        } else {
            if (amount <= 0) return;
            target.hurtServer(level, source, amount);
        }
    }
    public static void applyFinalDamage(ServerLevel level, Wolf wolf, DamageZip zip, boolean isFromEnchantment) {
        if (zip.target() == null) return;
        if (!isFromEnchantment) {
            double damage = wolf.getAttributeValue(Attributes.ATTACK_DAMAGE);
            DamageZip.apply(zip, level, (float) damage);
        } else {
            DamageZip.apply(zip, level);
        }
    }
    private static void buffForBabyWolves(Wolf parent, List<Wolf> babyWolves, WolfEnchantments enchants, int stat) {
        if (babyWolves.isEmpty()) return;
        for (Wolf wolf : babyWolves) {
            switch (stat) {
                //ARMOR TICK
                case 1 -> {
                    addEffect(wolf, VWEffects.AMPLIFIED_MIGHT, enchants.getMight() * 3, Math.min(enchants.getMight() - 1, 2));
                    addEffect(wolf, MobEffects.ABSORPTION, enchants.getMight() * 3, 1);
                }
                case 2 -> {
                    if (isInBiome(wolf, BiomeTags.IS_NETHER) && wolf.distanceTo(parent) < 24) {
                        addHiddenEffect(wolf, MobEffects.FIRE_RESISTANCE, 3, 8);
                    }
                }

                // ON-ATTACK
                case 3 -> {
                    float heal = enchants.getGnawing() == 1 ? wolf.getMaxHealth() * 0.15f : wolf.getMaxHealth() * 0.30f;
                    wolf.heal(heal);
                }
                case 4 -> {
                    Holder<MobEffect> effect;
                    int amp;
                    if (enchants.getBlastProtection() > 0 || enchants.getProtection() > 0) {
                        amp = enchants.getBlastProtection() > 0 ? 2 : 0;
                        effect = MobEffects.RESISTANCE;
                    } else {
                        amp = 1;
                        effect = MobEffects.FIRE_RESISTANCE;
                    }
                    addEffect(wolf, effect, enchants.getMight() * 3, amp);
                }
                default -> {
                }
            }
        }
    }

    private static float getHealStrength(MobEnchantments enchants) {
        float healStrength;
        if (enchants.blastProtection() > 0) {
            healStrength = enchants.blastProtection() * 1.6f;

        } else if (enchants.fireProtection() > 0) {
            healStrength = enchants.fireProtection() * 1.3f;

        } else if (enchants.projectileProtection() > 0 || enchants.protection() > 0) {
            healStrength = enchants.projectileProtection() + enchants.protection();

        } else {
            healStrength = 1f;
        }

        if (enchants.hasBenediction()) healStrength *= 1.20f;
        return healStrength;
    }
    private static String wolfName(Wolf wolf) {
        return VWWolfBehaviors.wolfName(wolf);
    }
}
