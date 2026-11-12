package cliffordha.totvw.entity.wolf;

import cliffordha.totvw.datagen.VWDamageTypes;
import cliffordha.totvw.effect.HavocEffect;
import cliffordha.totvw.enchantment.WitheringEffect;
import cliffordha.totvw.entity.MobEnchantmentRecord;
import cliffordha.totvw.entity.skill.*;
import cliffordha.totvw.enchantment.ArcticWindsEffect;
import cliffordha.totvw.enchantment.IgnitionEffect;
import cliffordha.totvw.entity.skills.RunestoneEffects;
import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.VWEffects;
import cliffordha.totvw.registry.VWSounds;
import cliffordha.totvw.registry.attachments.Runestone;
import cliffordha.totvw.registry.attachments.VWAttachment;
import cliffordha.totvw.registry.attachments.WolfFragmentData;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import cliffordha.totvw.util.ExtUtil;
import cliffordha.totvw.util.TextPopup;

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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.List;

import static cliffordha.totvw.util.ExtUtil.*;
import static cliffordha.totvw.util.ExtUtil.removeEffect;

public class WolfEffects {
    public static void onPostAttack(Wolf wolf, ServerLevel level, @Nullable LivingEntity linkVictim, boolean exclusive) {
        var victim = exclusive ? VWWolfBehaviors.CURRENT_VICTIM.get() : linkVictim;
        if (victim == null) return;
        LivingEntity player = wolf.getOwner();
        RandomSource random = wolf.getRandom();

        float victimHealth = victim.getHealth();
        float victimMaxHealth = victim.getMaxHealth();

        var enchantment = WolfEnchantmentRecord.of(wolf);

        float BLEEDING_DMG = 0;

        List<Wolf> babyWolves = level.getEntitiesOfClass(Wolf.class, scanArea(wolf, 12), t -> WolfAttachment.isFamilyRelated(wolf, t) && t.isBaby());

        if (enchantment.hasMending() && exclusive) {
            float conversion = enchantment.hasBenediction() ? 0.25f : 0.1f;
            int dmg = Mth.ceil(wolf.getAttributeValue(Attributes.ATTACK_DAMAGE) * conversion) + 2;
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
        if (exclusive && HAS_ANY_DEFENSE) {
            //CASE 4
            buffForBabyWolves(wolf, babyWolves, enchantment, 4);
        }

        if (wolf.hasAttached(WolfAttachment.FRAGMENT_DATA) && WolfFragmentData.getExtension(wolf) < 6) {
            WolfFragmentData.addLifespan(wolf, 15);
            WolfFragmentData.addExtension(wolf, 1);
        }

        // ordered
        if (enchantment.hasIgnition()) {
            IgnitionEffect.onAttack(level, wolf, victim, exclusive);

        } else if (enchantment.hasArcticWinds()) {
            ArcticWindsEffect.applyEffectsOnHit(wolf, victim, level, enchantment.getArcticWinds(), exclusive);

        } else if (enchantment.hasWithering()) {
            WitheringEffect.onAttack(level, wolf, player, victim, exclusive);

        } else if (enchantment.hasPoisoning()) {
            int ACTIVE_POISONING = enchantment.getPoisoning();
            if (ACTIVE_POISONING >= 3) {
                removeEffect(victim, MobEffects.REGENERATION);
            }
            addOrStackEffect(victim, MobEffects.POISON, (ACTIVE_POISONING * 2), Math.min(ACTIVE_POISONING - 1, 2), true);
        }

        // ordered
        if (enchantment.hasOozing() && exclusive) {
            int defaultTime;
            if (enchantment.getBloodlust() > 0) {
                defaultTime = (int) ((enchantment.getBloodlust() * 1.50) * 60);
            } else if (enchantment.getMight() > 0) {
                defaultTime = (int) ((enchantment.getMight() * 1.25) * 60);
            } else {
                defaultTime = 60;
            }
            addOrStackEffect(victim, MobEffects.OOZING, defaultTime, 1, false);

        } else if (enchantment.hasLifting()) {
            int ACTIVE_LIFTING = enchantment.getLifting();
            if (victim.hasEffect(MobEffects.LEVITATION)) {
                double str = level.getRandom().nextDouble();
                victim.knockback(str, ACTIVE_LIFTING, str);
            } else {
                boolean strong = victim.getY() > 196 || isDimension(victim, Level.END);
                int amp = strong ? ACTIVE_LIFTING * 10 : ACTIVE_LIFTING * 4;
                victim.addEffect(new MobEffectInstance(MobEffects.LEVITATION, strong ? 15 : 10, amp, true, false, false));
                victim.setAttached(VWAttachment.LIFT_DURATION, strong ? 40 : 30);
                for (int i = 0; i < ACTIVE_LIFTING; i++) {
                    TextPopup.show(wolf, victim, "^", 3.0f);
                }
            }
        }

        if (enchantment.hasBloodLust() && exclusive) {
            int ACTIVE_BLOODLUST = enchantment.getBloodlust();

            if (!wolf.hasEffect(VWEffects.BLOODLUST)) {
                addEffect(wolf, VWEffects.BLOODLUST, 6, ACTIVE_BLOODLUST - 1);

            } else {
                if (ACTIVE_BLOODLUST >= 3) {
                    replaceEffect(victim, MobEffects.SPEED, MobEffects.SLOWNESS);
                }

                SkillDefinition skill = WolfSkills.PARALYZER;
                boolean checkVictim = victim.is(EntityType.PLAYER) || victim.getMaxHealth() > 20.0;
                boolean isNotParalyzed = !victim.hasEffect(VWEffects.PARALYZE) && ArcticWindsEffect.frostBoundStack(victim) <= 0;
                if (checkVictim && !SkillManager.isOnCooldown(wolf, skill) && isNotParalyzed) {
                    int paralyzeTime = 3 + (ACTIVE_BLOODLUST * 3);
                    addHiddenEffect(victim, VWEffects.PARALYZE, paralyzeTime, 0);

                    TextPopup.showBold(wolf, victim, "PARALYZED!", 1.7f + random.nextFloat());
                    SkillManager.startCooldown(wolf, skill, cooldownFromBase(level, SkillCD.BASE_PARALYZE));

                    ExtUtil.playSound(victim, VWSounds.WOLF_SKILL_PARALYZE, SoundSource.HOSTILE, 0.1f, 0.55f + level.getRandom().nextFloat(), false);
                }
            }
        }
        if (enchantment.hasMight()) {
            int ACTIVE_MIGHT = enchantment.getMight();
            addOrStackEffect(wolf, MobEffects.ABSORPTION, 3, 30, 0, false);

            if (ACTIVE_MIGHT >= 3) {
                boolean checkHealth = victimHealth > 3 && victimHealth <= victimMaxHealth * 0.6f;
                if (checkHealth && !SkillManager.isOnCooldown(wolf, WolfSkills.RUPTURE)) {
                    if (victim.hasEffect(MobEffects.STRENGTH)) {
                        replaceEffect(victim, MobEffects.STRENGTH, MobEffects.WEAKNESS);
                    } else {
                        addEffect(victim, MobEffects.WEAKNESS, 10, 0);
                    }
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
                    int finalCD = cooldownFromBase(level, SkillCD.BASE_RUPTURE, decreaseTime);
                    BLEEDING_DMG += finalDMG;
                    sendParticles(ParticleTypes.EXPLOSION_EMITTER, level, victim.blockPosition().below(), 1, 0);
                    playSound(victim, SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, true);

                    TextPopup.showBold(wolf, victim, "RUPTURED! " + Mth.floor(finalDMG), VWColors.MIGHT, 1.7f + random.nextFloat());

                    SkillManager.startCooldown(wolf, WolfSkills.RUPTURE, finalCD);
                }
            }
            if (ACTIVE_MIGHT >= 4 && removeEffect(victim, MobEffects.ABSORPTION)) {
                addEffect(victim, MobEffects.ABSORPTION, 12, (ACTIVE_MIGHT - 1));

            }
            if (ACTIVE_MIGHT >= 5) {
                removeEffect(victim, MobEffects.RESISTANCE);
                BLEEDING_DMG += wolf.getMaxHealth() * 0.10f;
            }
        }

        if (enchantment.hasGnawing() && exclusive) {
            float heal = enchantment.getGnawing() == 1 ? wolf.getMaxHealth() * 0.15f : wolf.getMaxHealth() * 0.30f;
            wolf.heal(heal);

            // CASE 3
            buffForBabyWolves(wolf, babyWolves, enchantment, 3);

            if (player != null && player.getHealth() < player.getMaxHealth()) {
                float healStrength = getHealStrength(MobEnchantmentRecord.of(player));
                float baseCap = enchantment.getGnawing() > 1 ? player.getMaxHealth() * 0.4f : player.getMaxHealth() * 0.2f;
                player.heal(Math.min(baseCap, healStrength));
                TextPopup.show(wolf, player, "+" + (double) Math.min(baseCap, healStrength), VWColors.GNAWING, 1 + random.nextFloat());
            }
        }

        applyFinalDamage(level, wolf, victim, VWDamageTypes.bleeding(level), BLEEDING_DMG, exclusive);
    }

    public static void onTick(Wolf wolf, ServerLevel level) {
        boolean armor = !wolf.getBodyArmorItem().getEnchantments().isEmpty();
        if (armor) {
            armorTick(wolf, level);
        }
        Runestone type = Runestone.getType(wolf);
        RunestoneEffects.onTickRunestoneEffect(wolf, level, type);
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
        LivingEntity owner = wolf.getOwner();
        if (wolf.hasAttached(WolfAttachment.FRAGMENT_DATA)) {
            if (owner == null || !wolf.isTame()) WitheringEffect.unloadFragmentedWolf(level, wolf);

            wolf.unRide();
            wolf.dropLeash();

            int lifespan = WolfFragmentData.getLifespan(wolf);
            if (lifespan > 0) {
                WolfFragmentData.decrementLifespan(wolf);

            } else {

                if (owner != null) {
                    boolean fallback = wolf.distanceTo(owner) > 3 && wolf.getTarget() == null;
                    if (fallback) {
                        wolf.getNavigation().moveTo(owner.getX(), owner.getY(), owner.getZ(), 1, 1.33);
                    } else {
                        WitheringEffect.unloadFragmentedWolf(level, wolf);
                    }
                } else WitheringEffect.unloadFragmentedWolf(level, wolf);
            }
        }
    }
    private static void armorTick(Wolf wolf, ServerLevel level) {
        LivingEntity player = wolf.getOwner();

        var enchantment = WolfEnchantmentRecord.of(wolf);

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
            if (!wolf.hasEffect(VWEffects.BLOODLUST)) {
                addEffect(wolf, MobEffects.SPEED, 1, Math.min(ACTIVE_MIGHT - 1, 3));
            }
            addEffect(wolf, VWEffects.AMPLIFIED_MIGHT, ACTIVE_MIGHT * 3, ACTIVE_MIGHT - 1);

            // CASE 1
            buffForBabyWolves(wolf, babyWolves, enchantment, 1);
        }
        if (!enchantment.hasIgnition() && ACTIVE_MIGHT >= 3) {
            if (wolf.isOnFire() && !wolf.isInLava()) {
                wolf.extinguishFire();
            }
        }
    }

    public static void applyFinalDamage(ServerLevel level, Wolf wolf, LivingEntity target, DamageSource source, float damage, boolean isFromEnchantment) {
        if (damage <= 0) return;
        if (target == null) return;
        if (!isFromEnchantment) {
            target.hurtServer(level, source, Mth.ceil(wolf.getAttributeValue(Attributes.ATTACK_DAMAGE)));

        } else {
            target.hurtServer(level, source, damage);
        }
    }
    private static void buffForBabyWolves(Wolf parent, List<Wolf> babyWolves, WolfEnchantmentRecord enchants, int stat) {
        if (babyWolves.isEmpty()) return;
        for (Wolf wolf : babyWolves) {
            switch (stat) {
                //ARMOR TICK
                case 1 -> {
                    addEffect(wolf, VWEffects.AMPLIFIED_MIGHT, enchants.getMight() * 3, 0);
                    addEffect(wolf, MobEffects.ABSORPTION, enchants.getMight() * 3, 0);
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

    private static float getHealStrength(MobEnchantmentRecord enchants) {
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
}
