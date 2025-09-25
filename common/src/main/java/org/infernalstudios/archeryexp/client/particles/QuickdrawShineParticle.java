package org.infernalstudios.archeryexp.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class QuickdrawShineParticle extends SimpleAnimatedParticle {
    protected QuickdrawShineParticle(ClientLevel clientLevel, double d, double e, double f, double g, double h, double i, SpriteSet spriteSet) {
        super(clientLevel, d, e, f, spriteSet, 0.0125f);
        this.quadSize = 1;
        this.gravity = 0;
        this.lifetime = 8;
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getLightColor(float $$0) {
        return 15728880;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {

            QuickdrawShineParticle particle = new QuickdrawShineParticle(level, x, y, z, velocityX, velocityY, velocityZ, this.spriteSet);
            particle.pickSprite(this.spriteSet);
            return particle;

        }
    }
}
