package cliffordha.totvw.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;

public class MightParalyzeParticle extends SingleQuadParticle {
    private static final float EXPAND_PROGRESS = 0.20F;

    protected MightParalyzeParticle(ClientLevel level, double x, double y, double z, double xa, double ya, double za, TextureAtlasSprite sprite) {
        super(level, x, y, z, xa, ya, za, sprite);
        this.friction = 1.0F;
        this.quadSize *= 1.0F;
        this.baseQuadSize = 0.3f;
        this.yd *= 0.3F;
        this.xd *= 0.3F;
        this.zd *= 0.3F;
    }

    protected SingleQuadParticle.Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    public int getLightCoords(float a) {
        return (int)(255.0F * getFadeAmount(this.getLifetimeProgress((float)this.age + a)));
    }

    public void tick() {
        super.tick();

        float lifetimeProgress = this.getLifetimeProgress((float)this.age);

        if (lifetimeProgress <= EXPAND_PROGRESS) {
            float expandT = lifetimeProgress / EXPAND_PROGRESS;
            this.quadSize = Mth.lerp(expandT, this.baseQuadSize * 0.1F, this.baseQuadSize);
        } else {
            this.quadSize = this.baseQuadSize;
        }

        this.setAlpha(getFadeAmount(lifetimeProgress));

        if (this.age == 1) {
            this.setParticleSpeed(-0.05F + 0.3F * this.random.nextFloat(), this.random.nextFloat(), -0.05F + 0.1F * this.random.nextFloat());
        }
    }

    private float getLifetimeProgress(float currentAge) {
        return Mth.clamp(currentAge / (float)this.lifetime, 0.0F, 1.0F);
    }

    private static float getFadeAmount(float lifetimeProgress) {
        if (lifetimeProgress <= EXPAND_PROGRESS) {
            return lifetimeProgress / EXPAND_PROGRESS;
        }
        float fadeT = (lifetimeProgress - EXPAND_PROGRESS) / (1.0F - EXPAND_PROGRESS);
        return 1.0F - fadeT;
    }

    private final float baseQuadSize;

    public static class MightParalyzeParticleProvider implements ParticleProvider<SimpleParticleType> {
        protected SpriteSet sprite;

        public MightParalyzeParticleProvider(SpriteSet sprite) {
            this.sprite = sprite;
        }

        public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random) {
            MightParalyzeParticle particle = new MightParalyzeParticle(level, x, y, z, (double)0.5F - random.nextDouble(), random.nextBoolean() ? yAux : -yAux, (double)0.5F - random.nextDouble(), this.sprite.get(random));
            particle.setLifetime(random.nextIntBetweenInclusive(10, 20));
            particle.setColor(0.431f, 0.772f, 1.0f);
            particle.scale(1.0F);
            particle.setAlpha(0.0F);
            return particle;
        }
    }
}