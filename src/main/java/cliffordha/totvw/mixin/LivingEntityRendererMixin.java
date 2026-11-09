package cliffordha.totvw.mixin;

import cliffordha.totvw.registry.attachments.VWAttachment;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import cliffordha.totvw.util.TranslucentRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {

    @Shadow
    public abstract Identifier getTextureLocation(S state);

    @Unique
    private static final Map<UUID, float[]> FROZEN = new HashMap<>();

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V", at = @At("TAIL"))
    private void getRenderState(T entity, S state, float partialTicks, CallbackInfo ci) {
        if (entity instanceof Wolf wolf && wolf.hasAttached(WolfAttachment.FRAGMENT_DATA)) {
            ((TranslucentRenderState) state).vw$setTranslucent(true);
        }
        if (entity.getAttachedOrElse(VWAttachment.FROST_BOUND_TIMER, 0) <= 0) {
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

    @Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
    private void getTranslucentLayer(S state, boolean showBody, boolean translucent, boolean showOutline, CallbackInfoReturnable<RenderType> cir) {
        if (((TranslucentRenderState) state).vw$isTranslucent()) {
            cir.setReturnValue(RenderTypes.entityTranslucent(this.getTextureLocation(state)));
        }
    }
}
