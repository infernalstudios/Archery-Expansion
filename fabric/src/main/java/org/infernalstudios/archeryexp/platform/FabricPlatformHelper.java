package org.infernalstudios.archeryexp.platform;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import org.infernalstudios.archeryexp.ArcheryExpansion;
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
}
