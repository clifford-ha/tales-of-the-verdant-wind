package cliffordha.totvw.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SnowflakeParticle;
import net.minecraft.client.particle.SpriteSet;

public class ExtendedSnowflakeParticle extends SnowflakeParticle {
    public ExtendedSnowflakeParticle(ClientLevel level, double x, double y, double z, double xa, double ya, double za, SpriteSet sprites) {
        super(level, x, y, z, xa, ya, za, sprites);
    }

    @Override
    public float getQuadSize(float a) {
        return super.getQuadSize(a) * 2.5F;
    }

    @Override
    public int getLifetime() {
        return super.getLifetime() * 3;
    }

    @Override
    public Layer getLayer() {
        return Layer.TRANSLUCENT;
    }
}
