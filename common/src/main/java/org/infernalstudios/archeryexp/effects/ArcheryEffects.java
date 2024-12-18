package org.infernalstudios.archeryexp.effects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.infernalstudios.archeryexp.platform.Services;

public class ArcheryEffects {
    public static final MobEffect QUICKDRAW_EFFECT = new QuickdrawEffect();

    public static void registerEffects() {
        register("quickdraw", QUICKDRAW_EFFECT);
    }

    private static void register(String name, MobEffect effect) {
        Services.PLATFORM.registerEffect(name, effect);
    }
}
