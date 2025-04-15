package org.infernalstudios.archeryexp.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

import javax.annotation.Nullable;

public class PotionData {
    private final String effect;
    private final String fallback;
    private final int level;
    private final int length;
    private final boolean particles;

    public PotionData(String effect, String fallback, int level, int length, boolean particles) {
        this.effect = effect;
        this.fallback = fallback;
        this.level = level;
        this.length = length;
        this.particles = particles;
    }

    public @Nullable MobEffect getEffect() {
        ResourceLocation location = new ResourceLocation(this.effect);

        return BuiltInRegistries.MOB_EFFECT.get(location);
    }

    public @Nullable MobEffect getFallbackEffect() {
        ResourceLocation location = new ResourceLocation(this.fallback);

        return BuiltInRegistries.MOB_EFFECT.get(location);
    }

    public int getLength() {
        return this.length;
    }

    public int getLevel() {
        return this.level;
    }

    public boolean getParticles() {
        return this.particles;
    }
}
