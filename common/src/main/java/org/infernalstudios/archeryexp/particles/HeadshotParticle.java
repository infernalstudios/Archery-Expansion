package org.infernalstudios.archeryexp.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class HeadshotParticle extends TextureSheetParticle {
    protected HeadshotParticle(ClientLevel world, double x, double y, double z) {
        super(world, x, y, z);

        this.quadSize = 1;
        this.lifetime = 30;
        this.gravity = -1;
    }

    @Override
    public void tick() {
        super.tick();
        this.gravity += 0.2f;

        if (this.quadSize > 0) {
            this.quadSize -= 0.05f;
        }
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

            HeadshotParticle particle = new HeadshotParticle(level, x, y, z);
            particle.pickSprite(this.spriteSet);
            return particle;

        }
    }
}
