package cliffordha.totvw.mixin;

import cliffordha.totvw.registry.attachments.VWAttachments;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {

    @Unique
    private static final Map<UUID, float[]> FROZEN = new HashMap<>();

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V", at = @At("TAIL"))
    private void getRenderState(T entity, S state, float partialTicks, CallbackInfo ci) {
        if (entity.getAttachedOrElse(VWAttachments.FROST_BOUND_TIMER, 0) <= 0) {
            FROZEN.remove(entity.getUUID());
            return;
        }
        float[] saved = FROZEN.computeIfAbsent(entity.getUUID(),
                _ -> new float[]{state.ageInTicks, state.bodyRot, state.yRot, state.xRot, state.ageScale, state.scale});

        state.ageInTicks = saved[0];
        state.bodyRot = saved[1];
        state.yRot = saved[2];
        state.xRot = saved[3];
        state.walkAnimationSpeed = 0f;
        state.ageScale = saved[4];
        state.scale = saved[5];
    }
}
