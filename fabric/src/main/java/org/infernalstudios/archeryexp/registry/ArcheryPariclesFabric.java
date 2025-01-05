package org.infernalstudios.archeryexp.registry;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.infernalstudios.archeryexp.platform.Services;

import static org.infernalstudios.archeryexp.particles.ArcheryParticles.*;

public class ArcheryPariclesFabric {

    public static void registerParticles() {
        ARROW_TRAIL = register("arrow_trail");
        HEADSHOT = register("headshot");
    }

    public static SimpleParticleType register(String name) {
        return Registry.register(BuiltInRegistries.PARTICLE_TYPE, new ResourceLocation(ArcheryExpansion.MOD_ID, name), FabricParticleTypes.simple(true));
    }
}
