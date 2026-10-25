package cliffordha.totvw.mixin;

import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
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

        AttachmentType<BlockPos> PLAYER_RESPAWN_POINT = PlayerAttachment.RESPAWN_POINT;
        if (!level.isClientSide() && bedRule.canSetSpawn(player.level()) && !bedRule.destroyOnUse()) {
            player.setAttached(PLAYER_RESPAWN_POINT, pos);
            player.setAttached(PlayerAttachment.RESPAWN_DIMENSION, player.level().dimension());
        }
    }
}
