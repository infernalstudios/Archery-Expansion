package org.infernalstudios.archeryexp.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class PotionData {
    private final String effect;
    private final int level;
    private final int length;

    public PotionData(String effect, int level, int length) {
        this.effect = effect;
        this.level = level;
        this.length = length;
    }

    public MobEffect getEffect() {
        ResourceLocation location = new ResourceLocation(this.effect);

        return BuiltInRegistries.MOB_EFFECT.get(location);
    }

    public int getLength() {
        return this.length;
    }

    public int getLevel() {
        return this.level;
    }
}
