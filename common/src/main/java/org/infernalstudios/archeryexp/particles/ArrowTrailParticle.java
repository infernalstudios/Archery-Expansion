package org.infernalstudios.archeryexp.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class ArrowTrailParticle extends TextureSheetParticle {
    protected ArrowTrailParticle(ClientLevel world, double x, double y, double z) {
        super(world, x, y, z);

        this.lifetime = 0;
        this.gravity = 0;
        this.quadSize = 0.25f;
        this.alpha = 0.5f;
    }

    @Override
    public void tick() {
        super.tick();
        this.quadSize = 0.25f;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {

            ArrowTrailParticle particle = new ArrowTrailParticle(level, x, y, z);
            particle.pickSprite(this.spriteSet);
            return particle;

        }
    }
}
