package org.infernalstudios.archeryexp.util.json.data;

import com.google.gson.JsonObject;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

/**
 * This Class is used to store Data for Particle Effects from Archery Expansion Bows, which are pulled
 * from Bow Stats Jsons.
 */
public class BowParticleData {
    private final String type;
    private final Vec3 posOffset;
    private final Vec3 velocity;
    private final int count;
    private final float lookOffset;

    public BowParticleData(String type, Vec3 offset, Vec3 velocity, int count, float lookOffset) {
        this.type = type;
        this.posOffset = offset;
        this.velocity = velocity;
        this.count = count;
        this.lookOffset = lookOffset;
    }

    public @Nullable SimpleParticleType getType() {
        return (SimpleParticleType) BuiltInRegistries.PARTICLE_TYPE.get(new ResourceLocation(this.type));
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

    public void apply(LivingEntity entity) {
        if (entity.level() instanceof ServerLevel serverLevel && getType() != null) {
            Vec3 offset = getPosOffset();
            Vec3 velocity = getVelocity();

            Vec3 lookVector = entity.getLookAngle();

            Vec3 inFrontPos = entity.position().add(lookVector.scale(getLookOffset()));

            serverLevel.sendParticles(getType(), inFrontPos.x + offset.x, entity.getEyeY() + offset.y, inFrontPos.z() + offset.z,
                    getCount(), velocity.x, velocity.y, velocity.z, 0);
        }
    }

    public static BowParticleData fromJson(JsonObject json) {
        String effect = json.has("effect") ? json.get("effect").getAsString() : "minecraft:empty";
        float x = json.has("xoffset") ? json.get("xoffset").getAsFloat() : 0;
        float y = json.has("yoffset") ? json.get("yoffset").getAsFloat() : 0;
        float z = json.has("zoffset") ? json.get("zoffset").getAsFloat() : 0;

        float vx = json.has("xvel") ? json.get("xvel").getAsFloat() : 0;
        float vy = json.has("yvel") ? json.get("yvel").getAsFloat() : 0;
        float vz = json.has("zvel") ? json.get("zvel").getAsFloat() : 0;

        int count = json.has("count") ? json.get("count").getAsInt() : 0;
        float lookOffset = json.has("look_offset") ? json.get("look_offset").getAsFloat() : 0;

        return new BowParticleData(effect, new Vec3(x, y, z), new Vec3(vx, vy, vz), count, lookOffset);
    }
}
