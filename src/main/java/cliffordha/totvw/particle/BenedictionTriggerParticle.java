package cliffordha.totvw.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;

public class BenedictionTriggerParticle extends SingleQuadParticle {
    protected BenedictionTriggerParticle(ClientLevel level, double x, double y, double z, double xa, double ya, double za, TextureAtlasSprite sprite) {
        super(level, x, y, z, xa, ya, za, sprite);
        this.friction = 0.96F;
        this.quadSize *= 0.75F;
        this.yd *= 0.8F;
        this.xd *= 0.8F;
        this.zd *= 0.8F;
    }

    protected SingleQuadParticle.Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    public int getLightCoords(float a) {
        return (int)(255.0F * getFadeAmount(this.getLifetimeProgress((float)this.age + a), 0.1F, 0.3F));
    }

    public void tick() {
        super.tick();
        this.setAlpha(getFadeAmount(this.getLifetimeProgress((float)this.age), 0.5F, 0.5F));
        if (this.age == 1) {
            this.setParticleSpeed(-0.05F + 0.3F * this.random.nextFloat(), this.random.nextFloat(), -0.05F + 0.1F * this.random.nextFloat());
        }
    }

    private float getLifetimeProgress(float currentAge) {
        return Mth.clamp(currentAge / (float)this.lifetime, 0.0F, 1.0F);
    }
    
    
    private static float getFadeAmount(float lifetimeProgress, float fadeInTime, float fadeOutTime) {
        if (lifetimeProgress >= 1.0F - fadeInTime) {
            return (1.0F - lifetimeProgress) / fadeInTime;
        } else {
            return lifetimeProgress <= fadeOutTime ? lifetimeProgress / fadeOutTime : 1.0F;
        }
    }

    public static class BenedictionParticleProvider implements ParticleProvider<SimpleParticleType> {
        protected SpriteSet sprite;

        public BenedictionParticleProvider(SpriteSet sprite) {
            this.sprite = sprite;
        }

        public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random) {
            BenedictionTriggerParticle particle = new BenedictionTriggerParticle(level, x, y, z, (double)0.5F - random.nextDouble(), random.nextBoolean() ? yAux : -yAux, (double)0.5F - random.nextDouble(), this.sprite.get(random));
            particle.setLifetime(random.nextIntBetweenInclusive(10, 20));
            particle.setColor(0.0f, 1.0f, 0.584f);
            particle.scale(1.5F);
            particle.setAlpha(0.0F);
            return particle;
        }
    }
}
