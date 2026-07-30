package cliffordha.totvw.mixin;

import cliffordha.totvw.config.TOTVWConfig;
import cliffordha.totvw.registry.VWAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TamableAnimal.class)
public class TamableAnimalMixin {
    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    private void onDeath(CallbackInfo ci) {
        if ((Object) this instanceof Wolf wolf) {
            if (wolf.getAttachedOrElse(VWAttachments.Wolf.WOLF_BENEDICTION, 0) < 1) return;

            wolf.dropLeash();
            wolf.unRide();

            if (TOTVWConfig.get().SERVER_TELEPORT_AFTER_SAVE) {
                if (!wolf.isTame()) return;

                LivingEntity owner = wolf.getOwner();
                BlockPos pos = owner.blockPosition();
                wolf.setOrderedToSit(false);

                if (owner instanceof Player player && !player.isAlive()) {
                    BlockPos spawn = player.level().getRespawnData().pos();
                    wolf.teleportTo(spawn.getX(), spawn.getY() + 1, spawn.getZ());
                    ci.cancel();
                    return;
                }

                if (wolf.distanceTo(owner) > 16) {
                    wolf.tryToTeleportToOwner();
                } else {
                    owner.teleportTo(pos.getX(), pos.getY() + 1, pos.getZ());
                }
            }
            ci.cancel();
        }
    }
}
