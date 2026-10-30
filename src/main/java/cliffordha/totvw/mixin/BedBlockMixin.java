package cliffordha.totvw.mixin;

import cliffordha.totvw.registry.attachments.ReturnPointData;
import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.attribute.BedRule;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractBedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBedBlock.class)
public class BedBlockMixin {

    @Inject(method = "useWithoutItem", at = @At("HEAD"))
    private void onInteract(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        BedRule bedRule = level.environmentAttributes().getValue(EnvironmentAttributes.BED_RULE, pos);

        if (!level.isClientSide() && bedRule.canSetSpawn(player.level()) && !bedRule.destroyOnUse()) {
            if (cir.getReturnValue() != InteractionResult.SUCCESS) return;
            player.setAttached(PlayerAttachment.RETURN_POINT,
                    new ReturnPointData(player.level().dimension(), player.blockPosition(), true)
            );
        }
    }
}
