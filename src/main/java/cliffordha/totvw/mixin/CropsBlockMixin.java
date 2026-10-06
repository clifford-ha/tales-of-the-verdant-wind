package cliffordha.totvw.mixin;

import cliffordha.totvw.item.custom.EfflorescenceRunestonePlate;
import cliffordha.totvw.registry.attachments.Runestone;
import cliffordha.totvw.registry.attachments.VWAttachments;
import cliffordha.totvw.util.VWUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(CropBlock.class)
public abstract class CropsBlockMixin {
    @Unique
    private static boolean alwaysTick = false;

    @Inject(method = "randomTick", at = @At("TAIL"))
    private void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        AABB scan = new AABB(pos).inflate(16);
        List<Wolf> wolves = level.getEntitiesOfClass(
                Wolf.class,
                scan,
                Runestone::hasEfflorescence
        ).stream().limit(1).toList();

        List<Player> players = level.getEntities(
                EntityTypes.PLAYER,
                scan,
                test -> test.getAttachedOrElse(VWAttachments.VERDANT_BLOOM_STACK, 0) > 0
        ).stream().limit(1).toList();

        boolean wolfIsPresent = !wolves.isEmpty();
        boolean playerIsPresent = !players.isEmpty();

        LivingEntity entity;

        if (wolfIsPresent) {
            entity = wolves.getFirst();
            checkTick(entity, state, level, pos, random);

        } else if (playerIsPresent) {
            entity = players.getFirst();
            checkTick(entity, state, level, pos, random);

        } else {
            alwaysTick = false;
        }
    }

    @Unique
    private void checkTick(LivingEntity entity, BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        CropBlock crop = (CropBlock) (Object) this;

        for (int i = 0; i < crop.getMaxAge(); i++) {
            crop.performBonemeal(level, random, pos, state);
        }

        if (random.nextBoolean()) {
            CropBlock cropNorth = getCropBlock(pos.north(), level);
            applyBonemeal(cropNorth, level, pos.north());

            CropBlock cropEast = getCropBlock(pos.east(), level);
            applyBonemeal(cropEast, level, pos.east());
        } else {
            CropBlock cropSouth = getCropBlock(pos.south(), level);
            applyBonemeal(cropSouth, level, pos.south());

            CropBlock cropWest = getCropBlock(pos.west(), level);
            applyBonemeal(cropWest, level, pos.west());
        }

        if (entity instanceof Player player) {
            int stack = player.getAttachedOrElse(VWAttachments.VERDANT_BLOOM_STACK, 0);
            player.setAttached(VWAttachments.VERDANT_BLOOM_STACK, stack - 1);
        }

        alwaysTick = true;
        particle(level, pos);
    }

    @Unique
    private static CropBlock getCropBlock(BlockPos pos, ServerLevel level) {
        return level.getBlockState(pos).getBlock() instanceof CropBlock ? (CropBlock) level.getBlockState(pos).getBlock() : null;
    }
    @Unique
    private static void applyBonemeal(CropBlock block, ServerLevel level, BlockPos pos) {
        if (block == null) return;
        if (block.getAge(level.getBlockState(pos)) >= block.getMaxAge()) return;
        block.performBonemeal(level, level.getRandom(), pos, block.defaultBlockState());
        particle(level, pos);
    }
    @Unique
    private static void particle(ServerLevel level, BlockPos pos) {
        VWUtil.sendParticles(ParticleTypes.GLOW, level, pos, 12, 0.6);
    }

    // other FlourishingFlora-related growth accelerators
    @Inject(method = "isRandomlyTicking", at = @At("TAIL"), cancellable = true)
    private void growthSpeed(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        CropBlock crop = (CropBlock) (Object) this;
        if (alwaysTick && crop.getAge(state) < crop.getMaxAge()) {
            cir.setReturnValue(true);
        }
    }
    @Inject(method = "hasSufficientLight", at = @At("TAIL"), cancellable = true)
    private static void bypassLightRequirements(LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (alwaysTick) {
            cir.setReturnValue(true);
        }
    }
    @Inject(method = "entityInside", at = @At("HEAD"))
    private void wolfNear(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise, CallbackInfo ci) {
        if (level.isClientSide()) return;
        CropBlock crop = (CropBlock) (Object) this;

        boolean shouldGrowByWolf = crop.getAge(state) < crop.getMaxAge()
                && entity instanceof Wolf wolf
                && Runestone.hasEfflorescence(wolf);

        boolean shouldGrowByPlayer = crop.getAge(state) < crop.getMaxAge()
                && entity instanceof Player player
                && player.getAttachedOrElse(VWAttachments.VERDANT_BLOOM_STACK, 0) > 0
                && player.getInventory().contains(new ItemStack(Items.BONE_MEAL));

        if (shouldGrowByWolf || shouldGrowByPlayer) {
            crop.growCrops(level, pos, state);
            particle((ServerLevel) level, pos);

            if (level.getRandom().nextFloat() < 0.2f && entity instanceof Player player) {
                int currentStack = player.getAttachedOrElse(VWAttachments.VERDANT_BLOOM_STACK, 0);
                player.setAttached(VWAttachments.VERDANT_BLOOM_STACK, Math.max(0, currentStack - 1));

                ItemStack stack = EfflorescenceRunestonePlate.getBonemeal(player);
                boolean amount = VWUtil.isInBiome(player, BiomeTags.HAS_DESERT_PYRAMID);
                if (stack != null) {
                    stack.shrink(amount ? 2 : 1);
                }
            }
        }
    }
}
