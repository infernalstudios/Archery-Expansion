package org.infernalstudios.archeryexp.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class HeadshotParticle extends TextureSheetParticle {
    protected HeadshotParticle(ClientLevel world, double x, double y, double z) {
        super(world, x, y, z);

        this.quadSize = 1;
        this.lifetime = 50;
        this.gravity = -0.5f;
        this.hasPhysics = false;
    }

    @Override
    public void tick() {
        super.tick();
        this.gravity += 0.1f;

        if (this.quadSize > 0) {
            this.quadSize -= 0.025f;
        }
    }

    @Override
    protected int getLightColor(float $$0) {
        return 15728880;
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
