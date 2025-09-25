package org.infernalstudios.archeryexp.platform;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.infernalstudios.archeryexp.networking.ArcheryNetworkingForge;
import org.infernalstudios.archeryexp.platform.services.IPlatformHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.Map;
import java.util.function.Supplier;

public class ForgePlatformHelper implements IPlatformHelper {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, ArcheryExpansion.MOD_ID);
    private static final Map<ResourceKey<?>, DeferredRegister> DEFERRED_REGISTERIES = new Reference2ObjectOpenHashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public <T> Supplier<T> register(Registry<? super T> reg, ResourceLocation id, Supplier<T> obj) {
        return DEFERRED_REGISTERIES.computeIfAbsent(reg.key(),
                resourceKey -> DeferredRegister.create(reg.key().location(), id.getNamespace())).register(id.getPath(), obj);
    }

    @Override
    public Supplier<SimpleParticleType> registerParticle(String id, boolean alwaysSpawn) {
        return PARTICLES.register(id, () -> new SimpleParticleType(alwaysSpawn));
    }

    public static void register(IEventBus eventBus) {
        DEFERRED_REGISTERIES.values().forEach(deferredRegister -> deferredRegister.register(eventBus));
        PARTICLES.register(eventBus);
    }

    @Override
    public void sendBowStatsPacket(ServerPlayer player, ItemStack bow, float range, int drawTime, float speed, float x, float y) {
        ArcheryNetworkingForge.sendBowStatsPacket(player, bow, range, drawTime, speed, x, y);
    }

    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }
}