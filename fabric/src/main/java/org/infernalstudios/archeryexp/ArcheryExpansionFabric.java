package org.infernalstudios.archeryexp;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import org.infernalstudios.archeryexp.items.BowStatsLoader;
import org.infernalstudios.archeryexp.registry.ArcheryEntityTypesFabric;
import org.infernalstudios.archeryexp.registry.ArcheryItemsFabric;

public class ArcheryExpansionFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        ArcheryItemsFabric.registerItems();
        ArcheryEntityTypesFabric.registerEntityTypes();
        ArcheryExpansion.init();

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new ArcheryExpansionFabricReloadListener());
    }

    public class ArcheryExpansionFabricReloadListener implements SimpleSynchronousResourceReloadListener {
        @Override
        public ResourceLocation getFabricId() {
            return new ResourceLocation(ArcheryExpansion.MOD_ID, "bowstats_reload");
        }

        @Override
        public void onResourceManagerReload(ResourceManager resourceManager) {
            BowStatsLoader.loadBowStats(resourceManager);
        }
    }
}
