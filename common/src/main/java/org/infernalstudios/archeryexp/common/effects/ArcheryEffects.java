package org.infernalstudios.archeryexp.common.effects;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.infernalstudios.archeryexp.platform.Services;

import java.util.function.Supplier;

public class ArcheryEffects {
    public static final Supplier<MobEffect> QUICKDRAW_EFFECT = register("quickdraw", QuickdrawEffect::new);

    private static Supplier<MobEffect> register(String name, Supplier<MobEffect> effect) {
        return Services.PLATFORM.register(BuiltInRegistries.MOB_EFFECT, new ResourceLocation(ArcheryExpansion.MOD_ID, name), effect);
    }

    public static void register() {}
}
