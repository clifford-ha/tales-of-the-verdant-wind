package cliffordha.totvw.mixin;

import cliffordha.totvw.config.TOTVWConfig;
import cliffordha.totvw.registry.*;
import cliffordha.totvw.tag.ModBiomeTags;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;

@Mixin(Villager.class)
public class VillagerEntityMixin {

    @Inject(method = "finalizeSpawn", at = @At("HEAD"))
    private void setSpawnData(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnReason, SpawnGroupData groupData, CallbackInfoReturnable<SpawnGroupData> cir) {
        Villager villager = (Villager) (Object) this;
        boolean inVerdant = level.getBiome(villager.blockPosition()).is(ModBiomeTags.IS_VERDANT_BIOMES);
        if (!inVerdant) return;
        villager.setAttached(ModAttachments.Villager.VILLAGER_IS_VERDANT_TYPE, true);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        Villager villager = (Villager) (Object) this;

        boolean isCorrectVillager = villager.getAttachedOrElse(ModAttachments.Villager.VILLAGER_IS_VERDANT_TYPE, false) && villager.getVillagerData().profession().is(Predicate.isEqual(VillagerProfession.CLERIC));
        if (!isCorrectVillager) return;

        if (villager.level().getGameTime() % 20 == 0) {
            depleteCD(villager, ModAttachments.Villager.VILLAGER_CD_HEAL_OTHERS);
            depleteCD(villager, ModAttachments.Villager.VILLAGER_CD_HEAL_WOLF);
            depleteCD(villager, ModAttachments.Villager.VILLAGER_CD_HEAL_IRON_GOLEM);
            depleteCD(villager, ModAttachments.Villager.VILLAGER_CD_DISCOUNT_REROLL);
        }

        if (villager.level().getGameTime() % 30 == 0) {
            int healOthersCD = villager.getAttachedOrElse(ModAttachments.Villager.VILLAGER_CD_HEAL_OTHERS, 0);
            int healWolfCD = villager.getAttachedOrElse(ModAttachments.Villager.VILLAGER_CD_HEAL_WOLF, 0);
            int healIronGolemCD = villager.getAttachedOrElse(ModAttachments.Villager.VILLAGER_CD_HEAL_IRON_GOLEM, 0);

            int villagerCount = getVillagerCount(villager);
            float healStrength = villagerCount >= 3 ? villager.getHealth() * 0.7f + (villagerCount * 2) : villager.getHealth() * 0.4f;

            if (healOthersCD <= 0) {
                List<Villager> villagerList = villager.level().getEntities(
                        EntityType.VILLAGER,
                        villager.getBoundingBox().inflate(12),
                        v -> v.isAlive()
                                && v.getHealth() < v.getMaxHealth());

                if (!villagerList.isEmpty()) {
                    for (Villager others : villagerList) {
                        others.heal(healStrength);
                        villager.setAttached(ModAttachments.Villager.VILLAGER_CD_HEAL_OTHERS, 30);
                        healEffect(villager, others);
                    }
                }
            }

            if (healWolfCD <= 0) {
                List<Wolf> wolves = villager.level().getEntities(
                        EntityType.WOLF,
                        villager.getBoundingBox().inflate(12),
                        wolf -> wolf.isAlive()
                                && wolf.getHealth() < wolf.getMaxHealth() * 0.9f
                                && wolf.getAttachedOrElse(ModAttachments.Wolf.WOLF_TRY_SAVE_POINTS, 0) > 0);

                if (!wolves.isEmpty()) {
                    for (Wolf wolf : wolves) {
                        int currentPoints = wolf.getAttachedOrElse(ModAttachments.Wolf.WOLF_TRY_SAVE_POINTS, 0);

                        wolf.heal(healStrength);
                        wolf.setAttached(ModAttachments.Wolf.WOLF_TRY_SAVE_STATUS, 0);
                        wolf.setAttached(ModAttachments.Wolf.WOLF_TRY_SAVE_POINTS, currentPoints - 1);
                        villager.setAttached(ModAttachments.Villager.VILLAGER_CD_HEAL_WOLF, 60);
                        healEffect(villager, wolf);
                    }
                }
            }
            if (healIronGolemCD <= 0) {
                List<IronGolem> golems = villager.level().getEntities(
                        EntityType.IRON_GOLEM,
                        villager.getBoundingBox().inflate(12),
                        golem -> golem.isAlive() && golem.getHealth() < golem.getMaxHealth() * 0.75f);

                if (!golems.isEmpty()) {
                    for (IronGolem golem : golems) {
                        golem.heal(healStrength * 2f);
                        villager.setAttached(ModAttachments.Villager.VILLAGER_CD_HEAL_IRON_GOLEM, 90);
                        golem.makeSound(SoundEvents.IRON_GOLEM_REPAIR);
                        healEffect(villager, golem);
                    }
                }
            }
        }
    }

    @Inject(method = "updateSpecialPrices", at = @At("TAIL"))
    private void villagerVerdantTrades(Player player, CallbackInfo ci) {
        Villager villager = (Villager) (Object) this;

        boolean verdantVillager = villager.getAttachedOrElse(ModAttachments.Villager.VILLAGER_IS_VERDANT_TYPE, false);
        boolean villagerInVerdant = villager.level().getBiome(villager.blockPosition()).is(ModBiomeTags.IS_VERDANT_BIOMES);

        if (!verdantVillager) return;

        int rerollCD = villager.getAttachedOrElse(ModAttachments.Villager.VILLAGER_CD_DISCOUNT_REROLL, 0);
        float modifier = villager.getAttachedOrElse(ModAttachments.Villager.VILLAGER_DISCOUNT_MODIFIER, 0.0f);
        float additional = villagerInVerdant ? 0.25f : 0.0f;

        float MIN_MODIFIER = 0.0f;
        float MAX_MODIFIER = 0.75f;
        int REROLL_INTERVAL = 1440; // 24 minutes

        if (rerollCD <= 0 || modifier <= 0.0f) {
            modifier = MIN_MODIFIER + villager.level().getRandom().nextFloat() * ((MAX_MODIFIER + additional) - MIN_MODIFIER);
            villager.setAttached(ModAttachments.Villager.VILLAGER_DISCOUNT_MODIFIER, modifier);
            villager.setAttached(ModAttachments.Villager.VILLAGER_CD_DISCOUNT_REROLL, REROLL_INTERVAL);
        }

        for (MerchantOffer offer : villager.getOffers()) {
            int costReduction = (int) Math.floor(modifier * (double) offer.getBaseCostA().getCount());
            offer.addToSpecialPriceDiff(-Math.max(costReduction, 1));
        }
    }

    @Unique
    private static void depleteCD(Villager villager, AttachmentType<Integer> cooldown) {
        if (TOTVWConfig.get().otherAttachmentCD) {
            int attachment = villager.getAttachedOrElse(cooldown, 0);
            if (attachment <= 0) return;
            villager.setAttached(cooldown, attachment - 1);
        } else {
            villager.setAttached(cooldown, 0);
        }
    }

    @Unique
    private static void healEffect(Villager villager, LivingEntity entity) {
        float random = villager.level().getRandom().nextFloat();
        villager.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 0.7f + random, 0.7f + random);
        ModParticleEffects.spawnBlessingParticlesEntity(villager, 1);
        ModParticleEffects.spawnBlessingParticlesEntity(entity, 4);
    }

    private static int getVillagerCount(Villager villager) {
        List<Villager> aliveVillagerList = villager.level().getEntities(
                EntityType.VILLAGER,
                villager.getBoundingBox().inflate(16),
                LivingEntity::isAlive);
        if (aliveVillagerList.isEmpty()) return 0;
        return aliveVillagerList.size();
    }
}