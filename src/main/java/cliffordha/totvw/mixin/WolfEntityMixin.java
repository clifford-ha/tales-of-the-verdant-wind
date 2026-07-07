package cliffordha.totvw.mixin;

import cliffordha.totvw.config.TOTVWConfig;
import cliffordha.totvw.util.ModUtil;
import cliffordha.totvw.entity.player.InteractionData;
import cliffordha.totvw.registry.*;
import cliffordha.totvw.tag.ModBiomeTags;
import cliffordha.totvw.tag.ModItemTags;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static cliffordha.totvw.util.ModUtil.*;
import static cliffordha.totvw.entity.skill.ConfigTools.*;

@Mixin(Wolf.class)
public abstract class WolfEntityMixin extends LivingEntity {

    protected WolfEntityMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Inject(method = "finalizeSpawn", at = @At("TAIL"))
    private void setSpawnData(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnReason, SpawnGroupData groupData, CallbackInfoReturnable<SpawnGroupData> cir) {
        Wolf wolf = (Wolf) (Object) this;
        boolean inVerdant = level.getBiome(wolf.blockPosition()).is(ModBiomeTags.IS_VERDANT_BIOMES);
        if (inVerdant) {
            wolf.setAttached(ModAttachments.Wolf.WOLF_IS_VERDANT_TYPE, true);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        Wolf wolf = (Wolf) (Object) this;
        boolean isVerdantType = wolf.getAttachedOrElse(ModAttachments.Wolf.WOLF_IS_VERDANT_TYPE, false);

        if (wolf.level().getGameTime() % 60 == 0) {
            if (isVerdantType && !wolf.isTame()) {
                if (!wolf.isAngry()) {
                    wolf.setAttached(ModAttachments.Wolf.WOLF_TRY_SAVE_STATUS, 0);
                }
                LivingEntity target = wolf.getTarget();
                List<Monster> monsters = wolf.level().getEntitiesOfClass(Monster.class, wolf.getBoundingBox().inflate(12), z -> z.getTarget() != null && z.getTarget().is(EntityType.VILLAGER));
                if (monsters.isEmpty()) return;

                if (target != null && target.isAlive() && !target.is(EntityType.VILLAGER)) return;

                for (Monster monster : monsters) {
                    wolf.setTarget(monster);
                    if (wolf.getAttachedOrElse(ModAttachments.Wolf.WOLF_TRY_SAVE_STATUS, 0) == 2) return;
                    wolf.setAttached(ModAttachments.Wolf.WOLF_TRY_SAVE_STATUS, 1);
                }
            }
        }
    }

    @ModifyArg(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V", ordinal = 4), index = 1)
    private Goal leap(Goal goal) {
        Wolf wolf = (Wolf) (Object) this;
        return new LeapAtTargetGoal(wolf, 0.55f);
    }

    @Inject(method = "applyTamingSideEffects", at = @At("HEAD"), cancellable = true)
    private void createAttributes(CallbackInfo ci) {
        Wolf wolf = (Wolf) (Object) this;

        boolean isVerdant = wolf.getAttachedOrElse(ModAttachments.Wolf.WOLF_IS_VERDANT_TYPE, false);

        if (wolf.isTame()) {
            wolf.getAttribute(Attributes.MAX_HEALTH).setBaseValue(40.0);
            wolf.setHealth(40.0f);
        } else {
            wolf.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20.0);
            wolf.setHealth(20.0f);
        }

        wolf.getAttribute(Attributes.WATER_MOVEMENT_EFFICIENCY).setBaseValue(0.1);

        if (isVerdant) {
            wolf.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.375);
        } else {
            wolf.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.305);
        }
        ci.cancel();
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void wolfInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Wolf wolf = (Wolf) (Object) this;
        String name = wolf.getName().getString();

        int ACTIVE_BENEDICTION = wolf.getAttachedOrElse(ModAttachments.Wolf.WOLF_BENEDICTION, 0);
        int ACTIVE_BENEDICTION_ENCHANTMENT = wolfEnchantmentLVL(wolf, ModEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS);
        boolean villagerGuard = wolf.getAttachedOrElse(ModAttachments.Wolf.WOLF_IS_VILLAGE_GUARD, false);
        ItemStack itemStack = player.getItemInHand(hand);

        if (wolf.isTame()) {
            if (itemStack.is(Items.STICK) && villagerGuard) {
                notifyFromWolf(wolf, ModColors.DEFAULT, true, "This is a village wolf");
                cir.setReturnValue(InteractionResult.PASS);
            }
            if (itemStack.is(Items.SHEARS) && wolf.getOwner() != player) {
                wolf.setOwner(null);
                wolf.setTame(false, false);
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
            if (itemStack.is(Items.TOTEM_OF_UNDYING) && wolf.isWearingBodyArmor()) {
                if (ACTIVE_BENEDICTION_ENCHANTMENT == 0) return;
                if (ACTIVE_BENEDICTION >= 3) {
                    notifyFromWolf(wolf, ModColors.DEFAULT_MUTED, true, "Max Benediction stack reached!");
                }
                if (ACTIVE_BENEDICTION >= 3) return;

                wolf.setAttached(ModAttachments.Wolf.WOLF_BENEDICTION, ACTIVE_BENEDICTION + 1);
                if (player.isCreative() || player.isSpectator())  { itemStack.shrink(1); }
                ModUtil.playSound(wolf, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.AMBIENT);

                addParticle(wolf.level(), wolf.blockPosition(), ModParticles.BENEDICTION_TRIGGER_PARTICLE, 1);
                notifyFromWolf(wolf, ModColors.VERDANT_WIND, name + " Benediction stack: " + wolf.getAttachedOrElse(ModAttachments.Wolf.WOLF_BENEDICTION, 0));
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        } else {
            if (itemStack.is(ModItemTags.WOLF_ARMOR_ENCHANTABLE) && !wolf.isWearingBodyArmor()) {
                if (wolf.level() instanceof ServerLevel serverLevel) {
                    wolf.equipItemIfPossible(serverLevel, itemStack);
                    cir.setReturnValue(InteractionResult.SUCCESS);
                }
            }
        }
    }

    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    private void reviveWolf(DamageSource source, CallbackInfo ci) {
        Wolf wolf = (Wolf) (Object) this;
        int ACTIVE_BENEDICTION = wolf.getAttachedOrElse(ModAttachments.Wolf.WOLF_BENEDICTION, 0);

        if (ACTIVE_BENEDICTION == 0) return;
        wolf.setHealth(40.0f);
        wolf.removeAllEffects();

        rewriteEffect(wolf, MobEffects.RESISTANCE, sec(3), 255);
        rewriteEffect(wolf, ModEffects.BLESSING_OF_THE_VERDANT_WIND, sec(10), 2);
        rewriteEffect(wolf, MobEffects.ABSORPTION, sec(10), 2);
        rewriteEffect(wolf, MobEffects.STRENGTH, sec(10), 2);

        wolf.setAttached(ModAttachments.Wolf.WOLF_BENEDICTION, ACTIVE_BENEDICTION - 1);

        if (ACTIVE_BENEDICTION - 1 == 0) {
            notifyFromWolf(wolf, ModColors.BLOODLUST_EFFECT_MUTED,ACTIVE_BENEDICTION - 1 + " Benediction stack remaining for " + wolf.getName().getString());
            wolf.removeAttached(ModAttachments.Wolf.WOLF_BENEDICTION);
        } else {
            notifyFromWolf(wolf, ModColors.VERDANT_WIND_MUTED,ACTIVE_BENEDICTION - 1 + " Benediction stack remaining for " + wolf.getName().getString());
        }

        if (wolf.level() instanceof ServerLevel) {
            wolf.level().broadcastEntityEvent(wolf, (byte) 35);
        }
        ci.cancel();
    }

    @Inject(method = "wantsToAttack", at = @At("HEAD"), cancellable = true)
    private void wantsToAttack(LivingEntity target, LivingEntity owner, CallbackInfoReturnable<Boolean> cir) {
        Wolf wolf = (Wolf) (Object) this;

        if (target instanceof Creeper) {
            if (wolf.getHealth() < wolf.getMaxHealth() * 0.5f) return;
            if (!wolf.isWearingBodyArmor()) return;
            int encProtection = wolfEnchantmentLVL(wolf, Enchantments.PROTECTION);
            int encBlastProtection = wolfEnchantmentLVL(wolf, Enchantments.BLAST_PROTECTION);
            int encGnawing = wolfEnchantmentLVL(wolf, ModEnchantments.WOLF_EFFECT_GNAWING);
            int encMight = wolfEnchantmentLVL(wolf, ModEnchantments.WOLF_EFFECT_MIGHT);
            int encBenediction = wolfEnchantmentLVL(wolf, ModEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS);
            if (!(encGnawing > 0 && (encProtection >= 3 || encBlastProtection >= 3 || encMight > 2 || encBenediction > 0))) return;
            cir.setReturnValue(true);
        }
        if (wolf.isTame() && wolf.getOwner() != null) {
            LivingEntity player = wolf.getOwner();
            String dash = "-";
            String truster = player.getName().getString() + player.getStringUUID();
            String trustee = dash + target.getName().getString() + target.getStringUUID();
            InteractionData data = target.getAttached(ModAttachments.ENTITY_INTERACTION_DATA);
            if (data == null) return;
            if (data.player().equals(truster) && data.trustee().equals(trustee)) {
                wolf.stopBeingAngry();
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "canArmorAbsorb", at = @At("TAIL"), cancellable = true)
    private void absorbDMG(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        Wolf wolf = (Wolf) (Object) this;
        cir.setReturnValue(wolf.getBodyArmorItem().is(ModItemTags.WOLF_ARMOR_ENCHANTABLE) && !source.is(DamageTypeTags.BYPASSES_WOLF_ARMOR));
    }

    @Inject(method = "actuallyHurt", at = @At("HEAD"), cancellable = true)
    private void distributeHurt(ServerLevel level, DamageSource source, float damage, CallbackInfo ci) {
        Wolf wolf = (Wolf) (Object) this;

        int ACTIVE_MIGHT = wolfEnchantmentLVL(wolf, ModEnchantments.WOLF_EFFECT_MIGHT);
        int ACTIVE_BENEDICTION = wolfEnchantmentLVL(wolf, ModEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS);
        int ACTIVE_ENHANCEMENT_KIT = wolfEnchantmentLVL(wolf, ModEnchantments.WOLF_ARMOR_ENHANCEMENT_KIT);

        if (TOTVWConfig.get().wolfArmorDamageDistribution) {
            if (!wolf.canArmorAbsorb(source)) return;

            ItemStack armor = wolf.getBodyArmorItem();
            int damageBefore = armor.getDamageValue();
            int maxDamage = armor.getMaxDamage();

            float computedDMG;
            if (ACTIVE_BENEDICTION > 0) {
                computedDMG = damage * 0.6f;
            } else if (ACTIVE_ENHANCEMENT_KIT > 0) {
                computedDMG = damage * 0.7f;
            } else if (ACTIVE_MIGHT > 0) {
                computedDMG = damage * 0.8f;
            } else {
                computedDMG = damage;
            }

            float finalArmorDMG = computedDMG * 0.75f;
            float finalWolfDMG = computedDMG * 0.25f;

            armor.hurtAndBreak(Mth.ceil(finalArmorDMG), wolf, EquipmentSlot.BODY);
            if (Crackiness.WOLF_ARMOR.byDamage(damageBefore, maxDamage) != Crackiness.WOLF_ARMOR.byDamage(wolf.getBodyArmorItem())) {
                wolf.playSound(SoundEvents.WOLF_ARMOR_CRACK);
                level.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, Items.ARMADILLO_SCUTE), wolf.getX(), wolf.getY() + 1.0, wolf.getZ(), 20, 0.2, 0.1, 0.2, 0.1);
            }

            super.actuallyHurt(level, source, finalWolfDMG);

            if (TOTVWConfig.get().sendLog) { notifyFromWolf(wolf, ModColors.DEFAULT_MUTED, "TrueDMG: " + damage + " §e| FinalWolfDMG: " + finalWolfDMG + " §d| FinalArmorDMG: " + finalArmorDMG); }
            ci.cancel();
        }
    }
}
