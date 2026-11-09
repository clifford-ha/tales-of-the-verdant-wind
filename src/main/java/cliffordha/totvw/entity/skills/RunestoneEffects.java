package cliffordha.totvw.entity.skills;

import cliffordha.totvw.datagen.VWDamageTypes;
import cliffordha.totvw.effect.HavocEffect;
import cliffordha.totvw.entity.wolf.WolfEffects;
import cliffordha.totvw.registry.*;
import cliffordha.totvw.registry.attachments.*;
import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import cliffordha.totvw.util.TimeUtil;
import cliffordha.totvw.util.ExtUtil;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

import static cliffordha.totvw.util.ExtUtil.*;

public class RunestoneEffects {
    public static final AttachmentType<Runestone> RUNESTONE_TYPE = WolfAttachment.RUNESTONE_TYPE;
    public static final AttachmentType<List<String>> TETHERED_ENTITIES = WolfAttachment.TETHERED_ENTITY_TYPES;

    // ON TICK MAIN THREAD
    public static void onTickRunestoneEffect(Wolf wolf, ServerLevel level, Runestone type) {
        switch (type) {
            case HAVOC -> havocTick(wolf, level);
            case EFFLORESCENCE -> efflorescenceTick(wolf, level);
            default -> {}
        }
    }

    // ON TICK
    private static void efflorescenceTick(Wolf wolf, ServerLevel level) {}
    private static void havocTick(Wolf wolf, ServerLevel level) {
        LivingEntity owner = wolf.getOwner();

        if (owner == null) return;
        if (!(owner instanceof Player player)) return;

        int cdHavoc = player.getAttachedOrElse(PlayerAttachment.CD_HAVOC, 0);

        boolean inNether = isDimension(wolf, Level.NETHER) || isDimension(owner, Level.NETHER);
        if (owner.hasEffect(VWEffects.HAVOC)) {
            if (level.getGameTime() % 60 == 0 && !ExtUtil.isSameDimension(wolf, player)) {
                if (inNether) return;
                owner.removeEffect(VWEffects.HAVOC);
            }
        } else {

            boolean applied = ExtUtil.isSameDimension(wolf, player) || inNether;
            if (cdHavoc <= 0 && applied) {
                addEffect(player, VWEffects.HAVOC, 60, 0);

            }
        }
    }

    // HAVOC-RELATED
    public static void getHavocForPlayer(Player player, Entity source, float damage) {
        if (!(source instanceof LivingEntity attacker)) return;
        int CD = player.getAttachedOrElse(PlayerAttachment.CD_HAVOC, 0);
        if (CD > 0) return;

        if (!HavocType.isNone(player)) return;
        HavocData data;

        boolean noTotem = !player.getItemBySlot(EquipmentSlot.OFFHAND).is(Items.TOTEM_OF_UNDYING)
                || !player.getItemBySlot(EquipmentSlot.MAINHAND).is(Items.TOTEM_OF_UNDYING);
        boolean isVoid = damage >= player.getHealth() * 0.6f && noTotem;

        if (isVoid) {
            data = new HavocData(HavocType.VOID, 2);
            setPlayerHavocStat(player, data, 12);

        } else {
            boolean isObliteration = getEntityArmors(attacker) > 2;
            if (isObliteration) {
                data = new HavocData(HavocType.EXPULSION, 2);
                setPlayerHavocStat(player, data, 30);
            } else {
                data = new HavocData(HavocType.ANNIHILATION, 6);
                setPlayerHavocStat(player, data, 60);
            }
        }

        playSound(player, VWSounds.WOLF_SKILL_PARALYZE, SoundSource.PLAYERS, false);
        sendToChat(player, VWColors.HAVOC_PARTICLE, true, "Activated Havoc: " + data.type().getName());
        sendParticles(HavocEffect.HAVOC_PARTICLE, player.level(), player.blockPosition(), 32, 3);
    }
    public static void triggerHavocPenalty(LivingEntity attacker, HavocType type) {
        if (attacker == null) return;

        attacker.removeAllEffects();
        if (attacker.getHealth() > 4) {
            attacker.setHealth(4);
        }
        ServerLevel level = (ServerLevel) attacker.level();
        int duration = type == HavocType.VOID ? TimeUtil.minToSec(5) : TimeUtil.minToSec(2) + 30;
        attacker.hurtServer(level, VWDamageTypes.havoc(level), 4f);
        addEffect(attacker, VWEffects.PARALYZE, 10, 0);
        addEffect(attacker, MobEffects.GLOWING, duration, 0);
        sendParticles(HavocEffect.HAVOC_PARTICLE, (ServerLevel) attacker.level(), attacker.blockPosition(), 24, 0.5);
    }
    private static void setPlayerHavocStat(Player player, HavocData data, int duration) {
        int extra = VWEnchantments.getBenediction(player) ? 1 : 0;
        int sec = extra > 0 ? 15 : 0;
        restartEffect(player, VWEffects.HAVOC, duration + sec, 0);
        player.setAttached(VWAttachment.HAVOC_DATA, data.addUsageCount(extra));
    }
    public static void havocPlayerOnAttack(ServerLevel level, Player player, LivingEntity victim) {
        if (victim == null) return;
        if (victim instanceof Wolf) return;

        HavocData data = HavocData.getData(player);
        HavocType type = data.type();

        sendParticles(HavocEffect.HAVOC_PARTICLE, level, victim.blockPosition().above(), 32, 1);
        if (type == HavocType.ANNIHILATION) {
            addOrStackEffect(victim, MobEffects.WEAKNESS, 6, 1, true);
            addOrStackEffect(victim, MobEffects.HUNGER, 12, 0, true);
            ExtUtil.rewriteEffect(victim, MobEffects.RESISTANCE, 3);
            ExtUtil.rewriteEffect(victim, MobEffects.FIRE_RESISTANCE, 3);

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
                addEffect(victim, VWEffects.PARALYZE, 2, 0);
            }
            addOrStackEffect(player, MobEffects.SPEED, 6, 0, true);
            victim.knockback(1.3, victim.getXRot(), victim.getYHeadRot(), level.damageSources().explosion(null), 2);

        } else if (type == HavocType.VOID) {
            victim.teleportTo(victim.getX(), -200, victim.getZ());
            addOrStackEffect(player, MobEffects.SLOWNESS, 3, 0, true);
            addOrStackEffect(player, MobEffects.POISON, 15, 0, false);
        }

        HavocData.decrementUsageCount(player);
        int newUsage = HavocData.getUsageCount(player);

        if (newUsage < 1) {
            sendToChat(player, true, "Havoc has been removed");
            player.removeEffect(VWEffects.HAVOC);
        } else {
            sendToChat(player, true, type.name() + ": " + newUsage + " point(s) left");
        }
    }

    // LINK-RELATED
    public static void reapplyLinkStatus(LivingEntity victim, DamageSource damageSource) {
        if (!(damageSource.getEntity() instanceof Wolf wolf)) return;
        if (!Runestone.hasTether(wolf)) return;
        if (!VWEnchantments.getBenediction(wolf)) return;

        if (!(wolf.level() instanceof ServerLevel level)) return;
        processLinkStatus(wolf, victim, level);
    }
    public static void processLinkStatus(Wolf wolf, LivingEntity victim, ServerLevel level) {
        TetherData data = wolf.getAttachedOrElse(WolfAttachment.TETHER_DATA, TetherData.EMPTY);
        List<String> entities = new ArrayList<>(data.tetheredEntities());

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
                    ExtUtil.scanArea(wolf, Math.min(SCAN_SIZE, 24)),
                    test -> isEqualEntityID(entity, test)
            ).stream().limit(LINK_LIMIT).toList();

            if (scanner.isEmpty()) return;

            for (Entity mob : scanner) {
                if (!(mob instanceof LivingEntity target)) continue;
                if (target == victim) continue;

                if (target instanceof Player player) {
                    applyLinkToPlayer(wolf, player, level);
                } else {
                    applyLink(wolf, target, level);
                }
            }
        }
    }
    private static void applyLinkToPlayer(Wolf wolf, Player player, ServerLevel level) {
        if (WolfAttachment.isTetherBlacklisted(wolf, player)) return;

        if (wolf.getOwner() != null && wolf.getOwner().equals(player)) {
            return;
        } else {
            boolean isAggressor = WolfAttachment.isListedAggressor(wolf, player.getUUID());
            if (!isAggressor) {
                return;
            }
        }

        applyLink(wolf, player, level);
    }
    private static void applyLink(Wolf wolf, LivingEntity target, ServerLevel level) {
        if (wolf.isWearingBodyArmor()) {
            WolfEffects.onPostAttack(wolf, level, target, false);
        } else {
            target.hurtServer(level, VWDamageTypes.tetherProxy(level), Mth.ceil(wolf.getAttributeBaseValue(Attributes.ATTACK_DAMAGE)));
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

                int cycle = wolf.getAttachedOrElse(WolfAttachment.ATTACK_CYCLE, 0) - 1;
                List<Entity> scanner = level.getEntities(wolf,
                        scanArea(wolf, 12),
                        test -> isEqualEntityID(type, test)
                ).stream().limit(1).toList();

                if (scanner.isEmpty() && (cycle == 0 || cycle == 3)) {
                    newEntities.remove(type);
                }
            }
        }
        TetherData data = wolf.getAttachedOrElse(WolfAttachment.TETHER_DATA, TetherData.EMPTY);
        wolf.setAttached(WolfAttachment.TETHER_DATA, TetherData.updateEntities(data, List.copyOf(newEntities)));

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
        data = wolf.getAttachedOrElse(WolfAttachment.TETHER_DATA, TetherData.EMPTY);
        wolf.setAttached(WolfAttachment.TETHER_DATA, TetherData.updateEntities(data, List.copyOf(newEntities)));
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
