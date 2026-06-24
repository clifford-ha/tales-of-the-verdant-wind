package cliffordha.totvw.entity.player;

import cliffordha.totvw.config.TOTVWConfig;
import cliffordha.totvw.entity.TConstants;
import cliffordha.totvw.entity.skill.ConfigTools;
import cliffordha.totvw.entity.skill.PlayerSkillDefinition;
import cliffordha.totvw.entity.skill.SkillUtil;
import cliffordha.totvw.registry.ModEffects;
import cliffordha.totvw.registry.ModEnchantments;
import cliffordha.totvw.item.events.BlessingOfTheVerdantWind;
import cliffordha.totvw.item.ItemInstance;
import cliffordha.totvw.registry.ModAttachments;
import cliffordha.totvw.registry.ModParticleEffects;
import cliffordha.totvw.tag.ModBiomeTags;
import cliffordha.totvw.tag.ModItemTags;
import cliffordha.totvw.registry.ModColors;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static cliffordha.totvw.entity.TConstants.*;
import static cliffordha.totvw.entity.skill.ConfigTools.*;

public class ModPlayerBehaviors {
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
                ModPlayerBehaviors::runEnchantmentsOnDamage
        ));
        TICK_RULES.add(PlayerBehaviorRule.register(
                PlayerCondition.tick(1, 0)
                        .and(PlayerCondition.hasArmorWithEnchantment(EquipmentSlot.CHEST, ModEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS))
                        .and(PlayerCondition.checkBiomeTag(ModBiomeTags.IS_VERDANT_BIOMES)),
                ((player, _) -> player.heal(1.0f))
        ));
        TICK_RULES.add(PlayerBehaviorRule.register(
                PlayerCondition.tick(6, 0)
                        .and(PlayerCondition.hasArmorWithEnchantment(EquipmentSlot.CHEST, ModEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS)),
                ((player, _) -> player.heal(1.0f))
        ));
        TICK_RULES.add(PlayerBehaviorRule.register(
                        PlayerCondition.hasBodyArmor()
                        .and(PlayerCondition.checkNoAttached(ModAttachments.Player.CD_BLESSING_OF_THE_VERDANT_WIND)),
                ModPlayerBehaviors::runWolfBlessing
        ));
        TICK_RULES.add(PlayerBehaviorRule.register(
                PlayerCondition.tick(0, 1),
                (player, _) -> {
                    if (TOTVWConfig.get().sendLog) { ConfigTools.setPlayerConfiguration(player, 0); }
                    if (TOTVWConfig.get().attachmentSkillCD) {
                        ConfigTools.depleteCooldown(player, ModAttachments.Player.CD_BLESSING_OF_THE_VERDANT_WIND);
                    } else {
                        ConfigTools.setPlayerConfiguration(player, 1);
                    }

                    SkillUtil.notifyReset(player, VERDANT_BLESSING);
                    processCDNotify(player,
                            ModAttachments.Player.CD_BLESSING_OF_THE_VERDANT_WIND,
                            ModAttachments.Player.NOTIFY_BLESSING_OF_THE_VERDANT_WIND,
                            ModColors.VERDANT_WIND,
                            "§nVerdant Wind's Blessing§f cooldown reset for §r"
                    );
                }
        ));
    }
    private static void runWolfBlessing(Player player, ServerLevel level) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (playerEnchantmentLVL(player, ModEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS) <= 0) return;

        int blessingCD = verdantBlessingCD(level);

        double distance = TOTVWConfig.get().maxWolfPlayerDistance;
        float health = (float) (TOTVWConfig.get().lowHealthThreshold * 0.01);

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
            int wolfBenedictionEnchantment = TConstants.wolfEnchantmentLVL(wolf, ModEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS);
            int wolfBenediction = wolf.getAttachedOrElse(ModAttachments.Wolf.WOLF_BENEDICTION, 0);
            if (wolfBenediction > 0) return;

            float triggerHeal;
            if (!player.isCreative() || !player.isSpectator()) {
                triggerHeal = triggerHeal(player, wolf);
            } else {
                triggerHeal = 999.0f;
            }

            addEffect(wolf, ModEffects.BLESSING_OF_THE_VERDANT_WIND, sec(30), 2);
            if (wolfBenedictionEnchantment > 0) {
                removeEffect(wolf, MobEffects.POISON);
                removeEffect(wolf, MobEffects.WITHER);
            }
            rewriteEffect(wolf, MobEffects.RESISTANCE, sec(10), 254);

            notifyFromPlayer(player, ModColors.VERDANT_WIND, true, "Granted §nVerdant Wind's Blessing§r to " + wolfName(wolf));
            wolf.heal(triggerHeal);
        }
        ModParticleEffects.triggerBenedictionParticles(player, 1);

        if (!TOTVWConfig.get().attachmentSkillCD) return;
        if (isHealthHalf(player)) {
            addHiddenEffect(player, MobEffects.WEAKNESS, min(1), 0);
        } else {
            addHiddenEffect(player, MobEffects.WEAKNESS, min(1), 1);
        }
        SkillUtil.startCooldown(player, VERDANT_BLESSING, blessingCD);
        notifyFromPlayer(player, ModColors.VERDANT_WIND_MUTED, "Cooldown: " + blessingCD / min(1) + " minutes");
    }

    private static void runEnchantmentsOnDamage(Player player, ServerLevel level) {
        var victim = CURRENT_VICTIM.get();
        if (victim == null) return;

        int BENEDICTION_ACTIVE = playerEnchantmentLVL(player, ModEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS);
        int FIRE_PROTECTION = playerEnchantmentLVL(player, Enchantments.FIRE_PROTECTION);

        boolean inVerdantBiomes = player.level().getBiome(player.blockPosition()).is(ModBiomeTags.IS_VERDANT_BIOMES);
        boolean inNether = player.level().getBiome(player.blockPosition()).is(BiomeTags.IS_NETHER);

        if (BENEDICTION_ACTIVE > 0 && inVerdantBiomes) {
            victim.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, sec(3), 0));
        }

        if (FIRE_PROTECTION > 0 && inNether) {
            victim.hurtServer(level, level.damageSources().thorns(player), 1.0f + FIRE_PROTECTION);
        }
    }

    private static final PlayerSkillDefinition VERDANT_BLESSING = new PlayerSkillDefinition(
            ModAttachments.Player.CD_BLESSING_OF_THE_VERDANT_WIND,
            ModAttachments.Player.NOTIFY_BLESSING_OF_THE_VERDANT_WIND,
            ModColors.VERDANT_WIND_MUTED,
            "§nVerdant Wind's Blessing§r"
    );

    private static String wolfName(Wolf wolf) {
        String wolfName;
        if (wolf.getName().getString().equals("§dWolf§r")) {
            wolfName = "Wolf";
        } else {wolfName = "§d" + wolf.getName().getString() + "§r";}
        return wolfName;
    }

    private static int playerEnchantmentLVL(Player player, ResourceKey<Enchantment> enchantment) {
        if (player == null) return 0;
        return player.getItemBySlot(EquipmentSlot.CHEST).getEnchantments()
                .getLevel(player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                        .getOrThrow(enchantment));
    }

    private static void wireItemUseEvent() {
        UseItemCallback.EVENT.register((player, level, _) -> {
            if (level.isClientSide()) return InteractionResult.PASS;
            if (!player.isShiftKeyDown()) return InteractionResult.PASS;
            if (player.isSpectator()) return InteractionResult.PASS;
            if (playerEnchantmentLVL(player, ModEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS) <= 0) return InteractionResult.PASS;

            ItemStack mainHand = player.getItemBySlot(EquipmentSlot.MAINHAND);

            boolean isItem = (mainHand.tags().anyMatch(Predicate.isEqual(ModItemTags.BENEDICTION_ENCHANTMENT_USE_QUALIFIED_TOOLS))
                    || mainHand.tags().anyMatch(Predicate.isEqual(ModItemTags.BENEDICTION_ENCHANTMENT_USE_QUALIFIED_ITEMS)))
                    && !(mainHand.getItem() instanceof ItemInstance);

            if (!isItem) return InteractionResult.PASS;

            if (player.getCooldowns().isOnCooldown(mainHand)) return InteractionResult.PASS;

            boolean applied = BlessingOfTheVerdantWind.tryApply(level, player);
            if (applied) { return InteractionResult.SUCCESS;
            } else {return InteractionResult.PASS;}
        });
    }




    private static final ThreadLocal<LivingEntity> CURRENT_VICTIM = new ThreadLocal<>();
    private static void wireOnDamageEvent() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register(
                (victim, damageSource, _, _, _) ->
                        getPlayerVictimThread(victim, damageSource)
        );
        ServerLivingEntityEvents.AFTER_DEATH.register(
                ModPlayerBehaviors::getPlayerVictimThread
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
                    List<Wolf> wolves = serverLevel.getEntities(
                            EntityType.WOLF,
                            player.getBoundingBox().inflate(32),
                            wolf -> wolf.isTame() && (wolf.getUUID() != player.getUUID()) );
                    if (wolves.isEmpty()) return;
                    for (Wolf wolf : wolves) {
                        wolf.setOwner(player);
                    }
                });
            }
        });
    }
    private ModPlayerBehaviors() {}
}
