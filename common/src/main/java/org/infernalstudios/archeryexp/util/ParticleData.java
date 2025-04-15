package org.infernalstudios.archeryexp.util;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class ParticleData {
    private final String type;
    private final Vec3 posOffset;
    private final Vec3 velocity;
    private final int count;
    private final float lookOffset;

    public ParticleData(String type, Vec3 offset, Vec3 velocity, int count, float lookOffset) {
        this.type = type;
        this.posOffset = offset;
        this.velocity = velocity;
        this.count = count;
        this.lookOffset = lookOffset;
    }

    public @Nullable SimpleParticleType getType() {
        ResourceLocation location = new ResourceLocation(this.type);

        return (SimpleParticleType) BuiltInRegistries.PARTICLE_TYPE.get(location);
    }

    public Vec3 getPosOffset() {
        return this.posOffset;
    }

    public Vec3 getVelocity() {
        return this.velocity;
    }

    public int getCount() {
        return this.count;
    }

    public float getLookOffset() {
        return this.lookOffset;
    }
}
