package org.infernalstudios.archeryexp.platform;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.infernalstudios.archeryexp.networking.ArcheryNetworkingFabric;
import org.infernalstudios.archeryexp.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;

import java.util.function.Supplier;

public class FabricPlatformHelper implements IPlatformHelper {

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

    @Override
    public void registerEffect(String name, MobEffect effect) {
        Registry.register(BuiltInRegistries.MOB_EFFECT, new ResourceLocation(ArcheryExpansion.MOD_ID, name), effect);
    }

    @Override
    public void registerEnchantment(String name, Enchantment enchantment) {
        Registry.register(BuiltInRegistries.ENCHANTMENT, new ResourceLocation(ArcheryExpansion.MOD_ID, name), enchantment);
    }

    @Override
    public void sendBowStatsPacket(ServerPlayer player, ItemStack bow, float range, int drawTime, float speed, float x, float y) {
        ArcheryNetworkingFabric.sendBowStatsPacket(player, bow, range, drawTime, speed, x, y);
    }
}
