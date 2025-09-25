package org.infernalstudios.archeryexp.platform;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.infernalstudios.archeryexp.networking.ArcheryNetworkingFabric;
import org.infernalstudios.archeryexp.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;

import java.util.function.Supplier;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public <T> Supplier<T> register(Registry<? super T> reg, ResourceLocation id, Supplier<T> obj) {
        T object = Registry.register(reg, id, obj.get());
        return () -> object;
    }

    @Override
    public Supplier<SimpleParticleType> registerParticle(String id, boolean alwaysSpawn) {
        SimpleParticleType particle = Registry.register(BuiltInRegistries.PARTICLE_TYPE, id, FabricParticleTypes.simple(alwaysSpawn));
        return () -> particle;
    }

    @Override
    public void sendBowStatsPacket(ServerPlayer player, ItemStack bow, float range, int drawTime, float speed, float x, float y) {
        ArcheryNetworkingFabric.sendBowStatsPacket(player, bow, range, drawTime, speed, x, y);
    }

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
