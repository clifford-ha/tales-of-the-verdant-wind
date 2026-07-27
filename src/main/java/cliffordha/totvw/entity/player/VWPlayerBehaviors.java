package cliffordha.totvw.entity.player;

import cliffordha.totvw.config.TOTVWConfig;
import cliffordha.totvw.entity.skill.VWSkillProcessor;
import cliffordha.totvw.entity.skill.PlayerSkillDefinition;
import cliffordha.totvw.entity.skill.SkillUtil;
import cliffordha.totvw.registry.*;
import cliffordha.totvw.item.events.VWItemBlessings;
import cliffordha.totvw.item.VWItemBlessingsInstance;
import cliffordha.totvw.tag.VWBiomeTags;
import cliffordha.totvw.tag.VWItemTags;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static cliffordha.totvw.util.VWGlobalUtil.*;
import static cliffordha.totvw.entity.skill.VWSkillProcessor.*;

public class VWPlayerBehaviors {
    private static final List<PlayerBehaviorRule> ON_DAMAGE_RULES = new ArrayList<>();
    private static final List<PlayerBehaviorRule> TICK_RULES = new ArrayList<>();

    public static void registerModPlayerBehaviors() {
        registerRules();
        wireOnDamageEvent();
        wireTickEvent();
        wireItemUseEvent();
    }

    private static void registerRules() {
        ON_DAMAGE_RULES.add(PlayerBehaviorRule.register(
                PlayerCondition.hasBodyArmor(),
                VWPlayerBehaviors::runEnchantmentsOnDamage
        ));
        TICK_RULES.add(PlayerBehaviorRule.register(
                PlayerCondition.tick(1, 0)
                        .and(PlayerCondition.hasArmorWithEnchantment(EquipmentSlot.CHEST, VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS))
                        .and(PlayerCondition.checkBiomeTag(VWBiomeTags.IS_VERDANT_BIOMES)),
                ((player, _) -> player.heal(1.0f))
        ));
        TICK_RULES.add(PlayerBehaviorRule.register(
                PlayerCondition.tick(6, 0)
                        .and(PlayerCondition.hasArmorWithEnchantment(EquipmentSlot.CHEST, VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS)),
                ((player, _) -> player.heal(1.0f))
        ));
        TICK_RULES.add(PlayerBehaviorRule.register(
                        PlayerCondition.halfTick()
                        .and(PlayerCondition.checkNoAttached(VWAttachments.Player.PLAYER_CD_BLESSING_OF_THE_VERDANT_WIND)),
                (player, level) -> {
                            if (playerEnchantmentLVL(player, VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS) < 1) return;
                            runWolfBlessing(player, level);
                }
        ));
        TICK_RULES.add(PlayerBehaviorRule.register(
                PlayerCondition.tick(),
                (player, _) -> {
                    if (TOTVWConfig.get().DEBUG_PRINT_LOGS) { VWSkillProcessor.setPlayerConfiguration(player, 0); }
                    if (TOTVWConfig.get().ATTACHMENT_SKILL_CD) {
                        VWSkillProcessor.depleteCooldown(player, VWAttachments.Player.PLAYER_CD_BLESSING_OF_THE_VERDANT_WIND);
                        VWSkillProcessor.depleteCooldown(player, VWAttachments.ENTITY_TRUST_COOLDOWN);
                    } else {
                        VWSkillProcessor.setPlayerConfiguration(player, 1);
                    }

                    SkillUtil.notifyReset(player, VERDANT_BLESSING);
                    processCDNotify(player,
                            VWAttachments.Player.PLAYER_CD_BLESSING_OF_THE_VERDANT_WIND,
                            VWAttachments.Player.PLAYER_NOTIFY_BLESSING_OF_THE_VERDANT_WIND,
                            VWColors.VERDANT_WIND,
                            "§nVerdant Wind's Blessing§f cooldown reset for §r"
                    );
                }
        ));
    }
    private static void runWolfBlessing(Player player, ServerLevel level) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (playerEnchantmentLVL(player, VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS) <= 0) return;

        double distance = TOTVWConfig.get().MAX_WOLF_PLAYER_SCAN_DISTANCE;
        float health = TOTVWConfig.get().LOW_HEALTH_THRESHOLD * 0.01f;

        List<Wolf> wolves = serverLevel.getEntities(
                EntityType.WOLF,
                player.getBoundingBox().inflate(distance * 16),
                wolf -> wolf.isTame()
                        && wolf.getOwner() != null
                        && wolf.getHealth() <= wolf.getMaxHealth() * health
                        && wolf.getOwner().getUUID().equals(player.getUUID()) );
        if (wolves.isEmpty()) return;

        for (Wolf wolf : wolves) {
            if (!wolf.isAlive()) return;
            int wolfBenediction = wolf.getAttachedOrElse(VWAttachments.Wolf.WOLF_BENEDICTION, 0);
            if (wolfBenediction > 0) return;

            float triggerHeal;
            if (!player.isCreative() || !player.isSpectator()) {
                triggerHeal = triggerHeal(player, wolf);
            } else {
                triggerHeal = 999.0f;
            }

            rewriteEffect(wolf, MobEffects.RESISTANCE, sec(6), 254);
            rewriteEffect(wolf, VWEffects.BLESSING_OF_THE_VERDANT_WIND, sec(30), 2);
            if (wolfEnchantmentLVL(wolf, VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS) > 0) {
                removeEffect(wolf, MobEffects.POISON);
                removeEffect(wolf, MobEffects.WITHER);
            }
            wolf.heal(triggerHeal);
            sendToChat(player, VWColors.VERDANT_WIND, true, "Granted §nVerdant Wind's Blessing§r to " + wolfName(wolf));

            VWParticleEffects.triggerBenedictionParticles(player, 1);
            verdantBlessingAfterEffects(level, player);
        }
    }

    private static void runEnchantmentsOnDamage(Player player, ServerLevel level) {
        var victim = CURRENT_VICTIM.get();
        if (victim == null) return;

        int BENEDICTION_ACTIVE = playerEnchantmentLVL(player, VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS);
        int FIRE_PROTECTION = playerEnchantmentLVL(player, Enchantments.FIRE_PROTECTION);

        boolean inVerdantBiomes = player.level().getBiome(player.blockPosition()).is(VWBiomeTags.IS_VERDANT_BIOMES);
        boolean inNether = player.level().getBiome(player.blockPosition()).is(BiomeTags.IS_NETHER);

        if (BENEDICTION_ACTIVE > 0 && inVerdantBiomes) {
            victim.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, sec(3), 0));
        }

        if (FIRE_PROTECTION > 2 && inNether && !victim.fireImmune()) {
            victim.hurtServer(level, level.damageSources().onFire(), 1.0f + FIRE_PROTECTION);
        }
    }


    public static final PlayerSkillDefinition VERDANT_BLESSING = new PlayerSkillDefinition(
            VWAttachments.Player.PLAYER_CD_BLESSING_OF_THE_VERDANT_WIND,
            VWAttachments.Player.PLAYER_NOTIFY_BLESSING_OF_THE_VERDANT_WIND,
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

    private static void wireItemUseEvent() {
        UseItemCallback.EVENT.register((player, level, _) -> {
            ItemStack mainHand = player.getItemBySlot(EquipmentSlot.MAINHAND);
            if (level.isClientSide()) return InteractionResult.PASS;
            if (!player.isShiftKeyDown()) return InteractionResult.PASS;
            if (player.isSpectator()) return InteractionResult.PASS;
            if (playerEnchantmentLVL(player, VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS) <= 0)
                return InteractionResult.PASS;

            boolean isItem = (mainHand.tags().anyMatch(Predicate.isEqual(VWItemTags.BENEDICTION_ENCHANTMENT_USE_QUALIFIED_TOOLS))
                    || mainHand.tags().anyMatch(Predicate.isEqual(VWItemTags.BENEDICTION_ENCHANTMENT_USE_QUALIFIED_ITEMS)))
                    && !(mainHand.getItem() instanceof VWItemBlessingsInstance);

            if (!isItem) return InteractionResult.PASS;

            if (player.getCooldowns().isOnCooldown(mainHand)) return InteractionResult.PASS;

            boolean applied = VWItemBlessings.tryApply(level, player);
            if (applied) {
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.PASS;
            }
        });
    }




    private static final ThreadLocal<LivingEntity> CURRENT_VICTIM = new ThreadLocal<>();
    private static void wireOnDamageEvent() {
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
    private static void wireTickEvent() {
        if (TICK_RULES.isEmpty()) return;

        ServerTickEvents.END_SERVER_TICK.register((MinecraftServer server) -> {
            for (var serverLevel : server.getAllLevels()) {
                serverLevel.getEntities(
                        EntityType.PLAYER,
                        _ -> true
                ).forEach(player -> {
                    for (PlayerBehaviorRule rule : TICK_RULES) {
                        rule.evaluate(player, serverLevel);
                    }
                });
            }
        });

        // Debugging

        ServerTickEvents.START_SERVER_TICK.register((MinecraftServer server) -> {
            for (var serverLevel : server.getAllLevels()) {
                serverLevel.getEntities(
                        EntityType.PLAYER,
                        _ -> true
                ).forEach(player -> {
                    if (!player.getAttachedOrElse(VWAttachments.Player.PLAYER_IS_DEV_MODE, false)) {
                        player.setAttached(VWAttachments.Player.PLAYER_IS_DEV_MODE, true);
                    }
                    List<Wolf> wolves = serverLevel.getEntities(
                            EntityType.WOLF,
                            player.getBoundingBox().inflate(32),
                            wolf -> wolf.isTame()
                                    && !wolf.getAttachedOrElse(VWAttachments.Wolf.WOLF_IS_VILLAGE_GUARD, false)
                                    && (wolf.getUUID() != player.getUUID()) );
                    if (wolves.isEmpty()) return;
                    for (Wolf wolf : wolves) {
                        wolf.setOwner(player);
                    }
                });
            }
        });
    }
    private VWPlayerBehaviors() {}
}
