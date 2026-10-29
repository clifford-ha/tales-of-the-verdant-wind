package cliffordha.totvw.entity.wolf;

import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.entity.skill.WolfSkills;
import cliffordha.totvw.entity.skills.RunestoneEffects;
import cliffordha.totvw.entity.skills.VerdantWindBlessing;
import cliffordha.totvw.registry.*;
import cliffordha.totvw.registry.attachments.Runestone;
import cliffordha.totvw.registry.attachments.PlayerPrefs;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import cliffordha.totvw.util.ExtUtil;
import cliffordha.totvw.entity.skill.SkillManager;
import cliffordha.totvw.tag.VWBiomeTags;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Monster;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static cliffordha.totvw.entity.skill.SkillManager.*;
import static cliffordha.totvw.util.ExtUtil.*;

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
                int duration = (hardMode ? 7 : 3) + additional;
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
                        WolfAttackEffects.onPostAttack(wolf, level, null, true, null);
                    }
                    if (Runestone.hasRunestone(wolf)) {
                        processRunestones(wolf, level);
                    }
                }
        ));
        TICK_RULES.add(WolfBehaviorRule.forTamed(
                WolfCondition.tick(0, 3),
                (wolf, level) ->  {
                    LivingEntity owner = wolf.getOwner();

                    //actions with owner check
                    if (owner == null) return;

                    float HEALTH_THRESHOLD = owner.getAttachedOrElse(PlayerPrefs.BENEDICTION_HEALTH_THRESHOLD,
                            VWConfig.get().SYNCED_BENEDICTION_HEALTH_THRESHOLD) * 0.01f;

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
               WolfCondition.isInBiomes(VWBiomeTags.FOREST_WHERE_WOLVES_HOWL)
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
            WolfAttackEffects.onTick(wolf, level);

            if (VWConfig.get().SERVER_OTHER_COOLDOWNS) {
                depleteCooldown(wolf, WolfAttachment.TIMER_AIR_SUPPLY);
            }
            if (VWConfig.get().SERVER_SKILL_COOLDOWNS) {
                depleteCooldown(wolf, WolfAttachment.CD_BLESSING_OF_THE_VERDANT_WIND);
                depleteCooldown(wolf, WolfAttachment.CD_BLOODLUST_SKILL_PARALYZE);
                depleteCooldown(wolf, WolfAttachment.CD_MIGHT_SKILL_RUPTURE);
                depleteCooldown(wolf, WolfAttachment.CD_IGNORE_HIGH_DAMAGE);
            } else setWolfConfiguration(wolf);

            SkillManager.notifyReset(wolf, WolfSkills.VERDANT_BLESSING);
            SkillManager.notifyReset(wolf, WolfSkills.PARALYZER);

            processCDNotify(wolf,
                    WolfAttachment.CD_BLESSING_OF_THE_VERDANT_WIND,
                    WolfAttachment.NOTIFY_BLESSING_OF_THE_VERDANT_WIND,
                    VWColors.VERDANT_WIND_MUTED,
                    "§nVerdant Wind's Blessing§f cooldown reset for §r" + wolfName(wolf)
            );
            processCDNotify(wolf,
                    WolfAttachment.CD_BLOODLUST_SKILL_PARALYZE,
                    WolfAttachment.NOTIFY_BLOODLUST_SKILL_PARALYZE,
                    VWColors.BLOODLUST_MUTED,
                    "§nBloodlust Skill: Paralyzer§r cooldown reset for §r" + wolfName(wolf)
            );
        }));
    }


    private static void warnOwner(Wolf wolf, ServerLevel level) {
        if (wolf.getAirSupply() <= wolf.getMaxAirSupply() * 0.5 && wolf.getAirSupply() > 0.0f) {
            wolf.makeSound(new SoundEvent(Identifier.withDefaultNamespace("entity.wolf.whine"), Optional.of(16.0f)));
            sendToChat(wolf, VWColors.MIGHT_MUTED, "[" + wolfName(wolf) + "] My air supply is about to run out...");
            playNotification(wolf);
        }

        wolf.setAttached(WolfAttachment.TIMER_AIR_SUPPLY, 3);
        wolf.setAttached(WolfAttachment.NOTIFY_AIR_SUPPLY, 1);
    }
    public static void runPlayerBlessing(Wolf wolf, LivingEntity player, ServerLevel level) {
        VerdantWindBlessing.applyBenedictionEffects(player, false);

        player.heal(VerdantWindBlessing.triggerHeal(wolf, player));
        ExtUtil.sendToChat(wolf, VWColors.VERDANT_WIND, true, wolfName(wolf) + " has granted you the §nVerdant Wind's Blessing§r");

        sendParticles(VWParticles.BENEDICTION_TRIGGER, level, player.blockPosition(), 6, 0.5);
        VerdantWindBlessing.verdantBlessingAfterEffects(level, wolf);
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

    public static String wolfName(Wolf wolf) {
        return "§d" + wolf.getPlainTextName() + "§r";
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

        int cycle = wolf.getAttachedOrElse(WolfAttachment.ATTACK_CYCLE, 0);
        if (cycle < 5) {
            wolf.setAttached(WolfAttachment.ATTACK_CYCLE, cycle + 1);
        } else {
            wolf.setAttached(WolfAttachment.ATTACK_CYCLE, 0);
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
