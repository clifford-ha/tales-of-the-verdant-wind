package cliffordha.totvw.entity.wolf;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.config.TOTVWConfig;
import cliffordha.totvw.datagen.VWDamageTypes;
import cliffordha.totvw.util.VWGlobalUtil;
import cliffordha.totvw.entity.skill.VWSkillProcessor;
import cliffordha.totvw.entity.skill.WolfSkillDefinition;
import cliffordha.totvw.entity.skill.SkillUtil;
import cliffordha.totvw.registry.VWEffects;
import cliffordha.totvw.registry.VWEnchantments;
import cliffordha.totvw.registry.VWAttachments;
import cliffordha.totvw.registry.VWParticleEffects;
import cliffordha.totvw.registry.VWSounds;
import cliffordha.totvw.tag.VWBiomeTags;
import cliffordha.totvw.registry.VWColors;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.skeleton.WitherSkeleton;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.biome.Biomes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static cliffordha.totvw.util.VWGlobalUtil.*;
import static cliffordha.totvw.entity.skill.VWSkillProcessor.*;

public class VWWolfBehaviors {
    private static final SoundEvent[] DISTANT_HOWL_SOUNDS = {
            VWSounds.WOLF_HOWL_A,
            VWSounds.WOLF_HOWL_B1,
            VWSounds.WOLF_HOWL_B2,
            VWSounds.WOLF_HOWL_B3};

    private static final List<WolfBehaviorRule> ON_DAMAGE_RULES = new ArrayList<>();
    private static final List<WolfBehaviorRule> TICK_RULES = new ArrayList<>();

    public static void registerModWolfBehaviors() {
        registerTamedRules();
        registerWildRules();
        registerSharedRules();

        wireOnDamageEvent();
        wireTickEvent();
    }

    private static void registerTamedRules() {
        TICK_RULES.add(WolfBehaviorRule.forTamed(
                WolfCondition.tick(0, 3)
                        .and(WolfCondition.isInWater()
                        .and(WolfCondition.ownerFarther(4))),
                (wolf, _) -> wolf.tryToTeleportToOwner()
        ));
        TICK_RULES.add(WolfBehaviorRule.forTamed(
                WolfCondition.tick(0, 1),
                VWWolfBehaviors::runEnchantmentsOnTick
        ));
        TICK_RULES.add(WolfBehaviorRule.forTamed(
                WolfCondition.companionIsCritical(
                        () -> TOTVWConfig.get().LOW_HEALTH_THRESHOLD * 0.01f,
                        () -> TOTVWConfig.get().MAX_WOLF_PLAYER_SCAN_DISTANCE)
                        .and(WolfCondition.hasBodyArmor())
                        .and(WolfCondition.checkNoAttached(VWAttachments.Wolf.WOLF_CD_BLESSING_OF_THE_VERDANT_WIND)),
                VWWolfBehaviors::runPlayerBlessing
        ));
        TICK_RULES.add(WolfBehaviorRule.forTamed(
                WolfCondition.tick(0, 1)
                        .and(WolfCondition.checkNoAttached(VWAttachments.Wolf.WOLF_TIMER_AIR_SUPPLY))
                        .and(WolfCondition.isUnderWater())
                        .and(WolfCondition.airSupplyLowerThan(0.5f))
                        .and(WolfCondition.unableToTeleport()),
                VWWolfBehaviors::warnOwner
        ));
    }
    private static void registerWildRules() {
        ON_DAMAGE_RULES.add(WolfBehaviorRule.forWild(WolfCondition.isAngry(), (wolf, level) -> {
            var victim = CURRENT_VICTIM.get();
            boolean inVerdantBiomes = level.getBiome(wolf.blockPosition()).is(VWBiomeTags.IS_VERDANT_BIOMES);
            if (inVerdantBiomes) {
                addEffect(victim, MobEffects.WEAKNESS, sec(3), 2);
            } else {
                addEffect(victim, MobEffects.WEAKNESS, sec(3), 0);
            }
            addHiddenEffect(wolf, MobEffects.SPEED, sec(3), 0);
        }));
    }
    private static void registerSharedRules() {
        ON_DAMAGE_RULES.add(WolfBehaviorRule.forAny(
                WolfCondition.hasBodyArmor(),
                VWWolfBehaviors::runEnchantmentsOnDamage
        ));
        ON_DAMAGE_RULES.add(WolfBehaviorRule.forAny(
                WolfCondition.alwaysTrue(),
                (wolf, _) -> {
                    var victim = CURRENT_VICTIM.get();
                    if (victim == null) return;
                    if (wolf.getAttachedOrElse(VWAttachments.Wolf.WOLF_TRY_SAVE_STATUS, 0) == 1) {
                        int points = wolf.getAttachedOrElse(VWAttachments.Wolf.WOLF_TRY_SAVE_POINTS, 0);
                        wolf.setAttached(VWAttachments.Wolf.WOLF_TRY_SAVE_STATUS, 2);
                        victim.setAttached(VWAttachments.Wolf.WOLF_TRY_SAVE_STATUS, 2);
                        wolf.setAttached(VWAttachments.Wolf.WOLF_TRY_SAVE_POINTS, points + 1);
                    }
                }
        ));
        TICK_RULES.add(WolfBehaviorRule.forAny(
                WolfCondition.newSoundsEnable(),
                (wolf, level) -> {
                    if (wolf.isAngry()) return;
                    if (wolf.level().getMaxLocalRawBrightness(wolf.blockPosition()) > 11) return;
                    boolean isInForest = wolf.level().getBiome(wolf.blockPosition()).is(VWBiomeTags.IS_VERDANT_BIOMES)
                            || wolf.level().getBiome(wolf.blockPosition()).is(Biomes.FOREST)
                            || wolf.level().getBiome(wolf.blockPosition()).is(Biomes.FLOWER_FOREST)
                            || wolf.level().getBiome(wolf.blockPosition()).is(Biomes.DARK_FOREST);
                    if (isInForest) {
                        if (level.getRandom().nextInt(TOTVW.setTime(1, 0)) == 0 && level.getRandom().nextFloat() < 0.05f) {
                            SoundEvent sound = DISTANT_HOWL_SOUNDS[level.getRandom().nextInt(DISTANT_HOWL_SOUNDS.length)];
                            level.playSound(null, wolf.blockPosition(), sound, SoundSource.AMBIENT, 0.2f + level.getRandom().nextFloat() * 0.5f, 0.8f + level.getRandom().nextFloat() * 0.4f);
                        }
                    }
                }
        ));
        TICK_RULES.add(WolfBehaviorRule.forAny(
                WolfCondition.tick(1, 0).and(WolfCondition.healthBelow(0.8f)),
                VWWolfBehaviors::runNaturalHealOnTick
        ));
        TICK_RULES.add(WolfBehaviorRule.forAny(
                WolfCondition.tick(0, 1)
                        .and(WolfCondition.isPassenger())
                        .and(WolfCondition.isAngry()),
                (wolf, _) -> wolf.unRide()
        ));
        TICK_RULES.add(WolfBehaviorRule.forAny(WolfCondition.tick(0, 1), (wolf, _) -> {
            if (TOTVWConfig.get().DEBUG_PRINT_LOGS) {
                VWSkillProcessor.setWolfConfiguration(wolf, 0);}
            if (TOTVWConfig.get().OTHER_ATTACHMENT_CD) {
                VWSkillProcessor.depleteCooldown(wolf,  VWAttachments.Wolf.WOLF_TIMER_AIR_SUPPLY);
            }
            if (TOTVWConfig.get().ATTACHMENT_SKILL_CD) {
                VWSkillProcessor.depleteCooldown(wolf, VWAttachments.Wolf.WOLF_CD_BLESSING_OF_THE_VERDANT_WIND);
                VWSkillProcessor.depleteCooldown(wolf, VWAttachments.Wolf.WOLF_CD_BLOODLUST_SKILL_PARALYZE);
                VWSkillProcessor.depleteCooldown(wolf, VWAttachments.Wolf.WOLF_CD_MIGHT_SKILL_RUPTURE);
            } else {
                VWSkillProcessor.setWolfConfiguration(wolf, 1);
            }

            SkillUtil.notifyReset(wolf, VERDANT_BLESSING);
            SkillUtil.notifyReset(wolf, PARALYZER);

            processCDNotify(wolf,
                    VWAttachments.Wolf.WOLF_CD_BLESSING_OF_THE_VERDANT_WIND,
                    VWAttachments.Wolf.WOLF_NOTIFY_BLESSING_OF_THE_VERDANT_WIND,
                    VWColors.VERDANT_WIND_MUTED,
                    "§nVerdant Wind's Blessing§f cooldown reset for §r" + wolfName(wolf)
            );
            processCDNotify(wolf,
                    VWAttachments.Wolf.WOLF_CD_BLOODLUST_SKILL_PARALYZE,
                    VWAttachments.Wolf.WOLF_NOTIFY_BLOODLUST_SKILL_PARALYZE,
                    VWColors.BLOODLUST_EFFECT_MUTED,
                    "§nBloodlust Skill: Paralyzer§r cooldown reset for §r" + wolfName(wolf)
            );
        }));
    }


    private static void warnOwner(Wolf wolf, ServerLevel level) {
        if (wolf.getAirSupply() <= wolf.getMaxAirSupply() * 0.5 && wolf.getAirSupply() > 0.0f) {
            wolf.makeSound(new SoundEvent(Identifier.withDefaultNamespace("entity.wolf.whine"), Optional.of(16.0f)));
            notifyFromWolf(wolf, VWColors.MIGHT_EFFECT_MUTED, "[" + wolfName(wolf) + "] My air supply is about to run out...");
        }

        wolf.setAttached(VWAttachments.Wolf.WOLF_TIMER_AIR_SUPPLY, sec(3));
        wolf.setAttached(VWAttachments.Wolf.WOLF_NOTIFY_AIR_SUPPLY, 1);
    }
    private static void runPlayerBlessing(Wolf wolf, ServerLevel level) {
        if (wolfEnchantmentLVL(wolf, VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS) == 0) return;
        LivingEntity player = wolf.getOwner();
        if (player == null) return;
        
        rewriteEffect(player, MobEffects.RESISTANCE, sec(6), 254);
        rewriteEffect(player, VWEffects.BLESSING_OF_THE_VERDANT_WIND, sec(30), 2);
        if (playerEnchantmentLVL(player, VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS) > 0) {
            removeEffect(player, MobEffects.POISON);
            removeEffect(player, MobEffects.WITHER);
        }
        player.heal(triggerHeal(wolf, player));
        notifyFromWolf(wolf, VWColors.VERDANT_WIND, true, "Granted §nVerdant Wind's Blessing§r to " + playerName(wolf));

        VWParticleEffects.triggerBenedictionParticles(wolf, 1);
        verdantBlessingAfterEffects(level, wolf);
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

        int ACTIVE_BENEDICTION = wolfEnchantmentLVL(wolf, VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS);

        int ACTIVE_PROTECTION = wolfEnchantmentLVL(wolf, Enchantments.PROTECTION);
        int ACTIVE_FIRE_PROTECTION = wolfEnchantmentLVL(wolf, Enchantments.FIRE_PROTECTION);
        int ACTIVE_BLAST_PROTECTION = wolfEnchantmentLVL(wolf, Enchantments.BLAST_PROTECTION);

        boolean IN_NETHER = level.getBiome(wolf.blockPosition()).is(BiomeTags.IS_NETHER);

        int CD_PARALYZE = wolf.getAttachedOrElse(VWAttachments.Wolf.WOLF_CD_BLOODLUST_SKILL_PARALYZE, 0);
        int CD_RUPTURE = wolf.getAttachedOrElse(VWAttachments.Wolf.WOLF_CD_MIGHT_SKILL_RUPTURE, 0);

        if (ACTIVE_IGNITION > 0) {
            int burnTime = ACTIVE_IGNITION * 3;
            if (ACTIVE_IGNITION == 3) { removeEffect(victim, MobEffects.FIRE_RESISTANCE); }
            victim.igniteForSeconds(burnTime);
            if (victim.fireImmune()) {
                victim.hurtServer(level, level.damageSources().magic(), ACTIVE_IGNITION * 2);
                if (IN_NETHER) {
                    victim.hurtServer(level, level.damageSources().magic(), ACTIVE_IGNITION);
                }
            }
        }

        if (ACTIVE_POISONING > 0) {
            if (ACTIVE_POISONING >= 3) { removeEffect(victim, MobEffects.REGENERATION); }
            addEffect(victim, MobEffects.POISON, sec(2) + (ACTIVE_POISONING * sec(2)), Math.min(ACTIVE_POISONING, 2));
        }

        if (ACTIVE_WITHERING > 0) {
            if (ACTIVE_WITHERING >= 3) { removeEffect(victim, MobEffects.REGENERATION); }
            rewriteEffect(victim, MobEffects.WITHER, ACTIVE_WITHERING * sec(2), ACTIVE_WITHERING);
            if (victim instanceof WitherSkeleton skeleton) {
                skeleton.hurtServer(level, level.damageSources().magic(), victimHealth * 0.5f);
            }
        }

        if (ACTIVE_LIFTING > 0) {
            if (victim.hasEffect(MobEffects.LEVITATION)) {
                var random = level.getRandom().nextDouble();
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
                removeEffect(victim, MobEffects.RESISTANCE);
                removeEffect(victim, MobEffects.REGENERATION);
            }
            if ((victim.getMaxHealth() > 40.0) && CD_PARALYZE <= 0) {
                addHiddenEffect(victim, VWEffects.PARALYZE, paralyzeTime, 0);

                notifyFromWolf(wolf, VWColors.MIGHT_EFFECT, wolfName(wolf) + " | " + victim.getName().getString() + " has been paralyzed for " + (paralyzeTime / sec(1)) + " seconds.");
                SkillUtil.startCooldown(wolf, PARALYZER,
                        setDifficultyBasedValue(level, min(1), min(12), min(18), min(24)));

                VWGlobalUtil.playSound(victim, VWSounds.WOLF_SKILL_PARALYZE, SoundSource.HOSTILE);
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
                defaultTime = min(1); }
            addEffect(victim, MobEffects.OOZING, defaultTime, 1);
        }

        if (ACTIVE_MIGHT > 0) {
            DamageSource bleed = VWDamageTypes.create(level, VWDamageTypes.BLEEDING);
            addEffect(wolf, VWEffects.AMPLIFIED_MIGHT, ACTIVE_MIGHT * sec(3), Math.min(ACTIVE_MIGHT, 2));
            addEffect(wolf, MobEffects.ABSORPTION, ACTIVE_MIGHT * sec(3), Math.min(ACTIVE_MIGHT, 2));
            victim.hurtServer(level, bleed, wolf.getMaxHealth() * 0.10f);
            if (ACTIVE_MIGHT >= 3) {
                removeEffect(victim, MobEffects.STRENGTH);

                if (victimHealth <= victimMaxHealth * 0.5f && CD_RUPTURE <= 0) {
                    float finalDMG;
                    if (player != null) {
                        if (wolf.distanceTo(player) < 4) {
                            finalDMG = wolf.getMaxHealth() * 0.5f;
                        } else {
                            finalDMG = wolf.getMaxHealth() * 0.75f;
                        }
                    } else {
                        finalDMG = wolf.getMaxHealth() * 1.8f;
                    }
                    victim.hurtServer(level, bleed, finalDMG);
                    SkillUtil.startCooldown(wolf, RUPTURE,
                            setDifficultyBasedValue(level, sec(7), sec(14), sec(21), sec(28)));
                }
            }
        }

        if (ACTIVE_GNAWING > 0) {
            float heal;
            if (ACTIVE_GNAWING == 1) {
                heal = wolf.getMaxHealth() * 0.15f;
            } else {
                heal = wolf.getMaxHealth() * 0.30f;}

            wolf.heal(heal);
            if (ACTIVE_PROTECTION > 0 || ACTIVE_BLAST_PROTECTION > 0 || ACTIVE_FIRE_PROTECTION > 0) {
                if (player == null) return;
                player.heal( Math.max(ACTIVE_PROTECTION, ( Math.max(ACTIVE_BLAST_PROTECTION, ACTIVE_FIRE_PROTECTION))) );
            }
            if (playerEnchantmentLVL(wolf, VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS) > 0) {
                if (player == null) return;
                player.heal(1.0f);
            }
        }

        if (IN_NETHER && ACTIVE_FIRE_PROTECTION > 0 && ACTIVE_BENEDICTION > 0 && !victim.fireImmune()) {
            float dmg = ACTIVE_IGNITION > 0 ? (ACTIVE_FIRE_PROTECTION * 2) + ACTIVE_IGNITION : ACTIVE_FIRE_PROTECTION * 2;
            victim.hurtServer(level, level.damageSources().onFire(), dmg);
        }
    }
    private static void runEnchantmentsOnTick(Wolf wolf, ServerLevel level) {
        LivingEntity player = wolf.getOwner();
        boolean WITH_PLAYER_STATS = player != null;

        int ACTIVE_BENEDICTION = wolfEnchantmentLVL(wolf, VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS);
        int ACTIVE_IGNITION = wolfEnchantmentLVL(wolf, VWEnchantments.WOLF_EFFECT_IGNITION);
        int ACTIVE_FIRE_PROTECTION = wolfEnchantmentLVL(wolf, Enchantments.FIRE_PROTECTION);

        boolean bothInNether = player != null && level.getBiome(player.blockPosition()).is(BiomeTags.IS_NETHER) || level.getBiome(wolf.blockPosition()).is(BiomeTags.IS_NETHER);

        if (WITH_PLAYER_STATS) {
            if (bothInNether && ACTIVE_BENEDICTION > 0 && ACTIVE_IGNITION > 0 && ACTIVE_FIRE_PROTECTION >= 3) {
                addHiddenEffect(wolf, MobEffects.FIRE_RESISTANCE, sec(3), 4);
                addHiddenEffect(player, MobEffects.FIRE_RESISTANCE, sec(3), 4);
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
                    VWAttachments.Wolf.WOLF_CD_BLESSING_OF_THE_VERDANT_WIND,
                    VWAttachments.Wolf.WOLF_NOTIFY_BLESSING_OF_THE_VERDANT_WIND,
                    VWColors.VERDANT_WIND,
                   "§nVerdant Wind's Blessing§r"
            );

    private static final WolfSkillDefinition PARALYZER =
            new WolfSkillDefinition(
                    VWAttachments.Wolf.WOLF_CD_BLOODLUST_SKILL_PARALYZE,
                    VWAttachments.Wolf.WOLF_NOTIFY_BLOODLUST_SKILL_PARALYZE,
                    VWColors.BLOODLUST_EFFECT,
                "§nBloodlust Skill: Paralyzer§r"
            );

    private static final WolfSkillDefinition RUPTURE =
            new WolfSkillDefinition(
                    VWAttachments.Wolf.WOLF_CD_MIGHT_SKILL_RUPTURE,
                    VWAttachments.Wolf.WOLF_NOTIFY_MIGHT_SKILL_RUPTURE,
                    VWColors.MIGHT_EFFECT,
                    "§nMight Skill: Rupture§r"
            );



    private static String wolfName(Wolf wolf) {
        String wolfName;
        if (wolf.getName().getString().equals("Wolf")) {wolfName = "§dWolf§r";} else {wolfName = "§d" + wolf.getName().getString() + "§r";}
        return wolfName; }

    private static String playerName(Wolf wolf) {
        if (wolf.getOwner() != null) {
            return "§d" + wolf.getOwner().getName().getString() + "§r";
        } else return null; }



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
