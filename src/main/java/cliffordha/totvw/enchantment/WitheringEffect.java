package cliffordha.totvw.enchantment;

import cliffordha.totvw.datagen.VWWolfVariants;
import cliffordha.totvw.entity.skill.SkillCD;
import cliffordha.totvw.entity.skill.SkillManager;
import cliffordha.totvw.entity.skill.WolfSkills;
import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.VWEnchantments;
import cliffordha.totvw.registry.attachments.Runestone;
import cliffordha.totvw.registry.attachments.WolfFragmentData;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import cliffordha.totvw.util.TextUtil;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.TagValueInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static cliffordha.totvw.util.ExtUtil.*;

public class WitheringEffect {
    public static final ParticleOptions FRAGMENT_PARTICLE = new DustParticleOptions(VWColors.WITHERING, 1.0f);

    public static void onAttack(ServerLevel level, Wolf wolf, LivingEntity player, LivingEntity victim, boolean exclusive) {
        int ACTIVE_WITHERING = VWEnchantments.getWithering(wolf);

        if (ACTIVE_WITHERING >= 3) {
            int cooldown = wolf.getAttachedOrElse(WolfAttachment.CD_WOLF_FRAGMENT_SUMMON, 0);
            if (cooldown <= 0 && exclusive) trySummon(level, wolf, player, victim);
        }

        restartEffect(victim, MobEffects.WITHER, ACTIVE_WITHERING + 2, Math.min(ACTIVE_WITHERING - 1, 2));
    }
    private static void trySummon(ServerLevel level, Wolf wolf, LivingEntity player, LivingEntity victim) {
        if (!wolf.hasAttached(WolfAttachment.FRAGMENT_DATA) && player != null) {
            List<Wolf> wolves = level.getEntitiesOfClass(Wolf.class, scanArea(wolf, 32),
                    test -> test.getOwner() == player);

            if (wolves.stream().filter(e -> e.hasAttached(WolfAttachment.FRAGMENT_DATA)).toList().size() >= 2) return;

            CompoundTag tag = new CompoundTag();
            tag.putString("id", "minecraft:wolf");
            TagValueInput input = (TagValueInput) TagValueInput.create(ProblemReporter.DISCARDING, wolf.registryAccess(), tag);
            Optional<Entity> created = EntityType.create(input, level, EntitySpawnReason.LOAD);

            if (created.isPresent() && created.get() instanceof Wolf fragment) {
                int additional = wolves.stream().filter(VWEnchantments::getBenediction).toList().size();
                int lifespanBonus = additional > 0 ? (additional * 5) : 0;

                fragment.setAttached(WolfAttachment.FRAGMENT_DATA, new WolfFragmentData(30 + lifespanBonus, 0, 12 + additional));
                fragment.setPos(wolf.getX(), wolf.getY(), wolf.getZ());
                fragment.teleportToAroundBlockPos(wolf.blockPosition());
                fragment.setOwner(player);
                fragment.setTame(true, true);
                fragment.setCollarColor(wolf.getCollarColor());
                fragment.setSilent(true);
                fragment.setTarget(victim);
                fragment.setVariant(level.registryAccess().lookupOrThrow(Registries.WOLF_VARIANT).getOrThrow(VWWolfVariants.FRAGMENTED));

                List<Wolf> armoredWolves = wolves.stream().filter(e -> e.isWearingBodyArmor() && !e.getBodyArmorItem().getEnchantments().isEmpty()).toList();
                ItemStack equipment = !armoredWolves.isEmpty() ? armoredWolves.get(random.nextInt(armoredWolves.size())).getBodyArmorItem() : wolf.getBodyArmorItem();

                List<Holder<Enchantment>> enchantments = new ArrayList<>();

                for (Object2IntMap.Entry<Holder<Enchantment>> enchantment : equipment.getEnchantments().entrySet().stream().toList()) {
                    enchantments.add(enchantment.getKey());
                }
                ItemStack newEquipment = new ItemStack(equipment.getItem());
                for (Holder<Enchantment> enchantment : enchantments) {
                    if (enchantment.is(Enchantments.MENDING)) continue;
                    newEquipment.enchant(enchantment, 1);
                }
                newEquipment.setDamageValue(equipment.getDamageValue());
                fragment.equipItemIfPossible(level, newEquipment);

                Runestone type = Runestone.getType(wolf);
                if (type != Runestone.EMPTY && type != Runestone.HAVOC) {
                    fragment.setAttached(WolfAttachment.RUNESTONE_TYPE, wolf.getAttached(WolfAttachment.RUNESTONE_TYPE));
                }
                if (wolf.hasAttached(WolfAttachment.TETHER_DATA)) {
                    fragment.setAttached(WolfAttachment.TETHER_DATA, wolf.getAttached(WolfAttachment.TETHER_DATA));
                }

                level.addFreshEntity(fragment);

                SkillManager.startCooldown(wolf, WolfSkills.SUMMON_FRAGMENTED_WOLF, SkillCD.BASE_FRAGMENTED_WOLF_SUMMON);
                sendToChat(player, true, TextUtil.nameWrap(wolf) + " A Fragmented Wolf has been summoned!");
            }
        }
    }
    public static void unloadFragmentedWolf(ServerLevel level, Wolf wolf) {
        playSound(wolf, SoundEvents.BEACON_DEACTIVATE, SoundSource.NEUTRAL, false);
        sendParticles(FRAGMENT_PARTICLE, level, wolf.blockPosition(), 24, 1.2);
        wolf.remove(Entity.RemovalReason.UNLOADED_TO_CHUNK);
    }
    public static void unloadPreviouslySummoned(ServerLevel level) {
        level.getEntities(EntityType.WOLF, test -> test.hasAttached(WolfAttachment.FRAGMENT_DATA)).forEach(
                entity -> entity.remove(Entity.RemovalReason.UNLOADED_TO_CHUNK)
        );
    }
}
