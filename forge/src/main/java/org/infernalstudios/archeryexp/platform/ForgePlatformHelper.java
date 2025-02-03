package org.infernalstudios.archeryexp.platform;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.infernalstudios.archeryexp.networking.ArcheryNetworkingForge;
import org.infernalstudios.archeryexp.platform.services.IPlatformHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.function.Supplier;

public class ForgePlatformHelper implements IPlatformHelper {

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

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ArcheryExpansion.MOD_ID);
    public static final DeferredRegister<Enchantment> ENCHANTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, ArcheryExpansion.MOD_ID);

    @Override
    public void registerEffect(String name, MobEffect effect) {
        EFFECTS.register(name, () -> effect);
    }

    @Override
    public void registerEnchantment(String name, Enchantment enchantment) {
        ENCHANTS.register(name, () -> enchantment);
    }

    public static void register(IEventBus eventBus) {
        EFFECTS.register(eventBus);
        ENCHANTS.register(eventBus);
    }

    @Override
    public void sendBowStatsPacket(ServerPlayer player, ItemStack bow, float range, int drawTime, float speed, float x, float y) {
        ArcheryNetworkingForge.sendBowStatsPacket(player, bow, range, drawTime, speed, x, y);
    }
}