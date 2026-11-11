package cliffordha.totvw.mixin;

import cliffordha.totvw.util.TranslucentRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntityRenderState.class)
public abstract class LivingEntityRenderStateMixin implements TranslucentRenderState {
    @Unique
    private boolean vw$translucent;

    @Override
    public void vw$setTranslucent(boolean translucent) {
        this.vw$translucent = translucent;
    }

    @Override
    public boolean vw$isTranslucent() {
        return this.vw$translucent;
    }
}
