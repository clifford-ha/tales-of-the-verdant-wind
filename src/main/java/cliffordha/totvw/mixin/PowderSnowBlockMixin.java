package cliffordha.totvw.mixin;

import cliffordha.totvw.registry.VWEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static cliffordha.totvw.util.VWUtil.wolfEnchantmentLVL;

@Mixin(PowderSnowBlock.class)
public class PowderSnowBlockMixin {

    @Inject(method = "entityInside", at = @At("HEAD"), cancellable = true)
    private static void breakUponContact(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise, CallbackInfo ci) {
        if (entity instanceof Wolf wolf) {
            int hasIgnition = wolfEnchantmentLVL(wolf, VWEnchantments.WOLF_EFFECT_IGNITION);
            if (hasIgnition > 0) {
                boolean destroyBlock = level.getRandom().nextFloat() < 0.03f && level.getGameTime() % 60 == 0;
                if (hasIgnition > 2 && destroyBlock) {
                    level.destroyBlock(pos, false);
                    level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS);
                }
                ci.cancel();
            }
        }
    }
}
