package cliffordha.totvw.entity.player;

import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.entity.TickCondition;
import cliffordha.totvw.entity.skill.PlayerSkills;
import cliffordha.totvw.entity.skills.RunestoneEffects;
import cliffordha.totvw.entity.skills.VerdantWindBlessing;
import cliffordha.totvw.networking.VWNetworking;
import cliffordha.totvw.registry.*;
import cliffordha.totvw.registry.attachments.HavocType;
import cliffordha.totvw.registry.attachments.PlayerPrefs;
import cliffordha.totvw.registry.attachments.VWAttachment;
import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import cliffordha.totvw.tag.VWBiomeTags;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.ArrayList;
import java.util.List;

import static cliffordha.totvw.entity.skill.SkillManager.*;
import static cliffordha.totvw.util.ExtUtil.*;

public class VWPlayerBehaviors {
    private static final List<PlayerBehaviorRule> ON_DAMAGE_RULES = new ArrayList<>();
    private static final List<PlayerBehaviorRule> TICK_RULES = new ArrayList<>();

    public static void registerModPlayerBehaviors() {
        registerRules();
        playerOnDamageEvent();
        playerOnTickEvent();
    }

    private static void registerRules() {
        ON_DAMAGE_RULES.add(PlayerBehaviorRule.register(TickCondition.alwaysTrue(), (player, level) -> {
            if (player.getArmorValue() > 0) {
                runEnchantmentsOnDamage(player, level);
            }

            boolean hasHavoc = player.hasEffect(VWEffects.HAVOC) && !HavocType.isNone(player);
            if (hasHavoc) {
                RunestoneEffects.havocPlayerOnAttack(player, CURRENT_VICTIM.get());
            }
        }));
        TICK_RULES.add(PlayerBehaviorRule.register(TickCondition.tick(1, 0), ((player, level) -> {
            RandomSource random = level.getRandom();
            player.setAttached(PlayerAttachment.RANDOM_INT_10, random.nextIntBetweenInclusive(1, 10));

            if (VWEnchantments.getBenediction(player) && isInBiome(player, VWBiomeTags.IS_VERDANT_BIOMES)) {
                player.heal(1.0f);
            }
        })));
        TICK_RULES.add(PlayerBehaviorRule.register(TickCondition.tick(6, 0), ((player, _) -> {
            if (VWEnchantments.getBenediction(player)) player.heal(1.0f);
        })));
        TICK_RULES.add(PlayerBehaviorRule.register(TickCondition.tick(0, 30), (player, _) -> {
            if (VWConfig.get().SERVER_OTHER_COOLDOWNS) {
                depleteCooldown(player, PlayerAttachment.VILLAGER_ATROCITY_COUNT);
                depleteCooldown(player, PlayerAttachment.WOLF_ATROCITY_COUNT);
            }
        }));
        TICK_RULES.add(PlayerBehaviorRule.register(TickCondition.tick(0, 3), (player, level) -> {
            if (!PlayerAttachment.getTrustedPlayers(player).isEmpty()) {
                List<Wolf> wolves = level.getEntitiesOfClass(Wolf.class, scanArea(player, 16), wolf -> wolf.getOwner() == player);
                for (Wolf wolf : wolves) {
                    WolfAttachment.syncTrustedPlayers(wolf);
                }
            }

            AttachmentType<Integer> BLOOM_STACKS = VWAttachment.VERDANT_BLOOM_STACK;
            if (player.hasAttached(BLOOM_STACKS)) {
                int remaining = player.getAttachedOrElse(BLOOM_STACKS, 0);
                boolean shouldRemoveBloomStack = remaining > 0 && !player.getInventory().contains(new ItemStack(Items.BONE_MEAL));
                if (shouldRemoveBloomStack) {

                    player.setAttached(BLOOM_STACKS, remaining - 2);
                    if (player.getAttachedOrElse(BLOOM_STACKS, 0) <= 0) {
                        player.removeAttached(BLOOM_STACKS);
                    }
                }
            }

            float HEALTH_THRESHOLD = player.getAttachedOrElse(PlayerPrefs.BENEDICTION_HEALTH_THRESHOLD, VWConfig.get().SYNCED_BENEDICTION_HEALTH_THRESHOLD) * 0.01f;
            if (player.getHealth() <= player.getMaxHealth() * HEALTH_THRESHOLD) {
                VerdantWindBlessing.triggerBenedictionFromTick(player);
            }
        }));
        TICK_RULES.add(PlayerBehaviorRule.register(TickCondition.tick(), (player, _) -> {
            VWNetworking.syncPlayerArmor(player);

            if (VWConfig.get().SERVER_SKILL_COOLDOWNS) {
                depleteCooldown(player, PlayerAttachment.CD_BLESSING_OF_THE_VERDANT_WIND);
                depleteCooldown(player, PlayerAttachment.CD_HAVOC);
            } else {
                setPlayerConfiguration(player);
            }

            if (!VWConfig.get().SERVER_OTHER_COOLDOWNS) setPlayerOtherConfiguration(player);
            processCDNotify(player, PlayerSkills.VERDANT_BLESSING);
        }));
    }


    public static void runWolfBlessing(LivingEntity player, Wolf wolf, ServerLevel level) {
        float triggerHeal;
        if (player instanceof Player aPlayer && (!aPlayer.isCreative() || !aPlayer.isSpectator())) {
            triggerHeal = VerdantWindBlessing.triggerHeal(player, wolf);
        } else {
            triggerHeal = 999.0f;
        }
        wolf.heal(triggerHeal);
        VerdantWindBlessing.applyBenedictionEffects(wolf,  false);
        sendToChat(player, VWColors.VERDANT_WIND, true, "You have granted §nVerdant Wind's Blessing§r to " + wolfName(wolf));

        sendParticles(VWParticles.BENEDICTION_TRIGGER, level, wolf.blockPosition(), 6, 0.5);
        VerdantWindBlessing.verdantBlessingAfterEffects(level, player);
    }
    private static void runEnchantmentsOnDamage(Player player, ServerLevel level) {
        var victim = CURRENT_VICTIM.get();
        if (victim == null) return;

        boolean BENEDICTION_ACTIVE = VWEnchantments.getBenediction(player);
        int FIRE_PROTECTION = VWEnchantments.entityEnchantmentLVL(player, Enchantments.FIRE_PROTECTION);

        boolean inVerdantBiomes = isInBiome(player, VWBiomeTags.IS_VERDANT_BIOMES);
        boolean inNether = isInBiome(player, BiomeTags.IS_NETHER);

        if (BENEDICTION_ACTIVE && inVerdantBiomes) {
            victim.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 3, 0));
        }

        if (FIRE_PROTECTION > 2 && inNether && !victim.fireImmune()) {
            victim.hurtServer(level, level.damageSources().onFire(), 1.0f + FIRE_PROTECTION);
        }
    }

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
                serverLevel.getEntities(EntityTypes.PLAYER, _ -> true).forEach(player -> {
                    for (PlayerBehaviorRule rule : TICK_RULES) {
                        rule.evaluate(player, serverLevel);
                    }
                });
            }
        });
    }
}
