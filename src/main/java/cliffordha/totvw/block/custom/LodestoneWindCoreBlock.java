package cliffordha.totvw.block.custom;

import cliffordha.totvw.config.TOTVWConfig;
import cliffordha.totvw.datagen.VWDamageTypes;
import cliffordha.totvw.registry.*;
import cliffordha.totvw.tag.VWBiomeTags;
import cliffordha.totvw.tag.VWEntityTypeTags;
import cliffordha.totvw.tag.VWItemTags;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cliffordha.totvw.util.VWUtil.*;

public class LodestoneWindCoreBlock extends Block {
    public static final MapCodec<LodestoneWindCoreBlock> CODEC = simpleCodec(LodestoneWindCoreBlock::new);
    private static final int ENERGY_LIMIT = 100000;

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final IntegerProperty WIND_ENERGY = IntegerProperty.create("wind_energy", 0, ENERGY_LIMIT);

    public LodestoneWindCoreBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(ACTIVE, false).setValue(WIND_ENERGY, 0).setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ACTIVE, WIND_ENERGY);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(ACTIVE)) {
            int bound = random.nextIntBetweenInclusive(14, 18);
            if (level.getGameTime() % 20 * bound == 0) {
                float pitch = random.nextIntBetweenInclusive(20, 100) * 0.01f;
                level.playLocalSound(pos, VWSounds.LODESTONE_WIND_CORE_AMBIENT, SoundSource.BLOCKS, 0.1f, pitch, false);
            }
            if (state.getValue(WIND_ENERGY) > 0) {
                float randomPos = level.getRandom().nextFloat();
                level.addParticle(VWParticles.VERIXIUM_POWDER_RAIN_PARTICLE,
                        pos.getX() + randomPos, pos.getY() + 1, pos.getZ() + randomPos, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        String pName = player.getName().getString();
        boolean onSwitch = entityEnchantmentLVL(player, EquipmentSlot.CHEST, VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS) > 0 && itemStack.isEmpty();

        if (onSwitch || itemStack.is(VWItems.VERIXIUM_PAPER)) {
            if (!level.isClientSide()) {
                BlockState activeState = state.cycle(ACTIVE);
                level.setBlockAndUpdate(pos, activeState);
                if (activeState.getValue(ACTIVE)) {
                    level.scheduleTick(pos, this, 20);
                }
                String stat = level.getBlockState(pos).getValue(ACTIVE) ? "activated" : "deactivated";
                sendToLogger(LOG_ENERGY_UPDATES, "A core at " + getStringPos(pos) + " has been " + stat + " by " + pName + ".");
            }
            return InteractionResult.SUCCESS;
        }

        if (itemStack.is(VWItemTags.LODESTONE_WIND_CORE_ENERGY_SOURCES) && state.getValue(ACTIVE)) {
            if (state.getValue(WIND_ENERGY) >= ENERGY_LIMIT) {
                sendToChat(player, true, "Maximum amount of energy already reached!");
                return InteractionResult.SUCCESS;
            }

            if (!level.isClientSide()) {
                if (!player.isCreative()) {
                    itemStack.shrink(1);
                }
                int energy;
                if (itemStack.is(VWItems.VERIXIUM_POWDER)) {
                    energy = 350;
                } else if (itemStack.is(VWBlocks.VERIXIUM_POWDER_BLOCK.asItem())) {
                    energy = 3150;
                } else if (itemStack.is(Items.WIND_CHARGE)) {
                    energy = 75;
                } else energy = 0;

                if (energy == 0) return InteractionResult.PASS;

                addEnergy((ServerLevel) level, pos, state, energy);
                int stat = state.getValue(WIND_ENERGY) + energy;
                sendToChat(player, getIndicatorColor(state), true, "Lodestone Wind Core: " + Math.min(ENERGY_LIMIT, stat) + " energy");
            }

            level.playSound(null, pos, SoundEvents.SAND_PLACE, SoundSource.BLOCKS);

            return InteractionResult.SUCCESS;
        }

        if (itemStack.isEmpty() && state.getValue(ACTIVE)) {
            if (state.getValue(WIND_ENERGY) > 0) {
                sendToChat(player, getIndicatorColor(state), true, "Lodestone Wind Core: " + state.getValue(WIND_ENERGY) + " energy remaining.");
            } else {
                sendToChat(player, true, "Needs an energy source to function.");
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private static int getIndicatorColor(BlockState state) {
        int current = state.getValue(WIND_ENERGY);
        if (current < 250) {
            return VWColors.INDICATOR_20;
        } else if (current < 500) {
            return VWColors.INDICATOR_40;
        } else if (current < 1000) {
            return VWColors.INDICATOR_60;
        } else if (current < 2000) {
            return VWColors.INDICATOR_80;
        }
        return VWColors.INDICATOR_100;
    }
    private static void addEnergy(ServerLevel level, BlockPos pos, BlockState state, int value) {
        int compute = state.getValue(WIND_ENERGY) + value;
        int getStat = Math.min(compute, ENERGY_LIMIT);
        level.setBlockAndUpdate(pos, state.setValue(WIND_ENERGY, getStat));
    }
    private static void depleteEnergy(ServerLevel level, BlockPos pos, BlockState state, int value) {
        int compute = state.getValue(WIND_ENERGY) - value;
        int getStat = Math.max(compute, 0);
        level.setBlockAndUpdate(pos, state.setValue(WIND_ENERGY, getStat));
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(ACTIVE) && !level.isClientSide()) {
            level.scheduleTick(pos, this, 0);
            // notes: game time based on schedule tickDelay

            int deviation = random.nextIntBetweenInclusive(0, 10);

            if (state.getValue(WIND_ENERGY) < 50) {
                rechargeEnergy(level, pos, state);
            }

            if (state.getValue(WIND_ENERGY) > 0) {
                processEnergyConsumption(level, pos, state);

                if (tickInterval(level, 30 + deviation)) {
                    if (random.nextFloat() > 0.6f) return;
                    randomPositiveEffects(level, state, pos);
                    randomNegativeEffects(level, state, pos);
                }

                if (tickInterval(level, 10)) {
                    scanAndApplyEffects(level, state, pos);
                }

                if (tickInterval(level, 3)) {
                    if (random.nextBoolean()) {
                        removeImplodedStatus(level, state, pos);
                    }
                    if (random.nextFloat() < 0.03f && state.getValue(WIND_ENERGY) > 2000) {
                        transformToVerdantType(level, pos, state);
                    }
                }

                if (tickInterval(level, 1)) {
                    showRemainingEnergy(level, pos);
                }
            }

            if (tickInterval(level, 5) && state.getValue(WIND_ENERGY) <= 0 && random.nextFloat() < 0.5f) {
                level.setBlockAndUpdate(pos, state.setValue(ACTIVE, false));
                sendToLogger(LOG_ENERGY_UPDATES, "A core at " + getStringPos(pos) + " has been deactivated due to lack of energy.");
            }
        }
    }

    private static void processEnergyConsumption(ServerLevel level, BlockPos pos, BlockState state) {
        int additionalEnergyLoss = level.getDifficulty() == Difficulty.HARD ? 5 : 0;
        if (tickInterval(level, 10)) {
            int rate;

            if (isInBiome(level, pos, BiomeTags.IS_OVERWORLD)) {
                if (isInBiome(level, pos, VWBiomeTags.IS_VERDANT_FOREST)) {
                    rate = 2;
                } else if (level.getBiome(pos).value().getBaseTemperature() > 1.98f) {
                    rate = 15;
                } else if (isInBiome(level, pos, BiomeTags.IS_FOREST)) {
                    rate = 5;
                } else {
                    rate = 10;
                }
            } else if (isInBiome(level, pos, BiomeTags.IS_NETHER)) {
                boolean hasCooler = level.getBlockState(pos.below()).is(BlockTags.ICE);
                if (hasCooler) {
                    rate = 10;
                } else {
                    rate = 25;
                }
            } else {
                rate = 10;
            }

            int finalRate= rate + additionalEnergyLoss;

            // Standard rates may apply... Meralco yarn??
            depleteEnergy(level, pos, state, finalRate);
            sendToLogger(LOG_RECORD, "Energy consumed: " + finalRate);
        }
    }

    private static AABB scanner(BlockPos pos, int bound) {
        int yBound = Math.max((int) (bound * 0.75f), 1);
        return new AABB(pos.getX() - bound, pos.getY() - yBound, pos.getZ() - bound, pos.getX() + bound, pos.getY() + yBound, pos.getZ() + bound);
    }

    private void applyDangerousPulse(ServerLevel level, BlockState state, BlockPos pos) {
        AABB dangerousRange = scanner(pos, 3);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, dangerousRange, test ->
                !test.is(VWEntityTypeTags.IGNORES_STRONG_WIND_CORE_PULSE) && !test.is(EntityTypeTags.BOAT));
        float pulse = state.getValue(WIND_ENERGY) * 0.000095f;
        for (LivingEntity entity : entities) {
            boolean hasStrongProtection = entityEnchantmentLVL(entity, EquipmentSlot.CHEST, VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS) > 0
                    || entityEnchantmentLVL(entity, Enchantments.PROTECTION) > 3
                    || entityEnchantmentLVL(entity, Enchantments.BLAST_PROTECTION) > 2;
            if (hasStrongProtection) return;

            if (entity instanceof Player player) {
                if (player.isCreative() || player.isSpectator()) return;
                String dangerousPulseStamp = String.format("%.8s", player.getStringUUID()) + "-" + player.getPlainTextName() + "-dangerousPulse";
                if (!player.entityTags().contains(dangerousPulseStamp)) {
                    sendToChat(player, VWColors.BLOODLUST_EFFECT, false, "You are dangerously close to a strongly energized Wind Core!");
                    player.entityTags().add(dangerousPulseStamp);
                }
            }
            entity.hurtServer(level, VWDamageTypes.lodestoneWindCorePulse(level), pulse + entity.getMaxHealth() * 0.15f);
        }
    }

    private void scanAndApplyEffects(ServerLevel level, BlockState state, BlockPos pos) {
        double rangeMultiplier = state.getValue(WIND_ENERGY) > 60000 ? 1.5 : 1;
        AABB standardRange = scanner(pos, Mth.ceil(24 * rangeMultiplier));
        AABB monsterRange = scanner(pos, Mth.ceil(32 * rangeMultiplier));
        AABB shortRange = scanner(pos, Mth.ceil(16 * rangeMultiplier));

        int duration = Mth.ceil(180 * rangeMultiplier);

        float heal = isDifficulty(level, Difficulty.HARD) ? 2.0f : 6.0f;

        if (state.getValue(WIND_ENERGY) > 60000) applyDangerousPulse(level, state, pos);

        List<Wolf> wolves = level.getEntitiesOfClass(Wolf.class, standardRange);
        for (Wolf wolf : wolves) {
            int amp = wolf.isBaby() ? 2 : 0;
            wolf.heal(heal);

            addEffect(wolf, MobEffects.STRENGTH, duration, 0);
            addEffect(wolf, MobEffects.RESISTANCE, duration, amp);
            maybeClearBadEffects(level, wolf);

            VWParticleEffects.spawnBlessingParticlesEntity(wolf, 2);
        }

        List<Player> players = level.getEntitiesOfClass(Player.class, standardRange,
                test -> test.gameMode() == GameType.SURVIVAL
                        && test.getAttachedOrElse(VWAttachments.Player.PLAYER_WOLF_ATROCITY_COUNT, 0) < 20
                        && test.getAttachedOrElse(VWAttachments.Player.PLAYER_VILLAGER_ATROCITY_COUNT, 0) < 40
        );
        for (Player player : players) {
            addEffect(player, MobEffects.STRENGTH, duration, 0);
            maybeClearBadEffects(level, player);
            player.heal(heal);
            VWParticleEffects.spawnBlessingParticlesEntity(player, 0);
        }

        List<Villager> villagers = level.getEntitiesOfClass(Villager.class, standardRange);
        for (Villager villager : villagers) {
            int amp = villager.isBaby() ? 2 : 0;
            villager.heal(heal);

            addEffect(villager, MobEffects.ABSORPTION, duration, 0);
            addEffect(villager, MobEffects.RESISTANCE, duration, amp);
            maybeClearBadEffects(level, villager);

            VWParticleEffects.spawnBlessingParticlesEntity(villager, 2);

            boolean isCorrectVillager = villager.getAttachedOrElse(VWAttachments.Villager.VILLAGER_IS_VERDANT_TYPE, false)
                    && villager.getVillagerData().profession().is(Predicate.isEqual(VillagerProfession.CLERIC));
            if (isCorrectVillager) {
                float tryChance = villager.level().getRandom().nextBoolean() ? 0.15f : 0.3f;
                float randomizer = villager.level().getRandom().nextFloat() + tryChance;
                if (randomizer < 0.5f) helpHealer(villager, VWAttachments.Villager.VILLAGER_CD_HEAL_OTHERS);
                if (randomizer < 0.33f) helpHealer(villager, VWAttachments.Villager.VILLAGER_CD_HEAL_WOLF);
                if (randomizer < 0.33f) helpHealer(villager, VWAttachments.Villager.VILLAGER_CD_HEAL_IRON_GOLEM);
            }
        }

        List<IronGolem> golems = level.getEntitiesOfClass(IronGolem.class, shortRange);
        for (IronGolem golem : golems) {
            golem.heal(heal);
            addEffect(golem, MobEffects.ABSORPTION, duration, 0);
            maybeClearBadEffects(level, golem);
            VWParticleEffects.spawnBlessingParticlesEntity(golem, 0);
        }

        List<WanderingTrader> traders = level.getEntitiesOfClass(WanderingTrader.class, shortRange);
        for (WanderingTrader trader : traders) {
            addEffect(trader, MobEffects.ABSORPTION, duration, 0);
            maybeClearBadEffects(level, trader);
            trader.heal(heal);
            VWParticleEffects.spawnBlessingParticlesEntity(trader, 0);
        }

        List<LivingEntity> monsters = level.getEntitiesOfClass(LivingEntity.class, monsterRange, monster -> monster instanceof Enemy);
        for (LivingEntity monster : monsters) {
            boolean shouldPressurize = !monster.getAttachedOrElse(VWAttachments.WindCore.ENTITY_HAS_IMPLODED, false);
            if (shouldPressurize) {
                addPressureDifferenceToEnemy(monster, level, pos, state);
            }

            removeEffect(monster, MobEffects.INVISIBILITY);

            if (!monster.getAttachedOrElse(VWAttachments.ENTITY_HAS_VERDANT_OMEN, false)) {
                applyVerdantOmen(monster);
                VWParticleEffects.triggerMightParalyzeParticles(monster, 4);
            }

            VWParticleEffects.triggerMightParalyzeParticles(monster, 1);
            float pulseDMG = 2f * (level.getRandom().nextIntBetweenInclusive(1, 2) + level.getRandom().nextFloat());
            monster.hurtServer(level, VWDamageTypes.lodestoneWindCorePulse(level), pulseDMG);
        }

        int G1 = wolves.size() * 2;
        int G2 = villagers.size() * 2;
        int G3 = players.size() * 5;
        int G4 = traders.size() * 5;
        int G5 = monsters.size() * 7;

        float multiplier = level.getDifficulty() == Difficulty.HARD ? 1.2f : 1.0f;

        int finalTotal = (int) ((G1 + G2 + G3 + G4 + G5) * multiplier);
        //sendToServer("Nearby entity count: " + (wolves.size() + villagers.size() + players.size() + traders.size() + monsters.size()) + " Energy Use Total: " + finalTotal);
        if (finalTotal <= 0) return;

        depleteEnergy(level, pos, state, finalTotal);
    }

    private static void helpHealer(Villager villager, AttachmentType<Integer> cd) {
        int currentCD = villager.getAttachedOrElse(cd, 0);
        int computed = currentCD > 5 ? Math.max(currentCD / 2, 0) : currentCD;
        villager.setAttached(cd, computed);
    }

    private static void maybeClearBadEffects(ServerLevel level, LivingEntity entity) {
        if (!(level.getRandom().nextFloat() < 0.33f)) return;

        if (entity instanceof Player player) {
            removeEffect(player, MobEffects.NAUSEA);
            removeEffect(player, MobEffects.BLINDNESS);
        }
        if (level.getRandom().nextBoolean()) {
            removeEffect(entity, MobEffects.WEAKNESS);
            removeEffect(entity, MobEffects.SLOWNESS);
        } else {
            removeEffect(entity, MobEffects.WITHER);
            removeEffect(entity, MobEffects.POISON);
        }
    }

    private static void applyVerdantOmen(LivingEntity monster) {
        float healthDecrease = monster.getMaxHealth() > 150f ? 0.0f : -0.3f;
        Identifier omen = VWIdentifiers.VERDANT_OMEN;

        addAttributeModifier(monster, omen, Attributes.MAX_HEALTH, healthDecrease, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        addAttributeModifier(monster, omen, Attributes.ATTACK_DAMAGE, -1, AttributeModifier.Operation.ADD_VALUE);
        addAttributeModifier(monster, omen, Attributes.MOVEMENT_SPEED, -0.25f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        addAttributeModifier(monster, omen, Attributes.ATTACK_KNOCKBACK, -0.1f, AttributeModifier.Operation.ADD_VALUE);

        monster.setAttached(VWAttachments.ENTITY_HAS_VERDANT_OMEN, true);
        monster.level().playSound(null, monster.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.HOSTILE, 1.0F, 1.0F);
    }
    private static void addPressureDifferenceToEnemy(LivingEntity monster, ServerLevel level, BlockPos pos, BlockState state) {
        AttachmentType<Boolean> ATTACHMENT_IMPLODE = VWAttachments.WindCore.ENTITY_HAS_IMPLODED;
        AttachmentType<Integer> ATTACHMENT_PRESSURE = VWAttachments.WindCore.ENTITY_PRESSURE_DIFFERENCE;
        boolean hardMode = level.getDifficulty() == Difficulty.HARD;

        int currentPressure = monster.getAttachedOrElse(ATTACHMENT_PRESSURE, 0);

        int additional = level.getBiome(pos).is(VWBiomeTags.IS_VERDANT_BIOMES) ? 5 : 0;
        int wind = level.getRandom().nextIntBetweenInclusive(0, 10);
        int base = hardMode ? 5 : 10;
        int mass;
        if (monster.getMaxHealth() > 100f) {
            mass = 7;
        } else if (monster.getMaxHealth() > 50f) {
            mass = 5;
        } else {
            mass = 3;
        }
        int getPressureDiff = base + wind + additional + mass;
        int finalPressure = hardMode ? (int) (getPressureDiff * 0.75f) : getPressureDiff;

        float baseChance = monster.level().getRandom().nextFloat();
        float tryChance = (float) ((currentPressure * 0.01) - 1.0f);
        float healthToDMG = hardMode ? monster.getHealth() : monster.getMaxHealth();
        float baseDMG = hardMode ? 10f : 20f;

        String name = monster.getPlainTextName();

        sendToLogger(LOG_RECORD, name + " implosion chance: " + tryChance);

        if (baseChance < tryChance) {
            monster.hurtServer(level, VWDamageTypes.lodestoneWindCorePulse(level), baseDMG + healthToDMG * 0.3f);
            monster.forceAddEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 2), null);

            monster.setAttached(ATTACHMENT_IMPLODE, true);
            monster.setAttached(ATTACHMENT_PRESSURE, 0);

            double random = level.getRandom().nextDouble();
            level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, monster.getX() + random, monster.getY() + random, monster.getZ() + random, 3, random, random, random, 0);
            monster.makeSound(SoundEvents.WIND_CHARGE_BURST.value());

            depleteEnergy(level, pos, state, 750);

            sendToLogger(LOG_ENTITY_EVENT, name + " has been imploded by the Lodestone Wind Core.");
        } else {
            monster.setAttached(ATTACHMENT_PRESSURE, (currentPressure + finalPressure));
        }
        sendToLogger(LOG_ENTITY_EVENT, name + " has a pressure difference of " + monster.getAttachedOrElse(ATTACHMENT_PRESSURE, 0));
    }
    private static void removeImplodedStatus(ServerLevel level, BlockState state, BlockPos pos) {
        AABB test = scanner(pos, 24);
        List<Monster> monsters = level.getEntitiesOfClass(Monster.class, test, mob -> mob.getAttachedOrElse(VWAttachments.WindCore.ENTITY_HAS_IMPLODED, false));
        if (monsters.isEmpty()) return;
        for (Monster monster : monsters) {
            monster.removeAttached(VWAttachments.WindCore.ENTITY_HAS_IMPLODED);
            depleteEnergy(level, pos, state, 50);
        }
    }

    private static void transformToVerdantType(ServerLevel level, BlockPos pos, BlockState state) {
        AABB test = scanner(pos, 12);
        List<Wolf> wolves = level.getEntitiesOfClass(Wolf.class, test, wolf -> !wolf.getAttachedOrElse(VWAttachments.Wolf.WOLF_IS_VERDANT_TYPE, false));
        if (!wolves.isEmpty()) {
            int random = wolves.size() == 1 ? 0 : level.getRandom().nextIntBetweenInclusive(0, wolves.size() - 1);
            Wolf wolf = wolves.get(Math.max(random, 0));

            wolf.setAttached(VWAttachments.Wolf.WOLF_IS_VERDANT_TYPE, true);
            Identifier verdant = VWIdentifiers.VERDANT_WOLF_PERMANENT_MODIFIERS;
            addAttributeModifier(wolf, verdant, Attributes.ATTACK_DAMAGE, 2, AttributeModifier.Operation.ADD_VALUE);
            addAttributeModifier(wolf, verdant, Attributes.MOVEMENT_SPEED, 0.075, AttributeModifier.Operation.ADD_VALUE);
            addAttributeModifier(wolf, verdant, Attributes.SCALE, 0.2, AttributeModifier.Operation.ADD_VALUE);

            VWParticleEffects.triggerBenedictionParticles(wolf, 4);
            sendToLogger(LOG_ENTITY_CONVERSION, "A core at " + getStringPos(pos) + " converted a nearby wolf into a verdant type.");
        }

        List<Villager> villagers = level.getEntitiesOfClass(Villager.class, test, villager -> !villager.getAttachedOrElse(VWAttachments.Villager.VILLAGER_IS_VERDANT_TYPE, false));
        if (!villagers.isEmpty()) {
            int random = villagers.size() == 1 ? 0 : level.getRandom().nextIntBetweenInclusive(0, villagers.size());
            Villager villager = villagers.get(Math.min(random, 0));

            villager.setAttached(VWAttachments.Villager.VILLAGER_IS_VERDANT_TYPE, true);
            VWParticleEffects.triggerBenedictionParticles(villager, 4);
            sendToLogger(LOG_ENTITY_CONVERSION, "A core at " + getStringPos(pos) + " converted a nearby villager into a verdant type.");
        }
        depleteEnergy(level, pos, state, 1000);
    }

    private static void randomNegativeEffects(ServerLevel level, BlockState state, BlockPos pos) {
        List<LivingEntity> monsters = level.getEntitiesOfClass(LivingEntity.class, scanner(pos, 48), monster -> monster instanceof Enemy);
        int getRandom = level.getRandom().nextIntBetweenInclusive(1, 5);

        for (LivingEntity monster : monsters) {
            switch (getRandom) {
                case 1 -> addOrStackEffect(monster, MobEffects.SLOWNESS, 20 * 30, 0);
                case 2 -> addOrStackEffect(monster, MobEffects.WEAKNESS, 20 * 30, 1);
                case 3 -> addOrStackEffect(monster, MobEffects.WITHER, 20 * 15, 1);
                case 4 -> addOrStackEffect(monster, VWEffects.PARALYZE, 20 * 5, 0);
                case 5 -> addOrStackEffect(monster, MobEffects.GLOWING, 20 * 30, 0);
                default -> {}
            }

            depleteEnergy(level, pos, state, 15);
        }
    }
    private static void randomPositiveEffects(ServerLevel level, BlockState state, BlockPos pos) {
        AABB test = scanner(pos, 24);
        int randomAMP = level.getDifficulty() == Difficulty.HARD ? 0 : 1;
        int randomDuration = level.getRandom().nextBoolean() ? (20 * 45) : (20 * 30);

        List<Wolf> wolves = level.getEntitiesOfClass(Wolf.class, test);
        if (!wolves.isEmpty()) {
            for (Wolf wolf : wolves) {
                Holder<MobEffect> effect = level.getRandom().nextBoolean() ? MobEffects.STRENGTH : MobEffects.RESISTANCE;

                addOrStackEffect(wolf, effect, randomDuration, randomAMP);
                addOrStackEffect(wolf, VWEffects.AMPLIFIED_MIGHT, randomDuration, 0);
                addOrStackEffect(wolf, VWEffects.BLESSING_OF_THE_VERDANT_WIND, randomDuration, 2);

                if (wolf.isAngry() && wolf.getHealth() > wolf.getMaxHealth() * 0.5f) {
                    addOrStackEffect(wolf, VWEffects.BLOODLUST, 20 * 15, randomAMP);
                }
            }
        }

        List<Villager> villagers = level.getEntitiesOfClass(Villager.class, test);
        if (!villagers.isEmpty()) {
            for (Villager villager : villagers) {
                int resistanceAMP = (villager.isBaby() || villager.getHealth() < villager.getMaxHealth() * 0.5f) ? 1 : 0;
                Holder<MobEffect> effect = level.getRandom().nextBoolean() ? MobEffects.ABSORPTION : MobEffects.HEALTH_BOOST;

                addOrStackEffect(villager, effect, randomDuration, randomAMP);
                addOrStackEffect(villager, MobEffects.RESISTANCE, randomDuration, resistanceAMP);
                addOrStackEffect(villager, VWEffects.BLESSING_OF_THE_VERDANT_WIND, randomDuration, 2);
            }
        }

        List<Player> players = level.getEntitiesOfClass(Player.class, test);
        if (!players.isEmpty()) {
            for (Player player : players) {
                int CHECK_1 = player.getAttachedOrElse(VWAttachments.Player.PLAYER_WOLF_ATROCITY_COUNT, 0);
                int CHECK_2 = player.getAttachedOrElse(VWAttachments.Player.PLAYER_VILLAGER_ATROCITY_COUNT, 0);

                String omenStamp = String.format("%.8s", player.getStringUUID()) + "-" + player.getPlainTextName() + "-verdantOmen";

                if ((CHECK_1 + CHECK_2) > 60) {
                    if (player.isCreative() || player.isSpectator()) return;
                    addOrStackEffect(player, MobEffects.WEAKNESS, randomDuration, 2);
                    addOrStackEffect(player, MobEffects.SLOWNESS, randomDuration, 0);

                    if (player.entityTags().contains(omenStamp)) return;
                    sendToChat(player, VWColors.INDICATOR_40, true, "A surge of omen carried by the winds washes upon you...");
                    player.entityTags().add(omenStamp);
                } else {
                    player.entityTags().remove(omenStamp);
                    addOrStackEffect(player, MobEffects.RESISTANCE, randomDuration, randomAMP);
                    addOrStackEffect(player, VWEffects.BLESSING_OF_THE_VERDANT_WIND, randomDuration, 2);
                }
            }
        }

        List<WanderingTrader> wanderingTraders = level.getEntitiesOfClass(WanderingTrader.class, test);
        if (!wanderingTraders.isEmpty()) {
            for (WanderingTrader wanderingTrader : wanderingTraders) {
                addOrStackEffect(wanderingTrader, MobEffects.RESISTANCE, randomDuration, randomAMP);
                addOrStackEffect(wanderingTrader, VWEffects.BLESSING_OF_THE_VERDANT_WIND, randomDuration, 2);
            }
        }

        if (wolves.isEmpty() && players.isEmpty() && villagers.isEmpty() && wanderingTraders.isEmpty()) return;
        depleteEnergy(level, pos, state, 10);
    }

    private static void rechargeEnergy(ServerLevel level, BlockPos pos, BlockState state) {
        if (!level.getBlockState(pos.above()).is(VWBlocks.VERIXIUM_POWDER_BLOCK)) return;
        level.destroyBlock(pos.above(), false);
        addEnergy(level, pos, state, 1800);
        sendToLogger(LOG_ENERGY_UPDATES, "A core at " + getStringPos(pos) + " has been recharged using the Verixium Powder block.");
    }

    private static void showRemainingEnergy(ServerLevel level, BlockPos pos) {
        AABB test = scanner(pos, 3);
        List<Player> players = level.getEntitiesOfClass(Player.class, test, Entity::isCrouching);
        if (players.isEmpty()) return;
        for (Player player : players) {
            BlockState state = level.getBlockState(pos);
            int color = getIndicatorColor(state);
            sendToChat(player, color, true, "Lodestone Wind Core: " + level.getBlockState(pos).getValue(WIND_ENERGY) + " energy remaining.");
        }
    }



    @Override
    protected void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack tool, boolean dropExperience) {
        int energy = state.getValue(WIND_ENERGY);
        if (energy > 0) {
            int residue = (int) Math.max(Math.floor((double) energy / 350) * 0.80f, 0);
            if (residue <= 1) return;
            popResource(level, pos, new ItemStack(VWItems.VERIXIUM_POWDER, residue));
        }
        super.spawnAfterBreak(state, level, pos, tool, dropExperience);
    }

    private static void addOrStackEffect(LivingEntity entity, Holder<MobEffect> effect, int sec, int amp) {
        if (entity.hasEffect(effect)) {
            int currentAmp = entity.getEffect(effect).getAmplifier();
            int currentDuration = entity.getEffect(effect).getDuration();
            int DURATION_STACK;

            if (currentAmp >= 2) return;

            if (amp < currentAmp) {
                DURATION_STACK = currentDuration + sec;
                rewriteEffect(entity, effect, DURATION_STACK, currentAmp);
            } else {
                DURATION_STACK = (int) (currentDuration + Math.ceil(sec * 0.5f));
                rewriteEffect(entity, effect, DURATION_STACK, currentAmp + 1);
            }
        } else {
            rewriteEffect(entity, effect, sec, amp);
        }
    }

    private static boolean tickInterval(ServerLevel level, int sec) {
        return level.getGameTime() % Math.max(20 * sec, 0) == 0;
    }

    private static void addAttributeModifier(LivingEntity entity, Identifier attrIdentifier, Holder<Attribute> attribute, double amount, AttributeModifier.Operation operation) {
        AttributeInstance m = entity.getAttribute(attribute);
        if (m != null && m.getAttribute().equals(attribute) && !m.hasModifier(attrIdentifier)) {
            m.addOrReplacePermanentModifier(new AttributeModifier(attrIdentifier, amount, operation));
        }
    }

    private static String getStringPos(BlockPos pos) {
        return pos.getX() + ", " + pos.getY() + ", " + pos.getZ();
    }

    private static final Logger STAT_WINDCORE = LoggerFactory.getLogger("TOTVW/Lodestone Wind Core");
    private static void sendToLogger(String stat, String message) {
        if (stat.equals(LOG_ENERGY_UPDATES) && TOTVWConfig.get().LOG_WINDCORE_ENERGY_CHANGES) {
            stat = LOG_ENERGY_UPDATES;
            sendToServer(stat, message);
        }
        if (stat.equals(LOG_RECORD) && TOTVWConfig.get().LOG_WINDCORE_RECORD) {
            stat = LOG_RECORD;
            sendToServer(stat, message);
        }
        if (stat.equals(LOG_ENTITY_EVENT) && TOTVWConfig.get().LOG_WINDCORE_ENTITY_EVENT) {
            stat = LOG_ENTITY_EVENT;
            sendToServer(stat, message);
        }
        if (stat.equals(LOG_ENTITY_CONVERSION) && TOTVWConfig.get().LOG_WINDCORE_ENTITY_CONVERSION) {
            stat = LOG_ENTITY_CONVERSION;
            sendToServer(stat, message);
        }
    }
    private static void sendToServer(String stat, String message) {
        String statType = "[" + stat + "] ";
        STAT_WINDCORE.info("{}{}", statType, message);
    }
    public static final String LOG_ENERGY_UPDATES = "Energy Updates";
    public static final String LOG_RECORD = "Record";
    public static final String LOG_ENTITY_EVENT = "Entity Event";
    public static final String LOG_ENTITY_CONVERSION = "Entity Conversion";
}