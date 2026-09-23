package cliffordha.totvw.entity.player;

import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.entity.skill.PlayerSkillDefinition;
import cliffordha.totvw.entity.skill.SkillManager;
import cliffordha.totvw.entity.skills.RunestoneEffects;
import cliffordha.totvw.entity.skills.VerdantWindBlessing;
import cliffordha.totvw.registry.*;
import cliffordha.totvw.registry.attachments.HavocType;
import cliffordha.totvw.registry.attachments.PlayerPrefs;
import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
import cliffordha.totvw.tag.VWBiomeTags;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.ArrayList;
import java.util.List;

import static cliffordha.totvw.entity.skill.SkillManager.*;
import static cliffordha.totvw.util.VWUtil.*;
import static cliffordha.totvw.util.VWUtil.TimeUtil.*;

public class VWPlayerBehaviors {
    private static final List<PlayerBehaviorRule> ON_DAMAGE_RULES = new ArrayList<>();
    private static final List<PlayerBehaviorRule> TICK_RULES = new ArrayList<>();

    public static void registerModPlayerBehaviors() {
        registerRules();
        playerOnDamageEvent();
        playerOnTickEvent();
        VWPlayerInteractions.onInteractEvents();
    }

    private static void registerRules() {
        ON_DAMAGE_RULES.add(PlayerBehaviorRule.register(
                PlayerCondition.alwaysTrue(),
                (player, level) -> {
                    if (player.getArmorValue() > 0) {
                        runEnchantmentsOnDamage(player, level);
                    }
                    boolean hasHavoc = player.hasEffect(VWEffects.HAVOC)
                    && !HavocType.isNone(player);

                    if (hasHavoc) {
                        RunestoneEffects.havocPlayerOnAttack(player, CURRENT_VICTIM.get());
                    }
                }
        ));
        TICK_RULES.add(PlayerBehaviorRule.register(
                PlayerCondition.tick(1, 0),
                ((player, level) -> {
                    RandomSource random = level.getRandom();
                    player.setAttached(PlayerAttachment.RANDOM_INT_10, random.nextIntBetweenInclusive(1, 10));

                    if (VWEnchantments.getBenediction(player) && isInBiome(player, VWBiomeTags.IS_VERDANT_BIOMES)) {
                        player.heal(1.0f);
                    }
                })
        ));
        TICK_RULES.add(PlayerBehaviorRule.register(
                PlayerCondition.tick(6, 0)
                        .and(PlayerCondition.hasArmorWithEnchantment(EquipmentSlot.CHEST, VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS)),
                ((player, _) -> player.heal(1.0f))
        ));
        TICK_RULES.add(PlayerBehaviorRule.register(
                PlayerCondition.tick(0, 30),
                (player, _) -> {
                    if (VWConfig.get().SERVER_OTHER_COOLDOWNS) {
                        depleteCooldown(player, PlayerAttachment.VILLAGER_ATROCITY_COUNT);
                        depleteCooldown(player, PlayerAttachment.WOLF_ATROCITY_COUNT);
                    }
                }
        ));
        TICK_RULES.add(PlayerBehaviorRule.register(
                PlayerCondition.tick(0, 3),
                (player, _) -> {
                    float HEALTH_THRESHOLD = player.getAttachedOrElse(PlayerPrefs.BENEDICTION_HEALTH_THRESHOLD,
                            VWConfig.get().SERVER_BENEDICTION_HEALTH_THRESHOLD) * 0.01f;
                    if (player.getHealth() <= player.getMaxHealth() * HEALTH_THRESHOLD) {
                        VerdantWindBlessing.triggerBenedictionFromTick(player);
                    }
                }
        ));
        TICK_RULES.add(PlayerBehaviorRule.register(
                PlayerCondition.tick(),
                (player, _) -> {
                    if (VWConfig.get().LOG_ENCHANTMENT_SHOW_PLAYER_CD) setPlayerConfiguration(player, 0);
                    if (VWConfig.get().SERVER_SKILL_COOLDOWNS) {
                        depleteCooldown(player, PlayerAttachment.CD_BLESSING_OF_THE_VERDANT_WIND);
                        depleteCooldown(player, PlayerAttachment.CD_HAVOC);
                    } else {
                        setPlayerConfiguration(player, 1);
                    }

                    if (!VWConfig.get().SERVER_OTHER_COOLDOWNS) setPlayerOtherConfig(player);

                    SkillManager.notifyReset(player, VERDANT_BLESSING);
                    processCDNotify(player,
                            PlayerAttachment.CD_BLESSING_OF_THE_VERDANT_WIND,
                            PlayerAttachment.NOTIFY_BLESSING_OF_THE_VERDANT_WIND,
                            VWColors.VERDANT_WIND,
                            "§nVerdant Wind's Blessing§f cooldown reset for §r"
                    );
                }
        ));
    }


    public static void runWolfBlessing(LivingEntity player, Wolf wolf, ServerLevel level) {
        float triggerHeal;
        if (player instanceof Player aPlayer && (!aPlayer.isCreative() || !aPlayer.isSpectator())) {
            triggerHeal = triggerHeal(player, wolf);
        } else {
            triggerHeal = 999.0f;
        }
        wolf.heal(triggerHeal);
        VerdantWindBlessing.applyBenedictionEffects(wolf,  false);
        sendToChat(player, VWColors.VERDANT_WIND, true, "You have granted §nVerdant Wind's Blessing§r to " + wolfName(wolf));

        sendParticles(VWParticles.BENEDICTION_TRIGGER_PARTICLE, level, wolf.blockPosition(), 6, 0.5);
        verdantBlessingAfterEffects(level, player);
    }
    private static void runEnchantmentsOnDamage(Player player, ServerLevel level) {
        var victim = CURRENT_VICTIM.get();
        if (victim == null) return;

        boolean BENEDICTION_ACTIVE = VWEnchantments.getBenediction(player);
        int FIRE_PROTECTION = VWEnchantments.entityEnchantmentLVL(player, Enchantments.FIRE_PROTECTION);

        boolean inVerdantBiomes = isInBiome(player, VWBiomeTags.IS_VERDANT_BIOMES);
        boolean inNether = isInBiome(player, BiomeTags.IS_NETHER);

        if (BENEDICTION_ACTIVE && inVerdantBiomes) {
            victim.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, sec(3), 0));
        }

        if (FIRE_PROTECTION > 2 && inNether && !victim.fireImmune()) {
            victim.hurtServer(level, level.damageSources().onFire(), 1.0f + FIRE_PROTECTION);
        }
    }


    public static final PlayerSkillDefinition VERDANT_BLESSING = new PlayerSkillDefinition(
            PlayerAttachment.CD_BLESSING_OF_THE_VERDANT_WIND,
            PlayerAttachment.NOTIFY_BLESSING_OF_THE_VERDANT_WIND,
            VWColors.VERDANT_WIND_MUTED,
            "§nVerdant Wind's Blessing§r"
    );

    private static String wolfName(Wolf wolf) {
        String wolfName;
        if (wolf.getName().getString().equals("§dWolf§r")) {
            wolfName = "Wolf";
        } else {wolfName = "§d" + wolf.getName().getString() + "§r";}
        return wolfName;
    }

    private static final ThreadLocal<LivingEntity> CURRENT_VICTIM = new ThreadLocal<>();
    private static void playerOnDamageEvent() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register(
                (victim, damageSource, _, _, _) ->
                        getPlayerVictimThread(victim, damageSource)
        );
        ServerLivingEntityEvents.AFTER_DEATH.register(
                VWPlayerBehaviors::getPlayerVictimThread
        );
    }
    private static void getPlayerVictimThread(LivingEntity victim, DamageSource damageSource) {
        Entity directEntity = damageSource.getEntity();
        if (!(directEntity instanceof Player player)) return;
        if (!(player.level() instanceof ServerLevel serverLevel)) return;

        CURRENT_VICTIM.set(victim);
        try {
            for (PlayerBehaviorRule rule : ON_DAMAGE_RULES) {
                rule.evaluate(player, serverLevel);
            }
        } finally {
            CURRENT_VICTIM.remove();
        }
    }
    private static void playerOnTickEvent() {
        if (TICK_RULES.isEmpty()) return;

        ServerTickEvents.END_SERVER_TICK.register((MinecraftServer server) -> {
            for (var serverLevel : server.getAllLevels()) {
                serverLevel.getEntities(
                        EntityTypes.PLAYER,
                        _ -> true
                ).forEach(player -> {
                    for (PlayerBehaviorRule rule : TICK_RULES) {
                        rule.evaluate(player, serverLevel);
                    }
                });
            }
        });
    }
}
