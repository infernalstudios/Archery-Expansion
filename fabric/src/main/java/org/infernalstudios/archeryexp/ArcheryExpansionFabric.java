package org.infernalstudios.archeryexp;

import net.fabricmc.api.ModInitializer;
import org.infernalstudios.archeryexp.items.BowStatsLoader;
import org.infernalstudios.archeryexp.registry.ArcheryEntityTypesFabric;
import org.infernalstudios.archeryexp.registry.ArcheryItemsFabric;

public class ArcheryExpansionFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        ArcheryItemsFabric.registerItems();
        BowStatsLoader.loadBowStats();
        ArcheryEntityTypesFabric.registerEntityTypes();
        ArcheryExpansion.init();
    }
}
