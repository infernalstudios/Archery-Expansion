package org.infernalstudios.archeryexp.util.json.data;

import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

/**
 * This Class is used to store Data for Potion Effects from Archery Expansion Bows, which are pulled
 * from Bow Stats Jsons
 */
public class BowEffectData {
    private final String effect;
    private final String fallback;
    private final boolean hasTooltip;
    private final int level;
    private final int length;
    private final boolean particles;

    public BowEffectData(String effect, String fallback, int level, int length, boolean particles, boolean hasTooltip) {
        this.effect = effect;
        this.fallback = fallback;
        this.level = level;
        this.length = length;
        this.particles = particles;
        this.hasTooltip = hasTooltip;
    }

    public @Nullable MobEffect getEffect() {
        return BuiltInRegistries.MOB_EFFECT.get(new ResourceLocation(this.effect));
    }

    public @Nullable MobEffect getFallbackEffect() {
        return BuiltInRegistries.MOB_EFFECT.get(new ResourceLocation(this.fallback));
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

    public boolean hasTooltip() {
        return this.hasTooltip;
    }

    public void apply(LivingEntity entity) {
        if (getEffect() != null) {
            apply(entity, getEffect());
        } else if (getFallbackEffect() != null) {
            apply(entity, getFallbackEffect());
        }
    }

    private void apply(LivingEntity entity, MobEffect effect) {
        entity.addEffect(new MobEffectInstance(effect, this.length, this.level, true, this.particles));
    }

    public static BowEffectData fromJson(JsonObject json) {

        String effect = json.has("effect") ? json.get("effect").getAsString() : "minecraft:empty";
        String fallback = json.has("fallback") ? json.get("fallback").getAsString() : "minecraft:empty";
        int lvl = json.has("lvl") ? json.get("lvl").getAsInt() : 1;
        int length = json.has("length") ? json.get("length").getAsInt() : 0;
        boolean particles = json.has("particles") && json.get("particles").getAsBoolean();
        boolean hasTooltip = !json.has("tooltip") || json.get("tooltip").getAsBoolean();

        return new BowEffectData(effect, fallback, lvl, length, particles, hasTooltip);
    }
}
