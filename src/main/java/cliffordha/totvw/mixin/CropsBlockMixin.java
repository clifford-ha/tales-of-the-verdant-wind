package cliffordha.totvw.mixin;

import cliffordha.totvw.registry.attachments.Runestone;
import cliffordha.totvw.util.VWUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.animal.wolf.Wolf;
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
        CropBlock crop = (CropBlock) (Object) this;
        AABB scan = new AABB(pos).inflate(16);
        List<Wolf> wolves = level.getEntitiesOfClass(Wolf.class, scan, Runestone::hasGenesis);

        if (!wolves.isEmpty()) {
            for (int i = 0; i < crop.getMaxAge(); i++) {
                crop.performBonemeal(level, random, pos, state);
            }

            if (random.nextBoolean()) {
                CropBlock cropNorth = get(pos.north(), level);
                perfromBonemeal(cropNorth, level, pos.north());

                CropBlock cropEast = get(pos.east(), level);
                perfromBonemeal(cropEast, level, pos.east());
            } else {
                CropBlock cropSouth = get(pos.south(), level);
                perfromBonemeal(cropSouth, level, pos.south());

                CropBlock cropWest = get(pos.west(), level);
                perfromBonemeal(cropWest, level, pos.west());
            }

            alwaysTick = true;
            particle(level, pos);
        } else {
            alwaysTick = false;
        }
    }

    @Unique
    private static CropBlock get(BlockPos pos, ServerLevel level) {
        return level.getBlockState(pos).getBlock() instanceof CropBlock ? (CropBlock) level.getBlockState(pos).getBlock() : null;
    }
    @Unique
    private static void perfromBonemeal(CropBlock block, ServerLevel level, BlockPos pos) {
        if (block == null) return;
        if (block.getAge(level.getBlockState(pos)) >= block.getMaxAge()) return;
        block.performBonemeal(level, level.getRandom(), pos, block.defaultBlockState());
        particle(level, pos);
    }
    @Unique
    private static void particle(ServerLevel level, BlockPos pos) {
        VWUtil.sendParticles(ParticleTypes.GLOW, level, pos, 12, 0.6);
    }

    // other Genesis-related growth accelerators
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
        CropBlock crop = (CropBlock) (Object) this;

        boolean shouldGrow = level.getGameTime() % 20 == 0
                && crop.getAge(state) < crop.getMaxAge()
                && entity instanceof Wolf wolf
                && Runestone.hasGenesis(wolf);

        if (shouldGrow) {
            crop.growCrops(level, pos, state);
            particle((ServerLevel) level, pos);
        }
    }
}
