package cliffordha.totvw.entity.skills;

import cliffordha.totvw.datagen.VWDamageTypes;
import cliffordha.totvw.effect.HavocEffect;
import cliffordha.totvw.entity.wolf.VWWolfBehaviors;
import cliffordha.totvw.registry.*;
import cliffordha.totvw.registry.attachments.HavocType;
import cliffordha.totvw.registry.attachments.Runestone;
import cliffordha.totvw.registry.attachments.VWAttachments;
import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import cliffordha.totvw.util.VWUtil;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

import static cliffordha.totvw.util.VWUtil.*;
import static cliffordha.totvw.util.VWUtil.TimeUtil.sec;

public class RunestoneEffects {
    public static final AttachmentType<Runestone> RUNESTONE_TYPE = WolfAttachment.RUNESTONE_TYPE;
    public static final AttachmentType<List<String>> TETHERED_ENTITIES = WolfAttachment.TETHERED_ENTITY_TYPES;

    public static final AttachmentType<Integer> ATTACK_CYCLE = WolfAttachment.ATTACK_CYCLE;


    public static void processRunestones(Wolf wolf, ServerLevel level, Runestone type) {
        if (wolf.getOwner() != null && wolf.getOwner() instanceof Player player) {

            int cdHavoc = player.getAttachedOrElse(PlayerAttachment.CD_HAVOC, 0);
            if (type == Runestone.HAVOC && cdHavoc < 1) {
                if (!player.hasEffect(VWEffects.HAVOC)) {
                    addEffect(player, VWEffects.HAVOC, sec(60), 0);
                }
            }
        }
    }

    public static void processHavocStatus(Wolf wolf, LivingEntity victim, ServerLevel level) {
    }
    public static void getHavocForPlayer(Player player, LivingEntity attacker, float damage) {
        int CD = player.getAttachedOrElse(PlayerAttachment.CD_HAVOC, 0);
        if (CD > 0) return;

        if (!HavocType.isNone(player)) return;
        String type;

        boolean noTotem = !player.getItemBySlot(EquipmentSlot.OFFHAND).is(Items.TOTEM_OF_UNDYING)
                || !player.getItemBySlot(EquipmentSlot.MAINHAND).is(Items.TOTEM_OF_UNDYING);
        boolean isVoid = damage >= player.getHealth() * 0.6f && noTotem;

        if (isVoid) {
            setPlayerHavocStat(player, HavocType.VOID, 2, sec(12));
            type = HavocType.VOID.name().toUpperCase();

        } else {
            boolean isObliteration = getEntityArmors(attacker) > 2;
            if (isObliteration && attacker != null) {
                setPlayerHavocStat(player, HavocType.EXPULSION,2, sec(30));
                type = HavocType.EXPULSION.name().toUpperCase();
            } else {
                setPlayerHavocStat(player, HavocType.ANNIHILATION, 6, sec(60));
                type = HavocType.ANNIHILATION.name().toUpperCase();
            }
        }

        playSound(player, VWSounds.WOLF_SKILL_PARALYZE, SoundSource.PLAYERS, false);
        sendToChat(player, VWColors.HAVOC_PARTICLE, true, "Activated Havoc: " + type);
        sendParticles(HavocEffect.HAVOC_PARTICLE, (ServerLevel) player.level(), player.blockPosition(), 32, 3);
    }
    public static void triggerHavocPenalty(LivingEntity attacker, HavocType type) {
        if (attacker == null) return;

        attacker.removeAllEffects();
        if (attacker.getHealth() > 4) {
            attacker.setHealth(4);
        }
        ServerLevel level = (ServerLevel) attacker.level();
        int duration = type == HavocType.VOID ? TimeUtil.duration(5, 0) : TimeUtil.duration(2, 30);
        attacker.hurtServer(level, VWDamageTypes.havoc(level), 4f);
        addEffect(attacker, VWEffects.PARALYZE, sec(10), 0);
        addEffect(attacker, MobEffects.GLOWING, duration, 0);
        sendParticles(HavocEffect.HAVOC_PARTICLE, (ServerLevel) attacker.level(), attacker.blockPosition(), 24, 0.5);
    }
    private static void setPlayerHavocStat(Player player, HavocType type, int usageCount, int duration) {
        int extra = VWEnchantments.getBenediction(player) ? 1 : 0;
        int sec = extra > 0 ? sec(15) : 0;
        rewriteEffect(player, VWEffects.HAVOC, duration + sec, 0);
        player.setAttached(PlayerAttachment.HAVOC_TYPE, type);
        player.setAttached(PlayerAttachment.HAVOC_USAGE_COUNT, usageCount + extra);
    }
    public static void havocPlayerOnAttack(Player player, LivingEntity victim) {
        if (victim == null) return;
        if (victim instanceof Wolf) return;

        int usage = HavocType.getUsage(player);

        ServerLevel level = (ServerLevel) player.level();
        HavocType type = HavocType.getType(player);

        sendParticles(HavocEffect.HAVOC_PARTICLE, level, victim.blockPosition().above(), 32, 1);
        if (type == HavocType.ANNIHILATION) {
            addOrStackEffect(victim, MobEffects.WEAKNESS, sec(6), 1, true);
            addOrStackEffect(victim, MobEffects.HUNGER, sec(12), 0, true);
            replaceEffect(victim, MobEffects.RESISTANCE, sec(3));
            replaceEffect(victim, MobEffects.FIRE_RESISTANCE, sec(3));

        } else if (type == HavocType.EXPULSION) {
            if (victim instanceof Player p) {
                Inventory inventory = p.getInventory();
                for (int i = 0; i < inventory.getContainerSize(); i++) {
                    ItemStack stack = inventory.getItem(i);
                    boolean isItem = stack.is(Items.SHIELD)
                            || stack.is(ItemTags.CHEST_ARMOR)
                            || stack.is(ItemTags.HEAD_ARMOR)
                            || stack.is(ItemTags.LEG_ARMOR)
                            || stack.is(ItemTags.FOOT_ARMOR)
                            || stack.is(ItemTags.WEAPON_ENCHANTABLE)
                            || stack.is(Items.TOTEM_OF_UNDYING);
                    if (isItem) {
                        inventory.removeItem(stack);
                        playSound(victim, SoundEvents.ITEM_BREAK.value(), SoundSource.PLAYERS, false);
                        break;
                    }
                }
            } else {
                addEffect(victim, VWEffects.PARALYZE, sec(2), 0);
            }
            addOrStackEffect(player, MobEffects.SPEED, sec(6), 0, true);
            victim.knockback(1.3, victim.getXRot(), victim.getYHeadRot(), level.damageSources().explosion(null), 2);

        } else if (type == HavocType.VOID) {
            victim.teleportTo(victim.getX(), -200, victim.getZ());
            addOrStackEffect(player, MobEffects.SLOWNESS, sec(3), 0, true);
            addOrStackEffect(player, MobEffects.POISON, sec(15), 0, false);
        }

        player.setAttached(PlayerAttachment.HAVOC_USAGE_COUNT, usage - 1);
        int newUsage = HavocType.getUsage(player);

        if (newUsage < 1) {
            sendToChat(player, true, "Havoc has been removed");
            player.removeEffect(VWEffects.HAVOC);
        } else {
            sendToChat(player, true, type.name() + ": " + newUsage + " point(s) left");
        }
    }


    public static void reapplyLinkStatus(LivingEntity victim, DamageSource damageSource) {
        if (!(damageSource.getEntity() instanceof Wolf wolf)) return;
        if (!VWEnchantments.getBenediction(wolf)) return;

        if (!(wolf.level() instanceof ServerLevel level)) return;
        processLinkStatus(wolf, victim, level);
    }
    public static void processLinkStatus(Wolf wolf, LivingEntity victim, ServerLevel level) {
        List<String> entities = new ArrayList<>(wolf.getAttachedOrElse(TETHERED_ENTITIES, List.of()));

        if (victim != null) {
            updateLinkRecord(wolf, victim, entities, level);
        }

        for (String entity : entities) {
            int LINK_LIMIT = VWEnchantments.getBenediction(wolf) ? 16 : 9;
            int SCAN_SIZE = VWEnchantments.getEnhancementKit(wolf) ? 16 : 12;

            int MIGHT_ADDITIONAL = VWEnchantments.getMight(wolf);
            if (MIGHT_ADDITIONAL > 0) SCAN_SIZE += MIGHT_ADDITIONAL * 2;

            List<Entity> scanner = level.getEntities(
                    wolf,
                    VWUtil.scanArea(wolf, Math.min(SCAN_SIZE, 24)),
                    test -> isEqualEntityID(entity, test)
            ).stream().limit(LINK_LIMIT).toList();

            if (scanner.isEmpty()) return;

            for (Entity mob : scanner) {
                LivingEntity target = (LivingEntity) mob;

                applyLink(wolf, target, level);
            }
        }
    }
    private static void applyLink(Wolf wolf, LivingEntity target, ServerLevel level) {
        if (WolfAttachment.isTetherBlacklisted(wolf, target)) return;

        if (target instanceof Player player) {
            boolean isNotAggressor = !WolfAttachment.isListedAggressor(wolf, VWAttachments.getWolfPlayerSharedId(target));

            if (isNotAggressor) {
                return;
            } else if (wolf.getOwner() != null) {
                if (wolf.getOwner().equals(player)) {
                    return;
                }
            }
        }

        DamageSource source = VWDamageTypes.tetherProxy(level);
        if (wolf.isWearingBodyArmor()) {
            VWWolfBehaviors.runEnchantmentsOnDamage(wolf, level, target, false, source);
        } else {
            VWWolfBehaviors.applyConsolidatedDamage(level, wolf, target,
                    source,
                    0,
                    false
            );
        }
    }
    private static void updateLinkRecord(Wolf wolf, LivingEntity victim, List<String> entities, ServerLevel level) {
        if (victim instanceof Wolf) return;
        if (WolfAttachment.isTetherBlacklisted(wolf, victim)) return;

        List<String> newEntities = new ArrayList<>(entities);

        for (String type : entities) {
            if (WolfAttachment.isTetherBlacklisted(wolf, victim) && entities.contains(getEntityID(victim))) {
                newEntities.remove(type);
            } else {

                int cycle = wolf.getAttachedOrElse(ATTACK_CYCLE, 0) - 1;
                List<Entity> scanner = level.getEntities(wolf,
                        scanArea(wolf, 12),
                        test -> isEqualEntityID(type, test)
                ).stream().limit(1).toList();

                if (scanner.isEmpty() && cycle % 2 == 0) {
                    newEntities.remove(type);
                }
            }
        }
        wolf.setAttached(TETHERED_ENTITIES, newEntities);

        String targetType = getEntityID(victim);
        if (newEntities.contains(targetType)) return;

        if (newEntities.size() >= 2) {
            if (level.getRandom().nextBoolean()) {
                newEntities.removeFirst();
            } else {
                newEntities.removeLast();
            }
        }
        newEntities.add(targetType);
        wolf.setAttached(TETHERED_ENTITIES, newEntities);
    }


    // helpers
    private static int getEntityArmors(LivingEntity victim) {
        int count = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = victim.getItemBySlot(slot);
            if (stack.is(ItemTags.ARMOR_ENCHANTABLE) || stack.is(Items.SHIELD) || stack.is(Items.TOTEM_OF_UNDYING)) {
                count++;
            }
        }
        return count;
    }
}
