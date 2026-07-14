package cliffordha.totvw.mixin;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.config.TOTVWConfig;
import cliffordha.totvw.entity.variants.VWWolfVariants;
import cliffordha.totvw.tag.VWBiomeTags;
import cliffordha.totvw.util.VWGlobalUtil;
import cliffordha.totvw.entity.VWInteractionData;
import cliffordha.totvw.registry.*;
import cliffordha.totvw.tag.VWItemTags;
import cliffordha.totvw.world.VWBiomes;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.animal.wolf.WolfSoundVariants;
import net.minecraft.world.entity.animal.wolf.WolfVariant;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.villager.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.variant.SpawnContext;
import net.minecraft.world.entity.variant.VariantUtils;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

import static cliffordha.totvw.util.VWGlobalUtil.*;
import static cliffordha.totvw.entity.skill.VWSkillProcessor.*;

@Mixin(Wolf.class)
public abstract class WolfEntityMixin extends LivingEntity {

    protected WolfEntityMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Unique
    private static boolean isVerdant(Wolf wolf) {
        return wolf.getAttachedOrElse(VWAttachments.Wolf.WOLF_IS_VERDANT_TYPE, false);
    }

    @Unique
    private static boolean isInVerdantBiome(Wolf wolf) {
        return wolf.level().getBiome(wolf.blockPosition()).is(VWBiomeTags.IS_VERDANT_BIOMES);
    }

    @Inject(method = "getBreedOffspring*", at = @At("RETURN"), cancellable = true)
    private void getOffspring(ServerLevel level, AgeableMob partner, CallbackInfoReturnable<Wolf> cir) {
        Wolf baby = EntityType.WOLF.create(level, EntitySpawnReason.BREEDING);
        Wolf wolf = (Wolf) (Object) this;

        if (baby != null && partner instanceof Wolf wolfPartner) {
            if (isVerdant(wolf) || isVerdant(wolfPartner) || isInVerdantBiome(wolf) || isInVerdantBiome(wolfPartner)) {
                baby.setCustomName(Component.literal("Verdant " + baby.getName().getString()).withColor(VWColors.VERDANT_WIND));
                baby.setAttached(VWAttachments.Wolf.WOLF_IS_VERDANT_TYPE, true);
            }

            if (this.random.nextBoolean()) {
                baby.setVariant(wolf.getVariant());
            } else {
                baby.setVariant(wolfPartner.getVariant());
            }

            if (wolf.isTame()) {
                baby.setOwnerReference(wolf.getOwnerReference());
                baby.setTame(true, true);
                DyeColor parent1CollarColor = wolf.getCollarColor();
                DyeColor parent2CollarColor = wolfPartner.getCollarColor();
                baby.setCollarColor(DyeColor.getMixedColor(level, parent1CollarColor, parent2CollarColor));
            }

            baby.setSoundVariant(WolfSoundVariants.pickRandomSoundVariant(this.registryAccess(), this.random));

            cir.setReturnValue(baby);
            cir.cancel();
        }
    }

    @Inject(method = "finalizeSpawn", at = @At("HEAD"))
    private void setSpawnData(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnReason, SpawnGroupData groupData, CallbackInfoReturnable<SpawnGroupData> cir) {
        Wolf wolf = (Wolf) (Object) this;

        if (isInVerdantBiome(wolf)) {
            wolf.setAttached(VWAttachments.Wolf.WOLF_IS_VERDANT_TYPE, true);
            wolf.setCustomName(Component.literal("Verdant " + wolf.getName().getString()).withColor(VWColors.VERDANT_WIND));
        }
        wolf.setAttached(VWAttachments.Wolf.WOLF_BENEDICTION, 0);
        if (isVerdant(wolf)) {
            wolf.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.375);
        } else {
            wolf.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.305);
        }
        wolf.getAttribute(Attributes.WATER_MOVEMENT_EFFICIENCY).setBaseValue(0.1);
    }

    @Inject(method = "applyTamingSideEffects", at = @At("HEAD"), cancellable = true)
    private void createAttributes(CallbackInfo ci) {
        Wolf wolf = (Wolf) (Object) this;
        if (wolf.isTame()) {
            wolf.getAttribute(Attributes.MAX_HEALTH).setBaseValue(40.0);
            wolf.setHealth(40.0f);
        } else {
            wolf.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20.0);
            wolf.setHealth(20.0f);
        }
        ci.cancel();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        Wolf wolf = (Wolf) (Object) this;

        if (wolf.level().getGameTime() % 60 == 0) {
            if (isVerdant(wolf) && !wolf.isTame()) {
                if (!wolf.isAngry()) {
                    wolf.setAttached(VWAttachments.Wolf.WOLF_TRY_SAVE_STATUS, 0);
                }
                LivingEntity target = wolf.getTarget();
                List<Monster> monsters = wolf.level().getEntitiesOfClass(Monster.class, wolf.getBoundingBox().inflate(12), z -> z.getTarget() != null && z.getTarget().is(EntityType.VILLAGER));
                if (monsters.isEmpty()) return;

                if (target != null && target.isAlive() && !target.is(EntityType.VILLAGER)) return;

                for (Monster monster : monsters) {
                    wolf.setTarget(monster);
                    if (wolf.getAttachedOrElse(VWAttachments.Wolf.WOLF_TRY_SAVE_STATUS, 0) >= 2) return;
                    wolf.setAttached(VWAttachments.Wolf.WOLF_TRY_SAVE_STATUS, 1);
                }
            }
        }
    }

    @ModifyArg(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V", ordinal = 4), index = 1)
    private Goal leap(Goal goal) {
        Wolf wolf = (Wolf) (Object) this;
        return new LeapAtTargetGoal(wolf, 0.55f);
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void wolfInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Wolf wolf = (Wolf) (Object) this;
        String name = wolf.getName().getString();

        int ACTIVE_BENEDICTION = wolf.getAttachedOrElse(VWAttachments.Wolf.WOLF_BENEDICTION, 0);
        ItemStack itemStack = player.getItemInHand(hand);

        boolean canUseTotem = itemStack.is(Items.TOTEM_OF_UNDYING)
                && wolf.isTame()
                && !(wolfEnchantmentLVL(wolf, VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS) == 0);

        boolean giveArmorToUntamed = itemStack.is(VWItemTags.WOLF_ARMOR_ENCHANTABLE)
                && !wolf.isTame()
                && !wolf.isWearingBodyArmor();

        boolean unTame = itemStack.is(Items.HONEY_BOTTLE)
                && player.getAttachedOrElse(VWAttachments.Player.PLAYER_IS_DEV_MODE, false)
                && wolf.isTame()
                && wolf.getOwner() == player;

        boolean checkStat = itemStack.is(VWItems.VERIXIUM_ARMOR_UPGRADE_TEMPLATE);

        if (canUseTotem) {
            if (ACTIVE_BENEDICTION >= 3) {

                //reset stat for unconfigured value
                if (ACTIVE_BENEDICTION > 3) {
                    wolf.setAttached(VWAttachments.Wolf.WOLF_BENEDICTION, 3);
                    TOTVW.sendInfo(name + " has more than 3 of the Wolf Benediction stack. Resetting to 3.");
                } else if (ACTIVE_BENEDICTION < 0) {
                    wolf.setAttached(VWAttachments.Wolf.WOLF_BENEDICTION, 0);
                    TOTVW.sendInfo(name + " has negative value of the Wolf Benediction stack. Resetting to 0.");
                }
                notifyFromWolf(wolf, VWColors.DEFAULT_MUTED, true, name + " already reached Wolf Beneficiation stack limit!");
                return;
            }

            if (ACTIVE_BENEDICTION == 0) {
                notifyFromWolf(wolf, VWColors.DEFAULT_MUTED, "Totem has been converted to Wolf Beneficiation stack");
            }

            wolf.setAttached(VWAttachments.Wolf.WOLF_BENEDICTION, ACTIVE_BENEDICTION + 1);
            notifyFromWolf(wolf, VWColors.VERDANT_WIND, "+1 Wolf Benediction stack to " + name);
            if (player.isCreative() || player.isSpectator())  {
                itemStack.shrink(1);
            }
            VWGlobalUtil.playSound(wolf, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.AMBIENT);

            addParticle(wolf.level(), wolf.blockPosition(), VWParticles.BENEDICTION_TRIGGER_PARTICLE, 1);
            cir.setReturnValue(InteractionResult.SUCCESS);
        }

        if (giveArmorToUntamed) {
            if (wolf.level() instanceof ServerLevel serverLevel) {
                wolf.equipItemIfPossible(serverLevel, itemStack);
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }

        if (unTame) {
            wolf.setTame(false, false);
            wolf.setOwner(null);
        }
        if (checkStat && wolf.getAttachedOrElse(VWAttachments.Wolf.WOLF_IS_VERDANT_TYPE, false)) {
            notifyFromWolf(wolf, VWColors.DEFAULT_MUTED, true, name + " is already a Verdant type");
        }
    }

    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    private void reviveWolf(DamageSource source, CallbackInfo ci) {
        Wolf wolf = (Wolf) (Object) this;
        int STACK_BEFORE = wolf.getAttachedOrElse(VWAttachments.Wolf.WOLF_BENEDICTION, 0);

        if (STACK_BEFORE == 0) return;
        wolf.setHealth(40.0f);
        wolf.removeAllEffects();

        rewriteEffect(wolf, MobEffects.RESISTANCE, sec(3), 255);
        rewriteEffect(wolf, VWEffects.BLESSING_OF_THE_VERDANT_WIND, sec(10), 2);
        rewriteEffect(wolf, MobEffects.ABSORPTION, sec(10), 2);
        rewriteEffect(wolf, MobEffects.STRENGTH, sec(10), 2);

        // main
        wolf.setAttached(VWAttachments.Wolf.WOLF_BENEDICTION, STACK_BEFORE - 1);

        String name = wolf.getName().getString();
        int STACK_AFTER = wolf.getAttachedOrElse(VWAttachments.Wolf.WOLF_BENEDICTION, 0);
        if (STACK_AFTER == 0) {
            notifyFromWolf(wolf, VWColors.BLOODLUST_EFFECT_MUTED, name + " used up all Benediction stacks");
        } else {
            notifyFromWolf(wolf, VWColors.VERDANT_WIND_MUTED,STACK_AFTER + " Benediction stack remaining for " + name);
        }

        if (wolf.level() instanceof ServerLevel) {
            wolf.level().broadcastEntityEvent(wolf, (byte) 35);
        }

        TOTVW.sendInfo(name + " has triggered Benediction! Remaining stacks: " + STACK_AFTER);
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
            int encGnawing = wolfEnchantmentLVL(wolf, VWEnchantments.WOLF_EFFECT_GNAWING);
            int encMight = wolfEnchantmentLVL(wolf, VWEnchantments.WOLF_EFFECT_MIGHT);
            int encBenediction = wolfEnchantmentLVL(wolf, VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS);
            if (!(encGnawing > 0 && (encProtection >= 3 || encBlastProtection >= 3 || encMight > 2 || encBenediction > 0))) return;
            cir.setReturnValue(true);
        }
        if (wolf.isTame() && wolf.getOwner() != null) {
            LivingEntity player = wolf.getOwner();
            String dash = "-";
            String truster = player.getName().getString() + player.getStringUUID();
            String trustee = dash + target.getName().getString() + target.getStringUUID();
            VWInteractionData data = target.getAttached(VWAttachments.ENTITY_INTERACTION_DATA);
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
        cir.setReturnValue(wolf.getBodyArmorItem().is(VWItemTags.WOLF_ARMOR_ENCHANTABLE) && !source.is(DamageTypeTags.BYPASSES_WOLF_ARMOR));
    }

    @Inject(method = "actuallyHurt", at = @At("HEAD"), cancellable = true)
    private void distributeHurt(ServerLevel level, DamageSource source, float damage, CallbackInfo ci) {
        Wolf wolf = (Wolf) (Object) this;

        int ACTIVE_MIGHT = wolfEnchantmentLVL(wolf, VWEnchantments.WOLF_EFFECT_MIGHT);
        int ACTIVE_BENEDICTION = wolfEnchantmentLVL(wolf, VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS);
        int ACTIVE_ENHANCEMENT_KIT = wolfEnchantmentLVL(wolf, VWEnchantments.WOLF_ARMOR_ENHANCEMENT_KIT);

        if (TOTVWConfig.get().WOLF_ARMOR_DMG_DISTRIBUTION) {
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

            if (TOTVWConfig.get().DEBUG_PRINT_LOGS) { notifyFromWolf(wolf, VWColors.DEFAULT_MUTED, "TrueDMG: " + damage + " §e| FinalWolfDMG: " + finalWolfDMG + " §d| FinalArmorDMG: " + finalArmorDMG); }
            ci.cancel();
        }
    }
}
