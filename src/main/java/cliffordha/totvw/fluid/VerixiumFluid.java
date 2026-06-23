package cliffordha.totvw.fluid;

import java.util.Optional;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.ModBlocks;
import cliffordha.totvw.registry.ModFluids;
import cliffordha.totvw.registry.ModItems;
import cliffordha.totvw.registry.ModParticles;
import cliffordha.totvw.tag.ModBiomeTags;
import cliffordha.totvw.tag.ModFluidTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.level.block.Blocks;
import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

@SuppressWarnings("NullableProblems")
public abstract class VerixiumFluid extends FlowingFluid {
    private static final int TICK_SECONDS = 20;
    private static final int TICK_MINUTES = TICK_SECONDS * 60;
    private static final Direction[] ALL_DIRECTIONS = { Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.DOWN, Direction.UP};

    @Override
    public  Fluid getFlowing() { return ModFluids.FLOWING_VERIXIUM_FLUID; }

    @Override
    public  Fluid getSource() { return ModFluids.VERIXIUM_FLUID; }

    @Override
    public Item getBucket() { return ModItems.VERIXIUM_FLUID_BUCKET; }

    @Override
    public boolean isSame(Fluid fluid) { return fluid == ModFluids.VERIXIUM_FLUID || fluid == ModFluids.FLOWING_VERIXIUM_FLUID; }
    
    @Override
    public void animateTick(final Level level, final BlockPos pos, final FluidState fluidState, final RandomSource random) {
        double glowX = (double)pos.getX() + random.nextDouble() * 9.0 - 3.0;
        double glowY = (double)pos.getY() + random.nextDouble() * 3.0;
        double glowZ = (double)pos.getZ() + random.nextDouble() * 9.0 - 3.0;

        if (!fluidState.isSource() && !(Boolean)fluidState.getValue(FALLING)) {
            if (random.nextInt(64) == 0) {
                level.playLocalSound((double)pos.getX() + (double)0.5F, (double)pos.getY() + (double)0.5F, (double)pos.getZ() + (double)0.5F, SoundEvents.WATER_AMBIENT, SoundSource.AMBIENT, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false);
            }
        } else if (random.nextInt(10) == 0) {
            level.addParticle(ParticleTypes.UNDERWATER, (double)pos.getX() + random.nextDouble(), (double)pos.getY() + random.nextDouble(), (double)pos.getZ() + random.nextDouble(), 0.0F, 0.0F, 0.0F);
        }
        if (random.nextDouble() <= 0.08) {
            level.addParticle(ModParticles.VERDANT_BIOMES_ENVIRONMENT_AMBIANCE, glowX, glowY, glowZ, 3D, 1D, 3D);
        }

    }

    @Override
    public void tick(ServerLevel level, BlockPos pos, BlockState blockState, FluidState fluidState) {
        convertToDeepslate(level, pos);
        super.tick(level, pos, blockState, fluidState);
    }

    @Override
    protected void randomTick(ServerLevel level, BlockPos pos, FluidState fluidState, RandomSource random) {
        transformAdjacentBlocks(level, pos);
        super.randomTick(level, pos, fluidState, random);
    }

    @Override
    protected boolean isRandomlyTicking() {
        return true;
    }

    private static void convertToDeepslate(ServerLevel level, BlockPos pos) {
        double xx = pos.getX();
        double yy = (double)pos.getY() + (double)1.0F;
        double zz = pos.getZ();
        for (Direction direction : ALL_DIRECTIONS) {
            BlockPos neighborPos = pos.relative(direction);
            BlockState neighborState = level.getBlockState(neighborPos);
            if (neighborState.is(Blocks.WATER)) {
                level.setBlock(neighborPos, Blocks.DEEPSLATE.defaultBlockState(), Block.UPDATE_CLIENTS);
                level.addParticle(ParticleTypes.LARGE_SMOKE, xx, yy, zz, 0.0F, 0.0F, 0.0F);
                level.playSound(null, pos, SoundEvents.BASALT_BREAK, SoundSource.AMBIENT, 0.2F + level.getRandom().nextFloat() * 0.2F, 0.9F + level.getRandom().nextFloat() * 0.15F);
            } else if (neighborState.is(Blocks.LAVA)) {
                level.setBlock(neighborPos, Blocks.DEEPSLATE.defaultBlockState(), Block.UPDATE_CLIENTS);
                level.addParticle(ParticleTypes.LARGE_SMOKE, xx, yy, zz, 0.0F, 0.0F, 0.0F);
                level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.AMBIENT, 0.2F + level.getRandom().nextFloat() * 0.2F, 0.9F + level.getRandom().nextFloat() * 0.15F);
            }
        }
    }
    private static void transformAdjacentBlocks(ServerLevel level, BlockPos pos) {
        double xx = pos.getX();
        double yy = (double)pos.getY() + (double)1.0F;
        double zz = pos.getZ();
        float random = level.getRandom().nextFloat();

        for (Direction direction : ALL_DIRECTIONS) {
            BlockPos neighborPos = pos.relative(direction);
            BlockState neighborState = level.getBlockState(neighborPos);
            if (neighborState.is(Blocks.GLASS) && random < 0.67f) {
                level.setBlock(neighborPos, ModBlocks.IRIDESCENT_GLASS.defaultBlockState(), Block.UPDATE_CLIENTS);
                level.addParticle(ParticleTypes.GUST_EMITTER_LARGE, xx, yy, zz, 0.0F, 0.0F, 0.0F);
                level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.AMBIENT, 0.2F + level.getRandom().nextFloat() * 0.2F, 0.9F + level.getRandom().nextFloat() * 0.15F);
            } else if (neighborState.is(Blocks.GLASS_PANE) && random < 0.67f) {
                level.setBlock(neighborPos, ModBlocks.IRIDESCENT_GLASS_PANE.defaultBlockState(), Block.UPDATE_CLIENTS);
                level.addParticle(ParticleTypes.GUST_EMITTER_LARGE, xx, yy, zz, 0.0F, 0.0F, 0.0F);
                level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.AMBIENT, 0.2F + level.getRandom().nextFloat() * 0.2F, 0.9F + level.getRandom().nextFloat() * 0.15F);
            }
        }
    }

    @Nullable
    @Override
    public ParticleOptions getDripParticle() { return ParticleTypes.DRIPPING_WATER; }

    @Override
    protected boolean canConvertToSource(ServerLevel world) {
        return world.getGameRules().get(GameRules.WATER_SOURCE_CONVERSION);
    }

    @Override
    protected void beforeDestroyingBlock(final LevelAccessor level, final BlockPos pos, final BlockState state) {
        BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
        Block.dropResources(state, level, pos, blockEntity);
    }

    @Override
    protected void entityInside(Level world,  BlockPos pos,  Entity entity, InsideBlockEffectApplier handler) {
        handler.apply(InsideBlockEffectType.EXTINGUISH);
        handler.apply(InsideBlockEffectType.CLEAR_FREEZE);

        if (!(world instanceof ServerLevel) || !(entity instanceof LivingEntity livingEntity)) return;

        if (world.getGameTime() % (TICK_SECONDS * 3) == 0) {
            if (entity.level().getBiome(entity.blockPosition()).is(ModBiomeTags.IS_VERDANT_BIOMES)) {
                if (livingEntity.hasEffect(MobEffects.WITHER)) {
                    livingEntity.removeEffect(MobEffects.POISON);
                    livingEntity.removeEffect(MobEffects.WITHER);
                }
            }
            whoIsThis(livingEntity);
        }
    }

    private static void whoIsThis(LivingEntity entity) {
        int bossTime;
        int bossAmp;
        int playerTime;
        int playerAmp;
        if (entity.level().getBiome(entity.blockPosition()).is(ModBiomeTags.IS_VERDANT_BIOMES)) {
            bossTime = TOTVW.setTime(1, 30);
            bossAmp = 1;
            playerTime = TOTVW.setTime(0, 12);
            playerAmp = 0;
        } else {
            bossTime = TOTVW.setTime(0, 40);
            bossAmp = 0;
            playerTime = TOTVW.setTime(0, 3);
            playerAmp = -1;
        }
        switch (entity) {
            case Warden warden -> {
                warden.addEffect(new MobEffectInstance(MobEffects.WITHER, bossTime, bossAmp, false, false));
                if (!(warden.level().getDifficulty() == Difficulty.HARD)) {
                    evaluateSlowness(warden);
                }
            }
            case ServerPlayer player -> {
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, playerTime, playerAmp, false, false));
                if (!player.isCreative() || !player.isSpectator()) { evaluateSlowness(player); }
            }
            case Wolf wolf -> {
                wolf.addEffect(new MobEffectInstance(MobEffects.REGENERATION, (int) (playerTime * 1.25), playerAmp, false, false));
                if (!wolf.isBaby()) { evaluateSlowness(wolf); }
            }
            case Villager villager -> {
                villager.addEffect(new MobEffectInstance(MobEffects.REGENERATION, playerTime, playerAmp, false, false));
                if (!villager.isBaby()) { evaluateSlowness(villager); }
            }
            case WanderingTrader wanderingTrader -> {
                wanderingTrader.addEffect(new MobEffectInstance(MobEffects.REGENERATION, playerTime, playerAmp, false, false));
                if (!wanderingTrader.isBaby()) { evaluateSlowness(wanderingTrader); }
            }
            default -> {
                entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, playerTime, playerAmp, false, false));
            }
        }
    }
    private static void evaluateSlowness(LivingEntity livingEntity) {
        int defaultDuration;
        int defaultAmp;
        if (livingEntity.level().getBiome(livingEntity.blockPosition()).is(ModBiomeTags.IS_VERDANT_BIOMES)) {
            defaultDuration = TOTVW.setTime(0, 3);
            defaultAmp = 0;
        } else if (livingEntity.level().getBiome(livingEntity.blockPosition()).is(BiomeTags.IS_FOREST)) {
            defaultDuration = TOTVW.setTime(0, 30);
            defaultAmp = 0;
        } else if (livingEntity.level().getBiome(livingEntity.blockPosition()).is(BiomeTags.IS_END)) {
            defaultDuration = TOTVW.setTime(0, 12);
            defaultAmp = 1;
        } else {
            defaultDuration = TOTVW.setTime(0, 45);
            defaultAmp = 2;
        }
        livingEntity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, defaultDuration, defaultAmp, false, false));
    }

    @Override
    protected int getSlopeFindDistance( LevelReader world) { return 3; }

    @Override
    protected  BlockState createLegacyBlock( FluidState state) {
        return ModBlocks.VERIXIUM_FLUID.defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state)); }

    @Override
    public int getDropOff( LevelReader world) { return 1; }

    @Override
    public int getTickDelay( LevelReader world) { return 5; }

    @Override
    public boolean canBeReplacedWith(final FluidState state, final BlockGetter level, final BlockPos pos, final Fluid other, final Direction direction) {
        return direction == Direction.DOWN && !other.is(ModFluidTags.VERIXIUM_FLUID);
    }

    @Override
    protected float getExplosionResistance() { return 100.0F; }

    @Override
    public  Optional<SoundEvent> getPickupSound() { return Optional.of(SoundEvents.BUCKET_FILL); }

    public static class Source extends VerixiumFluid {
        @Override
        public int getAmount( FluidState state) { return 8; }

        @Override
        public boolean isSource( FluidState state) { return true; }
    }

    public static class Flowing extends VerixiumFluid {
        @Override
        protected void createFluidStateDefinition(final StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL); }

        @Override
        public int getAmount(final FluidState fluidState) {
            return fluidState.getValue(LEVEL);
        }

        @Override
        public boolean isSource( FluidState fluidState) { return false; }
    }
}