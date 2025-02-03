package org.infernalstudios.archeryexp.registry;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.infernalstudios.archeryexp.particles.ArcheryParticles;

public class ArcheryParticlesForge {

    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, ArcheryExpansion.MOD_ID);

    public static final RegistryObject<SimpleParticleType> ARROW_TRAIL =
            PARTICLES.register("arrow_trail", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> HEADSHOT =
            PARTICLES.register("headshot", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> QUICKDRAW_SHINE =
            PARTICLES.register("quickdraw_shine", () -> new SimpleParticleType(true));

    public static void registerParticles(IEventBus eventBus) {
        PARTICLES.register(eventBus);
    }

    public static void registerParticlesCommon() {
        ArcheryParticles.ARROW_TRAIL = ARROW_TRAIL.get();
        ArcheryParticles.HEADSHOT = HEADSHOT.get();
        ArcheryParticles.QUICKDRAW_SHINE = QUICKDRAW_SHINE.get();
    }
}
