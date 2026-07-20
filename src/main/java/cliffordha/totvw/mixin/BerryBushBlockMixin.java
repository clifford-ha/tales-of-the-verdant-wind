package cliffordha.totvw.mixin;

import cliffordha.totvw.tag.VWBiomeTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SweetBerryBushBlock.class)
public class BerryBushBlockMixin {

    @Inject(method = "entityInside", at = @At("HEAD"), cancellable = true)
    private void evaluateType(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise, CallbackInfo ci) {
        if (level.getBiome(pos).is(VWBiomeTags.IS_VERDANT_BIOMES)) {
            //if (entity instanceof LivingEntity && !entity.is(EntityType.FOX) && !entity.is(EntityType.BEE) && !entity.is(EntityType.WOLF) && !entity.is(EntityType.VILLAGER) && !entity.is(EntityType.IRON_GOLEM) && !entity.is(EntityType.WANDERING_TRADER)) {
            if (entity instanceof LivingEntity) {
                entity.makeStuckInBlock(state, new Vec3(0.6F, 0.7, 0.6F));
            }
            ci.cancel();
        }
    }
}
