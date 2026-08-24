package cliffordha.totvw.mixin;

import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.entity.skill.VWSkillProcessor;
import cliffordha.totvw.registry.*;
import cliffordha.totvw.tag.VWBiomeTags;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;

import static cliffordha.totvw.util.VWUtil.isInBiome;

@Mixin(Villager.class)
public class VillagerEntityMixin {
    
    @Unique
    private static final AttachmentType<Boolean> VERDANT_TYPE = VWAttachments.Villager.VILLAGER_IS_VERDANT_TYPE;
    
    @Unique
    private static final AttachmentType<Integer> CD_HEAL_OTHERS = VWAttachments.Villager.VILLAGER_CD_HEAL_OTHERS;
    
    @Unique
    private static final AttachmentType<Integer> CD_HEAL_WOLF = VWAttachments.Villager.VILLAGER_CD_HEAL_WOLF;
    
    @Unique
    private static final AttachmentType<Integer> CD_HEAL_IRON_GOLEM = VWAttachments.Villager.VILLAGER_CD_HEAL_IRON_GOLEM;

    @Unique
    private static final AttachmentType<Integer> CD_DISCOUNT_REROLL = VWAttachments.Villager.VILLAGER_CD_DISCOUNT_REROLL;

    @Unique
    private static final AttachmentType<Float> DISCOUNT_MODIFIER = VWAttachments.Villager.VILLAGER_DISCOUNT_MODIFIER;
    
    @Unique
    private static final AttachmentType<Integer> WOLF_TRY_SAVE_POINTS = VWAttachments.Wolf.WOLF_TRY_SAVE_POINTS;

    @Inject(method = "finalizeSpawn", at = @At("TAIL"))
    private void setSpawnData(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnReason, SpawnGroupData groupData, CallbackInfoReturnable<SpawnGroupData> cir) {
        Villager villager = (Villager) (Object) this;
        boolean inVerdant = isInBiome(villager, VWBiomeTags.IS_VERDANT_BIOMES);

        VillagerData data = villager.getVillagerData();
        Holder<VillagerType> taiga = level.registryAccess()
                .lookupOrThrow(Registries.VILLAGER_TYPE)
                .getOrThrow(VillagerType.TAIGA);

        if (spawnReason == EntitySpawnReason.BREEDING) {
            villager.setVillagerData(villager.getVillagerData().withProfession(level.registryAccess(), VillagerProfession.NONE));
            if (inVerdant || villager.getAttachedOrElse(VERDANT_TYPE, false)) {
                villager.setAttached(VERDANT_TYPE, true);
            }
        }

        if (inVerdant) {
            villager.setAttached(VERDANT_TYPE, true);
            villager.setVillagerData(new VillagerData(taiga, data.profession(), data.level()));
        }
    }
    
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        Villager villager = (Villager) (Object) this;
        Level level = villager.level();
        boolean isVerdant = villager.getAttachedOrElse(VERDANT_TYPE, false);
        boolean isVerdantCleric = isVerdant && hasProfession(villager, VillagerProfession.CLERIC);
        boolean hasProfession = !hasProfession(villager, VillagerProfession.NONE) || !hasProfession(villager, VillagerProfession.NITWIT);

        int discountReroll = villager.getAttachedOrElse(CD_DISCOUNT_REROLL, 0);

        if (level.getGameTime() % 20 == 0) {
            if (hasProfession && isVerdant && discountReroll <= 0) {
                float MIN_MODIFIER = 0.15f;
                float MAX_MODIFIER = 0.5f;
                float BONUS = isInBiome(villager, VWBiomeTags.IS_VERDANT_BIOMES) ? 0.30f : 0.0f;

                float modifier = MIN_MODIFIER + villager.level().getRandom().nextFloat() * (MAX_MODIFIER + BONUS) - MIN_MODIFIER;
                villager.setAttached(DISCOUNT_MODIFIER, modifier);
                villager.setAttached(CD_DISCOUNT_REROLL, 1440);
            }
            if (discountReroll > 0) depleteCD(villager, CD_DISCOUNT_REROLL);
        }
        if (isVerdantCleric) {
            if (level.getGameTime() % 20 == 0) {
                depleteCD(villager, CD_HEAL_OTHERS);
                depleteCD(villager, CD_HEAL_WOLF);
                depleteCD(villager, CD_HEAL_IRON_GOLEM);
            }
            if (level.getGameTime() % 60 == 0) {
                int CD_HEAL_OTHERS = villager.getAttachedOrElse(VillagerEntityMixin.CD_HEAL_OTHERS, 0);
                int CD_HEAL_WOLF = villager.getAttachedOrElse(VillagerEntityMixin.CD_HEAL_WOLF, 0);
                int CD_HEAL_GOLEM = villager.getAttachedOrElse(CD_HEAL_IRON_GOLEM, 0);

                int villagerCount = getVillagerCount(villager);
                float healStrength = villagerCount >= 3 ? villager.getHealth() * 0.3f + (0.1f * villagerCount) : villager.getHealth() * 0.3f;
                double speed = 0.75;

                if (CD_HEAL_OTHERS <= 0) {
                    List<Villager> villagerList = level.getEntities(EntityTypes.VILLAGER, scanner(villager, 24),
                            target -> target.isAlive()
                                    && target.getHealth() < target.getMaxHealth() * 0.9f);
                    if (!villagerList.isEmpty()) {
                        Villager others = villagerList.getFirst();

                        villager.getNavigation().moveTo(others, speed);
                        others.heal(healStrength);
                        villager.setAttached(VillagerEntityMixin.CD_HEAL_OTHERS, 30);
                        healEffect(level, villager, others);
                    }
                }

                if (CD_HEAL_WOLF <= 0) {
                    List<Wolf> wolves = villager.level().getEntities(EntityTypes.WOLF, scanner(villager, 16),
                            wolf -> wolf.isAlive()
                                    && !wolf.isTame()
                                    && wolf.getHealth() < wolf.getMaxHealth() * 0.9f
                                    && wolf.getAttachedOrElse(WOLF_TRY_SAVE_POINTS, 0) > 0);
                    if (!wolves.isEmpty()) {
                        Wolf wolf = wolves.getFirst();

                        int currentPoints = wolf.getAttachedOrElse(WOLF_TRY_SAVE_POINTS, 0);

                        villager.getNavigation().moveTo(wolf.getX(), wolf.getY(), wolf.getZ(), 2, speed);
                        villager.lookAt(wolf, 10, 10);
                        wolf.heal(healStrength);
                        wolf.setAttached(WOLF_TRY_SAVE_POINTS, currentPoints - 1);
                        villager.setAttached(VillagerEntityMixin.CD_HEAL_WOLF, 60);
                        healEffect(level, villager, wolf);
                    }
                }
                if (CD_HEAL_GOLEM <= 0) {
                    List<IronGolem> golems = villager.level().getEntities(EntityTypes.IRON_GOLEM, scanner(villager, 16),
                            golem -> golem.isAlive()
                                    && golem.getHealth() < golem.getMaxHealth() * 0.75f);

                    if (!golems.isEmpty()) {
                        IronGolem golem = golems.getFirst();

                        villager.getNavigation().moveTo(golem, speed);
                        golem.heal(healStrength * 2f);
                        villager.setAttached(CD_HEAL_IRON_GOLEM, 90);
                        healEffect(level, villager, golem);
                    }
                }
            }
        }
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void onInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Villager villager = (Villager) (Object) this;
        if (player.getAttachedOrElse(VWAttachments.Player.PLAYER_VILLAGER_ATROCITY_COUNT, 0) > 20) {
            if (!villager.level().isClientSide()) {
                villager.makeSound(SoundEvents.VILLAGER_NO);
            }
            int unhappiness = villager.getUnhappyCounter();
            villager.setUnhappyCounter(unhappiness + 12);
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }

    @Inject(method = "updateSpecialPrices", at = @At("TAIL"))
    private void villagerVerdantTrades(Player player, CallbackInfo ci) {
        Villager villager = (Villager) (Object) this;
        if (!villager.getAttachedOrElse(VERDANT_TYPE, false)) return;
        if (player.getAttachedOrElse(VWAttachments.Player.PLAYER_VILLAGER_ATROCITY_COUNT, 0) > 20) return;

        float modifier = villager.getAttachedOrElse(DISCOUNT_MODIFIER, 0.0f);

        for (MerchantOffer offer : villager.getOffers()) {
            int costReduction = (int) Math.floor(modifier * (double) offer.getBaseCostA().getCount());
            offer.addToSpecialPriceDiff(-Math.max(costReduction, 1));
        }
    }

    @Unique
    private static void depleteCD(Villager villager, AttachmentType<Integer> cooldown) {
        if (VWConfig.get().SERVER_OTHER_COOLDOWNS) {
            VWSkillProcessor.depleteCooldown(villager, cooldown);
        } else {
            villager.setAttached(cooldown, 0);
        }
    }

    @Unique
    private static void healEffect(Level level, Villager villager, LivingEntity entity) {
        float random = 0.5f + level.getRandom().nextFloat();
        if (entity.is(EntityTypes.IRON_GOLEM)) {
            entity.playSound(SoundEvents.IRON_GOLEM_REPAIR, random, random);
            VWParticleEffects.spawnBlessingParticlesEntity(villager, 4);
        } else {
            villager.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, random, random);
            VWParticleEffects.spawnBlessingParticlesEntity(villager, 1);
            VWParticleEffects.spawnBlessingParticlesEntity(entity, 4);
        }
    }

    @Unique
    private static int getVillagerCount(Villager villager) {
        List<Villager> aliveVillagerList = villager.level().getEntities(
                EntityTypes.VILLAGER,
                villager.getBoundingBox().inflate(16),
                LivingEntity::isAlive);
        if (aliveVillagerList.isEmpty()) return 0;
        return aliveVillagerList.size();
    }

    @Unique
    private static AABB scanner(Villager villager, int size) {
        return villager.getBoundingBox().inflate(size);
    }

    @Unique
    private static boolean hasProfession(Villager villager, ResourceKey<VillagerProfession> profession) {
        return villager.getVillagerData().profession().is(Predicate.isEqual(profession));
    }
}