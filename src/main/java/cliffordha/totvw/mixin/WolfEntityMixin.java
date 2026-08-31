package cliffordha.totvw.mixin;

import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.item.custom.SoulRunestonePlate;
import cliffordha.totvw.tag.VWBiomeTags;
import cliffordha.totvw.registry.*;
import cliffordha.totvw.tag.VWItemTags;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.animal.wolf.WolfSoundVariants;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.TagValueOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

import static cliffordha.totvw.util.VWUtil.*;

@Mixin(Wolf.class)
public abstract class WolfEntityMixin extends LivingEntity {

    @Unique
    private static final AttachmentType<Integer> WOLF_TRY_SAVE_STATUS = VWAttachments.Wolf.WOLF_TRY_SAVE_STATUS;
    @Unique
    private static final AttachmentType<Boolean> WOLF_IS_VERDANT_TYPE = VWAttachments.Wolf.WOLF_IS_VERDANT_TYPE;
    @Unique
    private static final AttachmentType<Integer> BENEDICTION_STACK = VWAttachments.Wolf.WOLF_BENEDICTION;
    @Unique
    private static final AttachmentType<List<CompoundTag>> WOLF_SOULS = VWAttachments.Player.PLAYER_WOLF_SOULS;

    protected WolfEntityMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Unique
    private static void addAttributeModifier(Wolf wolf, Holder<Attribute> attribute, double amount) {
        AttributeInstance m = wolf.getAttribute(attribute);
        if (m != null && !m.hasModifier(VWIdentifiers.VERDANT_WOLF_PERMANENT_MODIFIERS)) {
            m.addPermanentModifier(new AttributeModifier(VWIdentifiers.VERDANT_WOLF_PERMANENT_MODIFIERS, amount, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    @Unique
    private static void setAttributeBaseValue(Wolf wolf, Holder<Attribute> attribute, double amount) {
        if (wolf.getAttributes().hasAttribute(attribute)) {
            wolf.getAttribute(attribute).setBaseValue(amount);
        }
    }

    @Unique
    private static void setVerdantModifiers(Wolf wolf) {
        addAttributeModifier(wolf, Attributes.ATTACK_DAMAGE, 2);
        addAttributeModifier(wolf, Attributes.MOVEMENT_SPEED, 0.075);
        addAttributeModifier(wolf, Attributes.SCALE, 0.2);
    }

    @Inject(method = "getBreedOffspring(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/AgeableMob;)Lnet/minecraft/world/entity/animal/wolf/Wolf;", at = @At("RETURN"), cancellable = true)
    private void inheritStat(ServerLevel level, AgeableMob partner, CallbackInfoReturnable<Wolf> cir) {
        Wolf baby = EntityType.WOLF.create(level, EntitySpawnReason.BREEDING);
        Wolf wolf = (Wolf) (Object) this;

        if (baby != null && partner instanceof Wolf wolfPartner) {
            String parentAUUID = wolf.getStringUUID();
            String parentBUUID = wolfPartner.getStringUUID();
            String constructor = parentAUUID + ":baby " + parentBUUID + ":baby";
            String parentConstructor = baby.getStringUUID() + ":parent";
            baby.setAttached(VWAttachments.Wolf.WOLF_PARENTS_ID, constructor);
            wolf.setAttached(VWAttachments.Wolf.WOLF_BABY_ID, parentConstructor);
            wolfPartner.setAttached(VWAttachments.Wolf.WOLF_BABY_ID, parentConstructor);

            if (wolf.getAttachedOrElse(WOLF_IS_VERDANT_TYPE, false)
                    || wolfPartner.getAttachedOrElse(WOLF_IS_VERDANT_TYPE, false)
                    || isInBiome(wolf, VWBiomeTags.IS_VERDANT_BIOMES)
                    || isInBiome(wolfPartner, VWBiomeTags.IS_VERDANT_BIOMES)) {
                baby.setAttached(WOLF_IS_VERDANT_TYPE, true);
                setVerdantModifiers(baby);
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
        }
    }

    @Inject(method = "finalizeSpawn", at = @At("TAIL"))
    private void setSpawnData(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnReason, SpawnGroupData groupData, CallbackInfoReturnable<SpawnGroupData> cir) {
        Wolf wolf = (Wolf) (Object) this;
        boolean inVerdant = isInBiome(wolf, VWBiomeTags.IS_VERDANT_BIOMES);
        boolean notSummoned = spawnReason != EntitySpawnReason.MOB_SUMMONED;
        if (inVerdant && notSummoned) {
            wolf.setAttached(WOLF_IS_VERDANT_TYPE, true);
            setVerdantModifiers(wolf);
        }
    }

    @Inject(method = "applyTamingSideEffects", at = @At("HEAD"), cancellable = true)
    private void createAttributes(CallbackInfo ci) {
        Wolf wolf = (Wolf) (Object) this;
        if (wolf.isTame()) {
            setAttributeBaseValue(wolf, Attributes.MAX_HEALTH, 40.0);
            wolf.setHealth(40.0f);
        } else {
            setAttributeBaseValue(wolf, Attributes.MAX_HEALTH, 20.0);
        }
        ci.cancel();
    }

    @Inject(method = "createAttributes", at = @At("RETURN"), cancellable = true)
    private static void initializeAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        cir.setReturnValue(Animal.createAnimalAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.305)
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 0.125)
                .add(Attributes.SCALE, 1.0)
        );
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        Wolf wolf = (Wolf) (Object) this;
        Level level = wolf.level();

        if (!wolf.isTame() && level.getGameTime() % 60 == 0) {
            if (wolf.getAttachedOrElse(WOLF_IS_VERDANT_TYPE, false)) {
                List<Monster> monsters = level.getEntitiesOfClass(Monster.class, wolf.getBoundingBox().inflate(12), z -> wolf.getTarget() == null && z.getTarget() != null && z.getTarget().is(EntityType.VILLAGER));
                if (monsters.isEmpty()) {
                    if (!(wolf.getAttachedOrElse(WOLF_TRY_SAVE_STATUS, 0) > 0 && !wolf.isAngry())) return;
                    if (!level.getRandom().nextBoolean()) return;
                    wolf.setAttached(WOLF_TRY_SAVE_STATUS, 0);
                    return;
                }

                for (Monster monster : monsters) {
                    wolf.setTarget(monster);
                    wolf.setAttached(WOLF_TRY_SAVE_STATUS, 1);
                }
            }

        }

        if (wolf.isBaby() && wolf.isOnFire() && level.getGameTime() % 20 == 0) {
            List<Wolf> parents = level.getEntitiesOfClass(Wolf.class, wolf.getBoundingBox().inflate(16), z ->
                    z.getAttachedOrElse(VWAttachments.Wolf.WOLF_BABY_ID, "").contains(wolf.getStringUUID() + ":parent")
                            && (wolfEnchantmentLVL(z, VWEnchantments.WOLF_EFFECT_IGNITION) > 0 || wolfEnchantmentLVL(z, VWEnchantments.WOLF_EFFECT_MIGHT) >= 3));

            if (!parents.isEmpty()) {
                wolf.getNavigation().moveTo(parents.getFirst(), 1.1);
                wolf.extinguishFire();
            }
        }
    }

    @ModifyArg(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V", ordinal = 4), index = 1)
    private Goal leap(Goal goal) {
        Wolf wolf = (Wolf) (Object) this;
        return new LeapAtTargetGoal(wolf, 0.55f);
    }

    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    private void reviveWolf(DamageSource source, CallbackInfo ci) {
        Wolf wolf = (Wolf) (Object) this;
        Level level = wolf.level();
        int STACK_BEFORE = wolf.getAttachedOrElse(BENEDICTION_STACK, 0);

        if (STACK_BEFORE == 0) return;
        wolf.setHealth(wolf.getMaxHealth() * 0.5f);
        wolf.removeAllEffects();
        wolf.dropLeash();
        wolf.unRide();
        wolf.setOrderedToSit(false);

        rewriteEffect(wolf, MobEffects.RESISTANCE, sec(3), 255);
        rewriteEffect(wolf, VWEffects.BLESSING_OF_THE_VERDANT_WIND, sec(10), 2);
        rewriteEffect(wolf, MobEffects.ABSORPTION, sec(10), 2);
        rewriteEffect(wolf, MobEffects.STRENGTH, sec(10), 2);

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.broadcastEntityEvent(wolf, (byte) 35);
        }

        // main
        wolf.setAttached(BENEDICTION_STACK, STACK_BEFORE - 1);

        String name = wolf.getName().getString();
        int STACK_AFTER = wolf.getAttachedOrElse(BENEDICTION_STACK, 0);
        if (STACK_AFTER == 0) {
            sendToChat(wolf, VWColors.BLOODLUST_EFFECT_MUTED, name + " used up all Benediction stacks");
        } else {
            sendToChat(wolf, VWColors.VERDANT_WIND_MUTED,STACK_AFTER + " Benediction stack remaining for " + name);
        }

        if (VWConfig.get().SERVER_TELL_OWNER_WHO_HURT_WOLF) {
            if (wolf.getOwner() != null && wolf.getOwner() instanceof Player player) {
                Entity attacker = source.getEntity();
                if (attacker != null) {
                    sendToChat(player, VWColors.BLOODLUST_EFFECT_MUTED, false, attacker.getName().getString() + " tried to kill " + name + ".");
                } else {
                    sendToChat(player, VWColors.BLOODLUST_EFFECT_MUTED, false, "Someone tried to kill " + name + ".");
                }
            }
        }

        if (VWConfig.get().SERVER_TELEPORT_AFTER_SAVE) {
            if (!wolf.isTame()) return;
            LivingEntity owner = wolf.getOwner();
            BlockPos spawn = wolf.getAttachedOrElse(VWAttachments.Wolf.WOLF_RESPAWN_POINT, wolf.blockPosition());

            if (owner == null) {
                wolf.teleportToAroundBlockPos(spawn);
            } else {
                BlockPos pos = owner.blockPosition();
                if (level.dimension() != owner.level().dimension()) {
                    wolf.teleportToAroundBlockPos(spawn);
                } else if (wolf.canTeleport(wolf.level(), owner.level())) {
                    if (wolf.distanceTo(owner) > 32) {
                        wolf.teleportToAroundBlockPos(pos);
                    } else if (wolf.distanceTo(owner) > 4) {
                        owner.teleportTo(pos.getX(), pos.getY() + 1, pos.getZ());
                    }
                } else {
                    wolf.teleportToAroundBlockPos(spawn);
                }
            }
        }
        ci.cancel();
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void onInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Wolf wolf = (Wolf) (Object) this;
        String name = wolf.getName().getString();
        Level level = wolf.level();

        ItemStack itemStack = player.getItemInHand(hand);

        boolean isOwner = wolf.getOwner() == player;
        boolean isTame = wolf.isTame();
        boolean isServer = !level.isClientSide();

         if (isServer && itemStack.is(Items.BONE) && !isTame && !wolf.isAngry()) {
             int atrocityCount = player.getAttachedOrElse(VWAttachments.Player.PLAYER_WOLF_ATROCITY_COUNT, 0);
             if (atrocityCount < 20) {
                 consumeItem(player, itemStack);
                 if (level.getRandom().nextInt(3) == 0) {
                     wolf.tame(player);
                     wolf.getNavigation().stop();
                     wolf.setTarget(null);
                     wolf.setOrderedToSit(true);
                     level.broadcastEntityEvent(wolf, (byte) 7);
                 } else {
                     level.broadcastEntityEvent(wolf, (byte) 6);
                 }
             } else {
                 sendToChat(player, VWColors.DEFAULT_MUTED, false, "Something is preventing you from taming this wolf. Try again later.\nWolf atrocity count: " + atrocityCount);
             }
             cir.setReturnValue(InteractionResult.SUCCESS_SERVER);
        }

        if (itemStack.is(Items.TOTEM_OF_UNDYING) &&  wolfEnchantmentLVL(wolf, VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS) > 0) {
            int STACK = wolf.getAttachedOrElse(BENEDICTION_STACK, 0);
            //int STACK_LIMIT = TOTVWConfig.get().SERVER_MAX_WOLF_BENEDICTION_STACK;
            //boolean LIMITER = level.getDifficulty() == Difficulty.HARD || level.getLevelData().isHardcore();
            int STACK_LIMIT = 3;

            if (STACK > STACK_LIMIT) {
                wolf.setAttached(BENEDICTION_STACK, STACK_LIMIT);
                sendToServer("Stack limit overflow. Reverting to " + STACK_LIMIT);
                cir.setReturnValue(InteractionResult.FAIL);
            }

            if (STACK < STACK_LIMIT) {
                wolf.setAttached(BENEDICTION_STACK, STACK + 1);
                wolf.level().playSound(null, wolf.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.NEUTRAL);
                VWParticleEffects.triggerBenedictionParticles(wolf, 4);
                sendToChat(player, VWColors.VERDANT_WIND, true, wolf.getName().getString() + " Benediction stack " + wolf.getAttachedOrElse(BENEDICTION_STACK, 0));
                consumeItem(player, itemStack);
                cir.setReturnValue(InteractionResult.SUCCESS);
            } else {
                sendToChat(player, true, name + " already reached Beneficiation Stack limit!");
                cir.setReturnValue(InteractionResult.PASS);
            }
        } else if (itemStack.is(ItemTags.BEDS) && isOwner) {
            BlockPos pos = player.getAttachedOrElse(VWAttachments.Player.PLAYER_RESPAWN_POINT, player.blockPosition());
            if (pos.getX() == 0 && pos.getZ() == 0) {
                sendToChat(player, true, "Cannot set a respawn point here");
                return;
            }
            wolf.setAttached(VWAttachments.Wolf.WOLF_RESPAWN_POINT, pos);
            sendToChat(player, VWColors.VERDANT_WIND, true, name + " has been set to respawn at " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
            player.playSound(VWSounds.NOTIFY);
            cir.setReturnValue(InteractionResult.SUCCESS);
        } else if (itemStack.is(VWItemTags.WOLF_ARMOR_ENCHANTABLE) && !isTame && !wolf.isWearingBodyArmor()) {
            if (level instanceof ServerLevel) {
                wolf.equipItemIfPossible((ServerLevel) wolf.level(), itemStack);
                consumeItem(player, itemStack);
            }
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
        if (itemStack.is(Items.HONEY_BOTTLE)) {
            boolean unTame = player.isCrouching()
                    && player.getAttachedOrElse(VWAttachments.Player.PLAYER_IS_DEV_MODE, false)
                    && wolf.isTame();

            if (!unTame) return;
            wolf.setOrderedToSit(false);
            wolf.setTame(false, true);
            wolf.setOwner(null);
            sendToChat(player, true, name + " has been un-tamed");
        }
        if (itemStack.is(VWItems.SOUL_RUNESTONE_PLATE) && isOwner) {
            if (player.getAttachedOrElse(VWAttachments.Player.PLAYER_WOLF_ATROCITY_COUNT, 0) > 10) {
                sendToChat(player, VWColors.BLOODLUST_EFFECT_MUTED, true, "The runestone did not respond...");
                cir.setReturnValue(InteractionResult.FAIL);
                return;
            }

            if (player.getAttachedOrElse(VWAttachments.Player.PLAYER_WOLF_SOULS_COUNTER, 0) < 12) {
                List<CompoundTag> souls;
                if (player.hasAttached(WOLF_SOULS)) {
                    souls = player.getAttachedOrElse(WOLF_SOULS, List.of());
                } else {
                    souls = new ArrayList<>(player.getAttachedOrElse(WOLF_SOULS, List.of()));
                }

                wolf.setOrderedToSit(false);

                TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, wolf.registryAccess());
                wolf.save(output);
                CompoundTag tag = output.buildResult();
                tag.remove("Pos");
                souls.add(tag);
                player.setAttached(WOLF_SOULS, souls);
                player.setAttached(VWAttachments.Player.PLAYER_WOLF_SOULS_COUNTER, souls.size());
                wolf.remove(Entity.RemovalReason.UNLOADED_TO_CHUNK);

                sendToChat(player, VWColors.VERDANT_WIND, true, name + "'s soul has been stored within you.");

                SoulRunestonePlate.processAdditional(player, souls.size(), false);
                cir.setReturnValue(InteractionResult.SUCCESS_SERVER);
            } else {
                sendToChat(player, VWColors.BLOODLUST_EFFECT, "You can only store up to 12 wolf souls.");
                cir.setReturnValue(InteractionResult.FAIL);
            }
        }
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
        } else if (target instanceof Wolf wolfy) {
            if (wolfy.isBaby() || wolfy.getAttachedOrElse(WOLF_IS_VERDANT_TYPE, false)) {
                cir.setReturnValue(false);
            }
        } else if (target instanceof Villager villager) {
            if (villager.isBaby() || villager.getAttachedOrElse(VWAttachments.Villager.VILLAGER_IS_VERDANT_TYPE, false)) {
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

        ItemStack armor = wolf.getBodyArmorItem();

        int ACTIVE_BENEDICTION = wolfEnchantmentLVL(wolf, VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS);
        int ACTIVE_ENHANCEMENT_KIT = wolfEnchantmentLVL(wolf, VWEnchantments.WOLF_ARMOR_ENHANCEMENT_KIT);
        int ACTIVE_IGNITION = wolfEnchantmentLVL(wolf, VWEnchantments.WOLF_EFFECT_IGNITION);

        if (source.is(DamageTypeTags.IS_FREEZING) && ACTIVE_IGNITION > 0) {
            ci.cancel();
            //return;
        }

        if (!source.is(DamageTypes.GENERIC_KILL) && damage > wolf.getMaxHealth() * 0.5f && ACTIVE_BENEDICTION > 0) {
            AttachmentType<Integer> IGNORE_DMG_CD = VWAttachments.Wolf.WOLF_CD_IGNORE_HIGH_DAMAGE;
            int IGNORE_DMG = wolf.getAttachedOrElse(IGNORE_DMG_CD, 0);
            if (IGNORE_DMG > 0) return;
            armor.hurtAndBreak(12, wolf, EquipmentSlot.BODY);
            wolf.setAttached(IGNORE_DMG_CD, 15);
            sendToChat(wolf, false, wolf.getName().getString() + " ignored " + (double) damage + " damage");
            ci.cancel();
            //return;
        }

        if (VWConfig.get().SERVER_WOLF_DMG_DISTRIBUTION) {
            if (!wolf.canArmorAbsorb(source)) return;

            int damageBefore = armor.getDamageValue();
            int maxDamage = armor.getMaxDamage();

            float computedDMG = ACTIVE_ENHANCEMENT_KIT > 0 ? damage * 0.75f : damage;

            int finalArmorDMG = Mth.ceil(computedDMG * 0.75f);
            int finalWolfDMG = Mth.ceil(computedDMG * 0.25f);

            armor.hurtAndBreak(finalArmorDMG, wolf, EquipmentSlot.BODY);
            if (Crackiness.WOLF_ARMOR.byDamage(damageBefore, maxDamage) != Crackiness.WOLF_ARMOR.byDamage(wolf.getBodyArmorItem())) {
                wolf.playSound(SoundEvents.WOLF_ARMOR_CRACK);
                level.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, VWItems.VERIXIUM_POWDER), wolf.getX(), wolf.getY() + 1.0, wolf.getZ(), 20, 0.2, 0.1, 0.2, 0.1);
            }

            super.actuallyHurt(level, source, finalWolfDMG);

            if (VWConfig.get().DEBUG_PRINT_LOGS) { sendToChat(wolf, VWColors.DEFAULT_MUTED, "TrueDMG: " + damage + " §e| FinalWolfDMG: " + finalWolfDMG + " §d| FinalArmorDMG: " + finalArmorDMG); }
            ci.cancel();
        }
    }

    @Unique
    private static void consumeItem(Player player, ItemStack itemStack) {
        if (player.isCreative()) return;
        itemStack.shrink(1);
    }

    @Unique
    private static final Logger SEND = LoggerFactory.getLogger("TOTVW/WolfEntityMixin");
    @Unique
    private static void sendToServer(String message) {
        if (VWConfig.get().MIXIN_UPDATE_LOGS) {
            SEND.info(message);
        }
    }
}
