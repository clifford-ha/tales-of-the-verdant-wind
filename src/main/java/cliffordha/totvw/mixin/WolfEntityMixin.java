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
import net.minecraft.sounds.SoundEvent;
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
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biomes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static cliffordha.totvw.util.ModUtil.*;
import static cliffordha.totvw.entity.skill.ConfigTools.*;

@Mixin(Wolf.class)
public abstract class WolfEntityMixin extends LivingEntity {

    @Unique
    private static final SoundEvent[] DISTANT_HOWL_SOUNDS = {
            ModSounds.WOLF_HOWL_A,
            ModSounds.WOLF_HOWL_B1,
            ModSounds.WOLF_HOWL_B2,
            ModSounds.WOLF_HOWL_B3};

    protected WolfEntityMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Inject(method = "finalizeSpawn", at = @At("TAIL"))
    private void setSpawnData(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnReason, SpawnGroupData groupData, CallbackInfoReturnable<SpawnGroupData> cir) {
        Wolf wolf = (Wolf) (Object) this;
        boolean inVerdant = level.getBiome(wolf.blockPosition()).is(ModBiomeTags.IS_VERDANT_BIOMES);
        if (!inVerdant) return;
        wolf.setAttached(ModAttachments.Wolf.IS_VERDANT_TYPE, true);
    }

    @Inject(method = "applyTamingSideEffects", at = @At("HEAD"), cancellable = true)
    private void createAttributes(CallbackInfo ci) {
        Wolf wolf = (Wolf) (Object) this;
        if (wolf.isTame()) {
            wolf.getAttribute(Attributes.MAX_HEALTH).setBaseValue(40.0);
            wolf.setHealth(40.0F);
        } else {
            wolf.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20.0);
        }
        wolf.setHealth(wolf.getMaxHealth());

        wolf.getAttribute(Attributes.WATER_MOVEMENT_EFFICIENCY).setBaseValue(0.1);

        if (wolf.getAttachedOrElse(ModAttachments.Wolf.IS_VERDANT_TYPE, false) == true) {
            wolf.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.43);
        }
        ci.cancel();
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void wolfInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Wolf wolf = (Wolf) (Object) this;
        String name = wolf.getName().getString();

        int ACTIVE_BENEDICTION = wolf.getAttachedOrElse(ModAttachments.Wolf.WOLF_BENEDICTION, 0);
        int ACTIVE_BENEDICTION_ENCHANTMENT = wolfEnchantmentLVL(wolf, ModEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS);
        ItemStack itemStack = player.getItemInHand(hand);

        if (wolf.isTame()) {
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
            InteractionData data = target.getAttached(ModAttachments.INTERACTION_DATA);
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
