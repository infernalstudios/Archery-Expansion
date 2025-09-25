package org.infernalstudios.archeryexp.client.particles;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import org.infernalstudios.archeryexp.platform.Services;

import java.util.function.Supplier;

public class ArcheryParticles {
    public static final Supplier<SimpleParticleType> HEADSHOT = register("headshot", true);
    public static final Supplier<SimpleParticleType> QUICKDRAW_SHINE = register("quickdraw_shine", true);

    public static Supplier<SimpleParticleType> register(String name, boolean alwaysShow) {
        return Services.PLATFORM.registerParticle(name, alwaysShow);
    }

    public static void register() {}
}
