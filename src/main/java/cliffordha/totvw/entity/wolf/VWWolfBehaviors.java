package cliffordha.totvw.entity.wolf;

import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.datagen.VWDamageTypes;
import cliffordha.totvw.effect.HavocEffect;
import cliffordha.totvw.entity.player.EntityEnchants;
import cliffordha.totvw.entity.skills.RunestoneEffects;
import cliffordha.totvw.entity.skills.VerdantWindBlessing;
import cliffordha.totvw.registry.*;
import cliffordha.totvw.registry.attachments.Runestone;
import cliffordha.totvw.registry.attachments.PlayerPrefs;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import cliffordha.totvw.util.VWUtil;
import cliffordha.totvw.entity.skill.WolfSkillDefinition;
import cliffordha.totvw.entity.skill.SkillManager;
import cliffordha.totvw.tag.VWBiomeTags;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.*;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static cliffordha.totvw.entity.skill.SkillManager.*;
import static cliffordha.totvw.util.VWUtil.*;
import static cliffordha.totvw.util.VWUtil.TimeUtil.*;

public class VWWolfBehaviors {
    public static final SoundEvent[] DISTANT_HOWL_SOUNDS = {VWSounds.WOLF_HOWL_A, VWSounds.WOLF_HOWL_B1, VWSounds.WOLF_HOWL_B2, VWSounds.WOLF_HOWL_B3};

    private static final List<WolfBehaviorRule> ON_DAMAGE_RULES = new ArrayList<>();
    private static final List<WolfBehaviorRule> TICK_RULES = new ArrayList<>();

    public static void registerModWolfBehaviors() {
        registerTamedRules();
        registerWildRules();
        registerSharedRules();

        wolfOnDamageEvent();
        wolfOnTickEvent();
    }

    private static void registerTamedRules() {
        TICK_RULES.add(WolfBehaviorRule.forTamed(
                WolfCondition.tick()
                        .and(WolfCondition.noAttachment(WolfAttachment.TIMER_AIR_SUPPLY))
                        .and(WolfCondition.isUnderWater())
                        .and(WolfCondition.airSupplyLowerThan(0.5f))
                        .and(WolfCondition.unableToTeleport()),
                VWWolfBehaviors::warnOwner
        ));
        TICK_RULES.add(WolfBehaviorRule.forTamed(
                WolfCondition.tick()
                        .and(WolfCondition.isInLava())
                        .and(WolfCondition.ownerFarther(4)),
                ((wolf, level) -> {
                    LivingEntity owner = wolf.getOwner();
                    if (owner == null) return;
                    wolf.teleportToAroundBlockPos(owner.blockPosition());
                })
        ));
    }
    private static void registerWildRules() {
        ON_DAMAGE_RULES.add(WolfBehaviorRule.forWild(WolfCondition.alwaysTrue(), (wolf, level) -> {
            boolean hardMode = level.getDifficulty() == Difficulty.HARD || level.getServer().isHardcore();
            var victim = CURRENT_VICTIM.get();
            if (victim == null) return;
            if (wolf.isAngry()) {
                int additional = isInBiome(wolf, VWBiomeTags.IS_VERDANT_BIOMES) ? 3 : 0;
                int duration = (hardMode ? sec(7) : sec(3)) + additional;
                int amplifier = hardMode ? 1 : 0;
                addEffect(victim, MobEffects.WEAKNESS, duration, amplifier);
                addHiddenEffect(wolf, MobEffects.SPEED, duration, amplifier);
            }

            if (victim instanceof Monster) {
                int TRY_SAVE_STATUS = wolf.getAttachedOrElse(WolfAttachment.TRY_SAVE_STATUS, 0);
                int points = wolf.getAttachedOrElse(WolfAttachment.TRY_SAVE_POINTS, 0);

                if (TRY_SAVE_STATUS == 1) {
                    wolf.setAttached(WolfAttachment.TRY_SAVE_POINTS, Math.min(points + 1, 12));
                }
            }
        }));
    }
    private static void registerSharedRules() {
        ON_DAMAGE_RULES.add(WolfBehaviorRule.forAny(
                WolfCondition.alwaysTrue(),
                (wolf, level) -> {
                    if (wolf.isWearingBodyArmor()) {
                        runEnchantmentsOnDamage(wolf, level, null, true);
                    }
                    processRunestones(wolf, level);
                }
        ));
        TICK_RULES.add(WolfBehaviorRule.forTamed(
                WolfCondition.tick(0, 3),
                (wolf, level) ->  {
                    LivingEntity owner = wolf.getOwner();

                    //actions with owner check
                    if (owner == null) return;

                    float HEALTH_THRESHOLD = owner.getAttachedOrElse(PlayerPrefs.BENEDICTION_HEALTH_THRESHOLD,
                            VWConfig.get().SERVER_BENEDICTION_HEALTH_THRESHOLD) * 0.01f;

                    if (wolf.getHealth() <= wolf.getMaxHealth() * HEALTH_THRESHOLD) {
                        VerdantWindBlessing.triggerBenedictionFromTick(wolf);
                    }

                    boolean shouldBiteOffLeash = wolf.isAngry()
                            && wolf.getTarget() != null
                            && wolf.distanceTo(wolf.getTarget()) < 6;

                    if (shouldBiteOffLeash) {
                        wolf.unRide();
                        wolf.dropLeash();
                        wolf.setOrderedToSit(false);
                    }

                    boolean tryToTeleport = wolf.isInWater() && wolf.distanceToSqr(owner) > 6;
                    if (tryToTeleport) {
                        wolf.tryToTeleportToOwner();
                    }
                }
        ));
        TICK_RULES.add(WolfBehaviorRule.forAny(
                WolfCondition.newSoundsEnable()
                        .and(WolfCondition.isInBiomes(VWBiomeTags.FOREST_WHERE_WOLVES_HOWL))
                        .and(WolfCondition.tick(0, 30)),
                (wolf, level) -> {
                    if (wolf.isAngry()) return;
                    if (wolf.level().getMaxLocalRawBrightness(wolf.blockPosition()) > 11) return;

                    if (level.getRandom().nextFloat() < 0.05f) {
                        SoundEvent sound = DISTANT_HOWL_SOUNDS[level.getRandom().nextInt(DISTANT_HOWL_SOUNDS.length)];
                        level.playSound(null, wolf.blockPosition(), sound, SoundSource.AMBIENT, 0.2f + level.getRandom().nextFloat() * 0.5f, 0.8f + level.getRandom().nextFloat() * 0.4f);
                    }
                }
        ));
        TICK_RULES.add(WolfBehaviorRule.forAny(
                WolfCondition.tick(1, 0).and(WolfCondition.healthBelow(0.8f)),
                VWWolfBehaviors::runNaturalHealOnTick
        ));
        TICK_RULES.add(WolfBehaviorRule.forAny(WolfCondition.tick(), (wolf, level) -> {
            runEffectsOnTick(wolf, level);

            if (VWConfig.get().LOG_ENCHANTMENT_SHOW_WOLF_CD) setWolfConfiguration(wolf, 0);
            if (VWConfig.get().SERVER_OTHER_COOLDOWNS) {
                depleteCooldown(wolf, WolfAttachment.TIMER_AIR_SUPPLY);
            }
            if (VWConfig.get().SERVER_SKILL_COOLDOWNS) {
                depleteCooldown(wolf, WolfAttachment.CD_BLESSING_OF_THE_VERDANT_WIND);
                depleteCooldown(wolf, WolfAttachment.CD_BLOODLUST_SKILL_PARALYZE);
                depleteCooldown(wolf, WolfAttachment.CD_MIGHT_SKILL_RUPTURE);
                depleteCooldown(wolf, WolfAttachment.CD_IGNORE_HIGH_DAMAGE);
            } else setWolfConfiguration(wolf, 1);

            SkillManager.notifyReset(wolf, VERDANT_BLESSING);
            SkillManager.notifyReset(wolf, PARALYZER);

            processCDNotify(wolf,
                    WolfAttachment.CD_BLESSING_OF_THE_VERDANT_WIND,
                    WolfAttachment.NOTIFY_BLESSING_OF_THE_VERDANT_WIND,
                    VWColors.VERDANT_WIND_MUTED,
                    "§nVerdant Wind's Blessing§f cooldown reset for §r" + wolfName(wolf)
            );
            processCDNotify(wolf,
                    WolfAttachment.CD_BLOODLUST_SKILL_PARALYZE,
                    WolfAttachment.NOTIFY_BLOODLUST_SKILL_PARALYZE,
                    VWColors.BLOODLUST_EFFECT_MUTED,
                    "§nBloodlust Skill: Paralyzer§r cooldown reset for §r" + wolfName(wolf)
            );
        }));
    }


    private static void warnOwner(Wolf wolf, ServerLevel level) {
        if (wolf.getAirSupply() <= wolf.getMaxAirSupply() * 0.5 && wolf.getAirSupply() > 0.0f) {
            wolf.makeSound(new SoundEvent(Identifier.withDefaultNamespace("entity.wolf.whine"), Optional.of(16.0f)));
            sendToChat(wolf, VWColors.MIGHT_EFFECT_MUTED, "[" + wolfName(wolf) + "] My air supply is about to run out...");
            playNotification(wolf);
        }

        wolf.setAttached(WolfAttachment.TIMER_AIR_SUPPLY, 3);
        wolf.setAttached(WolfAttachment.NOTIFY_AIR_SUPPLY, 1);
    }
    public static void runPlayerBlessing(Wolf wolf, LivingEntity player, ServerLevel level) {
        VerdantWindBlessing.applyBenedictionEffects(player, false);

        player.heal(triggerHeal(wolf, player));
        VWUtil.sendToChat(wolf, VWColors.VERDANT_WIND, true, wolfName(wolf) + " has granted you the §nVerdant Wind's Blessing§r");

        sendParticles(VWParticles.BENEDICTION_TRIGGER_PARTICLE, level, player.blockPosition(), 6, 0.5);
        verdantBlessingAfterEffects(level, wolf);
    }
    public static void runEnchantmentsOnDamage(Wolf wolf, ServerLevel level, @Nullable LivingEntity linkVictim, boolean enchantmentExclusive) {
        var victim = enchantmentExclusive ? CURRENT_VICTIM.get() : linkVictim;
        if (victim == null) return;
        LivingEntity player = wolf.getOwner();

        float victimHealth = victim.getHealth();
        float victimMaxHealth = victim.getMaxHealth();

        WolfEnchants enchantment = WolfEnchants.of(wolf);

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
                int toRepairFromXpAmount = EnchantmentHelper.modifyDurabilityToRepairFromXp((ServerLevel) wolf.level(), itemStack, dmg);
                int repair = Math.min(toRepairFromXpAmount, itemStack.getDamageValue());
                itemStack.setDamageValue(itemStack.getDamageValue() - repair);
            }
        }

        boolean HAS_ANY_DEFENSE = enchantment.hasBlastProtection() || enchantment.hasFireProtection() || enchantment.hasProjectileProtection() && enchantment.hasProtection();
        if (enchantmentExclusive && HAS_ANY_DEFENSE) {
            //CASE 4
            buffForBabyWolves(wolf, babyWolves, enchantment, 4);
        }

        if (enchantment.hasIgnition()) {
            int ACTIVE_IGNITION = enchantment.getIgnition();
            int burnTime = ACTIVE_IGNITION * 3;
            boolean isWithinHotBiomes = isInBiome(wolf, BiomeTags.IS_NETHER) || wolf.level().getBiome(wolf.blockPosition()).value().getBaseTemperature() > 1.8f;
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
            addOrStackEffect(victim,
                    MobEffects.POISON,
                    sec(2) + (ACTIVE_POISONING * sec(2)),
                    Math.min(ACTIVE_POISONING, 2),
                    false
            );
        }

        if (enchantment.hasWithering()) {
            int ACTIVE_WITHERING = enchantment.getWithering();
            if (ACTIVE_WITHERING >= 3) {
                removeEffect(victim, MobEffects.REGENERATION);
            }
            rewriteEffect(victim, MobEffects.WITHER, ACTIVE_WITHERING * sec(2), ACTIVE_WITHERING);
            if (victim.isInvulnerableTo(level, victim.level().damageSources().wither())) {
                BLEEDING_CONSOLIDATED_DMG += victimHealth * 0.5f;
            }
        }

        if (enchantment.hasLifting()) {
            int ACTIVE_LIFTING = enchantment.getLifting();
            if (victim.hasEffect(MobEffects.LEVITATION)) {
                double random = level.getRandom().nextDouble();
                victim.knockback(random, ACTIVE_LIFTING, random, level.damageSources().flyIntoWall(), ACTIVE_LIFTING);
            } else {
                addHiddenEffect(victim, MobEffects.LEVITATION, 10, ACTIVE_LIFTING * 3);
            }
        }

        if (enchantment.hasBloodLust()) {
            int ACTIVE_BLOODLUST = enchantment.getBloodlust();
            int paralyzeTime = sec(3) + (sec(ACTIVE_BLOODLUST * 3));

            addEffect(wolf, VWEffects.BLOODLUST, sec(6), ACTIVE_BLOODLUST - 1);
            if (wolf.hasEffect(VWEffects.BLOODLUST)) {
                addEffect(victim, MobEffects.WEAKNESS, ACTIVE_BLOODLUST * sec(6), Math.min(ACTIVE_BLOODLUST - 1, 2));
                if (ACTIVE_BLOODLUST >= 3) {
                    addHiddenEffect(victim, MobEffects.SLOWNESS, sec(3), 0);
                    removeEffect(victim, MobEffects.SPEED);
                    removeEffect(victim, MobEffects.REGENERATION);
                }
            }
            boolean checkVictim = victim.is(EntityTypes.PLAYER) || victim.getMaxHealth() > 20.0;
            if (checkVictim && SkillManager.isOnCooldown(wolf, PARALYZER) && !victim.hasEffect(VWEffects.PARALYZE)) {
                int min = 60;
                addHiddenEffect(victim, VWEffects.PARALYZE, paralyzeTime, 0);

                SkillManager.startCooldown(wolf, PARALYZER,
                        setDifficultyBasedValue(level, min, 12 * min, 18 * min, 24 * min));

                if (enchantmentExclusive) {
                    sendToChat(wolf, VWColors.MIGHT_EFFECT, victim.getPlainTextName() + " has been paralyzed for " + (paralyzeTime / sec(1)) + " seconds by " + wolfName(wolf) + "!");
                    VWUtil.playSound(victim, VWSounds.WOLF_SKILL_PARALYZE, SoundSource.HOSTILE, 0.1f, 0.55f + level.getRandom().nextFloat(), false);
                }
            }
        }

        if (enchantment.hasOozing() && enchantmentExclusive) {
            int defaultTime;
            if (enchantment.getBloodlust() > 0) {
                defaultTime = (int) ((enchantment.getBloodlust() * 1.50) * min(1));
            } else if (enchantment.might() > 0) {
                defaultTime = (int) ((enchantment.might() * 1.25) * min(1));
            } else {
                defaultTime = min(1);
            }
            addOrStackEffect(victim, MobEffects.OOZING, defaultTime, 1, false);
        }

        if (enchantment.hasMight()) {
            int ACTIVE_MIGHT = enchantment.might();
            addEffect(wolf, MobEffects.ABSORPTION, ACTIVE_MIGHT * sec(3), 1);
            if (ACTIVE_MIGHT >= 3) {
                removeEffect(victim, MobEffects.RESISTANCE);
                removeEffect(victim, MobEffects.STRENGTH);
                removeEffect(victim, MobEffects.ABSORPTION);

                if (victimHealth <= victimMaxHealth * 0.6f && SkillManager.isOnCooldown(wolf, RUPTURE)) {
                    float finalDMG;
                    if (player != null) {
                        if (wolf.distanceTo(player) < 4) {
                            finalDMG = wolf.getHealth() * 0.4f;
                        } else {
                            finalDMG = wolf.getHealth() * 0.6f;
                        }
                    } else {
                        finalDMG = wolf.getMaxHealth() * 1.8f;
                    }
                    float decreaseTime = enchantment.hasBenediction() ? 0.75f: 1.0f;
                    int finalCD = (int) (setDifficultyBasedValue(level, 7, 14, 21, 28) * decreaseTime);
                    BLEEDING_CONSOLIDATED_DMG += finalDMG;
                    sendParticles(ParticleTypes.EXPLOSION_EMITTER, level, victim.blockPosition().below(), 1, 0);
                    victim.makeSound(SoundEvents.PLAYER_ATTACK_CRIT);
                    SkillManager.startCooldown(wolf, RUPTURE, finalCD);
                }
            }
            if (ACTIVE_MIGHT >= 5) {
                BLEEDING_CONSOLIDATED_DMG += wolf.getMaxHealth() * 0.10f;
            }
        }

        if (enchantment.hasGnawing() && enchantmentExclusive) {
            float heal = enchantment.gnawing() == 1 ? wolf.getMaxHealth() * 0.15f : wolf.getMaxHealth() * 0.30f;
            wolf.heal(heal);

            // CASE 3
            buffForBabyWolves(wolf, babyWolves, enchantment, 3);

            if (player != null && player.getHealth() < player.getMaxHealth()) {
                EntityEnchants entityEnchants = EntityEnchants.of(player);

                float healStrength;
                if (entityEnchants.blastProtection() > 0) {
                    healStrength = entityEnchants.blastProtection() * 1.6f;

                } else if (entityEnchants.fireProtection() > 0) {
                    healStrength = entityEnchants.fireProtection() * 1.3f;

                } else if (entityEnchants.projectileProtection() > 0 || entityEnchants.protection() > 0) {
                    healStrength = entityEnchants.projectileProtection() + entityEnchants.protection();

                } else {
                    healStrength = 0f;
                }
                if (healStrength > 0f) {
                    if (entityEnchants.protection() > 0) healStrength *= 1.20f;
                    float baseCap = enchantment.gnawing() > 1 ? player.getMaxHealth() * 0.4f : player.getMaxHealth() * 0.2f;
                    player.heal(Math.min(baseCap, healStrength));
                }
            }
        }
        applyConsolidatedDamage(level, wolf, victim, DMG_SOURCE_BLEEDING, BLEEDING_CONSOLIDATED_DMG, enchantmentExclusive);
        applyConsolidatedDamage(level, wolf, victim, VWDamageTypes.scorchingHeat(level), SCORCHING_CONSOLIDATED_DMG, enchantmentExclusive);
    }
    private static void runEffectsOnTick(Wolf wolf, ServerLevel level) {
        if (wolf.isWearingBodyArmor()) {
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
            } else if (type == Runestone.GENESIS) {
                t = ParticleTypes.GLOW;
                n = 2;
            } else {
                t = ParticleTypes.END_ROD;
                n = 4;
            }
            sendParticles(t, level, wolf.blockPosition(), n, 0.5);
        }
    }
    private static void armorTick(Wolf wolf, ServerLevel level) {
        LivingEntity player = wolf.getOwner();

        var enchantment = WolfEnchants.of(wolf);

        boolean ACTIVE_BENEDICTION = enchantment.hasBenediction();
        int ACTIVE_IGNITION = enchantment.getIgnition();
        int ACTIVE_MIGHT = enchantment.might();
        int ACTIVE_FIRE_PROTECTION = enchantment.fireProtection();

        List<Wolf> babyWolves = level.getEntitiesOfClass(
                Wolf.class,
                scanArea(wolf, 12),
                test -> WolfAttachment.isFamilyRelated(wolf, test) && test.isBaby()
        );

        if (ACTIVE_BENEDICTION && ACTIVE_IGNITION > 0 && ACTIVE_FIRE_PROTECTION >= 3 && isInBiome(wolf, BiomeTags.IS_NETHER)) {
            addHiddenEffect(wolf, MobEffects.FIRE_RESISTANCE, sec(3), 8);
            if (player != null && isInBiome(player, BiomeTags.IS_NETHER) && wolf.distanceTo(player) < 24) addHiddenEffect(player, MobEffects.FIRE_RESISTANCE, sec(3), 8);

            // CASE 2
            buffForBabyWolves(wolf, babyWolves, enchantment, 2);
        }
        if (ACTIVE_MIGHT > 0 && wolf.getTarget() != null) {
            addEffect(wolf, VWEffects.AMPLIFIED_MIGHT, ACTIVE_MIGHT * sec(3), Math.min(ACTIVE_MIGHT - 1, 2));

            // CASE 1
            buffForBabyWolves(wolf, babyWolves, enchantment, 1);
        }
        if (ACTIVE_IGNITION > 0 || ACTIVE_MIGHT > 3) {
            if (wolf.isOnFire()) {
                wolf.extinguishFire();
            }
        }
    }
    private static void buffForBabyWolves(Wolf parent, List<Wolf> babyWolves, WolfEnchants enchants, int stat) {
        if (babyWolves.isEmpty()) return;
        for (Wolf wolf : babyWolves) {
            switch (stat) {
                //ARMOR TICK
                case 1 -> {
                    addEffect(wolf, VWEffects.AMPLIFIED_MIGHT, enchants.might() * sec(3), Math.min(enchants.might() - 1, 2));
                    addEffect(wolf, MobEffects.ABSORPTION, enchants.might() * sec(3), 1);
                }
                case 2 -> {
                    if (isInBiome(wolf, BiomeTags.IS_NETHER) && wolf.distanceTo(parent) < 24) {
                        addHiddenEffect(wolf, MobEffects.FIRE_RESISTANCE, sec(3), 8);
                    }
                }

                // ON-ATTACK
                case 3 -> {
                    float heal = enchants.gnawing() == 1 ? wolf.getMaxHealth() * 0.15f : wolf.getMaxHealth() * 0.30f;
                    wolf.heal(heal);
                }
                case 4 -> {
                    Holder<MobEffect> effect;
                    int amp;
                    if (enchants.blastProtection() > 0 || enchants.protection() > 0) {
                        amp = enchants.blastProtection() > 0 ? 2 : 0;
                        effect = MobEffects.RESISTANCE;
                    } else {
                        amp = 1;
                        effect = MobEffects.FIRE_RESISTANCE;
                    }
                    addEffect(wolf, effect, enchants.might() * sec(3), amp);
                }
                default -> {
                }
            }
        }
    }
    public static void applyConsolidatedDamage(ServerLevel level, Wolf wolf, LivingEntity target, DamageSource source, float amount, boolean isFromEnchantment) {
        if (target == null) return;
        source = isFromEnchantment ? source : VWDamageTypes.tetherProxy(level);
        if (!isFromEnchantment) {
            double damage = wolf.getAttributeValue(Attributes.ATTACK_DAMAGE);
            target.hurtServer(level, level.damageSources().magic(), (float) damage);
        } else {
            if (amount <= 0) return;
            target.hurtServer(level, source, amount);
        }
    }
    // manually set victim for each runestone
    public static void processRunestones(Wolf wolf, ServerLevel level) {
        var victim = VWWolfBehaviors.CURRENT_VICTIM.get();

        if (Runestone.hasTether(wolf)) {
            RunestoneEffects.processLinkStatus(wolf, victim, level);

        } else if (Runestone.hasHavoc(wolf)) {
            RunestoneEffects.processHavocStatus(wolf, victim, level);
        }
    }
    private static void runNaturalHealOnTick(Wolf wolf, ServerLevel level) {
        boolean inVerdantBiomes = level.getBiome(wolf.blockPosition()).is(VWBiomeTags.IS_VERDANT_BIOMES);
        float healNatural = (inVerdantBiomes ? 2.0f : 1.0f) + Math.max(level.getRandom().nextInt(), 2);
        float healAdditional = wolf.getOwner() != null ? 1.0f : 0.0f;
        wolf.heal(healNatural + healAdditional);
    }



    public static final WolfSkillDefinition VERDANT_BLESSING =
            new WolfSkillDefinition(
                    WolfAttachment.CD_BLESSING_OF_THE_VERDANT_WIND,
                    WolfAttachment.NOTIFY_BLESSING_OF_THE_VERDANT_WIND,
                    VWColors.VERDANT_WIND,
                   "§nVerdant Wind's Blessing§r"
            );

    private static final WolfSkillDefinition PARALYZER =
            new WolfSkillDefinition(
                    WolfAttachment.CD_BLOODLUST_SKILL_PARALYZE,
                    WolfAttachment.NOTIFY_BLOODLUST_SKILL_PARALYZE,
                    VWColors.BLOODLUST_EFFECT,
                "§nBloodlust Skill: Paralyzer§r"
            );

    private static final WolfSkillDefinition RUPTURE =
            new WolfSkillDefinition(
                    WolfAttachment.CD_MIGHT_SKILL_RUPTURE,
                    WolfAttachment.NOTIFY_MIGHT_SKILL_RUPTURE,
                    VWColors.MIGHT_EFFECT,
                    "§nMight Skill: Rupture§r"
            );



    private static String wolfName(Wolf wolf) {
        String wolfName;
        if (wolf.getPlainTextName().equals("Wolf")) {
            wolfName = "§dWolf§r";
        } else {
            wolfName = "§d" + wolf.getPlainTextName() + "§r";
        }
        return wolfName;
    }

    public static final ThreadLocal<LivingEntity> CURRENT_VICTIM = new ThreadLocal<>();
    public static void wolfOnDamageEvent() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register(
                (victim, damageSource, _, _, _) -> getWolfVictimThread(victim,  damageSource)
        );
        ServerLivingEntityEvents.AFTER_DEATH.register(
                VWWolfBehaviors::getWolfVictimThread
        );
    }
    private static void wolfOnTickEvent() {
        if (TICK_RULES.isEmpty()) return;
        ServerTickEvents.END_SERVER_TICK.register((MinecraftServer server) -> {
            for (var serverLevel : server.getAllLevels()) {
                serverLevel.getEntities(
                        EntityTypes.WOLF,
                        _ -> true
                ).forEach(wolf -> {
                    for (WolfBehaviorRule rule : TICK_RULES) {
                        if (rule.isApplicableTo(wolf)) {
                            rule.evaluate(wolf, serverLevel);
                        }
                    }
                });
            }
        });
    }
    private static void getWolfVictimThread(LivingEntity victim, DamageSource damageSource) {
        Entity directEntity = damageSource.getEntity();
        if (!(directEntity instanceof Wolf wolf)) return;
        if (!(wolf.level() instanceof ServerLevel serverLevel)) return;

        int cycle = wolf.getAttachedOrElse(RunestoneEffects.ATTACK_CYCLE, 0);
        if (cycle < 5) {
            wolf.setAttached(RunestoneEffects.ATTACK_CYCLE, cycle + 1);
        } else {
            wolf.setAttached(RunestoneEffects.ATTACK_CYCLE, 0);
        }

        CURRENT_VICTIM.set(victim);
        try {
            for (WolfBehaviorRule rule : ON_DAMAGE_RULES) {
                if (rule.isApplicableTo(wolf)) {
                    rule.evaluate(wolf, serverLevel);
                }
            }
        } finally {
            CURRENT_VICTIM.remove();
        }
    }
}
