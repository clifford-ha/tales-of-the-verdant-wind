package cliffordha.totvw.mixin;

import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.registry.VWAttachments;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.attribute.BedRule;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BedBlock.class)
public class BedBlockMixin {

    @Inject(method = "useWithoutItem", at = @At("HEAD"))
    private void onInteract(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        BedRule bedRule = level.environmentAttributes().getValue(EnvironmentAttributes.BED_RULE, pos);

        AttachmentType<BlockPos> PLAYER_RESPAWN_POINT = VWAttachments.Player.PLAYER_RESPAWN_POINT;
        if (!level.isClientSide() && bedRule.canSetSpawn(player.level()) && !bedRule.explodes()) {
            player.setAttached(PLAYER_RESPAWN_POINT, pos);
            sendToServer("Player saved respawn point: " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
        }
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
