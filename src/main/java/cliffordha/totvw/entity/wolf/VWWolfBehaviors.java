package cliffordha.totvw.entity.wolf;

import cliffordha.totvw.config.TOTVWConfig;
import cliffordha.totvw.datagen.VWDamageTypes;
import cliffordha.totvw.util.VWUtil;
import cliffordha.totvw.entity.skill.WolfSkillDefinition;
import cliffordha.totvw.entity.skill.SkillUtil;
import cliffordha.totvw.registry.VWEffects;
import cliffordha.totvw.registry.VWEnchantments;
import cliffordha.totvw.registry.VWAttachments;
import cliffordha.totvw.registry.VWParticleEffects;
import cliffordha.totvw.registry.VWSounds;
import cliffordha.totvw.tag.VWBiomeTags;
import cliffordha.totvw.registry.VWColors;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static cliffordha.totvw.entity.skill.VWSkillProcessor.*;
import static cliffordha.totvw.util.VWUtil.*;

public class VWWolfBehaviors {
    private static final ResourceKey<Enchantment> BENEDICTION_OF_THE_VERDANT_MOUNTAINS = VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS;
    public static final SoundEvent[] DISTANT_HOWL_SOUNDS = {VWSounds.WOLF_HOWL_A, VWSounds.WOLF_HOWL_B1, VWSounds.WOLF_HOWL_B2, VWSounds.WOLF_HOWL_B3};

    private static final List<WolfBehaviorRule> ON_DAMAGE_RULES = new ArrayList<>();
    private static final List<WolfBehaviorRule> TICK_RULES = new ArrayList<>();
    
    private static final AttachmentType<Integer> CD_BLESSING_OF_THE_VERDANT_WIND = VWAttachments.Wolf.WOLF_CD_BLESSING_OF_THE_VERDANT_WIND;
    private static final AttachmentType<Integer> CD_BLOODLUST_SKILL_PARALYZE = VWAttachments.Wolf.WOLF_CD_BLOODLUST_SKILL_PARALYZE;
    private static final AttachmentType<Integer> CD_MIGHT_SKILL_RUPTURE = VWAttachments.Wolf.WOLF_CD_MIGHT_SKILL_RUPTURE;
    private static final AttachmentType<Integer> CD_IGNORE_HIGH_DAMAGE = VWAttachments.Wolf.WOLF_CD_IGNORE_HIGH_DAMAGE;
    private static final AttachmentType<Integer> WOLF_TRY_SAVE_STATUS = VWAttachments.Wolf.WOLF_TRY_SAVE_STATUS;
    private static final AttachmentType<Integer> WOLF_TRY_SAVE_POINTS = VWAttachments.Wolf.WOLF_TRY_SAVE_POINTS;
    private static final AttachmentType<Integer> TIMER_AIR_SUPPLY = VWAttachments.Wolf.WOLF_TIMER_AIR_SUPPLY;
    private static final AttachmentType<Integer> NOTIFY_AIR_SUPPLY = VWAttachments.Wolf.WOLF_NOTIFY_AIR_SUPPLY;
    private static final AttachmentType<Integer> NOTIFY_BLESSING_OF_THE_VERDANT_WIND = VWAttachments.Wolf.WOLF_NOTIFY_BLESSING_OF_THE_VERDANT_WIND;
    private static final AttachmentType<Integer> NOTIFY_BLOODLUST_SKILL_PARALYZE = VWAttachments.Wolf.WOLF_NOTIFY_BLOODLUST_SKILL_PARALYZE;
    private static final AttachmentType<Integer> NOTIFY_MIGHT_SKILL_RUPTURE = VWAttachments.Wolf.WOLF_NOTIFY_MIGHT_SKILL_RUPTURE;
    private static final AttachmentType<String> WOLF_PARENTS_ID = VWAttachments.Wolf.WOLF_PARENTS_ID;

    public static void registerModWolfBehaviors() {
        registerTamedRules();
        registerWildRules();
        registerSharedRules();

        wireOnDamageEvent();
        wireTickEvent();
    }

    private static void registerTamedRules() {
        TICK_RULES.add(WolfBehaviorRule.forTamed(
                WolfCondition.tick(),
                VWWolfBehaviors::runEnchantmentsOnTick
        ));
        TICK_RULES.add(WolfBehaviorRule.forTamed(
                WolfCondition.tick()
                        .and(WolfCondition.hasArmorWithEnchantment(BENEDICTION_OF_THE_VERDANT_MOUNTAINS))
                        .and(WolfCondition.noAttachment(CD_BLESSING_OF_THE_VERDANT_WIND)),
                (wolf, level) -> {
                    if (wolfEnchantmentLVL(wolf, BENEDICTION_OF_THE_VERDANT_MOUNTAINS) < 1) return;
                    runPlayerBlessing(wolf, level);
                }
        ));
        TICK_RULES.add(WolfBehaviorRule.forTamed(
                WolfCondition.tick()
                        .and(WolfCondition.noAttachment(TIMER_AIR_SUPPLY))
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
                int TRY_SAVE_STATUS = wolf.getAttachedOrElse(WOLF_TRY_SAVE_STATUS, 0);
                int points = wolf.getAttachedOrElse(WOLF_TRY_SAVE_POINTS, 0);

                if (TRY_SAVE_STATUS == 1) {
                    wolf.setAttached(WOLF_TRY_SAVE_POINTS, Math.min(points + 1, 12));
                }
            }
        }));
    }
    private static void registerSharedRules() {
        ON_DAMAGE_RULES.add(WolfBehaviorRule.forAny(
                WolfCondition.hasBodyArmor(),
                VWWolfBehaviors::runEnchantmentsOnDamage
        ));
        TICK_RULES.add(WolfBehaviorRule.forTamed(
                WolfCondition.tick(0, 3),
                (wolf, _) ->  {
                    LivingEntity owner = wolf.getOwner();

                    if (!wolf.isBaby() && wolf.getAttachedOrElse(WOLF_PARENTS_ID, "").contains(":baby")) {
                        wolf.removeAttached(WOLF_PARENTS_ID);
                    }

                    boolean checkOwner = owner != null;
                    boolean shouldBiteOffLeash = wolf.isAngry()
                            && wolf.getTarget() != null
                            && wolf.distanceTo(wolf.getTarget()) < 6;
                    if (shouldBiteOffLeash) {
                        wolf.unRide();
                        wolf.dropLeash();
                        wolf.setOrderedToSit(false);
                    }

                    boolean tryToTeleport = checkOwner && wolf.isInWater() && wolf.distanceToSqr(owner) > 16;
                    if (tryToTeleport) {
                        wolf.teleportToAroundBlockPos(owner.blockPosition());
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
        TICK_RULES.add(WolfBehaviorRule.forAny(
                WolfCondition.tick(),
                (wolf, level) -> {
                    LivingEntity target = wolf.getTarget();
                    boolean checkTarget = target != null;

                    boolean isAngryAndOnBoat = wolf.isAngry() && wolf.isPassenger() && checkTarget && target.distanceTo(wolf) > 3;
                    if (isAngryAndOnBoat) {
                        wolf.unRide();
                    }

                    if (wolf.getOwner() == null) return;
                    boolean shouldLeapAtTarget = checkTarget && wolf.distanceTo(target) < 2 && !wolf.walkAnimation.isMoving();
                    if (shouldLeapAtTarget && level.getRandom().nextFloat() < 0.33f) {
                        wolf.jumpFromGround();
                    }
                }
        ));
        TICK_RULES.add(WolfBehaviorRule.forAny(WolfCondition.tick(), (wolf, _) -> {
            if (TOTVWConfig.get().LOG_ENCHANTMENT_SHOW_WOLF_CD) setWolfConfiguration(wolf, 0);
            if (TOTVWConfig.get().SERVER_OTHER_COOLDOWNS) {
                depleteCooldown(wolf, TIMER_AIR_SUPPLY);
            }
            if (TOTVWConfig.get().SERVER_SKILL_COOLDOWNS) {
                depleteCooldown(wolf, CD_BLESSING_OF_THE_VERDANT_WIND);
                depleteCooldown(wolf, CD_BLOODLUST_SKILL_PARALYZE);
                depleteCooldown(wolf, CD_MIGHT_SKILL_RUPTURE);
                depleteCooldown(wolf, CD_IGNORE_HIGH_DAMAGE);
            } else setWolfConfiguration(wolf, 1);

            SkillUtil.notifyReset(wolf, VERDANT_BLESSING);
            SkillUtil.notifyReset(wolf, PARALYZER);

            processCDNotify(wolf,
                    CD_BLESSING_OF_THE_VERDANT_WIND,
                    NOTIFY_BLESSING_OF_THE_VERDANT_WIND,
                    VWColors.VERDANT_WIND_MUTED,
                    "§nVerdant Wind's Blessing§f cooldown reset for §r" + wolfName(wolf)
            );
            processCDNotify(wolf,
                    CD_BLOODLUST_SKILL_PARALYZE,
                    NOTIFY_BLOODLUST_SKILL_PARALYZE,
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

        wolf.setAttached(TIMER_AIR_SUPPLY, 3);
        wolf.setAttached(NOTIFY_AIR_SUPPLY, 1);
    }
    private static void runPlayerBlessing(Wolf wolf, ServerLevel level) {
        boolean checkFirst = wolf.getAttachedOrElse(VWAttachments.Wolf.WOLF_BENEDICTION, 0) > 1
                && TOTVWConfig.get().SERVER_WOLF_SHARES_BENEDICTION_STACK
                && !TOTVWConfig.get().SERVER_ALWAYS_TRIGGER_BLESSING;
        if (checkFirst) return;

        LivingEntity player = wolf.getOwner();
        if (player == null) return;
        if (!player.isAlive()) return;

        var SCAN_DISTANCE = TOTVWConfig.get().SERVER_WOLF_PLAYER_SCAN_DISTANCE * 16;
        var HEALTH_THRESHOLD = TOTVWConfig.get().SERVER_BENEDICTION_HEALTH_THRESHOLD * 0.01f;

        if (player.getHealth() >= player.getMaxHealth() * HEALTH_THRESHOLD) return;
        if (wolf.distanceTo(player) > SCAN_DISTANCE) return;
        
        rewriteEffect(player, MobEffects.RESISTANCE, sec(6), 254);
        rewriteEffect(player, VWEffects.BLESSING_OF_THE_VERDANT_WIND, sec(30), 2);
        if (entityEnchantmentLVL(player, EquipmentSlot.CHEST, BENEDICTION_OF_THE_VERDANT_MOUNTAINS) > 0) {
            removeEffect(player, MobEffects.POISON);
            removeEffect(player, MobEffects.WITHER);
        }
        player.heal(triggerHeal(wolf, player));
        VWUtil.sendToChat(wolf, VWColors.VERDANT_WIND, true, wolfName(wolf) + " has granted you the §nVerdant Wind's Blessing§r");

        VWParticleEffects.triggerBenedictionParticles(wolf, 1);
        verdantBlessingAfterEffects(level, wolf);
    }
    private static int minutes(int min) {
        return min * 60;
    }
    private static void runEnchantmentsOnDamage(Wolf wolf, ServerLevel level) {
        var victim = CURRENT_VICTIM.get();
        if (victim == null) return;
        LivingEntity player = wolf.getOwner();
        float victimHealth = victim.getHealth();
        float victimMaxHealth = victim.getMaxHealth();

        int ACTIVE_IGNITION = wolfEnchantmentLVL(wolf, VWEnchantments.WOLF_EFFECT_IGNITION);
        int ACTIVE_POISONING = wolfEnchantmentLVL(wolf, VWEnchantments.WOLF_EFFECT_POISONING);
        int ACTIVE_WITHERING = wolfEnchantmentLVL(wolf, VWEnchantments.WOLF_EFFECT_WITHERING);
        int ACTIVE_LIFTING = wolfEnchantmentLVL(wolf, VWEnchantments.WOLF_EFFECT_LIFTING);
        int ACTIVE_BLOODLUST = wolfEnchantmentLVL(wolf, VWEnchantments.WOLF_EFFECT_BLOODLUST);
        int ACTIVE_OOZING = wolfEnchantmentLVL(wolf, VWEnchantments.WOLF_EFFECT_OOZING);
        int ACTIVE_MIGHT = wolfEnchantmentLVL(wolf, VWEnchantments.WOLF_EFFECT_MIGHT);
        int ACTIVE_GNAWING = wolfEnchantmentLVL(wolf, VWEnchantments.WOLF_EFFECT_GNAWING);
        int ACTIVE_BENEDICTION = wolfEnchantmentLVL(wolf, BENEDICTION_OF_THE_VERDANT_MOUNTAINS);
        int ACTIVE_PROTECTION = wolfEnchantmentLVL(wolf, Enchantments.PROTECTION);
        int ACTIVE_FIRE_PROTECTION = wolfEnchantmentLVL(wolf, Enchantments.FIRE_PROTECTION);
        int ACTIVE_BLAST_PROTECTION = wolfEnchantmentLVL(wolf, Enchantments.BLAST_PROTECTION);
        int ACTIVE_MENDING = wolfEnchantmentLVL(wolf, Enchantments.MENDING);

        DamageSource DMG_SOURCE_BLEEDING = VWDamageTypes.bleeding(level);

        int CD_PARALYZE = wolf.getAttachedOrElse(CD_BLOODLUST_SKILL_PARALYZE, 0);
        int CD_RUPTURE = wolf.getAttachedOrElse(CD_MIGHT_SKILL_RUPTURE, 0);


        List<Wolf> babyWolves = level.getEntities(EntityType.WOLF, wolf.getBoundingBox().inflate(8), test -> test.isBaby() && test.getAttachedOrElse(WOLF_PARENTS_ID , "").contains(wolf.getStringUUID() + ":baby"));

        if (ACTIVE_MENDING > 0) {
            float conversion = ACTIVE_BENEDICTION > 0 ? 0.25f : 0.1f;
            int dmg = Mth.ceil(wolf.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * conversion) + 2;
            ItemStack itemStack = wolf.getBodyArmorItem();
            if (itemStack.isDamaged()) {
                int toRepairFromXpAmount = EnchantmentHelper.modifyDurabilityToRepairFromXp((ServerLevel) wolf.level(), itemStack, dmg);
                int repair = Math.min(toRepairFromXpAmount, itemStack.getDamageValue());
                itemStack.setDamageValue(itemStack.getDamageValue() - repair);
            }
        }

        if (ACTIVE_BLAST_PROTECTION > 0 || ACTIVE_FIRE_PROTECTION > 0 || ACTIVE_PROTECTION > 0) {
            if (!babyWolves.isEmpty()) {
                for (Wolf babyWolf : babyWolves) {
                    Holder<MobEffect> effect;
                    int amp;
                    if (ACTIVE_BLAST_PROTECTION > 0 || ACTIVE_PROTECTION > 0) {
                        amp = ACTIVE_BLAST_PROTECTION > 0 ? 2 : 0;
                        effect = MobEffects.RESISTANCE;
                    } else {
                        amp = 1;
                        effect = MobEffects.FIRE_RESISTANCE;
                    }
                    addEffect(babyWolf, effect, ACTIVE_MIGHT * sec(3), amp);
                }
            }
        }

        if (ACTIVE_IGNITION > 0) {
            int burnTime = ACTIVE_IGNITION * 3;
            boolean isWithinHotBiomes = isInBiome(wolf, BiomeTags.IS_NETHER) || wolf.level().getBiome(wolf.blockPosition()).value().getBaseTemperature() > 1.8f;
            int computedTime = isWithinHotBiomes ? (burnTime * 2) : burnTime;
            if (ACTIVE_IGNITION >= 3) {
                removeEffect(victim, MobEffects.FIRE_RESISTANCE);
            }

            if (victim.fireImmune()) {
                float finalDMG = isWithinHotBiomes ? computedTime * 3 : ACTIVE_IGNITION * 2;
                victim.hurtServer(level, VWDamageTypes.scorchingHeat(level), finalDMG);
            } else {
                victim.igniteForSeconds(computedTime);
            }
        }

        if (ACTIVE_POISONING > 0) {
            if (ACTIVE_POISONING >= 3) {
                removeEffect(victim, MobEffects.REGENERATION);
            }
            addOrStackEffect(victim, MobEffects.POISON, sec(2) + (ACTIVE_POISONING * sec(2)), Math.min(ACTIVE_POISONING, 2));
        }

        if (ACTIVE_WITHERING > 0) {
            if (ACTIVE_WITHERING >= 3) {
                removeEffect(victim, MobEffects.REGENERATION);
            }
            rewriteEffect(victim, MobEffects.WITHER, ACTIVE_WITHERING * sec(2), ACTIVE_WITHERING);
            if (victim.isInvulnerableTo(level, victim.level().damageSources().wither())) {
                victim.hurtServer(level, DMG_SOURCE_BLEEDING, victimHealth * 0.5f);
            }
        }

        if (ACTIVE_LIFTING > 0) {
            if (victim.hasEffect(MobEffects.LEVITATION)) {
                double random = level.getRandom().nextDouble();
                victim.knockback(ACTIVE_LIFTING, random, random);
            } else {
                addHiddenEffect(victim, MobEffects.LEVITATION, 10, ACTIVE_LIFTING * 3);
            }
        }

        if (ACTIVE_BLOODLUST > 0) {
            int paralyzeTime = sec(3) + (sec(ACTIVE_BLOODLUST * 3));

            addEffect(wolf, VWEffects.BLOODLUST, sec(6), ACTIVE_BLOODLUST - 1);
            addEffect(victim, MobEffects.WEAKNESS, ACTIVE_BLOODLUST * sec(6), Math.min(ACTIVE_BLOODLUST, 2));
            if (ACTIVE_BLOODLUST >= 3) {
                addHiddenEffect(victim, MobEffects.SLOWNESS, sec(3), 1);
                removeEffect(victim, MobEffects.SPEED);
                removeEffect(victim, MobEffects.REGENERATION);
            }
            // change later
            boolean checkVictim = victim.is(EntityType.PLAYER) || victim.getMaxHealth() > 20.0;
            if (checkVictim && CD_PARALYZE <= 0 && !victim.hasEffect(VWEffects.PARALYZE)) {
                addHiddenEffect(victim, VWEffects.PARALYZE, paralyzeTime, 0);

                sendToChat(wolf, VWColors.MIGHT_EFFECT, victim.getPlainTextName() + " has been paralyzed for " + (paralyzeTime / sec(1)) + " seconds by " + wolfName(wolf) + "!");
                SkillUtil.startCooldown(wolf, PARALYZER,
                        setDifficultyBasedValue(level, minutes(1), minutes(12), minutes(18), minutes(24)));

                VWUtil.playSound(victim, VWSounds.WOLF_SKILL_PARALYZE, SoundSource.HOSTILE, 0.1f, 0.55f + level.getRandom().nextFloat());
                VWParticleEffects.triggerMightParalyzeParticles(victim, 4);
            }
        }

        if (ACTIVE_OOZING > 0) {
            int defaultTime;
            if (ACTIVE_BLOODLUST > 0) {
                defaultTime = (int) ((ACTIVE_BLOODLUST * 1.50) * min(1));
            } else if (ACTIVE_MIGHT > 0) {
                defaultTime = (int) ((ACTIVE_MIGHT * 1.25) * min(1));
            } else {
                defaultTime = min(1);
            }
            addOrStackEffect(victim, MobEffects.OOZING, defaultTime, 1);
        }

        if (ACTIVE_MIGHT > 0) {
            addEffect(wolf, VWEffects.AMPLIFIED_MIGHT, ACTIVE_MIGHT * sec(3), Math.min(ACTIVE_MIGHT, 2));
            addEffect(wolf, MobEffects.ABSORPTION, ACTIVE_MIGHT * sec(3), 1);
            if (ACTIVE_MIGHT >= 3) {
                removeEffect(victim, MobEffects.RESISTANCE);
                removeEffect(victim, MobEffects.STRENGTH);
                removeEffect(victim, MobEffects.ABSORPTION);

                if (victimHealth <= victimMaxHealth * 0.6f && CD_RUPTURE <= 0) {
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
                    float decreaseTime = ACTIVE_BENEDICTION > 0 ? 0.75f: 1.0f;
                    int finalCD = (int) (setDifficultyBasedValue(level, 7, 14, 21, 28) * decreaseTime);
                    victim.hurtServer(level, DMG_SOURCE_BLEEDING, finalDMG);
                    VWParticleEffects.triggerRuptureParticles(victim);
                    victim.makeSound(SoundEvents.PLAYER_ATTACK_CRIT);
                    SkillUtil.startCooldown(wolf, RUPTURE, finalCD);
                }
            }
            if (ACTIVE_MIGHT >= 5) {
                victim.hurtServer(level, DMG_SOURCE_BLEEDING, wolf.getMaxHealth() * 0.10f);
            }
            if (!babyWolves.isEmpty()) {
                for (Wolf babyWolf : babyWolves) {
                    addEffect(babyWolf, VWEffects.AMPLIFIED_MIGHT, ACTIVE_MIGHT * sec(3), Math.min(ACTIVE_MIGHT, 2));
                    addEffect(babyWolf, MobEffects.ABSORPTION, ACTIVE_MIGHT * sec(3), 1);
                }
            }
        }

        if (ACTIVE_GNAWING > 0) {
            float heal;
            if (ACTIVE_GNAWING == 1) {
                heal = wolf.getMaxHealth() * 0.15f;
            } else {
                heal = wolf.getMaxHealth() * 0.30f;
            }
            if (!babyWolves.isEmpty()) {
                for (Wolf babyWolf : babyWolves) {
                    babyWolf.heal(heal);
                }
            }
            wolf.heal(heal);
            if (player != null && player.getHealth() < player.getMaxHealth()) {
                int playerBlastProtection = entityEnchantmentLVL(player, Enchantments.BLAST_PROTECTION);
                int playerFireProtection = entityEnchantmentLVL(player, Enchantments.FIRE_PROTECTION);
                int playerProjectileProtection = entityEnchantmentLVL(player, Enchantments.PROJECTILE_PROTECTION);
                int playerProtection = entityEnchantmentLVL(player, Enchantments.PROTECTION);
                int playerBenediction = entityEnchantmentLVL(player, EquipmentSlot.CHEST, BENEDICTION_OF_THE_VERDANT_MOUNTAINS);

                float healStrength;
                if (playerBlastProtection > 0) {
                    healStrength = playerBlastProtection * 1.6f;
                } else if (playerFireProtection > 0) {
                    healStrength = playerFireProtection * 1.3f;
                } else if (playerProjectileProtection > 0 || playerProtection > 0) {
                    healStrength = playerProjectileProtection + playerProtection;
                } else {
                    return;
                }
                if (playerBenediction > 0) healStrength *= 1.20f;
                float baseCap = ACTIVE_GNAWING > 1 ? player.getMaxHealth() * 0.4f : player.getMaxHealth() * 0.2f;
                player.heal(Math.min(baseCap, healStrength));
            }
        }
    }
    
    private static void runEnchantmentsOnTick(Wolf wolf, ServerLevel level) {
        LivingEntity player = wolf.getOwner();

        int ACTIVE_BENEDICTION = wolfEnchantmentLVL(wolf, BENEDICTION_OF_THE_VERDANT_MOUNTAINS);
        int ACTIVE_IGNITION = wolfEnchantmentLVL(wolf, VWEnchantments.WOLF_EFFECT_IGNITION);
        int ACTIVE_MIGHT = wolfEnchantmentLVL(wolf, VWEnchantments.WOLF_EFFECT_MIGHT);
        int ACTIVE_FIRE_PROTECTION = wolfEnchantmentLVL(wolf, Enchantments.FIRE_PROTECTION);

        if (ACTIVE_BENEDICTION > 0 && ACTIVE_IGNITION > 0 && ACTIVE_FIRE_PROTECTION >= 3 && isInBiome(wolf, BiomeTags.IS_NETHER)) {
            addHiddenEffect(wolf, MobEffects.FIRE_RESISTANCE, sec(3), 8);
            if (player != null && isInBiome(player, BiomeTags.IS_NETHER) && wolf.distanceTo(player) < 24) addHiddenEffect(player, MobEffects.FIRE_RESISTANCE, sec(3), 8);
            List<Wolf> babyWolves = level.getEntities(EntityType.WOLF, wolf.getBoundingBox().inflate(8), test ->
                    test.isBaby()
                            && test.getAttachedOrElse(WOLF_PARENTS_ID , "").contains(wolf.getStringUUID() + ":baby")
                            && isInBiome(test, BiomeTags.IS_NETHER)
                            && wolf.distanceTo(test) < 24);

            if (babyWolves.isEmpty()) return;
            for (Wolf babyWolf : babyWolves) {
                addHiddenEffect(babyWolf, MobEffects.FIRE_RESISTANCE, sec(3), 8);
            }
        }
        if (ACTIVE_IGNITION > 0 || ACTIVE_MIGHT > 3) {
            if (wolf.isOnFire()) {
                wolf.extinguishFire();
            }
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
                    CD_BLESSING_OF_THE_VERDANT_WIND,
                    NOTIFY_BLESSING_OF_THE_VERDANT_WIND,
                    VWColors.VERDANT_WIND,
                   "§nVerdant Wind's Blessing§r"
            );

    private static final WolfSkillDefinition PARALYZER =
            new WolfSkillDefinition(
                    CD_BLOODLUST_SKILL_PARALYZE,
                    NOTIFY_BLOODLUST_SKILL_PARALYZE,
                    VWColors.BLOODLUST_EFFECT,
                "§nBloodlust Skill: Paralyzer§r"
            );

    private static final WolfSkillDefinition RUPTURE =
            new WolfSkillDefinition(
                    CD_MIGHT_SKILL_RUPTURE,
                    NOTIFY_MIGHT_SKILL_RUPTURE,
                    VWColors.MIGHT_EFFECT,
                    "§nMight Skill: Rupture§r"
            );



    private static String wolfName(Wolf wolf) {
        String wolfName;
        if (wolf.getPlainTextName().equals("Wolf")) {wolfName = "§dWolf§r";} else {wolfName = "§d" + wolf.getPlainTextName() + "§r";}
        return wolfName;
    }

    private static String playerName(Wolf wolf) {
        if (wolf.getOwner() != null) {
            return "§d" + wolf.getOwner().getPlainTextName() + "§r";
        } else return null;
    }

    public static final ThreadLocal<LivingEntity> CURRENT_VICTIM = new ThreadLocal<>();
    public static void wireOnDamageEvent() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register(
                (victim, damageSource, _, _, _) -> getWolfVictimThread(victim,  damageSource)
        );

        // test purpose: for wolf to still get the enchantment benefits even after entity dies
        ServerLivingEntityEvents.AFTER_DEATH.register(
                VWWolfBehaviors::getWolfVictimThread
        );
    }
    private static void wireTickEvent() {
        if (TICK_RULES.isEmpty()) return;
        ServerTickEvents.END_SERVER_TICK.register((MinecraftServer server) -> {
            for (var serverLevel : server.getAllLevels()) {
                serverLevel.getEntities(
                        EntityType.WOLF,
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
    private VWWolfBehaviors() {}
}
